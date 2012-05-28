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
import com.zuehlke.pgadmissions.dto.NewUserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class CreateNewUserController {
	private static final String NEW_USER_VIEW_NAME = "private/staff/superAdmin/create_new_user_in_role_page";
	private final ProgramsService programsService;
	private final UserService userService;
	private final ProgramPropertyEditor programPropertyEditor;
	private final NewUserDTOValidator newUserDTOValidator;

	CreateNewUserController() {
		this(null, null, null, null);
	}

	@Autowired
	public CreateNewUserController(ProgramsService programsService, UserService userService, ProgramPropertyEditor programPropertyEditor, NewUserDTOValidator newUserDTOValidator) {
		this.programsService = programsService;
		this.userService = userService;
		this.programPropertyEditor = programPropertyEditor;
		this.newUserDTOValidator = newUserDTOValidator;

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
		if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER, Authority.SUPERVISOR);

		}
		return Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.REVIEWER, Authority.INTERVIEWER, Authority.SUPERVISOR);
	}

	@ModelAttribute("newUserDTO")
	public NewUserDTO getNewUserDTO() {
		return new NewUserDTO();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/createNewUser")
	public String getAddUsersView() {
		checkPermissions();
		return NEW_USER_VIEW_NAME;
	}

	
	@RequestMapping(method = RequestMethod.POST, value = "/createNewUser")
	public String handleNewUserToProgramSubmission(@Valid @ModelAttribute("newUserDTO") NewUserDTO newUserDTO, BindingResult result) {
		checkPermissions();
		if(result.hasErrors()){
			return NEW_USER_VIEW_NAME;
			
		}
		RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(newUserDTO.getEmail());
		if (existingUser != null) {
			userService.updateUserWithNewRoles(existingUser, newUserDTO.getSelectedProgram(), newUserDTO.getSelectedAuthorities());
		} else {
			userService.createNewUserForProgramme(newUserDTO.getFirstName(), newUserDTO.getLastName(), newUserDTO.getEmail(), newUserDTO.getSelectedProgram(),
					newUserDTO.getSelectedAuthorities());
		}
		if(newUserDTO.getSelectedProgram() == null){
			return "redirect:/manageUsers/showPage";
		}
		return "redirect:/manageUsers/showPage?programId="  + newUserDTO.getSelectedProgram().getId();
	}
	
	private void checkPermissions() {
		if (!userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) && !userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
	}

	@InitBinder(value = "newUserDTO")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(newUserDTOValidator);
		binder.registerCustomEditor(Program.class, programPropertyEditor);

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



}
