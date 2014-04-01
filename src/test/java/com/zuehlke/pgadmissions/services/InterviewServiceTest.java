package com.zuehlke.pgadmissions.services;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;

import com.zuehlke.pgadmissions.mail.MailSendingService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class InterviewServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsService;

    @Mock
    @InjectIntoByType
    private MailSendingService mailService;

    @Mock
    @InjectIntoByType
    private StageDurationService stageDurationService;

    @Mock
    @InjectIntoByType
    private CommentService commentService;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    // @Test
    // public void shouldGetInterviewById() {
    // Interview interview = EasyMock.createMock(Interview.class);
    // interview.setId(2);
    // EasyMock.expect(interviewDAOMock.getInterviewById(2)).andReturn(interview);
    // EasyMock.replay(interview, interviewDAOMock);
    // Assert.assertEquals(interview, interviewService.getInterviewById(2));
    // }
    //
    // @Test
    // public void shouldDelegateSaveToDAO() {
    // Interview interview = EasyMock.createMock(Interview.class);
    // interviewDAOMock.save(interview);
    // EasyMock.replay(interviewDAOMock);
    // interviewService.save(interview);
    // EasyMock.verify(interviewDAOMock);
    // }
    //
    // @Test
    // public void shouldSetDueDateOnInterviewUpdateFormAndSaveBoth() throws ParseException {
    // SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
    // Interviewer interviewer = new InterviewerBuilder().build();
    // Interview interview = new InterviewBuilder().interviewers(interviewer).dueDate(dateFormat.parse("01 04 2012")).id(1).stage(InterviewStage.SCHEDULED)
    // .build();
    // Referee referee = new RefereeBuilder().build();
    // StateChangeComment stateChangeComment = new StateChangeComment();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().comments(stateChangeComment).referees(referee).status(ApplicationFormStatus.VALIDATION)
    // .id(1).build();
    //
    // StageDuration duration = new StageDurationBuilder().duration(1).unit(DurationUnitEnum.DAYS).build();
    // RegisteredUser user = new RegisteredUser();
    //
    // expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.INTERVIEW)).andReturn(duration);
    //
    // applicationFormDAOMock.save(applicationForm);
    // InterviewStateChangeEvent interviewStateChangeEvent = new InterviewStateChangeEventBuilder().id(1).build();
    // EasyMock.expect(eventFactoryMock.createEvent(interview)).andReturn(interviewStateChangeEvent);
    // mailServiceMock.sendInterviewConfirmationToApplicant(applicationForm);
    // mailServiceMock.sendInterviewConfirmationToInterviewers(asList(interviewer));
    // mailServiceMock.sendReferenceRequest(asList(referee), applicationForm);
    // interviewDAOMock.save(interview);
    // applicationFormDAOMock.save(applicationForm);
    // applicationFormUserRoleServiceMock.validationStageCompleted(applicationForm);
    // applicationFormUserRoleServiceMock.movedToInterviewStage(interview);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
    //
    // EasyMock.replay(interviewDAOMock, applicationFormDAOMock, eventFactoryMock, mailServiceMock, stageDurationServiceMock,
    // applicationFormUserRoleServiceMock);
    // interviewService.moveApplicationToInterview(user, interview, applicationForm);
    // EasyMock.verify(interviewDAOMock, applicationFormDAOMock, eventFactoryMock, stageDurationServiceMock, mailServiceMock,
    // applicationFormUserRoleServiceMock);
    //
    // assertEquals(dateFormat.parse("03 04 2012"), applicationForm.getDueDate());
    // assertEquals(applicationForm, interview.getApplication());
    // assertEquals(interview, applicationForm.getLatestInterview());
    // assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
    //
    // assertEquals(1, applicationForm.getEvents().size());
    // assertEquals(interviewStateChangeEvent, applicationForm.getEvents().get(0));
    // }
    //
    // @Test
    // public void shouldMoveApplicationToInterviewStageWhenInterviewAlreadyHasTakenPlace() throws ParseException {
    // RegisteredUser user = new RegisteredUser();
    // SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
    // Interviewer interviewer = new InterviewerBuilder().build();
    // Interview interview = new InterviewBuilder().interviewers(interviewer).dueDate(dateFormat.parse("01 04 2012")).id(1).stage(InterviewStage.SCHEDULED)
    // .takenPlace(true).furtherDetails("applicant!").furtherInterviewerDetails("interviewer!").build();
    // Referee referee = new RefereeBuilder().build();
    // StateChangeComment stateChangeComment = new StateChangeComment();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).comments(stateChangeComment).status(ApplicationFormStatus.VALIDATION)
    // .id(1).build();
    //
    // StageDuration duration = new StageDurationBuilder().duration(5).unit(DurationUnitEnum.DAYS).build();
    //
    // EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.INTERVIEW)).andReturn(duration);
    //
    // applicationFormDAOMock.save(applicationForm);
    // InterviewStateChangeEvent interviewStateChangeEvent = new InterviewStateChangeEventBuilder().id(1).build();
    // EasyMock.expect(eventFactoryMock.createEvent(interview)).andReturn(interviewStateChangeEvent);
    // mailServiceMock.sendReferenceRequest(asList(referee), applicationForm);
    // interviewDAOMock.save(interview);
    // applicationFormDAOMock.save(applicationForm);
    // applicationFormUserRoleServiceMock.validationStageCompleted(applicationForm);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
    // applicationFormUserRoleServiceMock.movedToInterviewStage(interview);
    //
    // EasyMock.replay(interviewDAOMock, applicationFormDAOMock, eventFactoryMock, mailServiceMock, stageDurationServiceMock, commentServiceMock,
    // applicationFormUserRoleServiceMock);
    // interviewService.moveApplicationToInterview(user, interview, applicationForm);
    // EasyMock.verify(interviewDAOMock, applicationFormDAOMock, eventFactoryMock, mailServiceMock, stageDurationServiceMock, commentServiceMock,
    // applicationFormUserRoleServiceMock);
    //
    // Assert.assertNotNull(applicationForm.getDueDate());
    // assertEquals(applicationForm, interview.getApplication());
    // assertEquals(interview, applicationForm.getLatestInterview());
    // assertEquals(ApplicationFormStatus.INTERVIEW, applicationForm.getStatus());
    //
    // assertEquals(1, applicationForm.getEvents().size());
    // assertEquals(interviewStateChangeEvent, applicationForm.getEvents().get(0));
    // }
    //
    // @Test
    // public void shouldMoveToInterviewIfInReview() throws ParseException {
    // RegisteredUser user = new RegisteredUser();
    // Interview interview = new InterviewBuilder().dueDate(new SimpleDateFormat("dd MM yyyy").parse("01 04 2012")).id(1).furtherDetails("applicant!")
    // .furtherInterviewerDetails("interviewer!").locationURL("loc").build();
    // StateChangeComment changeComment = new StateChangeComment();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).comments(changeComment).build();
    // StageDuration duration = new StageDurationBuilder().duration(5).unit(DurationUnitEnum.DAYS).build();
    // InterviewScheduleComment interviewScheduleComment = new InterviewScheduleComment();
    //
    // interviewDAOMock.save(interview);
    // applicationFormDAOMock.save(applicationForm);
    // EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.INTERVIEW)).andReturn(duration);
    // EasyMock.expect(commentFactoryMock.createInterviewScheduleComment(user, applicationForm, "applicant!", "interviewer!", "loc")).andReturn(
    // interviewScheduleComment);
    // commentServiceMock.save(interviewScheduleComment);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
    // applicationFormUserRoleServiceMock.movedToInterviewStage(interview);
    //
    // EasyMock.replay(interviewDAOMock, applicationFormDAOMock, stageDurationServiceMock, commentFactoryMock, commentServiceMock,
    // applicationFormUserRoleServiceMock);
    // interviewService.moveApplicationToInterview(user, interview, applicationForm);
    // EasyMock.verify(interviewDAOMock, applicationFormDAOMock, stageDurationServiceMock, commentFactoryMock, commentServiceMock,
    // applicationFormUserRoleServiceMock);
    //
    // }
    //
    // @Test
    // public void shouldMoveToInterviewIfInInterview() throws ParseException {
    // RegisteredUser user = new RegisteredUser();
    // Interview interview = new InterviewBuilder().dueDate(new SimpleDateFormat("dd MM yyyy").parse("01 04 2012")).id(1).furtherDetails("applicant!")
    // .furtherInterviewerDetails("interviewer!").locationURL("loc").build();
    // StateChangeComment changeComment = new StateChangeComment();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).id(1).comments(changeComment).build();
    // StageDuration duration = new StageDurationBuilder().duration(5).unit(DurationUnitEnum.DAYS).build();
    // InterviewScheduleComment interviewScheduleComment = new InterviewScheduleComment();
    //
    // interviewDAOMock.save(interview);
    // applicationFormDAOMock.save(applicationForm);
    // EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.INTERVIEW)).andReturn(duration);
    // EasyMock.expect(commentFactoryMock.createInterviewScheduleComment(user, applicationForm, "applicant!", "interviewer!", "loc")).andReturn(
    // interviewScheduleComment);
    // commentServiceMock.save(interviewScheduleComment);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
    // applicationFormUserRoleServiceMock.movedToInterviewStage(interview);
    //
    // EasyMock.replay(interviewDAOMock, applicationFormDAOMock, stageDurationServiceMock, commentFactoryMock, commentServiceMock,
    // applicationFormUserRoleServiceMock);
    // interviewService.moveApplicationToInterview(user, interview, applicationForm);
    // EasyMock.verify(interviewDAOMock, applicationFormDAOMock, stageDurationServiceMock, commentFactoryMock, commentServiceMock,
    // applicationFormUserRoleServiceMock);
    //
    // }
    //
    // @Test
    // public void shouldCreateNewInterviewerInNewInterviewRoundIfLatestRoundIsNull() {
    // RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari")
    // .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
    // ApplicationForm application = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().id(1).build())
    // .applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();
    // interviewerDAOMock.save(interviewer);
    // EasyMock.replay(interviewerDAOMock);
    // interviewService.addInterviewerInPreviousInterview(application, interviewerUser);
    // Assert.assertEquals(interviewerUser, interviewer.getUser());
    // Assert.assertTrue(interview.getInterviewers().contains(interviewer));
    //
    // }
    //
    // @Test
    // public void shouldCreateNewInterviewerInLatestInterviewRoundIfLatestRoundIsNotNull() {
    // RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari")
    // .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
    // Interview latestInterview = new InterviewBuilder().build();
    // ApplicationForm application = new ApplicationFormBuilder().latestInterview(latestInterview).id(1).program(new ProgramBuilder().id(1).build())
    // .applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();
    // interviewerDAOMock.save(interviewer);
    //
    // EasyMock.replay(interviewerDAOMock);
    // interviewService.addInterviewerInPreviousInterview(application, interviewerUser);
    // EasyMock.verify(interviewerDAOMock);
    //
    // Assert.assertEquals(interviewerUser, interviewer.getUser());
    // Assert.assertTrue(latestInterview.getInterviewers().contains(interviewer));
    // }
    //
    // @Test
    // public void shouldPostVote() {
    // InterviewParticipant participant = new InterviewParticipant();
    // InterviewVoteComment interviewVoteComment = new InterviewVoteComment();
    //
    // interviewParticipantDAOMock.save(participant);
    // interviewVoteCommentDAOMock.save(interviewVoteComment);
    // mailServiceMock.sendInterviewVoteConfirmationToAdministrators(participant);
    // applicationFormUserRoleServiceMock.interviewParticipantResponded(participant);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(interviewVoteComment.getApplication(), participant.getUser(),
    // ApplicationUpdateScope.INTERNAL);
    //
    // EasyMock.replay(interviewParticipantDAOMock, interviewVoteCommentDAOMock, mailServiceMock, applicationFormUserRoleServiceMock);
    // interviewService.postVote(participant, interviewVoteComment);
    // EasyMock.verify(interviewParticipantDAOMock, interviewVoteCommentDAOMock, mailServiceMock, applicationFormUserRoleServiceMock);
    //
    // assertTrue(participant.getResponded());
    // }
    //
    // @Test
    // public void shouldConfirmInterview() {
    // Date date = new Date();
    // RegisteredUser user = new RegisteredUser();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().applicationAdministrator(new RegisteredUser()).build();
    // Interviewer interviewer = new Interviewer();
    //
    // AppointmentTimeslot timeslot1 = new InterviewTimeslotBuilder().id(1).build();
    // AppointmentTimeslot timeslot2 = new InterviewTimeslotBuilder().id(2).dueDate(date).startTime("11:11").build();
    //
    // Interview interview = new InterviewBuilder().timeslots(timeslot1, timeslot2).interviewers(interviewer).application(applicationForm).build();
    //
    // StageDuration interviewStageDuration = new StageDurationBuilder().duration(1).unit(DurationUnitEnum.WEEKS).stage(ApplicationFormStatus.INTERVIEW)
    // .build();
    // InterviewConfirmDTO interviewConfirmDTO = new InterviewConfirmDTO();
    // interviewConfirmDTO.setTimeslotId(2);
    // interviewConfirmDTO.setFurtherDetails("applicant!");
    // interviewConfirmDTO.setFurtherInterviewerDetails("interviewer!");
    // interviewConfirmDTO.setLocationUrl("loc");
    // InterviewScheduleComment interviewScheduleComment = new InterviewScheduleComment();
    //
    // EasyMock.expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.INTERVIEW)).andReturn(interviewStageDuration);
    // EasyMock.expect(commentFactoryMock.createInterviewScheduleComment(user, applicationForm, "applicant!", "interviewer!", "loc")).andReturn(
    // interviewScheduleComment);
    // commentServiceMock.save(interviewScheduleComment);
    //
    // interviewDAOMock.save(interview);
    // mailServiceMock.sendInterviewConfirmationToApplicant(applicationForm);
    // mailServiceMock.sendInterviewConfirmationToInterviewers(interview.getInterviewers());
    // applicationFormUserRoleServiceMock.interviewConfirmed(interview);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
    //
    // EasyMock.replay(interviewDAOMock, mailServiceMock, stageDurationServiceMock, commentFactoryMock, commentServiceMock, applicationFormUserRoleServiceMock);
    // interviewService.confirmInterview(user, interview, interviewConfirmDTO);
    // EasyMock.verify(interviewDAOMock, mailServiceMock, stageDurationServiceMock, commentFactoryMock, commentServiceMock, applicationFormUserRoleServiceMock);
    //
    // assertEquals(date, interview.getInterviewDueDate());
    // assertEquals("11:11", interview.getInterviewTime());
    // assertEquals(InterviewStage.SCHEDULED, interview.getStage());
    // }

}
