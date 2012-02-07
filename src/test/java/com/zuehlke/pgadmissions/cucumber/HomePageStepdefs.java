package com.zuehlke.pgadmissions.cucumber;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;

public class HomePageStepdefs {

	
	private final WebDriver webDriver = new HtmlUnitDriver();

	public HomePageStepdefs(){
	}
	
	@Given("^I am on the home page$")
	public void i_am_on_the_home_page(){
		webDriver.get("http://beacon.zuehlke.com/pgadmissions/");
	}
	
	@Then("^I should see \"([^\"]*)\" as a title$")
	public void I_should_see_a_title(String title){
		Assert.assertTrue(webDriver.getPageSource().contains(title));
		webDriver.close();
	}
}
