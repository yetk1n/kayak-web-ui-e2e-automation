package com.sahibinden.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class IlanlarPage extends BasePage {

    private final By ilanList = By.cssSelector(".searchResultsItem");
    private final By sortingDropdown = By.id("advancedSorting");
    private final By enDusukFiyatOption = By.xpath("//a[@title='Fiyata göre (Önce en düşük)']");
    private final By ilanFiyatlari = By.cssSelector("td.searchResultsPriceValue .classified-price-container span");
    private final By ilanIsimleri = By.cssSelector("a.classifiedTitle");
    private final By ilkIlan = By.cssSelector("tr.searchResultsItem a[class*='classifiedTitle']:first-of-type");
    private final By filters = By.className("facetedFilteredLink");

    public IlanlarPage(WebDriver driver) {
        super(driver);
    }

    public boolean areIlansDisplayed() {
        waitForPageLoad();
        List<WebElement> ilanlar = findElements(ilanList);
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
        click(sortingDropdown);
        waitForElementVisible(enDusukFiyatOption);
        Thread.sleep(1000);
        click(enDusukFiyatOption);
        Thread.sleep(1000);
        waitForPageLoad();
    }

    public boolean arePricesInAscendingOrder() {
        waitForPageLoad();
        // Find all the elements that contain the prices
        List<WebElement> priceElements = findElements(ilanFiyatlari);

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
        List<WebElement> ilanTitles = findElements(ilanIsimleri);
        // Get the first <a> element
        WebElement firstClassifiedTitleElement = ilanTitles.get(0);

        // Extract the title attribute
        String title = firstClassifiedTitleElement.getAttribute("title");

        return title;
    }

    public double getFirstIlanPrice() {
        List<WebElement> priceElements = findElements(ilanFiyatlari);
        // Get the first <a> element
        WebElement priceElement = priceElements.get(0);
        String priceText = priceElement.getText().trim().replace("TL", "").replace(".", "");
        double price = Double.parseDouble(priceText);

        return price;
    }

    public IlanDetayPage clickFirstIlan() {
        click(ilkIlan);
        waitForPageLoad();
        return new IlanDetayPage(driver);
    }
}