package com.elp.compliance_manager.entitlement.dto;

import com.elp.compliance_manager.entitlement.LicenseMetric;
import com.elp.compliance_manager.entitlement.LicenseType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EntitlementResponseDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private Long productId;
    private String productName;
    private String productVersion;
    private String productEdition;
    private int quantity;
    private LicenseType licenseType;
    private LicenseMetric licenseMetric;
    private boolean hasSoftwareAssurance;
    private String poNumber;
    private String source;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String notes;
    private LocalDateTime createdAt;
}