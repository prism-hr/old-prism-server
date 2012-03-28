package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.NewRolesDTO;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RolePropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/manageUsers")
public class ManageUsersController {

	private static final String ROLES_PAGE_VIEW_NAME = "private/staff/superAdmin/assign_roles_page";
	private final ProgramsService programsService;
	private final UserService userService;
	private final RolePropertyEditor rolePropertyEditor;

	ManageUsersController() {
		this(null, null, null);
	}

	@Autowired
	public ManageUsersController(ProgramsService programsService, UserService userService, RolePropertyEditor rolePropertyEditor) {
		this.programsService = programsService;
		this.userService = userService;
		this.rolePropertyEditor = rolePropertyEditor;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/showPage")
	public String getUsersPage(@ModelAttribute("selectedProgram") Program program, ModelMap modelMap) {

		RegisteredUser user = getCurrentUser();

		if (!(user.isInRole(Authority.SUPERADMINISTRATOR) || user.isInRole(Authority.ADMINISTRATOR))) {
			throw new AccessDeniedException();
		}

		if (program != null) {
			modelMap.put("usersInRoles", userService.getAllUsersForProgram(program));
		} else {
			modelMap.put("usersInRoles", new ArrayList<RegisteredUser>());
		}

		return ROLES_PAGE_VIEW_NAME;

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

	@ModelAttribute("user")
	public RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	@ModelAttribute("availableUsers")
	public List<RegisteredUser> getavailableUsers() {
		return userService.getAllInternalUsers();
	}

	@ModelAttribute("authorities")
	public List<Authority> getAuthorities() {
		if (getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER);

		}
		return Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER);

	}

	@ModelAttribute("programs")
	public List<Program> getPrograms() {
		if (getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return programsService.getAllPrograms();
		}
		return getCurrentUser().getProgramsOfWhichAdministrator();

	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(Role.class, "newRoles", rolePropertyEditor);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/updateRoles")
	public String updateUserWithNewRoles(@ModelAttribute("selectedUser") RegisteredUser selectedUser,
			@ModelAttribute("selectedProgram") Program selectedProgram, @ModelAttribute NewRolesDTO newRolesDTO) {

		removeFromSuperadminRoleIfRequired(selectedUser, newRolesDTO);
		for (Authority authority : Authority.values()) {
			addToRoleIfRequired(selectedUser, newRolesDTO, authority);
		}

		addOrRemoveFromProgramsOfWhichAdministratorIfRequired(selectedUser, selectedProgram, newRolesDTO);
		addOrRemoveFromProgramsOfWhichApproverIfRequired(selectedUser, selectedProgram, newRolesDTO);
		addOrRemoveFromProgramsOfWhichReviewerIfRequired(selectedUser, selectedProgram, newRolesDTO);
		userService.save(selectedUser);
		return "redirect:/manageUsers/showPage?programId=" + selectedProgram.getId();
	}

	private void removeFromSuperadminRoleIfRequired(RegisteredUser selectedUser, NewRolesDTO newRolesDTO) {
		if (getRole(newRolesDTO, Authority.SUPERADMINISTRATOR) == null) {
			Role superAdminRole = selectedUser.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
			selectedUser.getRoles().remove(superAdminRole);
		}
	}

	private void addOrRemoveFromProgramsOfWhichAdministratorIfRequired(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		if (getRole(newRolesDTO, Authority.ADMINISTRATOR) != null && !selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichAdministrator().add(selectedProgram);
		} else if (getRole(newRolesDTO, Authority.ADMINISTRATOR) == null && selectedUser.getProgramsOfWhichAdministrator().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichAdministrator().remove(selectedProgram);
		}

	}

	private void addOrRemoveFromProgramsOfWhichApproverIfRequired(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		if (getRole(newRolesDTO, Authority.APPROVER) != null && !selectedUser.getProgramsOfWhichApprover().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichApprover().add(selectedProgram);
		} else if (getRole(newRolesDTO, Authority.APPROVER) == null && selectedUser.getProgramsOfWhichApprover().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichApprover().remove(selectedProgram);
		}
	}

	private void addOrRemoveFromProgramsOfWhichReviewerIfRequired(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		if (getRole(newRolesDTO, Authority.REVIEWER) != null && !selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichReviewer().add(selectedProgram);
		} else if (getRole(newRolesDTO, Authority.REVIEWER) == null && selectedUser.getProgramsOfWhichReviewer().contains(selectedProgram)) {
			selectedUser.getProgramsOfWhichReviewer().remove(selectedProgram);
		}
	}

	private void addToRoleIfRequired(RegisteredUser selectedUser, NewRolesDTO newRolesDTO, Authority authority) {
		if (!selectedUser.isInRole(authority) && getRole(newRolesDTO, authority) != null) {
			selectedUser.getRoles().add(getRole(newRolesDTO, authority));
		}
	}

	private Role getRole(NewRolesDTO newRolesDTO, Authority authority) {
		for (Role role : newRolesDTO.getNewRoles()) {
			if (role.getAuthorityEnum() == authority) {
				return role;
			}
		}
		return null;
	}
}
