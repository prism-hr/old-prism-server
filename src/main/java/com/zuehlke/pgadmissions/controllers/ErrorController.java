package com.zuehlke.pgadmissions.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.pagemodels.PageModel;

@Controller
@RequestMapping(value = "/error")
public class ErrorController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getErrorPage() {
		PageModel model = new PageModel();
		model.setUser((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails());
		return new ModelAndView("/public/error/error", "model", model);
	}

}