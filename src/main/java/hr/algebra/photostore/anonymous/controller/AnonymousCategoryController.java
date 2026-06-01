package hr.algebra.photostore.anonymous.controller;

import hr.algebra.photostore.dto.CategoryDto;
import hr.algebra.photostore.service.CategoryService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class AnonymousCategoryController {

    private final CategoryService categoryService;

    public AnonymousCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {

        model.addAttribute("categories", categoryService.getAllCategories()
                .stream().map(CategoryDto::from).toList());
        return "anonymous/categories";
    }
}
