package hr.algebra.photostore.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class PictureForm {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;

    private String selectedImage;  // chosen from existing images

    private MultipartFile uploadedImage;  // new upload

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock must be positive")
    private Integer stockQuantity;

    @NotNull(message = "Category is required")
    private Long categoryId;
}