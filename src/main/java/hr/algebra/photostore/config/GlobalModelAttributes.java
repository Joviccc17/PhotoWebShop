package hr.algebra.photostore.config;

import hr.algebra.photostore.service.ShoppingCart;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final ShoppingCart shoppingCart;

    public GlobalModelAttributes(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        return shoppingCart.getItems().size();
    }
}