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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class ApplicationFormDAOTest extends AutomaticRollbackTestCase {

    private ApplicationFormDAO applicationDAO;
    private User user;
    private Program program;
    private State unsibmittedState;

    private Application application;

    @Before
    public void prepare() {
        applicationDAO = new ApplicationFormDAO(sessionFactory);

        user = testObjectProvider.getEnabledUserInRole(Authority.APPLICATION_CREATOR);
        unsibmittedState = testObjectProvider.getState(PrismState.APPLICATION_UNSUBMITTED);
        program = testObjectProvider.getEnabledProgram();
    }

    @Test(expected = NullPointerException.class)
    public void shouldSendNullPointerException() {
        ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO();
        Application applicationForm = new Application().withId(1);
        applicationFormDAO.save(applicationForm);
    }

    @Test
    public void shouldSaveAndLoadApplication() throws Exception {

        Application inApplication = new Application().withProgram(program).withUser(user).withState(unsibmittedState);

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
    public void shouldFindAllQualificationsBelongingToSameApplication() throws ParseException {
        List<Qualification> qualifications = getQualificationsBelongingToSameApplication();
        applicationDAO.save(application);
        flushAndClearSession();
        List<Qualification> qualificationsByApplication = applicationDAO.getQualificationsByApplication(application);
        assertNotSame(qualifications, qualificationsByApplication);
        assertEquals(qualifications.get(0).getApplication(), qualifications.get(1).getApplication());
    }

    @Test
    public void shouldAssignDateToApplicationForm() {
        Application inApplication = new Application().withProgram(program).withUser(user).withState(unsibmittedState);

        applicationDAO.save(inApplication);

        Integer id = inApplication.getId();
        Application reloadedApplication = applicationDAO.getById(id);
        assertNotNull(reloadedApplication.getCreatedTimestamp());
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
        Application applicationFormOne = new Application().withApplicationNumber("ABC").withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_APPROVAL));

        save(applicationFormOne);

        flushAndClearSession();

        Application returnedForm = applicationDAO.getByApplicationNumber("ABC");
        assertEquals(applicationFormOne.getId(), returnedForm.getId());
    }

    private List<Qualification> getQualificationsBelongingToSameApplication() throws ParseException {

        application = new Application().withProgram(program).withUser(user).withState(unsibmittedState);

        List<Qualification> qualifications = new ArrayList<Qualification>();

        Qualification qualification1 = new Qualification();
        qualification1.setAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification1.setGrade("");
        // qualification1.setQualificationInstitution("");

        qualification1.setLanguage("Abkhazian");
        qualification1.setSubject("");
        qualification1.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification1.setType(testObjectProvider.getQualificationType());

        qualifications.add(qualification1);

        Qualification qualification2 = new Qualification();
        qualification2.setAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification2.setGrade("");
        // qualification2.setQualificationInstitution("");
        qualification2.setLanguage("Abkhazian");
        qualification2.setSubject("");
        qualification2.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification2.setType(testObjectProvider.getQualificationType());

        qualifications.add(qualification1);
        return qualifications;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTwoApplicationsByApplicantAndProgram() {
        Application applicationForm = new Application().withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_APPROVAL));
        Application applicationForm2 = new Application().withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_UNSUBMITTED));

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

        Application applicationForm = new Application().withSubmittedTimestamp(initialDate.plusDays(2).toDate()).withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_APPROVAL));
        Application applicationForm2 = new Application().withSubmittedTimestamp(initialDate.toDate()).withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_VALIDATION));
        Application applicationForm3 = new Application().withSubmittedTimestamp(initialDate.plusDays(1).toDate()).withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_INTERVIEW));

        Application recentApplicationForm = new Application().withSubmittedTimestamp(initialDate.plusDays(2).plusMinutes(2).toDate())
                .withProgram(program).withUser(otherApplicant).withState(new State().withId(PrismState.APPLICATION_UNSUBMITTED));
        Application otherApplication = new Application().withSubmittedTimestamp(initialDate.plusWeeks(1).toDate()).withProgram(program)
                .withUser(otherApplicant).withState(new State().withId(PrismState.APPLICATION_REVIEW));

        save(otherApplicant, applicationForm, applicationForm2, applicationForm3, recentApplicationForm, otherApplication);

        Application returned = applicationDAO.getPreviousApplicationForApplicant(recentApplicationForm, otherApplicant);

        assertEquals(otherApplication.getId(), returned.getId());
    }

}
