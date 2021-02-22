package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import pageObjectModel.GitHubPOM;
import pageObjectModel.GithubLoginPOM;

public class LoginTest {
	private WebDriver driver;
	private GitHubPOM githubHomePage;
	private GithubLoginPOM githubLoginPage;
	private Properties account;
	
	@BeforeClass
	public void setupTestData() throws IOException {
		account = new Properties();
		FileInputStream accountProperties = new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\java\\objectRepo\\Account.properties");
		account.load(accountProperties);
	}
	
	@BeforeMethod
	public void setup() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get("https://github.com/");
		githubHomePage = new GitHubPOM(driver);
	}
	
	@Test
	public void shouldLoginSuccessfully() {
//		githubLoginPage = githubHomePage.signin(account.getProperty("username"), account.getProperty("password"));
		githubHomePage.clickSignInBtn();
		githubLoginPage = new GithubLoginPOM(driver);
		
		Assert.assertTrue(githubLoginPage.isCurrentPage(), "Unable to reach login page");
		
		githubLoginPage.login(account.getProperty("username"), account.getProperty("password"));
		
		Assert.assertTrue(githubHomePage.isCurrentPage(), "Unable to navigate back to home page when login successfully");
		
		String actualUserName = githubHomePage.getUserNameFromAccountMenu();
		
		Assert.assertEquals(actualUserName, "dummy-account-11");
	}
	
	@Test
	public void shouldLoginFail() {
		githubHomePage.clickSignInBtn();
		githubLoginPage = new GithubLoginPOM(driver);
		
		Assert.assertTrue(githubLoginPage.isCurrentPage(), "Unable to reach login page");
		
		githubLoginPage.login(account.getProperty("wrongUserName"), account.getProperty("password"));
		Assert.assertTrue(githubLoginPage.loginFailMessageIsDisplay());
		githubLoginPage.dismissloginFailMessage();
		
		githubLoginPage.login(account.getProperty("wrongUserName"), account.getProperty("wrongPassword"));
		Assert.assertTrue(githubLoginPage.loginFailMessageIsDisplay());
		githubLoginPage.dismissloginFailMessage();
		
		githubLoginPage.login(account.getProperty("wrongUserName"), account.getProperty("wrongPassword"));
		Assert.assertTrue(githubLoginPage.loginFailMessageIsDisplay());
		githubLoginPage.dismissloginFailMessage();
	}
	
	@AfterMethod
	public void cleanup() {
		driver.close();
	}
	
	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
