package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final String LOGIN_PAGE = "public/login/login_page";
    
    private static final String REGISTER_USER_REDIRECT = "redirect:/register";
    
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getLoginPage(HttpServletRequest request, HttpServletResponse response) {
		String returnPage = LOGIN_PAGE;
	    
		HttpSession session = request.getSession();
		
		DefaultSavedRequest attribute = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		
		if (attribute != null && attribute.getRequestURL() != null && attribute.getRequestURL().endsWith("/apply/new")) {
		    session.setAttribute("applyRequest", composeQueryString(attribute));
			returnPage = REGISTER_USER_REDIRECT;
			increaseNumberOfClicks(session);
		} else {
			session.setAttribute("applyRequest", null);
			session.setAttribute("applyRequestClicks", new Integer(0));
		}
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		if (getNumberOfClicks(session) > 1) {
            returnPage = LOGIN_PAGE;
        }
		
		return returnPage;
	}
	
	private void increaseNumberOfClicks(final HttpSession session) {
	    Integer numberOfClicks = (Integer) session.getAttribute("applyRequestClicks");
	    if (numberOfClicks == null) {
	        session.setAttribute("applyRequestClicks", new Integer(1));
	    } else {
	        session.setAttribute("applyRequestClicks", new Integer(((Integer)session.getAttribute("applyRequestClicks")) + 1));
	    }
	}
	
	private int getNumberOfClicks(final HttpSession session) {
	    Integer numberOfClicks = (Integer) session.getAttribute("applyRequestClicks");
        if (numberOfClicks == null) {
            return 0;
        }
        return numberOfClicks;
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
