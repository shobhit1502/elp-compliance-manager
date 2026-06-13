package com.elp.compliance_manager.compliance.rules;

import com.elp.compliance_manager.entitlement.Entitlement;
import com.elp.compliance_manager.product.Product;
import java.util.List;

public interface LicensingRule {
    String getRuleName();
    int applyRule(Product deployedProduct,
                  int deployedQty,
                  List<Entitlement> allEntitlements);
}