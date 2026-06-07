package com.elp.compliance_manager.entitlement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntitlementRepository
        extends JpaRepository<Entitlement, Long> {

    List<Entitlement> findByCompanyId(Long companyId);

    List<Entitlement> findByCompanyIdAndProductId(
            Long companyId, Long productId);

    int countByCompanyId(Long companyId);
}