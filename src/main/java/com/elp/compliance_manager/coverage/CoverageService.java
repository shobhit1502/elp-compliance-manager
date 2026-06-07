package com.elp.compliance_manager.coverage;

import com.elp.compliance_manager.asset.Asset;
import com.elp.compliance_manager.asset.AssetRepository;
import com.elp.compliance_manager.asset.AssetType;
import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoverageService {

    private final AssetRepository assetRepository;
    private final CompanyRepository companyRepository;

    public CoverageResponseDTO calculateCoverage(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));

        List<Asset> allAssets = assetRepository
                .findByCompanyId(companyId);

        long totalAssets = allAssets.size();
        long coveredAssets = allAssets.stream()
                .filter(Asset::isHasScriptOutput)
                .count();
        long uncoveredAssets = totalAssets - coveredAssets;

        double coveragePercentage = totalAssets > 0
                ? (double) coveredAssets / totalAssets * 100
                : 0.0;

        double extrapolationFactor = coveredAssets > 0
                ? (double) totalAssets / coveredAssets
                : 1.0;

        long totalWorkstations = allAssets.stream()
                .filter(a -> a.getAssetType() == AssetType.WORKSTATION)
                .count();
        long coveredWorkstations = allAssets.stream()
                .filter(a -> a.getAssetType() == AssetType.WORKSTATION
                        && a.isHasScriptOutput())
                .count();

        long totalServers = allAssets.stream()
                .filter(a -> a.getAssetType() == AssetType.SERVER)
                .count();
        long coveredServers = allAssets.stream()
                .filter(a -> a.getAssetType() == AssetType.SERVER
                        && a.isHasScriptOutput())
                .count();

        String coverageStatus = getCoverageStatus(coveragePercentage);

        return CoverageResponseDTO.builder()
                .companyId(companyId)
                .companyName(company.getName())
                .totalAssets(totalAssets)
                .coveredAssets(coveredAssets)
                .uncoveredAssets(uncoveredAssets)
                .coveragePercentage(Math.round(
                        coveragePercentage * 100.0) / 100.0)
                .extrapolationFactor(Math.round(
                        extrapolationFactor * 100.0) / 100.0)
                .totalWorkstations(totalWorkstations)
                .coveredWorkstations(coveredWorkstations)
                .totalServers(totalServers)
                .coveredServers(coveredServers)
                .coverageStatus(coverageStatus)
                .build();
    }

    private String getCoverageStatus(double percentage) {
        if (percentage >= 90) return "EXCELLENT";
        if (percentage >= 75) return "GOOD";
        if (percentage >= 50) return "ACCEPTABLE";
        return "INSUFFICIENT";
    }
}