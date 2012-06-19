package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getLoginPage(HttpServletRequest request) {
		HttpSession session = request.getSession();
		DefaultSavedRequest attribute = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		if (attribute != null && attribute.getRequestURL() != null && attribute.getRequestURL().endsWith("/apply/new")) {
			session.setAttribute("applyRequest", composeQueryString(attribute));
		} else {
			session.setAttribute("applyRequest", null);
		}

		return "public/login/login_page";
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
