package pageObjectModel;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import helper.StablerActions;

public class GithubLoginPOM {
	private WebDriver driver;
	private StablerActions stablerActions;
	private By usernameTextBoxSelector = By.id("login_field");
	private By passwordTextBoxSelector = By.id("password");
	private By signinBtnSelector = By.name("commit");
	private By loginFailMessageSelector = By.cssSelector("div.flash-error");
	
	public GithubLoginPOM(WebDriver driver) {
		this.driver = driver;
		this.stablerActions = new StablerActions(driver);
	}
	
	public boolean isCurrentPage() {
		if (driver.getCurrentUrl().equals("https://github.com/login") || driver.getCurrentUrl().equals("https://github.com/session"))
			return true;
		
		return false;
	}
	
	public void setUserName(String username) {
		WebElement usernameTextBox = stablerActions.getElement(usernameTextBoxSelector);
		
		usernameTextBox.clear();
		usernameTextBox.sendKeys(username);
		System.out.println(usernameTextBox.getAttribute("value"));
	}
	
	public void setPassword(String password) {
		WebElement passwordTextBox = stablerActions.getElement(passwordTextBoxSelector);
		
		passwordTextBox.clear();
		passwordTextBox.sendKeys(password);
		System.out.println(passwordTextBox.getAttribute("value"));
	}
	
	public void submit() {
		stablerActions.click(signinBtnSelector);
	}
	
	public void login(String username, String password) {
		setUserName(username);
		setPassword(password);
		submit();
	}
	
	public boolean loginFailMessageIsDisplay() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 2);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(loginFailMessageSelector, "Incorrect username or password."));
			
			return true;
		} catch(TimeoutException e) {
			return false;
		}
	}
	
	public void dismissloginFailMessage() {
		By dismissBtn = By.cssSelector("svg.octicon.octicon-x");
		stablerActions.click(dismissBtn);
	}
}
