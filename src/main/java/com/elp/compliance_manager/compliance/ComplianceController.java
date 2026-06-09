package com.elp.compliance_manager.compliance;

import com.elp.compliance_manager.compliance.dto.ComplianceResultDTO;
import com.elp.compliance_manager.compliance.dto.ComplianceSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceEngine complianceEngine;

    @PostMapping("/run/{companyId}")
    public ResponseEntity<ComplianceSummaryDTO> runComplianceCheck(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                complianceEngine.runComplianceCheck(companyId));
    }

    @GetMapping("/results/{companyId}")
    public ResponseEntity<List<ComplianceResultDTO>> getResults(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                complianceEngine.getResults(companyId));
    }
}