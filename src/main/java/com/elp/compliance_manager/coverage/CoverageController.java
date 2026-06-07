package com.elp.compliance_manager.coverage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coverage")
@RequiredArgsConstructor
public class CoverageController {

    private final CoverageService coverageService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<CoverageResponseDTO> getCoverage(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                coverageService.calculateCoverage(companyId));
    }
}