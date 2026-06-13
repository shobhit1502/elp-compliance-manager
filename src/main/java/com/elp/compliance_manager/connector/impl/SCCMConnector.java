package com.elp.compliance_manager.connector.impl;

import com.elp.compliance_manager.asset.AssetSource;
import com.elp.compliance_manager.connector.*;
import com.elp.compliance_manager.connector.mock.MockDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SCCMConnector implements DataConnector {

    private final MockDataSource mockDataSource;
    private final AssetNormalizer normalizer;

    @Override
    public ConnectorType getType() {
        return ConnectorType.SCCM_EXPORT;
    }

    @Override
    public List<NormalizedAsset> fetchData(Long companyId) {
        log.info("[SCCMConnector] Fetching data for company {}",
                companyId);

//        List<Map<String, Object>> rawData =
//                mockDataSource.getSCCMData();

        List<Map<String, Object>> rawData =
                mockDataSource.getSCCMData(companyId);

        List<NormalizedAsset> normalized = new ArrayList<>();

        for (Map<String, Object> raw : rawData) {
            List<NormalizedSoftware> software = new ArrayList<>();

            Object swObj = raw.get("installed_software");
            if (swObj instanceof List<?> swList) {
                for (Object sw : swList) {
                    NormalizedSoftware normalizedSw =
                            normalizer.normalizeSoftware(
                                    sw.toString());
                    if (normalizedSw != null)
                        software.add(normalizedSw);
                }
            }

            NormalizedAsset asset = NormalizedAsset.builder()
                    .machineName(str(raw.get("device_name")))
                    .operatingSystem(
                            normalizer.normalizeOsName(
                                    str(raw.get("os"))))
                    .assetType(
                            normalizer.normalizeAssetType(
                                    str(raw.get("device_type"))))
                    .lastSeen(str(raw.get("last_active")))
                    .assetSource(AssetSource.SCRIPT_OUTPUT)
                    .installedSoftware(software)
                    .build();
            normalized.add(asset);
        }

        log.info("[SCCMConnector] Fetched {} records with " +
                "software inventory", normalized.size());
        return normalized;
    }

    private String str(Object obj) {
        return obj != null ? obj.toString() : "";
    }
}