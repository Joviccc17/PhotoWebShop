package hr.algebra.photostore.admin.controller;

import hr.algebra.photostore.dto.OrderDto;
import hr.algebra.photostore.form.OrderSearchForm;
import hr.algebra.photostore.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private static final String ORDERS = "orders";
    private static final String SEARCH_FORM = "searchForm";

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrders(@ModelAttribute(SEARCH_FORM) OrderSearchForm searchForm,
                             Model model) {
        if (!model.containsAttribute(SEARCH_FORM)) {
            model.addAttribute(SEARCH_FORM, new OrderSearchForm());
        }
        model.addAttribute(ORDERS, orderService.getOrdersWithFilters(
                searchForm.getEmail(),
                searchForm.getDateFrom(),
                searchForm.getDateTo()
        ).stream().map(OrderDto::from).toList());
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", OrderDto.from(orderService.getOrderById(id)));
        return "admin/order-detail";
    }
}