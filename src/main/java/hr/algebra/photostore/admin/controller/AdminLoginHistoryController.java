package hr.algebra.photostore.admin.controller;

import hr.algebra.photostore.dto.LoginHistoryViewDto;
import hr.algebra.photostore.repository.LoginHistoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/login-history")
public class AdminLoginHistoryController {

    private final LoginHistoryRepository loginHistoryRepository;

    public AdminLoginHistoryController(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @GetMapping
    public String loginHistory(Model model) {
        model.addAttribute("activePage", "loginHistory");
        model.addAttribute("logins", loginHistoryRepository.findAllByOrderByLoginTimeDesc()
                .stream().map(LoginHistoryViewDto::from).toList());
        return "admin/login-history";
    }
}
