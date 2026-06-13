package com.elp.compliance_manager.connector;

import com.elp.compliance_manager.asset.AssetType;
import org.springframework.stereotype.Component;

@Component
public class AssetNormalizer {

    public String normalizeOsName(String rawOs) {
        if (rawOs == null || rawOs.isBlank()) return "Unknown";
        String os = rawOs.trim().toLowerCase();

        if (os.contains("windows 11 enterprise"))
            return "Windows 11 Enterprise";
        if (os.contains("windows 11 pro") || os.contains("win11 pro"))
            return "Windows 11 Pro";
        if (os.contains("windows 11"))
            return "Windows 11";
        if (os.contains("windows 10 enterprise")
                || os.contains("win10 enterprise"))
            return "Windows 10 Enterprise";
        if (os.contains("windows 10 pro")
                || os.contains("win10 pro"))
            return "Windows 10 Pro";
        if (os.contains("windows 10")
                || os.contains("win10")
                || os.contains("microsoft windows 10"))
            return "Windows 10";
        if (os.contains("server 2022 datacenter"))
            return "Windows Server 2022 Datacenter";
        if (os.contains("server 2022"))
            return "Windows Server 2022 Standard";
        if (os.contains("server 2019 datacenter"))
            return "Windows Server 2019 Datacenter";
        if (os.contains("server 2019"))
            return "Windows Server 2019 Standard";
        if (os.contains("server 2016 datacenter"))
            return "Windows Server 2016 Datacenter";
        if (os.contains("server 2016"))
            return "Windows Server 2016 Standard";
        return rawOs.trim();
    }

    public AssetType normalizeAssetType(String rawType) {
        if (rawType == null || rawType.isBlank())
            return AssetType.UNKNOWN;
        String type = rawType.trim().toLowerCase();
        return switch (type) {
            case "workstation", "desktop",
                 "laptop", "pc",
                 "end user device" -> AssetType.WORKSTATION;
            case "server" -> AssetType.SERVER;
            case "vm", "virtual machine",
                 "virtual" -> AssetType.VIRTUAL_MACHINE;
            default -> AssetType.UNKNOWN;
        };
    }

    public NormalizedSoftware normalizeSoftware(String rawName) {
        if (rawName == null || rawName.isBlank()) return null;
        String name = rawName.trim().toLowerCase();

        if (name.contains("office") &&
                (name.contains("professional plus")
                        || name.contains("pro plus")
                        || name.contains("proplus")))
            return NormalizedSoftware.builder()
                    .productName("Office")
                    .productVersion(extractYear(name))
                    .productEdition("Professional Plus")
                    .rawName(rawName).build();

        if (name.contains("sql server enterprise")
                || (name.contains("sql") && name.contains("enterprise")))
            return NormalizedSoftware.builder()
                    .productName("SQL Server")
                    .productVersion(extractYear(name))
                    .productEdition("Enterprise")
                    .rawName(rawName).build();

        if (name.contains("sql server standard")
                || (name.contains("sql") && name.contains("standard")))
            return NormalizedSoftware.builder()
                    .productName("SQL Server")
                    .productVersion(extractYear(name))
                    .productEdition("Standard")
                    .rawName(rawName).build();

        if (name.contains("sql server") || name.contains("sql"))
            return NormalizedSoftware.builder()
                    .productName("SQL Server")
                    .productVersion(extractYear(name))
                    .productEdition("Standard")
                    .rawName(rawName).build();

        if (name.contains("visual studio enterprise"))
            return NormalizedSoftware.builder()
                    .productName("Visual Studio")
                    .productVersion(extractYear(name))
                    .productEdition("Enterprise")
                    .rawName(rawName).build();

        if (name.contains("visual studio professional"))
            return NormalizedSoftware.builder()
                    .productName("Visual Studio")
                    .productVersion(extractYear(name))
                    .productEdition("Professional")
                    .rawName(rawName).build();

        if (name.contains("exchange server"))
            return NormalizedSoftware.builder()
                    .productName("Exchange Server")
                    .productVersion(extractYear(name))
                    .productEdition("Standard")
                    .rawName(rawName).build();

        if (name.contains("visio standard"))
            return NormalizedSoftware.builder()
                    .productName("Visio")
                    .productVersion(extractYear(name))
                    .productEdition("Standard")
                    .rawName(rawName).build();

        if (name.contains("visio professional"))
            return NormalizedSoftware.builder()
                    .productName("Visio")
                    .productVersion(extractYear(name))
                    .productEdition("Professional")
                    .rawName(rawName).build();

        if (name.contains("project standard"))
            return NormalizedSoftware.builder()
                    .productName("Project")
                    .productVersion(extractYear(name))
                    .productEdition("Standard")
                    .rawName(rawName).build();

        if (name.contains("project professional"))
            return NormalizedSoftware.builder()
                    .productName("Project")
                    .productVersion(extractYear(name))
                    .productEdition("Professional")
                    .rawName(rawName).build();

        return null;
    }

    private String extractYear(String name) {
        if (name.contains("2022")) return "2022";
        if (name.contains("2021")) return "2021";
        if (name.contains("2019")) return "2019";
        if (name.contains("2016")) return "2016";
        if (name.contains("2014")) return "2014";
        if (name.contains("2013")) return "2013";
        return "";
    }
}