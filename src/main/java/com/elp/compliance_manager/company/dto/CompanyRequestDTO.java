package com.elp.compliance_manager.company.dto;

import com.elp.compliance_manager.company.CompanyStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequestDTO {

    @NotBlank(message = "Company name is required")
    private String name;

    private String industry;

    @Email(message = "Invalid email format")
    private String contactEmail;

    private CompanyStatus status;
}