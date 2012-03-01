package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.pagemodels.ApplicationListModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping(value = {"","applications"})
public class ApplicationListController {

	private static final String APPLICATION_LIST_VIEW_NAME = "application/application_list";
	private ApplicationsService applicationsService;
	
	ApplicationListController(){
		this(null);
	}

	@Autowired
	public ApplicationListController(ApplicationsService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getApplicationListPage(@RequestParam(required=false) boolean submissionSuccess, String decision) {
		
		SecurityContext context = SecurityContextHolder.getContext();
		RegisteredUser user = (RegisteredUser) context.getAuthentication().getDetails();
		
		ApplicationListModel model = new ApplicationListModel();
		model.setUser(user);
		model.setApplications(applicationsService.getVisibleApplications(user));
		if(submissionSuccess){
		        model.setMessage("Your application is submitted successfully. <b>Coming soon: </b> email confirmation.");
		}
		if(decision != null){
		    model.setMessage("The application was successfully " + decision +".");
		}
		ModelAndView modelAndView = new ModelAndView(APPLICATION_LIST_VIEW_NAME, "model", model);
		
		return modelAndView;
	}	
}
