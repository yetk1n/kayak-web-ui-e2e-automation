package com.sahibinden.pages;

import com.sahibinden.SpringContext;
import com.sahibinden.config.ConfigurationManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage{
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ConfigurationManager config;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.config = SpringContext.getBean(ConfigurationManager.class);
        PageFactory.initElements(driver, this);
    }

    // When checking mobile view
    protected boolean isMobileView() {
        return config.isMobileView();
    }

    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForElementClickable(locator).click();
    }

    protected void setText(By locator, String text) {
        WebElement element = waitForElementVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForElementVisible(locator).getText();
    }

    protected void selectDropdownByVisibleText(By locator, String text) {
        Select select = new Select(waitForElementVisible(locator));
        select.selectByVisibleText(text);
    }

    protected void clickOnText(String text) throws InterruptedException {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='" + text + "']")));
        element.click();
    }

    protected void sendKeys(By locator, String text) {
        WebElement element = waitForElementVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected List<WebElement> findElements(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    protected void scrollToElement(By locator) {
        WebElement element = waitForElementVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
    }

    public boolean isPageLoaded() {
        try {
            return wait.until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            return false;
        }
    }
}