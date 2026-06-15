package com.elp.compliance_manager.connector;

import com.elp.compliance_manager.asset.*;
import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.company.CompanyRepository;
import com.elp.compliance_manager.coverage.CoverageService;
import com.elp.compliance_manager.deployment.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectorOrchestrator {

    private final List<DataConnector> connectors;
    private final AssetRepository assetRepository;
    private final AssetDeploymentRepository deploymentRepository;
    private final CompanyRepository companyRepository;
    private final CoverageService coverageService;

    public ConnectorSyncResult sync(Long companyId,
                                    ConnectorType type) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found: " + companyId));

        DataConnector connector = connectors.stream()
                .filter(c -> c.getType() == type)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No connector found for type: " + type));

        log.info("Starting sync for company: {} using connector: {}",
                company.getName(), type);

        List<NormalizedAsset> normalizedAssets =
                connector.fetchData(companyId);

        int assetsSaved = 0;
        int assetsSkipped = 0;
        int deploymentsSaved = 0;

        for (NormalizedAsset normalized : normalizedAssets) {
            if (normalized.getMachineName() == null ||
                    normalized.getMachineName().isBlank()) {
                assetsSkipped++;
                continue;
            }

            Optional<Asset> existing = assetRepository
                    .findByMachineNameAndCompanyId(
                            normalized.getMachineName(), companyId);

            Asset asset;
            if (existing.isPresent()) {
                asset = existing.get();
                assetsSkipped++;
                log.debug("Asset already exists: {}",
                        normalized.getMachineName());
            } else {
                asset = Asset.builder()
                        .machineName(normalized.getMachineName())
                        .operatingSystem(
                                normalized.getOperatingSystem())
                        .assetType(normalized.getAssetType())
                        .domain(normalized.getDomain())
                        .lastSeen(normalized.getLastSeen())
                        .ipAddress(normalized.getIpAddress())
                        .assetSource(normalized.getAssetSource())
                        .company(company)
                        .hasScriptOutput(
                                normalized.getInstalledSoftware()
                                        != null &&
                                        !normalized.getInstalledSoftware()
                                                .isEmpty())
                        .build();
                assetRepository.save(asset);
                assetsSaved++;
                log.debug("Saved new asset: {}",
                        normalized.getMachineName());
            }

            if (normalized.getInstalledSoftware() != null) {
                for (NormalizedSoftware sw :
                        normalized.getInstalledSoftware()) {
                    Optional<AssetDeployment> existingDep =
                            deploymentRepository
                                    .findByAssetIdAndProductNameAndProductVersion(
                                            asset.getId(),
                                            sw.getProductName(),
                                            sw.getProductVersion());

                    if (existingDep.isEmpty()) {
                        AssetDeployment deployment =
                                AssetDeployment.builder()
                                        .asset(asset)
                                        .company(company)
                                        .productName(sw.getProductName())
                                        .productVersion(
                                                sw.getProductVersion())
                                        .productEdition(
                                                sw.getProductEdition())
                                        .rawName(sw.getRawName())
                                        .source(type.name())
                                        .normalized(true)
                                        .build();
                        deploymentRepository.save(deployment);
                        deploymentsSaved++;
                    }
                }

                if (!normalized.getInstalledSoftware().isEmpty()
                        && existing.isPresent()) {
                    Asset toUpdate = existing.get();
                    toUpdate.setHasScriptOutput(true);
                    assetRepository.save(toUpdate);
                }
            }
        }

        log.info("Sync complete. Assets saved: {}, skipped: {}, " +
                        "deployments saved: {}",
                assetsSaved, assetsSkipped, deploymentsSaved);

        // Evict cache after sync — data has changed
        coverageService.evictCoverageCache(companyId);
        log.info("Cache evicted for company {} after {} sync",
                companyId, type);

        return ConnectorSyncResult.builder()
                .connectorType(type)
                .assetsProcessed(normalizedAssets.size())
                .assetsSaved(assetsSaved)
                .assetsSkipped(assetsSkipped)
                .deploymentsFound(normalizedAssets.stream()
                        .mapToInt(a -> a.getInstalledSoftware()
                                != null ?
                                a.getInstalledSoftware().size() : 0)
                        .sum())
                .deploymentsSaved(deploymentsSaved)
                .status("SUCCESS")
                .message("Sync completed for: " +
                        company.getName())
                .build();
    }

    public List<ConnectorSyncResult> syncAll(Long companyId) {
        return Arrays.stream(ConnectorType.values())
                .map(type -> sync(companyId, type))
                .collect(Collectors.toList());
    }




}