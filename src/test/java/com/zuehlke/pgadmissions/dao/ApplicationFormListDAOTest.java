package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
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
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationFormListDAOTest extends AutomaticRollbackTestCase {

    private ApplicationFormListDAO applicationDAO;

    private RegisteredUser user;

    private Program program;

    private ApplicationForm application;

    @Before
    public void setup() {
        applicationDAO = new ApplicationFormListDAO(sessionFactory);

        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).toUser();

        program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

        save(user, program);

        flushAndClearSession();
    }

    @Test
    public void shouldReturnAllSubmittedApplicationsForSuperAdmin() {
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).submittedDate(new Date()).toApplicationForm();
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.UNSUBMITTED).submittedDate(new Date()).toApplicationForm();
        save(applicationFormOne, applicationFormTwo);
        flushAndClearSession();
        RegisteredUser superAdmin = new RegisteredUserBuilder().role(
                new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(superAdmin, null, null,
                SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, 25);
        assertTrue(applications.contains(applicationFormOne));
        assertFalse(applications.contains(applicationFormTwo));
    }

    @Test
    public void shouldReturnOwnApplicationsAndRefereeingApplicationsIfApplicant() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).toApplicationForm();

        save(applicant, applicationFormOne);
        flushAndClearSession();

        Country country = new CountryBuilder().name("country").code("ZZ").enabled(true).toCountry();

        save(country);
        flushAndClearSession();

        RegisteredUser otherApplicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email2@test.com").username("username3").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        Referee referee = new RefereeBuilder().user(applicant).email("email3@test.com").firstname("bob")
                .lastname("smith").addressCountry(country).address1("london").jobEmployer("zuhlke").jobTitle("se")
                .messenger("skypeAddress").phoneNumber("hallihallo").toReferee();

        save(referee, otherApplicant);
        flushAndClearSession();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).referees(referee)
                .applicant(otherApplicant).status(ApplicationFormStatus.REVIEW).toApplicationForm();

        save(applicationFormTwo);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicant);

        assertTrue("The application form for which the user is an applicant could not be found.",
                applications.contains(applicationFormOne));
        assertTrue("The application form for which the user is a referee could not be found.",
                applications.contains(applicationFormTwo));
    }

    @Test
    public void shouldNotReturnApplicationsByOtherApplicant() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).toApplicationForm();

        save(applicant, applicationFormOne);
        flushAndClearSession();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicant);
        assertFalse(applications.contains(applicationFormOne));

    }

    @Test
    public void shouldReturnAllApplicationsRefereeIsAssignedTo() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("applicant").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
        save(applicant);
        Country country = new CountryBuilder().name("country").code("ZZ").enabled(true).toCountry();
        save(country);
        RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
        save(refereeUser);
        Referee referee = new RefereeBuilder().user(refereeUser).email("email@test.com").firstname("bob")
                .lastname("smith").addressCountry(country).address1("london").jobEmployer("zuhlke").jobTitle("se")
                .messenger("skypeAddress").phoneNumber("hallihallo").toReferee();
        save(referee);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).referees(referee)
                .applicant(applicant).status(ApplicationFormStatus.VALIDATION).toApplicationForm();

        save(applicationFormOne);
        flushAndClearSession();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(refereeUser);
        assertTrue(applications.contains(applicationFormOne));

    }

    @Test
    public void shouldReturnAllApplicationsRefereeIsAssignedToAndOfWhichIs() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("applicant").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
        save(applicant);
        Country country = new CountryBuilder().name("country").code("ZZ").enabled(true).toCountry();
        save(country);
        RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
        save(refereeUser);
        Referee referee = new RefereeBuilder().user(refereeUser).email("email@test.com").firstname("bob")
                .lastname("smith").addressCountry(country).address1("london").jobEmployer("zuhlke").jobTitle("se")
                .messenger("skypeAddress").phoneNumber("hallihallo").toReferee();
        save(referee);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).referees(referee)
                .applicant(applicant).status(ApplicationFormStatus.VALIDATION).toApplicationForm();

        save(applicationFormOne);
        flushAndClearSession();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(refereeUser);
        assertTrue(applications.contains(applicationFormOne));

    }

    @Test
    public void shouldNotReturnApplicationsRefereeIsNotAssignedTo() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("applicant").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
        save(applicant);
        Country country = new CountryBuilder().name("country").code("ZZ").enabled(true).toCountry();
        save(country);
        RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).toUser();
        save(refereeUser);
        Referee referee = new RefereeBuilder().user(refereeUser).email("email@test.com").firstname("bob")
                .lastname("smith").addressCountry(country).address1("london").jobEmployer("zuhlke").jobTitle("se")
                .messenger("skypeAddress").phoneNumber("hallihallo").toReferee();
        save(referee);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).toApplicationForm();

        save(applicationFormOne);
        flushAndClearSession();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(refereeUser);
        assertFalse(applications.contains(applicationFormOne));

    }

    @Test
    public void shouldReturnAllSubmittedApplicationsInProgramForAdmin() {
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).toApplicationForm();
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
        RegisteredUser admin = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();

        save(admin);

        save(applicationFormOne, applicationFormTwo);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(admin);
        assertTrue(applications.contains(applicationFormOne));
        assertFalse(applications.contains(applicationFormTwo));

    }

    @Test
    public void shouldReturnNotReturnApplicationsSubmittedToOtherProgramsForAdmin() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).toApplicationForm();
        RegisteredUser admin = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).toUser();

        save(admin);

        save(applicationFormOne);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(admin);

        assertFalse(applications.contains(applicationFormOne));

    }

    @Test
    public void shouldReturnAppsOfWhichApplicationAdministrator() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.REVIEW).toApplicationForm();

        RegisteredUser applicationAdministrator = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        applicationForm.setApplicationAdministrator(applicationAdministrator);

        save(applicationForm, applicationAdministrator);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicationAdministrator);
        assertTrue(applications.contains(applicationForm));

    }

    @Test
    public void shouldNotReturnUbnsubmittedAppsOfWhichApplicationAdministrator() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();

        RegisteredUser applicationAdministrator = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        applicationForm.setApplicationAdministrator(applicationAdministrator);

        save(applicationForm, applicationAdministrator);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicationAdministrator);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsOfWhichReviewerOfLatestReviewRound() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.REVIEW).toApplicationForm();

        RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).toReviewer();

        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer)
                .toReviewRound();
        applicationForm.setLatestReviewRound(reviewRound);

        save(applicationForm, reviewerUser, reviewer, reviewRound);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
        assertTrue(applications.contains(applicationForm));

    }

    @Test
    public void shouldNotReturnAppsOfWhichReviewerOfPreviousReviewRound() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.REVIEW).toApplicationForm();

        RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).toReviewer();

        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer)
                .toReviewRound();
        applicationForm.getReviewRounds().add(reviewRound);

        save(reviewerUser, applicationForm, reviewerUser, reviewer, reviewRound);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldNotReturnAppsOfWhichReviewerNotInReviewState() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.INTERVIEW).toApplicationForm();

        RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).toReviewer();

        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer)
                .toReviewRound();
        applicationForm.setLatestReviewRound(reviewRound);

        save(applicationForm, reviewerUser, reviewer, reviewRound);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsSubmittedToUsersProgramsAndAppsOfWhichReviewerIfAdminAndReviewer() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        RegisteredUser reviewerAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.REVIEW).toApplicationForm();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerAndAdminUser).toReviewer();

        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationFormOne).reviewers(reviewer)
                .toReviewRound();
        applicationFormOne.setLatestReviewRound(reviewRound);

        save(reviewerAndAdminUser, applicationFormOne, reviewer, reviewRound);

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).toApplicationForm();
        save(applicationFormTwo);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerAndAdminUser);

        assertTrue(applications.contains(applicationFormOne));
        assertTrue(applications.contains(applicationFormTwo));

    }

    @Test
    public void shouldReturnAppsOfWhichInterviewerOfLatestInterview() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.INTERVIEW).toApplicationForm();

        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).toInterviewer();

        Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();

        applicationForm.setLatestInterview(interview);

        save(applicationForm, interviewerUser, interviewer, interview);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
        assertTrue(applications.contains(applicationForm));

    }

    @Test
    public void shouldNotReturnAppsOfWhichInterviewerOfPreviousInterview() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.INTERVIEW).toApplicationForm();

        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).toInterviewer();

        Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();

        save(applicationForm, interviewerUser, interviewer, interview);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldNotReturnAppsOfWhichInterviewerNotInInterviewState() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.REVIEW).toApplicationForm();

        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).toInterviewer();

        Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();

        applicationForm.setLatestInterview(interview);

        save(applicationForm, interviewerUser, interviewer, interview);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsOfWhichSupervisorOfLatestApprovalRound() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        RegisteredUser supervisorUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.APPROVAL).toApplicationForm();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.APPROVED).toApplicationForm();
        Supervisor supervisorOne = new SupervisorBuilder().user(supervisorUser).toSupervisor();
        Supervisor supervisorTwo = new SupervisorBuilder().user(supervisorUser).toSupervisor();

        ApprovalRound approvalRoundOne = new ApprovalRoundBuilder().supervisors(supervisorOne)
                .application(applicationFormOne).toApprovalRound();
        ApprovalRound approvalRoundTwo = new ApprovalRoundBuilder().supervisors(supervisorTwo)
                .application(applicationFormTwo).toApprovalRound();

        applicationFormOne.setLatestApprovalRound(approvalRoundOne);
        applicationFormTwo.setLatestApprovalRound(approvalRoundTwo);

        save(applicationFormOne, applicationFormTwo, supervisorUser, supervisorOne, supervisorTwo, approvalRoundOne,
                approvalRoundTwo);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(supervisorUser);
        assertTrue(applications.contains(applicationFormOne));
        assertTrue(applications.contains(applicationFormTwo));

    }

    @Test
    public void shouldNotReturnAppsOfWhichSupervisorOfLatestApprovalRoundNotInApprovalStage() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        RegisteredUser supervisorUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.REJECTED).toApplicationForm();

        Supervisor supervisor = new SupervisorBuilder().user(supervisorUser).toSupervisor();

        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application)
                .toApprovalRound();

        applicationForm.setLatestApprovalRound(approvalRound);

        save(applicationForm, supervisorUser, supervisor, approvalRound);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(supervisorUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsSubmittedToUsersProgramsAndAppsOfWhichInterviewerIsAdminAndInterviewer() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        RegisteredUser interviewerAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).toUser();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
        Interviewer interviewer = new InterviewerBuilder().user(interviewerAndAdminUser).toInterviewer();
        Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).toInterview();
        applicationFormOne.setLatestInterview(interview);
        save(applicationFormOne, interviewerAndAdminUser, interviewer, interview);

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).toApplicationForm();
        save(applicationFormTwo);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerAndAdminUser);

        assertTrue(applications.contains(applicationFormOne));
        assertTrue(applications.contains(applicationFormTwo));

    }

    @Test
    public void shouldReturnApplicationsInProgramForApprover() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.APPROVAL).toApplicationForm();

        RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).toUser();

        save(approver, applicationForm);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approver);
        assertTrue(applications.contains(applicationForm));
    }

    @Test
    public void shouldNotReturnApplicationsInProgramForApproverInNotInApproval() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.INTERVIEW).toApplicationForm();

        RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username2").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).programsOfWhichApprover(program).toUser();

        save(approver, applicationForm);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approver);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsSubmittedToUsersProgramsAsAdminAndAprrover() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").toProgram();
        save(otherProgram);

        RegisteredUser approverAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).programsOfWhichApprover(otherProgram).toUser();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user)
                .status(ApplicationFormStatus.APPROVAL).toApplicationForm();
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user)
                .status(ApplicationFormStatus.VALIDATION).toApplicationForm();
        save(approverAndAdminUser, applicationFormOne, applicationFormTwo);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approverAndAdminUser);

        assertTrue(applications.contains(applicationFormOne));
        assertTrue(applications.contains(applicationFormTwo));

    }
}
