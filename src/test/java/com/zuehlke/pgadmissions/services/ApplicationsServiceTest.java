package com.zuehlke.pgadmissions.services;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApplicationsServiceTest {

    @TestedObject
    private ApplicationsService applicationsService;

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private MailSendingService mailServiceMock;

    @Mock
    @InjectIntoByType
    private ProgramDAO programDAOMock;

    @Mock
    @InjectIntoByType
    private ProgrammeDetailsService programmeDetailsServiceMock;;

    @Test
    public void shouldGetAllApplicationsDueAndUpdatedNotificationToAdmin() {
        List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).build(), new ApplicationFormBuilder().id(2).build());
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(applicationsList);

        replay();
        List<ApplicationForm> appsDueUpdateNotification = applicationsService.getApplicationsDueUpdateNotification();
        verify();

        assertSame(applicationsList, appsDueUpdateNotification);
    }

    @Test
    public void shouldGetAllApplicationsDueRegistryNotification() {
        List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).build(), new ApplicationFormBuilder().id(2).build());
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueRegistryNotification()).andReturn(applicationsList);

        replay();
        List<ApplicationForm> appsDueRegistryNotification = applicationsService.getApplicationsDueRegistryNotification();
        verify();

        assertSame(applicationsList, appsDueRegistryNotification);
    }

    @Test
    public void shouldGetAllApplicationsDueApprovalRestartRequestNotification() {
        List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).build(), new ApplicationFormBuilder().id(2).build());
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueApprovalRequestNotification()).andReturn(applicationsList);

        replay();
        List<ApplicationForm> appsDueNotification = applicationsService.getApplicationsDueApprovalRestartRequestNotification();
        verify();

        assertSame(applicationsList, appsDueNotification);
    }

    @Test
    public void shouldGetAllApplicationsDueApprovalRestartRequestReminder() {
        List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).build(), new ApplicationFormBuilder().id(2).build());
        EasyMock.expect(applicationFormDAOMock.getApplicationDueApprovalRestartRequestReminder()).andReturn(applicationsList);

        replay();
        List<ApplicationForm> appsDueNotification = applicationsService.getApplicationsDueApprovalRestartRequestReminder();
        verify();

        assertSame(applicationsList, appsDueNotification);
    }

    @Test
    public void shouldSendSubmissionsConfirmationToApplicant() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).build();

        mailServiceMock.sendSubmissionConfirmationToApplicant(application);

        applicationFormDAOMock.save(application);

        replay();
        applicationsService.sendSubmissionConfirmationToApplicant(application);
        verify();
    }

    @Test
    public void shouldGetApplicationById() {
        ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
        EasyMock.expect(applicationFormDAOMock.get(234)).andReturn(application);

        replay();
        Assert.assertEquals(application, applicationsService.getApplicationById(234));
        verify();
    }

    @Test
    public void shouldGetApplicationbyApplicationNumber() {
        ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
        EasyMock.expect(applicationFormDAOMock.getApplicationByApplicationNumber("ABC")).andReturn(application);

        replay();
        Assert.assertEquals(application, applicationsService.getApplicationByApplicationNumber("ABC"));
        verify();
    }

    @Test
    public void shouldCreateAndSaveNewApplicationFormWithoutBatchDeadlineProjectOrResearchHomePage() throws ParseException {
        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());

        // one existing application, since is approved should be ignored
        final ApplicationForm existingApplicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).build();
        EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program)).andReturn(
                Lists.newArrayList(existingApplicationForm));

        EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23L);
        applicationFormDAOMock.save(EasyMock.isA(ApplicationForm.class));

        replay();
        ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser, program, null);
        verify();

        assertNotNull(returnedForm);
        assertEquals(registeredUser, returnedForm.getApplicant());
        assertEquals(program, returnedForm.getProgram());
        assertEquals("KLOP-" + thisYear + "-000024", returnedForm.getApplicationNumber());
        assertNull(returnedForm.getBatchDeadline());
    }

    @Test
    public void shouldCreateAndSaveNewApplicationFormWithBatchDeadlineProjectAndResearchHomePage() throws ParseException {
        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());

        // one existing application, since is withdrawn should be ignored
        final ApplicationForm existingApplicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.WITHDRAWN).build();
        EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program)).andReturn(
                Lists.newArrayList(existingApplicationForm));

        EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23L);
        applicationFormDAOMock.save(EasyMock.isA(ApplicationForm.class));

        replay();
        ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser, program, null);
        verify();

        assertNotNull(returnedForm);
        assertEquals(registeredUser, returnedForm.getApplicant());
        assertEquals(program, returnedForm.getProgram());
        assertEquals("KLOP-" + thisYear + "-000024", returnedForm.getApplicationNumber());
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
        ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser, program, null);
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
        ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser, program, null);

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
        ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser, program, null);
        // THEN
        verify();

        assertSame(newApplicationForm, returnedForm);
    }

    @Test
    public void shouldFastTrackApplicationByClearingTheBatchDeadline() {
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("XXXXX").batchDeadline(new Date()).build();
        EasyMock.expect(applicationFormDAOMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        replay();
        applicationsService.fastTrackApplication(form.getApplicationNumber());
        verify();
        Assert.assertNull(form.getBatchDeadline());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateAndSaveNewApplicationFormWithProject() throws ParseException {
        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        Advert advert = new AdvertBuilder().id(1).title("title").studyDuration(6).build();
        RegisteredUser primarySupervisor = new RegisteredUserBuilder().firstName("Ezio").lastName("Imbecilo").email("ezio@mail.com").id(1).build();
        RegisteredUser secondarySupervisor = new RegisteredUserBuilder().firstName("Genowefa").lastName("Pigwa").email("gienia@mail.com").id(2).build();
        Project project = new ProjectBuilder().id(1).advert(advert).program(program).primarySupervisor(primarySupervisor)
                .secondarySupervisor(secondarySupervisor).build();

        EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgramAndProject(registeredUser, program, project)).andReturn(
                Lists.<ApplicationForm> newArrayList());
        EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23L);
        applicationFormDAOMock.save(EasyMock.isA(ApplicationForm.class));
        Capture<ProgrammeDetails> programDetails = new Capture<ProgrammeDetails>();
        programmeDetailsServiceMock.save(EasyMock.capture(programDetails));

        replay();
        ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser, program, project);
        verify();

        List<SuggestedSupervisor> suggestedSupervisors = programDetails.getValue().getSuggestedSupervisors();
        assertNotNull(returnedForm);
        assertEquals(registeredUser, returnedForm.getApplicant());
        assertEquals(program, returnedForm.getProgram());
        assertEquals("KLOP-" + thisYear + "-000024", returnedForm.getApplicationNumber());
        assertNull(returnedForm.getBatchDeadline());
        assertEquals(project, returnedForm.getProject());

        Matcher<SuggestedSupervisor> primarySupervisorMatcher = allOf(hasProperty("firstname", equalTo("Ezio")), hasProperty("lastname", equalTo("Imbecilo")),
                hasProperty("email", equalTo("ezio@mail.com")));
        Matcher<SuggestedSupervisor> secondarySupervisorMatcher = allOf(hasProperty("firstname", equalTo("Genowefa")), hasProperty("lastname", equalTo("Pigwa")),
                hasProperty("email", equalTo("gienia@mail.com")));
        assertThat(suggestedSupervisors, hasItems(primarySupervisorMatcher, secondarySupervisorMatcher));
    }

    @Test
    public void shouldGetBatchDeadlineForApplication() {
        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        ApplicationForm form = new ApplicationFormBuilder().program(program).build();
        Date deadline = new Date();

        EasyMock.expect(programDAOMock.getNextClosingDateForProgram(EasyMock.eq(program), EasyMock.isA(Date.class))).andReturn(deadline);

        replay();
        Date returnedDeadline = applicationsService.getBatchDeadlineForApplication(form);
        verify();

        assertSame(deadline, returnedDeadline);
    }

}
