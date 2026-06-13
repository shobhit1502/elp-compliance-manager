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
public class ServiceNowConnector implements DataConnector {

    private final MockDataSource mockDataSource;
    private final AssetNormalizer normalizer;

    @Override
    public ConnectorType getType() {
        return ConnectorType.SERVICENOW_EXPORT;
    }

    @Override
    public List<NormalizedAsset> fetchData(Long companyId) {
        log.info("[ServiceNowConnector] Fetching data for " +
                "company {}", companyId);

//        List<Map<String, Object>> rawData =
//                mockDataSource.getServiceNowData();

        List<Map<String, Object>> rawData =
                mockDataSource.getServiceNowData(companyId);

        List<NormalizedAsset> normalized = new ArrayList<>();

        for (Map<String, Object> raw : rawData) {
            List<NormalizedSoftware> software = new ArrayList<>();

            Object swObj = raw.get("u_installed_software");
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
                    .machineName(str(raw.get("u_hostname")))
                    .operatingSystem(
                            normalizer.normalizeOsName(
                                    str(raw.get("u_os"))))
                    .assetType(
                            normalizer.normalizeAssetType(
                                    str(raw.get("u_classification"))))
                    .department(str(raw.get("u_department")))
                    .lastSeen(str(raw.get("u_last_seen")))
                    .assetSource(AssetSource.MANUAL)
                    .installedSoftware(software)
                    .build();
            normalized.add(asset);
        }

        log.info("[ServiceNowConnector] Fetched {} records",
                normalized.size());
        return normalized;
    }

    private String str(Object obj) {
        return obj != null ? obj.toString() : "";
    }
}