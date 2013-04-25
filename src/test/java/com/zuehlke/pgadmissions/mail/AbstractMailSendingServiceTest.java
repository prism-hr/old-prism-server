package com.zuehlke.pgadmissions.mail;

import static org.easymock.EasyMock.createMock;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class AbstractMailSendingServiceTest {

    private class TestableAbstractMailSendingService extends AbstractMailSendingService {
        public TestableAbstractMailSendingService(
                final MailSender mailSender, 
                final ApplicationFormDAO formDAO,
                final ConfigurationService configurationService, 
                final UserDAO userDAO, 
                final RoleDAO roleDAO, 
                final RefereeDAO refereeDAO,
                final EncryptionUtils encryptionUtils) {
            super(mailSender, formDAO, configurationService, userDAO, roleDAO, refereeDAO, encryptionUtils);
        }
    }
    
    private TestableAbstractMailSendingService service;
    
    private MailSender mockMailSender;

    private ConfigurationService configurationServiceMock;
    
    private ApplicationFormDAO applicationFormDAOMock;
    
    private RoleDAO roleDAOMock;
    
    private RefereeDAO refereeDAOMock; 
    
    private EncryptionUtils encryptionUtilsMock;
    
    private UserDAO userDAOMock;
    
    @Before
    public void setup() {
        mockMailSender = createMock(MailSender.class);
        configurationServiceMock = createMock(ConfigurationService.class);
        applicationFormDAOMock = createMock(ApplicationFormDAO.class);
        userDAOMock = createMock(UserDAO.class);
        roleDAOMock = createMock(RoleDAO.class);
        refereeDAOMock = createMock(RefereeDAO.class);
        encryptionUtilsMock = createMock(EncryptionUtils.class);
        userDAOMock = createMock(UserDAO.class);
        service = new TestableAbstractMailSendingService(
                mockMailSender, 
                applicationFormDAOMock, 
                configurationServiceMock, 
                userDAOMock,
                roleDAOMock, 
                refereeDAOMock, 
                encryptionUtilsMock);
    }

    @Test
    public void shouldAddRefereeRoleIfUserExistsAndIsNotAReferee() {
        Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).role(reviewerRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        userDAOMock.save(user);
        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").build();
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
        userDAOMock.save(user);
        EasyMock.replay(userDAOMock);
        RegisteredUser existedReferee = service.processRefereeAndGetAsUser(referee);
        Assert.assertNotNull(existedReferee);
        Assert.assertEquals(2, existedReferee.getRoles().size());
    }

    @Test
    public void shouldAddRefereeRoleIfUserExistsAndIsApproverReviewerAdmin() {
        Role reviewerRole = new RoleBuilder().id(1).authorityEnum(Authority.REVIEWER).build();
        Role adminRole = new RoleBuilder().id(2).authorityEnum(Authority.ADMINISTRATOR).build();
        Role approverRole = new RoleBuilder().id(3).authorityEnum(Authority.APPROVER).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).roles(reviewerRole, adminRole, approverRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        userDAOMock.save(user);
        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").build();
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
        userDAOMock.save(user);
        EasyMock.replay(userDAOMock);
        RegisteredUser existedReferee = service.processRefereeAndGetAsUser(referee);
        Assert.assertNotNull(existedReferee);
        Assert.assertEquals(4, existedReferee.getRoles().size());
    }

    @Test
    public void shouldNotAddRefereeRoleIfUserExistsAndIsAlreadyAReferee() {
        Role refereeRole = new RoleBuilder().authorityEnum(Authority.REFEREE).build();
        RegisteredUser user = new RegisteredUserBuilder().id(3).role(refereeRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        userDAOMock.save(user);
        Referee referee = new RefereeBuilder().firstname("ref").lastname("erre").email("email@test.com").build();
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(user);
        userDAOMock.save(user);
        EasyMock.replay(userDAOMock);
        RegisteredUser existedReferee = service.processRefereeAndGetAsUser(referee);
        Assert.assertNotNull(existedReferee);
        Assert.assertEquals(1, existedReferee.getRoles().size());
    }

    @Test
    public void shouldCreateUserWithRefereeRoleIfRefereeDoesNotExist() {
        final RegisteredUser user = new RegisteredUserBuilder().id(1).accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        Referee referee = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail@test.com").application(new ApplicationFormBuilder().id(1).applicationNumber("abc").build()).build();
        Role role = new RoleBuilder().id(1).build();
        EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.REFEREE)).andReturn(role);
        EasyMock.expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("emailemail@test.com")).andReturn(null);
        
        userDAOMock.save(EasyMock.isA(RegisteredUser.class));
        referee.setUser(user);
        refereeDAOMock.save(referee);
        EasyMock.expect(encryptionUtilsMock.generateUUID()).andReturn("abc");
        EasyMock.replay(userDAOMock, refereeDAOMock, encryptionUtilsMock, roleDAOMock);

        RegisteredUser newUser = service.processRefereeAndGetAsUser(referee);
        EasyMock.verify(refereeDAOMock, userDAOMock);
        Assert.assertNotNull(newUser);
        Assert.assertEquals(1, newUser.getRoles().size());
        Assert.assertEquals(role, newUser.getRoles().get(0));
        Assert.assertEquals("ref", newUser.getFirstName());
        Assert.assertEquals("erre", newUser.getLastName());
        Assert.assertEquals("emailemail@test.com", newUser.getEmail());
        Assert.assertEquals("emailemail@test.com", newUser.getUsername());
        Assert.assertTrue(newUser.isAccountNonExpired());
        Assert.assertTrue(newUser.isAccountNonLocked());
        Assert.assertTrue(newUser.isCredentialsNonExpired());
        Assert.assertFalse(newUser.isEnabled());
        Assert.assertEquals("abc", newUser.getActivationCode());
        Assert.assertEquals(DirectURLsEnum.ADD_REFERENCE.displayValue() + "abc", newUser.getDirectToUrl());
    }
}
