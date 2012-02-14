package com.zuehlke.pgadmissions.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.temporary.ApplicationForm;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	@RequestMapping(method = RequestMethod.GET)
	public String getApplyForm(ModelMap model) {
		model.addAttribute("application", new ApplicationForm());
		return "applicationForm";
	}
	
	@RequestMapping(value = "/submit")
	public String getLoginSubmit(@ModelAttribute("application") ApplicationForm application,
			BindingResult result, ModelMap model) {
//		if (application.getTitle().isEmpty() || application.getCob().isEmpty() || application.getDob().isEmpty() || 
//				application.getGender().isEmpty() || application.getNat().isEmpty() || application.getUserId().isEmpty()) {
//			return "applicationForm";
//		}
		model.addAttribute("application", application);
		return "applicationFormSubmitted";
	}
	

}
