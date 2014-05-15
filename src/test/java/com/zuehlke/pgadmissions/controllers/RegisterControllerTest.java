package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class RegisterControllerTest {

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private RegisterFormValidator regusterFormValidatorMock;

    @Mock
    @InjectIntoByType
    private RegistrationService registrationServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationFormService;

    @Mock
    @InjectIntoByType
    private ProgramService programServiceMock;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelper;

    private MockHttpSession mockHttpSession;

    @TestedObject
    private RegistrationController registerController;

    @Test
    public void shouldReturnRegisterPageIfRedirectedFromPrismInternally() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("referer", "http://localhost:8080/pgadmissions/programs");
        assertEquals("public/register/register_applicant",
                registerController.getRegisterPage(null, null, new ExtendedModelMap(), mockHttpServletRequest, mockHttpSession));
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
    }

    @Test
    public void shouldReturnLoginPageIfRedirectedFromOutsidePrism() {
        assertEquals("redirect:/login",
                registerController.getRegisterPage(null, null, new ExtendedModelMap(), new MockHttpServletRequest(), mockHttpSession));
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));

    }

    @Test
    public void shouldRedirectToDirectURLIfUserExistsIsEnabledAndHasADirectURL() {
        String activationCode = "ABCDD";
        User pendingUser = new User().withAccount(new UserAccount().withEnabled(true));
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(pendingUser);
        EasyMock.replay(userServiceMock);
        assertEquals("redirect:/directHere",
                registerController.getRegisterPage(activationCode,  null, new ExtendedModelMap(), new MockHttpServletRequest(), mockHttpSession));
        EasyMock.verify(userServiceMock);
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
    }

    @Test
    public void shouldReturnRegisterPageIfUserExistsIsNOTEnabledAndHasADirectURL() {
        String activationCode = "ABCDD";
        User pendingUser = new User().withAccount(new UserAccount().withEnabled(false));
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(pendingUser);
        EasyMock.replay(userServiceMock);
        assertEquals("public/register/register_applicant",
                registerController.getRegisterPage(activationCode,  null, new ExtendedModelMap(), new MockHttpServletRequest(), mockHttpSession));
        EasyMock.verify(userServiceMock);
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
    }

    @Test
    public void shouldSaveRedirectUrlInSessionIfUserExistsIsNOTEnabledAndHasADirectUrl() {
        String activationCode = "ABCDD";

        User user = new User().withAccount(new UserAccount().withEnabled(false)).withActivationCode(activationCode).withId(1);
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);

        MockHttpServletRequest requestMock = new MockHttpServletRequest();
        requestMock.setSession(mockHttpSession);

        EasyMock.replay(userServiceMock);

        String page = registerController.getRegisterPage(activationCode, null, new ExtendedModelMap(), requestMock, mockHttpSession);
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
        assertEquals("public/register/register_applicant", page);
        assertEquals("/directHere", mockHttpSession.getAttribute("directToUrl"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnNewUserIfBlankActivationCode() {
        User pendingUser = registerController.getPendingUser(StringUtils.EMPTY);
        assertNull(pendingUser);
    }


    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundIfUserDoesNotExists() {
        EasyMock.expect(userServiceMock.getUserByActivationCode("Abc")).andReturn(null);
        EasyMock.replay(userServiceMock);
        registerController.getPendingUser("Abc");
    }

    @Test
    public void shouldReturnToRegistrationPageIfErrors() {
        User pendingUser = new User().withId(4);
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock, registrationServiceMock);

        String modelAndView = registerController.submitRegistration(pendingUser, errorsMock, new ExtendedModelMap(), new MockHttpServletRequest());
        assertEquals("public/register/register_applicant", modelAndView);

        EasyMock.verify(registrationServiceMock);
    }

    @Test
    public void shouldCreateAndSaveNewUserIfNoErrors() {
        User pendingUser = new User().withId(1);
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        EasyMock.expect(registrationServiceMock.submitRegistration(pendingUser, null)).andReturn(pendingUser);

        EasyMock.replay(registrationServiceMock);

        String view = registerController.submitRegistration(pendingUser, errorsMock, new ExtendedModelMap(), new MockHttpServletRequest());
        assertEquals("public/register/registration_complete", view);

        EasyMock.verify(registrationServiceMock);
    }

    @Test
    public void shouldGetQueryStringFromSessionAndSetOnUserIfAvailable() {
        User pendingUser = new User().withId(1);
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

        EasyMock.expect(registrationServiceMock.submitRegistration(pendingUser, null)).andReturn(pendingUser);

        EasyMock.replay(registrationServiceMock);

        MockHttpServletRequest request = new MockHttpServletRequest();
        mockHttpSession.setAttribute("requestAdvertId", 84);
        request.setSession(mockHttpSession);
        String view = registerController.submitRegistration(pendingUser, errorsMock, new ExtendedModelMap(), request);
        assertEquals("public/register/registration_complete", view);

        EasyMock.verify(registrationServiceMock);
    }

    @Test
    public void shouldResendConfirmationEmail() {
        User user = new User().withId(1);

        EasyMock.expect(userServiceMock.getUserByActivationCode("abc")).andReturn(user);
        registrationServiceMock.resendConfirmationEmail(user);
        EasyMock.replay(userServiceMock, registrationServiceMock);

        String view = registerController.resendConfirmation("abc", new ExtendedModelMap());

        EasyMock.verify(userServiceMock, registrationServiceMock);
        assertEquals("public/register/registration_complete", view);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourveNotFoundIfUserIsNotFound() {
        EasyMock.expect(userServiceMock.getUserByActivationCode("abc")).andReturn(null);
        EasyMock.replay(userServiceMock);
        registerController.resendConfirmation("abc", new ExtendedModelMap());

    }

    @Test
    public void shouldActivateAccountAndRedirectToApplicationListIfNoDirectURL() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        User user = new User().withId(1).withActivationCode(activationCode).withAccount(new UserAccount().withEnabled(false))
                ;
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock);
        String view = registerController.activateAccountSubmit(activationCode, null, null, new MockHttpServletRequest());
        EasyMock.verify(userServiceMock);
        assertEquals("redirect:/applications?activationCode=" + activationCode, view);
        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldActivateAccountAndRedirectToDirectURLIfProvided() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        User user = new User().withId(1).withActivationCode(activationCode)
                .withEmail("email@email.com")
                .withAccount(new UserAccount().withEnabled(false)
                .withPassword("1234"));
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock);
        String view = registerController.activateAccountSubmit(activationCode, null, null, new MockHttpServletRequest());
        EasyMock.verify(userServiceMock);
        assertEquals("redirect:/directLink?activationCode=" + activationCode, view);
        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldActivateAccountAndRedirectToDirectURLIfProvidedAtRegistrationTime() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";

        User user = new User().withId(1).withActivationCode(activationCode).withAccount(new UserAccount().withEnabled(false)
                .withPassword("1234"));

        MockHttpServletRequest requestMock = new MockHttpServletRequest();
        mockHttpSession.putValue("directToUrl", "/directLink");
        requestMock.setSession(mockHttpSession);

        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);

        userServiceMock.save(user);

        EasyMock.replay(userServiceMock);

        String view = registerController.activateAccountSubmit(activationCode, null, null, requestMock);

        EasyMock.verify(userServiceMock);

        assertEquals("redirect:/directLink?activationCode=" + activationCode, view);

        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldCreateNewApplicationAndRedirectToItIfQueryStringExistsOnUser() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        Advert advert = new Program();
        Program program = new Program().withId(1);
        User user = new User().withId(1).withActivationCode(activationCode).withAccount(new UserAccount().withEnabled(false)
                .withPassword("1234"));
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        Map<String, String> parsedParams = new HashMap<String, String>(3);
        parsedParams.put("program", "code");
        EasyMock.expect(programServiceMock.getValidProgramProjectAdvert(null)).andReturn(program);
        userServiceMock.save(user);

        replay();
        String view = registerController.activateAccountSubmit(activationCode, null, null,  new MockHttpServletRequest());
        assertEquals("redirect:/application?applicationId=ABC&activationCode=" + activationCode, view);
        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldCreateNewApplicationAndRedirectToItIfQueryStringExistsOnUserWithProject() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        Advert advert = new Program();
        Project project = new Project().withId(1);
        User user = new User().withId(1).withActivationCode(activationCode).withAccount(new UserAccount().withEnabled(false)
                .withPassword("1234"));
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        Map<String, String> parsedParams = new HashMap<String, String>(3);
        parsedParams.put("program", "code");
        parsedParams.put("project", "1");
        EasyMock.expect(programServiceMock.getValidProgramProjectAdvert(null)).andReturn(project);
        userServiceMock.save(user);

        replay();
        String view = registerController.activateAccountSubmit(activationCode, null, null, new MockHttpServletRequest());
        assertEquals("redirect:/application?applicationId=ABC&activationCode=" + activationCode, view);
        assertTrue(user.isEnabled());
    }

    @Test(expected = CannotApplyException.class)
    public void shouldThrowExceptionIfRegisteringForAnInvalidOpportunity() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        Advert advert = new Program();
        User user = new User().withId(1).withActivationCode(activationCode).withAccount(new UserAccount().withEnabled(false)
                .withPassword("1234"));
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        Map<String, String> parsedParams = new HashMap<String, String>(3);
        parsedParams.put("program", "code");
        parsedParams.put("advert", "1");
        EasyMock.expect(programServiceMock.getValidProgramProjectAdvert(1)).andThrow(new CannotApplyException());

        userServiceMock.save(user);
        replay();
        registerController.activateAccountSubmit(activationCode, null, null, new MockHttpServletRequest());
    }

    @Test
    public void shouldReturnToRegistrationPageIfNouserFound() throws ParseException {
        String activationCode = "differentactivationcode";
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(null);
        EasyMock.replay(userServiceMock);
        String view = registerController.activateAccountSubmit(activationCode, null, null, new MockHttpServletRequest());
        assertEquals("public/register/activation_failed", view);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
