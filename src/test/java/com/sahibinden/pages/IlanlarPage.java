package com.sahibinden.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class IlanlarPage extends BasePage {

    private final By ilanList = By.cssSelector(".searchResultsItem");
    private final By ilanListMobile = By.xpath("//li[contains(@class,'search-result-item')]//a[contains(@class,'search-classified-link')]//div[contains(@class,'item-price')]/span");
    private final By sortingDropdown = By.id("advancedSorting");
    private final By sortingDropdownMobile = By.id("sorting");
    private final By enDusukFiyatOption = By.xpath("//a[@title='Fiyata göre (Önce en düşük)']");
    private final By ilanFiyatlari = By.cssSelector("td.searchResultsPriceValue .classified-price-container span");
    private final By ilanFiyatlariMobile = By.cssSelector(".item-price span");
    private final By ilanIsimleri = By.cssSelector("a.classifiedTitle");
    private final By ilanIsimleriMobile = By.className("item-title");


    private final By ilkIlan = By.cssSelector("tr.searchResultsItem a[class*='classifiedTitle']:first-of-type");
    private final By ilanlarMobile = By.cssSelector("a.search-classified-link");

    private final By filters = By.className("facetedFilteredLink");
    private final By filterButtonMobile = By.className("search-filter");



    public IlanlarPage(WebDriver driver) {
        super(driver);
    }

    public boolean areIlansDisplayed() {
        waitForPageLoad();
        List<WebElement> ilanlar;
        if (isMobileView()){
            ilanlar = findElements(ilanListMobile);
        }
        else {
            ilanlar = findElements(ilanList);
        }
        return !ilanlar.isEmpty();
    }

    public void removeFiltersUntilIlansDisplayed() {
        int maxAttempts = 5;
        int attempts = 0;

        while (!areIlansDisplayed() && attempts < maxAttempts) {
            List<WebElement> removeButtons = findElements(filters);
            if (!removeButtons.isEmpty()) {
                removeButtons.get(0).click();
                waitForPageLoad();
            }
            attempts++;
        }
    }

    public void sortByLowestPrice() throws InterruptedException {
        if(!isMobileView()){
            click(sortingDropdown);
            waitForElementVisible(enDusukFiyatOption);
            Thread.sleep(1000);
            click(enDusukFiyatOption);
            Thread.sleep(1000);
            waitForPageLoad();
        }
        else {
            Select sortingSelect = new Select(driver.findElement(sortingDropdownMobile));
            sortingSelect.selectByValue("price_asc"); // Fiyata göre (Önce en düşük)
            Thread.sleep(1000);
            waitForPageLoad();
        }
    }

    public void openFilters() {
        if (isMobileView()) {
            click(filterButtonMobile);
        }
    }

    public boolean arePricesInAscendingOrder() {
        waitForPageLoad();
        List<WebElement> priceElements;
        if (!isMobileView()) {
            // Find all the elements that contain the prices
            priceElements = findElements(ilanFiyatlari);
        } else {
            priceElements = findElements(ilanFiyatlariMobile);
        }


        // List to store the numeric values of prices
        List<Double> prices = new ArrayList<>();

        // Extract price values from the elements
        for (WebElement priceElement : priceElements) {
            String priceText = priceElement.getText().trim().replace("TL", "").replace(".", "");
            try {
                double price = Double.parseDouble(priceText);
                prices.add(price);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing price: " + priceText);
                return false;  // Return false if any price value can't be parsed
            }
        }

        // Check if the list is in ascending order
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) {
                return false; // Prices are not in ascending order
            }
        }

        return true; // All prices are in ascending order
    }

    public String getFirstIlanTitle() {
        List<WebElement> ilanTitles;
        if(isMobileView()){
            ilanTitles = findElements(ilanIsimleriMobile);
        }
        else{
            ilanTitles = findElements(ilanIsimleri);
        }
        // Get the first <a> element
        WebElement firstClassifiedTitleElement = ilanTitles.get(0);

        return firstClassifiedTitleElement.getText();
    }

    public double getFirstIlanPrice() {
        List<WebElement> priceElements;
        if(isMobileView()){
            priceElements = findElements(ilanFiyatlariMobile);
        }
        else{
            priceElements = findElements(ilanFiyatlari);
        }
        // Get the first <a> element
        WebElement priceElement = priceElements.get(0);
        String priceText = priceElement.getText().trim().replace("TL", "").replace(".", "");
        double price = Double.parseDouble(priceText);

        return price;
    }

    public IlanDetayPage clickFirstIlan() {
        if(isMobileView()){
            click(ilanlarMobile);
        }
        else{
            click(ilkIlan);
        }
        waitForPageLoad();
        return new IlanDetayPage(driver);
    }
}