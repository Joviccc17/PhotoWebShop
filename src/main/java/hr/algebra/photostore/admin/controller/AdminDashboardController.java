package hr.algebra.photostore.admin.controller;

import hr.algebra.photostore.repository.CategoryRepository;
import hr.algebra.photostore.repository.LoginHistoryRepository;
import hr.algebra.photostore.repository.OrderRepository;
import hr.algebra.photostore.repository.PictureRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final CategoryRepository categoryRepository;
    private final PictureRepository pictureRepository;
    private final OrderRepository orderRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    public AdminDashboardController(CategoryRepository categoryRepository,
                                    PictureRepository pictureRepository,
                                    OrderRepository orderRepository,
                                    LoginHistoryRepository loginHistoryRepository) {
        this.categoryRepository = categoryRepository;
        this.pictureRepository = pictureRepository;
        this.orderRepository = orderRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("pictureCount", pictureRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("loginCount", loginHistoryRepository.count());
        return "admin/dashboard";
    }
}