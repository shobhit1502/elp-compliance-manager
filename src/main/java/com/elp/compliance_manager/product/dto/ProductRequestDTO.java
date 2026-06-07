package com.elp.compliance_manager.product.dto;

import com.elp.compliance_manager.product.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    private String name;

    private String version;

    private String edition;

    @NotNull(message = "Product category is required")
    private ProductCategory category;

    private String description;
}