package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProjectException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationFormCreationService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.ApplicationQueryStringParser;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

public class RegisterControllerTest {

    private RegisterController registerController;

    private UserService userServiceMock;

    private RegisterFormValidator regusterFormValidatorMock;

    private RegistrationService registrationServiceMock;

    private ApplicationFormCreationService applicationFormCreationServiceMock;

    private ProgramsService programServiceMock;

    private ApplicationQueryStringParser qureyStringParserMock;

    private EncryptionHelper encryptionHelper;

    private MockHttpSession mockHttpSession;

    private AdvertService advertServiceMock;

    @Before
    public void setUp() {
        regusterFormValidatorMock = EasyMock.createMock(RegisterFormValidator.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        registrationServiceMock = EasyMock.createMock(RegistrationService.class);
        applicationFormCreationServiceMock = EasyMock.createMock(ApplicationFormCreationService.class);
        programServiceMock = EasyMock.createMock(ProgramsService.class);
        qureyStringParserMock = EasyMock.createMock(ApplicationQueryStringParser.class);
        encryptionHelper = EasyMock.createMock(EncryptionHelper.class);
        advertServiceMock = EasyMock.createMock(AdvertService.class);
        registerController = new RegisterController(regusterFormValidatorMock, userServiceMock, registrationServiceMock, applicationFormCreationServiceMock,
                programServiceMock, qureyStringParserMock, encryptionHelper, advertServiceMock);
        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED, true);
    }

    @Test
    public void shouldReturnRegisterPageIfRedirectedFromPrismInternally() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("referer", "http://localhost:8080/pgadmissions/programs");
        assertEquals("public/register/register_applicant",
                registerController.getRegisterPage(null, null, null, new ExtendedModelMap(), mockHttpServletRequest, mockHttpSession));
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
    }

    @Test
    public void shouldReturnLoginPageIfRedirectedFromOutsidePrism() {
        assertEquals("redirect:/login",
                registerController.getRegisterPage(null, null, null, new ExtendedModelMap(), new MockHttpServletRequest(), mockHttpSession));
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));

    }

    @Test
    public void shouldRedirectToDirectURLIfUserExistsIsEnabledAndHasADirectURL() {
        String activationCode = "ABCDD";
        RegisteredUser pendingUser = new RegisteredUserBuilder().enabled(true).directURL("/directHere").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(pendingUser);
        EasyMock.replay(userServiceMock);
        assertEquals("redirect:/directHere",
                registerController.getRegisterPage(activationCode, "/directHere", null, new ExtendedModelMap(), new MockHttpServletRequest(), mockHttpSession));
        EasyMock.verify(userServiceMock);
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
    }

    @Test
    public void shouldReturnRegisterPageIfUserExistsIsNOTEnabledAndHasADirectURL() {
        String activationCode = "ABCDD";
        RegisteredUser pendingUser = new RegisteredUserBuilder().enabled(false).directURL("/directHere").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(pendingUser);
        EasyMock.replay(userServiceMock);
        assertEquals("public/register/register_applicant",
                registerController.getRegisterPage(activationCode, "/directHere", null, new ExtendedModelMap(), new MockHttpServletRequest(), mockHttpSession));
        EasyMock.verify(userServiceMock);
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
    }

    @Test
    public void shouldSaveRedirectUrlInSessionIfUserExistsIsNOTEnabledAndHasADirectUrl() {
        String activationCode = "ABCDD";

        RegisteredUser user = new RegisteredUserBuilder().enabled(false).activationCode(activationCode).directURL("/directHere").id(1).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);

        MockHttpServletRequest requestMock = new MockHttpServletRequest();
        requestMock.setSession(mockHttpSession);

        EasyMock.replay(userServiceMock);

        String page = registerController.getRegisterPage(activationCode, null, null, new ExtendedModelMap(), requestMock, mockHttpSession);
        assertNull(mockHttpSession.getAttribute(LoginController.CLICKED_ON_ALREADY_REGISTERED));
        assertEquals("public/register/register_applicant", page);
        assertEquals("/directHere", mockHttpSession.getAttribute("directToUrl"));
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldFindPendingUserByActivationCode() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode("Abc")).andReturn(user);
        EasyMock.replay(userServiceMock);
        assertEquals(user, registerController.getPendingUser("Abc", null));
        assertNull(user.getDirectToUrl());
    }

    @Test
    public void shouldReturnNewUserIfBlankActivationCode() {
        RegisteredUser pendingUser = registerController.getPendingUser(StringUtils.EMPTY, null);
        assertNull(pendingUser);
    }

    @Test
    public void shouldSetDirectToUrlOnUserIfPRovided() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        EasyMock.expect(userServiceMock.getUserByActivationCode("Abc")).andReturn(user);
        EasyMock.replay(userServiceMock);
        assertEquals(user, registerController.getPendingUser("Abc", "direct/to/here"));
        assertEquals("direct/to/here", user.getDirectToUrl());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundIfUserDoesNotExists() {
        EasyMock.expect(userServiceMock.getUserByActivationCode("Abc")).andReturn(null);
        EasyMock.replay(userServiceMock);
        registerController.getPendingUser("Abc", null);
    }

    @Test
    public void shouldReturnToRegistrationPageIfErrors() {
        RegisteredUser pendingUser = new RegisteredUserBuilder().id(4).build();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock, registrationServiceMock);

        String modelAndView = registerController.submitRegistration(pendingUser, errorsMock, new ExtendedModelMap(), new MockHttpServletRequest());
        assertEquals("public/register/register_applicant", modelAndView);

        EasyMock.verify(registrationServiceMock);
    }

    @Test
    public void shouldCreateAndSaveNewUserIfNoErrors() {
        RegisteredUser pendingUser = new RegisteredUserBuilder().id(1).build();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
        EasyMock.expect(registrationServiceMock.updateOrSaveUser(pendingUser, null)).andReturn(pendingUser);

        EasyMock.replay(registrationServiceMock);

        String view = registerController.submitRegistration(pendingUser, errorsMock, new ExtendedModelMap(), new MockHttpServletRequest());
        assertEquals("public/register/registration_complete", view);

        EasyMock.verify(registrationServiceMock);
    }

    @Test
    public void shouldGetQueryStringFromSessionAndSetOnUserIfAvailable() {
        String queryString = "queryString";
        RegisteredUser pendingUser = new RegisteredUserBuilder().id(1).build();
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

        EasyMock.expect(registrationServiceMock.updateOrSaveUser(pendingUser, queryString)).andReturn(pendingUser);

        EasyMock.replay(registrationServiceMock);

        MockHttpServletRequest request = new MockHttpServletRequest();
        mockHttpSession.setAttribute("applyRequest", queryString);
        request.setSession(mockHttpSession);
        String view = registerController.submitRegistration(pendingUser, errorsMock, new ExtendedModelMap(), request);
        assertEquals("public/register/registration_complete", view);

        EasyMock.verify(registrationServiceMock);
    }

    @Test
    public void shouldResendConfirmationEmail() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();

        EasyMock.expect(userServiceMock.getUserByActivationCode("abc")).andReturn(user);
        registrationServiceMock.sendConfirmationEmail(user);
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
        RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).id(1).activationCode(activationCode)
                .enabled(false).username("email@email.com").email("email@email.com").password("1234").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock);
        String view = registerController.activateAccountSubmit(activationCode, new MockHttpServletRequest());
        EasyMock.verify(userServiceMock);
        assertEquals("redirect:/applications?activationCode=" + activationCode, view);
        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldActivateAccountAndRedirectToDirectURLIfProvided() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        RegisteredUser user = new RegisteredUserBuilder().directURL("/directLink").role(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).id(1)
                .activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock);
        String view = registerController.activateAccountSubmit(activationCode, new MockHttpServletRequest());
        EasyMock.verify(userServiceMock);
        assertEquals("redirect:/directLink?activationCode=" + activationCode, view);
        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldActivateAccountAndRedirectToDirectURLIfProvidedAtRegistrationTime() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";

        RegisteredUser user = new RegisteredUserBuilder().directURL(null).role(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).id(1)
                .activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").build();

        MockHttpServletRequest requestMock = new MockHttpServletRequest();
        mockHttpSession.putValue("directToUrl", "/directLink");
        requestMock.setSession(mockHttpSession);

        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);

        userServiceMock.save(user);

        EasyMock.replay(userServiceMock);

        String view = registerController.activateAccountSubmit(activationCode, requestMock);

        EasyMock.verify(userServiceMock);

        assertEquals("redirect:/directLink?activationCode=" + activationCode, view);

        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldCreatNewApplicationAndRedirectToItIfQueryStringExistsOnUser() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        String queryString = "queryString";
        Program program = new ProgramBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false)
                .username("email@email.com").email("email@email.com").password("1234").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        Map<String, String> parsedParams = new HashMap<String, String>(3);
        parsedParams.put("program", "code");
        EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(parsedParams);
        EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
        userServiceMock.save(user);
        EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(user, program, null)).andReturn(applicationForm);
        EasyMock.replay(userServiceMock, applicationFormCreationServiceMock, programServiceMock, qureyStringParserMock);
        String view = registerController.activateAccountSubmit(activationCode, new MockHttpServletRequest());
        EasyMock.verify(userServiceMock);
        assertEquals("redirect:/application?applicationId=ABC&activationCode=" + activationCode, view);
        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldCreatNewApplicationAndRedirectToItIfQueryStringExistsOnUserWithProject() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        String queryString = "queryString";
        Program program = new ProgramBuilder().id(1).build();
        Project project = new ProjectBuilder().id(1).advert(new AdvertBuilder().id(1).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false)
                .username("email@email.com").email("email@email.com").password("1234").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        Map<String, String> parsedParams = new HashMap<String, String>(3);
        parsedParams.put("program", "code");
        parsedParams.put("project", "1");
        EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(parsedParams);
        EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
        EasyMock.expect(programServiceMock.getProject(1)).andReturn(project);
        userServiceMock.save(user);
        EasyMock.expect(applicationFormCreationServiceMock.createOrGetUnsubmittedApplicationForm(user, program, project)).andReturn(applicationForm);
        EasyMock.replay(userServiceMock, applicationFormCreationServiceMock, programServiceMock, qureyStringParserMock);
        String view = registerController.activateAccountSubmit(activationCode, new MockHttpServletRequest());
        EasyMock.verify(userServiceMock);
        assertEquals("redirect:/application?applicationId=ABC&activationCode=" + activationCode, view);
        assertTrue(user.isEnabled());
    }

    @Test(expected = CannotApplyToProjectException.class)
    public void shouldThrowExceptionIfRegisteringForAnInactiveProjectAdvert() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        String queryString = "queryString";
        Program program = new ProgramBuilder().id(1).build();
        Project project = new ProjectBuilder().id(1).advert(new AdvertBuilder().id(1).active(false).build()).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false)
                .username("email@email.com").email("email@email.com").password("1234").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        Map<String, String> parsedParams = new HashMap<String, String>(3);
        parsedParams.put("program", "code");
        parsedParams.put("project", "1");
        EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(parsedParams);
        EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
        EasyMock.expect(programServiceMock.getProject(1)).andReturn(project);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock, applicationFormCreationServiceMock, programServiceMock, qureyStringParserMock);
        registerController.activateAccountSubmit(activationCode, new MockHttpServletRequest());
    }

    @Test(expected = CannotApplyToProjectException.class)
    public void shouldThrowExceptionIfRegisteringForADisabledProject() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        String queryString = "queryString";
        Program program = new ProgramBuilder().id(1).build();
        Project project = new ProjectBuilder().id(1).disabled(true).advert(new AdvertBuilder().id(1).active(false).build()).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false)
                .username("email@email.com").email("email@email.com").password("1234").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        Map<String, String> parsedParams = new HashMap<String, String>(3);
        parsedParams.put("program", "code");
        parsedParams.put("project", "1");
        EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(parsedParams);
        EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
        EasyMock.expect(programServiceMock.getProject(1)).andReturn(project);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock, applicationFormCreationServiceMock, programServiceMock, qureyStringParserMock);
        registerController.activateAccountSubmit(activationCode, new MockHttpServletRequest());
    }

    @Test(expected = CannotApplyToProjectException.class)
    public void shouldThrowExceptionIfRegisteringForANonExistentProject() throws ParseException {
        String activationCode = "ul5oaij68186jbcg";
        String queryString = "queryString";
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false)
                .username("email@email.com").email("email@email.com").password("1234").build();
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
        Map<String, String> parsedParams = new HashMap<String, String>(3);
        parsedParams.put("program", "code");
        parsedParams.put("project", "1");
        EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(parsedParams);
        EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
        EasyMock.expect(programServiceMock.getProject(1)).andReturn(null);
        userServiceMock.save(user);
        EasyMock.replay(userServiceMock, applicationFormCreationServiceMock, programServiceMock, qureyStringParserMock);
        registerController.activateAccountSubmit(activationCode, new MockHttpServletRequest());
    }

    @Test
    public void shouldReturnToRegistrationPageIfNouserFound() throws ParseException {
        String activationCode = "differentactivationcode";
        EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(null);
        EasyMock.replay(userServiceMock);
        String view = registerController.activateAccountSubmit(activationCode, new MockHttpServletRequest());
        assertEquals("public/register/activation_failed", view);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
