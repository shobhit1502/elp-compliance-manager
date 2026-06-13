package com.elp.compliance_manager.connector.mock;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MockDataSource {

    public List<Map<String, Object>> getADData() {
        List<Map<String, Object>> data = new ArrayList<>();

        data.add(Map.of(
                "computerName", "CONTOSO-WS-001",
                "operatingSystem", "Windows 10 Enterprise",
                "deviceType", "Workstation",
                "domain", "contoso.local",
                "lastLogon", "2024-01-15"
        ));
        data.add(Map.of(
                "computerName", "CONTOSO-WS-002",
                "operatingSystem", "Windows 11 Pro",
                "deviceType", "Workstation",
                "domain", "contoso.local",
                "lastLogon", "2024-01-14"
        ));
        data.add(Map.of(
                "computerName", "CONTOSO-WS-003",
                "operatingSystem", "Windows 10 Pro",
                "deviceType", "Workstation",
                "domain", "contoso.local",
                "lastLogon", "2024-01-13"
        ));
        data.add(Map.of(
                "computerName", "CONTOSO-SRV-001",
                "operatingSystem", "Windows Server 2019 Standard",
                "deviceType", "Server",
                "domain", "contoso.local",
                "lastLogon", "2024-01-15"
        ));
        data.add(Map.of(
                "computerName", "CONTOSO-SRV-002",
                "operatingSystem", "Windows Server 2022 Datacenter",
                "deviceType", "Server",
                "domain", "contoso.local",
                "lastLogon", "2024-01-15"
        ));
        return data;
    }

    public List<Map<String, Object>> getSCCMData() {
        List<Map<String, Object>> data = new ArrayList<>();

        data.add(Map.of(
                "device_name", "CONTOSO-WS-001",
                "os", "Win10 Enterprise",
                "device_type", "Desktop",
                "last_active", "2024-01-15",
                "installed_software", List.of(
                        "MS Office Pro Plus 2019",
                        "Visual Studio Enterprise 2019",
                        "Microsoft SQL Server 2019 Standard"
                )
        ));
        data.add(Map.of(
                "device_name", "CONTOSO-WS-003",
                "os", "Win10 Pro",
                "device_type", "Laptop",
                "last_active", "2024-01-13",
                "installed_software", List.of(
                        "MS Office Pro Plus 2019",
                        "Visio Standard 2019"
                )
        ));
        data.add(Map.of(
                "device_name", "CONTOSO-SRV-001",
                "os", "Windows Server 2019",
                "device_type", "Server",
                "last_active", "2024-01-15",
                "installed_software", List.of(
                        "SQL Server 2019 Standard Edition"
                )
        ));
        data.add(Map.of(
                "device_name", "CONTOSO-SRV-003",
                "os", "Win Server 2016 Std",
                "device_type", "Server",
                "last_active", "2024-01-12",
                "installed_software", List.of(
                        "Exchange Server 2019",
                        "SQL Server 2016 Enterprise"
                )
        ));
        return data;
    }

    public List<Map<String, Object>> getVMwareData() {
        List<Map<String, Object>> data = new ArrayList<>();

        data.add(Map.of(
                "vm_name", "CONTOSO-VM-001",
                "host_name", "CONTOSO-HOST-01",
                "cluster_name", "CLUSTER-PROD",
                "num_cpu", 4,
                "memory_gb", 16,
                "guest_os", "Microsoft Windows Server 2019"
        ));
        data.add(Map.of(
                "vm_name", "CONTOSO-VM-002",
                "host_name", "CONTOSO-HOST-01",
                "cluster_name", "CLUSTER-PROD",
                "num_cpu", 2,
                "memory_gb", 8,
                "guest_os", "Microsoft Windows Server 2022"
        ));
        data.add(Map.of(
                "vm_name", "CONTOSO-VM-003",
                "host_name", "CONTOSO-HOST-02",
                "cluster_name", "CLUSTER-PROD",
                "num_cpu", 8,
                "memory_gb", 32,
                "guest_os", "Microsoft Windows Server 2019"
        ));
        return data;
    }

    public List<Map<String, Object>> getServiceNowData() {
        List<Map<String, Object>> data = new ArrayList<>();

        data.add(Map.of(
                "u_hostname", "CONTOSO-WS-004",
                "u_os", "Microsoft Windows 11",
                "u_classification", "End User Device",
                "u_department", "Finance",
                "u_last_seen", "2024-01-14",
                "u_installed_software", List.of(
                        "Office 2019 Professional Plus",
                        "Project Standard 2019"
                )
        ));
        data.add(Map.of(
                "u_hostname", "CONTOSO-SRV-004",
                "u_os", "Microsoft Windows Server 2022",
                "u_classification", "Server",
                "u_department", "IT",
                "u_last_seen", "2024-01-15",
                "u_installed_software", List.of(
                        "Exchange Server 2019"
                )
        ));
        return data;
    }
}