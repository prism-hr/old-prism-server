package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;
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
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApplicationFormDAOTest extends AutomaticRollbackTestCase {

    private ApplicationFormDAO applicationDAO;
    private RegisteredUser user;
    private Program program;

    private ApplicationForm application;

    @Before
    public void prepare() {
        applicationDAO = new ApplicationFormDAO(sessionFactory);
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        program = new ProgramBuilder().code("doesntexist").title("another title").build();

        save(user, program);

        flushAndClearSession();
    }

    @Test(expected = NullPointerException.class)
    public void shouldSendNullPointerException() {
        ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        applicationFormDAO.save(applicationForm);
    }

    @Test
    public void shouldSaveAndLoadApplication() throws Exception {

        ApplicationForm inApplication = new ApplicationForm();
        inApplication.setProgram(program);

        inApplication.setApplicant(user);

        assertNull(inApplication.getId());

        applicationDAO.save(inApplication);

        assertNotNull(inApplication.getId());
        Integer id = inApplication.getId();
        ApplicationForm reloadedApplication = applicationDAO.get(id);
        assertSame(inApplication, reloadedApplication);

        flushAndClearSession();

        reloadedApplication = applicationDAO.get(id);
        assertNotSame(inApplication, reloadedApplication);
        assertEquals(inApplication.getId(), reloadedApplication.getId());
        assertEquals(inApplication.getApplicant().getId(), user.getId());
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
        ApplicationForm inApplication = new ApplicationForm();
        inApplication.setProgram(program);
        inApplication.setApplicant(user);

        applicationDAO.save(inApplication);

        Integer id = inApplication.getId();
        ApplicationForm reloadedApplication = applicationDAO.get(id);
        assertNotNull(reloadedApplication.getApplicationTimestamp());

    }

    @Test
    public void shouldReturnNumberOfApplicationsInProgramThisYear() {
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        String lastYear = new Integer(Integer.parseInt(thisYear) - 1).toString();
        String nextYear = new Integer(Integer.parseInt(thisYear) + 1).toString();
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(program);
        flushAndClearSession();

        long number = applicationDAO.getApplicationsInProgramThisYear(program, thisYear);
        assertEquals(0, number);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();

        save(applicationFormOne);

        flushAndClearSession();

        assertEquals(Long.valueOf(1), applicationDAO.getApplicationsInProgramThisYear(program, thisYear));

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
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
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(program);

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(program).applicant(user)
                .status(ApplicationFormStatus.APPROVAL).build();

        save(applicationFormOne);

        flushAndClearSession();

        ApplicationForm returnedForm = applicationDAO.getApplicationByApplicationNumber("ABC");
        assertEquals(applicationFormOne.getId(), returnedForm.getId());
    }

    private List<Qualification> getQualificationsBelongingToSameApplication() throws ParseException {

        application = new ApplicationForm();
        application.setApplicant(user);
        application.setProgram(program);

        QualificationTypeDAO typeDao = new QualificationTypeDAO(sessionFactory);

        List<Qualification> qualifications = new ArrayList<Qualification>();

        Qualification qualification1 = new Qualification();
        qualification1.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification1.setQualificationGrade("");
        qualification1.setQualificationInstitution("");

        qualification1.setQualificationLanguage("Abkhazian");
        qualification1.setQualificationSubject("");
        qualification1.setQualificationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification1.setQualificationType(typeDao.getAllQualificationTypes().get(0));

        qualifications.add(qualification1);

        Qualification qualification2 = new Qualification();
        qualification2.setQualificationAwardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/02/02"));
        qualification2.setQualificationGrade("");
        qualification2.setQualificationInstitution("");
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

        Program program = new ProgramBuilder().title("program").code("KLOP").build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        ApplicationForm applicationForm2 = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED).build();

        save(program, applicationForm, applicationForm2);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getApplicationsByApplicantAndProgram(user, program);
        assertNotNull(applications);

        Matcher<Iterable<ApplicationForm>> hasItems = hasItems( //
                hasProperty("status", equalTo(ApplicationFormStatus.APPROVAL)), //
                hasProperty("status", equalTo(ApplicationFormStatus.UNSUBMITTED)));
        assertThat(applications, hasItems);
    }

    @Test
    public void shouldGetNoApplicationsByApplicantAndProgram() {

        Program program = new ProgramBuilder().title("program").code("KLOP").build();

        save(program);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getApplicationsByApplicantAndProgram(user, program);
        assertThat(applications, is(empty()));
    }

}
