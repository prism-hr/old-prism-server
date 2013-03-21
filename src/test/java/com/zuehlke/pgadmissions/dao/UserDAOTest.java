package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class UserDAOTest extends AutomaticRollbackTestCase {
    private UserDAO userDAO;

    @Test
    public void shouldSaveAndLoadUser() throws Exception {

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        assertNull(user.getId());

        userDAO.save(user);

        assertNotNull(user.getId());
        Integer id = user.getId();
        RegisteredUser reloadedUser = userDAO.get(id);
        assertSame(user, reloadedUser);

        flushAndClearSession();

        reloadedUser = userDAO.get(id);
        assertNotSame(user, reloadedUser);
        assertEquals(user.getId(), reloadedUser.getId());

    }

    @Test
    public void shouldFindUsersByUsername() throws Exception {

        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("otherusername").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();

        save(userOne, userTwo);

        flushAndClearSession();

        RegisteredUser foundUser = userDAO.getUserByUsername("username");
        assertEquals(userOne.getId(), foundUser.getId());

    }

    @Test
    public void shouldFindUsersByActivationCode() throws Exception {

        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).activationCode("xyz").build();
        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("otherusername").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).activationCode("def").build();

        save(userOne, userTwo);

        flushAndClearSession();

        RegisteredUser foundUser = userDAO.getUserByActivationCode("xyz");
        assertEquals(userOne.getId(), foundUser.getId());

    }

    @Test
    public void shouldFindDisabledUsersByEmail() throws Exception {
        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email1@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).activationCode("xyz").build();

        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email1@test.com")
                .username("otherusername").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).activationCode("def").build();

        save(userOne, userTwo);

        flushAndClearSession();

        RegisteredUser foundUser = userDAO.getDisabledUserByEmail("email1@test.com");
        assertEquals(userOne.getId(), foundUser.getId());
    }

    @Test
    public void shouldGetUsersByRole() {
        // clear out whatever test data is in there -remember, it will all be
        // rolled back!
        sessionFactory.getCurrentSession().createSQLQuery("delete from PENDING_ROLE_NOTIFICATION").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();

        Role roleOne = new RoleBuilder().authorityEnum(Authority.APPLICANT).build();
        Role roleTwo = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        save(roleOne, roleTwo);
        flushAndClearSession();

        RegisteredUser userOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).role(roleOne).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("otherusername").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).roles(roleOne, roleTwo).build();

        save(userOne, userTwo);

        flushAndClearSession();

        List<RegisteredUser> usersInRole = userDAO.getUsersInRole(roleTwo);
        assertEquals(1, usersInRole.size());
        assertEquals(userTwo.getId(), usersInRole.get(0).getId());

        usersInRole = userDAO.getUsersInRole(roleOne);
        assertEquals(2, usersInRole.size());
        assertTrue(listContainsId(userOne, usersInRole));
        assertTrue(listContainsId(userTwo, usersInRole));
    }

    @Test
    public void shouldGetUsersByProgramme() {

        Program programOne = new ProgramBuilder().code("111111").title("hello").build();
        Program programTwo = new ProgramBuilder().code("222222").title("hello").build();

        save(programOne, programTwo);

        flushAndClearSession();

        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        int numberOfExistingSuperAdminUsers = userDAO.getUsersInRole(superAdminRole).size();

        RegisteredUser superAdminOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username1").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        
        RegisteredUser superAdminTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username2").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(true).roles(superAdminRole).build();
        
        RegisteredUser superAdminThree = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username3").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(true).roles(superAdminRole).build();

        RegisteredUser approverOne = new RegisteredUserBuilder().programsOfWhichApprover(programOne).firstName("Jane")
                .lastName("Doe").email("email@test.com").username("username4").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser approverTwo = new RegisteredUserBuilder().programsOfWhichApprover(programTwo).firstName("Jane")
                .lastName("Doe").email("email@test.com").username("username5").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser approverThree = new RegisteredUserBuilder().programsOfWhichApprover(programOne)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username6").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        RegisteredUser reviewerOne = new RegisteredUserBuilder().programsOfWhichReviewer(programOne).firstName("Jane")
                .lastName("Doe").email("email@test.com").username("username7").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser reviewerTwo = new RegisteredUserBuilder().programsOfWhichReviewer(programTwo).firstName("Jane")
                .lastName("Doe").email("email@test.com").username("username8").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser reviewerThree = new RegisteredUserBuilder().programsOfWhichReviewer(programOne)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username9").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        RegisteredUser interviewerOne = new RegisteredUserBuilder().programsOfWhichInterviewer(programOne)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username14").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser interviewerTwo = new RegisteredUserBuilder().programsOfWhichInterviewer(programTwo)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username15").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser interviewerThree = new RegisteredUserBuilder().programsOfWhichInterviewer(programOne)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username16").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        RegisteredUser supervisorOne = new RegisteredUserBuilder().programsOfWhichSupervisor(programOne)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username17").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser supervisorTwo = new RegisteredUserBuilder().programsOfWhichSupervisor(programTwo)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username18").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser supervisorThree = new RegisteredUserBuilder().programsOfWhichSupervisor(programOne)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username19").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        RegisteredUser administratorOne = new RegisteredUserBuilder().programsOfWhichAdministrator(programOne)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username10").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser administratorTwo = new RegisteredUserBuilder().programsOfWhichAdministrator(programTwo)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username11").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser administratorThree = new RegisteredUserBuilder().programsOfWhichAdministrator(programOne)
                .firstName("Jane").lastName("Doe").email("email@test.com").username("username12").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        RegisteredUser reviewerAndApprover = new RegisteredUserBuilder().programsOfWhichReviewer(programOne)
                .programsOfWhichApprover(programOne).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username13").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();
        
        RegisteredUser viewer = new RegisteredUserBuilder().programsOfWhichViewer(programOne)
                .programsOfWhichApprover(programOne).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username20").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).build();

        save(superAdminOne, superAdminTwo, superAdminThree, administratorOne, administratorThree, administratorTwo,
                approverOne, approverThree, approverTwo, reviewerOne, reviewerThree, reviewerTwo, interviewerOne,
                interviewerTwo, interviewerThree, reviewerAndApprover, supervisorOne, supervisorTwo, supervisorThree, viewer);

        flushAndClearSession();

        List<RegisteredUser> usersInProgram = userDAO.getUsersForProgram(programOne);
        assertEquals(numberOfExistingSuperAdminUsers + 15, usersInProgram.size());
        assertTrue(listContainsId(reviewerAndApprover, usersInProgram));
        assertTrue(listContainsId(superAdminOne, usersInProgram));
        assertTrue(listContainsId(superAdminThree, usersInProgram));
        assertTrue(listContainsId(superAdminTwo, usersInProgram));
        assertTrue(listContainsId(reviewerOne, usersInProgram));
        assertTrue(listContainsId(reviewerThree, usersInProgram));
        assertTrue(listContainsId(interviewerOne, usersInProgram));
        assertTrue(listContainsId(interviewerThree, usersInProgram));
        assertTrue(listContainsId(approverOne, usersInProgram));
        assertTrue(listContainsId(approverThree, usersInProgram));
        assertTrue(listContainsId(administratorOne, usersInProgram));
        assertTrue(listContainsId(administratorThree, usersInProgram));
        assertTrue(listContainsId(supervisorOne, usersInProgram));
        assertTrue(listContainsId(supervisorThree, usersInProgram));
        assertTrue(listContainsId(viewer, usersInProgram));
    }

    @Test
    public void shouldReturnSuperAdmininistrator() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        RegisteredUser superAdmin = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username1").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(superAdmin);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(superAdmin, users));
    }

    @Test
    public void shouldReturnAdmininistrator() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.ADMINISTRATOR);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username1").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldReturnReviewer() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username1").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldReturnInteverviweer() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username1").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldReturnApprover() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.APPROVER);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username1").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldNotReturnApplicant() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role superAdminRole = roleDAO.getRoleByAuthority(Authority.APPLICANT);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username1").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).role(superAdminRole).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertFalse(users.contains(user));
    }

    @Test
    public void shouldNotReturnReferee() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username1").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).role(roleDAO.getRoleByAuthority(Authority.REFEREE)).build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertFalse(users.contains(user));
    }

    @Test
    public void shouldReturnEachUserOnlyOnce() {
        List<RegisteredUser> usersBefore = userDAO.getInternalUsers();
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username1").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true)
                .roles(roleDAO.getRoleByAuthority(Authority.APPROVER), roleDAO.getRoleByAuthority(Authority.REVIEWER))
                .build();
        save(user);
        flushAndClearSession();
        List<RegisteredUser> users = userDAO.getInternalUsers();
        assertEquals(usersBefore.size() + 1, users.size());
    }

    @Test
    public void shouldReturnUserWithPendingNotifications() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program)
                .build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole)
                .program(program).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingOne, pendingTwo).build();
        save(user);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getUsersWithPendingRoleNotifications();
        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldReturnUserWithPendingNotificationsOnlyOnce() {
        List<RegisteredUser> users = userDAO.getUsersWithPendingRoleNotifications();
        int previousNumberOfUsers = users.size();
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program)
                .build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole)
                .program(program).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingOne, pendingTwo).build();
        save(user);
        flushAndClearSession();

        users = userDAO.getUsersWithPendingRoleNotifications();
        assertEquals(previousNumberOfUsers + 1, users.size());
    }

    @Test
    public void shouldNotReturnUserWithPendingNotificationsIfDateIsNull() {
        List<RegisteredUser> users = userDAO.getUsersWithPendingRoleNotifications();
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);

        Date now = new Date();

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program)
                .notificationDate(now).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole)
                .program(program).notificationDate(now).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingOne, pendingTwo).build();
        save(user);
        flushAndClearSession();

        users = userDAO.getUsersWithPendingRoleNotifications();
        assertFalse(users.contains(user));
    }

    @Test
    public void shouldNotReturnEnalbedUserWithPendingNotifications() {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        save(program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program)
                .build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole)
                .program(program).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(true).pendingRoleNotifications(pendingOne, pendingTwo).build();
        save(user);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getUsersWithPendingRoleNotifications();
        assertFalse(users.contains(user));
    }

    @Test
    public void shouldNotReturnUserWithNoPendingNotifications() {

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
        save(user);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getUsersWithPendingRoleNotifications();
        assertFalse(users.contains(user));
    }

    @Test
    public void shouldReturnUserIfInterviewerOfAnyInterview() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("somethingelse@test.com").username("somethingelse").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(applicant, program);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();
        Interview interview = new InterviewBuilder().application(applicationForm)
                .interviewers(new InterviewerBuilder().user(user).build()).dueDate(new Date()).furtherDetails("rr")
                .locationURL("").build();
        save(user, applicationForm, interview);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getAllPreviousInterviewersOfProgram(program);
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Test
    public void shouldReturnUserIfReviewerOfAnyReviewRound() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("somethingelse@test.com").username("somethingelse").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(applicant, program);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm)
                .reviewers(new ReviewerBuilder().user(user).build()).build();
        save(user, applicationForm, reviewRound);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getAllPreviousReviewersOfProgram(program);
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Test
    public void shouldReturnUserWhoIsReviewerOfLatestRoundOfReviewsWhoAreWillingToInterview() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("somethingelse@test.com").username("somethingelse").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(applicant, program);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();
        Reviewer reviewer = new ReviewerBuilder().user(user).build();
        ReviewComment reviewComment = new ReviewCommentBuilder().user(user).reviewer(reviewer).willingToInterview(true)
                .application(applicationForm).comment("yep").commentType(CommentType.REVIEW).decline(false).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).build();
        applicationForm.setLatestReviewRound(reviewRound);
        save(user, applicationForm, reviewRound, reviewComment);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getReviewersWillingToInterview(applicationForm);

        assertTrue(listContainsId(user, users));
    }

    @Test
    public void shouldNotReturnUserWhoIsReviewerOfPreviousRoundOfReviewsWhoAreWillingToInterview() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("somethingelse@test.com").username("somethingelse").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(applicant, program);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();
        Reviewer reviewer = new ReviewerBuilder().user(user).build();
        ReviewComment reviewComment = new ReviewCommentBuilder().user(user).reviewer(reviewer).willingToInterview(true)
                .application(applicationForm).comment("yep").commentType(CommentType.REVIEW).decline(false).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).build();
        save(user, applicationForm, reviewRound, reviewComment);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getReviewersWillingToInterview(applicationForm);
        assertFalse(users.contains(user));
    }

    @Test
    public void shouldNotReturnUserWhoIsReviewerOfLatestRoundOfReviewsWhoAreNotWillingToInterview() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("somethingelse@test.com").username("somethingelse").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(applicant, program);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();
        Reviewer reviewer = new ReviewerBuilder().user(user).build();
        ReviewComment reviewComment = new ReviewCommentBuilder().user(user).reviewer(reviewer)
                .willingToInterview(false).application(applicationForm).comment("yep").commentType(CommentType.REVIEW)
                .decline(false).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).build();
        applicationForm.setLatestReviewRound(reviewRound);
        save(user, applicationForm, reviewRound, reviewComment);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getReviewersWillingToInterview(applicationForm);

        assertFalse(users.contains(user));
    }

    @Test
    public void shouldReturnUserWhoIsReviewerOfLatestRoundOfReviewsWhoAreWillingToInterviewForOtherApplication() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("somethingelse@test.com").username("somethingelse").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(applicant, program);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();
        ApplicationForm otherApplicationForm = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();
        Reviewer reviewer = new ReviewerBuilder().user(user).build();
        ReviewComment reviewComment = new ReviewCommentBuilder().user(user).reviewer(reviewer).willingToInterview(true)
                .application(applicationForm).comment("yep").commentType(CommentType.REVIEW).decline(false).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).reviewers(reviewer).build();
        applicationForm.setLatestReviewRound(reviewRound);
        save(user, applicationForm, otherApplicationForm, reviewRound, reviewComment);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getReviewersWillingToInterview(otherApplicationForm);

        assertFalse(users.contains(user));
    }

    @Test
    public void shouldReturnUserIfSupervisorOfAnyApprovalRound() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("somethingelse@test.com").username("somethingelse").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("ZZZZZZZ").title("another title").build();
        save(applicant, program);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant)
                .status(ApplicationFormStatus.VALIDATION).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm)
                .supervisors(new SupervisorBuilder().user(user).isPrimary(false).build()).build();
        save(user, applicationForm, approvalRound);
        flushAndClearSession();

        List<RegisteredUser> users = userDAO.getAllPreviousSupervisorsOfProgram(program);
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Before
    public void setup() {
        userDAO = new UserDAO(sessionFactory);
    }

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (user.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
}
