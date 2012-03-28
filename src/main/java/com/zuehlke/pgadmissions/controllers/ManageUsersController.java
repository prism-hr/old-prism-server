package com.zuehlke.pgadmissions.controllers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ModelAndView getUsersPage(@RequestParam(required = false) Integer programId,@RequestParam(required = false) Integer userId) {
		ManageUsersModel pageModel = new ManageUsersModel();
		RegisteredUser user = getCurrentUser();
	
		if (!(user.isInRole(Authority.SUPERADMINISTRATOR) || user.isInRole(Authority.ADMINISTRATOR))) {
			throw new AccessDeniedException();
		}
		pageModel.setUser(user);
		pageModel.setAvailableUsers(userService.getAllInternalUsers());
		if (programId != null) {
			Program program = programsService.getProgramById(programId);
			pageModel.setSelectedProgram(program);
			pageModel.setUsersInRoles(userService.getAllUsersForProgram(program));
		}
		if(userId != null){
			pageModel.setSelectedUser(userService.getUser(userId));
		}
		if (user.isInRole(Authority.SUPERADMINISTRATOR)){ 
			pageModel.setRoles(Arrays.asList( Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER));
			pageModel.setPrograms(programsService.getAllPrograms());
	
		}else{
			pageModel.setRoles(Arrays.asList( Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER));
			pageModel.setPrograms(user.getProgramsOfWhichAdministrator());
		}
		
		return new ModelAndView(ROLES_PAGE_VIEW_NAME, "model", pageModel);
	
	}



	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

}
