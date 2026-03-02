package com.linkedinbot.modules;

import com.linkedinbot.browser.ChromeSessionManager;
import com.linkedinbot.model.JobApplication;
import com.linkedinbot.web.CsvService;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JobApplier {

    private final Properties props;
    private final ChromeSessionManager chrome;
    private ClickersAndFinders cf;
    private final CsvService csvService;

    private static final String LINKEDIN  = "https://www.linkedin.com";
    private static final String JOBS_URL  = "https://www.linkedin.com/jobs/";
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JobApplier() throws Exception {
        this.props      = loadProperties();
        this.chrome     = new ChromeSessionManager();
        this.csvService = new CsvService();
        Helpers.setLogsFilePath(
            props.getProperty("settings.logs_folder", "logs/") + "log.txt");
        Helpers.makeDirectories(
            props.getProperty("settings.file_name"),
            props.getProperty("settings.failed_file_name"),
            props.getProperty("settings.logs_folder"));
    }

    public void run() {
        try {
            chrome.createSession(
                bool("settings.run_in_background"),
                bool("settings.disable_extensions"),
                bool("settings.safe_mode"));

            WebDriver driver   = chrome.getDriver();
            WebDriverWait wait = chrome.getWait();

            cf = new ClickersAndFinders(
                driver, wait, chrome.getActions(),
                integer("settings.click_gap"));

            loginToLinkedIn(driver);

            List<String> terms = parseList(props.getProperty("search.terms",""));
            if (bool("search.randomize_order")) Collections.shuffle(terms);

            for (String term : terms) {
                Helpers.printLog("=== Searching: " + term + " ===");
                searchAndApply(driver, wait, term);
            }

            Helpers.printLog("=== All searches done! ===");

        } catch (Exception e) {
            Helpers.printLog("Fatal error", e);
            Helpers.showAlert("Bot error: " + e.getMessage(), "Error");
        } finally {
            chrome.closeSession();
        }
    }

    private void loginToLinkedIn(WebDriver driver) throws Exception {
        driver.get(LINKEDIN + "/login");
        Helpers.buffer(2);
        if (driver.getCurrentUrl().contains("/feed")) {
            Helpers.printLog("Already logged in!");
            return;
        }
        cf.loginWithCredentials(
            props.getProperty("secrets.username"),
            props.getProperty("secrets.password"));
        Helpers.buffer(3);
        if (!driver.getCurrentUrl().contains("/feed")) {
            Helpers.showAlert(
                "Complete login manually, then click OK.",
                "Manual Login Required");
        }
    }

    private void searchAndApply(WebDriver driver,
                                 WebDriverWait wait,
                                 String term) throws Exception {
        driver.get(JOBS_URL);
        Helpers.buffer(2);

        try {
            WebElement box = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(
                ".//input[contains(@aria-label,'Search jobs')"
                + " or contains(@placeholder,'Search jobs')]")));
            box.clear();
            box.sendKeys(term);
            box.sendKeys(Keys.RETURN);
            Helpers.buffer(2);
        } catch (Exception e) {
            Helpers.printLog("Search box not found for: " + term);
            return;
        }

        applyFilters(driver);

        if (bool("search.pause_after_filters"))
            Helpers.showAlert(
                "Filters set for: " + term + "\nClick OK to start applying.",
                "Review Filters");

        int applied = 0;
        int limit   = integer("search.switch_number");

        while (applied < limit) {
            List<WebElement> cards = driver.findElements(By.xpath(
                ".//li[contains(@class,'jobs-search-results__list-item')]"));
            for (WebElement card : cards) {
                if (applied >= limit) break;
                if (processJobCard(driver, wait, card)) applied++;
                Helpers.buffer(2);
            }
            if (!goToNextPage(driver)) break;
        }

        Helpers.printLog("Done: " + term + " | Applied: " + applied);
    }

    private void applyFilters(WebDriver driver) {
        if (bool("search.easy_apply_only")) {
            cf.waitSpanClick("Easy Apply");
            Helpers.buffer(1);
        }
        String datePosted = props.getProperty("search.date_posted","");
        if (!datePosted.isBlank()) {
            clickFilter(driver, "Date posted");
            cf.waitSpanClick(datePosted);
            cf.waitSpanClick("Show results");
        }
    }

    private void clickFilter(WebDriver driver, String label) {
        try {
            driver.findElement(By.xpath(
                ".//button[contains(.,'" + label + "')]")).click();
            Helpers.buffer(1);
        } catch (Exception e) {
            Helpers.printLog("Filter not found: " + label);
        }
    }

    private boolean processJobCard(WebDriver driver,
                                    WebDriverWait wait,
                                    WebElement card) {
        try {
            card.click();
            Helpers.buffer(2);

            String title   = getText(driver, "h1");
            String company = getText(driver,
                ".job-details-jobs-unified-top-card__company-name");
            String jobLink = driver.getCurrentUrl();
            String jobId   = extractJobId(jobLink);

            Helpers.printLog("Processing: " + title + " @ " + company);

            if (containsBadWords(driver)) {
                Helpers.printLog("SKIPPED (bad word): " + title);
                return false;
            }

            if (!clickEasyApply(driver, wait)) return false;

            boolean submitted = fillAndSubmit(driver, wait);
            if (!submitted) return false;

            csvService.appendAppliedJob(new JobApplication(
                jobId, title, company, "Unknown", "",
                jobLink, "Easy Applied",
                LocalDateTime.now().format(FMT)));

            Helpers.printLog("✓ Applied: " + title + " @ " + company);
            return true;

        } catch (Exception e) {
            Helpers.printLog("Error on job card", e);
            return false;
        }
    }

    private boolean clickEasyApply(WebDriver driver, WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(".//button[contains(@aria-label,'Easy Apply')"
                       + " or (contains(@class,'jobs-apply-button')"
                       + " and contains(.,'Easy Apply'))]"))).click();
            Helpers.buffer(2);
            return true;
        } catch (Exception e) { return false; }
    }

    private boolean fillAndSubmit(WebDriver driver, WebDriverWait wait) {
        try {
            for (int step = 0; step < 10; step++) {
                Helpers.buffer(1);

                boolean onReview = !driver.findElements(By.xpath(
                    ".//button[contains(.,'Submit application')]")).isEmpty();

                if (onReview) {
                    if (bool("questions.pause_before_submit")) {
                        boolean go = Helpers.showConfirm(
                            "Ready to submit. Click Yes to submit.",
                            "Confirm");
                        if (!go) { closeModal(driver); return false; }
                    }
                    driver.findElement(By.xpath(
                        ".//button[contains(.,'Submit application')]")).click();
                    Helpers.buffer(2);
                    closeModal(driver);
                    return true;
                }

                boolean moved =
                    cf.tryXpath(".//button[contains(.,'Next')]") ||
                    cf.tryXpath(".//button[contains(.,'Continue')]") ||
                    cf.tryXpath(".//button[contains(.,'Review')]");

                if (!moved) { closeModal(driver); return false; }
            }
        } catch (Exception e) {
            Helpers.printLog("Form error", e);
            try { closeModal(driver); } catch (Exception ignored) {}
        }
        return false;
    }

    private void closeModal(WebDriver driver) {
        try {
            driver.findElement(By.xpath(
                ".//button[@aria-label='Dismiss'"
                + " or @aria-label='Close']")).click();
            Helpers.buffer(1);
            cf.tryXpath(".//button[contains(.,'Discard')]");
        } catch (Exception ignored) {}
    }

    private boolean containsBadWords(WebDriver driver) {
        List<String> bad = parseList(props.getProperty("search.bad_words",""));
        if (bad.isEmpty()) return false;
        try {
            String desc = driver.findElement(By.cssSelector(
                ".jobs-description,.jobs-box__html-content"))
                .getText().toLowerCase();
            return bad.stream().anyMatch(w -> desc.contains(w.toLowerCase()));
        } catch (Exception e) { return false; }
    }

    private boolean goToNextPage(WebDriver driver) {
        try {
            WebElement next = driver.findElement(By.xpath(
                ".//button[@aria-label='Next']"));
            if (next.isEnabled()) { next.click(); Helpers.buffer(3); return true; }
        } catch (Exception ignored) {}
        return false;
    }

    private String getText(WebDriver driver, String css) {
        try { return driver.findElement(By.cssSelector(css)).getText().trim(); }
        catch (Exception e) { return "Unknown"; }
    }

    private String extractJobId(String url) {
        try {
            String[] parts = url.split("/");
            for (int i = 0; i < parts.length - 1; i++)
                if ((parts[i].equals("view") || parts[i].equals("jobs"))
                        && parts[i+1].matches("\\d+"))
                    return parts[i+1];
        } catch (Exception ignored) {}
        return String.valueOf(System.currentTimeMillis());
    }

    private boolean bool(String key) {
        return Boolean.parseBoolean(props.getProperty(key,"false"));
    }

    private int integer(String key) {
        try { return Integer.parseInt(props.getProperty(key,"1").trim()); }
        catch (NumberFormatException e) { return 1; }
    }

    private List<String> parseList(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split(","))
                     .map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    private static Properties loadProperties() throws Exception {
        Properties p = new Properties();
        var s = JobApplier.class.getClassLoader()
                    .getResourceAsStream("application.properties");
        if (s == null) throw new RuntimeException(
            "application.properties not found!");
        p.load(s);
        return p;
    }
}
