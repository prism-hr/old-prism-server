package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.NewAdminUserDTO;
import com.zuehlke.pgadmissions.dto.NewRolesDTO;
import com.zuehlke.pgadmissions.dto.UpdateUserForProgramWithRolesDTO;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RolePropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewAdminUserDTOValidator;
import com.zuehlke.pgadmissions.validators.UpdateUserForProgramWithRolesDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class ManageUsersController {

	private static final String ROLES_PAGE_VIEW_NAME = "private/staff/superAdmin/assign_roles_page";
	private static final String NEW_USER_VIEW_NAME = "private/staff/superAdmin/create_new_user_in_role_page";
	private final ProgramsService programsService;
	private final UserService userService;
	private final RolePropertyEditor rolePropertyEditor;

	private final Logger log = Logger.getLogger(ManageUsersController.class);

	ManageUsersController() {
		this(null, null,  null);
	}

	@Autowired
	public ManageUsersController(ProgramsService programsService, UserService userService, RolePropertyEditor rolePropertyEditor) {
		this.programsService = programsService;
		this.userService = userService;
		this.rolePropertyEditor = rolePropertyEditor;

	}

	@RequestMapping(method = RequestMethod.GET, value = "/showPage")
	public String getUsersPage() {
		RegisteredUser user = getCurrentUser();

		if (!(user.isInRole(Authority.SUPERADMINISTRATOR) || user.isInRole(Authority.ADMINISTRATOR))) {
			throw new AccessDeniedException();
		}
		return ROLES_PAGE_VIEW_NAME;

	}

	@RequestMapping(method = RequestMethod.POST, value = "/createNewUser")
	public ModelAndView addNewUser(@ModelAttribute NewAdminUserDTO adminUser, @RequestParam Integer selectedProgramForNewUser,
			@ModelAttribute NewRolesDTO newRolesDTO, ModelMap modelMap) {

		Program selectedProgram = null;
		if (selectedProgramForNewUser != null && selectedProgramForNewUser != -1) {
			selectedProgram = getProgram(selectedProgramForNewUser);
		}

		adminUser.setProgramForNewUser(selectedProgramForNewUser);
		NewAdminUserDTOValidator validator = new NewAdminUserDTOValidator();
		BindingResult result = new DirectFieldBindingResult(adminUser, "adminUser");
		validator.validate(adminUser, result);

		if (result.hasErrors()) {
			modelMap.put("result", result);
			modelMap.put("selectedProgram", selectedProgram);
			modelMap.put("newUserFirstName", adminUser.getNewUserFirstName());
			modelMap.put("newUserLastName", adminUser.getNewUserLastName());
			modelMap.put("newUserEmail", adminUser.getNewUserEmail());

			return new ModelAndView(NEW_USER_VIEW_NAME, modelMap);
		}

		RegisteredUser potentiallyNewUser = userService.getUserByEmail(adminUser.getNewUserEmail());
		boolean isNewUser = false;
		if (potentiallyNewUser == null) {
			isNewUser = true;
			//potentiallyNewUser = createNewRegisteredUser(adminUser);
		}
		String id = "";
		if (selectedProgram != null && selectedProgram.getId() != null) {
			id = Integer.toString(selectedProgram.getId());
		}
		String view = "redirect:/manageUsers/showPage?programId=" + id;
		/*if (isNewUser) {
			try {
				Map<String, Object> model = modelMap();
				model.put("host", Environment.getInstance().getApplicationHostName());
				model.put("user", potentiallyNewUser);
				model.put("suggestingUser", getCurrentUser());
				model.put("programString", createProgramString(selectedProgramForNewUser));
				model.put("newUserRoles", createRolesString(potentiallyNewUser.getRoles()));
				InternetAddress toAddress = new InternetAddress(adminUser.getNewUserEmail(), adminUser.getNewUserFirstName() + " "
						+ adminUser.getNewUserLastName());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "UCL Portal Registration",
						"private/staff/mail/new_user_suggestion.ftl", model));
			} catch (Throwable e) {
				log.warn("error while sending email", e);
			}
		}*/

		return new ModelAndView(view);
	}
/*
	private String createRolesString(List<Role> roles) {
		StringBuilder rolesString = new StringBuilder();
		for (Role role : roles) {
			rolesString.append(role.getAuthority() + " ");
		}

		return rolesString.toString();
	

	private Object createProgramString(Integer selectedProgramForNewUser) {
		if (selectedProgramForNewUser != -1) {
			Program program = programsService.getProgramById(selectedProgramForNewUser);
			return "the program " + program.getTitle();
		}

		return "all programs";
	}
*/
/*	private RegisteredUser createNewRegisteredUser(NewAdminUserDTO adminUser) {
		RegisteredUser user = new RegisteredUser();

		user.setUsername(adminUser.getNewUserEmail());
		//user.setFirstName(adminUser.getNewUserFirstName());
		user.setLastName(adminUser.getNewUserLastName());
		user.setEmail(adminUser.getNewUserEmail());
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		//user.setEnabled(false);
		user.setCredentialsNonExpired(true);

		return user;
	}*/

	Map<String, Object> modelMap() {
		return new HashMap<String, Object>();
	}

	@ModelAttribute("selectedProgram")
	public Program getSelectedProgram(@RequestParam(required = false) Integer programId) {
		return getProgram(programId);
	}

	private Program getProgram(Integer programId) {
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
		return userService.getUser(((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails()).getId());
	}

	@ModelAttribute("availableUsers")
	public List<RegisteredUser> getavailableUsers() {
		return userService.getAllInternalUsers();
	}

	@ModelAttribute("authorities")
	public List<Authority> getAuthorities() {
		return getAuthoritiesInternal();
	}

	private List<Authority> getAuthoritiesInternal() {
		if (getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER);

		}
		return Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER);
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
	public ModelAndView updateUserWithNewRoles(@ModelAttribute("selectedUser") RegisteredUser selectedUser,
			@ModelAttribute("selectedProgram") Program selectedProgram, @ModelAttribute NewRolesDTO newRolesDTO, ModelMap modelMap) {
		
		UpdateUserForProgramWithRolesDTO dto = new UpdateUserForProgramWithRolesDTO();
		dto.setSelectedProgram(selectedProgram);
		dto.setSelectedUser(selectedUser);

		UpdateUserForProgramWithRolesDTOValidator validator = new UpdateUserForProgramWithRolesDTOValidator();
		BindingResult result = new DirectFieldBindingResult(dto, "dto");
		validator.validate(dto, result);

		if (result.hasErrors()) {
			modelMap.put("result", result);
			//putUserInRolesInternal(selectedProgram, modelMap);
			return new ModelAndView(ROLES_PAGE_VIEW_NAME, modelMap);
		}
		userService.updateUserWithNewRoles(selectedUser, selectedProgram, newRolesDTO);
		String id = "";
		if (selectedProgram != null && selectedProgram.getId() != null) {
			id = Integer.toString(selectedProgram.getId());
		}
		return new ModelAndView("redirect:/manageUsers/showPage?programId=" + id);
	}
	
	@ModelAttribute("usersInRoles")
	public List<RegisteredUser> getUsersInRoles(@RequestParam(required = false) Integer programId) {
		Program selectedProgram = getSelectedProgram(programId);
		if(selectedProgram == null){
			return new ArrayList<RegisteredUser>();
		}
		return userService.getAllUsersForProgram(selectedProgram);
	}
	
	
	
}
