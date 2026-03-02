package com.linkedinbot.modules;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class ClickersAndFinders {

    private final WebDriver driver;
    @SuppressWarnings("unused")
	private final WebDriverWait wait;
    private final Actions actions;
    private final int clickGap;

    public ClickersAndFinders(WebDriver driver, WebDriverWait wait,
                               Actions actions, int clickGap) {
        this.driver   = driver;
        this.wait     = wait;
        this.actions  = actions;
        this.clickGap = clickGap;
    }

    public WebElement waitSpanClick(String text, int timeout,
                                     boolean click, boolean scrollTo) {
        if (text == null || text.isBlank()) return null;
        try {
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(timeout))
                .until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(".//span[normalize-space(.)='" + text + "']")));
            if (scrollTo) scrollToView(el);
            if (click) { el.click(); Helpers.buffer(clickGap); }
            return el;
        } catch (Exception e) {
            Helpers.printLog("waitSpanClick: Didn't find '" + text + "'");
            return null;
        }
    }

    public WebElement waitSpanClick(String text) {
        return waitSpanClick(text, 5, true, true);
    }

    public void multiSelect(List<String> texts) {
        for (String text : texts) waitSpanClick(text, 5, true, true);
    }

    public void booleanButtonClick(String labelText) {
        try {
            WebElement fieldset = driver.findElement(
                By.xpath(".//h3[normalize-space()='" + labelText
                         + "']/ancestor::fieldset"));
            WebElement toggle = fieldset.findElement(
                By.xpath(".//input[@role='switch']"));
            scrollToView(toggle);
            actions.moveToElement(toggle).click().perform();
            Helpers.buffer(clickGap);
        } catch (Exception e) {
            Helpers.printLog("booleanButtonClick: Not found '" + labelText + "'");
        }
    }

    public WebElement findByClass(String className, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout))
            .until(ExpectedConditions.presenceOfElementLocated(
                By.className(className)));
    }

    public boolean tryXpath(String xpath, boolean click) {
        try {
            WebElement el = driver.findElement(By.xpath(xpath));
            if (click) el.click();
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean tryXpath(String xpath) {
        return tryXpath(xpath, true);
    }

    public void scrollToView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center',behavior:'instant'});",
            element);
    }

    public void textInputById(String id, String value, int timeout) {
        WebElement field = new WebDriverWait(driver, Duration.ofSeconds(timeout))
            .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(value);
    }

    public void loginWithCredentials(String username, String password) {
        textInputById("username", username, 10);
        textInputById("password", password, 10);
        tryXpath(".//button[@type='submit']");
        Helpers.buffer(3);
    }

    public void companySearchClick(String companyName) {
        waitSpanClick("Add a company", 1, true, false);
        try {
            WebElement searchBox = driver.findElement(
                By.xpath("(.//input[@placeholder='Add a company'])[1]"));
            searchBox.sendKeys(Keys.CONTROL + "a");
            searchBox.sendKeys(companyName);
            Helpers.buffer(3);
            actions.sendKeys(Keys.ARROW_DOWN).perform();
            actions.sendKeys(Keys.ENTER).perform();
        } catch (Exception e) {
            Helpers.printLog("companySearchClick: Failed for '" + companyName + "'");
        }
    }
}
