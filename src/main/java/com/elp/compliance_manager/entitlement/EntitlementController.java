package com.elp.compliance_manager.entitlement;

import com.elp.compliance_manager.entitlement.dto.EntitlementRequestDTO;
import com.elp.compliance_manager.entitlement.dto.EntitlementResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/entitlements")
@RequiredArgsConstructor
public class EntitlementController {

    private final EntitlementService entitlementService;

    @PostMapping("/company/{companyId}")
    public ResponseEntity<EntitlementResponseDTO> addEntitlement(
            @PathVariable Long companyId,
            @Valid @RequestBody EntitlementRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entitlementService
                        .addEntitlement(companyId, request));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<EntitlementResponseDTO>>
    getEntitlementsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(entitlementService
                .getEntitlementsByCompany(companyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntitlementResponseDTO> getEntitlementById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                entitlementService.getEntitlementById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntitlementResponseDTO> updateEntitlement(
            @PathVariable Long id,
            @Valid @RequestBody EntitlementRequestDTO request) {
        return ResponseEntity.ok(
                entitlementService.updateEntitlement(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntitlement(
            @PathVariable Long id) {
        entitlementService.deleteEntitlement(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/company/{companyId}/ingest")
    public ResponseEntity<List<EntitlementResponseDTO>> ingestEntitlements(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entitlementService
                        .ingestEntitlementCSV(companyId, file));
    }

}