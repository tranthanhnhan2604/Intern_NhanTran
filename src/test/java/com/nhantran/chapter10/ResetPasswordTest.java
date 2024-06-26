package com.nhantran.chapter10;

import com.nhantran.base.TestBase;
import com.nhantran.enums.RailwayTabs;
import com.nhantran.models.User;
import com.nhantran.pages.*;
import com.nhantran.common.Constants;
import com.nhantran.common.Messages;
import com.nhantran.utils.controls.WindowControl;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ResetPasswordTest extends TestBase {
    private HomePage homePage = new HomePage();
    private LoginPage loginPage = new LoginPage();
    private MailboxPage mailboxPage = new MailboxPage();
    private ForgetPasswordPage forgetPasswordPage = new ForgetPasswordPage();
    private ResetPasswordPage resetPasswordPage = new ResetPasswordPage();

    private User validUser = User.getLoginAccountFromJsonFile("validAccount");

    @DataProvider(name = "resetPasswordData")
    public Object[][] dataTestTC010AndTC011() {
        return new Object[][]{
                {validUser.getEmail(), validUser.getPassword(), validUser.getPassword(), Messages.MSG_ERROR_NEW_PASSWORD_SAME_OLD_PASSWORD, null},
                {validUser.getEmail(), validUser.getPassword() + "1", validUser.getPassword() + "2", Messages.MSG_ERROR_ABOVE_RESET_PASSWORD_FORM, Messages.MSG_ERROR_CONFIRM_PASSWORD_NOT_MATCH_PASSWORD}
        };
    }

    @Test(dataProvider = "resetPasswordData", description = "Reset password shows error if the new password is same as current password or not matches the confirm password")
    public void TC010_011_ErrorMessageDisplayWhenNewPasswordSameAsOldPasswordOrNotMatchConfirmPassword(String username, String newPassword, String confirmPassword, String expectedMessageAboveForm, String expectedMessageNextToTextBox) {
        String railwayWindow = WindowControl.getWindowHandle();
        homePage.clickTab(RailwayTabs.LOGIN);
        loginPage.clickForgotPasswordLink();
        forgetPasswordPage.submitPasswordResetInstructionsForm(username);
        WindowControl.openSiteInNewTab(Constants.TEMPORARY_MAIL_URL);
        String mailWindow = WindowControl.getWindowHandle();
        mailboxPage.setMail(username);
        mailboxPage.clickResetPasswordMail();
        String resetToken = mailboxPage.getResetPasswordToken();
        mailboxPage.clickResetPasswordLinkInMail();
        WindowControl.switchToRemainingTab(railwayWindow, mailWindow);
        Assert.assertTrue(resetPasswordPage.isChangePasswordFormDisplayed());
        Assert.assertEquals(resetPasswordPage.getResetTokenInTextBox(), resetToken);
        resetPasswordPage.resetPassword(newPassword, confirmPassword);
        Assert.assertEquals(resetPasswordPage.getMessageAboveForm(), expectedMessageAboveForm);
        Assert.assertEquals(resetPasswordPage.getMessageNextToConfirmPassword(), expectedMessageNextToTextBox);
    }
}
