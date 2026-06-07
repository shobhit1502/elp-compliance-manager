package com.elp.compliance_manager.company;

import com.elp.compliance_manager.company.dto.CompanyRequestDTO;
import com.elp.compliance_manager.company.dto.CompanyResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(
            @Valid @RequestBody CompanyRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(request));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(
            @PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequestDTO request) {
        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CompanyResponseDTO>> getCompaniesByStatus(
            @PathVariable CompanyStatus status) {
        return ResponseEntity.ok(companyService.getCompaniesByStatus(status));
    }
}