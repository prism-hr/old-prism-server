package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(method = RequestMethod.GET)
	public String getLoginPage(HttpServletRequest request, ModelMap modelMap) {
		HttpSession session = request.getSession();
		DefaultSavedRequest attribute = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		if (attribute != null && attribute.getRequestURL() != null && attribute.getRequestURL().endsWith("/apply/new")) {
			String[] programIds = attribute.getParameterValues("program");
			if (programIds != null && programIds.length > 0) {
				modelMap.put("program", programIds[0]);
			}
		}

		return "public/login/login_page";
	}

}
