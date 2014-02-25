package com.zuehlke.pgadmissions.domain;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class RegisteredUserTest {

    @Test
    public void shouldReturnTrueIfUserIsInRole() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertTrue(user.isInRole(Authority.APPLICANT));

    }

    @Test
    public void shouldReturnFalseIfUserIsNotInRole() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertFalse(user.isInRole(Authority.REVIEWER));

    }

    @Test
    public void shouldReturnTrueIfUserIsInRolePassedAsString() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertTrue(user.isInRole("APPLICANT"));

    }

    @Test
    public void shouldReturnFalseIfUserIsNotInRolePassedAsString() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertFalse(user.isInRole("REVIEWER"));

    }

    @Test
    public void shouldReturnFalseIStringIsNotAuthorityValue() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertFalse(user.isInRole("bob"));

    }

    @Test
    public void shouldReturnTrueIfUserIsApplicantAndOwnerOfForm() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).build();
        assertTrue(applicant.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserIsRefereeOfTheApplicationForm() {
        RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
        Referee referee = new RefereeBuilder().id(1).user(refereeUser).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(refereeUser.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserIsRefereeOfTheApplicationFormButHasDeclined() {
        RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
        Referee referee = new RefereeBuilder().id(1).user(refereeUser).declined(true).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(refereeUser.canSee(applicationForm));

    }

    @Test
    public void shouldReturnFalseIfUserIsRefereeOfButNotOnApplicationForm() {
        RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(refereeUser.canSee(applicationForm));

    }

    @Test
    public void shouldReturnFalseIfUserIsApplicantAndNotOwnerOfForm() {
        RegisteredUser applicantOne = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        RegisteredUser applicantTwo = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicantOne).build();
        assertFalse(applicantTwo.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserIsSuperAdministrator() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(administrator.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserInterviewerAndApplicationInInterviewStage() {

        RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).build();
        Interview interview = new InterviewBuilder().id(1).interviewers(new InterviewerBuilder().user(interviewerUser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().interviews(interview).latestInterview(interview).status(ApplicationFormStatus.INTERVIEW)
                .build();
        assertTrue(interviewerUser.canSee(applicationForm));

    }

    @Test
    public void shouldNotFailIfLatestInterviewIsNull() {

        RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).build();
        assertFalse(interviewerUser.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserReviewerAndApplicationInReviewStage() {

        RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).status(ApplicationFormStatus.REVIEW).build();
        assertTrue(revieweruser.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserProjectAdminInApplication() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION)
                .project(new ProjectBuilder().administrator(administrator).build()).build();
        assertTrue(administrator.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserAdminInProgramOfApplication() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION)
                .program(new ProgramBuilder().administrators(administrator).build()).build();
        assertTrue(administrator.canSee(applicationForm));

    }

    @Test
    public void shouldReturnFalseIfUserAdminInProgramOfApplicationNotSubmitted() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED)
                .program(new ProgramBuilder().administrators(administrator).build()).build();
        assertFalse(administrator.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfApplicationAdmin() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        applicationForm.getApplicationComments().add(new ReviewEvaluationCommentBuilder().delegateAdministrator(administrator).build());
        assertTrue(administrator.canSee(applicationForm));
    }

    @Test
    public void shouldReturnFalseIfUserAdminOfApplicationNotSubmitted() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).applicationAdministrator(administrator)
                .build();
        assertFalse(administrator.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserIsReviewerOfForm() {
        RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).status(ApplicationFormStatus.REVIEW).build();
        assertTrue(revieweruser.canSee(applicationForm));

    }

    @Test
    public void shouldReturnFalseIfUserIsNotItsReviewer() {
        RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().build();
        assertFalse(reviewer.canSee(applicationForm));

    }

    @Test
    public void shouldReturnFalseForAnyoneNotAnApplicantIfUnsubmittedApplication() {
        RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).build();
        assertFalse(revieweruser.canSee(applicationForm));
    }

    @Test
    public void shouldReturnFalseIfUserIsNotItsInterviewer() {
        RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().build();
        assertFalse(interviewer.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueForAnApplicantIfUnsubmittedApplication() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicant(applicant).build();
        assertTrue(applicant.canSee(applicationForm));
    }

    @Test
    public void shouldReturnTrueIfUserIsItsApproverOfProgramToWhichApplicationBelongsAndApplicatioIsInApproval() {
        RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPROVER).build()).build();
        Program program = new ProgramBuilder().id(1).approver(approver).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(approver.canSee(applicationForm));
        applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.APPROVAL).build();
        assertTrue(approver.canSee(applicationForm));

    }

    @Test
    public void shouldReturnTrueIfUserIsSupervisorOfApplication() {
        RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.SUPERVISOR).build()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(supervisorUser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(approvalRound).status(ApplicationFormStatus.APPROVAL).build();
        assertTrue(supervisorUser.canSee(applicationForm));
    }

    @Test
    public void shouldReturnFalseIfUserIsNotApproverOfProgramToWhichApplicationBelongs() {
        RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPROVER).build()).build();
        Program program = new ProgramBuilder().id(1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(approver.canSee(applicationForm));
    }

    @Test
    public void shouldReturnTrueIfUserIsViewerOfProgram() {
        RegisteredUser viewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.VIEWER).build()).build();
        Program program = new ProgramBuilder().id(1).viewers(viewer).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).program(program).build();
        assertTrue(viewer.canSee(applicationForm));
    }

    @Test
    public void shouldAllowSuperAdministratorToEdit() {
        RegisteredUser superAdmin = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.INTERVIEW).build();
        assertTrue(superAdmin.canEditAsAdministrator(applicationForm));
    }

    @Test
    public void shouldAllowProgramAdministratorToEdit() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.INTERVIEW).program(program).build();
        assertTrue(admin.canEditAsAdministrator(applicationForm));
    }

    @Test
    public void shouldNotAllowApplicantToEditAsAdministrator() {
        RegisteredUser applicant = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build()).id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.INTERVIEW).applicant(applicant).build();
        assertFalse(applicant.canEditAsAdministrator(applicationForm));
    }

    @Test
    public void shouldNotAllowAdministratorToEditApplicationInValidationStage() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program).build();
        assertFalse(admin.canEditAsAdministrator(applicationForm));
    }

    @Test
    public void shouldNotAllowAdministratorToEditUnsubmittedApplication() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.UNSUBMITTED).program(program).build();
        assertFalse(admin.canEditAsAdministrator(applicationForm));
    }

    @Test
    public void shouldNotAllowAdministratorToEditWithdrawnApplication() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.WITHDRAWN).program(program).build();
        assertFalse(admin.canEditAsAdministrator(applicationForm));
    }

    @Test
    public void shouldNotAllowAdministratorToEditRejectedApplication() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).program(program).build();
        assertFalse(admin.canEditAsAdministrator(applicationForm));
    }

    @Test
    public void shouldNotAllowAdministratorToEditApplicationInApprovalState() {
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVAL).program(program).build();
        assertFalse(admin.canEditAsAdministrator(applicationForm));
    }

    @Test
    public void shouldAllowApplicantToEditApplicationAsApplicant() {
        RegisteredUser applicant = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build()).id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).applicant(applicant)
                .isEditableByApplicant(true).build();
        assertTrue(applicant.canEditAsApplicant(applicationForm));
    }

    @Test
    public void shouldNotAllowAdminToEditApplicationAsApplicant() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(8).build();
        RegisteredUser admin = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).id(7).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).program(program)
                .isEditableByApplicant(true).applicant(applicant).build();
        assertFalse(admin.canEditAsApplicant(applicationForm));
    }

    @Test
    public void shouldNotAllowApplicantToEditRejectedApplicationAsApplicant() {
        RegisteredUser applicant = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build()).id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).applicant(applicant)
                .isEditableByApplicant(true).build();
        assertFalse(applicant.canEditAsApplicant(applicationForm));
    }

    @Test
    public void shouldNotAllowApplicantToEditApplicationInNotEditableStateAsApplicant() {
        RegisteredUser applicant = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build()).id(7).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).applicant(applicant)
                .isEditableByApplicant(false).build();
        assertFalse(applicant.canEditAsApplicant(applicationForm));
    }

    @Test
    public void shouldReturnTrueIfUserIsReviewerTApplication() {
        RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(reviewerUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnFalseIfLatestReviewRoundIsNull() {
        RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        Program program = new ProgramBuilder().id(1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(reviewer.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnFalseIfUserIsReviewerButNotInApplication() {
        RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        Program program = new ProgramBuilder().id(1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(new ReviewRoundBuilder().build()).program(program)
                .status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(reviewer.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnTrueInfUserIsReviewerOfReviewRound() {
        RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).build()).build();
        assertTrue(reviewerUser.isReviewerInReviewRound(reviewRound));
    }

    @Test
    public void shouldReturnFalseInfUserIsNotReviewerOfReviewRound() {
        RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().build();
        assertFalse(reviewerUser.isReviewerInReviewRound(reviewRound));
    }

    @Test
    public void shouldReturnFalseIfReviewRoundIsNull() {
        RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        assertFalse(reviewerUser.isReviewerInReviewRound(null));
    }

    @Test
    public void shouldReturnTrueInfUserIsInterviewerOfInterview() {
        RegisteredUser interviwerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        Interview interview = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviwerUser).build()).build();
        assertTrue(interviwerUser.isInterviewerInInterview(interview));
    }

    @Test
    public void shouldReturnFalseInfUserIsNotInterviewerOfInterview() {
        RegisteredUser interviwerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        Interview interview = new InterviewBuilder().build();
        assertFalse(interviwerUser.isInterviewerInInterview(interview));
    }

    @Test
    public void shouldReturnFalseIfInterviewIsNull() {
        RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        assertFalse(reviewerUser.isInterviewerInInterview(null));
    }

    @Test
    public void shouldReturnTrueInfUserIsSupvisorOfApprovalRound() {
        RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.SUPERVISOR).build()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(supervisorUser).build()).build();
        assertTrue(supervisorUser.isSupervisorInApprovalRound(approvalRound));
    }

    @Test
    public void shouldReturnFalseInfUserIsNotSupvisorOfApprovalRound() {
        RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.SUPERVISOR).build()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().build();
        assertFalse(supervisorUser.isSupervisorInApprovalRound(approvalRound));
    }

    @Test
    public void shouldReturnFalseIfApprovalRoundIsNull() {
        RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.SUPERVISOR).build()).build();
        assertFalse(reviewerUser.isSupervisorInApprovalRound(null));
    }

    @Test
    public void shouldReturnTrueIfUserIsPastOrPresentReviewerOfApplication() {
        RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(reviewerUser.isPastOrPresentReviewerOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnFalseIfUserIsNeitherPastOrPresentReviewerOfApplication() {
        RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(approver.isPastOrPresentReviewerOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnFalseForInterviewersIfUserIsReviewerButNotInApplication() {
        RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        Program program = new ProgramBuilder().id(1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(new Interview()).program(program)
                .status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(reviewer.isInterviewerOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnTrueForInterviewersIfUserIsInterviewerTApplication() {
        RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        Interview interview = new InterviewBuilder().id(1).interviewers(new InterviewerBuilder().user(interviewerUser).build()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(interviewerUser.isInterviewerOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnFalseForSupervisorsIfUserIsReviewerButNotInApplication() {
        RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        Program program = new ProgramBuilder().id(1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(new ApprovalRound()).program(program)
                .status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(reviewer.isSupervisorOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnTrueForSupervisorsIfUserIsSupervisorTApplication() {
        RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.SUPERVISOR).build()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).supervisors(new SupervisorBuilder().user(supervisorUser).build()).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(approvalRound).status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(supervisorUser.isSupervisorOfApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnListOfAuthoritiesForProgram() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program).build();
        List<Authority> authorities = user.getAuthoritiesForProgram(program);
        assertThat(authorities, hasItems(Authority.ADMINISTRATOR, Authority.APPROVER));
    }

    @Test
    public void shouldReturnListOfAuthoritiesForProgramWithoutNullPointerException() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program).build();
        List<Authority> authorities = user.getAuthoritiesForProgram(null);
        assertEquals(0, authorities.size());
    }

    @Test
    public void shouldReturnCommaSeparatedListOfAuthoritiesForProgram() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program).build();
        assertEquals("Administrator, Approver", user.getAuthoritiesForProgramAsString(program));

    }

    @Test
    public void shouldAddSuperAdminToReturnCommaSeparatedListIfSuperadmin() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build())
                .programsOfWhichAdministrator(program).programsOfWhichApprover(program).build();
        assertEquals("Superadministrator, Administrator, Approver", user.getAuthoritiesForProgramAsString(program));

    }

    @Test
    public void shouldReturnTrueIfUserHasRoleForProgram() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user1 = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).programsOfWhichApprover(program).build();
        assertFalse(user1.isInRoleInProgram(Authority.ADMINISTRATOR, program));
        RegisteredUser user2 = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).programsOfWhichAdministrator(program)
                .build();
        assertTrue(user2.isInRoleInProgram(Authority.ADMINISTRATOR, program));
    }

    @Test
    public void shouldReturnTrueForSuperadmins() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).build();
        assertTrue(user.isInRoleInProgram(Authority.SUPERADMINISTRATOR, program));
    }

    @Test
    public void shouldReturnTrueIfUserIsAdminAndOfAProgramme() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        Program program = new ProgramBuilder().id(1).administrators(user).build();
        assertTrue(user.isAdminInProgramme(program));
    }

    @Test
    public void shouldReturnFalseIfUserDoesNotBelongToTheProgramme() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        Program program = new ProgramBuilder().id(1).build();
        assertFalse(user.isAdminInProgramme(program));
    }

    @Test
    public void shouldReturnTrueIfHasRefereesInApplicationForm() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).build();
        Referee referee1 = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail1@test.com").application(form).build();
        Referee referee2 = new RefereeBuilder().id(2).firstname("ref").lastname("erre").email("emailemail2@test.com").build();
        Referee referee3 = new RefereeBuilder().id(3).firstname("ref").lastname("erre").email("emailemail3@test.com").build();

        RegisteredUser user = new RegisteredUserBuilder().id(1).referees(referee1, referee2, referee3).role(new RoleBuilder().id(Authority.REVIEWER).build())
                .build();

        assertTrue(user.hasRefereesInApplicationForm(form));
    }

    @Test
    public void shouldReturnFalseIfDoesntHaveRefereesInApplicationForm() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).build();
        Referee referee1 = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail1@test.com").build();
        Referee referee2 = new RefereeBuilder().id(2).firstname("ref").lastname("erre").email("emailemail2@test.com").build();
        Referee referee3 = new RefereeBuilder().id(3).firstname("ref").lastname("erre").email("emailemail3@test.com").build();

        RegisteredUser user = new RegisteredUserBuilder().id(1).referees(referee1, referee2, referee3).role(new RoleBuilder().id(Authority.REVIEWER).build())
                .build();

        assertFalse(user.hasRefereesInApplicationForm(form));
    }

    @Test
    public void shouldReturnFalseIfUserIsApplicant() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        assertFalse(user.canSeeReference(new ReferenceCommentBuilder().id(1).build()));
    }

    @Test
    public void shouldReturnFalseIfUserCannotSeeApplicationForReference() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        @SuppressWarnings("serial")
        RegisteredUser user = new RegisteredUser() {
            @Override
            public boolean canSee(ApplicationForm application) {
                return false;
            }
        };

        Referee referee = new RefereeBuilder().id(1).application(applicationForm).build();
        ReferenceComment reference = new ReferenceCommentBuilder().id(1).referee(referee).build();
        assertFalse(user.canSeeReference(reference));
    }

    @Test
    public void shouldReturnTrueIfUserCanSeeFormAndIsNotReferee() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        @SuppressWarnings("serial")
        RegisteredUser user = new RegisteredUser() {

            @Override
            public boolean canNotSeeApplication(final ApplicationForm form, final RegisteredUser user) {
                return false;
            }

            @Override
            public boolean isRefereeOfApplicationForm(ApplicationForm form) {
                return false;
            }
        };

        Referee referee = new RefereeBuilder().id(1).application(applicationForm).build();
        ReferenceComment reference = new ReferenceCommentBuilder().id(1).referee(referee).build();
        assertTrue(user.canSeeReference(reference));
    }

    @Test
    public void shouldReturnFalseIfUserIsRefereeAndNotUploadingReferee() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        @SuppressWarnings("serial")
        RegisteredUser user = new RegisteredUser() {
            @Override
            public boolean isRefereeOfApplicationForm(ApplicationForm form) {
                return true;
            }

        };

        Referee referee = new RefereeBuilder().id(1).application(applicationForm).user(new RegisteredUserBuilder().id(8).build()).build();
        ReferenceComment reference = new ReferenceCommentBuilder().id(1).referee(referee).build();
        assertFalse(user.canSeeReference(reference));
    }

    @Test
    public void shouldReturnTrueIfUserIsRefereeAndUploadingReferee() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        @SuppressWarnings("serial")
        RegisteredUser user = new RegisteredUser() {
            @Override
            public boolean isRefereeOfApplicationForm(ApplicationForm form) {
                return true;
            }

            @Override
            public boolean canNotSeeApplication(final ApplicationForm form, final RegisteredUser user) {
                return false;
            }
        };
        user.setId(1);
        Referee referee = new RefereeBuilder().id(1).application(applicationForm).user(user).build();
        ReferenceComment reference = new ReferenceCommentBuilder().id(1).referee(referee).build();
        assertTrue(user.canSeeReference(reference));
    }

    @Test
    public void shouldReturnNullIfDeclinedToRefereeForApplicationForm() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).build();
        Referee refereeOne = new RefereeBuilder().id(7).user(user).declined(true).application(applicationForm).build();
        Referee refereeTwo = new RefereeBuilder().id(8).user(new RegisteredUserBuilder().id(9).build())
                .application(new ApplicationFormBuilder().id(78).build()).build();
        user.getReferees().addAll(Arrays.asList(refereeOne, refereeTwo));
        assertNull(user.getRefereeForApplicationForm(applicationForm));
    }

    @Test
    public void shouldReturnTrueIfUserIsReviewerOfApplicationAndHasDeclinedToProvideReview() {

        RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
                .build();
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).id(1).build();

        Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").build();
        ReviewComment reviewComment = new ReviewCommentBuilder().application(application).id(2).decline(true).comment("This is a review comment")
                .commentType(CommentType.REVIEW).build();
        Comment comment1 = new CommentBuilder().id(1).application(application).comment("This is another generic Comment").build();

        RegisteredUser reviewer = new RegisteredUserBuilder().comments(comment1, comment, reviewComment)
                .roles(new RoleBuilder().id(Authority.REVIEWER).build()).username("email").firstName("bob").lastName("bobson").email("email@test.com").build();
        assertTrue(reviewer.hasDeclinedToProvideReviewForApplication(application));
    }

    @Test
    public void shouldReturnFalseIfUserIsReviewerOfApplicationButHasNotDeclinedToProvideReview() {

        RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
                .build();
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).id(1).build();

        Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").build();
        ReviewComment reviewComment = new ReviewCommentBuilder().application(application).id(2).decline(false).comment("This is a review comment")
                .commentType(CommentType.REVIEW).build();
        Comment comment1 = new CommentBuilder().id(1).application(application).comment("This is another generic Comment").build();

        RegisteredUser reviewer = new RegisteredUserBuilder().comments(comment1, comment, reviewComment)
                .roles(new RoleBuilder().id(Authority.REVIEWER).build()).username("email").firstName("bob").lastName("bobson").email("email@test.com").build();
        assertFalse(reviewer.hasDeclinedToProvideReviewForApplication(application));
    }

    @Test
    public void shouldReturnTrueIfUserIsReviewerOfApplicationAndHasProvidedReview() {

        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).build();

        Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").build();
        ReviewComment reviewComment = new ReviewCommentBuilder().application(application).id(2).decline(false).comment("This is a review comment")
                .commentType(CommentType.REVIEW).build();
        Comment comment1 = new CommentBuilder().id(3).application(application).comment("This is another generic Comment").build();

        RegisteredUser reviewer = new RegisteredUserBuilder().comments(comment1, comment, reviewComment)
                .roles(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        assertTrue(reviewer.hasRespondedToProvideReviewForApplication(application));
    }

    @Test
    public void shouldReturnFalseIfUserIsReviewerOfApplicationButHasNotProvidedReview() {

        RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
                .build();
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).id(1).build();

        Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").build();
        Comment comment1 = new CommentBuilder().id(3).application(application).comment("This is another generic Comment").build();

        RegisteredUser reviewer = new RegisteredUserBuilder().comments(comment1, comment).roles(new RoleBuilder().id(Authority.REVIEWER).build())
                .username("email").firstName("bob").lastName("bobson").email("email@test.com").build();
        assertFalse(reviewer.hasRespondedToProvideReviewForApplication(application));
    }

    @Test
    public void shouldReturnFalseIfUserIsNotReviewerForThisInApplication() {

        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application1 = new ApplicationFormBuilder().program(program).id(1).build();
        ApplicationForm application2 = new ApplicationFormBuilder().program(program).id(2).build();

        Comment comment = new CommentBuilder().id(1).application(application1).comment("This is a generic Comment").build();
        ReviewComment reviewComment = new ReviewCommentBuilder().application(application2).id(2).decline(false).comment("This is a review comment")
                .commentType(CommentType.REVIEW).build();
        Comment comment1 = new CommentBuilder().id(3).application(application1).comment("This is another generic Comment").build();

        RegisteredUser reviewer = new RegisteredUserBuilder().comments(comment1, comment, reviewComment)
                .roles(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        assertFalse(reviewer.hasRespondedToProvideReviewForApplication(application1));
    }

    @Test
    public void shouldReturnTrueIfUserIsInterviewerOfApplicationAndHasProvidedInterview() {

        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).build();

        Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").build();
        InterviewComment interviewComment = new InterviewCommentBuilder().application(application).id(2).decline(false).comment("This is an interview comment")
                .commentType(CommentType.INTERVIEW).build();
        Comment comment1 = new CommentBuilder().id(3).application(application).comment("This is another generic Comment").build();

        RegisteredUser interviewer = new RegisteredUserBuilder().comments(comment1, comment, interviewComment)
                .roles(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        assertTrue(interviewer.hasRespondedToProvideInterviewFeedbackForApplication(application));
    }

    @Test
    public void shouldReturnFalseIfInterviewerUserRespondedInPreviousRoundsButNotInLatest() {
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).build();
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(1).build();
        Interviewer interviewer1 = new InterviewerBuilder().interview(new InterviewBuilder().id(1).build()).id(1).user(interviewerUser1).build();
        Interviewer interviewer2 = new InterviewerBuilder().id(1).user(interviewerUser1).build();
        Interview interview1 = new InterviewBuilder().id(1).interviewers(interviewer1).build();
        Interview interview2 = new InterviewBuilder().id(1).interviewers(interviewer2).build();
        interviewer1.setInterview(interview1);
        interviewer2.setInterview(interview2);

        application.setInterviews(Arrays.asList(interview1, interview2));
        application.setLatestInterview(interview2);
        assertFalse(interviewerUser1.hasRespondedToProvideInterviewFeedbackForApplication(application));

    }

    @Test
    public void shouldReturnTrueIfInterviewerUserRespondedInLatestRound() {
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).build();
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(1).build();
        Interviewer interviewer1 = new InterviewerBuilder().id(1).user(interviewerUser1).build();
        Interviewer interviewer2 = new InterviewerBuilder().interview(new InterviewBuilder().id(2).build()).id(1).user(interviewerUser1).build();
        Interview reviewRound1 = new InterviewBuilder().id(1).interviewers(interviewer1).build();
        Interview reviewRound2 = new InterviewBuilder().id(2).interviewers(interviewer2).build();
        interviewer1.setInterview(reviewRound1);

        interviewer2.setInterview(reviewRound2);
        interviewer2.setInterviewComment(new InterviewCommentBuilder().id(2).build());

        application.setInterviews(Arrays.asList(reviewRound1, reviewRound2));
        application.setLatestInterview(reviewRound2);
        assertTrue(interviewerUser1.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application));
    }

    @Test
    public void shouldReturnFalseIfReviewerUserRespondedInPreviousRoundsButNotInLatest() {
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).build();
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).build();
        Reviewer reviewer1 = new ReviewerBuilder().review(new ReviewCommentBuilder().id(1).build()).id(1).user(reviewerUser1).build();
        Reviewer reviewer2 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
        ReviewRound reviewRound1 = new ReviewRoundBuilder().id(1).reviewers(reviewer1).build();
        ReviewRound reviewRound2 = new ReviewRoundBuilder().id(1).reviewers(reviewer2).build();
        reviewer1.setReviewRound(reviewRound1);
        reviewer2.setReviewRound(reviewRound2);

        application.setReviewRounds(Arrays.asList(reviewRound1, reviewRound2));
        application.setLatestReviewRound(reviewRound2);
        assertFalse(reviewerUser1.hasRespondedToProvideReviewForApplicationLatestRound(application));

    }

    @Test
    public void shouldReturnFalseIfReviewerUserDidNotRespondInAnyRound() {
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).build();
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
        Reviewer reviewer2 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
        ReviewRound reviewRound1 = new ReviewRoundBuilder().id(1).reviewers(reviewer1).build();
        ReviewRound reviewRound2 = new ReviewRoundBuilder().id(1).reviewers(reviewer2).build();
        reviewer1.setReviewRound(reviewRound1);
        reviewer2.setReviewRound(reviewRound2);

        application.setReviewRounds(Arrays.asList(reviewRound1, reviewRound2));
        application.setLatestReviewRound(reviewRound2);
        assertFalse(reviewerUser1.hasRespondedToProvideReviewForApplicationLatestRound(application));

    }

    @Test
    public void shouldReturnTrueIfReviewerUserRespondedInLatestRoundAndInPrevious() {
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).build();
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).build();
        Reviewer reviewer1 = new ReviewerBuilder().review(new ReviewCommentBuilder().id(1).build()).id(1).user(reviewerUser1).build();
        Reviewer reviewer2 = new ReviewerBuilder().review(new ReviewCommentBuilder().id(2).build()).id(1).user(reviewerUser1).build();
        ReviewRound reviewRound1 = new ReviewRoundBuilder().id(1).reviewers(reviewer1).build();
        ReviewRound reviewRound2 = new ReviewRoundBuilder().id(1).reviewers(reviewer2).build();
        reviewer1.setReviewRound(reviewRound1);
        reviewer2.setReviewRound(reviewRound2);

        application.setReviewRounds(Arrays.asList(reviewRound1, reviewRound2));
        application.setLatestReviewRound(reviewRound2);
        assertTrue(reviewerUser1.hasRespondedToProvideReviewForApplicationLatestRound(application));

    }

    @Test
    public void shouldReturnTrueIfReviewerUserRespondedInLatestRound() {
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).build();
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
        Reviewer reviewer2 = new ReviewerBuilder().review(new ReviewCommentBuilder().id(2).build()).id(1).user(reviewerUser1).build();
        ReviewRound reviewRound1 = new ReviewRoundBuilder().id(1).reviewers(reviewer1).build();
        ReviewRound reviewRound2 = new ReviewRoundBuilder().id(1).reviewers(reviewer2).build();
        reviewer1.setReviewRound(reviewRound1);
        reviewer2.setReviewRound(reviewRound2);

        application.setReviewRounds(Arrays.asList(reviewRound1, reviewRound2));
        application.setLatestReviewRound(reviewRound2);
        assertTrue(reviewerUser1.hasRespondedToProvideReviewForApplicationLatestRound(application));

    }

    @Test
    public void shouldReturnFalseIfUserIsInterviewerOfApplicationButHasNotProvidedInterviewFeedback() {

        RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
                .build();
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).id(1).build();

        Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").build();
        Comment comment1 = new CommentBuilder().id(3).application(application).comment("This is another generic Comment").build();

        RegisteredUser interviewer = new RegisteredUserBuilder().comments(comment1, comment).roles(new RoleBuilder().id(Authority.INTERVIEWER).build())
                .username("email").firstName("bob").lastName("bobson").email("email@test.com").build();
        assertFalse(interviewer.hasRespondedToProvideInterviewFeedbackForApplication(application));
    }

    @Test
    public void shouldReturnFalseIfUserIsNotInterviewerForThisInApplication() {

        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm application1 = new ApplicationFormBuilder().program(program).id(1).build();
        ApplicationForm application2 = new ApplicationFormBuilder().program(program).id(2).build();

        Comment comment = new CommentBuilder().id(1).application(application1).comment("This is a generic Comment").build();
        InterviewComment interviewComment = new InterviewCommentBuilder().application(application2).id(2).decline(false)
                .comment("This is an interview comment").commentType(CommentType.INTERVIEW).build();
        Comment comment1 = new CommentBuilder().id(3).application(application1).comment("This is another generic Comment").build();

        RegisteredUser interviewer = new RegisteredUserBuilder().comments(comment1, comment, interviewComment)
                .roles(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        assertFalse(interviewer.hasRespondedToProvideInterviewFeedbackForApplication(application1));
    }

    @Test
    public void shouldThrowExceptionIfLatestReviewRoundIsNull() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).build();
        try {
            user.getReviewerForCurrentUserFromLatestReviewRound(applicationForm);
            fail("the latestReviewRound is null and should have thrown an IllegalStateException");
        } catch (IllegalStateException e) {
            // do nothing
        }
    }

    @Test
    public void shouldReturnInterviewersForApplicationForm() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(new Interview()).id(7).build();
        Interviewer interviewerOne = new InterviewerBuilder().id(7).user(user).build();
        Interviewer interviewerTwo = new InterviewerBuilder().id(8).user(new RegisteredUserBuilder().id(9).build()).build();
        Interviewer interviewerThree = new InterviewerBuilder().id(9).user(user).build();

        applicationForm.getLatestInterview().getInterviewers().addAll(Arrays.asList(interviewerOne, interviewerTwo, interviewerThree));
        List<Interviewer> interviewers = user.getInterviewersForApplicationForm(applicationForm);
        assertEquals(2, interviewers.size());
        assertTrue(interviewers.containsAll(Arrays.asList(interviewerOne, interviewerThree)));
    }

    @Test
    public void shouldReturnEmptyListfNotInterviewerForApplicationForm() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(new Interview()).id(7).build();
        assertTrue(user.getInterviewersForApplicationForm(applicationForm).isEmpty());
    }

    @Test
    public void shouldHaveAdminRightsOnAppIfAdministratorInApplicationProgram() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().administrators(user).build())
                .status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasAdminRightsOnApplication(applicationForm));
    }

    @Test
    public void shouldHaveAdminRightsOnAppIfAdministratorInApplicationProject() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new Program()).project(new ProjectBuilder().administrator(user).build())
                .status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasAdminRightsOnApplication(applicationForm));
    }

    @Test
    public void shouldHaveAdminRightsOnAppIfSuperadmin() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasAdminRightsOnApplication(applicationForm));
    }

    @Test
    public void shouldNotHaveAdminRightsOnAppNeihterAdministratorOfProgramOrApplication() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new Program()).status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(user.hasAdminRightsOnApplication(applicationForm));
    }

    @Test
    public void shouldNotHaveAdminRightsOnUnsubmitteApplication() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationAdministrator(user).status(ApplicationFormStatus.UNSUBMITTED).build();
        assertFalse(user.hasAdminRightsOnApplication(applicationForm));
    }

    @Test
    public void shouldNotHaveStaffRigstOnlyIfInRefereeRoleOnly() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.REFEREE).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().build()).status(ApplicationFormStatus.VALIDATION)
                .referees(new RefereeBuilder().user(user).build()).build();
        assertFalse(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

    @Test
    public void shouldNotHaveStaffRigstOnlyIfApplicant() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().build()).status(ApplicationFormStatus.VALIDATION)
                .applicant(user).build();
        assertFalse(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

    @Test
    public void shouldHaveStaffRigstIfAdmin() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().administrators(user).build())
                .status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

    @Test
    public void shouldHaveStaffRigstIfReviewer() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        Reviewer reviewer = new ReviewerBuilder().id(1).user(user).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().id(4).reviewers(reviewer).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().build()).status(ApplicationFormStatus.VALIDATION)
                .reviewRounds(reviewRound).build();
        assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

    @Test
    public void shouldHaveStaffRigstIfInterviweer() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.INTERVIEWER).build()).build();
        Interviewer interviewer = new InterviewerBuilder().id(1).user(user).build();
        Interview interview = new InterviewBuilder().id(4).interviewers(interviewer).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().build()).status(ApplicationFormStatus.VALIDATION)
                .interviews(interview).build();
        assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

    @Test
    public void shouldHaveStaffRigstIfSupervisor() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.SUPERVISOR).build()).build();
        Supervisor supervisor = new SupervisorBuilder().id(1).user(user).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(4).supervisors(supervisor).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().build()).status(ApplicationFormStatus.APPROVAL)
                .approvalRounds(approvalRound).build();
        assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

    @Test
    public void shouldHaveStaffRigstIfApprover() {
        Program program = new ProgramBuilder().id(4).build();
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.APPROVER).build()).programsOfWhichApprover(program)
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

    @Test
    public void shouldHaveStaffRightsIfSuperadmin() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().build()).status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

    @Test
    public void shouldHaveStaffRigstIfReviewerAndReferee() {
        RegisteredUser user = new RegisteredUserBuilder().id(8)
                .roles(new RoleBuilder().id(Authority.REVIEWER).build(), new RoleBuilder().id(Authority.REFEREE).build()).build();
        Reviewer reviewer = new ReviewerBuilder().id(1).user(user).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().id(4).reviewers(reviewer).build();
        Referee referee = new RefereeBuilder().id(7).user(user).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().build()).status(ApplicationFormStatus.VALIDATION)
                .reviewRounds(reviewRound).referees(referee).build();
        assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

    }

}
