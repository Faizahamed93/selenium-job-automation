package com.linkedinbot.model;

public class JobApplication {

    private String jobId;
    private String title;
    private String company;
    private String hrName;
    private String hrLink;
    private String jobLink;
    private String externalJobLink;
    private String dateApplied;
    private String status;

    public JobApplication() {}

    public JobApplication(String jobId, String title, String company,
                          String hrName, String hrLink,
                          String jobLink, String externalJobLink,
                          String dateApplied) {
        this.jobId = jobId;
        this.title = title;
        this.company = company;
        this.hrName = hrName;
        this.hrLink = hrLink;
        this.jobLink = jobLink;
        this.externalJobLink = externalJobLink;
        this.dateApplied = dateApplied;
        this.status = "Applied";
    }

    public static final String[] CSV_HEADERS = {
        "Job ID", "Title", "Company", "HR Name", "HR Link",
        "Job Link", "External Job link", "Date Applied", "Status"
    };

    public String[] toCsvRow() {
        return new String[]{ jobId, title, company, hrName, hrLink,
                             jobLink, externalJobLink, dateApplied, status };
    }

    // Getters and Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getHrName() { return hrName; }
    public void setHrName(String hrName) { this.hrName = hrName; }

    public String getHrLink() { return hrLink; }
    public void setHrLink(String hrLink) { this.hrLink = hrLink; }

    public String getJobLink() { return jobLink; }
    public void setJobLink(String jobLink) { this.jobLink = jobLink; }

    public String getExternalJobLink() { return externalJobLink; }
    public void setExternalJobLink(String v) { this.externalJobLink = v; }

    public String getDateApplied() { return dateApplied; }
    public void setDateApplied(String dateApplied) { this.dateApplied = dateApplied; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
