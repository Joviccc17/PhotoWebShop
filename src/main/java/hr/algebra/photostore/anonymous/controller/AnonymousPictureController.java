package hr.algebra.photostore.anonymous.controller;

import hr.algebra.photostore.dto.CategoryDto;
import hr.algebra.photostore.dto.PictureDto;
import hr.algebra.photostore.service.CategoryService;
import hr.algebra.photostore.service.PictureService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import hr.algebra.photostore.model.Picture;

@Controller
@RequestMapping("/pictures")
public class AnonymousPictureController {

    private final PictureService pictureService;
    private final CategoryService categoryService;

    public AnonymousPictureController(PictureService pictureService,
                                      CategoryService categoryService) {
        this.pictureService = pictureService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listPictures(@RequestParam(required = false) Long categoryId,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Page<Picture> picturePage = pictureService.getPagedPictures(page, categoryId);

        model.addAttribute("pictures", picturePage.getContent()
                .stream().map(PictureDto::from).toList());
        model.addAttribute("totalPages", picturePage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("categories", categoryService.getAllCategories()
                .stream().map(CategoryDto::from).toList());

        if (categoryId != null) {
            model.addAttribute("selectedCategory",
                    CategoryDto.from(categoryService.getCategoryById(categoryId)));
        }

        return "anonymous/pictures";
    }

    @GetMapping("/{id}")
    public String pictureDetail(@PathVariable Long id, Model model) {
        model.addAttribute("picture", PictureDto.from(pictureService.getPictureById(id)));
        return "anonymous/picture-detail";
    }
}