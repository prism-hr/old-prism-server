package com.zuehlke.pgadmissions.cucumber.view;

import gherkin.formatter.model.DataTableRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Function;
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

public class ViewApplicationStepdefs {

	private static final int REVIEWERS_COLUMN_INDEX = 3;
	private static final int PROJECT_COLUMN_INDEX = 2;
	private static final int SUBMISSION_STATUS_COLUMN_INDEX = 1;
	private static final int ID_COLUMN_INDEX = 0;
	private final WebDriver webDriver = new HtmlUnitDriver();
	private SessionFactory sessionFactory;
	private Program programOne;
	private Project projectOne;
	private RegisteredUser anna;
	private RegisteredUser bert;

	private Map<String, RegisteredUser> userMap = new HashMap<String, RegisteredUser>();
	private Map<String, Integer> applicationIdMap = new HashMap<String, Integer>();
	private Map<String, Program> programMap = new HashMap<String, Program>();
	private Map<String, Project> projectMap = new HashMap<String, Project>();

	private Transaction transaction;
	private RegisteredUser charles;
	private RegisteredUser dorotha;
	private RegisteredUser elsie;
	private RegisteredUser foxy;
	private Program programTwo;
	private Project projectTwo;

	@Given("(\\w+) has applications$")
	public void userHasApplications(String username, DataTable table) {

		List<DataTableRow> rows = table.getGherkinRows();
		for (int i = 1; i < rows.size(); i++) {
			DataTableRow row = rows.get(i);
			String idKey = row.getCells().get(ID_COLUMN_INDEX);
			SubmissionStatus submissionStatus = SubmissionStatus.valueOf(row.getCells().get(SUBMISSION_STATUS_COLUMN_INDEX).toUpperCase());
			String projectCode = row.getCells().get(PROJECT_COLUMN_INDEX);

			String[] reviewerNames = new String[0];
			if (table.getGherkinRows().get(0).getCells().contains("Reviewers")) {
				reviewerNames = row.getCells().get(REVIEWERS_COLUMN_INDEX).split(",");
			}

			ApplicationForm applicationForm = createAndSaveApplicationForm(username, submissionStatus, projectCode, reviewerNames);

			applicationIdMap.put(idKey, applicationForm.getId());

		}
	}

	@When("(\\w+) views the application list")
	public void viewApplicationListPage(String username) {
		loginAs(username);
		webDriver.get("http://localhost:8080/pgadmissions/applications");
	}

	@Then("^she can (\\w+) the application (\\w+)$")
	public void she_can_take_action_for_the_application(String action, final String idKey) {
		WebElement selectElement = webDriver.findElement(By.name("app_[" + applicationIdMap.get(idKey) + "]"));
		Assert.assertNotNull(selectElement.findElement(By.xpath("option[@value='" + action + "']")));
	}

	@Then("^she gets a resource not found error$")
	public void she_gets_a_resource_not_found_error() {
		Assert.assertTrue(webDriver.getPageSource().contains("HTTP Status 404"));
	}

	@When("^(\\w+) opens view for application (\\w+)$")
	public void user_opens_view_for_application_id_(String username, String idKey) {
		loginAs(username);
		webDriver.get("http://localhost:8080/pgadmissions/application?id=" + applicationIdMap.get(idKey));
	}

	@Then("^she sees the view of application (\\w+)$")
	public void she_sees_application_id_(String idKey) {
		Assert.assertEquals(applicationIdMap.get(idKey).toString(), webDriver.findElement(ById.id("applicationNumber")).getAttribute("value"));
	}
	

	private ApplicationForm createAndSaveApplicationForm(String username, SubmissionStatus submissionStatus, String projectCode, String... reviewers) {

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(userMap.get(username)).project(projectMap.get(projectCode))
				.submissionStatus(submissionStatus).toApplicationForm();
		for (String revieweName : reviewers) {
			applicationForm.getReviewers().add(userMap.get(revieweName.trim()));
		}
		sessionFactory.getCurrentSession().saveUpdate(applicationForm);
		commitAndGetNewTransaction();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// ignore
		}
		return applicationForm;
	}

	private void loginAs(String username) {
		webDriver.get("http://localhost:8080/pgadmissions/login");
		WebElement usernameField = webDriver.findElement(By.name("j_username"));
		usernameField.sendKeys(String.valueOf(username));
		WebElement pass = webDriver.findElement(By.name("j_password"));
		pass.sendKeys(String.valueOf("password"));
		webDriver.findElement(By.name("commit")).submit();
	}

	@Before
	public void setup() {

		sessionFactory = (SessionFactory) new ClassPathXmlApplicationContext("testHibernateContext.xml").getBean("sessionFactory");
		transaction = sessionFactory.getCurrentSession().beginTransaction();
		cleanUp();
		commitAndGetNewTransaction();

		Role applicant = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("authorityEnum", Authority.APPLICANT))
				.uniqueResult();

		Role administrator = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class)
				.add(Restrictions.eq("authorityEnum", Authority.ADMINISTRATOR)).uniqueResult();

		Role reviewer = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("authorityEnum", Authority.REVIEWER))
				.uniqueResult();

		Role approver = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("authorityEnum", Authority.APPROVER))
				.uniqueResult();

		anna = new RegisteredUserBuilder().firstName("Anna").lastName("Cucumber").email("email@test.com").username("anna").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(applicant).toUser();
		sessionFactory.getCurrentSession().saveUpdate(anna);
		userMap.put("anna", anna);

		bert = new RegisteredUserBuilder().firstName("Bert").lastName("Cucumber").email("email@test.com").username("bert").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(applicant).toUser();
		sessionFactory.getCurrentSession().saveUpdate(bert);
		userMap.put("bert", bert);

		charles = new RegisteredUserBuilder().firstName("Charles").lastName("Cucumber").email("email@test.com").username("charles").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(administrator).toUser();
		sessionFactory.getCurrentSession().saveUpdate(charles);
		userMap.put("charles", charles);

		dorotha = new RegisteredUserBuilder().firstName("Dorotha").lastName("Cucumber").email("email@test.com").username("dorotha").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(reviewer).toUser();
		sessionFactory.getCurrentSession().saveUpdate(dorotha);
		userMap.put("dorotha", dorotha);

		elsie = new RegisteredUserBuilder().firstName("Elsie").lastName("Cucumber").email("email@test.com").username("elsie").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(reviewer).toUser();
		sessionFactory.getCurrentSession().saveUpdate(elsie);
		userMap.put("elsie", elsie);

		foxy = new RegisteredUserBuilder().firstName("Foxy").lastName("Cucumber").email("email@test.com").username("foxy").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(approver).toUser();
		sessionFactory.getCurrentSession().saveUpdate(foxy);
		userMap.put("foxy", foxy);

		programOne = new Program().code("CUKEPROG1").description("Cucumber Test Program Description").title("Cucumber Test Program Title")
				.approver(foxy).toProgram();
		sessionFactory.getCurrentSession().save(programOne);
		programMap.put(programOne.getCode(), programOne);

		programTwo = new Program().code("CUKEPROG2").description("Cucumber Test Program Description").title("Cucumber Test Program Title").toProgram();
		sessionFactory.getCurrentSession().save(programTwo);
		programMap.put(programTwo.getCode(), programTwo);

		projectOne = new ProjectBuilder().code("CUKEPROJ1").description("Cucumber Test Project Description").title("Cucumber Test Project Title")
				.program(programOne).toProject();
		sessionFactory.getCurrentSession().save(projectOne);
		projectMap.put(projectOne.getCode(), projectOne);

		projectTwo = new ProjectBuilder().code("CUKEPROJ2").description("Cucumber Test Project Description").title("Cucumber Test Project Title")
				.program(programTwo).toProject();
		sessionFactory.getCurrentSession().save(projectTwo);
		projectMap.put(projectTwo.getCode(), projectTwo);

		sessionFactory.getCurrentSession().flush();
		commitAndGetNewTransaction();

	}

	private void commitAndGetNewTransaction() {
		transaction.commit();
		transaction = sessionFactory.getCurrentSession().beginTransaction();
	}

	@After
	public void teardown() {
		cleanUp();
		transaction.commit();
		webDriver.close();

	}

	private void cleanUp() {
		deleteApplicationForms("anna");
		deleteApplicationForms("bert");

		deleteProjectIfExists("CUKEPROJ1");
		deleteProjectIfExists("CUKEPROJ2");
		deleteProgramIfExists("CUKEPROG1");
		deleteProgramIfExists("CUKEPROG2");

		deleteUserIfExists("anna");
		deleteUserIfExists("bert");
		deleteUserIfExists("charles");
		deleteUserIfExists("dorotha");
		deleteUserIfExists("elsie");
		deleteUserIfExists("foxy");

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

	private void deleteProgramIfExists(String code) {
		Program prog = (Program) sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("code", code)).uniqueResult();
		if (prog != null) {
			sessionFactory.getCurrentSession().delete(prog);
		}
		commitAndGetNewTransaction();
	}

	private void deleteProjectIfExists(String code) {
		Project proj = (Project) sessionFactory.getCurrentSession().createCriteria(Project.class).add(Restrictions.eq("code", code)).uniqueResult();
		if (proj != null) {
			sessionFactory.getCurrentSession().delete(proj);
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

}
