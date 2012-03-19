package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.pagemodels.ManageUsersModel;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/manageUsers")
public class ManageUsersController {

	private static final String ROLES_PAGE_VIEW_NAME = "private/staff/superAdmin/assign_roles_page";
	private final ProgramsService programsService;
	private final UserService userService;


	ManageUsersController() {
		this(null, null);
	}

	@Autowired
	public ManageUsersController(ProgramsService programsService, UserService userService) {
		this.programsService = programsService;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/showPage")
	public ModelAndView getUsersPage() {
		ManageUsersModel pageModel = new ManageUsersModel();
		RegisteredUser user = getCurrentUser();
		pageModel.setUser(user);
		if (! (user.isInRole(Authority.ADMINISTRATOR) || user.isInRole(Authority.SUPERADMINISTRATOR))) {
			throw new AccessDeniedException();
		}
		List<Program> allPrograms = programsService.getAllPrograms();
		List<Program> visiblePrograms = new ArrayList<Program>();

		List<RegisteredUser> allUsers = userService.getAllUsers();
		List<RegisteredUser> visibleUsers = new ArrayList<RegisteredUser>();

		if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
			if (allPrograms != null) {
				visiblePrograms.addAll(allPrograms);
			}
			if (allUsers != null) {
				visibleUsers.addAll(allUsers);
			}
		} else {
			for (Program program : allPrograms) {
				if (program.getAdministrators().contains(user)) {
					visiblePrograms.add(program);
					visibleUsers.addAll(program.getAdministrators());
					visibleUsers.addAll(program.getApprovers());
					visibleUsers.addAll(program.getReviewers());
				}
			}
		}
		
		for (RegisteredUser visibleUser : visibleUsers) {
			visibleUser.setRolesList();
		}

		pageModel.setPrograms(visiblePrograms);
		pageModel.setUsersInRoles(visibleUsers);

		ModelAndView modelAndView = new ModelAndView(ROLES_PAGE_VIEW_NAME, "model", pageModel);	
		return modelAndView;
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

}
