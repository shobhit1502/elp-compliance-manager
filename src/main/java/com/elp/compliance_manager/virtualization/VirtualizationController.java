package com.elp.compliance_manager.virtualization;

import com.elp.compliance_manager.virtualization.dto.VirtualMachineResponseDTO;
import com.elp.compliance_manager.virtualization.dto.VirtualizationTopologyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/virtualization")
@RequiredArgsConstructor
public class VirtualizationController {

    private final VirtualizationService virtualizationService;

    @PostMapping("/ingest/{companyId}")
    public ResponseEntity<String> ingestVMwareExport(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) {
        int count = virtualizationService
                .ingestVMwareExport(companyId, file);
        return ResponseEntity.ok(
                "Successfully ingested " + count + " VMs");
    }

    @GetMapping("/topology/{companyId}")
    public ResponseEntity<VirtualizationTopologyDTO> getTopology(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                virtualizationService.getTopology(companyId));
    }

    @GetMapping("/vms/{companyId}")
    public ResponseEntity<List<VirtualMachineResponseDTO>> getVMs(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                virtualizationService.getVMsByCompany(companyId));
    }
}