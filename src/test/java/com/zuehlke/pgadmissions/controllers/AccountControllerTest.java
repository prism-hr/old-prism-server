package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.SwitchAndLinkUserAccountDTO;
import com.zuehlke.pgadmissions.security.PgAdmissionSwitchUserAuthenticationProvider;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AccountValidator;
import com.zuehlke.pgadmissions.validators.SwitchAndLinkUserAccountDTOValidator;

@RunWith(PowerMockRunner.class)
public class AccountControllerTest {

	private AccountController accountController;
	private UserService userServiceMock;
	private RegisteredUser student;
	private PgAdmissionSwitchUserAuthenticationProvider authenticationProviderMock;
    private SwitchAndLinkUserAccountDTOValidator switchAndLinkAccountDTOValidatorMock;

	private AccountValidator accountValidatorMock;
	private BindingResult bindingResultMock;
	
    @Mock 
    SecurityContextHolder mockSecurityContextHolder;
    
    @Mock 
    SecurityContext mockSecurityContext;

	@Test
	public void shouldBindValidator() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(accountValidatorMock);
		EasyMock.replay(binderMock);
		accountController.registerValidator(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnMyAccountPage() {
		assertEquals("/private/my_account", accountController.getMyAccountPage());
	}

	@Test
	public void shouldReturnMyAccountSection() {
		assertEquals("/private/my_account_section", accountController.getMyAccountSection());
	}

	@Test
	public void shouldReturnToAccountPageAndNotSaveIfErrors() {
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		Assert.assertEquals("/private/my_account_section", accountController.saveAccountDetails(student, bindingResultMock));
	}

	@Test
	public void shouldSaveUserIfNoErrorsAccountIsChangedAndReturnAjaxOk() {
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
		userServiceMock.updateCurrentUser(student);
		EasyMock.replay(bindingResultMock, userServiceMock);
		Assert.assertEquals("/private/common/ajax_OK", accountController.saveAccountDetails(student, bindingResultMock));
		EasyMock.verify(bindingResultMock, userServiceMock);
	}

	@Test
	public void shouldReturnCloneOfCurrentUserAsUpdatedUser() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(student);
		EasyMock.replay(userServiceMock);
		RegisteredUser updateUser= accountController.getUpdatedUser();
		assertNotSame(updateUser, student);
		assertEquals(updateUser.getFirstName(), student.getFirstName());
		assertEquals(updateUser.getLastName(), student.getLastName());
		assertEquals(updateUser.getEmail(), student.getEmail());
		assertEquals(updateUser.getPassword(), student.getPassword());
	}
	
	@Test
	public void shouldReturnIfAccountsHaveAlreadyBeenLinked() {
        RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true)
                .accountNonLocked(true).enabled(true).activationCode("abc").email("B@A.com").password("password").toUser();

        RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true)
                .accountNonLocked(true).enabled(true).activationCode("abcd").email("A@B.com").password("password").toUser();
	    
        currentAccount.addLinkedAccount(secondAccount);
        
	    SwitchAndLinkUserAccountDTO dto = new SwitchAndLinkUserAccountDTO();
	    dto.setCurrentPassword(currentAccount.getPassword());
	    dto.setEmail(secondAccount.getEmail());
	    dto.setPassword(secondAccount.getPassword());
	    
	    ModelMap modelMapMock = EasyMock.createMock(ModelMap.class);
	    
	    EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
	    EasyMock.expect(userServiceMock.getUserByEmail(secondAccount.getEmail())).andReturn(secondAccount);
	    EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAccount);
	    
	    EasyMock.replay(userServiceMock, bindingResultMock);
	    
	    assertEquals("/private/common/ajax_OK", accountController.linkAccounts(dto, bindingResultMock, modelMapMock));
	    
	    EasyMock.verify(userServiceMock, bindingResultMock);
	}
	
    @Test
    public void shouldReturnFalseIfAccountsDisabled() {
        RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true)
                .accountNonLocked(true).enabled(true).activationCode("abc").email("B@A.com").password("password")
                .toUser();

        RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true).accountNonLocked(true)
                .enabled(false).activationCode("abcd").email("A@B.com").password("password").toUser();

        SwitchAndLinkUserAccountDTO dto = new SwitchAndLinkUserAccountDTO();
        dto.setCurrentPassword(currentAccount.getPassword());
        dto.setEmail(secondAccount.getEmail());
        dto.setPassword(secondAccount.getPassword());

        ModelMap modelMapMock = EasyMock.createMock(ModelMap.class);

        EasyMock.expect(userServiceMock.getUserByEmail(secondAccount.getEmail())).andReturn(secondAccount);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAccount);

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        bindingResultMock.rejectValue("email", "account.not.enabled");

        EasyMock.replay(userServiceMock, bindingResultMock);

        assertEquals("/private/my_account_section",
                accountController.linkAccounts(dto, bindingResultMock, modelMapMock));

        EasyMock.verify(userServiceMock, bindingResultMock);
    }
	
    @Test
    public void shouldReturnFalseIfAccountsIsExpired() {
        RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true)
                .accountNonLocked(true).enabled(true).activationCode("abc").email("B@A.com").password("password")
                .toUser();

        RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(false).accountNonLocked(true)
                .enabled(true).activationCode("abcd").email("A@B.com").password("password").toUser();

        SwitchAndLinkUserAccountDTO dto = new SwitchAndLinkUserAccountDTO();
        dto.setCurrentPassword(currentAccount.getPassword());
        dto.setEmail(secondAccount.getEmail());
        dto.setPassword(secondAccount.getPassword());

        ModelMap modelMapMock = EasyMock.createMock(ModelMap.class);

        EasyMock.expect(userServiceMock.getUserByEmail(secondAccount.getEmail())).andReturn(secondAccount);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAccount);

        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        bindingResultMock.rejectValue("email", "account.not.enabled");

        EasyMock.replay(userServiceMock, bindingResultMock);

        assertEquals("/private/my_account_section",
                accountController.linkAccounts(dto, bindingResultMock, modelMapMock));

        EasyMock.verify(userServiceMock, bindingResultMock);
    }	
	
    @Test
    public void shouldLinkAccounts() {
        RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true)
                .accountNonLocked(true).enabled(true).activationCode("abc").email("B@A.com").password("password")
                .toUser();

        RegisteredUser secondAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true).accountNonLocked(true)
                .enabled(true).activationCode("abcd").email("A@B.com").password("password").toUser();

        SwitchAndLinkUserAccountDTO dto = new SwitchAndLinkUserAccountDTO();
        dto.setCurrentPassword(currentAccount.getPassword());
        dto.setEmail(secondAccount.getEmail());
        dto.setPassword(secondAccount.getPassword());

        ModelMap modelMapMock = EasyMock.createMock(ModelMap.class);

        EasyMock.expect(userServiceMock.getUserByEmail(secondAccount.getEmail())).andReturn(secondAccount);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAccount);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);

        userServiceMock.save(currentAccount);
        userServiceMock.save(secondAccount);
        
        EasyMock.replay(userServiceMock, bindingResultMock);

        assertEquals("/private/common/ajax_OK", accountController.linkAccounts(dto, bindingResultMock, modelMapMock));
        assertEquals(1, currentAccount.getLinkedAccounts().size());
        assertEquals(1, secondAccount.getLinkedAccounts().size());
        assertTrue(currentAccount.getLinkedAccounts().contains(secondAccount));
        assertTrue(secondAccount.getLinkedAccounts().contains(currentAccount));
        
       EasyMock.verify(userServiceMock, bindingResultMock);
    }
    
    @Test
    @PrepareForTest(SecurityContextHolder.class)
    public void shouldSwitchUserAccount() {
        PowerMock.mockStatic(SecurityContextHolder.class);
        
        RegisteredUser currentAccount = new RegisteredUserBuilder().id(1).accountNonExpired(true)
                .accountNonLocked(true).enabled(true).activationCode("abc").email("B@A.com").password("password").toUser();

        RegisteredUser desiredAccount = new RegisteredUserBuilder().id(2).accountNonExpired(true)
                .accountNonLocked(true).enabled(true).activationCode("abcd").email("A@B.com").password("password").toUser();
        
        currentAccount.addLinkedAccount(desiredAccount);
        
        EasyMock.expect(userServiceMock.getUserByEmail(desiredAccount.getEmail())).andReturn(desiredAccount);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAccount);
     
        HttpServletRequest requestMock = new MockHttpServletRequest();
     
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentAccount, desiredAccount);
        token.setDetails(new WebAuthenticationDetails(requestMock));
        EasyMock.expect(authenticationProviderMock.authenticate(EasyMock.anyObject(UsernamePasswordAuthenticationToken.class))).andReturn(token);
        
        EasyMock.expect(SecurityContextHolder.getContext()).andReturn(mockSecurityContext);
        
        mockSecurityContext.setAuthentication(token);
        
        PowerMock.replay(SecurityContextHolder.class);
        EasyMock.replay(userServiceMock, mockSecurityContextHolder, mockSecurityContext, authenticationProviderMock);
        
        assertEquals("/private/common/ajax_OK", accountController.switchAccounts(desiredAccount.getEmail(), requestMock));
        
        EasyMock.verify(userServiceMock, mockSecurityContextHolder, mockSecurityContext, authenticationProviderMock);
        PowerMock.verifyAll();
    }
	
	@Before
	public void setUp() {
		userServiceMock = EasyMock.createMock(UserService.class);
		accountValidatorMock = EasyMock.createMock(AccountValidator.class);
		switchAndLinkAccountDTOValidatorMock = EasyMock.createMock(SwitchAndLinkUserAccountDTOValidator.class);
		authenticationProviderMock = EasyMock.createMock(PgAdmissionSwitchUserAuthenticationProvider.class);
		accountController = new AccountController(userServiceMock, accountValidatorMock, switchAndLinkAccountDTOValidatorMock, authenticationProviderMock);
		bindingResultMock = EasyMock.createMock(BindingResult.class);

		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").password("password").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
	}
}
