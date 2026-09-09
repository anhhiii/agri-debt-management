package com.manage.debt_management.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {
    private By emailInput = By.cssSelector("input[name='email']");
    private By passwordInput = By.cssSelector("input[name='password']");
    private By submitButton = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterEmail(String email) {
        type(emailInput, email);
    }

    public void enterPassword(String password) {
        type(passwordInput, password);
    }

    public void clickSubmit() {
        click(submitButton);
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSubmit();
    }

    public boolean checkEmailValidity() {
        WebElement element = waitForVisibility(emailInput);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript("return arguments[0].checkValidity();", element);
    }

    // Kiểm tra văn bản hiển thị trên màn hình (Toast / Alert) giống
    // cy.contains(...)
    public boolean isTextPresentOnPage(String text) {
        By pageText = By.xpath("//*[contains(text(),'" + text + "')]");
        try {
            return waitForVisibility(pageText).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Lấy dữ liệu LocalStorage key 'auth-storage'
    public String getAuthStorageFromLocalStorage() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return localStorage.getItem('auth-storage');");
    }

    // Đợi tối đa cho tới khi React gọi xong API login và ghi Token vào LocalStorage
    public String waitForAndGetAuthStorage() {
        return wait.until(d -> {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String data = (String) js.executeScript(
                        "return localStorage.getItem('auth-storage') || " +
                                "localStorage.getItem('token') || " +
                                "localStorage.getItem('accessToken') || " +
                                "localStorage.getItem('jwt') || " +
                                "(Object.keys(localStorage).length > 0 ? JSON.stringify(localStorage) : null);");
                if (data != null && !data.trim().isEmpty() && !data.equals("{}")) {
                    return data;
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        });
    }

    // Xóa sạch LocalStorage trước mỗi lần test (giống win.localStorage.clear())
    public void clearLocalStorage() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.localStorage.clear();");
        } catch (Exception ignored) {
        }
    }

}
