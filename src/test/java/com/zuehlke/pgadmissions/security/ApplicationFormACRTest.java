package com.zuehlke.pgadmissions.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicationFormACRTest {

    private SecurityService securityService;
    
    private UserService userServiceMock;
    
    @Before
    public void prepare() {
        userServiceMock = EasyMock.createMock(UserService.class);
        AccessControlRuleSupport programACR = new ProgramACR();
        AccessControlRuleSupport formACR = new ApplicationFormACR((ProgramACR) programACR);
        securityService = new SecurityService(userServiceMock, Arrays.asList(formACR, programACR));
    }
    
    @Test
    public void shouldReturnTrueIfUserIsApplicantAndOwnerOfForm() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserIsRefereeOfTheApplicationForm() {
        RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        Referee referee = new RefereeBuilder().id(1).user(refereeUser).toReferee();
        ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).status(ApplicationFormStatus.VALIDATION).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(refereeUser);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserIsRefereeOfTheApplicationFormButHasDeclined() {
        RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        Referee referee = new RefereeBuilder().id(1).user(refereeUser).declined(true).toReferee();
        ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).status(ApplicationFormStatus.VALIDATION).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(refereeUser);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseIfUserIsRefereeOfButNotOnApplicationForm() {
        RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(refereeUser);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseIfUserIsApplicantAndNotOwnerOfForm() {
        RegisteredUser applicantOne = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
        RegisteredUser applicantTwo = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicantOne).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicantTwo);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserIsSuperAdministrator() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(administrator);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserInterviewerAndApplicationInInterviewStage() {
        RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).build();
        Interview interview = new InterviewBuilder().id(1).interviewers(new InterviewerBuilder().user(interviewerUser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().interviews(interview).latestInterview(interview).status(ApplicationFormStatus.INTERVIEW).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(interviewerUser);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldNotFailIfLatestInterviewIsNull() {
        RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(interviewerUser);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserReviewerAndApplicationInReviewStage() {
        RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).status(ApplicationFormStatus.REVIEW).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(revieweruser);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseIfUserApproverAndApplicationInValidateStage() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser approver = new RegisteredUserBuilder().id(1).programsOfWhichApprover(program).roles(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).approver(approver).status(ApplicationFormStatus.VALIDATION).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(approver);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserAdminInProgramOfApplication() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).program(new ProgramBuilder().administrators(administrator).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(administrator);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseIfUserAdminInProgramOfApplicationNotSubmitted() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).program(new ProgramBuilder().administrators(administrator).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(administrator);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfApplicationAdmin() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicationAdministrator(administrator).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(administrator);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseIfUserAdminOfApplicationNotSubmitted() {
        RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).applicationAdministrator(administrator).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(administrator);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserIsReviewerOfForm() {
        RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).build()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).status(ApplicationFormStatus.REVIEW).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(revieweruser);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseIfUserIsNotItsReviewer() {
        RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(reviewer);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseForAnyoneNotAnApplicantIfUnsubmittedApplication() {
        RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).build()).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(revieweruser);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseIfUserIsNotItsInterviewer() {
        RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(interviewer);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueForAnApplicantIfUnsubmittedApplication() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicant(applicant).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserIsItsApproverOfProgramToWhichApplicationBelongsAndApplicatioIsInApproval() {
        RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).build();
        Program program = new ProgramBuilder().id(1).approver(approver).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(approver.canSee(applicationForm));
        applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.APPROVAL).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(approver);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnTrueIfUserIsSupervisorOfApplication() {
        RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.SUPERVISOR).build()).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(supervisorUser).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(approvalRound).status(ApplicationFormStatus.APPROVAL).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(supervisorUser);
        EasyMock.replay(userServiceMock);

        assertTrue(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnFalseIfUserIsNotApproverOfProgramToWhichApplicationBelongs() {
        RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).build();
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(approver);
        EasyMock.replay(userServiceMock);

        assertFalse(securityService.hasPermission(applicationForm, "SEE"));
        EasyMock.verify(userServiceMock);
    }
}
