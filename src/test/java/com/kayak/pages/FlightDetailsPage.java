package com.kayak.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FlightDetailsPage extends BasePage {

    private final By flightDates = By.xpath("//div[@class='c2x94-title']/span[2]");
    private final By passengerCount = By.cssSelector("div.Z8U5-description.Z8U5-mod-theme-default");
    private final By stopCount = By.cssSelector("div.c2x94-sub-title");
    private final By flightPrice = By.cssSelector("div.jnTP-display-price");


    public FlightDetailsPage(WebDriver driver) {
        super(driver);
    }

    public String[] getDates() {
        return driver.findElements(flightDates)
                .stream()
                .map(WebElement::getText)
                .toArray(String[]::new);
    }

    public int getTotalPassengerCount() {
        String passengerText = getText(passengerCount);
        // Extract and sum all passenger numbers
        return Integer.parseInt(passengerText.replaceAll("[^0-9]", ""));
    }

    public boolean hasOneStop() {
        String stops = getText(stopCount);
        return stops.contains("1 aktarma");
    }

    public String getPrice() {
        return getText(flightPrice);
    }

    public boolean verifyFlightDates(String expectedDepartDate, String expectedReturnDate) {
        String[] datesText = getDates();
        return datesText.length == 2 && datesText[0].contains(expectedDepartDate) && datesText[1].contains(expectedReturnDate);
    }

    public boolean isFlightDetailPageLoaded() {
        return isElementDisplayed(flightDates) && isElementDisplayed(passengerCount);
    }
}