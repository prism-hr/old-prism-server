package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
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
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RolePropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.validators.NewAdminUserDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class ManageUsersController {

	private static final String ROLES_PAGE_VIEW_NAME = "private/staff/superAdmin/assign_roles_page";
	private static final String NEW_USER_VIEW_NAME = "private/staff/superAdmin/create_new_user_in_role_page";
	private final ProgramsService programsService;
	private final UserService userService;
	private final RolePropertyEditor rolePropertyEditor;

	private final JavaMailSender mailsender;
	private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	private final Logger log = Logger.getLogger(ManageUsersController.class);

	ManageUsersController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ManageUsersController(ProgramsService programsService, UserService userService, RolePropertyEditor rolePropertyEditor,
			MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender) {
		this.programsService = programsService;
		this.userService = userService;
		this.rolePropertyEditor = rolePropertyEditor;
		this.mailsender = mailsender;
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
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

	@RequestMapping(method = RequestMethod.GET, value = "/createNewUser")
	public ModelAndView createNewUser(@RequestParam(required = false) Integer selectedProgramForNewUser, ModelMap modelMap) {
		if (selectedProgramForNewUser!= null && selectedProgramForNewUser == -1) {
			modelMap.put("allProgramsSelected", "yes");
			if (getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
				modelMap.put("authorities", Arrays.asList(Authority.SUPERADMINISTRATOR));
			} else {
				modelMap.put("authorities", Arrays.asList());
			}
		} else {
			modelMap.put("selectedProgram", getProgram(selectedProgramForNewUser));
			modelMap.put("authorities", getAuthoritiesInternal());
		}

		return new ModelAndView(NEW_USER_VIEW_NAME);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/createNewUser")
	public ModelAndView addNewUser(@ModelAttribute NewAdminUserDTO adminUser,
			@RequestParam Integer selectedProgramForNewUser, @ModelAttribute NewRolesDTO newRolesDTO, ModelMap modelMap) {

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
			potentiallyNewUser = createNewRegisteredUser(adminUser);
		}
		String view = updateSelectedUserInternal(potentiallyNewUser, selectedProgram, newRolesDTO);
		System.out.println("!!!!is new user :"+ isNewUser);
		if (isNewUser) {
			try {
				Map<String, Object> model = modelMap();
				model.put("host", Environment.getInstance().getApplicationHostName());
				model.put("user", potentiallyNewUser);
				model.put("suggestingUser", getCurrentUser());
				model.put("programString", createProgramString(selectedProgramForNewUser));
				model.put("newUserRoles", createRolesString(potentiallyNewUser.getRoles()));
				InternetAddress toAddress = new InternetAddress(adminUser.getNewUserEmail(), adminUser.getNewUserFirstName() + " "+ adminUser.getNewUserLastName());
				mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, "UCL Portal Registration", "private/staff/mail/new_user_suggestion.ftl", model));
			} catch (Throwable e) {
				log.warn("error while sending email",e);
			}
		}

		return new ModelAndView(view);
	}

	private String createRolesString(List<Role> roles) {
		StringBuilder rolesString = new StringBuilder();
		for (Role role : roles) {
			rolesString.append(role.getAuthority() + " ");
		}
		
		return rolesString.toString();
	}

	private Object createProgramString(Integer selectedProgramForNewUser) {
		if (selectedProgramForNewUser != -1) {
			Program program = programsService.getProgramById(selectedProgramForNewUser);
			return "the program " + program.getTitle();
		}
		
		return "all programs";
	}

	private RegisteredUser createNewRegisteredUser(NewAdminUserDTO adminUser) {
		RegisteredUser user = new RegisteredUser();

		user.setUsername(adminUser.getNewUserEmail());
		user.setFirstName(adminUser.getNewUserFirstName());
		user.setLastName(adminUser.getNewUserLastName());
		user.setEmail(adminUser.getNewUserEmail());
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setEnabled(false);
		user.setCredentialsNonExpired(true);

		return user;
	}

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

		return updateSelectedUserInternal(selectedUser, selectedProgram, newRolesDTO);
	}

	private String updateSelectedUserInternal(RegisteredUser selectedUser, Program selectedProgram, NewRolesDTO newRolesDTO) {
		if(getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)){
			removeFromSuperadminRoleIfRequired(selectedUser, newRolesDTO);
		}
		for (Authority authority : Authority.values()) {
			addToRoleIfRequired(selectedUser, newRolesDTO, authority);
		}

		addOrRemoveFromProgramsOfWhichAdministratorIfRequired(selectedUser, selectedProgram, newRolesDTO);
		addOrRemoveFromProgramsOfWhichApproverIfRequired(selectedUser, selectedProgram, newRolesDTO);
		addOrRemoveFromProgramsOfWhichReviewerIfRequired(selectedUser, selectedProgram, newRolesDTO);
		userService.save(selectedUser);
		String id = "";
		if (selectedProgram != null && selectedProgram.getId() != null) {
			id = Integer.toString(selectedProgram.getId());
		}
		return "redirect:/manageUsers/showPage?programId=" + id;
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
