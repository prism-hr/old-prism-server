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
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping(value = {"","applications"})
public class ApplicationListController {

	private static final String APPLICATION_LIST_VIEW_NAME = "private/my_applications_page";
	private final ApplicationsService applicationsService;
	private final UserService userService;
	
	ApplicationListController(){
		this(null, null);
	}

	@Autowired
	public ApplicationListController(ApplicationsService applicationsService, UserService userService) {
		this.applicationsService = applicationsService;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getApplicationListPage(@RequestParam(required=false) boolean submissionSuccess, String decision) {
		
		SecurityContext context = SecurityContextHolder.getContext();
		RegisteredUser user = (RegisteredUser) context.getAuthentication().getDetails();
		
		ApplicationListModel model = new ApplicationListModel();
		model.setUser(userService.getUser(user.getId()));
		model.setApplications(applicationsService.getVisibleApplications(user));
		if(submissionSuccess){
		        model.setMessage("Your application is submitted successfully.");
		}
		if(decision != null){
		    model.setMessage("The application was successfully " + decision +".");
		}
		ModelAndView modelAndView = new ModelAndView(APPLICATION_LIST_VIEW_NAME, "model", model);
		
		return modelAndView;
	}	
}
