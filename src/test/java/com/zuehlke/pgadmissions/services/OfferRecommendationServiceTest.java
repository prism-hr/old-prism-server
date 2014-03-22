package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.OfferRecommendedCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class OfferRecommendationServiceTest {

    @Mock
    @InjectIntoByType
    private EventFactory eventFactoryMock;

    @Mock
    @InjectIntoByType
    private ProgrammeDetailDAO programmeDetailDAOMock;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private CommentDAO commentDAOMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private MailSendingService mailSendingServiceMock;

    @Mock
    @InjectIntoByType
    private ExportQueueService approvedSenderServiceMock;
    
    @Mock
    @InjectIntoByType
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @TestedObject
    private OfferRecommendationService service;

    @Test
    public void shouldMoveApplicationToApprovedWithComment() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();

        Date startDate = new Date();
        ProgramDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
        ProgramInstance instance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 1))
                .enabled(true).studyOption("1", "full").build();
        Program program = new ProgramBuilder().id(1).instances(instance).enabled(true).build();
        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().supervisors(new Supervisor()).build();
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).advert(program).id(2)
                .programmeDetails(programmeDetails).latestApprovalRound(latestApprovalRound).build();
        Supervisor primarySupervisor = new SupervisorBuilder().isPrimary(true).build();
        Supervisor secondarySupervisor = new SupervisorBuilder().id(1).build();

        OfferRecommendedComment offerRecommendedComment = new OfferRecommendedCommentBuilder().supervisors(primarySupervisor, secondarySupervisor).build();

        applicationFormDAOMock.save(application);
        commentDAOMock.save(offerRecommendedComment);

        StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
        expect(eventFactoryMock.createEvent(ApplicationFormStatus.APPROVED)).andReturn(event);
        expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application)).andReturn(true);
        mailSendingServiceMock.sendApprovedNotification(application);
        applicationFormUserRoleService.deleteApplicationActions(application);

        replay();
        service.moveToApproved(application, offerRecommendedComment);
        verify();

        assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());

        assertEquals(1, application.getEvents().size());
        assertEquals(event, application.getEvents().get(0));

        assertSame(application, offerRecommendedComment.getApplication());
        assertEquals("", offerRecommendedComment.getComment());
        assertEquals(CommentType.OFFER_RECOMMENDED_COMMENT, offerRecommendedComment.getType());
        assertSame(currentUser, offerRecommendedComment.getUser());
        assertThat(latestApprovalRound.getSupervisors(), Matchers.contains(primarySupervisor, secondarySupervisor));
    }

    @Test
    public void shouldChangeStartDate() {
        RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
        expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();

        Date startDate = DateUtils.addDays(new Date(), 1);
        ProgramDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
        ProgramInstance instanceDisabled = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 4))
                .enabled(false).studyOption("1", "full").build();
        ProgramInstance instanceEnabled = new ProgramInstanceBuilder().applicationStartDate(DateUtils.addDays(startDate, 3))
                .applicationDeadline(DateUtils.addDays(startDate, 4)).enabled(true).studyOption("1", "full").build();
        Program program = new ProgramBuilder().id(1).enabled(true).instances(instanceDisabled, instanceEnabled).build();
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).advert(program).id(2)
                .programmeDetails(programmeDetails).latestApprovalRound(new ApprovalRound()).build();
        OfferRecommendedComment offerRecommendedComment = new OfferRecommendedComment();

        programmeDetailDAOMock.save(programmeDetails);
        applicationFormDAOMock.save(application);
        commentDAOMock.save(offerRecommendedComment);

        StateChangeEvent event = new StateChangeEventBuilder().id(1).build();

        expect(eventFactoryMock.createEvent(ApplicationFormStatus.APPROVED)).andReturn(event);
        expect(programInstanceServiceMock.isPrefferedStartDateWithinBounds(application)).andReturn(false);
        expect(programInstanceServiceMock.getEarliestPossibleStartDate(application)).andReturn(DateUtils.addDays(startDate, 3));
        mailSendingServiceMock.sendApprovedNotification(application);
        applicationFormUserRoleService.deleteApplicationActions(application);

        replay();
        service.moveToApproved(application, offerRecommendedComment);
        verify();

        assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());
        assertEquals(programmeDetails.getStartDate(), instanceEnabled.getApplicationStartDate());

        assertEquals(1, application.getEvents().size());
        assertEquals(event, application.getEvents().get(0));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOmMoveToApprovedIfApplicationNotInApproval() {
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).id(2).build();
        replay();
        service.moveToApproved(application, null);
        verify();
    }

}
