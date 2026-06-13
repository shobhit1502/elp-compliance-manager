package com.elp.compliance_manager.compliance.rules;

import com.elp.compliance_manager.entitlement.Entitlement;
import com.elp.compliance_manager.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class VersionDowngradeRule implements LicensingRule {

    private static final Map<String, List<String>>
            DOWNGRADE_MATRIX = Map.of(
            "Windows", List.of(
                    "11", "10", "8.1", "8", "7"),
            "Windows Server", List.of(
                    "2022", "2019", "2016", "2012 R2", "2012"),
            "SQL Server", List.of(
                    "2022", "2019", "2017", "2016", "2014", "2012"),
            "Office", List.of(
                    "2021", "2019", "2016", "2013", "2010"),
            "Exchange Server", List.of(
                    "2019", "2016", "2013"),
            "Visio", List.of(
                    "2021", "2019", "2016", "2013"),
            "Project", List.of(
                    "2021", "2019", "2016", "2013"),
            "Visual Studio", List.of(
                    "2022", "2019", "2017", "2015")
    );

    @Override
    public String getRuleName() {
        return "VERSION_DOWNGRADE_RIGHTS";
    }

    @Override
    public int applyRule(Product deployedProduct,
                         int deployedQty,
                         List<Entitlement> allEntitlements) {

        String productName = deployedProduct.getName();
        String deployedVersion = deployedProduct.getVersion();
        String deployedEdition = deployedProduct.getEdition();

        List<String> versionOrder =
                DOWNGRADE_MATRIX.get(productName);
        if (versionOrder == null) return 0;

        int deployedVersionIndex =
                versionOrder.indexOf(deployedVersion);
        if (deployedVersionIndex == -1) return 0;

        int totalCoveredByDowngrade = 0;

        for (Entitlement entitlement : allEntitlements) {
            if (!entitlement.getProduct().getName()
                    .equals(productName)) continue;

            String licensedVersion =
                    entitlement.getProduct().getVersion();
            String licensedEdition =
                    entitlement.getProduct().getEdition();
            int licensedVersionIndex =
                    versionOrder.indexOf(licensedVersion);

            if (licensedVersionIndex == -1) continue;

            boolean isNewerVersion =
                    licensedVersionIndex < deployedVersionIndex;
            boolean isSameEdition =
                    editionMatches(licensedEdition, deployedEdition);

            if (isNewerVersion && isSameEdition) {
                totalCoveredByDowngrade +=
                        entitlement.getQuantity();
                log.info("[VersionDowngradeRule] {} {} {} " +
                                "license covers {} {} deployment. " +
                                "Qty: {}",
                        productName, licensedVersion,
                        licensedEdition,
                        deployedVersion, deployedEdition,
                        entitlement.getQuantity());
            }
        }

        return totalCoveredByDowngrade;
    }

    private boolean editionMatches(String licensed,
                                   String deployed) {
        if (licensed == null || deployed == null) return true;
        if (licensed.equalsIgnoreCase(deployed)) return true;

        String l = licensed.toLowerCase();
        String d = deployed.toLowerCase();

        if (l.contains("enterprise") && d.contains("enterprise"))
            return true;
        if (l.contains("standard") && d.contains("standard"))
            return true;
        if (l.contains("professional plus") &&
                d.contains("professional plus")) return true;
        if (l.contains("datacenter") && d.contains("datacenter"))
            return true;

        return false;
    }
}