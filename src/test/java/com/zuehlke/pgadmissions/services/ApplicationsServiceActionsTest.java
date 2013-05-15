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
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;

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
                false, false, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "view", "withdraw", "emailApplicant" }, new String[] { "View / Edit", "Withdraw", "Email applicant" });
    }

    @Test
    public void shouldNotApplicantBeAbleToEditAndWithdrawIfDecided() {
        configureUserAndApplicationExpectations(true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true,
                false, false, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "view", "emailApplicant" }, new String[] { "View", "Email applicant" });
    }

    @Test
    public void shouldNotBeAbleToEditAndWithdrawIfNotApplicant() {
        configureUserAndApplicationExpectations(true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false,
                false, false, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "view", "emailApplicant" }, new String[] { "View", "Email applicant" });
    }

    @Test
    public void shouldNotBeAbleToViewIfCannotSee() {
        configureUserAndApplicationExpectations(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {"emailApplicant"}, new String[] {"Email applicant"});
    }

    @Ignore
    @Test
    public void shouldBeAbleToValidateIfAdminAndInvalidationStage() {
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, ApplicationFormStatus.VALIDATION);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "validate", "comment", "emailApplicant" }, new String[] { "Validate", "Comment", "Email applicant" });
    }

    @Ignore
    @Test
    public void shouldBeAbleToReviewIfAdminAndReviewStage() {
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, ApplicationFormStatus.REVIEW);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "validate", "comment", "emailApplicant" }, new String[] { "Evaluate reviews", "Comment", "Email applicant" });
    }

    @Test
    public void shouldBeAbleToInterviewIfAdminAndInterviewStage() {
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, ApplicationFormStatus.INTERVIEW);
        expect(applicationMock.getApplicationAdministrator()).andReturn(null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "validate", "comment", "emailApplicant" }, new String[] { "Evaluate interview feedback", "Comment", "Email applicant" });
    }

    @Test
    public void shouldBeAbleToCommentIfIsViewer() {
        configureUserAndApplicationExpectations(false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "comment", "emailApplicant" }, new String[] { "Comment", "Email applicant" });
    }

    @Test
    public void shouldBeAbleToAddReviewIfReviewer() {
        configureUserAndApplicationExpectations(false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, ApplicationFormStatus.REVIEW);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "review", "emailApplicant" }, new String[] { "Add review", "Email applicant" });
    }

    @Test
    public void shouldNotBeAbleToAddReviewIfAlreadyProvided() {
        configureUserAndApplicationExpectations(false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, false,
                false, false, false, false, false, ApplicationFormStatus.REVIEW);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {"emailApplicant"}, new String[] {"Email applicant"});
    }

    @Test
    public void shouldBeAbleToAddInterviewFeedbackIfInterviewer() {
        configureUserAndApplicationExpectations(false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, ApplicationFormStatus.INTERVIEW);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "interviewFeedback", "emailApplicant" }, new String[] { "Add interview feedback", "Email applicant" });
    }

    @Test
    public void shouldNotBeAbleToAddInterviewFeedbackIfAlreadyProvided() {
        configureUserAndApplicationExpectations(false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false,
                false, false, false, false, false, ApplicationFormStatus.INTERVIEW);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {"emailApplicant"}, new String[] {"Email applicant"});
    }

    @Test
    public void shouldBeAbleToAddReferenceIfReferee() {
        configureUserAndApplicationExpectations(false, false, false, false, false, true, false, false, false, false, false, false, false, true, true, false,
                false, false, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "reference", "emailApplicant" }, new String[] { "Add reference", "Email applicant" });
    }

    @Test
    public void shouldNotBeAbleToAddReferenceIfAlreadyProvided() {
        configureUserAndApplicationExpectations(false, false, false, false, false, true, false, false, false, true, false, false, false, true, true, false,
                false, false, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {"emailApplicant"}, new String[] {"Email applicant"});
    }

    @Test
    public void shouldNotBeAbleToWithdrawIfApplicantAndApplicationNotDecitedNorWithdrawn() {
        configureUserAndApplicationExpectations(false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false,
                false, false, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "withdraw", "emailApplicant" }, new String[] { "Withdraw", "Email applicant" });
    }

    @Test
    public void shouldNotBeAbleToRestartApprovalIfAdminAndPendingRestart() {
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, true, false, false, false, null);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "comment", "restartApproval", "emailApplicant" }, new String[] { "Comment", "Revise Approval", "Email applicant" });
    }

    @Test
    public void shouldBeAbleToApproveIfApproverAndInApproveStateAndPrimarySupervisorHasConfirmedSupervision() {
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, programMock)).andReturn(false);
        EasyMock.expect(userMock.isNotInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        configureUserAndApplicationExpectations(false, false, false, false, false, false, true, false, false, false, false, false, false, true, true, false, false, false, true, false, false, ApplicationFormStatus.APPROVAL);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] { "validate", "emailApplicant" }, new String[] { "Approve", "Email applicant" });
    }

    @Test
    public void shouldBeAbleToApproveIfSupervisorHasNotConfirmedSupervision() {
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, programMock)).andReturn(false);
        EasyMock.expect(userMock.isNotInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        configureUserAndApplicationExpectations(false, false, false, false, false, false, true, false, false, false, false, false, false, true, true, false, false, false, false, false, false, ApplicationFormStatus.APPROVAL);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, false, new String[] {"validate", "emailApplicant"}, new String[] {"Approve", "Email applicant"});
    }
    
    @Test
    public void shouldBeNotAbleToSupervisionIfSupervisionAndNotConfirmedYet() {
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, programMock)).andReturn(false);
        configureUserAndApplicationExpectations(false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, ApplicationFormStatus.APPROVAL);
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "confirmSupervision", "emailApplicant" }, new String[] { "Confirm supervision", "Email applicant" });
    }
    
    @Test
    public void shouldBeAbleToRejectApplicationIfAdministratorAndInApproval() {
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, programMock)).andReturn(false);
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, ApplicationFormStatus.APPROVAL);
        EasyMock.expect(userMock.isNotInRoleInProgram(Authority.APPROVER, programMock)).andReturn(true).anyTimes();
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "comment", "confirmSupervision", "emailApplicant" }, new String[] { "Comment", "Confirm supervision", "Email applicant" });
    }
    
    @Test
    public void shouldNotBeAbleToRejectApplicationIfAdministratorAndInApprovalAndApproverInProgram() {
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, programMock)).andReturn(false);
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, ApplicationFormStatus.APPROVAL);
        EasyMock.expect(userMock.isNotInRoleInProgram(Authority.APPROVER, programMock)).andReturn(true).anyTimes();
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "comment", "confirmSupervision", "emailApplicant" }, new String[] { "Comment", "Confirm supervision", "Email applicant" });
    }
    
    @Test
    public void shouldBeAbleToRestartTheApprovalProcessIfApplicationInApprovalIsNotPendingApprovalRestartAndUserIsAdministrator() {
        EasyMock.expect(userMock.isInRoleInProgram(Authority.ADMINISTRATOR, programMock)).andReturn(true);
        configureUserAndApplicationExpectations(false, true, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, ApplicationFormStatus.APPROVAL);
        EasyMock.expect(userMock.isNotInRoleInProgram(Authority.APPROVER, programMock)).andReturn(true).anyTimes();
        EasyMock.expect(userMock.isNotInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
        ActionsDefinitions actionsDefinition = executeGetActionsDefinitions();
        assertActionsDefinition(actionsDefinition, true, new String[] { "comment", "confirmSupervision", "restartApprovalAsAdministrator", "emailApplicant"}, new String[] { "Comment", "Confirm supervision", "Revise Approval", "Email applicant" });
    }

    private ActionsDefinitions executeGetActionsDefinitions() {
        EasyMock.replay(userMock, applicationMock);
        ActionsDefinitions actionsDefinition = applicationsService.getActionsDefinition(userMock, applicationMock);
        EasyMock.verify(userMock, applicationMock);
        return actionsDefinition;
    }
    
    private void configureUserAndApplicationExpectations(boolean canSee, boolean hasAdminRights, boolean isViewer, boolean isReviewer, boolean isInterviewer,
            boolean isReferee, boolean isApprover, boolean hasProvidedReview, boolean hasProvidedInterviewFeedback, boolean hasProvidedReference,
            boolean isApplicant, boolean isSupervisor, boolean canEditAsAdministrator, boolean isSubmitted, boolean isModifiable, boolean isDecided,
            boolean isWithdrawn, boolean isPendingApprovalRestart, boolean isSupervisionConfirmed, boolean isSuperAdmin, boolean isAdministrator, ApplicationFormStatus status) {
        EasyMock.expect(userMock.canSee(applicationMock)).andReturn(canSee).anyTimes();
        EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(isSuperAdmin).anyTimes();
        EasyMock.expect(userMock.isInRole(Authority.ADMINISTRATOR)).andReturn(isAdministrator).anyTimes();
        EasyMock.expect(userMock.isInRole(Authority.ADMITTER)).andReturn(false).anyTimes();
        EasyMock.expect(userMock.hasAdminRightsOnApplication(applicationMock)).andReturn(hasAdminRights).anyTimes();
        EasyMock.expect(userMock.isViewerOfProgramme(applicationMock)).andReturn(isViewer).anyTimes();
        EasyMock.expect(userMock.isReviewerInLatestReviewRoundOfApplicationForm(applicationMock)).andReturn(isReviewer).anyTimes();
        EasyMock.expect(userMock.isInterviewerOfApplicationForm(applicationMock)).andReturn(isInterviewer).anyTimes();
        EasyMock.expect(userMock.isRefereeOfApplicationForm(applicationMock)).andReturn(isReferee).anyTimes();
        EasyMock.expect(userMock.isInRoleInProgram(Authority.APPROVER, programMock)).andReturn(isApprover).anyTimes();
        EasyMock.expect(userMock.hasRespondedToProvideReviewForApplicationLatestRound(applicationMock)).andReturn(hasProvidedReview).anyTimes();
        EasyMock.expect(userMock.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(applicationMock)).andReturn(hasProvidedInterviewFeedback).anyTimes();
        EasyMock.expect(userMock.getRefereeForApplicationForm(applicationMock)).andReturn(new RefereeBuilder().declined(hasProvidedReference).build()).anyTimes();

        EasyMock.expect(applicationMock.getApplicant()).andReturn(isApplicant ? userMock : null).anyTimes();
        EasyMock.expect(applicationMock.getProgram()).andReturn(programMock).anyTimes();

        EasyMock.expect(applicationMock.isUserAllowedToSeeAndEditAsAdministrator(userMock)).andReturn(canEditAsAdministrator).anyTimes();
        EasyMock.expect(applicationMock.isSubmitted()).andReturn(isSubmitted).anyTimes();
        EasyMock.expect(applicationMock.isModifiable()).andReturn(isModifiable).anyTimes();
        EasyMock.expect(applicationMock.isDecided()).andReturn(isDecided).anyTimes();
        EasyMock.expect(applicationMock.isWithdrawn()).andReturn(isWithdrawn).anyTimes();
        EasyMock.expect(applicationMock.isPendingApprovalRestart()).andReturn(isPendingApprovalRestart).anyTimes();
        EasyMock.expect(applicationMock.getLatestInterview()).andReturn(new InterviewBuilder().stage(InterviewStage.SCHEDULED).build()).anyTimes();

        Supervisor supervisor = new SupervisorBuilder().isPrimary(true).confirmedSupervision(isSupervisionConfirmed).build();
        if (isSupervisor) {
            supervisor.setUser(userMock);
        }
        
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).build();
        EasyMock.expect(applicationMock.getLatestApprovalRound()).andReturn(approvalRound).anyTimes();

        if (status != null) {
            EasyMock.expect(applicationMock.isInState(status)).andReturn(true).anyTimes();
        }
        EasyMock.expect(applicationMock.isInState(EasyMock.isA(ApplicationFormStatus.class))).andReturn(false).anyTimes();
    }

    private void assertActionsDefinition(ActionsDefinitions actionsDefinition, boolean requiresAttention, String[] actionsNames,
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
