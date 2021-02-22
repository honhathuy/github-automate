package helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StablerActions {
	private WebDriverWait wait;
	private WebDriver driver;
	
	public StablerActions(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, 30);
	}
	
	public WebElement getElement(By selector) {
		wait.until(ExpectedConditions.elementToBeClickable(selector));
		
		return driver.findElement(selector);
	}
	
	public void click(WebElement element) {
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}
	
	public void click(By selector) {
		wait.until(ExpectedConditions.elementToBeClickable(selector));
		driver.findElement(selector).click();
	}
}
