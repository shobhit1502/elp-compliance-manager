package com.elp.compliance_manager.compliance;

import com.elp.compliance_manager.asset.Asset;
import com.elp.compliance_manager.asset.AssetRepository;
import com.elp.compliance_manager.audit.AuditService;
import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.company.CompanyRepository;
import com.elp.compliance_manager.compliance.dto.ComplianceResultDTO;
import com.elp.compliance_manager.compliance.dto.ComplianceSummaryDTO;
import com.elp.compliance_manager.coverage.CoverageService;
import com.elp.compliance_manager.entitlement.Entitlement;
import com.elp.compliance_manager.entitlement.EntitlementRepository;
import com.elp.compliance_manager.product.Product;
import com.elp.compliance_manager.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.elp.compliance_manager.compliance.rules.LicensingRule;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplianceEngine {

    private final CompanyRepository companyRepository;
    private final AssetRepository assetRepository;
    private final EntitlementRepository entitlementRepository;
    private final ProductRepository productRepository;
    private final ComplianceResultRepository complianceResultRepository;
    private final CoverageService coverageService;
    private final AuditService auditService;
    private final List<com.elp.compliance_manager.compliance.rules.LicensingRule> licensingRules;

    public ComplianceSummaryDTO runComplianceCheck(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));

        log.info("Running compliance check for company: {}",
                company.getName());

        var coverage = coverageService.calculateCoverage(companyId);
        double extrapolationFactor = coverage.getExtrapolationFactor();

        List<Entitlement> entitlements = entitlementRepository
                .findByCompanyId(companyId);

        Map<Long, Integer> licensedByProduct = entitlements.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getProduct().getId(),
                        Collectors.summingInt(Entitlement::getQuantity)));

        List<Asset> assets = assetRepository.findByCompanyId(companyId);

        List<ComplianceResult> results = new ArrayList<>();

        for (Long productId : licensedByProduct.keySet()) {
            Product product = productRepository.findById(productId)
                    .orElse(null);
            if (product == null) continue;

//            int deployedQty = countDeployments(assets, product);
//
//
//            long coveredAssets = coverage.getCoveredAssets();
//            long uncoveredAssets = coverage.getUncoveredAssets();
//
//            double ratio = coveredAssets > 0 ?
//                    (double) deployedQty / coveredAssets : 0;
//
//            int extrapolatedQty = deployedQty +
//                    (int) Math.ceil(ratio * uncoveredAssets);
//
//
//            int licensedQty = licensedByProduct.get(productId);
//            int gap = licensedQty - extrapolatedQty;

            int deployedQty = countDeployments(assets, product);
            int extrapolatedQty = (int) Math.ceil(
                    deployedQty * extrapolationFactor);
            int licensedQty = licensedByProduct.get(productId);

            int additionalCoverage = 0;
            for (LicensingRule rule : licensingRules) {
                int ruleCoverage = rule.applyRule(
                        product, extrapolatedQty, entitlements);
                if (ruleCoverage > 0) {
                    log.info("Rule [{}] provides {} additional coverage " +
                                    "for {} {}",
                            rule.getRuleName(), ruleCoverage,
                            product.getName(), product.getVersion());
                    additionalCoverage += ruleCoverage;
                }
            }

            int effectiveLicensed = licensedQty + additionalCoverage;
            int gap = effectiveLicensed - extrapolatedQty;

            ComplianceStatus status;
            if (gap == 0) status = ComplianceStatus.COMPLIANT;
            else if (gap < 0) status = ComplianceStatus.UNDER_LICENSED;
            else status = ComplianceStatus.OVER_LICENSED;

            ComplianceResult existing = complianceResultRepository
                    .findByCompanyIdAndProductId(companyId, productId)
                    .orElse(null);

            ComplianceResult result;
            if (existing != null) {
                existing.setDeployedQuantity(deployedQty);
                existing.setExtrapolatedQuantity(extrapolatedQty);
                existing.setLicensedQuantity(effectiveLicensed);
                existing.setGap(gap);
                existing.setStatus(status);
                result = existing;
            } else {
                result = ComplianceResult.builder()
                        .company(company)
                        .product(product)
                        .deployedQuantity(deployedQty)
                        .extrapolatedQuantity(extrapolatedQty)
                        .licensedQuantity(effectiveLicensed)  // ← change this
                        .gap(gap)
                        .status(status)
                        .build();
            }
            results.add(complianceResultRepository.save(result));
        }

        List<ComplianceResultDTO> resultDTOs = results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        long compliant = results.stream()
                .filter(r -> r.getStatus() ==
                        ComplianceStatus.COMPLIANT).count();
        long underLicensed = results.stream()
                .filter(r -> r.getStatus() ==
                        ComplianceStatus.UNDER_LICENSED).count();
        long overLicensed = results.stream()
                .filter(r -> r.getStatus() ==
                        ComplianceStatus.OVER_LICENSED).count();

        log.info("Compliance check complete. Under-licensed: {}, " +
                        "Compliant: {}, Over-licensed: {}",
                underLicensed, compliant, overLicensed);

        auditService.log(
                "COMPLIANCE_CHECK_RUN",
                "Company",
                String.valueOf(companyId),
                "system",
                companyId,
                "Compliance check completed. Products analyzed: " + results.size()
        );

        return ComplianceSummaryDTO.builder()
                .companyId(companyId)
                .companyName(company.getName())
                .totalProductsAnalyzed(results.size())
                .compliantProducts((int) compliant)
                .underLicensedProducts((int) underLicensed)
                .overLicensedProducts((int) overLicensed)
                .coveragePercentage(coverage.getCoveragePercentage())
                .extrapolationFactor(extrapolationFactor)
                .results(resultDTOs)
                .build();
    }

    public List<ComplianceResultDTO> getResults(Long companyId) {
        return complianceResultRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private int countDeployments(List<Asset> assets, Product product) {
        return (int) assets.stream()
                .filter(a -> matchesProduct(a, product))
                .count();
    }

    private boolean matchesProduct(Asset asset, Product product) {
        if (asset.getOperatingSystem() == null) return false;
        String os = asset.getOperatingSystem().toLowerCase();
        String productName = product.getName().toLowerCase();
        String version = product.getVersion() != null ?
                product.getVersion().toLowerCase() : "";
        return os.contains(productName) ||
                (os.contains("windows") && productName.contains("windows")
                        && (version.isEmpty() || os.contains(version)));
    }

    private ComplianceResultDTO toDTO(ComplianceResult result) {
        return ComplianceResultDTO.builder()
                .id(result.getId())
                .companyId(result.getCompany().getId())
                .companyName(result.getCompany().getName())
                .productId(result.getProduct().getId())
                .productName(result.getProduct().getName())
                .productVersion(result.getProduct().getVersion())
                .productEdition(result.getProduct().getEdition())
                .productCategory(result.getProduct()
                        .getCategory().name())
                .deployedQuantity(result.getDeployedQuantity())
                .extrapolatedQuantity(result.getExtrapolatedQuantity())
                .licensedQuantity(result.getLicensedQuantity())
                .gap(result.getGap())
                .status(result.getStatus())
                .notes(result.getNotes())
                .calculatedAt(result.getCalculatedAt())
                .build();
    }
}