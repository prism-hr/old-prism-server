package com.zuehlke.pgadmissions.services;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.UserFactory;
import com.zuehlke.pgadmissions.validators.AccountValidator;
import com.zuehlke.pgadmissions.validators.SwitchAndLinkUserAccountDTOValidator;

public class LinkUserAccountsAcceptanceTest extends AutomaticRollbackTestCase {

    private RoleDAO roleDAO;
    private UserDAO userDAO;
    private UserFactory userFactoryMock;
    private MimeMessagePreparatorFactory mimeMessageFactoryMock;
    private JavaMailSender mailsenderMock;
    private MessageSource msgSourceMock;
    private AccountValidator accountValidatorMock;
    private SwitchAndLinkUserAccountDTOValidator validator;
    private SwitchUserService authenticationProviderMock;
//
//    @Before
//    public void prepare() {
//        roleDAO = new RoleDAO(sessionFactory);
//        userDAO = new UserDAO(sessionFactory);
//        userFactoryMock = EasyMock.createMock(UserFactory.class);
//        mimeMessageFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
//        mailsenderMock = EasyMock.createMock(JavaMailSender.class);
//        msgSourceMock = EasyMock.createMock(MessageSource.class);
//        accountValidatorMock = EasyMock.createMock(AccountValidator.class);
//        validator = new SwitchAndLinkUserAccountDTOValidator();
//        authenticationProviderMock = EasyMock.createMock(PgAdmissionSwitchUserAuthenticationProvider.class);
//    }
//    
//    @Test
//    public void shouldLinkTwoUnrelatedAccounts() {
//        final RegisteredUser currentAccount = new RegisteredUserBuilder().accountNonExpired(true)
//                .accountNonLocked(true).enabled(true).activationCode("abc").email("sdsdkjds@A.com").username("sdsdkjds").password("password")
//                .build();
//        
//        final RegisteredUser secondAccount = new RegisteredUserBuilder().accountNonExpired(true).accountNonLocked(true)
//                .enabled(true).activationCode("abcd").email("3743784h@B.com").username("3743784h").password("password").build();
//        
//        save(currentAccount, secondAccount);
//        flushAndClearSession();
//        
//        UserService userService = new UserService(userDAO, roleDAO, userFactoryMock, mimeMessageFactoryMock, mailsenderMock, msgSourceMock, new EncryptionUtils()) {
//            @Override
//            public RegisteredUser getCurrentUser() {
//                return userDAO.get(currentAccount.getId());
//            }
//            
//            @Override
//            public RegisteredUser getUserByEmail(String email) {
//                return userDAO.get(secondAccount.getId());
//            }
//        };
//        
//        AccountController accountController = new AccountController(userService, accountValidatorMock, validator, authenticationProviderMock);
//
//        SwitchAndLinkUserAccountDTO dto = new SwitchAndLinkUserAccountDTO();
//        dto.setEmail(secondAccount.getEmail());
//
//        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
//        ModelMap modelMap = EasyMock.createMock(ModelMap.class);
//        
//        accountController.linkAccounts(dto, bindingResult, modelMap);
//        
//        flushAndClearSession();
//        
//        RegisteredUser relCurrentAccount = userDAO.get(currentAccount.getId());
//        RegisteredUser relSecondAccount = userDAO.get(secondAccount.getId());
//        
//        assertEquals(1, relCurrentAccount.getLinkedAccounts().size());
//        assertEquals(relSecondAccount.getId(), relCurrentAccount.getLinkedAccounts().get(0).getId());
//        assertEquals(1, relSecondAccount.getAllLinkedAccounts().size());
//    }
//    
//    @Test
//    public void shouldLinkAThirdAccountToAnExistingPrimaryAccount() {
//        final RegisteredUser primaryAccount = new RegisteredUserBuilder().accountNonExpired(true)
//                .accountNonLocked(true).enabled(true).activationCode("abc").email("sdsdkjds@A.com").username("sdsdkjds").password("password")
//                .build();
//        
//        final RegisteredUser secondAccount = new RegisteredUserBuilder().accountNonExpired(true).accountNonLocked(true)
//                .enabled(true).activationCode("abcd").email("3743784h@B.com").username("3743784h").password("password").build();
//        
//        final RegisteredUser thirdAccount = new RegisteredUserBuilder().accountNonExpired(true).accountNonLocked(true)
//                .enabled(true).activationCode("abcd").email("usd7384e73@B.com").username("usd7384e73").password("password").build();
//        
//        primaryAccount.setPrimaryAccount(null);
//        secondAccount.setPrimaryAccount(primaryAccount);
//        
//        save(primaryAccount, secondAccount, thirdAccount);
//        flushAndClearSession();
//        
//        UserService userService = new UserService(userDAO, roleDAO, userFactoryMock, mimeMessageFactoryMock, mailsenderMock, msgSourceMock, new EncryptionUtils()) {
//            @Override
//            public RegisteredUser getCurrentUser() {
//                return userDAO.get(secondAccount.getId());
//            }
//            
//            @Override
//            public RegisteredUser getUserByEmail(String email) {
//                return userDAO.get(thirdAccount.getId());
//            }
//        };
//        
//        AccountController accountController = new AccountController(userService, accountValidatorMock, validator, authenticationProviderMock);
//
//        SwitchAndLinkUserAccountDTO dto = new SwitchAndLinkUserAccountDTO();
//        dto.setEmail(thirdAccount.getEmail());
//
//        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
//        ModelMap modelMap = EasyMock.createMock(ModelMap.class);
//        
//        accountController.linkAccounts(dto, bindingResult, modelMap);
//        
//        flushAndClearSession();
//        
//        RegisteredUser relSecondAccount = userDAO.get(secondAccount.getId());
//        RegisteredUser relCurrentAccount = userDAO.get(primaryAccount.getId());
//        
//        assertEquals(2, relCurrentAccount.getLinkedAccounts().size());
//        assertEquals(primaryAccount.getId(), relSecondAccount.getPrimaryAccount().getId());
//        assertEquals(2, relSecondAccount.getAllLinkedAccounts().size());
//    }
//    
//    @Test
//    public void shouldDeletePrimaryAccountAndReInitialiseTheLeafAccounts() {
//        final RegisteredUser primaryAccount = new RegisteredUserBuilder().accountNonExpired(true)
//                .accountNonLocked(true).enabled(true).activationCode("abc").email("sdsdkjds@A.com").username("sdsdkjds").password("password")
//                .build();
//        
//        final RegisteredUser secondAccount = new RegisteredUserBuilder().accountNonExpired(true).accountNonLocked(true)
//                .enabled(true).activationCode("abcd").email("3743784h@B.com").username("3743784h").password("password").build();
//        
//        final RegisteredUser thirdAccount = new RegisteredUserBuilder().accountNonExpired(true).accountNonLocked(true)
//                .enabled(true).activationCode("abcd").email("usd7384e73@B.com").username("usd7384e73").password("password").build();
//        
//        primaryAccount.setPrimaryAccount(null);
//        secondAccount.setPrimaryAccount(primaryAccount);
//        thirdAccount.setPrimaryAccount(primaryAccount);
//        
//        save(primaryAccount, secondAccount, thirdAccount);
//        flushAndClearSession();
//        
//        UserService userService = new UserService(userDAO, roleDAO, userFactoryMock, mimeMessageFactoryMock, mailsenderMock, msgSourceMock, new EncryptionUtils()) {
//            @Override
//            public RegisteredUser getCurrentUser() {
//                return userDAO.get(secondAccount.getId());
//            }
//            
//            @Override
//            public RegisteredUser getUserByEmail(String email) {
//                return userDAO.get(primaryAccount.getId());
//            }
//        };
//        
//        AccountController accountController = new AccountController(userService, accountValidatorMock, validator, authenticationProviderMock);
//
//        accountController.deleteLinkedAccount(primaryAccount.getEmail());
//        
//        flushAndClearSession();
//        
//        RegisteredUser relPrimaryAccount = userDAO.get(primaryAccount.getId());
//        RegisteredUser relSecondAccount = userDAO.get(secondAccount.getId());
//        RegisteredUser relThirdAccount = userDAO.get(thirdAccount.getId());
//        
//        assertEquals(null, relPrimaryAccount.getPrimaryAccount());
//        assertEquals(null, relSecondAccount.getPrimaryAccount());
//        assertEquals(secondAccount.getId(), relThirdAccount.getPrimaryAccount().getId());
//        
//        assertEquals(1, relSecondAccount.getLinkedAccounts().size());
//        assertEquals(relSecondAccount.getId(), relThirdAccount.getPrimaryAccount().getId());
//        assertEquals(1, relThirdAccount.getAllLinkedAccounts().size());
//    }
//    
//    @Test
//    public void shouldLinkTwoPrimaryGroups() {
//        final RegisteredUser primaryAccount = new RegisteredUserBuilder().accountNonExpired(true)
//                .accountNonLocked(true).enabled(true).activationCode("abc").email("sdsdkjds@A.com").username("sdsdkjds").password("password")
//                .build();
//        
//        final RegisteredUser secondAccount = new RegisteredUserBuilder().accountNonExpired(true).accountNonLocked(true)
//                .enabled(true).activationCode("abcd").email("3743784h@B.com").username("3743784h").password("password").build();
//        
//        final RegisteredUser secondPrimaryAccount = new RegisteredUserBuilder().accountNonExpired(true).accountNonLocked(true)
//                .enabled(true).activationCode("abcd").email("usd7384e73@B.com").username("usd7384e73").password("password").build();
//        
//        final RegisteredUser thirdAccount = new RegisteredUserBuilder().accountNonExpired(true).accountNonLocked(true)
//                .enabled(true).activationCode("abcd").email("23uwhdb@B.com").username("23uwhdb").password("password").build();
//        
//        primaryAccount.setPrimaryAccount(null);
//        secondAccount.setPrimaryAccount(primaryAccount);
//        
//        secondPrimaryAccount.setPrimaryAccount(null);
//        thirdAccount.setPrimaryAccount(secondPrimaryAccount);
//        
//        save(primaryAccount, secondAccount, secondPrimaryAccount, thirdAccount);
//        flushAndClearSession();
//        
//        UserService userService = new UserService(userDAO, roleDAO, userFactoryMock, mimeMessageFactoryMock, mailsenderMock, msgSourceMock, new EncryptionUtils()) {
//            @Override
//            public RegisteredUser getCurrentUser() {
//                return userDAO.get(thirdAccount.getId());
//            }
//            
//            @Override
//            public RegisteredUser getUserByEmail(String email) {
//                return userDAO.get(secondAccount.getId());
//            }
//        };
//        
//        AccountController accountController = new AccountController(userService, accountValidatorMock, validator, authenticationProviderMock);
//
//        SwitchAndLinkUserAccountDTO dto = new SwitchAndLinkUserAccountDTO();
//        dto.setEmail(secondAccount.getEmail());
//
//        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
//        ModelMap modelMap = EasyMock.createMock(ModelMap.class);
//        
//        accountController.linkAccounts(dto, bindingResult, modelMap);
//        
//        flushAndClearSession();
//
//        RegisteredUser relPrimaryAccount = userDAO.get(primaryAccount.getId());
//        RegisteredUser relSecondAccount = userDAO.get(secondAccount.getId());
//        RegisteredUser relSecondPrimaryAccount = userDAO.get(secondPrimaryAccount.getId());
//        RegisteredUser relThirdAccount = userDAO.get(thirdAccount.getId());
//        
//        assertEquals(null, secondPrimaryAccount.getPrimaryAccount());
//        assertEquals(secondPrimaryAccount.getId(), relPrimaryAccount.getPrimaryAccount().getId());
//        assertEquals(secondPrimaryAccount.getId(), relSecondAccount.getPrimaryAccount().getId());
//        assertEquals(secondPrimaryAccount.getId(), relThirdAccount.getPrimaryAccount().getId());        
//      
//        assertEquals(3, relThirdAccount.getAllLinkedAccounts().size());
//        assertEquals(3, relSecondPrimaryAccount.getLinkedAccounts().size());
//    } 
}
