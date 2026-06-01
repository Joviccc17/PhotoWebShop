package hr.algebra.photostore.user.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import hr.algebra.photostore.dto.OrderDto;
import hr.algebra.photostore.enums.PaymentMethod;
import hr.algebra.photostore.exceptions.PayPalApprovalException;
import hr.algebra.photostore.service.OrderService;
import hr.algebra.photostore.service.PayPalService;
import hr.algebra.photostore.service.ShoppingCart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/user")
public class UserOrderController {

    private static final String REDIRECT_CHECKOUT = "redirect:/user/checkout";
    private static final String ERROR             = "error";

    private final OrderService orderService;
    private final ShoppingCart shoppingCart;
    private final PayPalService payPalService;

    public UserOrderController(OrderService orderService,
                               ShoppingCart shoppingCart,
                               PayPalService payPalService) {
        this.orderService = orderService;
        this.shoppingCart = shoppingCart;
        this.payPalService = payPalService;
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        if (shoppingCart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", shoppingCart);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "user/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam PaymentMethod paymentMethod,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            if (paymentMethod == PaymentMethod.PAYPAL) {
                return handlePayPalPayment();
            }
            orderService.createOrder(
                    userDetails.getUsername(), shoppingCart, paymentMethod);
            redirectAttributes.addFlashAttribute(
                    "success", "Order placed successfully!");
            return "redirect:/user/orders";
        } catch (RuntimeException | PayPalRESTException e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
            return REDIRECT_CHECKOUT;
        }
    }

    @GetMapping("/checkout/paypal/success")
    public String paypalSuccess(@RequestParam("paymentId") String paymentId,
                                @RequestParam("PayerID") String payerId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);

            if ("approved".equals(payment.getState())) {
                orderService.createOrder(
                        userDetails.getUsername(), shoppingCart, PaymentMethod.PAYPAL);
                redirectAttributes.addFlashAttribute(
                        "success", "PayPal payment successful! Order placed.");
                return "redirect:/user/orders";
            }

            redirectAttributes.addFlashAttribute(ERROR, "PayPal payment not approved.");
            return REDIRECT_CHECKOUT;
        } catch (PayPalRESTException | RuntimeException e) {
            redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
            return REDIRECT_CHECKOUT;
        }
    }

    @GetMapping("/checkout/paypal/cancel")
    public String paypalCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR, "PayPal payment was cancelled.");
        return REDIRECT_CHECKOUT;
    }

    @GetMapping("/orders")
    public String orderHistory(@AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        model.addAttribute("orders",
                orderService.getOrdersByUser(userDetails.getUsername())
                        .stream().map(OrderDto::from).toList());
        return "user/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order",
                OrderDto.from(orderService.getOrderById(id)));
        return "user/order-detail";
    }

    private String handlePayPalPayment() throws PayPalRESTException {
        BigDecimal total = shoppingCart.getTotalPrice();
        Payment payment = payPalService.createPayment(
                total,
                "EUR",
                "PhotoStore Order",
                "http://localhost:8080/user/checkout/paypal/cancel",
                "http://localhost:8080/user/checkout/paypal/success"
        );

        for (Links link : payment.getLinks()) {
            if ("approval_url".equals(link.getRel())) {
                return "redirect:" + link.getHref();
            }
        }

        throw new PayPalApprovalException("PayPal approval URL not found");
    }
}