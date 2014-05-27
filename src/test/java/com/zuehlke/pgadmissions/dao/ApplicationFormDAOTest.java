package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class ApplicationFormDAOTest extends AutomaticRollbackTestCase {

    private ApplicationFormDAO applicationDAO;
    private System system;
    private Institution institution;
    private Program program;
    private User user;
    private State unsubmittedState;

    @Before
    public void prepare() {
        applicationDAO = new ApplicationFormDAO(sessionFactory);

        system = testObjectProvider.getSystem();
        institution = testObjectProvider.getInstitution();
        program = testObjectProvider.getEnabledProgram();
        user = testObjectProvider.getEnabledUserInRole(Authority.APPLICATION_CREATOR);
        unsubmittedState = testObjectProvider.getState(PrismState.APPLICATION_UNSUBMITTED);
    }

    @Test(expected = NullPointerException.class)
    public void shouldSendNullPointerException() {
        ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO();
        Application applicationForm = new Application().withId(1);
        applicationFormDAO.save(applicationForm);
    }

    @Test
    public void shouldSaveAndLoadApplication() throws Exception {

        Application inApplication = TestData.anApplicationForm(system, institution, program, user, unsubmittedState);

        assertNull(inApplication.getId());

        applicationDAO.save(inApplication);

        assertNotNull(inApplication.getId());
        Integer id = inApplication.getId();
        Application reloadedApplication = applicationDAO.getById(id);
        assertSame(inApplication, reloadedApplication);

        flushAndClearSession();

        reloadedApplication = applicationDAO.getById(id);
        assertNotSame(inApplication, reloadedApplication);
        assertEquals(inApplication.getId(), reloadedApplication.getId());
        assertEquals(inApplication.getUser().getId(), user.getId());
    }

    @Test
    public void shouldReturnNumberOfApplicationsInProgramThisYear() {
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        String lastYear = new Integer(Integer.parseInt(thisYear) - 1).toString();
        String nextYear = new Integer(Integer.parseInt(thisYear) + 1).toString();

        Institution institution = testObjectProvider.getInstitution();
        ProgramType programType = testObjectProvider.getProgramType();
        Program program = TestData.aProgram(programType, institution, user, testObjectProvider.getState(PrismState.PROGRAM_APPROVED));
        save(program);
        
        flushAndClearSession();

        long number = applicationDAO.getApplicationsInProgramThisYear(program, thisYear);
        assertEquals(0, number);
        Application applicationFormOne = new Application().withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_APPROVAL));

        save(applicationFormOne);

        flushAndClearSession();

        assertEquals(Long.valueOf(1), applicationDAO.getApplicationsInProgramThisYear(program, thisYear));

        Application applicationFormTwo = new Application().withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_VALIDATION));
        save(applicationFormTwo);

        flushAndClearSession();

        assertEquals(Long.valueOf(2), applicationDAO.getApplicationsInProgramThisYear(program, thisYear));
        assertEquals(Long.valueOf(0), applicationDAO.getApplicationsInProgramThisYear(program, lastYear));
        assertEquals(Long.valueOf(0), applicationDAO.getApplicationsInProgramThisYear(program, nextYear));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowDataAccessAcceptionOnParseException() {

        applicationDAO.getApplicationsInProgramThisYear(program, "bob");

    }

    @Test
    public void shouldGetApplicationByApplicationNumber() {
        Application applicationFormOne = TestData.anApplicationForm(system, institution, program, user, testObjectProvider.getState(PrismState.APPLICATION_APPROVAL)).withCode("ABC"); 

        save(applicationFormOne);

        flushAndClearSession();

        Application returnedForm = applicationDAO.getByApplicationNumber("ABC");
        assertEquals(applicationFormOne.getId(), returnedForm.getId());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTwoApplicationsByApplicantAndProgram() {
        Application applicationForm = TestData.anApplicationForm(system, institution, program, user, testObjectProvider.getState(PrismState.APPLICATION_APPROVAL)).withCode("dfgf1");
        Application applicationForm2 = TestData.anApplicationForm(system, institution, program, user, testObjectProvider.getState(PrismState.APPLICATION_UNSUBMITTED)).withCode("dfgf2");

        save(applicationForm, applicationForm2);
        flushAndClearSession();

        List<Application> applications = applicationDAO.getApplicationsByApplicantAndProgram(user, program);
        assertNotNull(applications);

        Matcher<Iterable<Application>> hasItems = hasItems( //
                hasProperty("state", hasProperty("id", equalTo(PrismState.APPLICATION_APPROVAL))), //
                hasProperty("state", hasProperty("id", equalTo(PrismState.APPLICATION_UNSUBMITTED))));
        assertThat(applications, hasItems);
    }

    @Test
    public void shouldGetNoApplicationsByApplicantAndProgram() {
        List<Application> applications = applicationDAO.getApplicationsByApplicantAndProgram(user, program);
        assertThat(applications, is(empty()));
    }

    @Test
    public void shouldGetPreviousApplicationForApplicant() {
        DateTime initialDate = new DateTime(2014, 5, 13, 15, 56);

        User otherApplicant = new User().withFirstName("Other").withLastName("Applicant").withEmail("other@applicant.com").withActivationCode("AA")
                .withAccount(new UserAccount().withPassword("password").withEnabled(false));

        Application applicationForm = TestData.anApplicationForm(system, institution, program, user, testObjectProvider.getState(PrismState.APPLICATION_APPROVAL)).withCode("kdjsa1");
        Application applicationForm2 = TestData.anApplicationForm(system, institution, program, user, testObjectProvider.getState(PrismState.APPLICATION_VALIDATION)).withCode("kdjsa2");
        Application applicationForm3 = TestData.anApplicationForm(system, institution, program, user, testObjectProvider.getState(PrismState.APPLICATION_INTERVIEW)).withCode("kdjsa3");

        Application recentApplicationForm = new Application().withSubmittedTimestamp(initialDate.plusDays(2).plusMinutes(2))
                .withProgram(program).withUser(otherApplicant).withState(new State().withId(PrismState.APPLICATION_UNSUBMITTED));
        Application otherApplication = new Application().withSubmittedTimestamp(initialDate.plusWeeks(1)).withProgram(program)
                .withUser(otherApplicant).withState(new State().withId(PrismState.APPLICATION_REVIEW));

        save(otherApplicant, applicationForm, applicationForm2, applicationForm3, recentApplicationForm, otherApplication);

        Application returned = applicationDAO.getPreviousApplication(recentApplicationForm);

        assertEquals(otherApplication.getId(), returned.getId());
    }

}
