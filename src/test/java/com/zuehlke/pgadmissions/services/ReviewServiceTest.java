package com.zuehlke.pgadmissions.services;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.mail.MailSendingService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ReviewServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsService;

    @Mock
    @InjectIntoByType
    private MailSendingService mailService;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleService;

    @TestedObject
    private ReviewService reviewService;

    // @Test
    // public void shouldSetDueDateOnApplicationUpdateFormAndSaveBoth() throws ParseException {
    //
    // ReviewRound reviewRound = new ReviewRoundBuilder().id(1).build();
    // Referee referee = new RefereeBuilder().build();
    // StateChangeComment stateChangeComment = new StateChangeComment();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).comments(stateChangeComment).status(new State().withId(ApplicationFormStatus.VALIDATION))
    // .id(1).build();
    // EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.REVIEW)).andReturn(
    // new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
    //
    // applicationFormDAOMock.save(applicationForm);
    //
    // StateChangeEvent event = new ReviewStateChangeEventBuilder().id(1).build();
    // EasyMock.expect(eventFactoryMock.createEvent(reviewRound)).andReturn(event);
    // mailServiceMock.sendReferenceRequest(asList(referee), applicationForm);
    // reviewRoundDAOMock.save(reviewRound);
    // applicationFormDAOMock.save(applicationForm);
    // applicationFormUserRoleServiceMock.validationStageCompleted(applicationForm);
    // applicationFormUserRoleServiceMock.movedToReviewStage(reviewRound);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, null, ApplicationUpdateScope.ALL_USERS);
    //
    // EasyMock.replay(reviewRoundDAOMock, applicationFormDAOMock, mailServiceMock, stageDurationDAOMock, eventFactoryMock, applicationFormUserRoleServiceMock);
    // reviewService.moveApplicationToReview(applicationForm, reviewRound, null);
    // EasyMock.verify(reviewRoundDAOMock, applicationFormDAOMock, mailServiceMock, stageDurationDAOMock, eventFactoryMock, applicationFormUserRoleServiceMock);
    //
    // assertEquals(DateUtils.truncate(com.zuehlke.pgadmissions.utils.DateUtils.addWorkingDaysInMinutes(new Date(), 2 * 1400), Calendar.DATE),
    // DateUtils.truncate(applicationForm.getDueDate(), Calendar.DATE));
    // assertEquals(applicationForm, reviewRound.getApplication());
    // assertEquals(reviewRound, applicationForm.getLatestReviewRound());
    // assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getStatus());
    //
    // assertEquals(1, applicationForm.getEvents().size());
    // assertEquals(event, applicationForm.getEvents().get(0));
    // }
    //
    // @Test
    // public void shouldMoveToReviewIfInReview() throws ParseException {
    // StateChangeComment changeComment = new StateChangeComment();
    // ReviewRound reviewRound = new ReviewRoundBuilder().id(1).build();
    // ApplicationForm applicationForm = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.REVIEW)).id(1).comments(changeComment).build();
    // EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.REVIEW)).andReturn(
    // new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
    // reviewRoundDAOMock.save(reviewRound);
    //
    // applicationFormDAOMock.save(applicationForm);
    // applicationFormUserRoleServiceMock.movedToReviewStage(reviewRound);
    // applicationFormUserRoleServiceMock.registerApplicationUpdate(applicationForm, null, ApplicationUpdateScope.ALL_USERS);
    //
    // EasyMock.replay(reviewRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, applicationFormUserRoleServiceMock);
    // reviewService.moveApplicationToReview(applicationForm, reviewRound, null);
    // EasyMock.verify(reviewRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, applicationFormUserRoleServiceMock);
    // }

}
