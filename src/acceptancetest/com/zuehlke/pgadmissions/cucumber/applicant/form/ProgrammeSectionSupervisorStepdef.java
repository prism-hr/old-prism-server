package com.zuehlke.pgadmissions.cucumber.applicant.form;

import gherkin.formatter.model.DataTableRow;

import java.util.List;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import com.zuehlke.pgadmissions.cucumber.WebDriverProvider;

import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;

public class ProgrammeSectionSupervisorStepdef {

	private WebDriver webDriver;

	public ProgrammeSectionSupervisorStepdef() {
		webDriver = WebDriverProvider.getInstance().getWebdriver();
	}

	@When("^I select the latest unsubmitted application applications list$")
	public void I_select_the_latest_unsubmitted_application_applications_list() {
		pause(2000);
		webDriver.findElement(By.xpath("//a[@class='proceed-link'][1]")).click();
	}

	@When("^I enter \"([^\"]*)\" into the supervisor firstname field$")
	public void I_enter_into_the_supervisor_firstname_field(String value) {
		webDriver.findElement(By.id("supervisorFirstname")).clear();
		webDriver.findElement(By.id("supervisorFirstname")).sendKeys(value);
	}

	@When("^I enter \"([^\"]*)\" into the supervisor lastname field$")
	public void I_enter_into_the_supervisor_lastname_field(String value) {
		webDriver.findElement(By.id("supervisorLastname")).clear();
		webDriver.findElement(By.id("supervisorLastname")).sendKeys(value);
	}

	@When("^I enter \"([^\"]*)\" into the supervisor email field$")
	public void I_enter_into_the_supervisor_email_field(String value) {
		webDriver.findElement(By.id("supervisorEmail")).clear();
		webDriver.findElement(By.id("supervisorEmail")).sendKeys(value);
	}

	@When("^I click the \"([^\"]*)\" button$")
	public void I_click_the_button(String button) {
		webDriver.findElement(By.id(button)).click();
	}

	@When("^I select \"([^\"]*)\" to is-supervisor-aware$")
	public void I_select_to_is_supervisor_aware(String value) {
		webDriver.findElement(By.xpath("//input[@type='radio' and @value='" + value.toUpperCase() + "']")).click();
	}

	@Then("^I see the following supervisor table$")
	public void I_see_the_following_supervisor_table(DataTable table) {
		List<DataTableRow> rows = table.getGherkinRows();
		for(int i = 1; i <=rows.size(); i++){
			String text = webDriver.findElement(By.xpath("//table[@id='supervisors']//tr[" + i + "]/td[1]")).getText(); 
			Assert.assertEquals(text.trim().toUpperCase(), rows.get(i-1).getCells().get(0).trim().toUpperCase());
		}
}

	@When("^I hover over \"([^\"]*)\" in the supervisor table$")
	public void I_hover_over_in_the_supervisor_table(int rownNumber) {
		webDriver.findElement(By.xpath("//table[@id='supervisors']//tr/td["+ rownNumber + "]")).click();
	
	}

	@Then("^I see message \"([^\"]*)\"$")
	public void I_see_message(String message) {
		String text = webDriver.findElement(By.tagName("body")).getText();
		Assert.assertTrue(message, text.contains(message));
	}

	@When("^I click the edit icon on row number (\\d+)$")
	public void I_click_the_edit_icon_on_row_number(int rowNumber) {
		webDriver.findElement(By.xpath("//table[@id='supervisors']//tr["+ rowNumber + "]/td/a[.='edit']")).click();
		
	}

	@Then("^I see \"([^\"]*)\" in the supervisor firstname field$")
	public void I_see_in_the_supervisor_firstname_field(String arg1) {
		Assert.assertEquals(arg1, 	webDriver.findElement(By.id("supervisorFirstname")).getText());
	}

	@Then("^I see \"([^\"]*)\" in the supervisor lastName field$")
	public void I_see_in_the_supervisor_lastName_field(String arg1) {
		Assert.assertEquals(arg1, 	webDriver.findElement(By.id("supervisorLastname")).getText());
	}

	@Then("^I see \"([^\"]*)\" in the supervisor email field$")
	public void I_see_in_the_supervisor_email_field(String arg1) {
		Assert.assertEquals(arg1, 	webDriver.findElement(By.id("supervisorEmail")).getText());
	}

	@When("^I click the delete icon on row number (\\d+)$")
	public void I_click_the_delete_icon_on_row_number(int rowNumber) {
		webDriver.findElement(By.xpath("//table[@id='supervisors']//tr["+ rowNumber + "]/td/a[.='delete']")).click();
	}

	private void pause(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

}
