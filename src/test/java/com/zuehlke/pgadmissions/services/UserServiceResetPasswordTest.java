package com.zuehlke.pgadmissions.services;

import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import javax.mail.internet.AddressException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationsFilteringDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class UserServiceResetPasswordTest {

    @Mock
    @InjectIntoByType
    private UserDAO userDAOMock;

    @Mock
    @InjectIntoByType
    private RoleDAO roleDAOMock;

    @Mock
    @InjectIntoByType
    private ApplicationsFilteringDAO filteringDAOMock;

    @Mock
    @InjectIntoByType
    private EncryptionUtils encryptionUtilsMock;

    @Mock
    @InjectIntoByType
    private MailSendingService mailServiceMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @TestedObject
    private UserService service;

    @Test
    public void ignoreInvalidEmails() {
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("aaaa")).andReturn(null);

        replay();
        service.resetPassword("aaaa");
        verify();
    }

    @Test
    public void generateNewPasswordAndSendMail() throws AddressException {
        String oldPassword = "i forget this every time";
        User storedUser = new User().withId(23).withFirstName("first").withLastName("last").withEmail("first@last.com")
                .withAccount(new UserAccount().withPassword(oldPassword));
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("aaaa")).andReturn(storedUser);

        String newPassword = "this is better";
        String hashedNewPassword = "some ol' celtic bollocks";
        EasyMock.expect(encryptionUtilsMock.generateUserPassword()).andReturn(newPassword);

        mailServiceMock.sendResetPasswordMessage(storedUser, newPassword);

        EasyMock.expect(encryptionUtilsMock.getMD5Hash(newPassword)).andReturn(hashedNewPassword);
        userDAOMock.save(storedUser);
        EasyMock.expectLastCall();

        replay();
        service.resetPassword("aaaa");
        verify();

        Assert.assertEquals(hashedNewPassword, storedUser.getPassword());
    }

    @Test
    public void keepOldPasswordIfMailSendFails() throws AddressException {
        String oldPassword = "i forget this every time";
        User storedUser = new User().withId(23).withFirstName("first").withLastName("last")//
                .withAccount(new UserAccount().withPassword(oldPassword));
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("aaaa")).andReturn(storedUser);

        String newPassword = "this is better";
        EasyMock.expect(encryptionUtilsMock.generateUserPassword()).andReturn(newPassword);

        mailServiceMock.sendResetPasswordMessage(storedUser, newPassword);
        EasyMock.expectLastCall().andThrow(new RuntimeException("intentional exception"));

        replay();
        service.resetPassword("aaaa");
        verify();

        Assert.assertEquals(oldPassword, storedUser.getPassword());
    }
}
