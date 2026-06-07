package com.elp.compliance_manager.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCompanyId(Long companyId);

    Optional<Asset> findByMachineNameAndCompanyId(
            String machineName, Long companyId);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndHasScriptOutputTrue(Long companyId);

    List<Asset> findByCompanyIdAndAssetType(
            Long companyId, AssetType assetType);
}