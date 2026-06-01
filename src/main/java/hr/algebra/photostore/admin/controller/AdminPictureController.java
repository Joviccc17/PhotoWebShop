package hr.algebra.photostore.admin.controller;

import hr.algebra.photostore.dto.CategoryDto;
import hr.algebra.photostore.dto.PictureDto;
import hr.algebra.photostore.exceptions.ImageUploadException;
import hr.algebra.photostore.form.PictureForm;
import hr.algebra.photostore.model.Picture;
import hr.algebra.photostore.service.CategoryService;
import hr.algebra.photostore.service.ImageService;
import hr.algebra.photostore.service.PictureService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pictures")
public class AdminPictureController {

    private static final String ACTIVE_PAGE     = "activePage";
    private static final String PICTURES        = "pictures";
    private static final String CATEGORIES      = "categories";
    private static final String IMAGES          = "images";
    private static final String ACTION_URL      = "actionUrl";
    private static final String VIEW_FORM       = "admin/picture-form";
    private static final String REDIRECT        = "redirect:/admin/pictures";
    private static final String IMAGES_PREFIX   = "/images/";

    private final PictureService pictureService;
    private final CategoryService categoryService;
    private final ImageService imageService;

    public AdminPictureController(PictureService pictureService,
                                  CategoryService categoryService,
                                  ImageService imageService) {
        this.pictureService = pictureService;
        this.categoryService = categoryService;
        this.imageService = imageService;
    }

    @GetMapping
    public String listPictures(Model model) {
        model.addAttribute(PICTURES, pictureService.getAllPictures()
                .stream().map(PictureDto::from).toList());
        model.addAttribute(ACTIVE_PAGE, PICTURES);
        return "admin/pictures";
    }

    @GetMapping("/new")
    public String newPictureForm(Model model) {
        model.addAttribute("form", new PictureForm());
        model.addAttribute(ACTIVE_PAGE, PICTURES);
        populateFormModel(model, "/admin/pictures/new");
        return VIEW_FORM;
    }

    @PostMapping("/new")
    public String createPicture(@Valid @ModelAttribute("form") PictureForm form,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            populateFormModel(model, "/admin/pictures/new");
            return VIEW_FORM;
        }

        Picture picture = buildPicture(form, new Picture());
        pictureService.save(picture);
        return REDIRECT;
    }

    @GetMapping("/edit/{id}")
    public String editPictureForm(@PathVariable Long id, Model model) {
        Picture picture = pictureService.getPictureById(id);

        PictureForm form = new PictureForm();
        form.setTitle(picture.getTitle());
        form.setDescription(picture.getDescription());
        form.setPrice(picture.getPrice());
        form.setSelectedImage(picture.getImageUrl() != null
                ? picture.getImageUrl().replace(IMAGES_PREFIX, "") : null);
        form.setStockQuantity(picture.getStockQuantity());
        form.setCategoryId(picture.getCategory().getId());

        model.addAttribute("form", form);
        model.addAttribute("pictureId", id);
        model.addAttribute(ACTIVE_PAGE, PICTURES);
        populateFormModel(model, "/admin/pictures/edit/" + id);
        return VIEW_FORM;
    }

    @PostMapping("/edit/{id}")
    public String updatePicture(@PathVariable Long id,
                                @Valid @ModelAttribute("form") PictureForm form,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pictureId", id);
            populateFormModel(model, "/admin/pictures/edit/" + id);
            return VIEW_FORM;
        }

        Picture picture = buildPicture(form, pictureService.getPictureById(id));
        pictureService.save(picture);
        return REDIRECT;
    }

    @PostMapping("/delete/{id}")
    public String deletePicture(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            pictureService.deleteById(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return REDIRECT;
    }

    private void populateFormModel(Model model, String actionUrl) {
        model.addAttribute(CATEGORIES, categoryService.getAllCategories()
                .stream().map(CategoryDto::from).toList());
        model.addAttribute(IMAGES, imageService.getAllImageNames());
        model.addAttribute(ACTION_URL, actionUrl);
    }

    private Picture buildPicture(PictureForm form, Picture picture) {
        picture.setTitle(form.getTitle());
        picture.setDescription(form.getDescription());
        picture.setPrice(form.getPrice());
        picture.setImageUrl(resolveImageUrl(form));
        picture.setStockQuantity(form.getStockQuantity());
        picture.setCategory(categoryService.getCategoryById(form.getCategoryId()));
        return picture;
    }

    private String resolveImageUrl(PictureForm form) {
        if (form.getUploadedImage() != null && !form.getUploadedImage().isEmpty()) {
            try {
                String fileName = imageService.saveImage(form.getUploadedImage());
                return IMAGES_PREFIX + fileName;
            } catch (Exception e) {
                throw new ImageUploadException("Failed to upload image: " + e.getMessage());
            }
        }
        if (form.getSelectedImage() != null && !form.getSelectedImage().isEmpty()) {
            return IMAGES_PREFIX + form.getSelectedImage();
        }
        return null;
    }
}