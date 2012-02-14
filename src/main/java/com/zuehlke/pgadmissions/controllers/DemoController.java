package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = { "", "free" })
public class DemoController {

	private final String jspViewName;
	private final String freemarkerViewName;

	public DemoController(String jspViewName, String freemarkerViewName) {
		this.jspViewName = jspViewName;
		this.freemarkerViewName = freemarkerViewName;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getPage(HttpServletRequest request, ModelMap modelMap) {

		SecurityContext context = SecurityContextHolder.getContext();
		modelMap.addAttribute("user", context.getAuthentication().getDetails());

		return resolveView(request);
	}

	private String resolveView(HttpServletRequest request) {

		if ("/free".equals(request.getServletPath())) {
			return freemarkerViewName;
		}
		return jspViewName;
	}

}
