package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.temporary.PhoneNumber;
import com.zuehlke.pgadmissions.temporary.User;

@Controller
@RequestMapping(value={"", "home"})
public class DemoController {

	private final String jspViewName;
	private final String velocityViewName;

	public DemoController(String jspViewName, String velocityViewName) {
		this.jspViewName = jspViewName;
		this.velocityViewName = velocityViewName;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getPage(HttpServletRequest request, ModelMap modelMap) {
		
		User user = new User();
		user.setFirstName("bob");
		user.setLastName("smith");

		user.getPhoneNumbers().add(new PhoneNumber("office", "0123 456 789"));
		user.getPhoneNumbers().add(new PhoneNumber("home", "0123 567 890"));
		user.getPhoneNumbers().add(new PhoneNumber("mobile", "0123 678 901"));

		modelMap.addAttribute("user", user);
		
		return resolveView(request);
	}

	private String resolveView(HttpServletRequest request) {
		if("/home".equals(request.getServletPath())){
			return velocityViewName;
		}
		return jspViewName;
	}

}
