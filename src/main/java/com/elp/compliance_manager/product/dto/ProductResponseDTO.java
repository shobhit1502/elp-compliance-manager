package com.elp.compliance_manager.product.dto;

import com.elp.compliance_manager.product.ProductCategory;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String version;
    private String edition;
    private ProductCategory category;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;
}