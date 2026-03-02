[README.md](https://github.com/user-attachments/files/25676159/README.md)
# 💼 LinkedIn Auto Applier — Java Edition

> Automated LinkedIn Easy Apply bot built with **Selenium + Spring Boot + Maven + Java 17**

---

## 📌 About

This bot opens Chrome, logs into your LinkedIn account, searches for jobs based on your preferences, and automatically fills and submits Easy Apply applications.

All your settings are stored in a single `application.properties` file — no Java knowledge needed to configure it.

---

## 📊 Project Overview

| Item | Count |
|---|---|
| Packages | 7 |
| Java Classes | 10 |
| Maven Libraries | 6 |
| Config File | 1 |

---

## ✅ Prerequisites — Install These First

| Tool | Version | Download |
|---|---|---|
| Java JDK | 17 or higher | https://adoptium.net |
| Maven | 3.8 or higher | https://maven.apache.org/download.cgi |
| Google Chrome | Latest | https://www.google.com/chrome |
| Eclipse IDE | 2023 or higher | https://www.eclipse.org/downloads |

> **Note:** ChromeDriver is downloaded **automatically** by WebDriverManager. You do NOT need to install it manually. This replaces `windows-setup.bat` and `setup.sh` entirely.

---

## 📁 Project Structure

```
linkedin-bot/
│
├── pom.xml                                         ← Maven dependencies (6 libraries)
│
├── src/main/resources/
│   ├── application.properties                      ← ALL your settings go here
│   ├── logback.xml                                 ← Logging config
│   └── templates/
│       └── dashboard.html                          ← Web dashboard UI
│
├── src/main/java/com/linkedinbot/
│   ├── Main.java                                   ← Entry point — RUN THIS FILE
│   │
│   ├── config/
│   │   └── AppConfig.java                          ← Loads application.properties
│   │
│   ├── browser/
│   │   └── ChromeSessionManager.java               ← Opens and manages Chrome
│   │
│   ├── modules/
│   │   ├── Helpers.java                            ← Logging, sleep, alert utilities
│   │   ├── ClickersAndFinders.java                 ← All Selenium click/find actions
│   │   └── JobApplier.java                         ← Main bot automation loop
│   │
│   ├── model/
│   │   └── JobApplication.java                     ← CSV row data model
│   │
│   ├── validation/
│   │   └── ConfigValidator.java                    ← Validates config before run
│   │
│   └── web/
│       ├── CsvService.java                         ← CSV file read/write
│       └── WebAppController.java                   ← REST API for web dashboard
│
├── all resumes/
│   └── default/
│       └── resume.pdf                              ← PUT YOUR RESUME HERE
│
└── all excels/                                     ← Created automatically on first run
    ├── all_applied_applications_history.csv
    └── all_failed_applications_history.csv
```

---

## 📦 Packages and Classes

| Package | Class | Role |
|---|---|---|
| `com.linkedinbot` | `Main.java` | Entry point — run this class |
| `com.linkedinbot.config` | `AppConfig.java` | Loads all application.properties values |
| `com.linkedinbot.browser` | `ChromeSessionManager.java` | Opens and manages Chrome browser |
| `com.linkedinbot.modules` | `Helpers.java` | Logging, sleep delays, alert dialogs |
| `com.linkedinbot.modules` | `ClickersAndFinders.java` | All Selenium click, find, scroll actions |
| `com.linkedinbot.modules` | `JobApplier.java` | Main bot loop — search, filter, apply |
| `com.linkedinbot.model` | `JobApplication.java` | Data model for one applied job row |
| `com.linkedinbot.validation` | `ConfigValidator.java` | Validates config before bot starts |
| `com.linkedinbot.web` | `CsvService.java` | Reads and writes CSV history files |
| `com.linkedinbot.web` | `WebAppController.java` | REST API endpoints for dashboard |

---

## 🔧 Maven Libraries (pom.xml)

| Library | Version | Purpose |
|---|---|---|
| `selenium-java` | 4.18.1 | Browser automation |
| `webdrivermanager` | 5.7.0 | Auto-downloads ChromeDriver |
| `opencsv` | 5.9 | Read and write CSV files |
| `jackson-databind` | 2.16.1 | JSON parsing for API responses |
| `okhttp` | 4.12.0 | HTTP client for AI API calls |
| `spring-boot-starter-web` | 3.2.2 | REST API and web dashboard |

---

## 🚀 Setup Steps in Eclipse

### Step 1 — Import the Project
1. Open Eclipse → **File → Import**
2. Select **Maven → Existing Maven Projects** → Next
3. Browse to the extracted `linkedin-bot` folder
4. Eclipse detects `pom.xml` automatically → check the checkbox → **Finish**
5. Wait for Maven to download all dependencies (watch bottom-right progress bar)

### Step 2 — Verify Dependencies
In Project Explorer → expand your project → expand **Maven Dependencies**

You should see all 6 JARs listed. If not, right-click project → **Maven → Update Project** → check **Force Update** → OK

### Step 3 — Add Your Resume
Create this folder inside the project and copy your resume PDF into it:
```
linkedin-bot/all resumes/default/resume.pdf
```

### Step 4 — Edit application.properties
Open `src/main/resources/application.properties` and fill in your details (see full reference below)

### Step 5 — Run the Bot
1. Right-click `Main.java` in Project Explorer
2. Click **Run As → Java Application**
3. The console shows the startup menu
4. Type `3` first to validate your config
5. Type `1` to start the bot

---

## 🖥️ Startup Menu

```
==========================================
  LinkedIn Auto Applier - Java Edition
==========================================
[1] Start Bot
[2] Open Web Dashboard
[3] Validate Config Only
[4] Stop Bot
==========================================
Enter choice:
```

| Option | Action | Description |
|---|---|---|
| `1` | Start Bot | Validates config, opens Chrome, logs in, starts applying |
| `2` | Web Dashboard | Starts server → open http://localhost:5000 in browser |
| `3` | Validate Config | Checks application.properties for errors without running |
| `4` | Stop Bot | Stops the bot gracefully after current job finishes |

---

## ⚙️ application.properties — Complete Reference

### 🔐 Login Credentials
```properties
secrets.username=your_email@gmail.com
secrets.password=yourpassword123
```

### 👤 Personal Details
```properties
personal.first_name=John
personal.middle_name=William
personal.last_name=Doe
personal.phone_number=9876543210
personal.current_city=New York
personal.street=123 Main Street
personal.state=New York
personal.zipcode=10001
personal.country=United States

# Options: Decline, Hispanic/Latino, Asian, Black or African American, White, Other
personal.ethnicity=Decline

# Options: Male, Female, Other, Decline
personal.gender=Decline

# Options: Yes, No, Decline
personal.disability_status=Decline
personal.veteran_status=Decline
```

### 🔍 Job Search Settings
```properties
# Comma-separated job titles to search
search.terms=Software Engineer,Java Developer,Full Stack Developer

# City, state, or country
search.location=United States

# Max applications per search term before switching to next term
search.switch_number=30

# Options: Any time, Past month, Past week, Past 24 hours
search.date_posted=Past week

# true = only show Easy Apply jobs
search.easy_apply_only=true

# Options: Full-time, Part-time, Contract, Temporary, Volunteer, Internship, Other
search.job_type=Full-time

# Options: On-site, Remote, Hybrid (comma-separated)
search.on_site=Remote,Hybrid

# Options: Internship, Entry level, Associate, Mid-Senior level, Director, Executive
search.experience_level=Mid-Senior level

# Skip jobs containing these words in the job description
search.bad_words=US Citizen,USA Citizen,No C2C,PHP,Ruby

# Skip companies with these words in their About section
search.about_company_bad_words=Crossover,Staffing

# Your years of experience (-1 = apply to all jobs regardless of required experience)
search.current_experience=5

# Pause after applying filters to let you review before applying starts
search.pause_after_filters=true
```

### 📝 Application Questions
```properties
# Path to your resume PDF
questions.default_resume_path=all resumes/default/resume.pdf

# Years of experience answer
questions.years_of_experience=5

# Options: Yes, No
questions.require_visa=No

# Your portfolio or GitHub link
questions.website=https://github.com/YourUsername

# Your LinkedIn profile URL
questions.linkedin=https://www.linkedin.com/in/yourprofile/

# Options: U.S. Citizen/Permanent Resident, Non-citizen allowed to work for any employer,
#          Non-citizen seeking work authorization, Canadian Citizen/Permanent Resident, Other
questions.us_citizenship=U.S. Citizen/Permanent Resident

# Expected salary — numbers only
questions.desired_salary=120000

# Current salary — numbers only
questions.current_ctc=80000

# Notice period in days
questions.notice_period=30

# Your LinkedIn headline
questions.linkedin_headline=Full Stack Java Developer with 5+ years of experience

# Your LinkedIn summary
questions.linkedin_summary=I am an experienced Full Stack Developer with 5 years of experience in Java, Spring Boot, React, and AWS.

# Your cover letter
questions.cover_letter=Dear Hiring Manager, I am excited to apply for this position and believe my skills are a strong match.

# Your most recent employer
questions.recent_employer=ABC Technologies

# Confidence level 1 to 10
questions.confidence_level=8

# true = bot pauses before submitting so you can review
questions.pause_before_submit=true

# true = bot pauses if it cannot answer a question automatically
questions.pause_at_failed_question=true

questions.overwrite_previous_answers=false
```

### 🤖 Bot Settings
```properties
# Stop bot automatically after this many applications (0 = no limit)
settings.max_applications=10

# false = show Chrome window (recommended), true = run hidden
settings.run_in_background=false

# true = use a temporary guest Chrome profile (recommended)
settings.safe_mode=true

# Seconds to pause between each click action
settings.click_gap=1

# CSV output files
settings.file_name=all excels/all_applied_applications_history.csv
settings.failed_file_name=all excels/all_failed_applications_history.csv

# Logs folder
settings.logs_folder=logs/

settings.disable_extensions=false
settings.smooth_scroll=false
settings.follow_companies=false
settings.close_tabs=false

# Web dashboard port
server.port=5000
```

### 🧠 AI Settings (Optional)
```properties
# Set to true only if you have an AI API key
secrets.use_ai=false

# Options: openai, deepseek, gemini
secrets.ai_provider=openai

secrets.llm_api_url=https://api.openai.com/v1/
secrets.llm_api_key=your_api_key_here
secrets.llm_model=gpt-4o-mini
secrets.stream_output=false
```

---

## 📂 Output Files

| File | Description |
|---|---|
| `all excels/all_applied_applications_history.csv` | All successfully applied jobs |
| `all excels/all_failed_applications_history.csv` | Failed or skipped applications |
| `logs/log.txt` | Full activity log with timestamps |

All files are **created automatically** when the bot runs for the first time.

### CSV Columns

| Column | Description |
|---|---|
| Job ID | LinkedIn job ID number |
| Title | Job title |
| Company | Company name |
| HR Name | HR contact name if found |
| HR Link | LinkedIn profile of HR |
| Job Link | LinkedIn job URL |
| External Job Link | External application URL or "Easy Applied" |
| Date Applied | Timestamp of when applied |
| Status | Applied / Failed / Skipped |

---

## 🌐 Web Dashboard

Start with option `2`, then open:
```
http://localhost:5000
```

Features:
- View all applied jobs in a sortable, searchable table
- Filter by Applied / Pending / Easy Apply / External
- Statistics bar showing totals
- Click external links → auto-marks them as Applied

---

## 🔴 Stop Bot After N Applications

Set this in `application.properties`:
```properties
# Bot stops automatically after 10 applications
settings.max_applications=10
```

Console output when limit is reached:
```
==========================================
Limit reached! Applied: 10/10
Bot stopping now.
==========================================
Bot stopped. Total applied: 10
```

Set to `0` for no limit:
```properties
settings.max_applications=0
```

---

## 🐛 Troubleshooting

| Error / Problem | Fix |
|---|---|
| `invalid session id / browser closed` | Chrome closed unexpectedly. Add longer timeouts in `ChromeSessionManager.java`. Also update Chrome to latest version. |
| `application.properties not found` | Run Maven → Update Project in Eclipse first. Make sure you are running `Main.java`. |
| `CsvValidationException on readNext()` | Add `throws CsvValidationException` to the method and import `com.opencsv.exceptions.CsvValidationException` |
| Switch expression not supported | Eclipse is using Java 8 or 11. Right-click project → Properties → Java Build Path → change JRE to Java 17 |
| Chrome won't open | Make sure Google Chrome is installed. WebDriverManager handles ChromeDriver automatically. |
| Login fails / CAPTCHA shown | Log in manually in the opened Chrome window then click OK on the dialog. |
| Port 5000 already in use | Change `server.port=5000` to `server.port=8080` in `application.properties` |
| Maven not downloading | Check internet connection. Disable VPN. Right-click project → Maven → Update Project → Force Update. |
| No job cards found | LinkedIn may have updated its HTML. Check that `search.easy_apply_only=true` and your search terms return results manually. |
| Bot applies to wrong jobs | Add keywords to `search.bad_words` in `application.properties` to skip unwanted descriptions. |

### Fix for Chrome Closed Error (from log.txt)

If you see this error:
```
invalid session id: session deleted as the browser has closed the connection
```

Add these lines in `ChromeSessionManager.java` after creating the driver:

```java
driver = new ChromeDriver(options);
driver.manage().window().maximize();

// Add these lines
driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
wait = new WebDriverWait(driver, Duration.ofSeconds(30));
```

And in `JobApplier.java` inside `loginToLinkedIn()`:

```java
driver.get(LINKEDIN + "/login");
Helpers.buffer(3);   // increase from 2 to 3
```

---

## 🔄 Python to Java Mapping

| Python File | Java Equivalent |
|---|---|
| `runAiBot.py` | `Main.java` + `JobApplier.java` |
| `open_chrome.py` | `ChromeSessionManager.java` |
| `helpers.py` | `Helpers.java` |
| `clickers_and_finders.py` | `ClickersAndFinders.java` |
| `personals.py` | `application.properties` (personal.*) |
| `secrets.py` | `application.properties` (secrets.*) |
| `search.py` | `application.properties` (search.*) |
| `questions.py` | `application.properties` (questions.*) |
| `settings.py` | `application.properties` (settings.*) |
| `validator.py` | `ConfigValidator.java` |
| `app.py` (Flask) | `WebAppController.java` + `CsvService.java` |
| `index.html` | `dashboard.html` (Thymeleaf) |
| `windows-setup.bat` + `setup.sh` | **Eliminated** — WebDriverManager does it in 1 line |

---

## 🛠️ Built With

- [Selenium Java 4.18.1](https://www.selenium.dev/) — Browser automation
- [WebDriverManager 5.7.0](https://bonigarcia.dev/webdrivermanager/) — Auto ChromeDriver
- [Spring Boot 3.2.2](https://spring.io/projects/spring-boot) — REST API and web server
- [OpenCSV 5.9](http://opencsv.sourceforge.net/) — CSV file handling
- [Jackson 2.16.1](https://github.com/FasterXML/jackson) — JSON parsing
- [OkHttp 4.12.0](https://square.github.io/okhttp/) — HTTP client

---

*LinkedIn Auto Applier — Java Edition*
