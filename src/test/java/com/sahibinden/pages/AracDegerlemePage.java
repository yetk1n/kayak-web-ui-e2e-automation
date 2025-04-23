package com.sahibinden.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class AracDegerlemePage extends BasePage {

    private final By aracDegerleButton = By.id("evaluateVehiclePrice");
    private final By categoryOtomobil = By.xpath("//span[text()='Otomobil']");
    private final By categoriesBoxes = By.xpath("//ul[@class='category-selectbox']//span");
    private final By kmInput = By.id("mileage-value");
    private final By calculateButton = By.id("calculate-vehicle-price");
    private final By lowerLimit = By.id("lowerLimit");
    private final By median = By.id("median");
    private final By upperLimit = By.id("upperLimit");
    private final By resultPage = By.id("result-page");
    private final By benzerIlanlarLinki = By.cssSelector("a.similar-classifieds-link");


    public AracDegerlemePage(WebDriver driver) {
        super(driver);
    }

    public void verifyUrl(String expectedUrl) {
        wait.until(driver -> driver.getCurrentUrl().contains(expectedUrl));
    }

    public void selectBinek() {
        if (!isMobileView()) {
            click(aracDegerleButton);
            click(categoryOtomobil);
        }
        else {
            System.out.println("HAHAHAH");
        }
    }

    public void selectCategory(String categoryText) throws InterruptedException {
        clickOnText(categoryText);
    }

    public void selectKilometre(String km) {
        sendKeys(kmInput, km);
    }

    public void seeResults() {
        click(calculateButton);
        waitForPageLoad();
    }

    public void verifyValueRanges() {
        WebElement lower = wait.until(ExpectedConditions.visibilityOfElementLocated(lowerLimit));
        WebElement mid = wait.until(ExpectedConditions.visibilityOfElementLocated(median));
        WebElement upper = wait.until(ExpectedConditions.visibilityOfElementLocated(upperLimit));

        // Extract and parse the numeric values
        double lowerMax = extractMaxValue(lower.getText());
        double midMin = extractMinValue(mid.getText());
        double midMax = extractMaxValue(mid.getText());
        double upperMin = extractMinValue(upper.getText());

        assert lowerMax < midMin : "Lower max is not less than median min";
        assert midMax < upperMin : "Median max is not less than upper min";
    }

    private double extractMinValue(String rangeText) {
        return Double.parseDouble(rangeText.split("-")[0].trim().replace(".", "").replace(",", "."));
    }

    private double extractMaxValue(String rangeText) {
        return Double.parseDouble(rangeText.split("-")[1].trim().replace(".", "").replace(",", "."));
    }



    public boolean isDegerlemeSonucDisplayed() {
        return isElementDisplayed(resultPage);
    }


    public IlanlarPage clickIlanlariListele() {
        // Store the current window handle
        String originalWindow = driver.getWindowHandle();

        click(benzerIlanlarLinki);

        // Wait for new tab to open
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // Switch to the new window/tab
        for (String windowHandle : driver.getWindowHandles()) {
            if(!originalWindow.equals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Wait for the new page to load
        waitForPageLoad();

        return new IlanlarPage(driver);
    }
}