package hr.algebra.photostore.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}

