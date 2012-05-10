package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;


@Controller
@RequestMapping("/manageUsers")
public class CreateNewUserController {
	private static final String NEW_USER_VIEW_NAME = "private/staff/superAdmin/create_new_user_in_role_page";
	private final ProgramsService programsService;
	private final UserService userService;

	CreateNewUserController(){
		this(null, null);
	}
	 
	@Autowired
	public CreateNewUserController(ProgramsService programsService, UserService userService) {
		this.programsService = programsService;
		this.userService = userService;

	}

	@ModelAttribute("programs")
	public List<Program> getPrograms() {
		if(userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)){
			return programsService.getAllPrograms(); 
		}
		return userService.getCurrentUser().getProgramsOfWhichAdministrator();
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}
	
	@ModelAttribute("authorities")
	public List<Authority> getAuthorities() {
		if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER);

		}
		return Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER);
	}
	@RequestMapping(method = RequestMethod.GET, value = "/createNewUser")
	public String getAddUsersView() {
		if(userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)  || userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR)){
			return NEW_USER_VIEW_NAME;
		}
		throw new ResourceNotFoundException();
	}

}
