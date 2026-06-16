package com.elp.compliance_manager.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SyncJobRepository
        extends JpaRepository<SyncJob, String> {

    List<SyncJob> findByCompanyIdOrderByRequestedAtDesc(
            Long companyId);
}