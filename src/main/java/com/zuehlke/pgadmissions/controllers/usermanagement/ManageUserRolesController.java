package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UpdateUserRolesDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PlainTextUserPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.UpdateUserRolesDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class ManageUserRolesController {
	private static final String ROLES_PAGE_VIEW_NAME = "private/staff/superAdmin/assign_roles_page";
	private final UserService userService;
	private final ProgramsService programsService;
	private final PlainTextUserPropertyEditor userPropertyEditor;
	private final ProgramPropertyEditor programPropertyEditor;
	private final UpdateUserRolesDTOValidator updateUserRolesDTOValidator;

	ManageUserRolesController(){
		this(null, null, null, null, null);
	}
	
	@Autowired
	public ManageUserRolesController(UserService userService, ProgramsService programsService, PlainTextUserPropertyEditor userPropertyEditor, ProgramPropertyEditor programPropertyEditor, UpdateUserRolesDTOValidator updateUserRolesDTOValidator) {
		this.userService = userService;	
		this.programsService = programsService;
		this.userPropertyEditor = userPropertyEditor;
		this.programPropertyEditor = programPropertyEditor;
		this.updateUserRolesDTOValidator = updateUserRolesDTOValidator;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("selectedProgram")
	public Program getSelectedProgram(@RequestParam(required = false) Integer programId) {
		if (programId == null) {
			return null;
		}
		Program program = programsService.getProgramById(programId);
		if (program == null) {
			throw new ResourceNotFoundException();
		}
		return program;
	}
	
	@ModelAttribute("selectedUser")
	public RegisteredUser getSelectedUser(@RequestParam(required = false) Integer userId) {
		if (userId == null) {
			return null;
		}
		RegisteredUser user = userService.getUser(userId);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		return user;

	}
	
	@ModelAttribute("availableUsers")
	public List<RegisteredUser> getavailableUsers() {
		return userService.getAllInternalUsers();
	}

	@ModelAttribute("usersInRoles")
	public List<RegisteredUser> getUsersInRoles(@RequestParam(required = false) Integer programId) {
		Program selectedProgram = getSelectedProgram(programId);
		if(selectedProgram == null){
			return new ArrayList<RegisteredUser>();
		}
		return userService.getAllUsersForProgram(selectedProgram);
	}
	
	@ModelAttribute("programs")
	public List<Program> getPrograms() {
		if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return programsService.getAllPrograms();
		}
		return userService.getCurrentUser().getProgramsOfWhichAdministrator();

	}
	@ModelAttribute("authorities")
	public List<Authority> getAuthorities() {
		if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER, Authority.SUPERVISOR);

		}
		return Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER, Authority.SUPERVISOR);
	}
	
	@ModelAttribute("updateUserRolesDTO")	
	public UpdateUserRolesDTO getUpdateUserRolesDTO() {
		return new UpdateUserRolesDTO();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/showPage")
	public String getUsersPage() {
		if (!(userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) || userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR))) {
			throw new ResourceNotFoundException();
		}
		return ROLES_PAGE_VIEW_NAME;

	}

	
	@InitBinder(value = "updateUserRolesDTO")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(updateUserRolesDTOValidator);
		binder.registerCustomEditor(Program.class, programPropertyEditor);
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}
	@RequestMapping(method = RequestMethod.POST, value = "/updateUserRoles")
	public String updateUserRoles(@Valid @ModelAttribute("updateUserRolesDTO") UpdateUserRolesDTO userDTO, BindingResult result) {
		checkPermissions();
		if(result.hasErrors()){
			return ROLES_PAGE_VIEW_NAME;			
		}
		
		userService.updateUserWithNewRoles(userDTO.getSelectedUser(), userDTO.getSelectedProgram(), userDTO.getSelectedAuthorities());	
		if(userDTO.getSelectedProgram() == null){
			return "redirect:/manageUsers/showPage";
		}
		return "redirect:/manageUsers/showPage?programId="  + userDTO.getSelectedProgram().getId();
	}
	
	private void checkPermissions() {
		if (!userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) && !userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
	}

	

	
}
