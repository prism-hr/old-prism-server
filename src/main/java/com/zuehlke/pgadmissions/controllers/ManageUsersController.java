package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ProgramsService;

@Controller
@RequestMapping("/manageUsers")
public class ManageUsersController {
	
	private static final String ROLES_PAGE_VIEW_NAME = "private/staff/superAdmin/assign_roles_page";
	private final ProgramsService programsService;

	
	ManageUsersController() {
		this(null);
	}

	@Autowired
	public ManageUsersController(ProgramsService programsService) {
		this.programsService = programsService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getUsersPage() {
		PageModel pageModel = new PageModel();
		pageModel.setUser(getCurrentUser());
		pageModel.setPrograms(programsService.getAllPrograms());
		
		ModelAndView modelAndView = new ModelAndView(ROLES_PAGE_VIEW_NAME, "model", pageModel);	
		return modelAndView;
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

}
