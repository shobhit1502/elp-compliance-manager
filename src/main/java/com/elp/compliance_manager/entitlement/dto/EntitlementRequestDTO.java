package com.elp.compliance_manager.entitlement.dto;

import com.elp.compliance_manager.entitlement.LicenseMetric;
import com.elp.compliance_manager.entitlement.LicenseType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EntitlementRequestDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "License type is required")
    private LicenseType licenseType;

    @NotNull(message = "License metric is required")
    private LicenseMetric licenseMetric;

    private boolean hasSoftwareAssurance;
    private String poNumber;
    private String source;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String notes;
}