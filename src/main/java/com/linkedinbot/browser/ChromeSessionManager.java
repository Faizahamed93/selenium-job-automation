
package com.linkedinbot.browser;

import com.linkedinbot.modules.Helpers;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ChromeSessionManager {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    public void createSession(boolean runInBackground,
                               boolean disableExtensions,
                               boolean safeMode) {
        Helpers.printLog("Setting up ChromeDriver...");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        if (runInBackground) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        if (disableExtensions) {
            options.addArguments("--disable-extensions");
        }

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches",
            new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        if (safeMode) {
            String tempProfile = System.getProperty("java.io.tmpdir")
                                 + File.separator + "linkedin-bot-profile";
            options.addArguments("--user-data-dir=" + tempProfile);
            Helpers.printLog("Using temp Chrome profile (safe mode)");
        }

        options.addArguments("--start-maximized");

        driver  = new ChromeDriver(options);
        wait    = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);

        Helpers.printLog("Chrome started successfully.");
    }

    public void closeSession() {
        if (driver != null) {
            try { driver.quit(); }
            catch (Exception e) { Helpers.printLog("Error closing Chrome", e); }
        }
    }

    public WebDriver getDriver()     { return driver; }
    public WebDriverWait getWait()   { return wait; }
    public Actions getActions()      { return actions; }
}