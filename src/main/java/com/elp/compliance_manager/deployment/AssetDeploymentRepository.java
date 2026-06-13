package com.elp.compliance_manager.deployment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetDeploymentRepository
        extends JpaRepository<AssetDeployment, Long> {

    List<AssetDeployment> findByCompanyId(Long companyId);

    List<AssetDeployment> findByAssetId(Long assetId);

    List<AssetDeployment> findByCompanyIdAndProductName(
            Long companyId, String productName);

    Optional<AssetDeployment> findByAssetIdAndProductNameAndProductVersion(
            Long assetId, String productName, String productVersion);

    long countByCompanyIdAndProductName(
            Long companyId, String productName);
}