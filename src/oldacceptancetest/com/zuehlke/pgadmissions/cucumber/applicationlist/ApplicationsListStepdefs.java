package com.zuehlke.pgadmissions.cucumber.applicationlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ApplicationsListStepdefs {

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
	private Map<Integer, String> applicationIdMap = new HashMap<Integer, String>();
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
			
			ApplicationForm applicationForm = createAndSaveApplicationForm(username, submissionStatus,projectCode, reviewerNames);
			
			applicationIdMap.put(applicationForm.getId(), idKey);

		}
	}

	@When("(\\w+) views the application list")
	public void viewApplicationListPage(String username) {
		loginAs(username);
		webDriver.get("http://localhost:8080/pgadmissions/applications");
	}

	@Then("she sees a list containing only applications$")
	public void userSeesListContainintOnly(DataTable table) {
		List<WebElement> htmlRows = webDriver.findElements(By.name("applicationRow"));
		Assert.assertEquals(table.getGherkinRows().size() - 1, htmlRows.size());

		List<List<String>> actualApplications = new ArrayList<List<String>>();
		List<String> actualRow = new ArrayList<String>();
		actualRow.add("ApplicationNumber");
		if (table.getGherkinRows().get(0).getCells().contains("SubmissionStatus")) {
			actualRow.add("SubmissionStatus");
		}
		actualApplications.add(actualRow);

		for (WebElement htmlRow : htmlRows) {
			actualRow = new ArrayList<String>();
			WebElement idElement = htmlRow.findElement(By.name("idColumn"));
			String expectedIdPlaceHolder = applicationIdMap.get(Integer.parseInt(idElement.getText()));
			actualRow.add(expectedIdPlaceHolder);
			if (table.getGherkinRows().get(0).getCells().contains("SubmissionStatus")) {
				WebElement submissionStatusElement = htmlRow.findElement(By.name("statusColumn"));
				actualRow.add(submissionStatusElement.getText());
			}
			actualApplications.add(actualRow);
		}

		table.diff(actualApplications);
	}

	@Then("she sees a list containing applications$")
	public void userSeesListContaining(DataTable table) {
		List<WebElement> htmlRows = webDriver.findElements(By.name("applicationRow"));

		List<List<String>> actualApplications = new ArrayList<List<String>>();
		List<String> actualRow = new ArrayList<String>();
		actualRow.add("ApplicationNumber");
		actualApplications.add(actualRow);

		for (WebElement htmlRow : htmlRows) {
			actualRow = new ArrayList<String>();
			WebElement idElement = htmlRow.findElement(By.name("idColumn"));
			String expectedIdPlaceHolder = applicationIdMap.get(Integer.parseInt(idElement.getText()));
			actualRow.add(expectedIdPlaceHolder);
			actualApplications.add(actualRow);

		}
		List<DataTableRow> gherkinRows = table.getGherkinRows();
		for (DataTableRow dataTableRow : gherkinRows) {
			Assert.assertTrue(contains(actualApplications, dataTableRow.getCells()));
		}
	}

	@Then("not containing applications$")
	public void notContainingApplications(DataTable table) {
		List<WebElement> htmlRows = webDriver.findElements(By.name("applicationRow"));

		List<List<String>> actualApplications = new ArrayList<List<String>>();
		List<String> actualRow = new ArrayList<String>();
		actualRow.add("ApplicationNumber");
		actualApplications.add(actualRow);

		for (WebElement htmlRow : htmlRows) {
			actualRow = new ArrayList<String>();
			WebElement idElement = htmlRow.findElement(By.name("idColumn"));
			String expectedIdPlaceHolder = applicationIdMap.get(Integer.parseInt(idElement.getText()));
			actualRow.add(expectedIdPlaceHolder);
			actualApplications.add(actualRow);

		}
		List<DataTableRow> gherkinRows = table.getGherkinRows();
		for (int i = 1; i < gherkinRows.size(); i++) {
			Assert.assertFalse(contains(actualApplications, gherkinRows.get(i).getCells()));

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

	
	
	private ApplicationForm createAndSaveApplicationForm(String username, SubmissionStatus submissionStatus, String projectCode, String... reviewers) {

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(userMap.get(username)).project(projectMap.get(projectCode)).submissionStatus(submissionStatus)
				.toApplicationForm();
		for (String revieweName : reviewers) {
			applicationForm.getReviewers().add(userMap.get(revieweName.trim()));
		}
		sessionFactory.getCurrentSession().save(applicationForm);
		commitAndGetNewTransaction();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			//ignore
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
	

		Role applicant = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("authorityEnum", Authority.APPLICANT)).uniqueResult();

		Role administrator = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("authorityEnum", Authority.ADMINISTRATOR)).uniqueResult();

		Role reviewer = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("authorityEnum", Authority.REVIEWER)).uniqueResult();
		
		Role approver = (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("authorityEnum", Authority.APPROVER)).uniqueResult();

		anna = new RegisteredUserBuilder().firstName("Anna").lastName("Cucumber").email("email@test.com").username("anna").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(applicant).toUser();
		sessionFactory.getCurrentSession().save(anna);
		userMap.put("anna", anna);

		bert = new RegisteredUserBuilder().firstName("Bert").lastName("Cucumber").email("email@test.com").username("bert").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(applicant).toUser();
		sessionFactory.getCurrentSession().save(bert);
		userMap.put("bert", bert);

		charles = new RegisteredUserBuilder().firstName("Charles").lastName("Cucumber").email("email@test.com").username("charles").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(administrator).toUser();
		sessionFactory.getCurrentSession().save(charles);
		userMap.put("charles", charles);

		dorotha = new RegisteredUserBuilder().firstName("Dorotha").lastName("Cucumber").email("email@test.com").username("dorotha").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(reviewer).toUser();
		sessionFactory.getCurrentSession().save(dorotha);
		userMap.put("dorotha", dorotha);

		elsie = new RegisteredUserBuilder().firstName("Elsie").lastName("Cucumber").email("email@test.com").username("elsie").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(reviewer).toUser();
		sessionFactory.getCurrentSession().save(elsie);
		userMap.put("elsie", elsie);
		
		foxy = new RegisteredUserBuilder().firstName("Foxy").lastName("Cucumber").email("email@test.com").username("foxy").password("password")
				.accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true).role(approver).toUser();
		sessionFactory.getCurrentSession().save(foxy);
		userMap.put("foxy", foxy);
		
		programOne = new Program().code("CUKEPROG1").description("Cucumber Test Program Description").title("Cucumber Test Program Title").approver(foxy).toProgram();
		sessionFactory.getCurrentSession().save(programOne);
		programMap.put(programOne.getCode(), programOne);
		
		programTwo = new Program().code("CUKEPROG2").description("Cucumber Test Program Description").title("Cucumber Test Program Title").toProgram();
		sessionFactory.getCurrentSession().save(programTwo);
		programMap.put(programTwo.getCode(), programTwo);
		
		projectOne = new ProjectBuilder().code("CUKEPROJ1").description("Cucumber Test Project Description").title("Cucumber Test Project Title").program(programOne)
				.toProject();
		sessionFactory.getCurrentSession().save(projectOne);
		projectMap.put(projectOne.getCode(), projectOne);
		
		
		projectTwo = new ProjectBuilder().code("CUKEPROJ2").description("Cucumber Test Project Description").title("Cucumber Test Project Title").program(programTwo)
				.toProject();
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
		Program prog = (Program) sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("code",code)).uniqueResult();
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
