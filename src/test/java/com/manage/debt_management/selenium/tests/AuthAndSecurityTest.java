package com.manage.debt_management.selenium.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.manage.debt_management.selenium.base.BaseSeleniumTest;
import com.manage.debt_management.selenium.pages.LoginPage;
import com.manage.debt_management.selenium.pages.NavigationPage;

public class AuthAndSecurityTest extends BaseSeleniumTest {

    private LoginPage loginPage;
    private NavigationPage navPage;

    @BeforeMethod
    public void beforeEach() {
        loginPage = new LoginPage(driver);
        navPage = new NavigationPage(driver);

        // 1. Điều hướng tới trang /login trước
        driver.get(baseUrl + "/login");
        // 2. Xóa sạch localStorage của domain này trước mỗi lần test
        loginPage.clearLocalStorage();
    }

    // --- NHÓM 1: VALIDATION (4 TRƯỜNG HỢP) ---

    @Test(priority = 1, description = "Trường hợp 1: Để trống email hoặc mật khẩu")
    public void testCase1_EmptyEmailOrPassword() {
        loginPage.clickSubmit();
        boolean isValid = loginPage.checkEmailValidity();
        Assert.assertFalse(isValid, "Trường email để trống phải có checkValidity() == false!");
    }

    @Test(priority = 2, description = "Trường hợp 2: Email không tồn tại")
    public void testCase2_EmailNotFound() {
        loginPage.login("notfound@example.com", "123456");
        boolean isPresent = loginPage.isTextPresentOnPage("Email không tồn tại");
        Assert.assertTrue(isPresent, "Màn hình phải hiển thị thông báo 'Email không tồn tại'!");
    }

    @Test(priority = 3, description = "Trường hợp 3: Sai mật khẩu (Backend Validation)")
    public void testCase3_WrongPassword() {
        loginPage.login("admin@example.com", "wrongpass");
        boolean isPresent = loginPage.isTextPresentOnPage("Sai tên đăng nhập hoặc mật khẩu");
        Assert.assertTrue(isPresent, "Màn hình phải hiển thị thông báo 'Sai tên đăng nhập hoặc mật khẩu'!");
    }

    @Test(priority = 4, description = "Trường hợp 4: Đăng nhập đúng và chuyển hướng")
    public void testCase4_ValidLoginAndRedirect() {

        loginPage.login("admin@example.com", "123456");

        boolean isSuccessToastPresent = loginPage.isTextPresentOnPage("Đăng nhập thành công");
        Assert.assertTrue(isSuccessToastPresent, "Màn hình phải hiển thị 'Đăng nhập thành công'!");

        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, baseUrl + "/", "Phải chuyển hướng đến trang chủ '/'!");
    }

    // --- NHÓM 2: TOKEN & SESSION ---

    @Test(priority = 5, description = "Kiểm tra Token được lưu vào localStorage")
    public void testCase5_CheckTokenInLocalStorage() {
        loginPage.login("admin@example.com", "123456");
        loginPage.waitForUrlNotContains("/login");

        String authData = loginPage.waitForAndGetAuthStorage();
        Assert.assertNotNull(authData, "Dữ liệu Token trong LocalStorage không được null sau khi đăng nhập!");
    }

    // --- NHÓM 3: LOGOUT & SECURITY ---

    @Test(priority = 6, description = "Logout: Xóa sạch Token và chặn nút Back")
    public void testCase6_LogoutAndPreventBackButton() {
        // 1. Đăng nhập trước & chờ rời trang /login
        loginPage.login("admin@example.com", "123456");
        loginPage.waitForUrlNotContains("/login");

        // 2. Click nút Logout (hỗ trợ force click)
        navPage.clickLogout();

        // 3. Kiểm tra đã về trang login chưa
        loginPage.waitForUrlContains("/login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), "Sau khi bấm đăng xuất phải về trang /login!");

        // 4. Nhấn Back trình duyệt
        driver.navigate().back();

        if ("data:,".equals(driver.getCurrentUrl())) {
            driver.get(baseUrl + "/");
        }

        // 5. Kết quả mong muốn: Route Guard vẫn giữ/chuyển hướng về trang /login
        loginPage.waitForUrlContains("/login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "Sau khi nhấn Back trình duyệt vẫn phải ở trang /login!");
    }
}
