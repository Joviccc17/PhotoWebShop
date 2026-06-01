package hr.algebra.photostore.anonymous.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/welcome"})
    public String home() {
        return "anonymous/welcome";
    }
}