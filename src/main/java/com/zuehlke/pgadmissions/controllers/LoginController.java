package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final String APPLY_REQUEST_SESSION_ATTRIBUTE = "applyRequest";

    private static final String ACTIVATION_CODE_URL_PARAMETER = "activationCode";
    
    public static final String USER_EMAIL_SESSION_PARAMETER = "loginUserEmail";
    
    private static final String LOGIN_PAGE = "public/login/login_page";
    
    private static final String REGISTER_USER_REDIRECT = "redirect:/register";

    private final UserService userService;
    
    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }
    
    public LoginController() {
        this(null);
    }
    
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getLoginPage(final HttpServletRequest request, final HttpServletResponse response) {
		
	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		if (hasUserClickedOnAlreadyRegisteredOnTheRegistrationPage(request)) {
		    clearUserEmailInSession(request);
            clearApplyRequestInSession(request);
            return LOGIN_PAGE;
		} 
		
		if (doesRequestParameterIncludeAnActivationCode(request)) {
		    setUserEmailInSession(request);
		    clearApplyRequestInSession(request);
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
	
	private boolean hasUserClickedOnAlreadyRegisteredOnTheRegistrationPage(final HttpServletRequest request) {
	    return StringUtils.contains(getReferrerFromHeader(request), "register") 
	            && StringUtils.contains(request.getRequestURI().toString(), "/login");
	}
	
	private boolean doesRequestParameterIncludeAnActivationCode(final HttpServletRequest request) {
	    DefaultSavedRequest defaultSavedRequest = getDefaultSavedRequest(request);
	    return defaultSavedRequest != null 
	            && defaultSavedRequest.getRequestURL() != null
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
            RegisteredUser userByActivationCode = userService.getUserByActivationCode(activationCode);
            if (userByActivationCode != null) {
                request.getSession().setAttribute(USER_EMAIL_SESSION_PARAMETER, userByActivationCode.getEmail());
            }
        }
	}
	
	private void setApplyNewQueryStringInSession(final HttpServletRequest request) {
	    DefaultSavedRequest defaultSavedRequest = getDefaultSavedRequest(request);
	    request.getSession().setAttribute(APPLY_REQUEST_SESSION_ATTRIBUTE, composeQueryString(defaultSavedRequest));
	}
	
	private void clearUserEmailInSession(final HttpServletRequest request) {
        request.getSession().setAttribute(USER_EMAIL_SESSION_PARAMETER, null);
    }
	
	private void clearApplyRequestInSession(final HttpServletRequest request) {
	    request.getSession().setAttribute(APPLY_REQUEST_SESSION_ATTRIBUTE, null);
	}
	
	private String composeQueryString(DefaultSavedRequest savedRequest) {
		StringBuilder sb = new StringBuilder();
		composeQueryStringPart(savedRequest, "program", "program", sb);
		composeQueryStringPart(savedRequest, "programhome", "programhome", sb);
		composeQueryStringPart(savedRequest, "programDeadline", "bacthdeadline", sb);
		composeQueryStringPart(savedRequest, "projectTitle", "projectTitle", sb);
		return sb.toString();
	}
	
	private void composeQueryStringPart(DefaultSavedRequest request, String parameter, String key, StringBuilder builder) {
	    String[] parameterArr = request.getParameterValues(parameter);
        if (parameterArr != null && parameterArr.length > 0) {
            if (builder.length() > 0) {
                builder.append("||");
            }
            builder.append(key).append(":").append(parameterArr[0]);
        }
	}
}
