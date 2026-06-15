package com.elp.compliance_manager.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCompanyId(Long companyId);

    Optional<Asset> findByMachineNameAndCompanyId(
            String machineName, Long companyId);

    @Query("SELECT COUNT(a) FROM Asset a " +
            "WHERE a.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM Asset a " +
            "WHERE a.company.id = :companyId " +
            "AND a.hasScriptOutput = true")
    long countCoveredByCompanyId(
            @Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM Asset a " +
            "WHERE a.company.id = :companyId " +
            "AND a.assetType = 'WORKSTATION'")
    long countWorkstationsByCompanyId(
            @Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM Asset a " +
            "WHERE a.company.id = :companyId " +
            "AND a.assetType = 'WORKSTATION' " +
            "AND a.hasScriptOutput = true")
    long countCoveredWorkstationsByCompanyId(
            @Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM Asset a " +
            "WHERE a.company.id = :companyId " +
            "AND a.assetType = 'SERVER'")
    long countServersByCompanyId(
            @Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM Asset a " +
            "WHERE a.company.id = :companyId " +
            "AND a.assetType = 'SERVER' " +
            "AND a.hasScriptOutput = true")
    long countCoveredServersByCompanyId(
            @Param("companyId") Long companyId);

    @Query("SELECT a FROM Asset a " +
            "WHERE a.company.id = :companyId " +
            "AND a.hasScriptOutput = true")
    List<Asset> findCoveredByCompanyId(
            @Param("companyId") Long companyId);
}


