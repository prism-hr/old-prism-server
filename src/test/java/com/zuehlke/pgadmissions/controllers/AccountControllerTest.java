package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.services.SwitchUserService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AccountValidator;
import com.zuehlke.pgadmissions.validators.SwitchAndLinkUserAccountDTOValidator;

@RunWith(PowerMockRunner.class)
public class AccountControllerTest {

    private AccountController accountController;
    private UserService userServiceMock;
    private SwitchUserService switchUserService;
    private SwitchAndLinkUserAccountDTOValidator switchAndLinkAccountDTOValidatorMock;

    private AccountValidator accountValidatorMock;
    private BindingResult bindingResultMock;

    @Test
    public void shouldBindValidator() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(accountValidatorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
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
        User student = new User();
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
        EasyMock.replay(bindingResultMock);
        assertEquals("/private/my_account_section", accountController.saveAccountDetails(student, bindingResultMock));
    }

    @Test
    public void shouldSaveUserIfNoErrorsAccountIsChangedAndReturnAjaxOk() {
        User student = new User();
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        userServiceMock.updateCurrentUser(student);
        EasyMock.replay(bindingResultMock, userServiceMock);
        assertEquals("/private/common/ajax_OK", accountController.saveAccountDetails(student, bindingResultMock));
        EasyMock.verify(bindingResultMock, userServiceMock);
    }

    @Test
    public void shouldReturnCloneOfCurrentUserAsUpdatedUser() {
        User student = new User();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(student);
        EasyMock.replay(userServiceMock);
        User updateUser = accountController.getUpdatedUser();
        assertNotSame(updateUser, student);
        assertEquals(updateUser.getFirstName(), student.getFirstName());
        assertEquals(updateUser.getFirstName2(), student.getFirstName2());
        assertEquals(updateUser.getFirstName3(), student.getFirstName3());
        assertEquals(updateUser.getLastName(), student.getLastName());
        assertEquals(updateUser.getEmail(), student.getEmail());
        assertEquals(updateUser.getPassword(), student.getPassword());
    }

    @Test
    @PrepareForTest(SecurityContextHolder.class)
    public void shouldSwitchUserAccount() {
        PowerMock.mockStatic(SecurityContextHolder.class);

        User currentAccount = new UserBuilder().id(1).enabled(true).activationCode("abc").email("B@A.com").password("password").build();

        User desiredAccount = new UserBuilder().id(2).enabled(true).activationCode("abcd").email("A@B.com").password("password").build();

        desiredAccount.setPrimaryAccount(currentAccount);

        EasyMock.expect(userServiceMock.getUserByEmail(desiredAccount.getEmail())).andReturn(desiredAccount);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentAccount);

        HttpServletRequest requestMock = new MockHttpServletRequest();
        requestMock.getSession().setAttribute("applicationSearchDTO", "fake");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentAccount, desiredAccount);
        token.setDetails(new WebAuthenticationDetails(requestMock));
        EasyMock.expect(switchUserService.authenticate(EasyMock.anyObject(UsernamePasswordAuthenticationToken.class))).andReturn(token);

        // FIXME mock SecurityContext and SecurityContextHolder with Powermock
        // EasyMock.expect(SecurityContextHolder.getContext()).andReturn(mockSecurityContext);
        //
        // mockSecurityContext.setAuthentication(token);
        //
        // PowerMock.replay(SecurityContextHolder.class);
        // EasyMock.replay(userServiceMock, mockSecurityContextHolder, mockSecurityContext, switchUserService);
        //
        // assertEquals("OK", accountController.switchAccounts(desiredAccount.getEmail(), requestMock));
        //
        // EasyMock.verify(userServiceMock, mockSecurityContextHolder, mockSecurityContext, switchUserService);
        PowerMock.verifyAll();

        assertNull(requestMock.getSession().getAttribute("applicationSearchDTO"));

    }

}
