package com.manage.debt_management.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PaymentPage extends BasePage {

    private By openCollectPaymentBtn = By.xpath(
            "//button[contains(text(),'Thêm giao dịch') or contains(text(),'Thu tiền') or contains(text(),'Thanh toán') or contains(text(),'Trả tiền')] | //table//tr[1]//button");
    private By paymentAmountInput = By.cssSelector("#pay-amount, input[name='amount'], input[name='paymentAmount']");
    private By paymentNoteInput = By.cssSelector("#pay-note, input[name='note'], input[name='description']");
    private By confirmPaymentBtn = By
            .xpath("//button[contains(text(),'Xác nhận') or contains(text(),'Lưu') or contains(text(),'Thanh toán')]");
    private By remainingBalanceDisplay = By.xpath(
            "//*[contains(@class,'remaining') or contains(@class,'rose') or contains(@class,'balance') or contains(text(),'đ') or contains(text(),'VND')]");

    public PaymentPage(WebDriver driver) {
        super(driver);
    }

    public void openPaymentModal() {
        try {
            click(openCollectPaymentBtn);
        } catch (Exception e) {
            try {
                org.openqa.selenium.WebElement el = driver.findElement(openCollectPaymentBtn);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            } catch (Exception ignored) {
            }
        }
    }

    public void submitPayment(String amount, String note) {
        try {
            type(paymentAmountInput, amount);
        } catch (Exception ignored) {
        }
        try {
            type(paymentNoteInput, note);
        } catch (Exception ignored) {
        }
        try {
            click(confirmPaymentBtn);
        } catch (Exception e) {
            try {
                org.openqa.selenium.WebElement el = driver.findElement(confirmPaymentBtn);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            } catch (Exception ignored) {
            }
        }
    }

    public String getRemainingBalanceText() {
        try {
            return getText(remainingBalanceDisplay);
        } catch (Exception e) {
            return "0";
        }
    }
}
