package hr.algebra.photostore.rests;

import hr.algebra.photostore.dto.ErrorResponseDto;
import hr.algebra.photostore.dto.PictureDto;
import hr.algebra.photostore.dto.PictureRequestDto;
import hr.algebra.photostore.model.Picture;
import hr.algebra.photostore.service.CategoryService;
import hr.algebra.photostore.service.PictureService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Pictures", description = "Picture catalogue")
@RestController
@RequestMapping("/api/pictures")
public class  PictureRestController {

    private final PictureService pictureService;
    private final CategoryService categoryService;

    public PictureRestController(PictureService pictureService, CategoryService categoryService) {
        this.pictureService = pictureService;
        this.categoryService = categoryService;
    }


    @GetMapping
    public ResponseEntity<List<PictureDto>> getAllPictures() {
        return ResponseEntity.ok(
                pictureService.getAllPictures().stream().map(PictureDto::from).toList()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getPictureById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(PictureDto.from(pictureService.getPictureById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponseDto(e.getMessage()));
        }
    }


    @GetMapping("/paged")
    public ResponseEntity<Map<String, Object>> getPagedPictures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long categoryId) {
        Page<Picture> picturePage = pictureService.getPagedPictures(page, categoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("pictures", picturePage.getContent().stream().map(PictureDto::from).toList());
        response.put("totalPages", picturePage.getTotalPages());
        response.put("currentPage", page);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Object> createPicture(@RequestBody @Valid PictureRequestDto dto) {
        try {
            Picture picture = new Picture();
            picture.setTitle(dto.title());
            picture.setDescription(dto.description());
            picture.setPrice(dto.price());
            picture.setImageUrl(dto.imageUrl());
            picture.setStockQuantity(dto.stockQuantity());
            picture.setCategory(categoryService.getCategoryById(dto.categoryId()));
            return ResponseEntity.status(201).body(PictureDto.from(pictureService.save(picture)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.getMessage()));
        }
    }
}