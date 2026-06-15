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
        companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found: " + companyId));

        // All counts done in database — no data loaded to memory
        long totalAssets = assetRepository
                .countByCompanyId(companyId);
        long coveredAssets = assetRepository
                .countCoveredByCompanyId(companyId);
        long uncoveredAssets = totalAssets - coveredAssets;

        long totalWorkstations = assetRepository
                .countWorkstationsByCompanyId(companyId);
        long coveredWorkstations = assetRepository
                .countCoveredWorkstationsByCompanyId(companyId);

        long totalServers = assetRepository
                .countServersByCompanyId(companyId);
        long coveredServers = assetRepository
                .countCoveredServersByCompanyId(companyId);

        double coveragePercentage = totalAssets > 0 ?
                Math.round((double) coveredAssets /
                           totalAssets * 10000.0) / 100.0 : 0.0;

        double extrapolationFactor = coveredAssets > 0 ?
                Math.round((double) totalAssets /
                           coveredAssets * 100.0) / 100.0 : 1.0;

        String status;
        if (coveragePercentage >= 90) status = "EXCELLENT";
        else if (coveragePercentage >= 75) status = "GOOD";
        else if (coveragePercentage >= 60) status = "ACCEPTABLE";
        else status = "INSUFFICIENT";

        return CoverageResponseDTO.builder()
                .companyId(companyId)
                .totalAssets((int) totalAssets)
                .coveredAssets((int) coveredAssets)
                .uncoveredAssets((int) uncoveredAssets)
                .coveragePercentage(coveragePercentage)
                .extrapolationFactor(extrapolationFactor)
                .totalWorkstations((int) totalWorkstations)
                .coveredWorkstations((int) coveredWorkstations)
                .totalServers((int) totalServers)
                .coveredServers((int) coveredServers)
                .coverageStatus(status)
                .build();
    }
}