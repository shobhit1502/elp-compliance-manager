package com.elp.compliance_manager.report;

import com.elp.compliance_manager.asset.Asset;
import com.elp.compliance_manager.asset.AssetRepository;
import com.elp.compliance_manager.company.Company;
import com.elp.compliance_manager.company.CompanyRepository;
import com.elp.compliance_manager.compliance.ComplianceResult;
import com.elp.compliance_manager.compliance.ComplianceResultRepository;
import com.elp.compliance_manager.coverage.CoverageResponseDTO;
import com.elp.compliance_manager.coverage.CoverageService;
import com.elp.compliance_manager.entitlement.Entitlement;
import com.elp.compliance_manager.entitlement.EntitlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ELPReportGenerator {

    private final CompanyRepository companyRepository;
    private final AssetRepository assetRepository;
    private final EntitlementRepository entitlementRepository;
    private final ComplianceResultRepository complianceResultRepository;
    private final CoverageService coverageService;

    public byte[] generateReport(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CoverageResponseDTO coverage =
                    coverageService.calculateCoverage(companyId);
            List<Asset> assets =
                    assetRepository.findByCompanyId(companyId);
            List<Entitlement> entitlements =
                    entitlementRepository.findByCompanyId(companyId);
            List<ComplianceResult> results =
                    complianceResultRepository.findByCompanyId(companyId);

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);

            createSummarySheet(workbook, company, coverage,
                    results, headerStyle, titleStyle);
            createCoverageSheet(workbook, coverage,
                    headerStyle, titleStyle);
            createAssetSheet(workbook, assets,
                    headerStyle, titleStyle);
            createComplianceSheet(workbook, results,
                    headerStyle, titleStyle);
            createEntitlementSheet(workbook, entitlements,
                    headerStyle, titleStyle);

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();
            workbook.write(outputStream);
            log.info("ELP Report generated for company: {}",
                    company.getName());
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate ELP report: " + e.getMessage());
        }
    }

    private void createSummarySheet(XSSFWorkbook workbook,
                                    Company company, CoverageResponseDTO coverage,
                                    List<ComplianceResult> results,
                                    CellStyle headerStyle, CellStyle titleStyle) {

        Sheet sheet = workbook.createSheet("Summary");
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 6000);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ELP COMPLIANCE REPORT");
        titleCell.setCellStyle(titleStyle);

        rowNum++;
        createDataRow(sheet, rowNum++, "Company", company.getName());
        createDataRow(sheet, rowNum++, "Report Date",
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        createDataRow(sheet, rowNum++, "Engagement Status",
                company.getStatus().name());

        rowNum++;
        Row coverageHeader = sheet.createRow(rowNum++);
        Cell coverageCell = coverageHeader.createCell(0);
        coverageCell.setCellValue("COVERAGE SUMMARY");
        coverageCell.setCellStyle(headerStyle);

        createDataRow(sheet, rowNum++, "Total Assets",
                String.valueOf(coverage.getTotalAssets()));
        createDataRow(sheet, rowNum++, "Covered Assets",
                String.valueOf(coverage.getCoveredAssets()));
        createDataRow(sheet, rowNum++, "Coverage %",
                coverage.getCoveragePercentage() + "%");
        createDataRow(sheet, rowNum++, "Extrapolation Factor",
                String.valueOf(coverage.getExtrapolationFactor()));

        rowNum++;
        Row complianceHeader = sheet.createRow(rowNum++);
        Cell complianceCell = complianceHeader.createCell(0);
        complianceCell.setCellValue("COMPLIANCE SUMMARY");
        complianceCell.setCellStyle(headerStyle);

        long compliant = results.stream().filter(r ->
                r.getStatus().name().equals("COMPLIANT")).count();
        long underLicensed = results.stream().filter(r ->
                r.getStatus().name().equals("UNDER_LICENSED")).count();
        long overLicensed = results.stream().filter(r ->
                r.getStatus().name().equals("OVER_LICENSED")).count();

        createDataRow(sheet, rowNum++, "Total Products Analyzed",
                String.valueOf(results.size()));
        createDataRow(sheet, rowNum++, "Compliant Products",
                String.valueOf(compliant));
        createDataRow(sheet, rowNum++, "Under-Licensed Products",
                String.valueOf(underLicensed));
        createDataRow(sheet, rowNum++, "Over-Licensed Products",
                String.valueOf(overLicensed));
    }

    private void createCoverageSheet(XSSFWorkbook workbook,
                                     CoverageResponseDTO coverage,
                                     CellStyle headerStyle, CellStyle titleStyle) {

        Sheet sheet = workbook.createSheet("Coverage Analysis");
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("COVERAGE ANALYSIS");
        titleRow.getCell(0).setCellStyle(titleStyle);

        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Metric", "Count", "Percentage"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Object[][] data = {
                {"Total In-Scope Assets", coverage.getTotalAssets(), "100%"},
                {"Covered Assets", coverage.getCoveredAssets(),
                        coverage.getCoveragePercentage() + "%"},
                {"Uncovered Assets", coverage.getUncoveredAssets(),
                        (100 - coverage.getCoveragePercentage()) + "%"},
                {"Total Workstations", coverage.getTotalWorkstations(), ""},
                {"Covered Workstations", coverage.getCoveredWorkstations(), ""},
                {"Total Servers", coverage.getTotalServers(), ""},
                {"Covered Servers", coverage.getCoveredServers(), ""}
        };

        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowData[0].toString());
            row.createCell(1).setCellValue(rowData[1].toString());
            row.createCell(2).setCellValue(rowData[2].toString());
        }
    }

    private void createAssetSheet(XSSFWorkbook workbook,
                                  List<Asset> assets,
                                  CellStyle headerStyle, CellStyle titleStyle) {

        Sheet sheet = workbook.createSheet("Asset Register");
        int[] widths = {6000, 5000, 6000, 4000, 4000, 5000};
        for (int i = 0; i < widths.length; i++)
            sheet.setColumnWidth(i, widths[i]);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("ASSET REGISTER");
        titleRow.getCell(0).setCellStyle(titleStyle);

        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Machine Name", "OS", "Asset Type",
                "Domain", "Script Coverage", "Last Seen"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        for (Asset asset : assets) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(asset.getMachineName());
            row.createCell(1).setCellValue(
                    asset.getOperatingSystem() != null ?
                            asset.getOperatingSystem() : "");
            row.createCell(2).setCellValue(
                    asset.getAssetType() != null ?
                            asset.getAssetType().name() : "");
            row.createCell(3).setCellValue(
                    asset.getDomain() != null ? asset.getDomain() : "");
            row.createCell(4).setCellValue(
                    asset.isHasScriptOutput() ? "Yes" : "No");
            row.createCell(5).setCellValue(
                    asset.getLastSeen() != null ? asset.getLastSeen() : "");
        }
    }

    private void createComplianceSheet(XSSFWorkbook workbook,
                                       List<ComplianceResult> results,
                                       CellStyle headerStyle, CellStyle titleStyle) {

        Sheet sheet = workbook.createSheet("Compliance Results");
        int[] widths = {5000, 3000, 3000, 5000, 5000, 5000, 4000, 5000};
        for (int i = 0; i < widths.length; i++)
            sheet.setColumnWidth(i, widths[i]);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("COMPLIANCE RESULTS");
        titleRow.getCell(0).setCellStyle(titleStyle);

        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Product", "Version", "Edition",
                "Category", "Deployed", "Licensed", "Gap", "Status"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        for (ComplianceResult result : results) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(
                    result.getProduct().getName());
            row.createCell(1).setCellValue(
                    result.getProduct().getVersion() != null ?
                            result.getProduct().getVersion() : "");
            row.createCell(2).setCellValue(
                    result.getProduct().getEdition() != null ?
                            result.getProduct().getEdition() : "");
            row.createCell(3).setCellValue(
                    result.getProduct().getCategory().name());
            row.createCell(4).setCellValue(
                    result.getExtrapolatedQuantity());
            row.createCell(5).setCellValue(
                    result.getLicensedQuantity());
            row.createCell(6).setCellValue(result.getGap());
            row.createCell(7).setCellValue(
                    result.getStatus().name());
        }
    }

    private void createEntitlementSheet(XSSFWorkbook workbook,
                                        List<Entitlement> entitlements,
                                        CellStyle headerStyle, CellStyle titleStyle) {

        Sheet sheet = workbook.createSheet("Entitlements");
        int[] widths = {5000, 3000, 3000, 4000, 5000, 4000, 5000};
        for (int i = 0; i < widths.length; i++)
            sheet.setColumnWidth(i, widths[i]);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("ENTITLEMENTS");
        titleRow.getCell(0).setCellStyle(titleStyle);

        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Product", "Version", "Edition",
                "Quantity", "License Type", "Metric", "Source"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        for (Entitlement e : entitlements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.getProduct().getName());
            row.createCell(1).setCellValue(
                    e.getProduct().getVersion() != null ?
                            e.getProduct().getVersion() : "");
            row.createCell(2).setCellValue(
                    e.getProduct().getEdition() != null ?
                            e.getProduct().getEdition() : "");
            row.createCell(3).setCellValue(e.getQuantity());
            row.createCell(4).setCellValue(
                    e.getLicenseType().name());
            row.createCell(5).setCellValue(
                    e.getLicenseMetric().name());
            row.createCell(6).setCellValue(
                    e.getSource() != null ? e.getSource() : "");
        }
    }

    private void createDataRow(Sheet sheet, int rowNum,
                               String label, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT
                .getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }
}