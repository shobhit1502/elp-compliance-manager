package com.elp.compliance_manager.deployment;

import com.elp.compliance_manager.asset.Asset;
import com.elp.compliance_manager.company.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_deployments",
        indexes = {
                @Index(name = "idx_dep_company_id",
                        columnList = "company_id"),
                @Index(name = "idx_dep_asset_id",
                        columnList = "asset_id"),
                @Index(name = "idx_dep_product_name",
                        columnList = "product_name"),
                @Index(name = "idx_dep_company_product",
                        columnList = "company_id,product_name")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDeployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_version")
    private String productVersion;

    @Column(name = "product_edition")
    private String productEdition;

    @Column(name = "raw_name")
    private String rawName;

    @Column(name = "source")
    private String source;

    @Column(name = "normalized")
    private boolean normalized;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}