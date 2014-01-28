package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApplicationFormCreationServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private ProgrammeDetailsService programmeDetailsServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContextMock;

    @TestedObject
    private ApplicationFormCreationService service;

    @Test
    public void shouldReturnRecentApplicationForm() throws ParseException {
        RegisteredUser applicant = new RegisteredUser();
        Program program = new Program();
        Project project = new Project();
        ApplicationForm existingApplicationForm = new ApplicationForm();

        ApplicationFormCreationService thisBeanMock = EasyMockUnitils.createMock(ApplicationFormCreationService.class);

        expect(applicationContextMock.getBean(ApplicationFormCreationService.class)).andReturn(thisBeanMock);
        expect(thisBeanMock.findMostRecentApplication(applicant, program, project)).andReturn(existingApplicationForm);

        replay();
        ApplicationForm returnedForm = service.createOrGetUnsubmittedApplicationForm(applicant, program, project);
        verify();

        assertSame(existingApplicationForm, returnedForm);
    }

    @Test
    public void shouldCreateAndSaveNewApplicationForm() throws ParseException {
        RegisteredUser applicant = new RegisteredUser();
        Program program = new Program();
        Project project = new Project();
        ApplicationForm newApplicationForm = new ApplicationForm();

        ApplicationFormCreationService thisBeanMock = EasyMockUnitils.createMock(ApplicationFormCreationService.class);

        expect(applicationContextMock.getBean(ApplicationFormCreationService.class)).andReturn(thisBeanMock);
        expect(thisBeanMock.findMostRecentApplication(applicant, program, project)).andReturn(null);
        expect(thisBeanMock.createNewApplicationForm(applicant, program, project)).andReturn(newApplicationForm);
        thisBeanMock.addSuggestedSupervisors(newApplicationForm, project);
        applicationFormUserRoleServiceMock.applicationCreated(newApplicationForm);

        replay();
        ApplicationForm returnedForm = service.createOrGetUnsubmittedApplicationForm(applicant, program, project);
        verify();

        assertSame(newApplicationForm, returnedForm);
    }

    @Test
    public void shouldGetRecentlyEditedUnsubmittedApplicationForGivenQueryString() throws ParseException {
        // GIVEN
        Date date = new Date();

        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
        final ApplicationForm oldApplicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.UNSUBMITTED).lastUpdated(date).build();
        final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.UNSUBMITTED)
                .lastUpdated(DateUtils.addDays(date, 2)).build();
        final ApplicationForm oldApplicationForm2 = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED)
                .lastUpdated(DateUtils.addDays(date, 1)).build();

        EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program)).andReturn(
                Lists.newArrayList(oldApplicationForm, newApplicationForm, oldApplicationForm2));

        // WHEN
        replay();
        ApplicationForm returnedForm = service.findMostRecentApplication(registeredUser, program, null);
        // THEN
        verify();

        assertSame(newApplicationForm, returnedForm);
    }

    @Test
    public void shouldGetInterviewedApplicationForGivenQueryString() throws ParseException {

        // GIVEN
        Date date = new Date();

        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
        final ApplicationForm validationApplicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).lastUpdated(date).build();
        final ApplicationForm unsubmittedApplicationForm = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.UNSUBMITTED)
                .lastUpdated(DateUtils.addDays(date, 2)).build();
        final ApplicationForm interviewApplicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.INTERVIEW)
                .lastUpdated(DateUtils.addDays(date, 1)).build();

        EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program)).andReturn(
                Lists.newArrayList(validationApplicationForm, unsubmittedApplicationForm, interviewApplicationForm));

        // WHEN
        replay();
        ApplicationForm returnedForm = service.findMostRecentApplication(registeredUser, program, null);

        // THEN
        verify();

        assertSame(interviewApplicationForm, returnedForm);
    }

    @Test
    public void shouldGetMostRecentApprovalApplicationForGivenQueryString() throws ParseException {

        // GIVEN
        Date date = new Date();

        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
        final ApplicationForm oldApplicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVAL).lastUpdated(date).build();
        final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.APPROVAL)
                .lastUpdated(DateUtils.addDays(date, 2)).build();
        final ApplicationForm oldApplicationForm2 = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.APPROVAL)
                .lastUpdated(DateUtils.addDays(date, 1)).build();

        EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program)).andReturn(
                Lists.newArrayList(oldApplicationForm, newApplicationForm, oldApplicationForm2));

        // WHEN
        replay();
        ApplicationForm returnedForm = service.findMostRecentApplication(registeredUser, program, null);
        verify();

        // THEN
        assertSame(newApplicationForm, returnedForm);
    }

    @Test
    public void shouldGenerateNewApplicationNumber() {
        Program program = new ProgramBuilder().code("KLOP").id(1).build();

        String thisYear = new SimpleDateFormat("yyyy").format(new Date());

        expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23L);

        replay();
        String result = service.generateNewApplicationNumber(program);
        verify();

        assertEquals("KLOP-" + thisYear + "-000024", result);
    }

    @Test
    public void shouldNotAddSuggestedSupervisorsIfNoProject() {
        service.addSuggestedSupervisors(null, null);
    }

    @Test
    public void shouldAddSuggestedSupervisors() {
        RegisteredUser primarySupervisorUser = new RegisteredUserBuilder().email("primary@mail.com").firstName("primaryF").lastName("primaryL").build();
        RegisteredUser secondarySupervisorUser = new RegisteredUserBuilder().email("secondary@mail.com").firstName("secondaryF").lastName("secondaryL").build();

        ProgrammeDetails programmeDetails = new ProgrammeDetails();
        ApplicationForm applicationForm = new ApplicationFormBuilder().programmeDetails(programmeDetails).build();
        Project project = new ProjectBuilder().primarySupervisor(primarySupervisorUser).secondarySupervisor(secondarySupervisorUser).build();

        service.addSuggestedSupervisors(applicationForm, project);

        SuggestedSupervisor primarySupervisor = programmeDetails.getSuggestedSupervisors().get(0);
        SuggestedSupervisor secondarySupervisor = programmeDetails.getSuggestedSupervisors().get(1);

        assertEquals("primary@mail.com", primarySupervisor.getEmail());
        assertEquals("primaryF", primarySupervisor.getFirstname());
        assertEquals("primaryL", primarySupervisor.getLastname());

        assertEquals("secondary@mail.com", secondarySupervisor.getEmail());
        assertEquals("secondaryF", secondarySupervisor.getFirstname());
        assertEquals("secondaryL", secondarySupervisor.getLastname());
    }

    @Test
    public void shouldCreateNewApplicationForm() {
        RegisteredUser applicant = new RegisteredUser();
        Program program = new ProgramBuilder().title("A program").build();
        Project project = new Project();

        ApplicationFormCreationService thisBeanMock = EasyMockUnitils.createMock(ApplicationFormCreationService.class);

        expect(applicationContextMock.getBean(ApplicationFormCreationService.class)).andReturn(thisBeanMock);
        expect(thisBeanMock.generateNewApplicationNumber(program)).andReturn("007");
        applicationFormDAOMock.save(isA(ApplicationForm.class));
        programmeDetailsServiceMock.save(isA(ProgrammeDetails.class));

        replay();
        ApplicationForm applicationForm = service.createNewApplicationForm(applicant, program, project);
        verify();

        assertSame(applicant, applicationForm.getApplicant());
        assertSame(program, applicationForm.getProgram());
        assertSame(project, applicationForm.getProject());
        assertEquals("007", applicationForm.getApplicationNumber());

        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        assertEquals("A program", programmeDetails.getProgrammeName());
        assertSame(programmeDetails, applicationForm.getProgrammeDetails());
        assertSame(applicationForm, programmeDetails.getApplication());
    }

}
