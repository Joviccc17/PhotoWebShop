package hr.algebra.photostore.admin.controller;

import hr.algebra.photostore.dto.CategoryDto;
import hr.algebra.photostore.form.CategoryForm;
import hr.algebra.photostore.model.Category;
import hr.algebra.photostore.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private static final String ACTIVE_PAGE = "activePage";
    private static final String CATEGORIES = "categories";
    private static final String VIEW_CATEGORY_FORM = "admin/category-form";
    private static final String REDIRECT_CATEGORIES = "redirect:/admin/categories";

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute(CATEGORIES, categoryService.getAllCategories()
                .stream().map(CategoryDto::from).toList());
        model.addAttribute(ACTIVE_PAGE, CATEGORIES);
        return "admin/categories";
    }

    @GetMapping("/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("form", new CategoryForm());
        model.addAttribute(ACTIVE_PAGE, CATEGORIES);
        return VIEW_CATEGORY_FORM;
    }

    @PostMapping("/new")
    public String createCategory(@Valid @ModelAttribute("form") CategoryForm form,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return VIEW_CATEGORY_FORM;
        }

        Category category = new Category();
        category.setName(form.getName());
        category.setDescription(form.getDescription());
        categoryService.save(category);

        return REDIRECT_CATEGORIES;
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);

        CategoryForm form = new CategoryForm();
        form.setName(category.getName());
        form.setDescription(category.getDescription());

        model.addAttribute("form", form);
        model.addAttribute("categoryId", id);
        model.addAttribute(ACTIVE_PAGE, CATEGORIES);
        return VIEW_CATEGORY_FORM;
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("form") CategoryForm form,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categoryId", id);
            return VIEW_CATEGORY_FORM;
        }

        Category category = categoryService.getCategoryById(id);
        category.setName(form.getName());
        category.setDescription(form.getDescription());
        categoryService.save(category);

        return REDIRECT_CATEGORIES;
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECT_CATEGORIES;
    }
}