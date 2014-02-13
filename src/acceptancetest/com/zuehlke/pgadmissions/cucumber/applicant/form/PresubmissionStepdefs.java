package com.zuehlke.pgadmissions.cucumber.applicant.form;
import gherkin.formatter.model.DataTableRow;

import java.util.List;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.zuehlke.pgadmissions.cucumber.WebDriverProvider;

import cucumber.annotation.After;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;

public class PresubmissionStepdefs {

	private final WebDriver webDriver;
	private String applicationNumber;
	
	public PresubmissionStepdefs() {
		
		webDriver = WebDriverProvider.getInstance().getWebdriver();
	}


	@Given("^I create an application to program \"([^\"]*)\" as \"([^\"]*)\"$")
	public void I_create_an_application_to_program_as(String programCode, String userName) throws InterruptedException {
		webDriver.get("http://pgadmissions-sit.zuehlke.com/pgadmissions/programs");
		webDriver.findElement(By.xpath("//button[@id='" + programCode + "']")).click();			
		WebElement username = webDriver.findElement(By.name("j_username"));
		username.sendKeys(userName);
		WebElement pass = webDriver.findElement(By.name("j_password"));
		pass.sendKeys("password");
		webDriver.findElement(By.name("commit")).submit();
		applicationNumber = webDriver.findElement(By.id("applicationId")).getText();
	}

	@When("^I submit the application$")
	public void I_submit_the_application() {
		webDriver.findElement(By.id("submitAppButton")).click();
	}



	@Then("^I see the following validation messages$")
	public void I_see_the_following_validation_messages_( DataTable table) {
		List<DataTableRow> gherkinRows = table.getGherkinRows();
		for (DataTableRow dataTableRow : gherkinRows) {
			String message = dataTableRow.getCells().get(0);
			String text = webDriver.findElement(By.tagName("body")).getText();
			Assert.assertTrue(message, text.contains(message));
		}
	}


	@Then("^the terms and conditions field is red$")
	public void the_terms_and_conditions_field_is_red() {
		Assert.assertEquals("border-color: red;", webDriver.findElement(By.xpath("//section[@id='acceptTermsSection']/form/div[@class='row-group']")).getAttribute("style").trim());
	}
	@Given("^the above$")
	public void the_above() {
	
	}

	@When("^I click the terms and conditions field$")
	public void I_click_the_terms_and_conditions_field() {
	   webDriver.findElement(By.id("acceptTermsCB")).click();
	}



}
