package com.elp.compliance_manager.report;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ELPReportGenerator reportGenerator;

    @GetMapping("/elp/{companyId}")
    public ResponseEntity<byte[]> generateELPReport(
            @PathVariable Long companyId) {

        byte[] reportBytes = reportGenerator.generateReport(companyId);

        String filename = "ELP_Report_" + companyId + "_" +
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument" +
                                ".spreadsheetml.sheet"))
                .body(reportBytes);
    }
}