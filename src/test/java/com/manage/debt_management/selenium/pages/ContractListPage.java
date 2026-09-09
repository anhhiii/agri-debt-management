package com.manage.debt_management.selenium.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ContractListPage extends BasePage {

    private By searchInput = By.cssSelector("input[placeholder*='Tìm'], input[type='search'], input[name='search']");
    private By tableRows = By.cssSelector("table tbody tr");
    private By deleteButton = By.xpath(
            "//button[contains(text(),'Xóa')] | //a[contains(text(),'Xóa')] | //button[contains(@class,'delete')] | //button[contains(@class,'red')] | //button[contains(@class,'rose')] | //*[contains(@data-testid,'delete')] | //button[contains(@title,'Xóa')]");
    private By nextPaginationBtn = By.xpath("//button[contains(text(),'Sau') or contains(text(),'Next')]");
    private By prevPaginationBtn = By.xpath("//button[contains(text(),'Trước') or contains(text(),'Prev')]");

    public ContractListPage(WebDriver driver) {
        super(driver);
    }

    public void searchCustomer(String keyword) {
        try {
            type(searchInput, keyword);
        } catch (Exception ignored) {
        }
    }

    public void filterByStatus(String statusLabel) {
        try {
            By filterBtn = By.xpath("//button[contains(text(),'" + statusLabel + "')] | //span[contains(text(),'"
                    + statusLabel + "')]");
            click(filterBtn);
        } catch (Exception ignored) {
        }
    }

    public int getTableRowCount() {
        List<WebElement> rows = driver.findElements(tableRows);
        return rows.size();
    }

    public boolean isDeleteButtonVisible() {
        try {
            List<WebElement> buttons = driver.findElements(deleteButton);
            for (WebElement btn : buttons) {
                if (btn.isDisplayed()) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean isNextPaginationAvailable() {
        List<WebElement> btns = driver.findElements(nextPaginationBtn);
        return !btns.isEmpty() && btns.get(0).isDisplayed();
    }

    public void clickNextPage() {
        click(nextPaginationBtn);
    }

    public void clickPrevPage() {
        click(prevPaginationBtn);
    }

}
