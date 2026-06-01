package hr.algebra.photostore.config;

import hr.algebra.photostore.model.User;
import hr.algebra.photostore.repository.UserRepository;
import hr.algebra.photostore.service.ShoppingCart;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRepository userRepository;
    private final ShoppingCart shoppingCart;

    public GlobalModelAttributes(UserRepository userRepository,
                                 ShoppingCart shoppingCart) {
        this.userRepository = userRepository;
        this.shoppingCart = shoppingCart;
    }

    @ModelAttribute("currentUserFirstName")
    public String currentUserFirstName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return userRepository.findByEmail(auth.getName())
                    .map(User::getFirstName)
                    .orElse(null);
        }
        return null;
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        return shoppingCart.getItems().size();
    }
}