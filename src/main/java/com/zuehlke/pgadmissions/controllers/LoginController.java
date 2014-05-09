package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = { "/login", "/alreadyRegistered" })
public class LoginController {

    public static final String CLICKED_ON_ALREADY_REGISTERED = "CLICKED_ON_ALREADY_REGISTERED";

    private static final String REQUEST_ADVERT_ID = "requestAdvertId";

    private static final String ACTIVATION_CODE_URL_PARAMETER = "activationCode";

    public static final String USER_EMAIL_SESSION_PARAMETER = "loginUserEmail";

    private static final String LOGIN_PAGE = "public/login/login_page";

    private static final String REGISTER_USER_REDIRECT = "redirect:/register";

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programService;

    @RequestMapping(method = RequestMethod.GET)
    public String getLoginPage(final HttpServletRequest request, final HttpServletResponse response, final HttpSession session) {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (hasUserClickedOnAlreadyRegisteredOnTheRegistrationPage(request, session)) {
            clearUserEmailInSession(request);
            // clearApplyRequestInSession(request);
            session.setAttribute(CLICKED_ON_ALREADY_REGISTERED, true);
            return LOGIN_PAGE;
        }

        if (doesRequestParameterIncludeAnActivationCode(request)) {
            setUserEmailInSession(request);
            clearApplyRequestInSession(request);
            return LOGIN_PAGE;
        }

        if (isAnApplyNewRequestAndLoginFailed(request, session)) {
            clearUserEmailInSession(request);
            // clearApplyRequestInSession(request);
            return LOGIN_PAGE;
        }

        if (isAnApplyNewRequest(request)) {
            setApplyNewQueryStringInSession(request);
            clearUserEmailInSession(request);
            return REGISTER_USER_REDIRECT;
        }

        clearUserEmailInSession(request);
        clearApplyRequestInSession(request);
        return LOGIN_PAGE;
    }

    private boolean isAnApplyNewRequest(final HttpServletRequest request) {
        DefaultSavedRequest defaultSavedRequest = getDefaultSavedRequest(request);
        return defaultSavedRequest != null && StringUtils.contains(defaultSavedRequest.getRequestURL(), "/apply/new");
    }

    private boolean hasUserClickedOnAlreadyRegisteredOnTheRegistrationPage(final HttpServletRequest request, HttpSession session) {
        return session.getAttribute(CLICKED_ON_ALREADY_REGISTERED) != null
                || (StringUtils.contains(getReferrerFromHeader(request), "register") && StringUtils.contains(request.getRequestURI().toString(),
                        "/alreadyRegistered"));
    }

    private boolean isAnApplyNewRequestAndLoginFailed(final HttpServletRequest request, final HttpSession session) {
        return StringUtils.contains(getReferrerFromHeader(request), "/login")
                && BooleanUtils.isTrue((Boolean) session.getAttribute(CLICKED_ON_ALREADY_REGISTERED));
    }

    private boolean doesRequestParameterIncludeAnActivationCode(final HttpServletRequest request) {
        DefaultSavedRequest defaultSavedRequest = getDefaultSavedRequest(request);
        return defaultSavedRequest != null && defaultSavedRequest.getRequestURL() != null
                && defaultSavedRequest.getParameterValues(ACTIVATION_CODE_URL_PARAMETER) != null;
    }

    private String getReferrerFromHeader(final HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getHeader("referer"));
    }

    private DefaultSavedRequest getDefaultSavedRequest(final HttpServletRequest request) {
        return (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
    }

    private void setUserEmailInSession(final HttpServletRequest request) {
        DefaultSavedRequest defaultSavedRequest = getDefaultSavedRequest(request);
        String[] activationCodeValues = defaultSavedRequest.getParameterValues(ACTIVATION_CODE_URL_PARAMETER);
        if (activationCodeValues.length == 1) {
            String activationCode = activationCodeValues[0];
            User userByActivationCode = userService.getUserByActivationCode(activationCode);
            if (userByActivationCode != null) {
                request.getSession().setAttribute(USER_EMAIL_SESSION_PARAMETER, userByActivationCode.getEmail());
            }
        }
    }

    private void setApplyNewQueryStringInSession(final HttpServletRequest request) {
        DefaultSavedRequest defaultSavedRequest = getDefaultSavedRequest(request);
        request.getSession().setAttribute(REQUEST_ADVERT_ID, getAdvertId(defaultSavedRequest));
    }

    private void clearUserEmailInSession(final HttpServletRequest request) {
        request.getSession().setAttribute(USER_EMAIL_SESSION_PARAMETER, null);
    }

    private void clearApplyRequestInSession(final HttpServletRequest request) {
        request.getSession().setAttribute(REQUEST_ADVERT_ID, null);
    }

    private Integer getAdvertId(SavedRequest request) {
        if (request.getParameterValues("advert") != null) {
            return Integer.parseInt(request.getParameterValues("advert")[0]);
        }
        if (request.getParameterValues("program") != null) {
            Program program = programService.getProgramByCode(request.getParameterValues("program")[0]);
            return program.getId();
        }
        return null;
    }

}