package com.elp.compliance_manager.company;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company createCompany(Company company) {
        if (companyRepository.existsByName(company.getName())) {
            throw new RuntimeException("Company with this name already exists");
        }
        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
    }

    public Company updateCompany(Long id, Company updatedCompany) {
        Company existing = getCompanyById(id);
        existing.setName(updatedCompany.getName());
        existing.setIndustry(updatedCompany.getIndustry());
        existing.setContactEmail(updatedCompany.getContactEmail());
        existing.setStatus(updatedCompany.getStatus());
        return companyRepository.save(existing);
    }

    public void deleteCompany(Long id) {
        Company existing = getCompanyById(id);
        companyRepository.delete(existing);
    }

    public List<Company> getCompaniesByStatus(CompanyStatus status) {
        return companyRepository.findByStatus(status);
    }
}