package com.zuehlke.pgadmissions.cucumber.applicant.form;

import gherkin.formatter.model.DataTableRow;

import java.util.List;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.zuehlke.pgadmissions.cucumber.WebDriverProvider;

import cucumber.annotation.After;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;


public class ProgrammeSectionBasicsStepdef {


	private  WebDriver webDriver;

	
	public ProgrammeSectionBasicsStepdef() {
		webDriver = WebDriverProvider.getInstance().getWebdriver();
	}
	@Given("^I log in as \"([^\"]*)\"$")
	public void I_log_in_as(String userName) {
		webDriver.get("http://pgadmissions-sit.zuehlke.com/pgadmissions/");	
		WebElement username = webDriver.findElement(By.name("j_username"));
		username.sendKeys(userName);
		WebElement pass = webDriver.findElement(By.name("j_password"));
		pass.sendKeys("password");
		webDriver.findElement(By.name("commit")).submit();

	}

	@When("^I select application \"([^\"]*)\" in the applications list$")
	public void I_select_application_in_the_applications_list(String applicationNumber) {
		pause(2000);
		webDriver.findElement(By.xpath("//tr[@id='row_" +  applicationNumber + "']//a[@class='proceed-link']")).click();
	}
	
		
	
	@When("^I save the programme section$")
	public void I_save_the_programme_section() {
	   webDriver.findElement(By.id("programmeSaveButton")).click();
	}
	
	@When("^I choose \"([^\"]*)\" as study option$")
	public void I_choose_as_study_option(String studyOption) {
	    webDriver.findElement(By.xpath("//select[@id='studyOption']/option[.='" + studyOption +"']")).click();
	}

	@When("^I choose \"([^\"]*)\" as the how-did-you-find-us-option$")
	public void I_choose_as_the_how_did_you_find_us_option(String referrer) {
	    webDriver.findElement(By.xpath("//select[@id='referrer']/option[.='" + referrer +"']")).click();
	}

	@When("^I choose \"([^\"]*)\" as start date$")
	public void I_choose_as_start_date(String date) {
		webDriver.findElement(By.id("startDate")).sendKeys("1");
		String[] parts = date.split("-");

		webDriver.findElement(By.xpath("//select[@class='ui-datepicker-year']/option[@value='" + parts[2] + "']")).click();
		webDriver.findElement(By.xpath("//select[@class='ui-datepicker-month']/option[.='" + parts[1] + "']")).click();
		webDriver.findElement(By.xpath("//a[.='" + parts[0] + "']")).click();
		
	}

	@Then("^the \"([^\"]*)\" section should collapse$")
	public void the_section_should_collapse(String section) {
		pause(2000);
	    Assert.assertEquals("display: none;", webDriver.findElement(By.xpath("//section[@id='" + section + "']/div")).getAttribute("style").trim());
	}

	@When("^I expand the \"([^\"]*)\" section$")
	public void I_expand_the_section(String section) {
		webDriver.findElement(By.xpath("//section[@id='" + section + "']/h2")).click();
	}

	@Then("^the study option field should have value \"([^\"]*)\"$")
	public void the_study_option_field_should_have_value(String value) {
	    Assert.assertEquals(value,  webDriver.findElement(By.xpath("//select[@id='studyOption']/option[@selected='selected']")).getText());
	}

	@Then("^the start date field should have value \"([^\"]*)\"$")
	public void the_start_date_field_should_have_value(String value) {
	    Assert.assertEquals(value,  webDriver.findElement(By.id("startDate")).getAttribute("value"));
	}

	@Then("^the how-did-you-find-us-option field should have value \"([^\"]*)\"$")
	public void the_how_did_you_find_us_option_field_should_have_value(String value) {
		  Assert.assertEquals(value,  webDriver.findElement(By.xpath("//select[@id='referrer']/option[@selected='selected']")).getText());
	}




	private void pause(long milliseconds){
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}
	
	public void tearDown(){
		webDriver.close();
	}
}
