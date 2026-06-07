package com.elp.compliance_manager.virtualization;

import com.elp.compliance_manager.company.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "virtual_machines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VirtualMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vm_name", nullable = false)
    private String vmName;

    @Column(name = "host_name", nullable = false)
    private String hostName;

    @Column(name = "cluster_name")
    private String clusterName;

    @Column(name = "v_cpu")
    private int vCPU;

    @Column(name = "ram_gb")
    private int ramGb;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "power_state")
    private String powerState;

    @Column(name = "is_in_scope")
    private boolean isInScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isInScope = true;
    }
}