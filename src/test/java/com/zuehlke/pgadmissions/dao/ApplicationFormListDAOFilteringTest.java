package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationFilter;
import com.zuehlke.pgadmissions.domain.ApplicationFilterGroup;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;

public class ApplicationFormListDAOFilteringTest extends AutomaticRollbackTestCase {

    private ApplicationFormListDAO applicationDAO;

    private UserDAO userDAO;

    private User applicant;

    private Program program;

    private User currentUser;

    private Application app1InApproval;

    private Application app2InReview;

    private Application app3InValidation;

    private Application app4InApproved;

    private Application app5InInterview;

    private Application app6InReview;

    private DateTime submissionDate;

    private DateTime lastEditedDate;

    private LocalDate searchTermDateForLastEdited;

    private LocalDate searchTermDateForSubmission;

    private Role role;

    @Before
    public void prepare() {
        userDAO = new UserDAO(sessionFactory);
        applicationDAO = new ApplicationFormListDAO(sessionFactory, userDAO);
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        role = roleDAO.getById(Authority.APPLICATION_INTERVIEWER);

        applicant = new User()
                //
                .withFirstName("Jane")
                //
                .withLastName("Doe")
                //
                .withEmail("email1@test.com")
                //
                .withActivationCode("activation1")
                .withAccount(
                        new UserAccount().withPassword("password").withEnabled(false).withApplicationListLastAccessTimestamp(DateUtils.addHours(new Date(), 1)));

        currentUser = new User()
                //
                .withFirstName("Jane")
                //
                .withLastName("Doe")
                //
                .withEmail("email2@test.com")
                //
                .withActivationCode("actkivation2")
                .withAccount(
                        new UserAccount().withPassword("password").withEnabled(false).withApplicationListLastAccessTimestamp(DateUtils.addHours(new Date(), 1)));

        sessionFactory.getCurrentSession().flush();

        lastEditedDate = new DateTime(2011, 4, 7, 19, 33);
        submissionDate = new DateTime(2011, 3, 7, 17, 28);

        searchTermDateForLastEdited = new LocalDate(2011, 4, 9);
        searchTermDateForSubmission = new LocalDate(2011, 3, 9);

        program = testObjectProvider.getEnabledProgram();

        app1InApproval = new Application().withProgram(program).withUser(applicant).withState(new State().withId(PrismState.APPLICATION_APPROVAL))
                .withApplicationNumber("app1").withSubmittedTimestamp(submissionDate.plusDays(0));
        app2InReview = new Application().withProgram(program).withUser(applicant).withState(new State().withId(PrismState.APPLICATION_REVIEW))
                .withApplicationNumber("app2").withSubmittedTimestamp(submissionDate.plusDays(1));
        app3InValidation = new Application().withProgram(program).withUser(applicant).withState(new State().withId(PrismState.APPLICATION_VALIDATION))
                .withApplicationNumber("app3").withSubmittedTimestamp(submissionDate.plusDays(2));
        app4InApproved = new Application().withProgram(program).withUser(applicant).withState(new State().withId(PrismState.APPLICATION_APPROVED))
                .withApplicationNumber("app4").withSubmittedTimestamp(submissionDate.plusDays(3));
        app5InInterview = new Application().withProgram(program).withUser(applicant).withState(new State().withId(PrismState.APPLICATION_INTERVIEW))
                .withApplicationNumber("app5").withSubmittedTimestamp(submissionDate.plusDays(4));
        app6InReview = new Application().withProgram(program).withUser(applicant).withState(new State().withId(PrismState.APPLICATION_REVIEW))
                .withApplicationNumber("app6").withSubmittedTimestamp(submissionDate.plusDays(5));

        save(applicant, currentUser, app1InApproval, app2InReview, app3InValidation, app4InApproved, app5InInterview, app6InReview);

        createAndSaveApplicationFormUserRoles(app1InApproval, app2InReview, app3InValidation, app4InApproved, app5InInterview, app6InReview);

        flushAndClearSession();

    }

    private void createAndSaveApplicationFormUserRoles(Application... applications) {
        for (Application applicaton : applications) {
            UserRole userRole = TestData.aUserRole(applicaton, role, currentUser, currentUser);
            save(userRole);
        }
    }

    @Test
    public void shouldReturnAppsFilteredByNumber() {
        Application otherApplicationForm = TestData.anApplicationForm(applicant, program, TestData.aState(PrismState.APPLICATION_APPROVAL));
        save(otherApplicationForm);
        flushAndClearSession();

        ApplicationFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter), 50);

        assertContainsApplications(applications, app1InApproval, app2InReview, app3InValidation, app4InApproved, app5InInterview, app6InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByNotNumber() {
        Application otherApplicationForm = new Application().withProgram(program).withUser(applicant)
                .withState(new State().withId(PrismState.APPLICATION_APPROVAL)).withApplicationNumber("other1");
        save(otherApplicationForm);
        createAndSaveApplicationFormUserRoles(otherApplicationForm);
        flushAndClearSession();

        ApplicationFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER)
                .searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("app").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter), 50);

        assertContainsApplications(applications, otherApplicationForm);
    }

    @Test
    public void shouldReturnAppsFilteredByNumberAndStatus() {
        ApplicationFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();
        ApplicationFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, nameFilter, statusFilter), 50);

        assertContainsApplications(applications, app6InReview, app2InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByDifferentStatus() {
        ApplicationFilter statusfilter2 = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Validation").build();
        ApplicationFilter statusFilter1 = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, true, statusfilter2, statusFilter1), 50);

        assertContainsApplications(applications, app6InReview, app3InValidation, app2InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByNotNumberAndStatus() {
        ApplicationFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER)
                .searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("app2").build();
        ApplicationFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, nameFilter, statusFilter), 50);

        assertContainsApplications(applications, app6InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByStatusAndNumber() {
        ApplicationFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();
        ApplicationFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, nameFilter, statusFilter), 50);

        assertContainsApplications(applications, app6InReview, app2InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByStatusOrNumber() {
        ApplicationFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Approval").build();
        ApplicationFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app5").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, true, nameFilter, statusFilter), 50);

        assertContainsApplications(applications, app5InInterview, app1InApproval);
    }

    @Test
    public void shouldReturnAppsFilteredByApplicantName() {
        User otherUser = new User().withFirstName("Franciszek").withLastName("Pieczka").withEmail("franek@pieczka.com").withActivationCode("activation3")
                .withAccount(new UserAccount().withPassword("franek123").withEnabled(false));

        Application otherApplicationForm = new Application().withProgram(program).withUser(otherUser)
                .withState(new State().withId(PrismState.APPLICATION_APPROVAL)).withApplicationNumber("other1");
        save(otherUser, otherApplicationForm);
        createAndSaveApplicationFormUserRoles(otherApplicationForm);
        flushAndClearSession();

        ApplicationFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("czka").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_STATUS, SortOrder.DESCENDING, 1, filter), 50);

        assertContainsApplications(applications, otherApplicationForm);
    }

    @Test
    public void shouldReturnAppsFilteredByStatusAndLastUpdatedFromDate() {
        String lastEditedDatePlus2String = dateToString(searchTermDateForLastEdited);

        ApplicationFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();
        ApplicationFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchTerm(lastEditedDatePlus2String)
                .searchPredicate(SearchPredicate.FROM_DATE).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, dateFilter, statusFilter), 50);

        assertContainsApplications(applications, app2InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByNotStatusAndLastUpdatedFromDate() {
        String lastEditedDatePlus2String = dateToString(searchTermDateForLastEdited);

        ApplicationFilter notReviewFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS)
                .searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("Review").build();
        ApplicationFilter notApprovalFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS)
                .searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("Approval").build();
        ApplicationFilter notValidationFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS)
                .searchPredicate(SearchPredicate.NOT_CONTAINING).searchTerm("validation").build();
        ApplicationFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchTerm(lastEditedDatePlus2String)
                .searchPredicate(SearchPredicate.FROM_DATE).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, dateFilter, notReviewFilter, notApprovalFilter, notValidationFilter), 50);

        assertContainsApplications(applications, app4InApproved);
    }

    @Test
    public void shouldReturnAppsFilteredByToUpdatedDate() {
        String lastEditedDatePlus2String = dateToString(searchTermDateForLastEdited);

        ApplicationFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchTerm(lastEditedDatePlus2String)
                .searchPredicate(SearchPredicate.TO_DATE).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, dateFilter), 50);

        assertContainsApplications(applications, app6InReview, app5InInterview, app4InApproved);
    }

    @Test
    public void shouldReturnAppsFilteredByOnUpdatedDate() {
        String lastEditedDatePlus2String = dateToString(searchTermDateForLastEdited);

        ApplicationFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.LAST_EDITED_DATE).searchTerm(lastEditedDatePlus2String)
                .searchPredicate(SearchPredicate.ON_DATE).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, dateFilter), 50);

        assertContainsApplications(applications, app4InApproved);
    }

    @Test
    public void shouldReturnAppsFilteredByFromSubmissionDate() {
        String submissionDatePlus2String = dateToString(searchTermDateForSubmission);

        ApplicationFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUBMISSION_DATE).searchTerm(submissionDatePlus2String)
                .searchPredicate(SearchPredicate.FROM_DATE).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, dateFilter), 50);

        assertContainsApplications(applications, app6InReview, app5InInterview, app4InApproved, app3InValidation);
    }

    @Test
    public void shouldReturnAppsFilteredByToSubmissionDate() {
        String submissionDatePlus2String = dateToString(searchTermDateForSubmission);

        ApplicationFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUBMISSION_DATE).searchTerm(submissionDatePlus2String)
                .searchPredicate(SearchPredicate.TO_DATE).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, dateFilter), 50);

        assertContainsApplications(applications, app3InValidation, app2InReview, app1InApproval);
    }

    @Test
    public void shouldReturnAppsFilteredByOnSubmissionDate() {
        String submissionDatePlus2String = dateToString(searchTermDateForSubmission);

        ApplicationFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUBMISSION_DATE).searchTerm(submissionDatePlus2String)
                .searchPredicate(SearchPredicate.ON_DATE).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, dateFilter), 50);

        assertContainsApplications(applications, app3InValidation);
    }

    @Test
    public void shouldReturnAppsFilteredByEmptyNumber() {
        ApplicationFilter dateFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("")
                .searchPredicate(SearchPredicate.CONTAINING).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, dateFilter), 50);

        assertContainsApplications(applications, app1InApproval, app2InReview, app3InValidation, app4InApproved, app5InInterview, app6InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByApplicantFirstNameAndLastName() {
        User otherUser = new User().withFirstName("Franciszek").withLastName("Pieczka").withEmail("franek@pieczka.com").withActivationCode("activation3");

        Application otherApplicationForm = new Application().withProgram(program).withUser(otherUser)
                .withState(new State().withId(PrismState.APPLICATION_APPROVAL)).withApplicationNumber("other1");
        save(otherUser, otherApplicationForm);
        createAndSaveApplicationFormUserRoles(otherApplicationForm);
        flushAndClearSession();

        ApplicationFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("ciszek Pieczka").build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.APPLICATION_STATUS, SortOrder.DESCENDING, 1, filter), 50);

        assertContainsApplications(applications, otherApplicationForm);
    }

    @Test
    public void shouldReturnAppsFilteredByProgramName() {
        Program alternativeProgram = testObjectProvider.getAlternativeEnabledProgram(program);

        Application app1InApproval = new Application().withProgram(alternativeProgram).withUser(applicant)
                .withState(new State().withId(PrismState.APPLICATION_APPROVAL)).withApplicationNumber("app112")
                .withSubmittedTimestamp(submissionDate.plusDays(0));

        save(app1InApproval);
        createAndSaveApplicationFormUserRoles(app1InApproval);

        flushAndClearSession();

        ApplicationFilter programFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROGRAMME_NAME)
                .searchTerm(alternativeProgram.getTitle()).searchPredicate(SearchPredicate.CONTAINING).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.ASCENDING, 1, programFilter), 50);

        assertContainsApplications(applications, app1InApproval);
    }

    @Test
    public void shouldReturnAppsSortedByRating() {

        ApplicationFilter programFilter = new ApplicationsFilterBuilder().searchCategory(null).searchTerm(null).searchPredicate(null).build();

        List<ApplicationDescriptor> applications = applicationDAO.getVisibleApplicationsForList(currentUser,
                newFiltering(SortCategory.RATING, SortOrder.ASCENDING, 1, programFilter), 50);

        List<Application> expectedApplications = Lists.newArrayList(app4InApproved, app3InValidation, app5InInterview, app1InApproval, app2InReview,
                app6InReview);

        for (int i = 0; i < expectedApplications.size(); i++) {
            assertEquals(expectedApplications.get(i).getApplicationNumber(), applications.get(i).getApplicationFormNumber());
        }
    }

    private String dateToString(LocalDate date) {
        return ApplicationFormListDAO.USER_DATE_FORMAT.print(date);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void assertContainsApplications(List<ApplicationDescriptor> applications, Application... expectedApplications) {
        for (Application expectedApplication : expectedApplications) {
            assertThat(applications, (Matcher) hasItem(hasProperty("applicationFormNumber", equalTo(expectedApplication.getApplicationNumber()))));
        }
    }

    private ApplicationFilterGroup newFiltering(SortCategory sortCategory, SortOrder sortOrder, int blockCount, ApplicationFilter... filters) {
        return new ApplicationsFilteringBuilder().sortCategory(sortCategory).order(sortOrder).blockCount(blockCount).filters(filters).build();
    }

    private ApplicationFilterGroup newFiltering(SortCategory sortCategory, SortOrder sortOrder, int blockCount, boolean useDisjunction,
            ApplicationFilter... filters) {
        return new ApplicationsFilteringBuilder().sortCategory(sortCategory).order(sortOrder).blockCount(blockCount).useDisjunction(useDisjunction)
                .filters(filters).build();
    }

}