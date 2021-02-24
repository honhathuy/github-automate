package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.proxy.CaptureType;
import pageObjectModel.GitHubPOM;
import pageObjectModel.GithubLoginPOM;

public class LoginTest {
	private WebDriver driver;
	private GitHubPOM githubHomePage;
	private GithubLoginPOM githubLoginPage;
	private Properties account;
	private BrowserMobProxyServer proxy;
	private SoftAssert softAssert;
	private String username;
	private String password;
	private String wrongusername;
	private String wrongpassword;

	@BeforeClass
	public void setupTestData() throws IOException {
		account = new Properties();
		FileInputStream accountProperties = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\test\\java\\objectRepo\\Account.properties");
		account.load(accountProperties);
		username = account.getProperty("username");
		password = account.getProperty("password");
		wrongusername = account.getProperty("wrongUserName");
		wrongpassword = account.getProperty("wrongPassword");
		
		softAssert = new SoftAssert();
	}

	@BeforeMethod
	public void setup() {
		WebDriverManager.chromedriver().setup();
		
		// create proxy server
		proxy = new BrowserMobProxyServer();
		proxy.setTrustAllServers(true);
		proxy.start();

		Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

		try {
			String hostIp = Inet4Address.getLocalHost().getHostAddress();

			seleniumProxy.setHttpProxy(hostIp + ":" + proxy.getPort());
			seleniumProxy.setSslProxy(hostIp + ":" + proxy.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		// setup chromedriver options with proxy
		DesiredCapabilities seleniumCapabilities = new DesiredCapabilities();
		seleniumCapabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
		ChromeOptions options = new ChromeOptions();
		options.setAcceptInsecureCerts(true);
		options.merge(seleniumCapabilities);

		driver = new ChromeDriver(options);
		proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

		driver.manage().window().maximize();
		
		// create HAR
		proxy.newHar("github");

		driver.get("https://github.com/");
		
		// catch all api call
		List<HarEntry> entries = proxy.getHar().getLog().getEntries();
		for (HarEntry entry : entries) {
			System.out.println(entry.getRequest().getUrl());
		}

		githubHomePage = new GitHubPOM(driver);
	}

	@Test
	public void shouldLoginSuccessfully() {
		githubHomePage.clickSignInBtn();
		githubLoginPage = new GithubLoginPOM(driver);

		Assert.assertTrue(githubLoginPage.isCurrentPage(), "Unable to reach login page");

		githubLoginPage.login(username, password);

		Assert.assertTrue(githubHomePage.isCurrentPage(),
				"Unable to navigate back to home page when login successfully");

		String actualUserName = githubHomePage.getUserNameFromAccountMenu();

		softAssert.assertEquals(actualUserName, "dummy-account-11");
		
		List<HarEntry> entries = proxy.getHar().getLog().getEntries();

		for (HarEntry entry : entries) {
			List<HarNameValuePair> headers = entry.getRequest().getHeaders();
			System.out.println(headers.size());

			for (HarNameValuePair header : headers) {
				System.out.println(header.getName() + " : " + header.getValue());
			}
		}

	}

	@Test(enabled = false)
	public void shouldLoginFail() {
		githubHomePage.clickSignInBtn();
		githubLoginPage = new GithubLoginPOM(driver);

		Assert.assertTrue(githubLoginPage.isCurrentPage(), "Unable to reach login page");

		githubLoginPage.login(wrongusername, password);
		softAssert.assertTrue(githubLoginPage.loginFailMessageIsDisplay());
		githubLoginPage.dismissloginFailMessage();

		githubLoginPage.login(wrongusername, wrongpassword);
		softAssert.assertTrue(githubLoginPage.loginFailMessageIsDisplay());
		githubLoginPage.dismissloginFailMessage();

		githubLoginPage.login(wrongusername, wrongpassword);
		softAssert.assertTrue(githubLoginPage.loginFailMessageIsDisplay());
		githubLoginPage.dismissloginFailMessage();
	}

	@AfterMethod
	public void cleanup() {
		driver.close();
		proxy.stop();
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
