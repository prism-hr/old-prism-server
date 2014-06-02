package com.zuehlke.pgadmissions.cucumber;

import org.junit.Assert;

public class ApplyForAProjectStepdefs {
	private final WebDriver webDriver = new HtmlUnitDriver();

	public ApplyForAProjectStepdefs(){
	}
	
	@Given("^I am on projects page$")
	public void I_am_on_projects_page(){
		webDriver.get("http://localhost:8080/pgadmissions/projects");
//		webDriver.get("http://c12545-2.zuehlke.com:8080/pgadmissions/projects");
	}
	
	@When("^I click on Apply Now button for project KLM and log in as fred$")
	public void I_click_on_Apply_Now_button_for_project_KLM_and_log_in_as_fred() throws InterruptedException {
		webDriver.findElement(By.id("45")).click();
		long timeout = 2;		
		System.out.println(webDriver.getPageSource());
		WebElement username = webDriver.findElement(By.name("j_username"));
		username.sendKeys("fred");
		WebElement pass = webDriver.findElement(By.name("j_password"));
		pass.sendKeys("password");
		webDriver.findElement(By.name("commit")).submit();
	}
	@Then("^I should see my application form$")
	public void I_should_see_my_application_form(){
		System.out.println(webDriver.getPageSource());
		Assert.assertTrue(webDriver.getPageSource().contains("Application Number:"));
		System.out.println(webDriver.getPageSource());
		webDriver.findElement(By.name("submit")).submit();
		webDriver.close();
	}
	
}
