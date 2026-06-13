package com.elp.compliance_manager.connector;

import java.util.List;

public interface DataConnector {
    ConnectorType getType();
    List<NormalizedAsset> fetchData(Long companyId);
}