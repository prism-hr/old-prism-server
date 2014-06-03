package com.zuehlke.pgadmissions.cucumber;

import org.junit.Assert;

public class RejectApplicationAsAdminStepdefs {
	private final WebDriver webDriver = new HtmlUnitDriver();
	
	public RejectApplicationAsAdminStepdefs(){
	}

	@Given("^I am logged in as admin and on view applications page$")
	public void I_am_logged_in_as_admin_and_on_view_applications_page(){
		//webDriver.get("http://beacon.zuehlke.com/pgadmissions");
		webDriver.get("http://localhost:8080/pgadmissions");
		WebElement username = webDriver.findElement(By.name("j_username"));
		username.sendKeys("bob");
		WebElement pass = webDriver.findElement(By.name("j_password"));
		pass.sendKeys("password");
		webDriver.findElement(By.name("commit")).submit();
	}
	
	@When("^I reject a project$")
	public void I_reject_a_project(){
		webDriver.findElement(By.name("submit")).submit();
	}
	
	@Then("^I should see Your have successfully rejected the application as a message$")
	public void I_should_see_Your_have_successfully_rejected_the_application_as_a_message() {
		Assert.assertTrue(webDriver.getPageSource().contains("Your have successfully rejected the application"));
		webDriver.close();
	}

}
