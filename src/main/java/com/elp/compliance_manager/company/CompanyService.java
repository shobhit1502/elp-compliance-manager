package com.elp.compliance_manager.company;

import com.elp.compliance_manager.company.dto.CompanyRequestDTO;
import com.elp.compliance_manager.company.dto.CompanyResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponseDTO createCompany(CompanyRequestDTO request) {
        if (companyRepository.existsByName(request.getName())) {
            throw new RuntimeException("Company with this name already exists");
        }
        Company company = Company.builder()
                .name(request.getName())
                .industry(request.getIndustry())
                .contactEmail(request.getContactEmail())
                .status(request.getStatus())
                .build();
        return toResponseDTO(companyRepository.save(company));
    }

    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CompanyResponseDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        return toResponseDTO(company);
    }

    public CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO request) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        existing.setName(request.getName());
        existing.setIndustry(request.getIndustry());
        existing.setContactEmail(request.getContactEmail());
        existing.setStatus(request.getStatus());
        return toResponseDTO(companyRepository.save(existing));
    }

    public void deleteCompany(Long id) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        companyRepository.delete(existing);
    }

    public List<CompanyResponseDTO> getCompaniesByStatus(CompanyStatus status) {
        return companyRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private CompanyResponseDTO toResponseDTO(Company company) {
        return CompanyResponseDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .industry(company.getIndustry())
                .contactEmail(company.getContactEmail())
                .status(company.getStatus())
                .createdAt(company.getCreatedAt())
                .build();
    }
}