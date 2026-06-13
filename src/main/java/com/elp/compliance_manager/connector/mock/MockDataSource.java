package com.elp.compliance_manager.connector.mock;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MockDataSource {

    public List<Map<String, Object>> getADData(Long companyId) {
        if (companyId == 3) return getTailwindADData();
        return getContosoADData();
    }

    public List<Map<String, Object>> getSCCMData(Long companyId) {
        if (companyId == 3) return getTailwindSCCMData();
        return getContosoSCCMData();
    }

    public List<Map<String, Object>> getVMwareData(Long companyId) {
        if (companyId == 3) return getTailwindVMwareData();
        return getContosoVMwareData();
    }

    public List<Map<String, Object>> getServiceNowData(Long companyId) {
        if (companyId == 3) return getTailwindServiceNowData();
        return getContosoServiceNowData();
    }

    // ─── CONTOSO DATA ───────────────────────────────────────

    private List<Map<String, Object>> getContosoADData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("computerName", "CONTOSO-WS-001",
                "operatingSystem", "Windows 10 Enterprise",
                "deviceType", "Workstation",
                "domain", "contoso.local",
                "lastLogon", "2024-01-15"));
        data.add(Map.of("computerName", "CONTOSO-WS-002",
                "operatingSystem", "Windows 11 Pro",
                "deviceType", "Workstation",
                "domain", "contoso.local",
                "lastLogon", "2024-01-14"));
        data.add(Map.of("computerName", "CONTOSO-WS-003",
                "operatingSystem", "Windows 10 Pro",
                "deviceType", "Workstation",
                "domain", "contoso.local",
                "lastLogon", "2024-01-13"));
        data.add(Map.of("computerName", "CONTOSO-SRV-001",
                "operatingSystem", "Windows Server 2019 Standard",
                "deviceType", "Server",
                "domain", "contoso.local",
                "lastLogon", "2024-01-15"));
        data.add(Map.of("computerName", "CONTOSO-SRV-002",
                "operatingSystem", "Windows Server 2022 Datacenter",
                "deviceType", "Server",
                "domain", "contoso.local",
                "lastLogon", "2024-01-15"));
        return data;
    }

    private List<Map<String, Object>> getContosoSCCMData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of(
                "device_name", "CONTOSO-WS-001",
                "os", "Win10 Enterprise",
                "device_type", "Desktop",
                "last_active", "2024-01-15",
                "installed_software", List.of(
                        "MS Office Pro Plus 2019",
                        "Visual Studio Enterprise 2019",
                        "Microsoft SQL Server 2019 Standard")));
        data.add(Map.of(
                "device_name", "CONTOSO-WS-003",
                "os", "Win10 Pro",
                "device_type", "Laptop",
                "last_active", "2024-01-13",
                "installed_software", List.of(
                        "MS Office Pro Plus 2019",
                        "Visio Standard 2019")));
        data.add(Map.of(
                "device_name", "CONTOSO-SRV-001",
                "os", "Windows Server 2019",
                "device_type", "Server",
                "last_active", "2024-01-15",
                "installed_software", List.of(
                        "SQL Server 2019 Standard Edition")));
        data.add(Map.of(
                "device_name", "CONTOSO-SRV-003",
                "os", "Win Server 2016 Std",
                "device_type", "Server",
                "last_active", "2024-01-12",
                "installed_software", List.of(
                        "Exchange Server 2019",
                        "SQL Server 2016 Enterprise")));
        return data;
    }

    private List<Map<String, Object>> getContosoVMwareData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("vm_name", "CONTOSO-VM-001",
                "host_name", "CONTOSO-HOST-01",
                "cluster_name", "CLUSTER-PROD",
                "num_cpu", 4, "memory_gb", 16,
                "guest_os", "Microsoft Windows Server 2019"));
        data.add(Map.of("vm_name", "CONTOSO-VM-002",
                "host_name", "CONTOSO-HOST-01",
                "cluster_name", "CLUSTER-PROD",
                "num_cpu", 2, "memory_gb", 8,
                "guest_os", "Microsoft Windows Server 2022"));
        data.add(Map.of("vm_name", "CONTOSO-VM-003",
                "host_name", "CONTOSO-HOST-02",
                "cluster_name", "CLUSTER-PROD",
                "num_cpu", 8, "memory_gb", 32,
                "guest_os", "Microsoft Windows Server 2019"));
        return data;
    }

    private List<Map<String, Object>> getContosoServiceNowData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of(
                "u_hostname", "CONTOSO-WS-004",
                "u_os", "Microsoft Windows 11",
                "u_classification", "End User Device",
                "u_department", "Finance",
                "u_last_seen", "2024-01-14",
                "u_installed_software", List.of(
                        "Office 2019 Professional Plus",
                        "Project Standard 2019")));
        data.add(Map.of(
                "u_hostname", "CONTOSO-SRV-004",
                "u_os", "Microsoft Windows Server 2022",
                "u_classification", "Server",
                "u_department", "IT",
                "u_last_seen", "2024-01-15",
                "u_installed_software", List.of(
                        "Exchange Server 2019")));
        return data;
    }

    // ─── TAILWIND TRADERS DATA ──────────────────────────────

    private List<Map<String, Object>> getTailwindADData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("computerName", "TAILWIND-WS-001",
                "operatingSystem", "Windows 11 Enterprise",
                "deviceType", "Workstation",
                "domain", "tailwind.local",
                "lastLogon", "2024-02-10"));
        data.add(Map.of("computerName", "TAILWIND-WS-002",
                "operatingSystem", "Windows 11 Enterprise",
                "deviceType", "Workstation",
                "domain", "tailwind.local",
                "lastLogon", "2024-02-10"));
        data.add(Map.of("computerName", "TAILWIND-WS-003",
                "operatingSystem", "Windows 10 Pro",
                "deviceType", "Workstation",
                "domain", "tailwind.local",
                "lastLogon", "2024-02-09"));
        data.add(Map.of("computerName", "TAILWIND-WS-004",
                "operatingSystem", "Windows 10 Pro",
                "deviceType", "Workstation",
                "domain", "tailwind.local",
                "lastLogon", "2024-02-08"));
        data.add(Map.of("computerName", "TAILWIND-WS-005",
                "operatingSystem", "Windows 10 Enterprise",
                "deviceType", "Workstation",
                "domain", "tailwind.local",
                "lastLogon", "2024-02-07"));
        data.add(Map.of("computerName", "TAILWIND-SRV-001",
                "operatingSystem", "Windows Server 2022 Standard",
                "deviceType", "Server",
                "domain", "tailwind.local",
                "lastLogon", "2024-02-10"));
        data.add(Map.of("computerName", "TAILWIND-SRV-002",
                "operatingSystem", "Windows Server 2019 Standard",
                "deviceType", "Server",
                "domain", "tailwind.local",
                "lastLogon", "2024-02-10"));
        data.add(Map.of("computerName", "TAILWIND-SRV-003",
                "operatingSystem", "Windows Server 2019 Datacenter",
                "deviceType", "Server",
                "domain", "tailwind.local",
                "lastLogon", "2024-02-09"));
        return data;
    }

    private List<Map<String, Object>> getTailwindSCCMData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of(
                "device_name", "TAILWIND-WS-001",
                "os", "Win11 Enterprise",
                "device_type", "Desktop",
                "last_active", "2024-02-10",
                "installed_software", List.of(
                        "MS Office Pro Plus 2019",
                        "Visio Professional 2019",
                        "Project Professional 2019")));
        data.add(Map.of(
                "device_name", "TAILWIND-WS-002",
                "os", "Win11 Enterprise",
                "device_type", "Desktop",
                "last_active", "2024-02-10",
                "installed_software", List.of(
                        "MS Office Pro Plus 2019",
                        "Visual Studio Enterprise 2019")));
        data.add(Map.of(
                "device_name", "TAILWIND-WS-003",
                "os", "Win10 Pro",
                "device_type", "Laptop",
                "last_active", "2024-02-09",
                "installed_software", List.of(
                        "MS Office Pro Plus 2019")));
        data.add(Map.of(
                "device_name", "TAILWIND-SRV-001",
                "os", "Windows Server 2022 Standard",
                "device_type", "Server",
                "last_active", "2024-02-10",
                "installed_software", List.of(
                        "SQL Server 2019 Enterprise")));
        data.add(Map.of(
                "device_name", "TAILWIND-SRV-002",
                "os", "Windows Server 2019",
                "device_type", "Server",
                "last_active", "2024-02-10",
                "installed_software", List.of(
                        "SQL Server 2016 Standard",
                        "Exchange Server 2019")));
        return data;
    }

    private List<Map<String, Object>> getTailwindVMwareData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("vm_name", "TAILWIND-VM-001",
                "host_name", "TAILWIND-HOST-01",
                "cluster_name", "TAILWIND-CLUSTER-01",
                "num_cpu", 4, "memory_gb", 16,
                "guest_os", "Microsoft Windows Server 2022"));
        data.add(Map.of("vm_name", "TAILWIND-VM-002",
                "host_name", "TAILWIND-HOST-01",
                "cluster_name", "TAILWIND-CLUSTER-01",
                "num_cpu", 4, "memory_gb", 16,
                "guest_os", "Microsoft Windows Server 2019"));
        data.add(Map.of("vm_name", "TAILWIND-VM-003",
                "host_name", "TAILWIND-HOST-02",
                "cluster_name", "TAILWIND-CLUSTER-01",
                "num_cpu", 2, "memory_gb", 8,
                "guest_os", "Microsoft Windows Server 2019"));
        data.add(Map.of("vm_name", "TAILWIND-VM-004",
                "host_name", "TAILWIND-HOST-02",
                "cluster_name", "TAILWIND-CLUSTER-01",
                "num_cpu", 8, "memory_gb", 32,
                "guest_os", "Microsoft Windows Server 2022"));
        return data;
    }

    private List<Map<String, Object>> getTailwindServiceNowData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of(
                "u_hostname", "TAILWIND-WS-004",
                "u_os", "Microsoft Windows 10",
                "u_classification", "End User Device",
                "u_department", "Sales",
                "u_last_seen", "2024-02-08",
                "u_installed_software", List.of(
                        "Office 2019 Professional Plus")));
        data.add(Map.of(
                "u_hostname", "TAILWIND-WS-005",
                "u_os", "Microsoft Windows 10 Enterprise",
                "u_classification", "End User Device",
                "u_department", "HR",
                "u_last_seen", "2024-02-07",
                "u_installed_software", List.of(
                        "Office 2019 Professional Plus",
                        "Visio Standard 2019")));
        data.add(Map.of(
                "u_hostname", "TAILWIND-SRV-003",
                "u_os", "Microsoft Windows Server 2019 Datacenter",
                "u_classification", "Server",
                "u_department", "IT",
                "u_last_seen", "2024-02-09",
                "u_installed_software", List.of(
                        "SQL Server 2019 Enterprise")));
        return data;
    }
}