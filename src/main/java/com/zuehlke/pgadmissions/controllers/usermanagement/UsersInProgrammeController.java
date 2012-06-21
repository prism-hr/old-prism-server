package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/manageUsers")
public class UsersInProgrammeController {
	private static final String USERS_ROLES_VIEW = "private/staff/superAdmin/users_roles";
	private final UserService userService;
	private final ProgramsService programsService;
	private final EncryptionHelper encryptionHelper;

	UsersInProgrammeController(){
		this(null, null, null);
	}
	
	@Autowired
	public UsersInProgrammeController(UserService userService, ProgramsService programsService, EncryptionHelper encryptionHelper) {
		this.userService = userService;
		this.programsService = programsService;
		this.encryptionHelper = encryptionHelper;
	}


	@ModelAttribute("usersInRoles")
	public List<RegisteredUser> getUsersInProgram(@RequestParam(required = false) String programCode) {
		if(programCode == null){
			return new ArrayList<RegisteredUser>();
		}
		Program selectedProgram = getSelectedProgram(programCode);
		if(selectedProgram == null){
			return new ArrayList<RegisteredUser>();
		}
		return userService.getAllUsersForProgram(selectedProgram);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/program")
	public String getUsersInProgramView() {
		if (!(userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) || userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR))) {
			throw new ResourceNotFoundException();
		}
		return USERS_ROLES_VIEW;
	}
	
	@ModelAttribute("selectedProgram")
	public Program getSelectedProgram(@RequestParam(required = false) String programCode) {
		if (programCode == null) {
			return null;
		}
		return programsService.getProgramByCode(programCode);
	}

	
}
