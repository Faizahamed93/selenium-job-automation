package com.linkedinbot.web;

import com.linkedinbot.model.JobApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class WebAppController {

    @Autowired
    private CsvService csvService;

    @GetMapping("/")
    public String home() {
        return "dashboard";
    }

    @GetMapping("/applied-jobs")
    @ResponseBody
    public ResponseEntity<?> getAppliedJobs() {
        try {
            List<JobApplication> jobs = csvService.readAllJobs();
            return ResponseEntity.ok(jobs);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "No applications history found"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/applied-jobs/{jobId}")
    @ResponseBody
    public ResponseEntity<?> updateAppliedDate(@PathVariable String jobId) {
        try {
            csvService.updateDateApplied(jobId);
            return ResponseEntity.ok(
                Map.of("message", "Date Applied updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "Job ID not found: " + jobId));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
}