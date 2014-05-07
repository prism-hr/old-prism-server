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

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class ApplicationFormDAOTest extends AutomaticRollbackTestCase {

    private ApplicationFormDAO applicationDAO;
    private User user;
    private Program program;
    private State unsibmittedState;

    private ApplicationForm application;

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
        ApplicationForm applicationForm = new ApplicationForm().withId(1);
        applicationFormDAO.save(applicationForm);
    }

    @Test
    public void shouldSaveAndLoadApplication() throws Exception {

        ApplicationForm inApplication = new ApplicationForm().withProgram(program).withUser(user).withState(unsibmittedState);

        assertNull(inApplication.getId());

        applicationDAO.save(inApplication);

        assertNotNull(inApplication.getId());
        Integer id = inApplication.getId();
        ApplicationForm reloadedApplication = applicationDAO.getById(id);
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
        ApplicationForm inApplication = new ApplicationForm().withProgram(program).withUser(user).withState(unsibmittedState);

        applicationDAO.save(inApplication);

        Integer id = inApplication.getId();
        ApplicationForm reloadedApplication = applicationDAO.getById(id);
        assertNotNull(reloadedApplication.getCreatedTimestamp());
    }

    @Test
    public void shouldReturnNumberOfApplicationsInProgramThisYear() {
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        String lastYear = new Integer(Integer.parseInt(thisYear) - 1).toString();
        String nextYear = new Integer(Integer.parseInt(thisYear) + 1).toString();
        flushAndClearSession();

        Institution institution = testObjectProvider.getInstitution();
        Program program = new ProgramBuilder().code("test").title("test").description("test")
                .contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).institution(institution).build();
        save(program);

        long number = applicationDAO.getApplicationsInProgramThisYear(program, thisYear);
        assertEquals(0, number);
        ApplicationForm applicationFormOne = new ApplicationForm().withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_APPROVAL));

        save(applicationFormOne);

        flushAndClearSession();

        assertEquals(Long.valueOf(1), applicationDAO.getApplicationsInProgramThisYear(program, thisYear));

        ApplicationForm applicationFormTwo = new ApplicationForm().withProgram(program).withUser(user)
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
        ApplicationForm applicationFormOne = new ApplicationForm().withApplicationNumber("ABC").withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_APPROVAL));

        save(applicationFormOne);

        flushAndClearSession();

        ApplicationForm returnedForm = applicationDAO.getByApplicationNumber("ABC");
        assertEquals(applicationFormOne.getId(), returnedForm.getId());
    }

    private List<Qualification> getQualificationsBelongingToSameApplication() throws ParseException {

        application = new ApplicationForm().withProgram(program).withUser(user).withState(unsibmittedState);

        QualificationTypeDAO typeDao = new QualificationTypeDAO(sessionFactory);

        List<Qualification> qualifications = new ArrayList<Qualification>();

        Qualification qualification1 = new Qualification();
        qualification1.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification1.setQualificationGrade("");
        // qualification1.setQualificationInstitution("");

        qualification1.setQualificationLanguage("Abkhazian");
        qualification1.setQualificationSubject("");
        qualification1.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification1.setQualificationType(typeDao.getAllQualificationTypes().get(0));

        qualifications.add(qualification1);

        Qualification qualification2 = new Qualification();
        qualification2.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification2.setQualificationGrade("");
        // qualification2.setQualificationInstitution("");
        qualification2.setQualificationLanguage("Abkhazian");
        qualification2.setQualificationSubject("");
        qualification2.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification2.setQualificationType(typeDao.getAllQualificationTypes().get(0));

        qualifications.add(qualification1);
        return qualifications;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTwoApplicationsByApplicantAndProgram() {
        ApplicationForm applicationForm = new ApplicationForm().withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_APPROVAL));
        ApplicationForm applicationForm2 = new ApplicationForm().withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_UNSUBMITTED));

        save(applicationForm, applicationForm2);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getApplicationsByApplicantAndProgram(user, program);
        assertNotNull(applications);

        Matcher<Iterable<ApplicationForm>> hasItems = hasItems( //
                hasProperty("state", hasProperty("id", equalTo(PrismState.APPLICATION_APPROVAL))), //
                hasProperty("state", hasProperty("id", equalTo(PrismState.APPLICATION_UNSUBMITTED))));
        assertThat(applications, hasItems);
    }

    @Test
    public void shouldGetNoApplicationsByApplicantAndProgram() {
        List<ApplicationForm> applications = applicationDAO.getApplicationsByApplicantAndProgram(user, program);
        assertThat(applications, is(empty()));
    }

    @Test
    public void shouldGetPreviousApplicationForApplicant() {
        DateTime initialDate = new DateTime(2014, 5, 13, 15, 56);

        User otherApplicant = new User().withFirstName("Other").withLastName("Applicant").withEmail("other@applicant.com").withActivationCode("AA")
                .withAccount(new UserAccount().withPassword("password").withEnabled(false));

        ApplicationForm applicationForm = new ApplicationForm().withSubmittedTimestamp(initialDate.plusDays(2).toDate()).withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_APPROVAL));
        ApplicationForm applicationForm2 = new ApplicationForm().withSubmittedTimestamp(initialDate.toDate()).withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_VALIDATION));
        ApplicationForm applicationForm3 = new ApplicationForm().withSubmittedTimestamp(initialDate.plusDays(1).toDate()).withProgram(program).withUser(user)
                .withState(testObjectProvider.getState(PrismState.APPLICATION_INTERVIEW));

        ApplicationForm recentApplicationForm = new ApplicationForm().withSubmittedTimestamp(initialDate.plusDays(2).plusMinutes(2).toDate())
                .withProgram(program).withUser(otherApplicant).withState(new State().withId(PrismState.APPLICATION_UNSUBMITTED));
        ApplicationForm otherApplication = new ApplicationForm().withSubmittedTimestamp(initialDate.plusWeeks(1).toDate()).withProgram(program)
                .withUser(otherApplicant).withState(new State().withId(PrismState.APPLICATION_REVIEW));

        save(otherApplicant, applicationForm, applicationForm2, applicationForm3, recentApplicationForm, otherApplication);

        ApplicationForm returned = applicationDAO.getPreviousApplicationForApplicant(recentApplicationForm, otherApplicant);

        assertEquals(otherApplication.getId(), returned.getId());
    }

}
