package hr.algebra.photostore.anonymous.controller;

import hr.algebra.photostore.model.Picture;
import hr.algebra.photostore.service.PictureService;
import hr.algebra.photostore.service.ShoppingCart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class AnonymousCartController {

    private static final String REDIRECT_CART = "redirect:/cart";

    private final ShoppingCart shoppingCart;
    private final PictureService pictureService;

    public AnonymousCartController(ShoppingCart shoppingCart, PictureService pictureService) {
        this.shoppingCart = shoppingCart;
        this.pictureService = pictureService;
    }

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cart", shoppingCart);
        return "anonymous/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long pictureId,
                            @RequestParam(defaultValue = "1") int quantity) {
        Picture picture = pictureService.getPictureById(pictureId);
        shoppingCart.addItem(
                picture.getId(),
                picture.getTitle(),
                picture.getPrice(),
                picture.getImageUrl(),
                quantity
        );
        return "redirect:/pictures";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long pictureId,
                                 @RequestParam int quantity) {
        shoppingCart.updateQuantity(pictureId, quantity);
        return REDIRECT_CART;
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long pictureId) {
        shoppingCart.removeItem(pictureId);
        return REDIRECT_CART;
    }

    @PostMapping("/clear")
    public String clearCart() {
        shoppingCart.clear();
        return REDIRECT_CART;
    }
}