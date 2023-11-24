package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class MoneyTransferTest {

    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;

    DataHelper.CardInfo secondCardinfo;
    int firstCardBalance;
    int secondCarBalance;

    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = getAuthinfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = getFerstCardInfo();
        secondCardinfo = getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCarBalance = dashboardPage.getCardBalance(secondCardinfo);
    }

    @Test
    void shouldTransferFromFirstToSecond() {
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCarBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardinfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardinfo);
        assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }
    @Test
    void shouldGetErrorMassageIfAmountMoreBalance() {
        var amount = generateInvalidAmount(secondCarBalance);
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardinfo);
        transferPage.findErrorMassage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardinfo);
        assertEquals(firstCardBalance, actualBalanceFirstCard);
        assertEquals(secondCarBalance, actualBalanceSecondCard);
    }
}
