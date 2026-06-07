package com.elp.compliance_manager.company.dto;

import com.elp.compliance_manager.company.CompanyStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CompanyResponseDTO {

    private Long id;
    private String name;
    private String industry;
    private String contactEmail;
    private CompanyStatus status;
    private LocalDateTime createdAt;
}