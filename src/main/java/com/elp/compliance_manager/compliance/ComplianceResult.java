package com.elp.compliance_manager.compliance;

import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "deployed_quantity")
    private int deployedQuantity;

    @Column(name = "extrapolated_quantity")
    private int extrapolatedQuantity;

    @Column(name = "licensed_quantity")
    private int licensedQuantity;

    @Column(name = "gap")
    private int gap;

    @Enumerated(EnumType.STRING)
    private ComplianceStatus status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.calculatedAt = LocalDateTime.now();
    }
}