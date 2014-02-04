package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class RegisteredUserMappingTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldSaveAndLoadUserWithSimpleValues() throws Exception {

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").firstName2("bob").firstName3("Claudio").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .originalApplicationQueryString("?hi&hello").build();

        assertNull(user.getId());

        sessionFactory.getCurrentSession().save(user);

        assertNotNull(user.getId());
        Integer id = user.getId();
        RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
        assertSame(user, reloadedUser);

        flushAndClearSession();

        reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
        assertNotSame(user, reloadedUser);
        assertEquals(user.getId(), reloadedUser.getId());
        assertEquals(user.getPassword(), reloadedUser.getPassword());
        assertEquals(user.getUsername(), reloadedUser.getUsername());
        assertEquals(user.getFirstName(), reloadedUser.getFirstName());
        assertEquals(user.getFirstName2(), reloadedUser.getFirstName2());
        assertEquals(user.getFirstName3(), reloadedUser.getFirstName3());
        assertEquals(user.getLastName(), reloadedUser.getLastName());
        assertEquals(user.getEmail(), reloadedUser.getEmail());
        assertEquals("?hi&hello", reloadedUser.getOriginalApplicationQueryString());
        assertFalse(reloadedUser.isAccountNonExpired());
        assertFalse(reloadedUser.isAccountNonLocked());
        assertFalse(reloadedUser.isCredentialsNonExpired());
        assertFalse(reloadedUser.isEnabled());
    }

    @Test
    public void shouldLoadRegisteredUserWithReferees() {

        Referee referee1 = new RefereeBuilder().id(4).firstname("ref").lastname("erre").email("ref@test.com").phoneNumber("hallihallo").build();
        Referee referee2 = new RefereeBuilder().id(5).firstname("ref1").lastname("erre1").email("ref1@test.com").phoneNumber("hallihallo").build();

        RegisteredUser admin1 = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").build();

        sessionFactory.getCurrentSession().save(admin1);
        flushAndClearSession();
        referee1.setUser(admin1);
        referee2.setUser(admin1);

        save(referee1, referee2);

        RegisteredUser reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, admin1.getId()));

        assertTrue(reloadedUser.getReferees().contains(referee1));
        assertTrue(reloadedUser.getReferees().contains(referee2));
        assertNotNull(referee1.getUser());

    }

    @Test
    public void shouldSaveAndLoadRegisteredUserWithReferee() {
        RegisteredUser admin1 = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").build();

        flushAndClearSession();
        assertNull(admin1.getId());

        sessionFactory.getCurrentSession().save(admin1);

        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("ref@test.com").user(admin1).phoneNumber("hallihallo").build();
        save(referee);
        assertNotNull(admin1.getId());
        Integer id = admin1.getId();
        RegisteredUser reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id));

        assertSame(admin1, reloadedUser);

        flushAndClearSession();

        reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id));

        assertEquals(admin1.getId(), reloadedUser.getId());
        assertEquals("email", reloadedUser.getUsername());
        Assert.assertNotNull(reloadedUser.getReferees());
        Assert.assertTrue(listContainsId(referee, reloadedUser.getReferees()));
    }

    @Test
    public void shouldSaveAndLoadUserWithRoles() throws Exception {

        // clear out whatever test data is in there -remember, it will all be
        // rolled back!
        sessionFactory.getCurrentSession().createSQLQuery("delete from PENDING_ROLE_NOTIFICATION").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_ACTION_OPTIONAL").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_ACTION_REQUIRED").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_USER_ROLE").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();

        Role roleOne = new RoleBuilder().id(Authority.APPLICANT).doSendUpdateNotification(false).build();
        Role roleTwo = new RoleBuilder().id(Authority.ADMINISTRATOR).doSendUpdateNotification(false).build();
        save(roleOne, roleTwo);
        flushAndClearSession();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(roleOne).role(roleTwo).build();

        sessionFactory.getCurrentSession().save(user);

        Integer id = user.getId();
        flushAndClearSession();
        RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);

        assertEquals(2, reloadedUser.getRoles().size());
        assertTrue(listContainsId(roleOne, reloadedUser.getRoles()));
        assertTrue(listContainsId(roleTwo, reloadedUser.getRoles()));
    }

    @Test
    public void shouldDeleteRoleMappingWhenDeletingUser() throws Exception {
        // clear out whatever test data is in there -remember, it will all be
        // rolled back!
        sessionFactory.getCurrentSession().createSQLQuery("delete from PENDING_ROLE_NOTIFICATION").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_ACTION_OPTIONAL").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_ACTION_REQUIRED").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_FORM_USER_ROLE").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
        sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();
        Role role = new RoleBuilder().id(Authority.APPLICANT).doSendUpdateNotification(false).build();
        save(role);
        flushAndClearSession();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).role(role).build();

        sessionFactory.getCurrentSession().save(user);

        Integer id = user.getId();

        flushAndClearSession();
        RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
        assertEquals(
                BigInteger.valueOf(1),
                sessionFactory.getCurrentSession()
                        .createSQLQuery("select count(*) from USER_ROLE_LINK where application_role_id = '" + Authority.APPLICANT + "'").uniqueResult());
        sessionFactory.getCurrentSession().delete(reloadedUser);
        flushAndClearSession();

        assertEquals(
                BigInteger.valueOf(0),
                sessionFactory.getCurrentSession()
                        .createSQLQuery("select count(*) from USER_ROLE_LINK where application_role_id = '" + Authority.APPLICANT + "'").uniqueResult());
    }

    @Test
    public void shouldSaveAndLoadProgramsOfWhichAdministrator() throws Exception {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("111111").title("hello").institution(institution).build();
        save(institution, program);
        flushAndClearSession();

        RegisteredUser admin = new RegisteredUserBuilder().programsOfWhichAdministrator(program).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username10").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .build();

        save(admin);

        flushAndClearSession();

        RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, admin.getId());
        assertEquals(1, reloadedUser.getProgramsOfWhichAdministrator().size());
        assertEquals(program.getId(), reloadedUser.getProgramsOfWhichAdministrator().get(0).getId());
    }

    @Test
    public void shouldLoadProgramsOfWhichApprover() throws Exception {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("111111").title("hello").institution(institution).build();
        save(institution, program);
        flushAndClearSession();

        RegisteredUser approver = new RegisteredUserBuilder().programsOfWhichApprover(program).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username10").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .build();

        save(approver);

        flushAndClearSession();

        RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, approver.getId());
        assertEquals(1, reloadedUser.getProgramsOfWhichApprover().size());
        assertEquals(program.getId(), reloadedUser.getProgramsOfWhichApprover().get(0).getId());
    }

    @Test
    public void shouldLoadRegisteredUserWithComments() {
        RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
                .build();

        sessionFactory.getCurrentSession().save(applicant);

        ApplicationForm application = new ApplicationFormBuilder().applicant(applicant).id(1).build();

        sessionFactory.getCurrentSession().save(application);

        Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").build();
        ReviewComment reviewComment = new ReviewCommentBuilder().application(application).id(2).adminsNotified(false).comment("This is a review comment")
                .commentType(CommentType.REVIEW).build();
        Comment comment1 = new CommentBuilder().id(1).application(application).comment("This is another generic Comment").build();

        RegisteredUser admin1 = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").build();

        sessionFactory.getCurrentSession().save(admin1);
        flushAndClearSession();
        comment.setUser(admin1);
        reviewComment.setUser(admin1);
        comment1.setUser(admin1);

        save(comment, comment1, reviewComment);

        RegisteredUser reloadedUser = ((RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, admin1.getId()));

        assertEquals(3, reloadedUser.getComments().size());
        assertTrue(reloadedUser.getComments().contains(comment));
        assertTrue(reloadedUser.getComments().contains(comment1));
        assertTrue(reloadedUser.getComments().contains(reviewComment));
        assertNotNull(comment.getUser());
        assertNotNull(comment1.getUser());
        assertNotNull(reviewComment.getUser());

    }

    @Test
    public void shouldSaveAndLoadPendingRoleNotificationsWithUser() throws ParseException {
        RoleDAO roleDAO = new RoleDAO(sessionFactory);
        Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
        Role interviewerRole = roleDAO.getRoleByAuthority(Authority.INTERVIEWER);

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(institution, program);

        PendingRoleNotification pendingOne = new PendingRoleNotificationBuilder().role(reviewerRole).program(program).build();
        PendingRoleNotification pendingTwo = new PendingRoleNotificationBuilder().role(interviewerRole).program(program).build();

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).pendingRoleNotifications(pendingOne, pendingTwo)
                .build();

        save(user);
        Integer recordOneId = pendingOne.getId();
        assertNotNull(recordOneId);
        assertNotNull(pendingTwo.getId());
        flushAndClearSession();

        RegisteredUser reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, user.getId());
        assertEquals(2, reloadedUser.getPendingRoleNotifications().size());
        assertTrue(listContainsId(pendingOne, reloadedUser.getPendingRoleNotifications()));
        assertTrue(listContainsId(pendingTwo, reloadedUser.getPendingRoleNotifications()));

        pendingOne = (PendingRoleNotification) sessionFactory.getCurrentSession().get(PendingRoleNotification.class, recordOneId);
        reloadedUser.getPendingRoleNotifications().remove(pendingOne);
        save(reloadedUser);
        flushAndClearSession();

        reloadedUser = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, user.getId());
        assertEquals(1, reloadedUser.getPendingRoleNotifications().size());
        assertEquals(pendingTwo.getId(), reloadedUser.getPendingRoleNotifications().get(0).getId());
        assertNull(sessionFactory.getCurrentSession().get(PendingRoleNotification.class, recordOneId));
    }

    private boolean listContainsId(Referee referee, List<Referee> referees) {
        for (Referee entry : referees) {
            if (entry.getId().equals(referee.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean listContainsId(Role role, List<Role> roles) {
        for (Role entry : roles) {
            if (entry.getId().equals(role.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean listContainsId(PendingRoleNotification record, List<PendingRoleNotification> notificationRecords) {
        for (PendingRoleNotification entry : notificationRecords) {
            if (entry.getId().equals(record.getId())) {
                return true;
            }
        }
        return false;
    }
}
