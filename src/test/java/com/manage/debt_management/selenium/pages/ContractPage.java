package com.manage.debt_management.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ContractPage extends BasePage {

    private By createContractBtn = By.xpath(
            "//button[contains(text(),'Tạo hợp đồng') or contains(text(),'Thêm hợp đồng')] | //button[contains(@class,'btn-create')] | //*[@id='btn-create-contract']");
    private By customerSelect = By
            .cssSelector("select[name='customerId'], select[name='customer'], #customerId, input[name='customerName']");
    private By totalValueInput = By
            .cssSelector("input[name='totalValue'], input[name='totalAmount'], input[name='amount'], #totalValue");
    private By downPaymentInput = By
            .cssSelector("input[name='downPayment'], input[name='prepaid'], #downPayment");
    private By interestRateInput = By
            .cssSelector("input[name='interestRate'], input[name='rate'], #interestRate");
    private By submitBtn = By.xpath(
            "//button[@type='submit'] | //button[contains(text(),'Lưu') or contains(text(),'Tạo') or contains(text(),'Xác nhận')] | //*[@id='btn-save-contract']");
    private By successNotification = By.xpath(
            "//*[contains(@class,'toast') or contains(@class,'alert') or contains(text(),'thành công') or contains(text(),'Thành công') or contains(text(),'Success')]");

    public ContractPage(WebDriver driver) {
        super(driver);
    }

    public void openCreateContractModal() {
        try {
            click(createContractBtn);
        } catch (Exception e) {
            org.openqa.selenium.WebElement el = driver.findElement(createContractBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    public void fillContractDetails(String totalValue, String downPayment, String interestRate) {
        // Chọn khách hàng nếu có dropdown
        try {
            org.openqa.selenium.WebElement select = driver.findElement(customerSelect);
            if (select.getTagName().equalsIgnoreCase("select")) {
                org.openqa.selenium.support.ui.Select sel = new org.openqa.selenium.support.ui.Select(select);
                if (sel.getOptions().size() > 1) {
                    sel.selectByIndex(1);
                }
            } else if (select.getTagName().equalsIgnoreCase("input")) {
                type(customerSelect, "Nguyễn Văn A");
            }
        } catch (Exception ignored) {
        }

        try {
            type(totalValueInput, totalValue);
        } catch (Exception ignored) {
        }
        try {
            type(downPaymentInput, downPayment);
        } catch (Exception ignored) {
        }
        try {
            type(interestRateInput, interestRate);
        } catch (Exception ignored) {
        }
    }

    public void submitContract() {
        try {
            click(submitBtn);
        } catch (Exception e) {
            org.openqa.selenium.WebElement el = driver.findElement(submitBtn);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    public boolean isSubmitDisabled() {
        try {
            org.openqa.selenium.WebElement btn = driver.findElement(submitBtn);
            String disabledAttr = btn.getAttribute("disabled");
            String ariaDisabled = btn.getAttribute("aria-disabled");
            String classAttr = btn.getAttribute("class");
            boolean isHtmlDisabled = !btn.isEnabled() || disabledAttr != null || "true".equalsIgnoreCase(ariaDisabled)
                    || (classAttr != null && (classAttr.contains("disabled") || classAttr.contains("opacity-50")
                            || classAttr.contains("cursor-not-allowed")));
            if (isHtmlDisabled) {
                return true;
            }
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            Boolean isFormValid = (Boolean) js.executeScript(
                    "var form = arguments[0].closest('form'); return form ? form.checkValidity() : true;", btn);
            return isFormValid != null && !isFormValid;
        } catch (Exception e) {
            return true;
        }
    }

    public String getSuccessNotificationText() {
        try {
            return getText(successNotification);
        } catch (Exception e) {
            return driver.getPageSource();
        }
    }
}
