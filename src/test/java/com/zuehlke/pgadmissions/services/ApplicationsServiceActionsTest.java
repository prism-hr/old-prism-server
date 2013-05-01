package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;

public class ApplicationsServiceActionsTest {

    private ApplicationFormDAO applicationFormDAOMock;
    private ApplicationsService applicationsService;

    private RegisteredUser userMock;
    private ApplicationForm applicationMock;
    private Program programMock;
    
    @Before
    public void setup() {
        applicationFormDAOMock = createMock(ApplicationFormDAO.class);
        applicationsService = new ApplicationsService(applicationFormDAOMock, null, null);

        userMock = EasyMock.createMock(RegisteredUser.class);
        applicationMock = EasyMock.createMock(ApplicationForm.class);
        programMock = EasyMock.createMock(Program.class);
    }

    @Test
    public void shouldApplicantBeAbleEditAndWithdrawHisApplication() {
        configureUserAndApplicationExpectations(true, false, false, false, false, false, false, false, false, false, true, false, false, false, true, false,
                false, false, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "view", "withdraw" }, new String[] { "View / Edit", "Withdraw" });
    }

    @Test
    public void shouldNotApplicantBeAbleToEditAndWithdrawIfDecided() {
        configureUserAndApplicationExpectations(true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true,
                false, false, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "view" }, new String[] { "View" });
    }

    @Test
    public void shouldNotBeAbleToEditAndWithdrawIfNotApplicant() {
        configureUserAndApplicationExpectations(true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false,
                false, false, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "view" }, new String[] { "View" });
    }

    @Test
    public void shouldNotBeAbleToViewIfCannotSee() {
        configureUserAndApplicationExpectations(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {}, new String[] {});
    }

    @Ignore
    @Test
    public void shouldBeAbleToValidateIfAdminAndInvalidationStage() {
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, "VALIDATION");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "validate", "comment" }, new String[] { "Validate", "Comment" });
    }

    @Ignore
    @Test
    public void shouldBeAbleToReviewIfAdminAndReviewStage() {
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, "REVIEW");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "validate", "comment" }, new String[] { "Evaluate reviews", "Comment" });
    }

    @Test
    public void shouldBeAbleToInterviewIfAdminAndInterviewStage() {
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, "INTERVIEW");
        expect(applicationMock.getApplicationAdministrator()).andReturn(null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "validate", "comment" }, new String[] { "Evaluate interview feedback", "Comment" });
    }

    @Test
    public void shouldBeAbleToCommentIfIsViewer() {
        configureUserAndApplicationExpectations(false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "comment" }, new String[] { "Comment" });
    }

    @Test
    public void shouldBeAbleToAddReviewIfReviewer() {
        configureUserAndApplicationExpectations(false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, "REVIEW");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "review" }, new String[] { "Add review" });
    }

    @Test
    public void shouldNotBeAbleToAddReviewIfAlreadyProvided() {
        configureUserAndApplicationExpectations(false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, false,
                false, false, false, "REVIEW");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {}, new String[] {});
    }

    @Test
    public void shouldBeAbleToAddInterviewFeedbackIfInterviewer() {
        configureUserAndApplicationExpectations(false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, "INTERVIEW");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "interviewFeedback" }, new String[] { "Add interview feedback" });
    }

    @Test
    public void shouldNotBeAbleToAddInterviewFeedbackIfAlreadyProvided() {
        configureUserAndApplicationExpectations(false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false,
                false, false, false, "INTERVIEW");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {}, new String[] {});
    }

    @Test
    public void shouldBeAbleToAddReferenceIfReferee() {
        configureUserAndApplicationExpectations(false, false, false, false, false, true, false, false, false, false, false, false, false, true, true, false,
                false, false, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "reference" }, new String[] { "Add reference" });
    }

    @Test
    public void shouldNotBeAbleToAddReferenceIfAlreadyProvided() {
        configureUserAndApplicationExpectations(false, false, false, false, false, true, false, false, false, true, false, false, false, true, true, false,
                false, false, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {}, new String[] {});
    }

    @Test
    public void shouldNotBeAbleToWithdrawIfApplicantAndApplicationNotDecitedNorWithdrawn() {
        configureUserAndApplicationExpectations(false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false,
                false, false, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "withdraw" }, new String[] { "Withdraw" });
    }

    @Test
    public void shouldNotBeAbleToRestartApprovalIfAdminAndPendingRestart() {
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, true, false, null);
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "comment", "restartApproval" }, new String[] { "Comment", "Approve" });
    }

    @Test
    public void shouldBeAbleToApproveIfApproverAndInApproveStateAndPrimarySupervisorHasConfirmedSupervision() {
        configureUserAndApplicationExpectations(false, false, false, false, false, false, true, false, false, false, false, false, false, true, true, false,
                false, false, true, "APPROVAL");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "validate" }, new String[] { "Approve" });
    }

    @Test
    public void shouldBeAbleToApproveIfSupervisorHasNotConfirmedSupervision() {
        configureUserAndApplicationExpectations(false, false, false, false, false, false, true, false, false, false, false, false, false, true, true, false, false, false, false, "APPROVAL");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] {"validate"}, new String[] {"Approve"});
    }

    @Test
    public void shouldBeNotAbleToSupervisionIfSupervisionAndNotConfirmedYet() {
        configureUserAndApplicationExpectations(false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false,
                false, false, false, "APPROVAL");
        ApplicationActionsDefinition actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "confirmSupervision" }, new String[] { "Confirm supervision" });
    }

    private ApplicationActionsDefinition executeGetActionsDefinitions() {
        EasyMock.replay(userMock, applicationMock);
        ApplicationActionsDefinition actionsDefinition = applicationsService.getActionsDefinition(userMock, applicationMock);
        EasyMock.verify(userMock, applicationMock);
        return actionsDefinition;
    }

    private void configureUserAndApplicationExpectations(boolean canSee, boolean hasAdminRights, boolean isViewer, boolean isReviewer, boolean isInterviewer,
            boolean isReferee, boolean isApprover, boolean hasProvidedReview, boolean hasProvidedInterviewFeedback, boolean hasProvidedReference,
            boolean isApplicant, boolean isSupervisor, boolean canEditAsAdministrator, boolean isSubmitted, boolean isModifiable, boolean isDecided,
            boolean isWithdrawn, boolean isPendingApprovalRestart, boolean isSupervisionConfirmed, String state) {
        EasyMock.expect(userMock.canSee(applicationMock)).andReturn(canSee).anyTimes();
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(hasAdminRights).anyTimes();
        EasyMock.expect(userMock.isViewerOfProgramme(applicationMock)).andReturn(isViewer).anyTimes();
        EasyMock.expect(userMock.isReviewerInLatestReviewRoundOfApplicationForm(applicationMock)).andReturn(isReviewer).anyTimes();
        EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationMock)).andReturn(isInterviewer).anyTimes();
        EasyMock.expect(userMock.isRefereeOfApplicationForm(applicationMock)).andReturn(isReferee).anyTimes();
        EasyMock.expect(userMock.isInRoleInProgram("APPROVER", programMock)).andReturn(isApprover).anyTimes();
        EasyMock.expect(userMock.hasRespondedToProvideReviewForApplicationLatestRound(applicationMock)).andReturn(hasProvidedReview).anyTimes();
        EasyMock.expect(userMock.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(applicationMock)).andReturn(hasProvidedInterviewFeedback)
                .anyTimes();
        EasyMock.expect(userMock.getRefereeForApplicationForm(applicationMock)).andReturn(new RefereeBuilder().declined(hasProvidedReference).build())
                .anyTimes();

        EasyMock.expect(applicationMock.getApplicant()).andReturn(isApplicant ? userMock : null).anyTimes();
        EasyMock.expect(applicationMock.getProgram()).andReturn(programMock).anyTimes();

        EasyMock.expect(applicationMock.isUserAllowedToSeeAndEditAsAdministrator(userMock)).andReturn(canEditAsAdministrator).anyTimes();
        EasyMock.expect(applicationMock.isSubmitted()).andReturn(isSubmitted).anyTimes();
        EasyMock.expect(applicationMock.isModifiable()).andReturn(isModifiable).anyTimes();
        EasyMock.expect(applicationMock.isDecided()).andReturn(isDecided).anyTimes();
        EasyMock.expect(applicationMock.isWithdrawn()).andReturn(isWithdrawn).anyTimes();
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(isPendingApprovalRestart).anyTimes();

        Supervisor supervisor = new SupervisorBuilder().isPrimary(true).confirmedSupervision(isSupervisionConfirmed).build();
        if (isSupervisor) {
            supervisor.setUser(userMock);
        }
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).build();
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound).anyTimes();

        if (state != null) {
            EasyMock.expect(applicationMock.isInState(state)).andReturn(true).anyTimes();
        }
        EasyMock.expect(applicationMock.isInState(EasyMock.isA(String.class))).andReturn(false).anyTimes();
    }

    private void assertActionsDefinition(ApplicationActionsDefinition actionsDefinition, boolean requiresAttention, String[] actionsNames,
            String[] actionDisplayValues) {
        Preconditions.checkArgument(actionsNames.length == actionDisplayValues.length);

        Map<String, String> actions = actionsDefinition.getActions();
        assertEquals("Got actions: " + actions.keySet(), actionsNames.length, actions.size());
        for (int i = 0; i < actionsNames.length; i++) {
            assertEquals("Got actions: " + actions.keySet(), actionDisplayValues[i], actions.get(actionsNames[i]));
        }

        assertEquals(requiresAttention, actionsDefinition.isRequiresAttention());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
