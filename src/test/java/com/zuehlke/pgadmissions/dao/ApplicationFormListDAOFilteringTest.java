package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationFormListDAOFilteringTest extends AutomaticRollbackTestCase {

    private ApplicationFormListDAO applicationDAO;

    private RegisteredUser user;

    private Program program;

    private RegisteredUser adminUser;

    private ApplicationForm app1InApproval;

    private ApplicationForm app2InReview;

    private ApplicationForm app3InValidation;

    private ApplicationForm app4InApproved;

    private ApplicationForm app5InInterview;

    private ApplicationForm app6InReview;

    private Date submissionDate;

    private Date lastEditedDate;

    private Date searchTermDateForLastEdited;

    private Date searchTermDateForSubmission;

    @Before
    public void prepare() {
        applicationDAO = new ApplicationFormListDAO(sessionFactory);

        program = new ProgramBuilder().code("doesntexist").title("another title").build();

        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        adminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).build();

        lastEditedDate = new GregorianCalendar(2011, 4, 7, 19, 33).getTime();
        submissionDate = new GregorianCalendar(2011, 3, 7, 17, 28).getTime();

        searchTermDateForLastEdited = new GregorianCalendar(2011, 4, 9).getTime();
        searchTermDateForSubmission = new GregorianCalendar(2011, 3, 9).getTime();

        app1InApproval = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.APPROVAL).applicationNumber("app1")
                .submittedDate(DateUtils.addDays(submissionDate, 0)).lastUpdated(DateUtils.addDays(lastEditedDate, 5))
                .build();
        app2InReview = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.REVIEW).applicationNumber("app2")
                .submittedDate(DateUtils.addDays(submissionDate, 1)).lastUpdated(DateUtils.addDays(lastEditedDate, 4))
                .build();
        app3InValidation = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).applicationNumber("app3")
                .submittedDate(DateUtils.addDays(submissionDate, 2)).lastUpdated(DateUtils.addDays(lastEditedDate, 3))
                .build();
        app4InApproved = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.APPROVED).applicationNumber("app4")
                .submittedDate(DateUtils.addDays(submissionDate, 3)).lastUpdated(DateUtils.addDays(lastEditedDate, 2))
                .build();
        app5InInterview = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.INTERVIEW).applicationNumber("app5")
                .submittedDate(DateUtils.addDays(submissionDate, 4)).lastUpdated(DateUtils.addDays(lastEditedDate, 1))
                .build();
        app6InReview = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.REVIEW).applicationNumber("app6")
                .submittedDate(DateUtils.addDays(submissionDate, 5)).lastUpdated(DateUtils.addDays(lastEditedDate, 0))
                .build();

        save(program, user, adminUser, app1InApproval, app2InReview, app3InValidation, app4InApproved, app5InInterview, app6InReview);

        flushAndClearSession();
    }

    @Test
    public void shouldReturnAppsFilteredByNumber() {
        ApplicationForm otherApplicationForm = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.APPROVAL).applicationNumber("other1").build();
        save(otherApplicationForm);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(filter), SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, 50);

        assertContainsApplications(applications, app1InApproval, app2InReview, app3InValidation, app4InApproved, app5InInterview, app6InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByNotNumber() {
        ApplicationForm otherApplicationForm = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.APPROVAL).applicationNumber("other1").build();
        save(otherApplicationForm);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("app").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(filter), SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, 50);

        assertContainsApplications(applications, otherApplicationForm);
    }

    @Test
    public void shouldReturnAppsFilteredByNumberAndStatus() {
        ApplicationsFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();
        ApplicationsFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(nameFilter, statusFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app6InReview, app2InReview);
    }
    
    @Test
    public void shouldReturnAppsFilteredByNotNumberAndStatus() {
        ApplicationsFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("app2").build();
        ApplicationsFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(nameFilter, statusFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app6InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByStatusAndNumber() {
        ApplicationsFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();
        ApplicationsFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(nameFilter, statusFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app6InReview, app2InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByApplicantName() {
        RegisteredUser otherUser = new RegisteredUserBuilder().firstName("Franciszek").lastName("Pieczka")
                .email("franek@pieczka.com").username("franek").password("franek123").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm otherApplicationForm = new ApplicationFormBuilder().program(program).applicant(otherUser)
                .status(ApplicationFormStatus.APPROVAL).applicationNumber("other1").build();
        save(otherUser, otherApplicationForm);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("czka").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(filter), SortCategory.APPLICATION_STATUS, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, otherApplicationForm);
    }
    
    @Test
    public void shouldReturnAppsFilteredByNotApplicantName() {
        RegisteredUser otherUser = new RegisteredUserBuilder().firstName("Franciszek").lastName("Pieczka")
                .email("franek@pieczka.com").username("franek").password("franek123").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm otherApplicationForm = new ApplicationFormBuilder().program(program).applicant(otherUser)
                .status(ApplicationFormStatus.APPROVAL).applicationNumber("other1").build();
        save(otherUser, otherApplicationForm);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("czka").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(filter), SortCategory.APPLICATION_STATUS, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, otherApplicationForm);
    }

    @Test
    public void shouldReturnAppsFilteredByStatusAndLastUpdatedFromDate() {
        String lastEditedDatePlus2String = dateToString(searchTermDateForLastEdited);

        ApplicationsFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();
        ApplicationsFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchTerm(lastEditedDatePlus2String).searchPredicate(SearchPredicate.FROM_DATE).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(dateFilter, statusFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app2InReview);
    }
    
    @Test
    public void shouldReturnAppsFilteredByNotStatusAndLastUpdatedFromDate() {
        String lastEditedDatePlus2String = dateToString(searchTermDateForLastEdited);

        ApplicationsFilter notReviewFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("Review").build();
        ApplicationsFilter notApprovalFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("Approval").build();
        ApplicationsFilter notValidationFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("validation").build();
        ApplicationsFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchTerm(lastEditedDatePlus2String)
                .searchPredicate(SearchPredicate.FROM_DATE).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(dateFilter, notReviewFilter, notApprovalFilter, notValidationFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app4InApproved);
    }

    @Test
    public void shouldReturnAppsFilteredByToUpdatedDate() {
        String lastEditedDatePlus2String = dateToString(searchTermDateForLastEdited);

        ApplicationsFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchTerm(lastEditedDatePlus2String)
                .searchPredicate(SearchPredicate.TO_DATE).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(dateFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app6InReview, app5InInterview, app4InApproved);
    }

    @Test
    public void shouldReturnAppsFilteredByOnUpdatedDate() {
        String lastEditedDatePlus2String = dateToString(searchTermDateForLastEdited);

        ApplicationsFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE)
                .searchTerm(lastEditedDatePlus2String).searchPredicate(SearchPredicate.ON_DATE).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(dateFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app4InApproved);
    }

    @Test
    public void shouldReturnAppsFilteredByFromSubmissionDate() {
        String submissionDatePlus2String = dateToString(searchTermDateForSubmission);

        ApplicationsFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUBMISSION_DATE)
                .searchTerm(submissionDatePlus2String).searchPredicate(SearchPredicate.FROM_DATE).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(dateFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app6InReview, app5InInterview, app4InApproved, app3InValidation);
    }

    @Test
    public void shouldReturnAppsFilteredByToSubmissionDate() {
        String submissionDatePlus2String = dateToString(searchTermDateForSubmission);

        ApplicationsFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUBMISSION_DATE)
                .searchTerm(submissionDatePlus2String).searchPredicate(SearchPredicate.TO_DATE).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(dateFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app3InValidation, app2InReview, app1InApproval);
    }

    @Test
    public void shouldReturnAppsFilteredByOnSubmissionDate() {
        String submissionDatePlus2String = dateToString(searchTermDateForSubmission);

        ApplicationsFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUBMISSION_DATE).searchTerm(submissionDatePlus2String)
                .searchPredicate(SearchPredicate.ON_DATE).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(dateFilter), SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app3InValidation);
    }
    
    @Test
    public void shouldReturnAppsFilteredByEmptyNumber() {
        ApplicationsFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("")
                .searchPredicate(SearchPredicate.CONTAINING).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(dateFilter), SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, 50);

        assertContainsApplications(applications, app1InApproval, app2InReview, app3InValidation, app4InApproved, app5InInterview, app6InReview);
    }
    
    @Test
    public void shouldReturnAppsFilteredByApplicantFirstNameAndLastName() {
        RegisteredUser otherUser = new RegisteredUserBuilder().firstName("Franciszek").lastName("Pieczka")
                .email("franek@pieczka.com").username("franek").password("franek123").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm otherApplicationForm = new ApplicationFormBuilder().program(program).applicant(otherUser)
                .status(ApplicationFormStatus.APPROVAL).applicationNumber("other1").build();
        save(otherUser, otherApplicationForm);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("ciszek Pieczka").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(filter), SortCategory.APPLICATION_STATUS, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, otherApplicationForm);
    }
    
    @Test
    public void shouldReturnAppsFilteredByProgramName() {
        
        Program program = new ProgramBuilder().code("MMM2013").title("Research Degree: Electronic and Electrical Engineering").build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email12@test.com")
                .username("username12").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        RegisteredUser adminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email12@test.com")
                .username("username212").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).build();
        
        ApplicationForm app1InApproval = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.APPROVAL).applicationNumber("app112")
                .submittedDate(DateUtils.addDays(submissionDate, 0)).lastUpdated(DateUtils.addDays(lastEditedDate, 5))
                .build();
        
        save(program, user, adminUser, app1InApproval);

        flushAndClearSession();
        
        ApplicationsFilter programFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROGRAMME_NAME).searchTerm(program.getTitle()).searchPredicate(SearchPredicate.CONTAINING).build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(programFilter), SortCategory.PROGRAMME_NAME, SortOrder.ASCENDING, 1, 50);

        assertContainsApplications(applications, app1InApproval);
    }

    private String dateToString(Date date) {
        String formattedDate = ApplicationFormListDAO.USER_DATE_FORMAT.print(new DateTime(date));
        return formattedDate;
    }

    private static void assertContainsApplications(List<ApplicationForm> applications, ApplicationForm... expectedApplications) {
        assertEquals(expectedApplications.length, applications.size());
        for (int i = 0; i < expectedApplications.length; i++) {
            assertEquals(expectedApplications[i].getApplicationNumber(), applications.get(i).getApplicationNumber());
        }
    }

}
