package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class RegistrationServiceTest {

    private RegistrationService registrationService;

    private EncryptionUtils encryptionUtilsMock;

    private RoleDAO roleDAOMock;

    private UserDAO userDAOMock;

    private InterviewerDAO interviewerDAOMock;

    private ReviewerDAO reviewerDAOMock;

    private SupervisorDAO supervisorDAOMock;

    private RefereeDAO refereeDAOMock;

    private MailSendingService mailServiceMock;

    @Test
    public void shouldHashPasswordsAndSetAccountDataAndAndQueryString() {
        String queryString = "queryString";
        Role role = new RoleBuilder().id(Authority.APPLICANT).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.APPLICANT)).andReturn(role);
        EasyMock.replay(roleDAOMock);

        RegisteredUser pendingUser = new RegisteredUser();
        pendingUser.setFirstName("Mark");
        pendingUser.setLastName("Euston");
        pendingUser.setEmail("meuston@gmail.com");
        pendingUser.setPassword("1234");
        pendingUser.setConfirmPassword("1234");

        EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("5678");
        EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("abc");
        EasyMock.replay(encryptionUtilsMock);

        RegisteredUser newUser = registrationService.processPendingApplicantUser(pendingUser, queryString);

        assertEquals("meuston@gmail.com", newUser.getEmail());
        assertEquals("Mark", newUser.getFirstName());
        assertEquals("Euston", newUser.getLastName());
        assertEquals("5678", newUser.getPassword());

        assertTrue(newUser.isAccountNonExpired());
        assertTrue(newUser.isAccountNonLocked());
        assertTrue(newUser.isCredentialsNonExpired());
        assertFalse(newUser.isEnabled());
        assertEquals("abc", newUser.getActivationCode());
        assertTrue(newUser.isInRole(Authority.APPLICANT));
        assertEquals(queryString, newUser.getOriginalApplicationQueryString());
    }

    @Test
    public void shouldhasPasswordsOnPendingSuggestedUser() {
        RegisteredUser pendingSuggestedUser = new RegisteredUser();
        pendingSuggestedUser.setFirstName("Mark");
        pendingSuggestedUser.setLastName("Euston");
        pendingSuggestedUser.setEmail("meuston@gmail.com");
        pendingSuggestedUser.setPassword("1234");
        pendingSuggestedUser.setConfirmPassword("1234");
        userDAOMock.save(EasyMock.isA(RegisteredUser.class));
        EasyMock.expect(userDAOMock.get(2)).andReturn(new RegisteredUser());
        EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("121");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("1234");
        EasyMock.replay(userDAOMock, encryptionUtilsMock);
        RegisteredUser updateUser = registrationService.updateOrSaveUser(pendingSuggestedUser, StringUtils.EMPTY);
        Assert.assertEquals("Mark", updateUser.getFirstName());
        Assert.assertEquals("Euston", updateUser.getLastName());
        Assert.assertEquals("meuston@gmail.com", updateUser.getEmail());
        Assert.assertEquals("meuston@gmail.com", updateUser.getUsername());
        Assert.assertEquals("1234", updateUser.getPassword());
    }

    @Test
    public void shouldClearNotificationDatesFromPendingNotifications() {
        RegisteredUser databaseUser = new RegisteredUserBuilder()
                .id(4)
                .email("test@test.com")
                .enabled(false)
                .activationCode("abc")
                .pendingRoleNotifications(new PendingRoleNotificationBuilder().id(1).notificationDate(new Date()).build(),
                        new PendingRoleNotificationBuilder().id(2).notificationDate(new Date()).build()).build();
        registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, interviewerDAOMock, reviewerDAOMock, supervisorDAOMock,
                refereeDAOMock, mailServiceMock);
        registrationService.sendInstructionsToRegisterIfActivationCodeIsMissing(databaseUser);
        Assert.assertTrue(databaseUser.getPendingRoleNotifications().size() > 0);
        for (PendingRoleNotification roleNotification : databaseUser.getPendingRoleNotifications()) {
            Assert.assertNull(roleNotification.getNotificationDate());
        }
    }

    @Test
    public void shouldClearNotificationDatesFromReferee() {
        RegisteredUser databaseUser = new RegisteredUserBuilder().id(4).email("someEmail@email.com").enabled(false).activationCode("abc").build();
        Referee referee = new RefereeBuilder().id(1).lastNotified(new Date()).user(databaseUser).build();
        EasyMock.expect(refereeDAOMock.getRefereeByUser(databaseUser)).andReturn(referee);
        refereeDAOMock.save(referee);
        EasyMock.replay(refereeDAOMock);
        registrationService.sendInstructionsToRegisterIfActivationCodeIsMissing(databaseUser);
        EasyMock.verify(refereeDAOMock);
        Assert.assertNull(referee.getLastNotified());
    }

    @Test
    public void shouldSavePendingApplicantUserAndSendEmail() throws UnsupportedEncodingException {
        final RegisteredUser expectedRecord = new RegisteredUser();

        final RegisteredUser newUser = new RegisteredUserBuilder().id(1).email("email@test.com").firstName("bob").lastName("bobson").build();
        registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, interviewerDAOMock, reviewerDAOMock, supervisorDAOMock,
                refereeDAOMock, mailServiceMock) {

            @Override
            public RegisteredUser processPendingApplicantUser(RegisteredUser record, String queryString) {
                if (expectedRecord == record && "queryString".equals(queryString)) {
                    return newUser;
                }
                return null;
            }
        };

        userDAOMock.save(newUser);

        mailServiceMock.sendRegistrationConfirmation(newUser);

        EasyMock.replay(userDAOMock, mailServiceMock);

        registrationService.updateOrSaveUser(expectedRecord, "queryString");

        EasyMock.verify(userDAOMock, mailServiceMock);
    }

    @Test
    public void shouldSavePendingSuggestedUserAndSendEmail() throws UnsupportedEncodingException {
        final RegisteredUser expectedRecord = new RegisteredUserBuilder().id(1).activationCode("ABCD").build();
        final RegisteredUser suggestedUser = new RegisteredUserBuilder().id(1).activationCode("ABCD").email("email@test.com").firstName("bob")
                .lastName("bobson").roles(new RoleBuilder().id(Authority.APPLICANT).build()).build();

        registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, interviewerDAOMock, reviewerDAOMock, supervisorDAOMock,
                refereeDAOMock, mailServiceMock) {
        };

        EasyMock.expect(userDAOMock.getUserByActivationCode(expectedRecord.getActivationCode())).andReturn(suggestedUser);

        userDAOMock.save(suggestedUser);

        mailServiceMock.sendRegistrationConfirmation(suggestedUser);

        EasyMock.replay(userDAOMock, mailServiceMock);

        registrationService.updateOrSaveUser(expectedRecord, "queryString");

        EasyMock.verify(userDAOMock, mailServiceMock);
    }

    @Test
    public void shouldNotSendEmailIfSaveFails() {
        final RegisteredUser expectedRecord = new RegisteredUser();
        expectedRecord.setEmail("email@test.com");
        final RegisteredUser newUser = new RegisteredUserBuilder().id(1).build();
        registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, interviewerDAOMock, reviewerDAOMock, supervisorDAOMock,
                refereeDAOMock, mailServiceMock) {

            @Override
            public RegisteredUser processPendingApplicantUser(RegisteredUser record, String queryString) {
                if (expectedRecord == record && "queryString".equals(queryString)) {
                    return newUser;
                }
                return null;
            }

        };

        userDAOMock.save(newUser);
        EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

        EasyMock.replay(userDAOMock, mailServiceMock);
        try {
            registrationService.updateOrSaveUser(expectedRecord, "queryString");
        } catch (RuntimeException e) {
            // expected...ignore
        }

        EasyMock.verify(userDAOMock, mailServiceMock);

    }

    @Test
    public void shouldDelegateFindUserToDAO() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        EasyMock.expect(userDAOMock.getUserByActivationCode("code")).andReturn(user);
        EasyMock.replay(userDAOMock);
        RegisteredUser foundUser = registrationService.findUserForActivationCode("code");
        assertEquals(user, foundUser);

    }

    @Before
    public void setup() {
        userDAOMock = EasyMock.createMock(UserDAO.class);
        roleDAOMock = EasyMock.createMock(RoleDAO.class);

        interviewerDAOMock = EasyMock.createMock(InterviewerDAO.class);
        reviewerDAOMock = EasyMock.createMock(ReviewerDAO.class);
        supervisorDAOMock = EasyMock.createMock(SupervisorDAO.class);
        refereeDAOMock = EasyMock.createMock(RefereeDAO.class);

        encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
        mailServiceMock = EasyMock.createMock(MailSendingService.class);

        registrationService = new RegistrationService(encryptionUtilsMock, roleDAOMock, userDAOMock, interviewerDAOMock, reviewerDAOMock, supervisorDAOMock,
                refereeDAOMock, mailServiceMock);
    }

}