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
public class ADConnector implements DataConnector {

    private final MockDataSource mockDataSource;
    private final AssetNormalizer normalizer;

    @Override
    public ConnectorType getType() {
        return ConnectorType.AD_EXPORT;
    }

    @Override
    public List<NormalizedAsset> fetchData(Long companyId) {
        log.info("[ADConnector] Fetching data for company {}",
                companyId);

        List<Map<String, Object>> rawData =
                mockDataSource.getADData();

        List<NormalizedAsset> normalized = new ArrayList<>();

        for (Map<String, Object> raw : rawData) {
            NormalizedAsset asset = NormalizedAsset.builder()
                    .machineName(
                            str(raw.get("computerName")))
                    .operatingSystem(
                            normalizer.normalizeOsName(
                                    str(raw.get("operatingSystem"))))
                    .assetType(
                            normalizer.normalizeAssetType(
                                    str(raw.get("deviceType"))))
                    .domain(str(raw.get("domain")))
                    .lastSeen(str(raw.get("lastLogon")))
                    .assetSource(AssetSource.AD_EXPORT)
                    .installedSoftware(new ArrayList<>())
                    .build();
            normalized.add(asset);
        }

        log.info("[ADConnector] Fetched and normalized {} records",
                normalized.size());
        return normalized;
    }

    private String str(Object obj) {
        return obj != null ? obj.toString() : "";
    }
}