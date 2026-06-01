package hr.algebra.photostore.anonymous.controller;

import hr.algebra.photostore.form.RegistrationForm;
import hr.algebra.photostore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private static final String VIEW_REGISTER = "anonymous/register";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "anonymous/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return VIEW_REGISTER;
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return VIEW_REGISTER;
        }

        try {
            userService.register(
                    form.getEmail(),
                    form.getPassword(),
                    form.getFirstName(),
                    form.getLastName(),
                    form.getAddress()
            );
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return VIEW_REGISTER;
        }
    }
}