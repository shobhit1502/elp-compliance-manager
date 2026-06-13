package com.elp.compliance_manager.connector;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NormalizedSoftware {
    private String productName;
    private String productVersion;
    private String productEdition;
    private String rawName;
}
