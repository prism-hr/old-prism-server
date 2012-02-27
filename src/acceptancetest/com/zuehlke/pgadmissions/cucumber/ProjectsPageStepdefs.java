package com.zuehlke.pgadmissions.cucumber;

import junit.framework.Assert;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;


public class ProjectsPageStepdefs {
	private final WebDriver webDriver = new HtmlUnitDriver();

	public ProjectsPageStepdefs(){
	}
	
	@Given ("^I am on the projects page$")
	public void I_am_on_the_projects_page(){
//		webDriver.get("http://http://c12545-2.zuehlke.com:8080/pgadmissions/projects");
		webDriver.get("http://localhost:8080/pgadmissions/projects");
	}
	
	@Then("^I should see a list of projects with an (.+) button next to each one$")
	public void I_(String button){
		Assert.assertTrue(webDriver.getPageSource().contains("Code"));
		Assert.assertTrue(webDriver.getPageSource().contains(button));
		webDriver.close();
	}

}
