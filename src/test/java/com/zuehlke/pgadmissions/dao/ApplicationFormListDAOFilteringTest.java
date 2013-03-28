package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
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

    @Before
    public void setup() {
        applicationDAO = new ApplicationFormListDAO(sessionFactory);

        program = new ProgramBuilder().code("doesntexist").title("another title").build();

        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        adminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).build();

        Date submissionDate = new Date();
        Date lastEditedDate = new Date();

        app1InApproval = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).applicationNumber("app1")
                .submittedDate(DateUtils.addDays(submissionDate, 0)).lastUpdated(DateUtils.addDays(lastEditedDate, 4)).build();
        app2InReview = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REVIEW).applicationNumber("app2")
                .submittedDate(DateUtils.addDays(submissionDate, 1)).lastUpdated(DateUtils.addDays(lastEditedDate, 3)).build();
        app3InValidation = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).applicationNumber("app3")
                .submittedDate(DateUtils.addDays(submissionDate, 2)).lastUpdated(DateUtils.addDays(lastEditedDate, 2)).build();
        app4InApproved = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVED).applicationNumber("app4")
                .submittedDate(DateUtils.addDays(submissionDate, 3)).lastUpdated(DateUtils.addDays(lastEditedDate, 1)).build();
        app5InInterview = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).applicationNumber("app5")
                .submittedDate(DateUtils.addDays(submissionDate, 4)).lastUpdated(DateUtils.addDays(lastEditedDate, 0)).build();

        save(program, user, adminUser, app1InApproval, app2InReview, app3InValidation, app4InApproved, app5InInterview);

        flushAndClearSession();
    }

    @Test
    public void shouldReturnAppsFilteredByNumber() {

        ApplicationForm otherApplicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVAL)
                .applicationNumber("other1").build();
        save(otherApplicationForm);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(filter), SortCategory.APPLICATION_STATUS,
                SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app3InValidation, app2InReview, app5InInterview, app4InApproved, app1InApproval);

    }

    @Test
    public void shouldReturnAppsFilteredByNumberAndStatus() {

        ApplicationsFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();
        ApplicationsFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(nameFilter, statusFilter),
                SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app2InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByStatusAndNumber() {

        ApplicationsFilter statusFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Review").build();
        ApplicationsFilter nameFilter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("app").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(nameFilter, statusFilter),
                SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, app2InReview);
    }

    @Test
    public void shouldReturnAppsFilteredByApplicantNameAndDate() {

        RegisteredUser otherUser = new RegisteredUserBuilder().firstName("Franciszek").lastName("Pieczka").email("franek@pieczka.com").username("franek")
                .password("franek123").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm otherApplicationForm = new ApplicationFormBuilder().program(program).applicant(otherUser).status(ApplicationFormStatus.APPROVAL)
                .applicationNumber("other1").build();
        save(otherUser, otherApplicationForm);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("czka").build();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(adminUser, Arrays.asList(filter), SortCategory.APPLICATION_STATUS,
                SortOrder.DESCENDING, 1, 50);

        assertContainsApplications(applications, otherApplicationForm);
    }

    private static void assertContainsApplications(List<ApplicationForm> applications, ApplicationForm... expectedApplications) {
        assertEquals(expectedApplications.length, applications.size());
        for (int i = 0; i < expectedApplications.length; i++) {
            assertEquals(expectedApplications[i].getApplicationNumber(), applications.get(i).getApplicationNumber());
        }
    }

}
