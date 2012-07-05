package com.zuehlke.pgadmissions.cucumber;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;

public class FailureLoginPageStepdefs {

	
	private final WebDriver webDriver = new HtmlUnitDriver();

	public FailureLoginPageStepdefs(){
	}
	
	@Given("^I am on the failed login page$")
	public void I_am_on_the_failed_login_page(){
		//webDriver.get("http://beacon.zuehlke.com/pgadmissions");
		webDriver.get("http://localhost:8080/pgadmissions");
	}
	
	@When("^I enter user (.+) and password (.+) and submit unsuccessfully$")
	public void I_enter_user_and_password_and_submit_unsuccessfully(String user, String password){
		WebElement username = webDriver.findElement(By.name("j_username"));
		username.sendKeys(String.valueOf(user));
		WebElement pass = webDriver.findElement(By.name("j_password"));
		pass.sendKeys(String.valueOf(password));
		webDriver.findElement(By.name("commit")).submit();
	}
	
	@Then("^I should see (.+) as a failed title$")
	public void I_should_see_HTTP_Status_403_as_a_failed_title(String title){
		Assert.assertTrue(webDriver.getPageSource().contains(title));
		webDriver.close();
	}
}
