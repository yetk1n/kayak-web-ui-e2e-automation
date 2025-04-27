package com.sahibinden.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class AracDegerlemePage extends BasePage {

    // Web elements
    private final By aracDegerleButton = By.id("evaluateVehiclePrice");
    private final By categoryOtomobil = By.xpath("//span[text()='Otomobil']");
    private final By categoryOtomobilMobile = By.linkText("Otomobil");

    private final By categoriesBoxes = By.xpath("//ul[@class='category-selectbox']//span");
//    private final By  = By.id("mileage-value");
    private final By kmInput = By.xpath("//input[contains(@placeholder, 'Kilometre') or @id='mileage-value' or @id='mileageInput']");
    private final By calculateButton = By.xpath("//button[text()='Aracın Değerini Göster'] | //a[span[text()='Aracın Değerini Göster']]");

    private final By lowerLimit = By.id("lowerLimit");
    private final By tumFiyatAraliklariniGosterMobile = By.className("show-all-price");
    private final By lowerLimitMobile = By.xpath("//li[.//span[text()='Düşük Fiyat']]//span[contains(@class,'item-price-range')]");
    private final By median = By.id("median");
    private final By medianMobile = By.xpath("//li[.//span[text()='Piyasa Ortalaması']]//span[contains(@class,'item-price-range')]");
    private final By upperLimit = By.id("upperLimit");
    private final By upperLimitMobile = By.xpath("//li[.//span[text()='Yüksek Fiyat']]//span[contains(@class,'item-price-range')]");
//    private final By resultPage = By.id("result-page");
    private final By resultPage = By.className("results");

    private final By benzerIlanlarLinki = By.cssSelector("a.similar-classifieds-link");

    // Locators for mobile


    public AracDegerlemePage(WebDriver driver) {
        super(driver);
    }

    // URL kontrolü either ""oto360/arac-degerleme/alirken" or "oto360/arac-degerleme/satarken"
//    public void verifyUrl(String expectedUrl) {
//        wait.until(driver -> driver.getCurrentUrl().contains(expectedUrl));
//    }

    public void verifyUrl(String url1, String url2) {
        wait.until(driver -> {
            String currentUrl = driver.getCurrentUrl();
            return currentUrl.contains(url1) || currentUrl.contains(url2);
        });
    }

    public void selectBinek() {
        click(aracDegerleButton);
        if(isMobileView()) {
            click(categoryOtomobilMobile);
        } else {
            click(categoryOtomobil);
        }
    }

//    public void selectCategory(String categoryText) throws InterruptedException {
////        Thread.sleep(1000);
//        clickOnText(categoryText);
//    }

    public void selectCategory(String categoryText) {
        // Find the element
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[text()='" + categoryText + "']")));
        // Wait for element to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(element));

        try {
            // First try normal click
            element.click();
        } catch (Exception e) {
            // If failed, try JavaScript click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    public void selectKilometre(String km) {
        sendKeys(kmInput, km);
    }

    public void seeResults() {
        click(calculateButton);
        waitForPageLoad();
    }

    public void verifyValueRanges() {
        WebElement lower;
        WebElement mid;
        WebElement upper;


        if (isMobileView()) {
            click(tumFiyatAraliklariniGosterMobile);
            lower = wait.until(ExpectedConditions.visibilityOfElementLocated(lowerLimitMobile));
            mid = wait.until(ExpectedConditions.visibilityOfElementLocated(medianMobile));
            upper = wait.until(ExpectedConditions.visibilityOfElementLocated(upperLimitMobile));
        } else {
            lower = wait.until(ExpectedConditions.visibilityOfElementLocated(lowerLimit));
            mid = wait.until(ExpectedConditions.visibilityOfElementLocated(median));
            upper = wait.until(ExpectedConditions.visibilityOfElementLocated(upperLimit));
        }

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

        if (!isMobileView()){
            // Wait for new tab to open
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        }

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