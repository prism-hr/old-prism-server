package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPLICATION_SUBMIT_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.EXPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.IMPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEWER_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.MOVED_TO_APPROVED_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.MOVED_TO_INTERVIEW_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_PASSWORD_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REGISTRATION_CONFIRMATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REJECTED_NOTIFICATION;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class MailSendingServiceTest {

    protected static final String SAMPLE_REJECTION_REASON = "You ain't goog enough";

    protected static final String SAMPLE_PROGRAM_TITLE = "MRes Security Science";

    protected static final String SAMPLE_APPLICATION_NUMBER = "TMRSECSING01-2013-000004";

    protected static final String SAMPLE_ADMIN2_EMAIL_ADDRESS = "admin2@mail.com";

    protected static final String SAMPLE_ADMIN1_EMAIL_ADDRESS = "admin1@mail.com";

    protected static final String SAMPLE_APPLICANT_SURNAME = "Capatonda";

    protected static final String SAMPLE_APPLICANT_NAME = "Maccio";

    protected static final String SAMPLE_APPLICANT_EMAIL_ADDRESS = "capatonda@mail.com";

    protected static final String HOST = "http://localhost:8080";

    protected static final String UCL_PROSPECTUS_LINK = "http://www.ucl.ac.uk/prospective-students/graduate-study";

    protected static final String SERVICE_OFFER = "5 working days";

    protected MailSendingService service;

    protected MailSender mockMailSender;

    protected ConfigurationService configurationServiceMock;

    protected ApplicationFormDAO applicationFormDAOMock;

    protected UserDAO userDAOMock;

    protected RoleDAO roleDAOMock;

    protected RefereeDAO refereeDAOMock;

    protected EncryptionUtils encryptionUtilsMock;

    @Before
    public void setup() {
        userDAOMock = createMock(UserDAO.class);
        roleDAOMock = createMock(RoleDAO.class);
        refereeDAOMock = createMock(RefereeDAO.class);
        encryptionUtilsMock = createMock(EncryptionUtils.class);
        mockMailSender = createMock(MailSender.class);
        configurationServiceMock = createMock(ConfigurationService.class);
        applicationFormDAOMock = createMock(ApplicationFormDAO.class);
        service = new MailSendingService(mockMailSender, configurationServiceMock, applicationFormDAOMock, userDAOMock, roleDAOMock, refereeDAOMock,
                encryptionUtilsMock, HOST, SERVICE_OFFER, UCL_PROSPECTUS_LINK);
    }

    @Test
    public void sendExportErrorMessageShouldSuccessfullySendMessage() throws Exception {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
        String messageCode = "message_code";
        Date timestamp = new Date();
        Map<String, Object> model1 = new HashMap<String, Object>();
        model1.put("user", user1);
        model1.put("message", messageCode);
        model1.put("time", timestamp);
        model1.put("host", HOST);
        Map<String, Object> model2 = new HashMap<String, Object>();
        model2.putAll(model1);
        model2.put("user", user2);

        expect(mockMailSender.resolveSubject(EXPORT_ERROR)).andReturn("UCL Prism to Portico Export Error");

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
        expectLastCall().times(2);

        replay(mockMailSender);
        service.sendExportErrorMessage(asList(user1, user2), messageCode, timestamp);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValues().get(0);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals("UCL Prism to Portico Export Error", message.getSubjectCode());
        assertModelEquals(model1, message.getModel());

        message = messageCaptor.getValues().get(1);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 2, message.getTo().get(0).getId());
        assertEquals("UCL Prism to Portico Export Error", message.getSubjectCode());
        assertModelEquals(model2, message.getModel());
    }

    @Test
    public void sendImportErrorMessageShouldSuccessfullySendMessage() throws Exception {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
        String messageCode = "message_code";
        Date timestamp = new Date();
        Map<String, Object> model1 = new HashMap<String, Object>();
        model1.put("user", user1);
        model1.put("message", messageCode);
        model1.put("time", timestamp);
        model1.put("host", HOST);
        Map<String, Object> model2 = new HashMap<String, Object>();
        model2.putAll(model1);
        model2.put("user", user2);

        expect(mockMailSender.resolveSubject(IMPORT_ERROR)).andReturn("UCL Prism to Portico Import Error");

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
        expectLastCall().times(2);

        replay(mockMailSender);
        service.sendImportErrorMessage(asList(user1, user2), messageCode, timestamp);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValues().get(0);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals("UCL Prism to Portico Import Error", message.getSubjectCode());
        assertModelEquals(model1, message.getModel());

        message = messageCaptor.getValues().get(1);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 2, message.getTo().get(0).getId());
        assertEquals("UCL Prism to Portico Import Error", message.getSubjectCode());
        assertModelEquals(model2, message.getModel());
    }

    @Test
    public void sendRefereeRequestShouldSuccessfullySendMessage() throws Exception {
        service = new MailSendingService(mockMailSender, configurationServiceMock, applicationFormDAOMock, userDAOMock, roleDAOMock, refereeDAOMock,
                encryptionUtilsMock, HOST, SERVICE_OFFER, UCL_PROSPECTUS_LINK) {
            @Override
            protected RegisteredUser processRefereeAndGetAsUser(final Referee referee) {
                return null;
            }
        };

        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        Referee referee = new RefereeBuilder().id(0).user(user).build();
        String adminMails = SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS;
        ApplicationForm form = getSampleApplicationForm();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("referee", referee);
        model.put("adminsEmails", adminMails);
        model.put("applicant", form.getApplicant());
        model.put("application", form);
        model.put("programme", form.getProgrammeDetails());
        model.put("host", HOST);

        String subjectToReturn = SAMPLE_APPLICANT_NAME + " " + SAMPLE_APPLICANT_SURNAME + " " + "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL "
                + SAMPLE_PROGRAM_TITLE + " - Reference Request";

        expect(
                mockMailSender.resolveSubject(REFEREE_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender);
        service.sendReferenceRequest(asList(referee), form);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());

        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void sendRefereeRequestShouldSuccessfullySendMessageWithNoApplicant() throws Exception {
        service = new MailSendingService(mockMailSender, configurationServiceMock, applicationFormDAOMock, userDAOMock, roleDAOMock, refereeDAOMock,
                encryptionUtilsMock, HOST, SERVICE_OFFER, UCL_PROSPECTUS_LINK) {
            @Override
            protected RegisteredUser processRefereeAndGetAsUser(final Referee referee) {
                return null;
            }
        };

        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        Referee referee = new RefereeBuilder().id(0).user(user).build();
        String adminMails = SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS;
        ApplicationForm form = getSampleApplicationForm();
        form.setApplicant(null);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("referee", referee);
        model.put("adminsEmails", adminMails);
        model.put("applicant", form.getApplicant());
        model.put("application", form);
        model.put("programme", form.getProgrammeDetails());
        model.put("host", HOST);

        expect(mockMailSender.resolveSubject(REFEREE_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE)).andReturn(
                "[2] [3] Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Reference Request");

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender);
        service.sendReferenceRequest(asList(referee), form);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());

        assertEquals("[2] [3] Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Reference Request", message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void sendResetPasswordShouldSuccessfullySendMessage() throws Exception {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        String newPassword = "password";
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("newPassword", newPassword);
        model.put("host", HOST);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        expect(mockMailSender.resolveSubject(NEW_PASSWORD_CONFIRMATION)).andReturn("New Password for UCL Prism");

        replay(mockMailSender);
        service.sendResetPasswordMessage(user, newPassword);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals("New Password for UCL Prism", message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void sendConfirmationEmailToRegisteringUserShouldSuccessfullySendMessage() throws Exception {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("host", HOST);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        expect(mockMailSender.resolveSubject(REGISTRATION_CONFIRMATION)).andReturn("Your Registration for UCL Prism");

        replay(mockMailSender);
        service.sendRegistrationConfirmation(user);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals("Your Registration for UCL Prism", message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendSubmissionConfirmationToApplicant() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(null);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE;
        expect(mockMailSender.resolveSubject(APPLICATION_SUBMIT_CONFIRMATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendSubmissionConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendSubmissionConfirmationToApplicantAndAddReasonToModel() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.REJECTED);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());
        model.put("reason", form.getRejection().getRejectionReason());

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE;
        expect(mockMailSender.resolveSubject(APPLICATION_SUBMIT_CONFIRMATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendSubmissionConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendSubmissionConfirmationToApplicantAndAddProspectusLinkToModel() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.REJECTED);
        form.getRejection().setIncludeProspectusLink(true);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());
        model.put("reason", form.getRejection().getRejectionReason());
        model.put("prospectusLink", UCL_PROSPECTUS_LINK);

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE;
        expect(mockMailSender.resolveSubject(APPLICATION_SUBMIT_CONFIRMATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendSubmissionConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendRejectionConfirmationToUser() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(null);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - "
                + form.getOutcomeOfStage().displayValue() + " Outcome";
        expect(
                mockMailSender.resolveSubject(REJECTED_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendRejectionConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendRejectionConfirmationToUserAndAddReasonToModel() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.REJECTED);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());
        model.put("reason", form.getRejection().getRejectionReason());

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - "
                + form.getOutcomeOfStage().displayValue() + " Outcome";
        expect(
                mockMailSender.resolveSubject(REJECTED_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendRejectionConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendRejectionConfirmationToUserAndAddProspectusLinkToModel() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.REJECTED);
        form.getRejection().setIncludeProspectusLink(true);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());
        model.put("reason", form.getRejection().getRejectionReason());
        model.put("prospectusLink", UCL_PROSPECTUS_LINK);

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - "
                + form.getOutcomeOfStage().displayValue() + " Outcome";
        expect(
                mockMailSender.resolveSubject(REJECTED_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendRejectionConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendApprovedNotification() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(null);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Approval Outcome";
        expect(
                mockMailSender.resolveSubject(MOVED_TO_APPROVED_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendApprovedNotification(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendApprovedNotificationAndAddReasonToModel() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.REJECTED);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());
        model.put("reason", form.getRejection().getRejectionReason());

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Approval Outcome";
        expect(
                mockMailSender.resolveSubject(MOVED_TO_APPROVED_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendApprovedNotification(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendApprovedNotificationAndAddProspectusLinkToModel() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.REJECTED);
        form.getRejection().setIncludeProspectusLink(true);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());
        model.put("reason", form.getRejection().getRejectionReason());
        model.put("prospectusLink", UCL_PROSPECTUS_LINK);

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Approval Outcome";
        expect(
                mockMailSender.resolveSubject(MOVED_TO_APPROVED_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendApprovedNotification(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendInterviewConfirmationToInterviewers() {
        ApplicationForm form = getSampleApplicationForm();
        Interview interview = new InterviewBuilder().application(form).build();
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(user1).interview(interview).build();
        Interviewer interviewer2 = new InterviewerBuilder().user(user2).interview(interview).build();

        Map<String, Object> model1 = new HashMap<String, Object>();
        model1.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model1.put("interviewer", interviewer1);
        model1.put("application", form);
        model1.put("applicant", form.getApplicant());
        model1.put("host", HOST);
        Map<String, Object> model2 = new HashMap<String, Object>();
        model2.putAll(model1);
        model2.put("interviewer", interviewer2);

        String subjectToReturn = SAMPLE_APPLICANT_NAME + " " + SAMPLE_APPLICANT_SURNAME + " Application " + SAMPLE_APPLICATION_NUMBER + " for UCL "
                + SAMPLE_PROGRAM_TITLE + " - Interview Confirmation";
        expect(
                mockMailSender.resolveSubject(INTERVIEWER_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn).times(2);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
        expectLastCall().times(2);

        replay(mockMailSender);
        service.sendInterviewConfirmationToInterviewers(asList(interviewer1, interviewer2));
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValues().get(0);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model1, message.getModel());

        message = messageCaptor.getValues().get(1);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 2, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model2, message.getModel());
    }

    @Test
    public void shouldSendInterviewConfirmationToApplicant() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(null);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Interview Confirmation";
        expect(
                mockMailSender.resolveSubject(MOVED_TO_INTERVIEW_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendInterviewConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendInterviewConfirmationToApplicantAndAddReasonToModel() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.REJECTED);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());
        model.put("reason", form.getRejection().getRejectionReason());

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Interview Confirmation";
        expect(
                mockMailSender.resolveSubject(MOVED_TO_INTERVIEW_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendInterviewConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendInterviewConfirmationToApplicantAndAddProspectusLinkToModel() {
        Person person1 = new PersonBuilder().email("person1@mail.com").firstname("Ivo").lastname("avido").build();
        Person person2 = new PersonBuilder().email("person2@mail.com").firstname("Ektor").lastname("Baboden").build();
        List<Person> registryUsers = asList(person1, person2);
        ApplicationForm form = getSampleApplicationForm();
        form.setStatus(ApplicationFormStatus.REJECTED);
        form.getRejection().setIncludeProspectusLink(true);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("registryContacts", registryUsers);
        model.put("host", HOST);
        model.put("admissionOfferServiceLevel", SERVICE_OFFER);
        model.put("previousStage", form.getOutcomeOfStage());
        model.put("reason", form.getRejection().getRejectionReason());
        model.put("prospectusLink", UCL_PROSPECTUS_LINK);

        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);

        String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Interview Confirmation";
        expect(
                mockMailSender.resolveSubject(MOVED_TO_INTERVIEW_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME, form.getOutcomeOfStage().displayValue())).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender, configurationServiceMock);
        service.sendInterviewConfirmationToApplicant(form);
        verify(mockMailSender, configurationServiceMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
    }

    @Test
    public void shouldSendInterviewVoteNotificationToInterviewerParticipants() {
        ApplicationForm form = getSampleApplicationForm();
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).build();
        InterviewParticipant participant1 = new InterviewParticipantBuilder().user(user1).build();
        InterviewParticipant participant2 = new InterviewParticipantBuilder().user(user2).build();
        Interview interview = new InterviewBuilder().application(form).participants(participant1, participant2).build();

        Map<String, Object> model1 = new HashMap<String, Object>();
        model1.put("adminsEmails", SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS);
        model1.put("participant", participant1);
        model1.put("application", form);
        model1.put("host", HOST);
        Map<String, Object> model2 = new HashMap<String, Object>();
        model2.putAll(model1);
        model2.put("participant", participant2);

        String subjectToReturn = SAMPLE_APPLICANT_NAME + " " + SAMPLE_APPLICANT_SURNAME + " Application " + SAMPLE_APPLICATION_NUMBER + " for UCL "
                + SAMPLE_PROGRAM_TITLE + " - Interview Confirmation";
        expect(
                mockMailSender.resolveSubject(EmailTemplateName.INTERVIEW_VOTE_NOTIFICATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE,
                        SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
        expectLastCall().times(2);

        replay(mockMailSender);
        service.sendInterviewVoteNotificationToInterviewerParticipants(interview);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValues().get(0);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model1, message.getModel());

        message = messageCaptor.getValues().get(1);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 2, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model2, message.getModel());
    }

    @Test
    public void shouldSendInterviewVoteConfirmationToAdministrators() {
        ApplicationForm form = getSampleApplicationForm();
        RegisteredUser admin1 = form.getProgram().getAdministrators().get(0);
        RegisteredUser admin2 = form.getProgram().getAdministrators().get(1);
        InterviewParticipant participant1 = new InterviewParticipantBuilder().user(admin1).build();
        Interview interview = new InterviewBuilder().application(form).participants(participant1).build();
        participant1.setInterview(interview);

        Map<String, Object> model1 = new HashMap<String, Object>();
        model1.put("administrator", admin2);
        model1.put("application", form);
        model1.put("participant", participant1);
        model1.put("host", HOST);

        String subjectToReturn = SAMPLE_APPLICANT_NAME + " " + SAMPLE_APPLICANT_SURNAME + " Application " + SAMPLE_APPLICATION_NUMBER + " for UCL "
                + SAMPLE_PROGRAM_TITLE + " - Interview Confirmation";
        expect(
                mockMailSender.resolveSubject(EmailTemplateName.INTERVIEW_VOTE_CONFIRMATION, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE,
                        SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender);
        service.sendInterviewVoteConfirmationToAdministrators(participant1);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValues().get(0);
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 3, message.getTo().get(0).getId());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model1, message.getModel());
    }

    @Test
    public void shouldSendOpportunityRequestOutcome() {
        RegisteredUser author = new RegisteredUserBuilder().firstName("Franek").build();
        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(author, null).build();
        OpportunityRequestComment comment = OpportunityRequestCommentBuilder.aOpportunityRequestComment(null, opportunityRequest).build();

        Map<String, Object> model1 = new HashMap<String, Object>();
        model1.put("user", author);
        model1.put("comment", comment);
        model1.put("host", HOST);

        expect(mockMailSender.resolveSubject(EmailTemplateName.OPPORTUNITY_REQUEST_OUTCOME)).andReturn("ss1");

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        replay(mockMailSender);
        service.sendOpportunityRequestOutcome(comment);
        verify(mockMailSender);

        PrismEmailMessage message = messageCaptor.getValues().get(0);
        assertThat(message.getTo(), contains(author));
        assertEquals(1, message.getTo().size());
        assertEquals("ss1", message.getSubjectCode());
        assertModelEquals(model1, message.getModel());
    }

    protected void assertModelEquals(Map<String, Object> expected, Map<String, Object> actual) {
        assertEquals("The size of the expected and actual models don't match", expected.size(), actual.size());
        for (Map.Entry<String, Object> entry : expected.entrySet()) {
            assertTrue("Model doesn't contain key: " + entry.getKey(), actual.containsKey(entry.getKey()));
            assertEquals("Expected: " + entry.getValue() + " but was: " + actual.get(entry.getKey()), entry.getValue(), actual.get(entry.getKey()));
        }
    }

    protected ApplicationForm getSampleApplicationForm() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(1).email(SAMPLE_APPLICANT_EMAIL_ADDRESS).firstName(SAMPLE_APPLICANT_NAME)
                .lastName(SAMPLE_APPLICANT_SURNAME).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(2).email(SAMPLE_ADMIN1_EMAIL_ADDRESS).build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(3).email(SAMPLE_ADMIN2_EMAIL_ADDRESS).build();
        Program program = new ProgramBuilder().id(4).title(SAMPLE_PROGRAM_TITLE).administrators(admin1, admin2).build();
        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(5).build();
        RejectReason reason = new RejectReasonBuilder().text(SAMPLE_REJECTION_REASON).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(6).status(ApplicationFormStatus.APPROVED)
                .rejection(new RejectionBuilder().rejectionReason(reason).includeProspectusLink(false).build()).applicant(applicant)
                .programmeDetails(programDetails).applicationNumber(SAMPLE_APPLICATION_NUMBER).program(program).build();
        return applicationForm;

    }

}
