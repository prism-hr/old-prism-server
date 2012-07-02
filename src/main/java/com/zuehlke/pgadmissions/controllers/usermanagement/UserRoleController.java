package com.zuehlke.pgadmissions.controllers.usermanagement;

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
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.UserDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class UserRoleController {
	private static final String NEW_USER_VIEW_NAME = "private/staff/superAdmin/create_new_user_in_role_page";
	private final ProgramsService programsService;
	private final UserService userService;
	private final ProgramPropertyEditor programPropertyEditor;
	private final UserDTOValidator newUserDTOValidator;
	private final EncryptionHelper encryptionHelper;

	UserRoleController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public UserRoleController(ProgramsService programsService, UserService userService, ProgramPropertyEditor programPropertyEditor, UserDTOValidator newUserDTOValidator, EncryptionHelper encryptionHelper) {
		this.programsService = programsService;
		this.userService = userService;
		this.programPropertyEditor = programPropertyEditor;
		this.newUserDTOValidator = newUserDTOValidator;
		this.encryptionHelper = encryptionHelper;

	}

	@ModelAttribute("programs")
	public List<Program> getPrograms() {
		if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
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
			return Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER, Authority.SUPERVISOR);
	}

	@ModelAttribute("userDTO")
	public UserDTO getNewUserDTO(@RequestParam(required=false) String user, @RequestParam(required = false) String programCode) {

		if(user == null){
			return newUserDTO(programCode);			
		}
		return createUserDTOFromExistingUser(user, programCode);
	}

	private UserDTO createUserDTOFromExistingUser(String user, String programCode) {
		RegisteredUser selectedUser = userService.getUser(encryptionHelper.decryptToInteger(user));
		UserDTO userDTO = new UserDTO();
		userDTO.setFirstName(selectedUser.getFirstName());
		userDTO.setLastName(selectedUser.getLastName());
		userDTO.setEmail(selectedUser.getEmail());
		Program selectedProgram = getSelectedProgram(programCode);
		userDTO.setSelectedProgram(selectedProgram);
		List<Authority> authoritiesForProgram = selectedUser.getAuthoritiesForProgram(selectedProgram);
		Authority[] selectedAuthorities = authoritiesForProgram.toArray(new Authority[] {});
		userDTO.setSelectedAuthorities(selectedAuthorities);
		userDTO.setNewUser(false);
		return userDTO;
	}

	private UserDTO newUserDTO(String programCode) {
		UserDTO userDTO = new UserDTO();
		userDTO.setSelectedProgram(getSelectedProgram(programCode));
		return userDTO;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/edit")
	public String getAddUsersView() {
		checkPermissions();
		return NEW_USER_VIEW_NAME;
	}

	
	@RequestMapping(method = RequestMethod.POST, value = "/edit")
	public String handleEditUserRoles(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result) {
		checkPermissions();
		if(result.hasErrors()){
			return NEW_USER_VIEW_NAME;
			
		}
		RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(userDTO.getEmail());
		if (existingUser != null) {
			userService.updateUserWithNewRoles(existingUser, userDTO.getSelectedProgram(), userDTO.getSelectedAuthorities());
		} else {
			existingUser = userService.createNewUserForProgramme(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getSelectedProgram(),
					userDTO.getSelectedAuthorities());
		}
		if(userDTO.getSelectedProgram() == null){
			return "redirect:/manageUsers/edit";
		}
		return "redirect:/manageUsers/edit?programCode="  + userDTO.getSelectedProgram().getCode();
	}
	
	private void checkPermissions() {
		if (!userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) && !userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
	}

	@InitBinder(value = "userDTO")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(newUserDTOValidator);
		binder.registerCustomEditor(Program.class, programPropertyEditor);

	}
	

	Program getSelectedProgram( String programCode) {
		if (programCode == null) {
			return null;
		}
		Program selectedProgram = programsService.getProgramByCode(programCode);
		if (selectedProgram == null) {
			throw new ResourceNotFoundException();
		}
		return selectedProgram;
			
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/remove")
	public String handleRemoveUserFromProgram(@ModelAttribute("userDTO") UserDTO userDTO) {
		checkPermissions();
		userService.updateUserWithNewRoles(userService.getUserByEmailIncludingDisabledAccounts(userDTO.getEmail()), userDTO.getSelectedProgram());		
		return "redirect:/manageUsers/edit?programCode="  + userDTO.getSelectedProgram().getCode();
	}
	


}
