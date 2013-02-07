package com.zuehlke.pgadmissions.cucumber.projects;

import gherkin.formatter.model.DataTableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

import cucumber.annotation.After;
import cucumber.annotation.Before;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;


public class ProjectsPageStepdefs {
	private final WebDriver webDriver = new HtmlUnitDriver(true);
	private SessionFactory sessionFactory;
	private Program program;
	private Transaction transaction;
	private RegisteredUser anna;
	private ApplicationForm application = new ApplicationForm();
	
	private Map<String, RegisteredUser> userMap = new HashMap<String, RegisteredUser>();
	private List<Project> projects = new ArrayList<Project>();
	
	public ProjectsPageStepdefs(){
	}
	
	@Before
	public void setup() {
		sessionFactory = (SessionFactory) new ClassPathXmlApplicationContext("testHibernateContext.xml").getBean("sessionFactory");
		transaction = sessionFactory.getCurrentSession().beginTransaction();
		cleanUp();
		commitAndGetNewTransaction();

		program = new ProgramBuilder().code("CUKEPROG").description("Cucumber Test Program Description").title("Cucumber Test Program Title").toProgram();
		sessionFactory.getCurrentSession().save(program);

		Role applicant = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("authorityEnum", Authority.APPLICANT))
				.uniqueResult();

		anna = new RegisteredUserBuilder().firstName("Anna").lastName("Cucumber").email("email@test.com").username("anna").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(applicant).toUser();
		sessionFactory.getCurrentSession().save(anna);
		userMap.put("anna", anna);

		sessionFactory.getCurrentSession().flush();
		commitAndGetNewTransaction();
	}
	
	@Given("^there is a list of projects$")
	public void thereIsAListOfProjects(DataTable table){
		List<DataTableRow> rows = table.getGherkinRows();
		for (int i = 1; i < rows.size(); i++) {
			DataTableRow row = rows.get(i);
			String code = row.getCells().get(0);
			String title = row.getCells().get(1);
			String description = row.getCells().get(2);
			Project project = createAndSaveProject(code, title, description);
			projects.add(project);
		}
	}
	
	@Given("user views project list and apply for the first one")
	public void viewProjectsAndApplyForOne(){
		webDriver.get("http://localhost:8080/pgadmissions/projects");
		WebElement applyBtn = webDriver.findElement(By.id(projects.get(0).getId()+""));
		applyBtn.click();
	}
	
	
	@When("^user is logged in as (\\w+)$")
	public void promptedToLogin(String username){
		String expectedUrl = "http://localhost:8080/pgadmissions/login";
		Assert.assertEquals(expectedUrl, webDriver.getCurrentUrl().substring(0, 40));
		loginAs(username);
	}
	
	private ApplicationForm laodApplication() {
		return (ApplicationForm) sessionFactory.getCurrentSession()
				.createCriteria(ApplicationForm.class)
				.add(Restrictions.eq("applicant", anna)).uniqueResult();
	}

	@When("^(\\w+) sees the application$")
	public void logIn(String username){
		Assert.assertTrue(webDriver.getPageSource().contains("CUKEPROG - Cucumber Test Program Title"));
		Assert.assertTrue(webDriver.getPageSource().contains("Application Number"));
	}
	
	@Then("^(\\w+) submits the application$")
	public void applicantSeesApplicationForm(String username){
		WebElement submitBtn = webDriver.findElement(By.id("submitButton"));
		submitBtn.click();
	}
	
	@Then("see the application as submitted in the application list$")
	public void viewApplicationForm(DataTable table){
		application = laodApplication();
		Assert.assertEquals("http://localhost:8080/pgadmissions/applications?success=true", webDriver.getCurrentUrl());
		List<WebElement> htmlRows = webDriver.findElements(By.name("applicationRow"));

		List<List<String>> actualApplications = new ArrayList<List<String>>();
		List<String> actualRow = new ArrayList<String>();
		actualRow.add("SubmissionStatus");
		actualRow.add("Project Code");
		actualApplications.add(actualRow);
		for (WebElement htmlRow : htmlRows) {
			actualRow = new ArrayList<String>();
			String expectedSubmission = application.getSubmissionStatus().name();
			String expectedIdPlaceHolder = application.getProject().getCode();
			actualRow.add(expectedSubmission);
			actualRow.add(expectedIdPlaceHolder);
			actualApplications.add(actualRow);

		}
		List<DataTableRow> gherkinRows = table.getGherkinRows();
		for (DataTableRow dataTableRow : gherkinRows) {
			System.out.println(dataTableRow.getCells());
			System.out.println(actualApplications);
			Assert.assertTrue(contains(actualApplications, dataTableRow.getCells()));
		}
	}
	
	private boolean contains(List<List<String>> actualApplications, List<String> expectedRow) {
		for (List<String> actualRow : actualApplications) {
			if (actualRow.size() == expectedRow.size()) {
				if (actualRow.containsAll(expectedRow)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void loginAs(String username) {
//		webDriver.get("http://localhost:8080/pgadmissions/login");
		WebElement usernameField = webDriver.findElement(By.name("j_username"));
		usernameField.sendKeys(String.valueOf(username));
		WebElement pass = webDriver.findElement(By.name("j_password"));
		pass.sendKeys(String.valueOf("password"));
		webDriver.findElement(By.name("commit")).submit();
	}
	
	private void commitAndGetNewTransaction() {
		transaction.commit();
		transaction = sessionFactory.getCurrentSession().beginTransaction();
	}
	
	private void cleanUp() {
		deleteApplicationForms("anna");
		deleteUserIfExists("anna");

		
		deleteProjectsIfExist();
		deleteProgramIfExists();

		sessionFactory.getCurrentSession().flush();

	}
	
	@SuppressWarnings("unchecked")
	private void deleteApplicationForms(String username) {
		List<ApplicationForm> forms = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).createCriteria("applicant")
				.add(Restrictions.eq("username", username)).list();
		for (ApplicationForm applicationForm : forms) {
			sessionFactory.getCurrentSession().delete(applicationForm);
		}
		commitAndGetNewTransaction();
	}
	
	private void deleteProgramIfExists() {
		Program prog = (Program) sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("code", "CUKEPROG")).uniqueResult();
		if (prog != null) {
			sessionFactory.getCurrentSession().delete(prog);
		}
		commitAndGetNewTransaction();
	}

	private void deleteProjectsIfExist() {
		@SuppressWarnings("unchecked")
		List<Project> testProjects = (List<Project>) sessionFactory.getCurrentSession().createCriteria(Project.class).add(Restrictions.eq("program", program)).list();
		for (Project project : testProjects) {
			System.out.println(project.getId());
			sessionFactory.getCurrentSession().delete(project);
		}
		commitAndGetNewTransaction();
	}

	private void deleteUserIfExists(String username) {
		RegisteredUser user = (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
				.add(Restrictions.eq("username", username)).uniqueResult();
		if (user != null) {
			sessionFactory.getCurrentSession().delete(user);
		}
		commitAndGetNewTransaction();
	}
	
	private ApplicationForm createAndSaveApplicationForm(String username, SubmissionStatus submissionStatus,  Project project, String... reviewers) {

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(userMap.get(username)).project(project).submissionStatus(submissionStatus)
				.toApplicationForm();
		for (String revieweName : reviewers) {
			applicationForm.getReviewers().add(userMap.get(revieweName.trim()));
		}
		sessionFactory.getCurrentSession().save(applicationForm);
		commitAndGetNewTransaction();
		return applicationForm;
	}
	
	private Project createAndSaveProject(String code, String title, String description) {
		
		Project project = new ProjectBuilder().title(title).description(description).code(code).program(program).toProject();
		sessionFactory.getCurrentSession().save(project);
		commitAndGetNewTransaction();
		return project;
	}
	
	@After
	public void teardown() {
		cleanUp();
		transaction.commit();
		webDriver.close();

	}

}
