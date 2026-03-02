package com.linkedinbot.web;

import com.linkedinbot.model.JobApplication;
import com.linkedinbot.modules.Helpers;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    @Value("${settings.file_name}")
    private String appliedCsvPath;

    @Value("${settings.failed_file_name}")
    private String failedCsvPath;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<JobApplication> readAllJobs() throws IOException, CsvValidationException {
        List<JobApplication> jobs = new ArrayList<>();
        File f = new File(appliedCsvPath);

        if (!f.exists()) {
            throw new FileNotFoundException("CSV not found: " + appliedCsvPath);
        }

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(
                    new FileInputStream(f), StandardCharsets.UTF_8))) {

            reader.readNext(); // skip header row

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length < 8) continue;

                JobApplication job = new JobApplication();
                job.setJobId(row[0].trim());
                job.setTitle(row[1].trim());
                job.setCompany(row[2].trim());
                job.setHrName(row[3].trim());
                job.setHrLink(row[4].trim());
                job.setJobLink(row[5].trim());
                job.setExternalJobLink(row[6].trim());
                job.setDateApplied(row[7].trim());
                jobs.add(job);
            }
        }
        return jobs;
    }

    public void updateDateApplied(String jobId) throws IOException, CsvValidationException {
        File f = new File(appliedCsvPath);

        if (!f.exists()) {
            throw new FileNotFoundException("CSV not found: " + appliedCsvPath);
        }

        List<String[]> rows = new ArrayList<>();
        String[] headers;
        boolean found = false;

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(
                    new FileInputStream(f), StandardCharsets.UTF_8))) {

            headers = reader.readNext(); // read header

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length > 0 && row[0].equals(jobId)) {
                    if (row.length > 7) {
                        row[7] = LocalDateTime.now().format(FMT);
                    }
                    found = true;
                }
                rows.add(row);
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Job ID not found: " + jobId);
        }

        try (CSVWriter writer = new CSVWriter(
                new OutputStreamWriter(
                    new FileOutputStream(f), StandardCharsets.UTF_8))) {
            writer.writeNext(headers);
            writer.writeAll(rows);
        }
    }

    public void appendAppliedJob(JobApplication job) {
        appendToCsv(appliedCsvPath, job);
    }

    public void appendFailedJob(JobApplication job) {
        appendToCsv(failedCsvPath, job);
    }

    private void appendToCsv(String path, JobApplication job) {
        try {
            File f = new File(path);
            boolean needsHeader = !f.exists() || f.length() == 0;
            f.getParentFile().mkdirs();

            try (CSVWriter writer = new CSVWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(f, true), StandardCharsets.UTF_8))) {
                if (needsHeader) {
                    writer.writeNext(JobApplication.CSV_HEADERS);
                }
                writer.writeNext(job.toCsvRow());
            }
        } catch (IOException e) {
            Helpers.printLog("Failed to write CSV: " + path, e);
        }
    }
}