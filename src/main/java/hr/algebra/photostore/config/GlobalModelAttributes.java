package hr.algebra.photostore.config;

import hr.algebra.photostore.service.ShoppingCart;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final ShoppingCart shoppingCart;

    public GlobalModelAttributes(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount(HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return 0;
        }
        return shoppingCart.getItems().size();
    }
}