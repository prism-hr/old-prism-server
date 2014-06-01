package com.zuehlke.pgadmissions.services;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.mail.NotificationService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class OfferRecommendationServiceTest {

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private CommentDAO commentDAOMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private NotificationService mailSendingServiceMock;

    @TestedObject
    private OfferRecommendationService service;

//    @Test
//    public void shouldMoveApplicationToApprovedWithComment() {
//        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
//        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
//
//        Date startDate = new Date();
//        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
//        ProgramInstance instance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 1))
//                .enabled(true).studyOption("1", "full").build();
//        Program program = new Program().id(1).instances(instance).enabled(true).build();
//        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().supervisors(new Supervisor()).build();
//        ApplicationForm application = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.APPROVAL)).program(program).id(2)
//                .programmeDetails(programmeDetails).latestApprovalRound(latestApprovalRound).build();
//        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
//        Supervisor secondarySupervisor = new SupervisorBuilder().id(1).build();
//
//        OfferRecommendedComment offerRecommendedComment = new OfferRecommendedCommentBuilder().supervisors(primarySupervisor, secondarySupervisor).build();
//
//        applicationFormDAOMock.save(application);
//        commentDAOMock.save(offerRecommendedComment);
//
//        expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application)).andReturn(true);
//        mailSendingServiceMock.sendApprovedNotification(application);
//        applicationFormUserRoleService.moveToApprovedOrRejectedOrWithdrawn(application);
//
//        replay();
//        service.moveToApproved(application, offerRecommendedComment);
//        verify();
//
//        assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());
//
//
//        assertSame(application, offerRecommendedComment.getApplication());
//        assertEquals("", offerRecommendedComment.getContent());
//        assertEquals(CommentType.OFFER_RECOMMENDED_COMMENT, offerRecommendedComment.getType());
//        assertSame(currentUser, offerRecommendedComment.getUser());
//        assertThat(latestApprovalRound.getSupervisors(), Matchers.contains(primarySupervisor, secondarySupervisor));
//    }
//
//    @Test
//    public void shouldChangeStartDate() {
//        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
//        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
//
//        Date startDate = DateUtils.addDays(new Date(), 1);
//        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
//        ProgramInstance instanceDisabled = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 4))
//                .enabled(false).studyOption("1", "full").build();
//        ProgramInstance instanceEnabled = new ProgramInstanceBuilder().applicationStartDate(DateUtils.addDays(startDate, 3))
//                .applicationDeadline(DateUtils.addDays(startDate, 4)).enabled(true).studyOption("1", "full").build();
//        Program program = new Program().id(1).enabled(true).instances(instanceDisabled, instanceEnabled).build();
//        ApplicationForm application = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.APPROVAL)).program(program).id(2)
//                .programmeDetails(programmeDetails).build();
//        OfferRecommendedComment offerRecommendedComment = new OfferRecommendedComment();
//
//        programmeDetailDAOMock.save(programmeDetails);
//        applicationFormDAOMock.save(application);
//        commentDAOMock.save(offerRecommendedComment);
//
//        expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application)).andReturn(false);
//        expect(programInstanceServiceMock.getEarliestPossibleStartDate(application)).andReturn(DateUtils.addDays(startDate, 3));
//        mailSendingServiceMock.sendApprovedNotification(application);
//        applicationFormUserRoleService.moveToApprovedOrRejectedOrWithdrawn(application);
//
//        replay();
//        service.moveToApproved(application, offerRecommendedComment);
//        verify();
//
//        assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());
//        assertEquals(programmeDetails.getStartDate(), instanceEnabled.getApplicationStartDate());
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void shouldFailOmMoveToApprovedIfApplicationNotInApproval() {
//        ApplicationForm application = new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.REJECTED)).id(2).build();
//        replay();
//        service.moveToApproved(application, null);
//        verify();
//    }

}
