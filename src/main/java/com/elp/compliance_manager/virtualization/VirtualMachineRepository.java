package com.elp.compliance_manager.virtualization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualMachineRepository
        extends JpaRepository<VirtualMachine, Long> {

    List<VirtualMachine> findByCompanyId(Long companyId);

    List<VirtualMachine> findByCompanyIdAndHostName(
            Long companyId, String hostName);

    List<VirtualMachine> findByCompanyIdAndClusterName(
            Long companyId, String clusterName);

    Optional<VirtualMachine> findByVmNameAndCompanyId(
            String vmName, Long companyId);

    long countByCompanyId(Long companyId);
}