package com.manage.debt_management.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NavigationPage extends BasePage {

    private By logoutButton = By.xpath("//button[contains(text(),'Đăng xuất')] | //a[contains(text(),'Đăng xuất')]");

    public NavigationPage(WebDriver driver) {
        super(driver);
    }

    public void clickLogout() {
        try {
            click(logoutButton);
        } catch (Exception e) {
            org.openqa.selenium.WebElement element = driver.findElement(logoutButton);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}
