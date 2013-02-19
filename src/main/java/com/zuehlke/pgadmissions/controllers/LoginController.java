package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	public String getLoginPage(HttpServletRequest request, HttpServletResponse response) {
		String returnPage = LOGIN_PAGE;

		String referrer = request.getHeader("referer");
		HttpSession session = request.getSession();
		DefaultSavedRequest attribute = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		
		if (StringUtils.contains(referrer, "register") && StringUtils.contains(request.getRequestURI().toString(), "/login")) {
		    returnPage = LOGIN_PAGE;
		    clearUserEmailInSession(session);
            clearApplyRequestInSession(session);
		} else if (attribute != null && attribute.getRequestURL() != null && attribute.getParameterValues(ACTIVATION_CODE_URL_PARAMETER) != null) {
		    // When a user follows a link in a notification and launches the login form: populate the email address field on the login form.
		    String[] activationCodeValues = attribute.getParameterValues(ACTIVATION_CODE_URL_PARAMETER);
		    if (activationCodeValues.length == 1) {
		        String activationCode = activationCodeValues[0];
		        RegisteredUser userByActivationCode = userService.getUserByActivationCode(activationCode);
		        if (userByActivationCode != null) {
		            session.setAttribute(USER_EMAIL_SESSION_PARAMETER, userByActivationCode.getEmail());
		        }
		    }
		    clearApplyRequestInSession(session);
		} else if (attribute != null && StringUtils.contains(attribute.getRequestURL(), "/apply/new")) {
            session.setAttribute(APPLY_REQUEST_SESSION_ATTRIBUTE, composeQueryString(attribute));
            returnPage = REGISTER_USER_REDIRECT;
            clearUserEmailInSession(session);
		} else {
		    clearUserEmailInSession(session);
		    clearApplyRequestInSession(session);
		}
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		return returnPage;
	}
	
	private void clearUserEmailInSession(HttpSession session) {
        session.setAttribute(USER_EMAIL_SESSION_PARAMETER, null);
    }
	
	private void clearApplyRequestInSession(HttpSession session) {
	    session.setAttribute(APPLY_REQUEST_SESSION_ATTRIBUTE, null);
	}
	
	private String composeQueryString(DefaultSavedRequest savedRequest) {
		StringBuilder sb = new StringBuilder();
		String[] programIds = savedRequest.getParameterValues("program");
		if (programIds != null && programIds.length > 0) {
			sb.append("program:" + programIds[0]);
		}
		String[] programhomes = savedRequest.getParameterValues("programhome");
		if (programhomes != null && programIds.length > 0) {
			if (sb.length() > 0) {
				sb.append("||");
			}
			sb.append("programhome:" + programhomes[0]);
		}
		String[] batchdeadlines = savedRequest.getParameterValues("programDeadline");
		if (batchdeadlines != null && batchdeadlines.length > 0) {
			if (sb.length() > 0) {
				sb.append("||");
			}
			sb.append("bacthdeadline:" + batchdeadlines[0]);
		}
		String[] projects = savedRequest.getParameterValues("projectTitle");
		if (projects != null && projects.length > 0) {
			if (sb.length() > 0) {
				sb.append("||");
			}
			sb.append("projectTitle:" + projects[0]);
		}

		return sb.toString();
	}
}
