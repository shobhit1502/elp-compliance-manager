package com.elp.compliance_manager.entitlement;

import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "entitlements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;

    @Enumerated(EnumType.STRING)
    private LicenseMetric licenseMetric;

    @Column(name = "has_software_assurance")
    private boolean hasSoftwareAssurance;

    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "source")
    private String source;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}