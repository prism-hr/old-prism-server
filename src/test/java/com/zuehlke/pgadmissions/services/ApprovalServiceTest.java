package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.Capture;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.OfferRecommendedCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;

public class ApprovalServiceTest {

    private ApprovalService approvalService;

    private ApplicationFormDAO applicationFormDAOMock;

    private ApprovalRoundDAO approvalRoundDAOMock;

    private StageDurationService stageDurationDAOMock;

    private ProgrammeDetailDAO programmeDetailDAOMock;

    private EventFactory eventFactoryMock;

    private CommentDAO commentDAOMock;

    private UserService userServiceMock;

    private SupervisorDAO supervisorDAOMock;

    private ApprovalRound approvalRound;

    private Supervisor supervisor;

    private PorticoQueueService porticoQueueServiceMock;

    private MailSendingService mailSendingServiceMock;

    private ProgramInstanceService programInstanceServiceMock;

    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Before
    public void setUp() {
        supervisor = new SupervisorBuilder().id(1).build();
        approvalRound = new ApprovalRoundBuilder().id(1).build();
        supervisorDAOMock = createMock(SupervisorDAO.class);
        applicationFormDAOMock = createMock(ApplicationFormDAO.class);
        approvalRoundDAOMock = createMock(ApprovalRoundDAO.class);
        stageDurationDAOMock = createMock(StageDurationService.class);
        programmeDetailDAOMock = createMock(ProgrammeDetailDAO.class);
        eventFactoryMock = createMock(EventFactory.class);
        porticoQueueServiceMock = createMock(PorticoQueueService.class);
        commentDAOMock = createMock(CommentDAO.class);
        userServiceMock = createMock(UserService.class);
        mailSendingServiceMock = createMock(MailSendingService.class);
        programInstanceServiceMock = createMock(ProgramInstanceService.class);
        applicationFormUserRoleService = createMock(ApplicationFormUserRoleService.class);

        approvalService = new ApprovalService(userServiceMock, applicationFormDAOMock, approvalRoundDAOMock, stageDurationDAOMock, eventFactoryMock,
                commentDAOMock, supervisorDAOMock, programmeDetailDAOMock, porticoQueueServiceMock, mailSendingServiceMock, programInstanceServiceMock,
                applicationFormUserRoleService) {
            @Override
            public ApprovalRound newApprovalRound() {
                return approvalRound;
            }

            @Override
            public Supervisor newSupervisor() {
                return supervisor;
            }
        };
    }

    @Test
    public void shouldCreateNewSupervisorInNeApprovalRoundIfLatestRoundIsNull() {
        RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm application = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().id(1).build())
                .applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();

        supervisorDAOMock.save(supervisor);
        replay(supervisorDAOMock);

        approvalService.addSupervisorInPreviousApprovalRound(application, supervisorUser);

        Assert.assertEquals(supervisorUser, supervisor.getUser());
        Assert.assertTrue(approvalRound.getSupervisors().contains(supervisor));

    }

    @Test
    public void shouldCreateNewSueprvisorInLatestAppprovalRoundIfLatestRoundIsNotNull() {
        RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().build();

        ApplicationForm application = new ApplicationFormBuilder().latestApprovalRound(latestApprovalRound).id(1).program(new ProgramBuilder().id(1).build())
                .applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();

        supervisorDAOMock.save(supervisor);
        replay(supervisorDAOMock);
        approvalService.addSupervisorInPreviousApprovalRound(application, supervisorUser);
        Assert.assertEquals(supervisorUser, supervisor.getUser());
        Assert.assertTrue(latestApprovalRound.getSupervisors().contains(supervisor));

    }

    @Test
    public void shouldSetDueDateOnApplicationUpdateFormAndSaveBoth() {

        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).build();
        applicationForm.addNotificationRecord(new NotificationRecordBuilder().id(2).notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION)
                .build());
        applicationForm.addNotificationRecord(new NotificationRecordBuilder().id(5).notificationType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER)
                .build());
        applicationForm.addNotificationRecord(new NotificationRecordBuilder().id(4).notificationType(NotificationType.APPROVAL_NOTIFICATION).build());
        applyValidSendToPorticoData(applicationForm);
        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
        approvalRoundDAOMock.save(approvalRound);
        applicationFormDAOMock.save(applicationForm);

        StateChangeEvent event = new ApprovalStateChangeEventBuilder().id(1).build();
        expect(eventFactoryMock.createEvent(approvalRound)).andReturn(event);
        applicationFormUserRoleService.validationStageCompleted(applicationForm);
        applicationFormUserRoleService.movedToApprovalStage(approvalRound);

        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock, applicationFormUserRoleService);
        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock, applicationFormUserRoleService);

        assertEquals(DateUtils.truncate(com.zuehlke.pgadmissions.utils.DateUtils.addWorkingDaysInMinutes(new Date(), 2 * 1400), Calendar.DATE),
                DateUtils.truncate(applicationForm.getDueDate(), Calendar.DATE));
        assertEquals(applicationForm, approvalRound.getApplication());
        assertEquals(approvalRound, applicationForm.getLatestApprovalRound());
        assertEquals(ApplicationFormStatus.APPROVAL, applicationForm.getStatus());
        assertEquals(1, applicationForm.getEvents().size());
        assertEquals(event, applicationForm.getEvents().get(0));
        assertNull(applicationForm.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION));
        assertNull(applicationForm.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER));
        assertNull(applicationForm.getNotificationForType(NotificationType.APPROVAL_NOTIFICATION));

    }

    @Test
    public void shouldCopyLastNotifiedForSupervisorsWhoWereAlsoInPreviousRound() throws ParseException {
        Date lastNotified = new SimpleDateFormat("dd MM yyyy").parse("05 06 2012");
        RegisteredUser repeatUser = new RegisteredUserBuilder().id(1).build();
        Supervisor repeatSupervisorOld = new SupervisorBuilder().id(1).user(repeatUser).lastNotified(lastNotified).build();
        Supervisor repeatSupervisorNew = new SupervisorBuilder().id(2).user(repeatUser).build();

        RegisteredUser nonRepeatUser = new RegisteredUserBuilder().id(2).build();
        Supervisor nonRepeatUserSupervisor = new SupervisorBuilder().id(3).user(nonRepeatUser).build();
        ApprovalRound previousApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(repeatSupervisorOld).build();

        ApprovalRound newApprovalRound = new ApprovalRoundBuilder().id(2).supervisors(repeatSupervisorNew, nonRepeatUserSupervisor).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(previousApprovalRound).status(ApplicationFormStatus.APPROVAL).id(1)
                .build();
        applyValidSendToPorticoData(applicationForm);
        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
        approvalRoundDAOMock.save(newApprovalRound);
        applicationFormDAOMock.save(applicationForm);

        commentDAOMock.save(isA(ApprovalComment.class));
        StateChangeEvent event = new ApprovalStateChangeEventBuilder().id(1).build();
        expect(eventFactoryMock.createEvent(newApprovalRound)).andReturn(event);
        
        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock, commentDAOMock);
        approvalService.moveApplicationToApproval(applicationForm, newApprovalRound);
        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock, commentDAOMock);
        
        assertNull(nonRepeatUserSupervisor.getLastNotified());
        assertEquals(lastNotified, repeatSupervisorNew.getLastNotified());
        assertSame(newApprovalRound, applicationForm.getLatestApprovalRound());

    }

    @Test
    public void shouldMoveToApprovaAndApplyApprovalComment() {
        Date date = new Date();
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
        Supervisor secondarySupervisor = new SupervisorBuilder().build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).projectAbstract("abstract").projectTitle("title").projectDescriptionAvailable(true)
                .recommendedConditionsAvailable(true).recommendedConditions("conditions").recommendedStartDate(date)
                .supervisors(primarySupervisor, secondarySupervisor).build();
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).id(1).build();
        applyValidSendToPorticoData(applicationForm);
        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
        approvalRoundDAOMock.save(approvalRound);
        applicationFormDAOMock.save(applicationForm);
        expect(userServiceMock.getCurrentUser()).andReturn(user);

        Capture<ApprovalComment> approvalCommentCapture = new Capture<ApprovalComment>();
        commentDAOMock.save(capture(approvalCommentCapture));
        applicationFormUserRoleService.movedToApprovalStage(approvalRound);

        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, userServiceMock, applicationFormUserRoleService);
        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, userServiceMock, applicationFormUserRoleService);

        ApprovalComment approvalComment = approvalCommentCapture.getValue();

        assertTrue(approvalComment.getProjectDescriptionAvailable());
        assertEquals("abstract", approvalComment.getProjectAbstract());
        assertEquals("title", approvalComment.getProjectTitle());
        assertEquals("conditions", approvalComment.getRecommendedConditions());
        assertTrue(approvalComment.getRecommendedConditionsAvailable());
        assertEquals(date, approvalComment.getRecommendedStartDate());
        assertEquals("", approvalComment.getComment());
        assertEquals(CommentType.APPROVAL, approvalComment.getType());
        assertSame(applicationForm, approvalComment.getApplication());
        assertSame(user, approvalComment.getUser());
        assertSame(primarySupervisor, approvalComment.getSupervisor());
        assertSame(secondarySupervisor, approvalComment.getSecondarySupervisor());

    }

    @Test
    public void shouldMoveToApprovalIfInApproval() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).id(1).build();
        applyValidSendToPorticoData(applicationForm);
        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
        approvalRoundDAOMock.save(approvalRound);
        applicationFormDAOMock.save(applicationForm);
        commentDAOMock.save(isA(ApprovalComment.class));
        applicationFormUserRoleService.movedToApprovalStage(approvalRound);
        
        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, applicationFormUserRoleService);
        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, applicationFormUserRoleService);

    }

    @Test
    public void shouldFailIfApplicationInInvalidState() {
        ApplicationFormStatus[] values = ApplicationFormStatus.values();
        for (ApplicationFormStatus status : values) {
            if (status != ApplicationFormStatus.VALIDATION && status != ApplicationFormStatus.APPROVAL && status != ApplicationFormStatus.REVIEW
                    && status != ApplicationFormStatus.INTERVIEW) {
                ApplicationForm application = new ApplicationFormBuilder().id(3).status(status).build();
                boolean threwException = false;
                try {
                    approvalService.moveApplicationToApproval(application, new ApprovalRoundBuilder().id(1).build());
                } catch (IllegalStateException ise) {
                    if (ise.getMessage().equals("Application in invalid status: '" + status + "'!")) {
                        threwException = true;
                    }
                }
                Assert.assertTrue(threwException);
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfApplicationHasNoReferencesForSendingToPortico() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).id(1).build();
        applyValidSendToPorticoData(applicationForm);
        for (Referee referee : applicationForm.getReferees()) {
            referee.setSendToUCL(false);
        }
        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfApplicationHasNoQualicifacionsForSendingToPorticoAndNoExplanation() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).id(1).build();
        applyValidSendToPorticoData(applicationForm);
        for (Qualification qualifications : applicationForm.getQualifications()) {
            qualifications.setSendToUCL(false);
        }
        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
    }

    @Test
    public void shouldMoveToApprovalIfInApplicationWithNoQualificationsButExplanationProvided() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).missingQualificationExplanation("explanation").build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).id(1).build();
        applyValidSendToPorticoData(applicationForm);
        for (Qualification qualifications : applicationForm.getQualifications()) {
            qualifications.setSendToUCL(false);
        }
        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
        approvalRoundDAOMock.save(approvalRound);
        applicationFormDAOMock.save(applicationForm);
        commentDAOMock.save(isA(ApprovalComment.class));
        applicationFormUserRoleService.movedToApprovalStage(approvalRound);
        
        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, applicationFormUserRoleService);
        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, applicationFormUserRoleService);

    }

    @Test
    public void shouldConfirmSupervision() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(2).build();
        RegisteredUser user = new RegisteredUserBuilder().id(3).email("a.user@ucl.co.uk").build();
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
        Supervisor secondarySupervisor = new SupervisorBuilder().isPrimary(false).user(user).build();

        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).missingQualificationExplanation("explanation")
                .supervisors(primarySupervisor, secondarySupervisor).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).id(1).latestApprovalRound(approvalRound).build();
        approvalRound.setApplication(applicationForm);

        Date startDate = Calendar.getInstance().getTime();

        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
        confirmSupervisionDTO.setConfirmedSupervision(true);
        confirmSupervisionDTO.setProjectTitle("title");
        confirmSupervisionDTO.setProjectAbstract("abstract");
        confirmSupervisionDTO.setRecommendedStartDate(startDate);
        confirmSupervisionDTO.setRecommendedConditionsAvailable(true);
        confirmSupervisionDTO.setRecommendedConditions("conditions");
        confirmSupervisionDTO.setSecondarySupervisorEmail("a.user@ucl.co.uk");

        Capture<SupervisionConfirmationComment> supervisionConfirmationCommentcapture = new Capture<SupervisionConfirmationComment>();
        commentDAOMock.save(capture(supervisionConfirmationCommentcapture));
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);

        replay(commentDAOMock, userServiceMock);
        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        verify(commentDAOMock, userServiceMock);

        assertTrue(primarySupervisor.getConfirmedSupervision());
        SupervisionConfirmationComment comment = supervisionConfirmationCommentcapture.getValue();

        // assert comment
        assertSame(applicationForm, comment.getApplication());
        assertEquals("", comment.getComment());
        assertNotNull(comment.getDate());
        assertEquals("abstract", comment.getProjectAbstract());
        assertEquals("title", comment.getProjectTitle());
        assertEquals("conditions", comment.getRecommendedConditions());
        assertTrue(comment.getRecommendedConditionsAvailable());
        assertEquals(startDate, comment.getRecommendedStartDate());
        assertSame(primarySupervisor, comment.getSupervisor());
        assertSame(secondarySupervisor, comment.getSecondarySupervisor());
        assertEquals(CommentType.SUPERVISION_CONFIRMATION, comment.getType());
        assertSame(currentUser, comment.getUser());

        // assert ApprovalRound
        assertTrue(approvalRound.getProjectDescriptionAvailable());
        assertEquals("abstract", approvalRound.getProjectAbstract());
        assertEquals("title", approvalRound.getProjectTitle());
        assertEquals("conditions", approvalRound.getRecommendedConditions());
        assertTrue(approvalRound.getRecommendedConditionsAvailable());
        assertEquals(startDate, approvalRound.getRecommendedStartDate());
        assertSame(primarySupervisor, approvalRound.getPrimarySupervisor());
        assertSame(secondarySupervisor, approvalRound.getSecondarySupervisor());
    }

    @Test
    public void shouldModifySecondarySupervision() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(2).build();
        RegisteredUser user = new RegisteredUserBuilder().id(3).email("a.user@ucl.co.uk").build();
        RegisteredUser anotherUser = new RegisteredUserBuilder().id(3).email("a.n.other@ucl.co.uk").build();
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
        Supervisor secondarySupervisor = new SupervisorBuilder().isPrimary(false).user(user).build();

        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).missingQualificationExplanation("explanation")
                .supervisors(primarySupervisor, secondarySupervisor).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).id(1).latestApprovalRound(approvalRound).build();

        Date startDate = Calendar.getInstance().getTime();

        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
        confirmSupervisionDTO.setConfirmedSupervision(true);
        confirmSupervisionDTO.setProjectTitle("title");
        confirmSupervisionDTO.setProjectAbstract("abstract");
        confirmSupervisionDTO.setRecommendedStartDate(startDate);
        confirmSupervisionDTO.setRecommendedConditionsAvailable(true);
        confirmSupervisionDTO.setRecommendedConditions("conditions");
        confirmSupervisionDTO.setSecondarySupervisorEmail("a.n.other@ucl.co.uk");

        Capture<SupervisionConfirmationComment> supervisionConfirmationCommentcapture = new Capture<SupervisionConfirmationComment>();
        commentDAOMock.save(capture(supervisionConfirmationCommentcapture));
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        expect(userServiceMock.getUserByEmail("a.n.other@ucl.co.uk")).andReturn(anotherUser);

        replay(commentDAOMock, userServiceMock);
        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        verify(commentDAOMock, userServiceMock);

        assertThat(approvalRound.getSupervisors(), not(hasItems(secondarySupervisor)));
        assertEquals(anotherUser, approvalRound.getSecondarySupervisor().getUser());
    }

    @Test
    public void shouldDeclineSupervisionAndRestartApprovalRound() {
        RegisteredUser user1 = new RegisteredUserBuilder().firstName("John Paul").lastName("Jones").build();
        RegisteredUser user2 = new RegisteredUserBuilder().firstName("Dave").lastName("Jones").email("d.jones@ucl.ac.uk").build();
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).user(user1).build();
        Supervisor secondarySupervisor = new SupervisorBuilder().isPrimary(false).user(user2).build();

        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).missingQualificationExplanation("explanation").projectDescriptionAvailable(false)
                .supervisors(primarySupervisor, secondarySupervisor).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).id(1).latestApprovalRound(approvalRound).build();
        approvalRound.setApplication(applicationForm);

        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
        confirmSupervisionDTO.setConfirmedSupervision(false);
        confirmSupervisionDTO.setDeclinedSupervisionReason("reason");
        confirmSupervisionDTO.setSecondarySupervisorEmail("d.jones@ucl.ac.uk");

        Capture<SupervisionConfirmationComment> supervisionConfirmationCommentcapture = new Capture<SupervisionConfirmationComment>();
        commentDAOMock.save(capture(supervisionConfirmationCommentcapture));
        expect(userServiceMock.getCurrentUser()).andReturn(user1);

        replay(commentDAOMock, userServiceMock);
        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        verify(commentDAOMock, userServiceMock);

        assertFalse(primarySupervisor.getConfirmedSupervision());
        assertEquals("reason", primarySupervisor.getDeclinedSupervisionReason());

        SupervisionConfirmationComment comment = supervisionConfirmationCommentcapture.getValue();
        assertSame(applicationForm, comment.getApplication());
        assertEquals("", comment.getComment());
        assertNotNull(comment.getDate());
        assertSame(primarySupervisor, comment.getSupervisor());
        assertEquals(CommentType.SUPERVISION_CONFIRMATION, comment.getType());
        assertSame(user1, comment.getUser());

        assertFalse(approvalRound.getProjectDescriptionAvailable());
    }

    @Test
    public void shouldSaveReviewRound() {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
        approvalRoundDAOMock.save(approvalRound);
        replay(approvalRoundDAOMock);
        approvalService.save(approvalRound);
        verify(approvalRoundDAOMock);
    }

    @Test
    public void shouldMoveApplicationToApprovedWithComment() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        replay(userServiceMock);

        Date startDate = new Date();
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
        ProgramInstance instance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 1))
                .enabled(true).studyOption("1", "full").build();
        Program program = new ProgramBuilder().id(1).instances(instance).enabled(true).build();
        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().supervisors(new Supervisor()).build();
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).program(program).id(2)
                .programmeDetails(programmeDetails).latestApprovalRound(latestApprovalRound).build();
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
        OfferRecommendedComment offerRecommendedComment = new OfferRecommendedCommentBuilder().supervisors(primarySupervisor, supervisor).build();

        applicationFormDAOMock.save(application);
        commentDAOMock.save(offerRecommendedComment);

        StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
        expect(eventFactoryMock.createEvent(ApplicationFormStatus.APPROVED)).andReturn(event);
        expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application)).andReturn(true);

        replay(applicationFormDAOMock, eventFactoryMock, commentDAOMock, programInstanceServiceMock);
        approvalService.moveToApproved(application, offerRecommendedComment);
        verify(applicationFormDAOMock, eventFactoryMock, commentDAOMock, programInstanceServiceMock);

        assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());

        assertEquals(1, application.getEvents().size());
        assertEquals(event, application.getEvents().get(0));

        assertSame(application, offerRecommendedComment.getApplication());
        assertEquals("", offerRecommendedComment.getComment());
        assertEquals(CommentType.OFFER_RECOMMENDED_COMMENT, offerRecommendedComment.getType());
        assertSame(currentUser, offerRecommendedComment.getUser());
        assertThat(latestApprovalRound.getSupervisors(), Matchers.contains(primarySupervisor, supervisor));
    }

    @Test
    public void shouldChangeStartDate() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        replay(userServiceMock);

        Date startDate = DateUtils.addDays(new Date(), 1);
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
        ProgramInstance instanceDisabled = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 4))
                .enabled(false).studyOption("1", "full").build();
        ProgramInstance instanceEnabled = new ProgramInstanceBuilder().applicationStartDate(DateUtils.addDays(startDate, 3))
                .applicationDeadline(DateUtils.addDays(startDate, 4)).enabled(true).studyOption("1", "full").build();
        Program program = new ProgramBuilder().id(1).enabled(true).instances(instanceDisabled, instanceEnabled).build();
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).program(program).id(2)
                .programmeDetails(programmeDetails).latestApprovalRound(new ApprovalRound()).build();
        OfferRecommendedComment offerRecommendedComment = new OfferRecommendedComment();

        programmeDetailDAOMock.save(programmeDetails);
        applicationFormDAOMock.save(application);
        commentDAOMock.save(offerRecommendedComment);

        StateChangeEvent event = new StateChangeEventBuilder().id(1).build();

        expect(eventFactoryMock.createEvent(ApplicationFormStatus.APPROVED)).andReturn(event);
        expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application)).andReturn(false);
        expect(programInstanceServiceMock.getEarliestPossibleStartDate(application)).andReturn(DateUtils.addDays(startDate, 3));

        replay(applicationFormDAOMock, eventFactoryMock, commentDAOMock, programmeDetailDAOMock, programInstanceServiceMock);
        approvalService.moveToApproved(application, offerRecommendedComment);
        verify(applicationFormDAOMock, eventFactoryMock, commentDAOMock, programmeDetailDAOMock, programInstanceServiceMock);

        assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());
        assertEquals(programmeDetails.getStartDate(), instanceEnabled.getApplicationStartDate());

        assertEquals(1, application.getEvents().size());
        assertEquals(event, application.getEvents().get(0));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOmMoveToApprovedIfApplicationNotInApproval() {
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).id(2).build();
        replay(applicationFormDAOMock, eventFactoryMock, commentDAOMock);
        approvalService.moveToApproved(application, null);
        verify(applicationFormDAOMock, commentDAOMock);
    }

    private void applyValidSendToPorticoData(ApplicationForm applicationForm) {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();

        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).build();
        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(true).build();

        user1.getReferees().add(referee1);
        user2.getReferees().add(referee2);

        Document document1 = new DocumentBuilder().id(1).build();

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification2 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();

        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();

        referee1.setReference(referenceComment1);
        referee2.setReference(referenceComment2);

        applicationForm.setReferees(Arrays.asList(referee1, referee2));
        applicationForm.setApplicationComments(Arrays.<Comment> asList(referenceComment1, referenceComment2));
        applicationForm.setQualifications(Arrays.asList(qualification1, qualification2));
    }

}
