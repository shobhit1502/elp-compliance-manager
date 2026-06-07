package com.elp.compliance_manager.virtualization;

import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.company.CompanyRepository;
import com.elp.compliance_manager.virtualization.dto.VirtualMachineResponseDTO;
import com.elp.compliance_manager.virtualization.dto.VirtualizationTopologyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualizationService {

    private final VirtualMachineRepository vmRepository;
    private final CompanyRepository companyRepository;

    public int ingestVMwareExport(Long companyId, MultipartFile file) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));

        List<VirtualMachine> vms = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(",");
                if (cols.length < 3) continue;

                String vmName = cols[0].trim();
                if (vmRepository.findByVmNameAndCompanyId(
                        vmName, companyId).isPresent()) continue;

                VirtualMachine vm = VirtualMachine.builder()
                        .vmName(vmName)
                        .hostName(cols[1].trim())
                        .clusterName(cols.length > 2 ?
                                cols[2].trim() : "")
                        .vCPU(cols.length > 3 ?
                                parseInt(cols[3].trim()) : 0)
                        .ramGb(cols.length > 4 ?
                                parseInt(cols[4].trim()) : 0)
                        .operatingSystem(cols.length > 5 ?
                                cols[5].trim() : "")
                        .powerState(cols.length > 6 ?
                                cols[6].trim() : "Unknown")
                        .company(company)
                        .build();
                vms.add(vm);
            }
            vmRepository.saveAll(vms);
            log.info("Ingested {} VMs for company {}",
                    vms.size(), company.getName());

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse VMware export: " + e.getMessage());
        }
        return vms.size();
    }

    public VirtualizationTopologyDTO getTopology(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));

        List<VirtualMachine> vms =
                vmRepository.findByCompanyId(companyId);

        Map<String, List<VirtualMachine>> byHost = vms.stream()
                .collect(Collectors.groupingBy(
                        VirtualMachine::getHostName));

        long totalClusters = vms.stream()
                .map(VirtualMachine::getClusterName)
                .distinct().count();

        List<HostSummary> hostSummaries = byHost.entrySet()
                .stream()
                .map(entry -> HostSummary.builder()
                        .hostName(entry.getKey())
                        .clusterName(entry.getValue()
                                .get(0).getClusterName())
                        .vmCount(entry.getValue().size())
                        .totalVCPU(entry.getValue().stream()
                                .mapToInt(VirtualMachine::getVCPU)
                                .sum())
                        .vmNames(entry.getValue().stream()
                                .map(VirtualMachine::getVmName)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return VirtualizationTopologyDTO.builder()
                .companyId(companyId)
                .companyName(company.getName())
                .totalVMs(vms.size())
                .totalHosts(byHost.size())
                .totalClusters(totalClusters)
                .hostSummaries(hostSummaries)
                .build();
    }

    public List<VirtualMachineResponseDTO> getVMsByCompany(
            Long companyId) {
        return vmRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private VirtualMachineResponseDTO toResponseDTO(VirtualMachine vm) {
        return VirtualMachineResponseDTO.builder()
                .id(vm.getId())
                .vmName(vm.getVmName())
                .hostName(vm.getHostName())
                .clusterName(vm.getClusterName())
                .vCPU(vm.getVCPU())
                .ramGb(vm.getRamGb())
                .operatingSystem(vm.getOperatingSystem())
                .powerState(vm.getPowerState())
                .isInScope(vm.isInScope())
                .companyId(vm.getCompany().getId())
                .createdAt(vm.getCreatedAt())
                .build();
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { return 0; }
    }
}