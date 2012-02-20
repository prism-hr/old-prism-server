package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
@Controller
@RequestMapping("/apply")
@Deprecated
public class DeprecatedApplicationFormController {
	
    private final ApplicationFormDAO applicationDAO;
	
    DeprecatedApplicationFormController(){
    	this(null);
    }
    
    @Autowired
	public DeprecatedApplicationFormController(ApplicationFormDAO applicationDAO) {
		this.applicationDAO = applicationDAO;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getApplyForm(ModelMap model) {
		model.addAttribute("application", new ApplicationForm());
		return "applicationForm";
	}
	
	@Transactional
	@RequestMapping(value = "/submit")
	public String getLoginSubmit(@ModelAttribute("application") ApplicationForm application,
			BindingResult result, ModelMap model) {
		//toDO: validation
		RegisteredUser user = (RegisteredUser)SecurityContextHolder.getContext().getAuthentication().getDetails();
		application.setUser(user);
		applicationDAO.save(application);
		
		
		model.addAttribute("application", application);
		return "applicationFormSubmitted";
	}
	

}
