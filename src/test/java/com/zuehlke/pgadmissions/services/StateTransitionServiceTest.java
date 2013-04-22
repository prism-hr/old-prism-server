package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class StateTransitionServiceTest {

    private StateTransitionService service;
    
    @Before
    public void setup() {
        service = new StateTransitionService(new StateTransitionViewResolver());
    }
    
    @Test
    public void shouldReturnRejectedReviewApprovedAndInterviewForValidationState(){
        ApplicationFormStatus[] avaialbleStati = service.getAvailableNextStati(ApplicationFormStatus.VALIDATION);
        assertArrayEquals(new ApplicationFormStatus[]{ ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL, ApplicationFormStatus.REJECTED},avaialbleStati);
    }
    
    @Test
    public void shouldReturnRejectedReviewApprovedAndInterviewForReviewState(){
        ApplicationFormStatus[] avaialbleStati = service.getAvailableNextStati(ApplicationFormStatus.REVIEW);
        assertArrayEquals(new ApplicationFormStatus[]{ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL, ApplicationFormStatus.REJECTED},avaialbleStati);
    }
    
    @Test
    public void shouldReturnRejectedApprovedAndInterviewForInterviewState(){
        ApplicationFormStatus[] avaialbleStati = service.getAvailableNextStati(ApplicationFormStatus.INTERVIEW);
        assertArrayEquals(new ApplicationFormStatus[]{ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL, ApplicationFormStatus.REJECTED},avaialbleStati);
    }
    
    @Test
    public void shouldReturnRejectedApprovedForApprovalState(){
        ApplicationFormStatus[] avaialbleStati = service.getAvailableNextStati(ApplicationFormStatus.APPROVAL);
        assertArrayEquals(new ApplicationFormStatus[]{ ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVED, ApplicationFormStatus.REJECTED, ApplicationFormStatus.REQUEST_RESTART_APPROVAL},avaialbleStati);
    }
    
    @Test
    public void shouldReturnEmptyArrayForOtherStates(){
        assertArrayEquals(new ApplicationFormStatus[]{},service.getAvailableNextStati(ApplicationFormStatus.UNSUBMITTED));
        assertArrayEquals(new ApplicationFormStatus[]{},service.getAvailableNextStati(ApplicationFormStatus.REJECTED));
        assertArrayEquals(new ApplicationFormStatus[]{},service.getAvailableNextStati(ApplicationFormStatus.APPROVED));
        assertArrayEquals(new ApplicationFormStatus[]{},service.getAvailableNextStati(ApplicationFormStatus.WITHDRAWN));
    }
}
