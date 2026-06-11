package com.elp.compliance_manager.asset;

import com.elp.compliance_manager.asset.dto.AssetResponseDTO;
import com.elp.compliance_manager.asset.dto.IngestionSummaryDTO;
import com.elp.compliance_manager.audit.AuditService;
import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;
    private final CompanyRepository companyRepository;
    private final AuditService auditService;

    public IngestionSummaryDTO ingestADExport(
            Long companyId, MultipartFile file) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));
        List<Asset> assets = new ArrayList<>();
        int skipped = 0;
        int totalRows = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                totalRows++;
                String[] columns = line.split(",");

                if (columns.length < 3) {
                    skipped++;
                    continue;
                }

                String machineName = columns[0].trim();

                if (assetRepository.findByMachineNameAndCompanyId(
                        machineName, companyId).isPresent()) {
                    skipped++;
                    continue;
                }

                Asset asset = Asset.builder()
                        .machineName(machineName)
                        .operatingSystem(columns.length > 1 ?
                                columns[1].trim() : "Unknown")
                        .assetType(parseAssetType(
                                columns.length > 2 ?
                                        columns[2].trim() : ""))
                        .domain(columns.length > 3 ?
                                columns[3].trim() : "")
                        .lastSeen(columns.length > 4 ?
                                columns[4].trim() : "")
                        .assetSource(AssetSource.AD_EXPORT)
                        .company(company)
                        .build();
                assets.add(asset);
            }

            assetRepository.saveAll(assets);
            log.info("Ingested {} assets for company {}",
                    assets.size(), company.getName());

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse CSV file: " + e.getMessage());
        }

        auditService.log(
                "ASSET_INGESTION",
                "Asset",
                "bulk",
                "system",
                companyId,
                "AD Export ingested. Assets saved: " + assets.size()
        );

        return IngestionSummaryDTO.builder()
                .totalRows(totalRows)
                .savedCount(assets.size())
                .skippedCount(skipped)
                .message("AD Export ingested successfully for: "
                        + company.getName())
                .build();
    }

    public List<AssetResponseDTO> getAssetsByCompany(Long companyId) {
        return assetRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AssetResponseDTO getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Asset not found with id: " + id));
        return toResponseDTO(asset);
    }

    private AssetType parseAssetType(String type) {
        if (type == null || type.isEmpty()) return AssetType.UNKNOWN;
        return switch (type.toLowerCase()) {
            case "server" -> AssetType.SERVER;
            case "workstation", "desktop", "laptop" ->
                    AssetType.WORKSTATION;
            case "vm", "virtual machine" ->
                    AssetType.VIRTUAL_MACHINE;
            default -> AssetType.UNKNOWN;
        };
    }

    private AssetResponseDTO toResponseDTO(Asset asset) {
        return AssetResponseDTO.builder()
                .id(asset.getId())
                .machineName(asset.getMachineName())
                .ipAddress(asset.getIpAddress())
                .operatingSystem(asset.getOperatingSystem())
                .osVersion(asset.getOsVersion())
                .assetType(asset.getAssetType())
                .assetSource(asset.getAssetSource())
                .domain(asset.getDomain())
                .lastSeen(asset.getLastSeen())
                .isInScope(asset.isInScope())
                .hasScriptOutput(asset.isHasScriptOutput())
                .companyId(asset.getCompany().getId())
                .createdAt(asset.getCreatedAt())
                .build();
    }
}