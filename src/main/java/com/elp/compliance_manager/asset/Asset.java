package com.elp.compliance_manager.asset;

import com.elp.compliance_manager.company.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Table(name = "assets",
        indexes = {
                @Index(name = "idx_assets_company_id",
                        columnList = "company_id"),
                @Index(name = "idx_assets_has_script",
                        columnList = "has_script_output"),
                @Index(name = "idx_assets_company_script",
                        columnList = "company_id,has_script_output")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_machine_company",
                        columnNames = {"machine_name", "company_id"})
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "machine_name", nullable = false)
    private String machineName;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "os_version")
    private String osVersion;

    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    @Enumerated(EnumType.STRING)
    private AssetSource assetSource;

    private String domain;

    @Column(name = "last_seen")
    private String lastSeen;

    @Column(name = "is_in_scope")
    private boolean isInScope;

    @Column(name = "has_script_output")
    private boolean hasScriptOutput;

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