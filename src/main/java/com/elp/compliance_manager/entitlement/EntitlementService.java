package com.elp.compliance_manager.entitlement;

import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.company.CompanyRepository;
import com.elp.compliance_manager.entitlement.dto.EntitlementRequestDTO;
import com.elp.compliance_manager.entitlement.dto.EntitlementResponseDTO;
import com.elp.compliance_manager.product.Product;
import com.elp.compliance_manager.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.elp.compliance_manager.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntitlementService {

    private final EntitlementRepository entitlementRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;

    public EntitlementResponseDTO addEntitlement(
            Long companyId, EntitlementRequestDTO request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: "
                                + request.getProductId()));

        Entitlement entitlement = Entitlement.builder()
                .company(company)
                .product(product)
                .quantity(request.getQuantity())
                .licenseType(request.getLicenseType())
                .licenseMetric(request.getLicenseMetric())
                .hasSoftwareAssurance(request.isHasSoftwareAssurance())
                .poNumber(request.getPoNumber())
                .source(request.getSource())
                .purchaseDate(request.getPurchaseDate())
                .expiryDate(request.getExpiryDate())
                .notes(request.getNotes())
                .build();

        return toResponseDTO(entitlementRepository.save(entitlement));
    }

    public List<EntitlementResponseDTO> getEntitlementsByCompany(
            Long companyId) {
        return entitlementRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public EntitlementResponseDTO getEntitlementById(Long id) {
        Entitlement entitlement = entitlementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Entitlement not found with id: " + id));
        return toResponseDTO(entitlement);
    }

    public EntitlementResponseDTO updateEntitlement(
            Long id, EntitlementRequestDTO request) {
        Entitlement existing = entitlementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Entitlement not found with id: " + id));
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: "
                                + request.getProductId()));

        existing.setProduct(product);
        existing.setQuantity(request.getQuantity());
        existing.setLicenseType(request.getLicenseType());
        existing.setLicenseMetric(request.getLicenseMetric());
        existing.setHasSoftwareAssurance(
                request.isHasSoftwareAssurance());
        existing.setPoNumber(request.getPoNumber());
        existing.setSource(request.getSource());
        existing.setPurchaseDate(request.getPurchaseDate());
        existing.setExpiryDate(request.getExpiryDate());
        existing.setNotes(request.getNotes());

        return toResponseDTO(entitlementRepository.save(existing));
    }

    public void deleteEntitlement(Long id) {
        entitlementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Entitlement not found with id: " + id));
        entitlementRepository.deleteById(id);
    }

    public List<EntitlementResponseDTO> ingestEntitlementCSV(
            Long companyId, MultipartFile file) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));

        List<Entitlement> entitlements = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(",");
                if (cols.length < 4) continue;

                Long productId = Long.parseLong(cols[0].trim());
                Product product = productRepository.findById(productId)
                        .orElse(null);
                if (product == null) continue;

                Entitlement entitlement = Entitlement.builder()
                        .company(company)
                        .product(product)
                        .quantity(Integer.parseInt(cols[1].trim()))
                        .licenseType(LicenseType.valueOf(cols[2].trim()))
                        .licenseMetric(LicenseMetric.valueOf(cols[3].trim()))
                        .hasSoftwareAssurance(cols.length > 4 &&
                                Boolean.parseBoolean(cols[4].trim()))
                        .source(cols.length > 5 ? cols[5].trim() : "")
                        .purchaseDate(cols.length > 6 &&
                                !cols[6].trim().isEmpty() ?
                                LocalDate.parse(cols[6].trim()) : null)
                        .notes(cols.length > 7 ? cols[7].trim() : "")
                        .build();
                entitlements.add(entitlement);
            }

            entitlementRepository.saveAll(entitlements);
            log.info("Ingested {} entitlements for company {}",
                    entitlements.size(), company.getName());

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse entitlement CSV: " + e.getMessage());
        }

        return entitlements.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private EntitlementResponseDTO toResponseDTO(
            Entitlement entitlement) {
        return EntitlementResponseDTO.builder()
                .id(entitlement.getId())
                .companyId(entitlement.getCompany().getId())
                .companyName(entitlement.getCompany().getName())
                .productId(entitlement.getProduct().getId())
                .productName(entitlement.getProduct().getName())
                .productVersion(entitlement.getProduct().getVersion())
                .productEdition(entitlement.getProduct().getEdition())
                .quantity(entitlement.getQuantity())
                .licenseType(entitlement.getLicenseType())
                .licenseMetric(entitlement.getLicenseMetric())
                .hasSoftwareAssurance(
                        entitlement.isHasSoftwareAssurance())
                .poNumber(entitlement.getPoNumber())
                .source(entitlement.getSource())
                .purchaseDate(entitlement.getPurchaseDate())
                .expiryDate(entitlement.getExpiryDate())
                .notes(entitlement.getNotes())
                .createdAt(entitlement.getCreatedAt())
                .build();
    }
}
