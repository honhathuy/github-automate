package pageObjectModel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import helper.StablerActions;

public class GitHubPOM {
	private WebDriver driver;
	private StablerActions stablerActions;
	private GithubLoginPOM githubLoginPF;
	private By signinBtnSelector = By.linkText("Sign in");
	private By accountMenuDropdownSelector = By.xpath("//header/div[7]/details[1]/summary[1]/span[2]");
	
	public GitHubPOM(WebDriver driver) {
		this.driver = driver;
		stablerActions = new StablerActions(driver);
	}
	
	public boolean isCurrentPage() {
		if (driver.getCurrentUrl().equals("https://github.com/"))
			return true;
		return false;
	}
	
	public void clickSignInBtn() {
		stablerActions.click(signinBtnSelector);
	}
	
	public GithubLoginPOM signin(String username, String password) {
		stablerActions.click(signinBtnSelector);
		githubLoginPF = new GithubLoginPOM(driver);
		githubLoginPF.login(username, password);
		
		return githubLoginPF;
	}
	
	public void selectAccountMenu() {
		stablerActions.click(accountMenuDropdownSelector);
	}
	
	public String getUserNameFromAccountMenu() {
		selectAccountMenu();
		
		By usernameLocator = By.cssSelector("strong.css-truncate-target");
		String username = stablerActions.getElement(usernameLocator).getText(); 
		
		return username;
	}
}
