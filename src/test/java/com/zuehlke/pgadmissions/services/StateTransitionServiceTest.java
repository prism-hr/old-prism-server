package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class StateTransitionServiceTest {

    @Mock @InjectIntoByType
    private StateService stateService;

    @Mock @InjectIntoByType
    private PermissionsService permissionsService;

    @TestedObject
    private StateTransitionService service;

//    @Test
//    public void shouldReturnRejectedReviewApprovedAndInterviewForValidationState() {
//        ApplicationFormStatus[] avaialbleStati = service.getAvailableNextStati(ApplicationFormStatus.VALIDATION).toArray(new ApplicationFormStatus[] {});
//        assertArrayEquals(new ApplicationFormStatus[] { ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL,
//                ApplicationFormStatus.REJECTED }, avaialbleStati);
//    }
//
//    @Test
//    public void shouldReturnRejectedReviewApprovedAndInterviewForReviewState() {
//        ApplicationFormStatus[] avaialbleStati = service.getAvailableNextStati(ApplicationFormStatus.REVIEW).toArray(new ApplicationFormStatus[] {});
//        assertArrayEquals(new ApplicationFormStatus[] { ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL,
//                ApplicationFormStatus.REJECTED }, avaialbleStati);
//    }
//
//    @Test
//    public void shouldReturnRejectedApprovedAndInterviewForInterviewState() {
//        ApplicationFormStatus[] avaialbleStati = service.getAvailableNextStati(ApplicationFormStatus.INTERVIEW).toArray(new ApplicationFormStatus[] {});
//        assertArrayEquals(new ApplicationFormStatus[] { ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL,
//                ApplicationFormStatus.REJECTED }, avaialbleStati);
//    }
//
//    @Test
//    public void shouldReturnRejectedApprovedForApprovalState() {
//        ApplicationFormStatus[] avaialbleStati = service.getAvailableNextStati(ApplicationFormStatus.APPROVAL).toArray(new ApplicationFormStatus[] {});
//        assertArrayEquals(new ApplicationFormStatus[] { ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL,
//                ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED }, avaialbleStati);
//    }
//
//    @Test
//    public void shouldReturnEmptyArrayForOtherStates() {
//        assertArrayEquals(new ApplicationFormStatus[] {},
//                service.getAvailableNextStati(ApplicationFormStatus.UNSUBMITTED).toArray(new ApplicationFormStatus[] {}));
//        assertArrayEquals(new ApplicationFormStatus[] {}, service.getAvailableNextStati(ApplicationFormStatus.REJECTED).toArray(new ApplicationFormStatus[] {}));
//        assertArrayEquals(new ApplicationFormStatus[] {}, service.getAvailableNextStati(ApplicationFormStatus.APPROVED).toArray(new ApplicationFormStatus[] {}));
//        assertArrayEquals(new ApplicationFormStatus[] {}, service.getAvailableNextStati(ApplicationFormStatus.WITHDRAWN)
//                .toArray(new ApplicationFormStatus[] {}));
//    }
    
    @Test
    public void shouldReturnRedirectToReviewIfReviewNextStatus() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(new State().withId(ApplicationFormStatus.REVIEW)).build();
        assertEquals("redirect:/review/moveToReview?applicationId=ABC", service.resolveView(applicationForm));
    }
    
    @Test
    public void shouldReturnRedirectToInterviewIfInterviewNextStatus() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(new State().withId(ApplicationFormStatus.INTERVIEW)).build();
        assertEquals("redirect:/interview/moveToInterview?applicationId=ABC", service.resolveView(applicationForm));
    }
    
    @Test
    public void shouldReturnRedirectToApprovalIfApprovalNextStatus() {
        ApplicationForm   applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(new State().withId(ApplicationFormStatus.APPROVAL)).build();
        assertEquals("redirect:/approval/moveToApproval?action=firstLoad&applicationId=ABC", service.resolveView(applicationForm));
    }
    
    @Test
    public void shouldReturnRedirectToRejectedIfRejectedNextStatus() {
        ApplicationForm   applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(new State().withId(ApplicationFormStatus.REJECTED)).build();
        assertEquals("redirect:/rejectApplication?applicationId=ABC", service.resolveView(applicationForm));
    }

    @Test
    public void shouldReturnRedirectToOfferRecommendationIfApprovedNextStatus() {
        ApplicationForm     applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(new State().withId(ApplicationFormStatus.APPROVED)).build();
        assertEquals("redirect:/offerRecommendation?applicationId=ABC", service.resolveView(applicationForm));
    }
    
    @Test
    public void shouldReturnStateTransitionViewIfNextStatusNotSpecified() {
        ApplicationForm     applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(null).build();
        assertEquals("private/staff/admin/state_transition",  service.resolveView(applicationForm));
    }
}
