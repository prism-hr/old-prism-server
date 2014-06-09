package com.zuehlke.pgadmissions.services;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.mail.NotificationService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApprovalServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationDAO applicationDAO;

    @Mock
    @InjectIntoByType
    private CommentService commentService;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private NotificationService mailSendingService;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceService;

    @Mock
    @InjectIntoByType
    private ApplicationService applicationsService;
    
    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;
    
    @TestedObject
    private ApprovalService service;

//    @Test
//    public void shouldSetDueDateOnApplicationUpdateFormAndSaveBoth() {
//
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().build();
//        StateChangeComment stateChangeComment = new StateChangeComment();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().comments(stateChangeComment).status(new State().withId(ApplicationFormStatus.VALIDATION)).id(1).build();
//        applyValidSendToPorticoData(applicationForm);
//        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
//                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
//        approvalRoundDAOMock.save(approvalRound);
//        applicationFormDAOMock.save(applicationForm);
//
//        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(null).times(2);
//
//        StateChangeEvent event = new ApprovalStateChangeEventBuilder().id(1).build();
//        expect(eventFactoryMock.createEvent(approvalRound)).andReturn(event);
//        applicationFormDAOMock.save(applicationForm);
//        applicationFormUserRoleService.validationStageCompleted(applicationForm);
//        applicationFormUserRoleService.movedToApprovalStage(approvalRound);
//        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, null, ApplicationUpdateScope.ALL_USERS);
//
//        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock, applicationFormUserRoleService, userServiceMock);
//        approvalService.moveApplicationToApproval(applicationForm, approvalRound, userServiceMock.getCurrentUser());
//        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock, applicationFormUserRoleService, userServiceMock);
//
//        assertEquals(DateUtils.truncate(com.zuehlke.pgadmissions.utils.DateUtils.addWorkingDaysInMinutes(new Date(), 2 * 1400), Calendar.DATE),
//                DateUtils.truncate(applicationForm.getDueDate(), Calendar.DATE));
//        assertEquals(applicationForm, approvalRound.getApplication());
//        assertEquals(approvalRound, applicationForm.getLatestApprovalRound());
//        assertEquals(ApplicationFormStatus.APPROVAL, applicationForm.getStatus());
//        assertEquals(1, applicationForm.getEvents().size());
//        assertEquals(event, applicationForm.getEvents().get(0));
//    }
//
//    @Test
//    public void shouldMoveToApprovaAndApplyApprovalComment() {
//        Date date = new Date();
//        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
//        Supervisor secondarySupervisor = new SupervisorBuilder().build();
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).projectAbstract("abstract").projectTitle("title").projectDescriptionAvailable(true)
//                .recommendedConditionsAvailable(true).recommendedConditions("conditions").recommendedStartDate(date)
//                .supervisors(primarySupervisor, secondarySupervisor).build();
//        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.INTERVIEW)).id(1).build();
//        applyValidSendToPorticoData(applicationForm);
//        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
//                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
//        approvalRoundDAOMock.save(approvalRound);
//        applicationFormDAOMock.save(applicationForm);
//
//        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user).once();
//
//        Capture<ApprovalComment> approvalCommentCapture = new Capture<ApprovalComment>();
//        commentDAOMock.save(capture(approvalCommentCapture));
//        applicationFormUserRoleService.movedToApprovalStage(approvalRound);
//        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
//
//        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, userServiceMock, applicationFormUserRoleService);
//        approvalService.moveApplicationToApproval(applicationForm, approvalRound, user);
//        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, userServiceMock, applicationFormUserRoleService);
//
//        Comment approvalComment = approvalCommentCapture.getValue();
//
//        assertTrue(approvalComment.getProjectDescriptionAvailable());
//        assertEquals("abstract", approvalComment.getProjectAbstract());
//        assertEquals("title", approvalComment.getProjectTitle());
//        assertEquals("conditions", approvalComment.getRecommendedConditions());
//        assertTrue(approvalComment.getRecommendedConditionsAvailable());
//        assertEquals(date, approvalComment.getRecommendedStartDate());
//        assertEquals("", approvalComment.getComment());
//        assertEquals(CommentType.APPROVAL, approvalComment.getType());
//        assertSame(applicationForm, approvalComment.getApplication());
//        assertSame(user, approvalComment.getUser());
//        assertSame(primarySupervisor, approvalComment.getSupervisor());
//        assertSame(secondarySupervisor, approvalComment.getSecondarySupervisor());
//
//    }
//
//    @Test
//    public void shouldMoveToApprovalIfInApproval() {
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.APPROVAL)).id(1).build();
//        applyValidSendToPorticoData(applicationForm);
//        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
//                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
//        approvalRoundDAOMock.save(approvalRound);
//        applicationFormDAOMock.save(applicationForm);
//        commentDAOMock.save(isA(ApprovalComment.class));
//        applicationFormUserRoleService.movedToApprovalStage(approvalRound);
//        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, null, ApplicationUpdateScope.ALL_USERS);
//
//        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, applicationFormUserRoleService);
//        approvalService.moveApplicationToApproval(applicationForm, approvalRound, null);
//        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, applicationFormUserRoleService);
//    }
//
//    @Test
//    public void shouldFailIfApplicationInInvalidState() {
//        RegisteredUser initiator = new RegisteredUserBuilder().id(10).build();
//        ApplicationFormStatus[] values = ApplicationFormStatus.values();
//        for (ApplicationFormStatus status : values) {
//            if (status != ApplicationFormStatus.VALIDATION && status != ApplicationFormStatus.APPROVAL && status != ApplicationFormStatus.REVIEW
//                    && status != ApplicationFormStatus.INTERVIEW) {
//                ApplicationForm application = new ApplicationFormBuilder().id(3).status(status).build();
//                boolean threwException = false;
//                try {
//                    approvalService.moveApplicationToApproval(application, new ApprovalRoundBuilder().id(1).build(), initiator);
//                } catch (IllegalStateException ise) {
//                    if (ise.getMessage().equals("Application in invalid status: '" + status + "'!")) {
//                        threwException = true;
//                    }
//                }
//                Assert.assertTrue(threwException);
//            }
//        }
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void shouldFailIfApplicationHasNoReferencesForSendingToPortico() {
//        RegisteredUser initiator = new RegisteredUserBuilder().id(10).build();
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.INTERVIEW)).id(1).build();
//        applyValidSendToPorticoData(applicationForm);
//        for (Referee referee : applicationForm.getReferees()) {
//            referee.setSendToUCL(false);
//        }
//        approvalService.moveApplicationToApproval(applicationForm, approvalRound, initiator);
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void shouldFailIfApplicationHasNoQualicifacionsForSendingToPorticoAndNoExplanation() {
//        RegisteredUser initiator = new RegisteredUserBuilder().id(10).build();
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.INTERVIEW)).id(1).build();
//        applyValidSendToPorticoData(applicationForm);
//        for (Qualification qualifications : applicationForm.getQualifications()) {
//            qualifications.setSendToUCL(false);
//        }
//        approvalService.moveApplicationToApproval(applicationForm, approvalRound, initiator);
//    }
//
//    @Test
//    public void shouldMoveToApprovalIfInApplicationWithNoQualificationsButExplanationProvided() {
//        RegisteredUser initiator = new RegisteredUserBuilder().id(10).build();
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).missingQualificationExplanation("explanation").build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.INTERVIEW)).id(1).build();
//        applyValidSendToPorticoData(applicationForm);
//        for (Qualification qualifications : applicationForm.getQualifications()) {
//            qualifications.setSendToUCL(false);
//        }
//        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
//                new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
//        approvalRoundDAOMock.save(approvalRound);
//        applicationFormDAOMock.save(applicationForm);
//        commentDAOMock.save(isA(ApprovalComment.class));
//        applicationFormUserRoleService.movedToApprovalStage(approvalRound);
//        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, initiator, ApplicationUpdateScope.ALL_USERS);
//
//        replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, applicationFormUserRoleService);
//        approvalService.moveApplicationToApproval(applicationForm, approvalRound, initiator);
//        verify(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, commentDAOMock, applicationFormUserRoleService);
//
//    }
//
//    @Test
//    public void shouldConfirmSupervision() {
//        RegisteredUser currentUser = new RegisteredUserBuilder().id(2).build();
//        RegisteredUser user = new RegisteredUserBuilder().id(3).email("a.user@ucl.co.uk").build();
//        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
//        Supervisor secondarySupervisor = new SupervisorBuilder().isPrimary(false).user(user).build();
//
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).missingQualificationExplanation("explanation")
//                .supervisors(primarySupervisor, secondarySupervisor).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.INTERVIEW)).id(1).latestApprovalRound(approvalRound).build();
//        approvalRound.setApplication(applicationForm);
//
//        Date startDate = Calendar.getInstance().getTime();
//
//        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
//        confirmSupervisionDTO.setConfirmedSupervision(true);
//        confirmSupervisionDTO.setProjectTitle("title");
//        confirmSupervisionDTO.setProjectAbstract("abstract");
//        confirmSupervisionDTO.setRecommendedStartDate(startDate);
//        confirmSupervisionDTO.setRecommendedConditionsAvailable(true);
//        confirmSupervisionDTO.setRecommendedConditions("conditions");
//        confirmSupervisionDTO.setSecondarySupervisorEmail("a.user@ucl.co.uk");
//
//        Capture<SupervisionConfirmationComment> supervisionConfirmationCommentcapture = new Capture<SupervisionConfirmationComment>();
//        commentDAOMock.save(capture(supervisionConfirmationCommentcapture));
//        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).once();
//
//        replay(commentDAOMock, userServiceMock);
//        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
//        verify(commentDAOMock, userServiceMock);
//
//        assertTrue(primarySupervisor.getConfirmedSupervision());
//        SupervisionConfirmationComment comment = supervisionConfirmationCommentcapture.getValue();
//
//        // assert comment
//        assertSame(applicationForm, comment.getApplication());
//        assertEquals("", comment.getComment());
//        assertNotNull(comment.getDate());
//        assertEquals("abstract", comment.getProjectAbstract());
//        assertEquals("title", comment.getProjectTitle());
//        assertEquals("conditions", comment.getRecommendedConditions());
//        assertTrue(comment.getRecommendedConditionsAvailable());
//        assertEquals(startDate, comment.getRecommendedStartDate());
//        assertSame(primarySupervisor, comment.getSupervisor());
//        assertSame(secondarySupervisor, comment.getSecondarySupervisor());
//        assertEquals(CommentType.SUPERVISION_CONFIRMATION, comment.getType());
//        assertSame(currentUser, comment.getUser());
//
//        // assert ApprovalRound
//        assertTrue(approvalRound.getProjectDescriptionAvailable());
//        assertEquals("abstract", approvalRound.getProjectAbstract());
//        assertEquals("title", approvalRound.getProjectTitle());
//        assertEquals("conditions", approvalRound.getRecommendedConditions());
//        assertTrue(approvalRound.getRecommendedConditionsAvailable());
//        assertEquals(startDate, approvalRound.getRecommendedStartDate());
//        assertSame(primarySupervisor, approvalRound.getPrimarySupervisor());
//        assertSame(secondarySupervisor, approvalRound.getSecondarySupervisor());
//    }
//
//    @Test
//    public void shouldModifySecondarySupervision() {
//        RegisteredUser currentUser = new RegisteredUserBuilder().id(2).build();
//        RegisteredUser user = new RegisteredUserBuilder().id(3).email("a.user@ucl.co.uk").build();
//        RegisteredUser anotherUser = new RegisteredUserBuilder().id(3).email("a.n.other@ucl.co.uk").build();
//        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
//        Supervisor secondarySupervisor = new SupervisorBuilder().isPrimary(false).user(user).build();
//
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).missingQualificationExplanation("explanation")
//                .supervisors(primarySupervisor, secondarySupervisor).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.INTERVIEW)).id(1).latestApprovalRound(approvalRound).build();
//
//        Date startDate = Calendar.getInstance().getTime();
//
//        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
//        confirmSupervisionDTO.setConfirmedSupervision(true);
//        confirmSupervisionDTO.setProjectTitle("title");
//        confirmSupervisionDTO.setProjectAbstract("abstract");
//        confirmSupervisionDTO.setRecommendedStartDate(startDate);
//        confirmSupervisionDTO.setRecommendedConditionsAvailable(true);
//        confirmSupervisionDTO.setRecommendedConditions("conditions");
//        confirmSupervisionDTO.setSecondarySupervisorEmail("a.n.other@ucl.co.uk");
//
//        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).once();
//
//        Capture<SupervisionConfirmationComment> supervisionConfirmationCommentcapture = new Capture<SupervisionConfirmationComment>();
//        commentDAOMock.save(capture(supervisionConfirmationCommentcapture));
//        expect(userServiceMock.getUserByEmail("a.n.other@ucl.co.uk")).andReturn(anotherUser);
//
//        replay(commentDAOMock, userServiceMock);
//        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
//        verify(commentDAOMock, userServiceMock);
//
//        assertThat(approvalRound.getSupervisors(), not(hasItems(secondarySupervisor)));
//        assertEquals(anotherUser, approvalRound.getSecondarySupervisor().getUser());
//    }
//
//    @Test
//    public void shouldDeclineSupervisionAndRestartApprovalRound() {
//        RegisteredUser user1 = new RegisteredUserBuilder().firstName("John Paul").lastName("Jones").build();
//        RegisteredUser user2 = new RegisteredUserBuilder().firstName("Dave").lastName("Jones").email("d.jones@ucl.ac.uk").build();
//        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).user(user1).build();
//        Supervisor secondarySupervisor = new SupervisorBuilder().isPrimary(false).user(user2).build();
//
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).missingQualificationExplanation("explanation").projectDescriptionAvailable(false)
//                .supervisors(primarySupervisor, secondarySupervisor).build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.INTERVIEW)).id(1).latestApprovalRound(approvalRound).build();
//        approvalRound.setApplication(applicationForm);
//
//        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();
//        confirmSupervisionDTO.setConfirmedSupervision(false);
//        confirmSupervisionDTO.setDeclinedSupervisionReason("reason");
//        confirmSupervisionDTO.setSecondarySupervisorEmail("d.jones@ucl.ac.uk");
//
//        Capture<SupervisionConfirmationComment> supervisionConfirmationCommentcapture = new Capture<SupervisionConfirmationComment>();
//        commentDAOMock.save(capture(supervisionConfirmationCommentcapture));
//        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user1).once();
//
//        replay(commentDAOMock, userServiceMock);
//        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
//        verify(commentDAOMock, userServiceMock);
//
//        assertFalse(primarySupervisor.getConfirmedSupervision());
//        assertEquals("reason", primarySupervisor.getDeclinedSupervisionReason());
//
//        SupervisionConfirmationComment comment = supervisionConfirmationCommentcapture.getValue();
//        assertSame(applicationForm, comment.getApplication());
//        assertEquals("", comment.getComment());
//        assertNotNull(comment.getDate());
//        assertSame(primarySupervisor, comment.getSupervisor());
//        assertEquals(CommentType.SUPERVISION_CONFIRMATION, comment.getType());
//        assertSame(user1, comment.getUser());
//
//        assertFalse(approvalRound.getProjectDescriptionAvailable());
//    }
//
//    @Test
//    public void shouldReturnNewApprovalRoundWithExistingRoundsSupervisorsIfApplicationHasProject() {
//        Program program = new Program().id(1).enabled(true).build();
//        RegisteredUser primarySupervisor = new RegisteredUserBuilder().id(1).email("primary.supervisor@email.test").build();
//        RegisteredUser secondarySupervisor = new RegisteredUserBuilder().id(2).email("secondary.supervisor@email.test").build();
//        Advert advert = new AdvertBuilder().description("desc").funding("fund").studyDuration(1).title("title").build();
//        Project project = new ProjectBuilder().program(program).advert(advert).primarySupervisor(primarySupervisor).secondarySupervisor(secondarySupervisor)
//                .build();
//
//        Date nowDate = new Date();
//        Date testDate = DateUtils.addMonths(nowDate, 1);
//        Date deadlineDate = DateUtils.addMonths(nowDate, 2);
//
//        Supervisor primary = new Supervisor();
//        primary.setUser(primarySupervisor);
//        primary.setIsPrimary(true);
//        Supervisor secondary = new Supervisor();
//        secondary.setUser(secondarySupervisor);
//
//        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("bob").program(program).project(project)
//                .latestApprovalRound(new ApprovalRoundBuilder().recommendedStartDate(testDate).supervisors(primary, secondary).build())
//                .programmeDetails(new ProgrammeDetails()).build();
//
//        final ProgramInstance programInstance = new ProgramInstance();
//        programInstance.setId(1);
//        programInstance.setProgram(program);
//        programInstance.setApplicationStartDate(nowDate);
//        programInstance.setApplicationDeadline(deadlineDate);
//
//        Capture<ApprovalRound> approvalRoundCapture = new Capture<ApprovalRound>();
//        EasyMock.expect(applicationFormDAOMock.getByApplicationNumber("bob")).andReturn(application).anyTimes();
//        EasyMock.expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application, testDate)).andReturn(true);
//        approvalRoundDAOMock.save(EasyMock.isA(ApprovalRound.class));
//        ApprovalRound returnExpectation = new ApprovalRoundBuilder().recommendedStartDate(testDate).supervisors(primary, secondary).build();
//        EasyMock.expect(approvalRoundDAOMock.initialise(EasyMock.capture(approvalRoundCapture))).andReturn(returnExpectation);
//
//        EasyMock.replay(applicationFormDAOMock, programInstanceServiceMock, approvalRoundDAOMock);
//        ApprovalRound returnedApprovalRound = approvalService.initiateApprovalRound("bob");
//        EasyMock.verify(applicationFormDAOMock, programInstanceServiceMock, approvalRoundDAOMock);
//
//        assertNull(returnedApprovalRound.getId());
//        List<Supervisor> supervisors = returnedApprovalRound.getSupervisors();
//        assertEquals(2, supervisors.size());
//        Supervisor supervisorOne = supervisors.get(0);
//        assertEquals(supervisorOne.getUser(), primarySupervisor);
//        assertTrue(supervisorOne.getIsPrimary());
//        Supervisor supervisorTwo = supervisors.get(1);
//        assertEquals(supervisorTwo.getUser(), secondarySupervisor);
//        assertFalse(supervisorTwo.getIsPrimary());
//    }
//
//    @Test
//    public void shouldReturnNewApprovalRoundWithExistingRoundsSupervisorsIfAny() {
//        Supervisor supervisorOne = new SupervisorBuilder().id(1).build();
//        Supervisor suprvisorTwo = new SupervisorBuilder().id(2).build();
//
//        Date nowDate = new Date();
//        Date testDate = DateUtils.addMonths(nowDate, 1);
//        Date deadlineDate = DateUtils.addMonths(nowDate, 2);
//
//        final Program program = new Program();
//        program.setId(100000);
//
//        final ProgramInstance programInstance = new ProgramInstance();
//        programInstance.setId(1);
//        programInstance.setProgram(program);
//        programInstance.setApplicationStartDate(nowDate);
//        programInstance.setApplicationDeadline(deadlineDate);
//
//        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(testDate).studyOption("1", "full").build();
//
//        final ApplicationForm application = new ApplicationFormBuilder().id(2).program(program).applicationNumber("bob").programmeDetails(programmeDetails)
//                .latestApprovalRound(new ApprovalRoundBuilder().recommendedStartDate(testDate).supervisors(supervisorOne, suprvisorTwo).build()).build();
//
//        Capture<ApprovalRound> approvalRoundCapture = new Capture<ApprovalRound>();
//        EasyMock.expect(applicationFormDAOMock.getByApplicationNumber("bob")).andReturn(application).anyTimes();
//        EasyMock.expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application, testDate)).andReturn(true);
//        approvalRoundDAOMock.save(EasyMock.isA(ApprovalRound.class));
//        ApprovalRound returnExpectation = new ApprovalRoundBuilder().recommendedStartDate(testDate).supervisors(supervisorOne, suprvisorTwo).build();
//        EasyMock.expect(approvalRoundDAOMock.initialise(EasyMock.capture(approvalRoundCapture))).andReturn(returnExpectation);
//
//        EasyMock.replay(applicationFormDAOMock, programInstanceServiceMock, approvalRoundDAOMock);
//        ApprovalRound returnedApprovalRound = approvalService.initiateApprovalRound("bob");
//        EasyMock.verify(applicationFormDAOMock, programInstanceServiceMock, approvalRoundDAOMock);
//
//        assertSame(returnedApprovalRound, returnExpectation);
//        assertEquals(2, returnedApprovalRound.getSupervisors().size());
//        assertTrue(returnedApprovalRound.getSupervisors().containsAll(Arrays.asList(supervisorOne, suprvisorTwo)));
//        assertEquals(testDate, returnedApprovalRound.getRecommendedStartDate());
//    }
//
//    @Test
//    public void shouldReturnNewApprovalRoundWithEmtpySupervisorsIfNoLatestApprovalRound() {
//
//        Date startDate = new Date();
//
//        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
//
//        Date nowDate = new Date();
//        Date testDate = DateUtils.addMonths(nowDate, 1);
//        Date deadlineDate = DateUtils.addMonths(nowDate, 3);
//
//        final Program program = new Program();
//        program.setId(100000);
//
//        final ProgramInstance programInstance = new ProgramInstance();
//        programInstance.setId(1);
//        programInstance.setProgram(program);
//        programInstance.setApplicationStartDate(nowDate);
//        programInstance.setApplicationDeadline(deadlineDate);
//
//        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("bob").programmeDetails(programmeDetails)
//                .latestApprovalRound(new ApprovalRoundBuilder().recommendedStartDate(testDate).supervisors().build()).build();
//
//        Capture<ApprovalRound> approvalRoundCapture = new Capture<ApprovalRound>();
//        EasyMock.expect(applicationFormDAOMock.getByApplicationNumber("bob")).andReturn(application).anyTimes();
//        EasyMock.expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application, testDate)).andReturn(true);
//        approvalRoundDAOMock.save(EasyMock.isA(ApprovalRound.class));
//        ApprovalRound returnExpectation = new ApprovalRoundBuilder().recommendedStartDate(testDate).supervisors().build();
//        EasyMock.expect(approvalRoundDAOMock.initialise(EasyMock.capture(approvalRoundCapture))).andReturn(returnExpectation);
//
//        EasyMock.replay(applicationFormDAOMock, programInstanceServiceMock, approvalRoundDAOMock);
//        ApprovalRound returnedApprovalRound = approvalService.initiateApprovalRound("bob");
//        EasyMock.verify(applicationFormDAOMock, programInstanceServiceMock, approvalRoundDAOMock);
//
//        assertTrue(returnedApprovalRound.getSupervisors().isEmpty());
//        assertEquals(testDate, returnedApprovalRound.getRecommendedStartDate());
//
//    }
//
//    private void applyValidSendToPorticoData(ApplicationForm applicationForm) {
//        RegisteredUser user1 = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
//        RegisteredUser user2 = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
//
//        Referee referee1 = new RefereeBuilder().user(user1).sendToUCL(true).build();
//        Referee referee2 = new RefereeBuilder().user(user2).sendToUCL(true).build();
//
//        user1.getReferees().add(referee1);
//        user2.getReferees().add(referee2);
//
//        Document document1 = new Document().id(1).build();
//
//        Qualification qualification1 = new Qualification().id(1).sendToUCL(true).proofOfAward(document1).build();
//        Qualification qualification2 = new Qualification().id(1).sendToUCL(true).proofOfAward(document1).build();
//
//        ReferenceComment referenceComment1 = new ReferenceCommentBuilder().id(1).referee(referee1).build();
//        ReferenceComment referenceComment2 = new ReferenceCommentBuilder().id(2).referee(referee2).build();
//
//        referee1.setReference(referenceComment1);
//        referee2.setReference(referenceComment2);
//
//        applicationForm.setReferees(Arrays.asList(referee1, referee2));
//        applicationForm.getApplicationComments().addAll(Arrays.<Comment> asList(referenceComment1, referenceComment2));
//        applicationForm.setQualifications(Arrays.asList(qualification1, qualification2));
//    }

}
