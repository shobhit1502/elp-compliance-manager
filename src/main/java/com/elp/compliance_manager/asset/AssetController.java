package com.elp.compliance_manager.asset;

import com.elp.compliance_manager.asset.dto.AssetResponseDTO;
import com.elp.compliance_manager.asset.dto.IngestionSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/ingest/ad-export/{companyId}")
    public ResponseEntity<IngestionSummaryDTO> ingestADExport(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                assetService.ingestADExport(companyId, file));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AssetResponseDTO>> getAssetsByCompany(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                assetService.getAssetsByCompany(companyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> getAssetById(
            @PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }
}