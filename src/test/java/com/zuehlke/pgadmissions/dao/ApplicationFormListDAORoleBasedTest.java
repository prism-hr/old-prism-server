package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
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
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationFormListDAORoleBasedTest extends AutomaticRollbackTestCase {

    private ApplicationFormListDAO applicationDAO;

    private RegisteredUser user;

    private Program program;

    private ApplicationForm application;

    @Before
    public void prepare() {
        applicationDAO = new ApplicationFormListDAO(sessionFactory, null);

        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        program = new ProgramBuilder().code("doesntexist").title("another title").build();

        save(user, program);

        flushAndClearSession();
    }

    @Test
    public void shouldReturnAllSubmittedApplicationsForSuperAdmin() {
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION)
                .submittedDate(new Date()).build();
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED)
                .submittedDate(new Date()).build();
        save(applicationFormOne, applicationFormTwo);
        flushAndClearSession();
        RegisteredUser superAdmin = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();
        
        ApplicationsFiltering filtering = new ApplicationsFiltering();
        filtering.setPreFilter(ApplicationsPreFilter.ALL);
        filtering.setOrder(SortOrder.DESCENDING);
        
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(superAdmin, filtering, 5);
        assertTrue(contains(applicationFormOne, applications));
        assertFalse(contains(applicationFormTwo, applications));
    }

    @Test
    public void shouldReturnOwnApplicationsAndRefereeingApplicationsIfApplicant() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION)
                .build();

        save(applicant, applicationFormOne);
        flushAndClearSession();

        Country country = new CountryBuilder().name("country").code("ZZ").enabled(true).build();

        save(country);
        flushAndClearSession();

        RegisteredUser otherApplicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email2@test.com").username("username3")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        Referee referee = new RefereeBuilder().user(applicant).email("email3@test.com").firstname("bob").lastname("smith").addressCountry(country)
                .address1("london").jobEmployer("zuhlke").jobTitle("se").messenger("skypeAddress").phoneNumber("hallihallo").build();

        save(referee, otherApplicant);
        flushAndClearSession();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).referees(referee).applicant(otherApplicant)
                .status(ApplicationFormStatus.REVIEW).build();

        save(applicationFormTwo);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicant);

        assertTrue("The application form for which the user is an applicant could not be found.", contains(applicationFormOne, applications));
        assertTrue("The application form for which the user is a referee could not be found.", contains(applicationFormTwo, applications));
    }

    @Test
    public void shouldNotReturnApplicationsByOtherApplicant() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();

        save(applicant, applicationFormOne);
        flushAndClearSession();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicant);
        assertFalse(applications.contains(applicationFormOne));
    }

    @Test
    public void shouldReturnAllApplicationsRefereeIsAssignedTo() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("applicant")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).build();
        save(applicant);
        Country country = new CountryBuilder().name("country").code("ZZ").enabled(true).build();
        save(country);
        RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).build();
        save(refereeUser);
        Referee referee = new RefereeBuilder().user(refereeUser).email("email@test.com").firstname("bob").lastname("smith").addressCountry(country)
                .address1("london").jobEmployer("zuhlke").jobTitle("se").messenger("skypeAddress").phoneNumber("hallihallo").build();
        save(referee);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).referees(referee).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();

        save(applicationFormOne);
        flushAndClearSession();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(refereeUser);
        assertTrue(contains(applicationFormOne, applications));

    }

    @Test
    public void shouldReturnAllApplicationsRefereeIsAssignedToAndOfWhichIs() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("applicant")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).build();
        save(applicant);
        Country country = new CountryBuilder().name("country").code("ZZ").enabled(true).build();
        save(country);
        RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).build();
        save(refereeUser);
        Referee referee = new RefereeBuilder().user(refereeUser).email("email@test.com").firstname("bob").lastname("smith").addressCountry(country)
                .address1("london").jobEmployer("zuhlke").jobTitle("se").messenger("skypeAddress").phoneNumber("hallihallo").build();
        save(referee);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).referees(referee).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();

        save(applicationFormOne);
        flushAndClearSession();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(refereeUser);
        assertTrue(contains(applicationFormOne, applications));

    }

    @Test
    public void shouldNotReturnApplicationsRefereeIsNotAssignedTo() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("applicant")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).build();
        save(applicant);
        Country country = new CountryBuilder().name("country").code("ZZ").enabled(true).build();
        save(country);
        RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.REFEREE)).build();
        save(refereeUser);
        Referee referee = new RefereeBuilder().user(refereeUser).email("email@test.com").firstname("bob").lastname("smith").addressCountry(country)
                .address1("london").jobEmployer("zuhlke").jobTitle("se").messenger("skypeAddress").phoneNumber("hallihallo").build();
        save(referee);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION)
                .build();

        save(applicationFormOne);
        flushAndClearSession();
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(refereeUser);
        assertFalse(applications.contains(applicationFormOne));

    }

    @Test
    public void shouldReturnAllSubmittedApplicationsInProgramForAdmin() {
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED).build();
        RegisteredUser admin = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).build();

        save(admin);

        save(applicationFormOne, applicationFormTwo);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(admin);
        assertTrue(contains(applicationFormOne, applications));
        assertFalse(contains(applicationFormTwo, applications));

    }

    @Test
    public void shouldReturnNotReturnApplicationsSubmittedToOtherProgramsForAdmin() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.VALIDATION)
                .build();
        RegisteredUser admin = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).programsOfWhichAdministrator(program).build();

        save(admin);

        save(applicationFormOne);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(admin);

        assertFalse(applications.contains(applicationFormOne));

    }

    @Test
    public void shouldReturnAppsOfWhichApplicationAdministrator() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).build();

        RegisteredUser applicationAdministrator = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        applicationForm.setApplicationAdministrator(applicationAdministrator);

        save(applicationForm, applicationAdministrator);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicationAdministrator);
        assertTrue(contains(applicationForm, applications));

    }

    @Test
    public void shouldNotReturnUbnsubmittedAppsOfWhichApplicationAdministrator() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.UNSUBMITTED).build();

        RegisteredUser applicationAdministrator = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        applicationForm.setApplicationAdministrator(applicationAdministrator);

        save(applicationForm, applicationAdministrator);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicationAdministrator);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsOfWhichReviewerOfLatestReviewRound() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).build();

        RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).build();
        applicationForm.setLatestReviewRound(reviewRound);

        save(applicationForm, reviewerUser, reviewer, reviewRound);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
        assertTrue(contains(applicationForm, applications));
    }

    @Test
    public void shouldNotReturnAppsOfWhichReviewerOfPreviousReviewRound() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).build();

        RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).build();
        applicationForm.getReviewRounds().add(reviewRound);

        save(reviewerUser, applicationForm, reviewerUser, reviewer, reviewRound);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldNotReturnAppsOfWhichReviewerNotInReviewState() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).build();

        RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).build();
        applicationForm.setLatestReviewRound(reviewRound);

        save(applicationForm, reviewerUser, reviewer, reviewRound);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsSubmittedToUsersProgramsAndAppsOfWhichReviewerIfAdminAndReviewer() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser reviewerAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).build();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerAndAdminUser).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationFormOne).reviewers(reviewer).build();
        applicationFormOne.setLatestReviewRound(reviewRound);

        save(reviewerAndAdminUser, applicationFormOne, reviewer, reviewRound);

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(applicationFormTwo);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(reviewerAndAdminUser);

        assertTrue(contains(applicationFormOne, applications));
        assertTrue(contains(applicationFormTwo, applications));
    }

    @Test
    public void shouldReturnAppsOfWhichInterviewerOfLatestInterview() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).build();

        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).build();

        Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();

        applicationForm.setLatestInterview(interview);

        save(applicationForm, interviewerUser, interviewer, interview);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
        assertTrue(contains(applicationForm, applications));
    }

    @Test
    public void shouldNotReturnAppsOfWhichInterviewerOfPreviousInterview() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).build();

        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).build();

        Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();

        save(applicationForm, interviewerUser, interviewer, interview);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldNotReturnAppsOfWhichInterviewerNotInInterviewState() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser interviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REVIEW).build();

        Interviewer interviewer = new InterviewerBuilder().user(interviewerUser).build();

        Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();

        applicationForm.setLatestInterview(interview);

        save(applicationForm, interviewerUser, interviewer, interview);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsOfWhichSupervisorOfLatestApprovalRound() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser supervisorUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.APPROVAL).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.APPROVED).build();
        Supervisor supervisorOne = new SupervisorBuilder().user(supervisorUser).isPrimary(false).build();
        Supervisor supervisorTwo = new SupervisorBuilder().user(supervisorUser).isPrimary(false).build();

        ApprovalRound approvalRoundOne = new ApprovalRoundBuilder().supervisors(supervisorOne).application(applicationFormOne).build();
        ApprovalRound approvalRoundTwo = new ApprovalRoundBuilder().supervisors(supervisorTwo).application(applicationFormTwo).build();

        applicationFormOne.setLatestApprovalRound(approvalRoundOne);
        applicationFormTwo.setLatestApprovalRound(approvalRoundTwo);

        save(applicationFormOne, applicationFormTwo, supervisorUser, supervisorOne, supervisorTwo, approvalRoundOne, approvalRoundTwo);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(supervisorUser);
        assertTrue(contains(applicationFormOne, applications));
        assertTrue(contains(applicationFormTwo, applications));

    }

    @Test
    public void shouldNotReturnAppsOfWhichSupervisorOfLatestApprovalRoundNotInApprovalStage() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser supervisorUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.REJECTED).build();

        Supervisor supervisor = new SupervisorBuilder().user(supervisorUser).isPrimary(false).build();

        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor).application(application).build();

        applicationForm.setLatestApprovalRound(approvalRound);

        save(applicationForm, supervisorUser, supervisor, approvalRound);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(supervisorUser);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsSubmittedToUsersProgramsAndAppsOfWhichInterviewerIsAdminAndInterviewer() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser interviewerAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.INTERVIEW).build();
        Interviewer interviewer = new InterviewerBuilder().user(interviewerAndAdminUser).build();
        Interview interview = new InterviewBuilder().interviewers(interviewer).application(application).build();
        applicationFormOne.setLatestInterview(interview);
        save(applicationFormOne, interviewerAndAdminUser, interviewer, interview);

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(applicationFormTwo);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(interviewerAndAdminUser);

        assertTrue(contains(applicationFormOne, applications));
        assertTrue(contains(applicationFormTwo, applications));
    }

    @Test
    public void shouldReturnApplicationsInProgramForApprover() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).build();

        RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichApprover(program).build();

        save(approver, applicationForm);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approver);
        assertTrue(contains(applicationForm, applications));
    }

    @Test
    public void shouldNotReturnApplicationsInProgramForApproverInNotInApproval() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.INTERVIEW).build();

        RegisteredUser approver = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichApprover(program).build();

        save(approver, applicationForm);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approver);
        assertFalse(applications.contains(applicationForm));

    }

    @Test
    public void shouldReturnAppsSubmittedToUsersProgramsAsAdminAndAprrover() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser approverAndAdminUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).programsOfWhichApprover(otherProgram).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.APPROVAL).build();
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
        save(approverAndAdminUser, applicationFormOne, applicationFormTwo);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(approverAndAdminUser);

        assertTrue(contains(applicationFormOne, applications));
        assertTrue(contains(applicationFormTwo, applications));
    }

    @Test
    public void shouldReturnAppsForWhichHeIsViewer() {
        Program otherProgram = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(otherProgram);

        RegisteredUser viewer = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .programsOfWhichAdministrator(program).programsOfWhichViewer(otherProgram).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(otherProgram).applicant(user).status(ApplicationFormStatus.APPROVAL).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED).build();

        save(viewer, applicationFormOne, applicationFormTwo, applicationFormThree);

        flushAndClearSession();

        ApplicationsFiltering filtering = new ApplicationsFiltering();
        filtering.setPreFilter(ApplicationsPreFilter.ALL);
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(viewer, filtering, 10);

        assertTrue(contains(applicationFormOne, applications));
        assertTrue(contains(applicationFormTwo, applications));
    }
    
    @Test
    public void shouldReturnWithdrawnApplicationsForApplicantEvenThoughTheyHaveBeenWithdrawnBeforeSubmit() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.WITHDRAWN).withdrawnBeforeSubmit(true).build();

        save(applicant, applicationFormOne);
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(applicant);
        assertTrue(contains(applicationFormOne, applications));
    }
    
    @Test
    public void shouldNotReturnWithdrawnApplicationsForAdminWhenTheyHaveBeenWithdrawnBeforeSubmit() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser superAdmin = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.WITHDRAWN).withdrawnBeforeSubmit(true).build();

        save(applicant, applicationFormOne);
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationDAO.getVisibleApplications(superAdmin);
        assertFalse(contains(applicationFormOne, applications));
    }

    private boolean contains(ApplicationForm form, List<ApplicationForm> aplicationForms) {
        for (ApplicationForm entry : aplicationForms) {
            if (form.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
}
