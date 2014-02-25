package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.reset;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class StateTransitionViewResolverTest {

    @TestedObject
    private StateTransitionViewResolver resolver;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceServiceMock;

    private ApplicationForm applicationForm;

    @Test
    public void shouldReturnRedirectToRejectViewIfProgrammeIsNotAvailableAnyMore() {
        applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").build();

        reset();
        EasyMock.expect(programInstanceServiceMock.isProgrammeStillAvailable(applicationForm)).andReturn(false);

        replay();
        assertEquals("redirect:/rejectApplication?applicationId=ABC&rejectionId=7&rejectionIdForced=true", resolver.resolveView(applicationForm));
        verify();
    }

    @Test
    public void shouldReturnRedirectToReviewIfReviewNextStatus() {
        applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(ApplicationFormStatus.REVIEW).build();
        assertEquals("redirect:/review/moveToReview?applicationId=ABC", resolver.resolveView(applicationForm));
    }
    
    @Test
    public void shouldReturnRedirectToInterviewIfInterviewNextStatus() {
        applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(ApplicationFormStatus.INTERVIEW).build();
        assertEquals("redirect:/interview/moveToInterview?applicationId=ABC", resolver.resolveView(applicationForm));
    }
    
    @Test
    public void shouldReturnRedirectToApprovalIfApprovalNextStatus() {
        applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(ApplicationFormStatus.APPROVAL).build();
        assertEquals("redirect:/approval/moveToApproval?action=firstLoad&applicationId=ABC", resolver.resolveView(applicationForm));
    }
    
    @Test
    public void shouldReturnRedirectToRejectedIfRejectedNextStatus() {
        applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(ApplicationFormStatus.REJECTED).build();
        assertEquals("redirect:/rejectApplication?applicationId=ABC", resolver.resolveView(applicationForm));
    }

    @Test
    public void shouldReturnRedirectToOfferRecommendationIfApprovedNextStatus() {
        applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(ApplicationFormStatus.APPROVED).build();
        assertEquals("redirect:/offerRecommendation?applicationId=ABC", resolver.resolveView(applicationForm));
    }
    
    @Test
    public void shouldReturnStateTransitionViewIfNextStatusNotSpecified() {
        applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").nextStatus(null).build();
        assertEquals("private/staff/admin/state_transition",  resolver.resolveView(applicationForm));
    }

    @Before
    public void setUp() {
        EasyMock.expect(programInstanceServiceMock.isProgrammeStillAvailable(applicationForm)).andReturn(true);
        EasyMockUnitils.replay();
    }
}
