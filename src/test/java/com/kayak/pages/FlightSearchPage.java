package com.kayak.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FlightSearchPage extends BasePage {

    private final By departureField = By.cssSelector("input[aria-label='Uçuşun kalkış noktası']");
    private final By departureInput = By.cssSelector("input[aria-label='Uçuşun kalkış noktası']");
    private final By removeDefaultDepartureButton = By.cssSelector("div[aria-label='Remove value']");
    private final By departureAirportList = By.id("flight-origin-smarty-input-list");
    private final By destinationAirportList = By.id("flight-destination-smarty-input-list");
    private final By destinationField = By.cssSelector("input[aria-label='Uçuşun varış noktası']");
    private final By departureDateButton = By.cssSelector("div[role='button'][aria-label='Kalkış']");
    private final By adultIncreaseButton = By.xpath("(//button[@aria-label='Artan'])[1]");
    private final By studentIncreaseButton = By.xpath("(//button[@aria-label='Artan'])[2]");
    private final By childIncreaseButton = By.xpath("(//button[@aria-label='Artan'])[4]");
    private final By searchButton = By.xpath("//button[@aria-label='Ara']");


    public FlightSearchPage(WebDriver driver) {
        super(driver);
    }

    public void enterOriginAirport(String origin) {
        click(removeDefaultDepartureButton);
        click(departureField);
        sendKeys(departureInput, origin);
        waitForElementVisible(departureAirportList);
        sendEnter();
    }

    public void enterDestinationAirport(String destination) {
        click(destinationField);
        sendKeys(destinationField, destination);
        waitForElementVisible(destinationAirportList);
        sendEnter();
    }

    public void selectDates(String departDate, String returnDate) {
        click(departureDateButton);

        // Select departure date
        By departDateLocator = By.xpath("//div[contains(@aria-label, '" + departDate + "')]");
        click(departDateLocator);

        // Select return date
        By returnDateLocator = By.xpath("//div[contains(@aria-label, '" + returnDate + "')]");
        click(returnDateLocator);
    }

    public void selectPassengers(int adults, int students, int children) {
        for (int i = 1; i < adults; i++) {
            click(adultIncreaseButton);
        }

        for (int i = 0; i < students; i++) {
            click(studentIncreaseButton);
        }

        for (int i = 0; i < children; i++) {
            click(childIncreaseButton);
        }
    }

    private void setNumberInputValue(WebElement input, int value) {
        // Clear existing value first
        input.clear();

        // Set the input to the desired value
        while (Integer.parseInt(input.getAttribute("value")) < value) {
            By increaseButton = By.xpath("./..//button[contains(@class, 'increment')]");
            input.findElement(increaseButton).click();
        }

        while (Integer.parseInt(input.getAttribute("value")) > value) {
            By decreaseButton = By.xpath("./..//button[contains(@class, 'decrement')]");
            input.findElement(decreaseButton).click();
        }
    }

    public FlightResultsPage searchFlights() {
        click(searchButton);
        waitForPageLoad();
        return new FlightResultsPage(driver);
    }
}