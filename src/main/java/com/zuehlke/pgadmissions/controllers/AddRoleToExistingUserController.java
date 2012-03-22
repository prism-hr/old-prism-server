package com.zuehlke.pgadmissions.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ManageUsersModel;
import com.zuehlke.pgadmissions.propertyeditors.RolePropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/assignUser")
public class AddRoleToExistingUserController {
	
	private final ProgramsService programsService;
	private final UserService userService;
	private final RolePropertyEditor rolePropertyEditor;

	public AddRoleToExistingUserController() {
		this(null, null, null);
	}
	
	@Autowired
	public AddRoleToExistingUserController(UserService userService, ProgramsService programsService, RolePropertyEditor rolePropertyEditor) {
		this.userService = userService;
		this.programsService = programsService;
		this.rolePropertyEditor = rolePropertyEditor;
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RolePropertyEditor.class, rolePropertyEditor);
	}
	
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView addRoleToExistingUser(@RequestParam Integer programId, @ModelAttribute UserDTO userDTO) {
		RegisteredUser user = userService.getUser(userDTO.getUserId());
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		
		Program program = programsService.getProgramById(programId);
		if (program == null) {
			throw new ResourceNotFoundException();
		}
		
		List<Role> userRoles = user.getRoles();
		List<Role> userDTORoles = userDTO.getRoles();
		for (Role role : userDTORoles) {
			if(!containsRole(userRoles, role)){
				System.out.println(role.getAuthority());
				user.getRoles().add(role);
				userService.save(user);
			}
			if(!program.isUserWithRoleInProgram(user, role)){
				program.addUserToRightRoleList(user, role);
				programsService.save(program);
			}
		}
		return new  ModelAndView("redirect:/manageUsers/showPage","programId", program.getId());
	}
	
	private boolean containsRole(List<Role> roles, Role userRole){
		for (Role role : roles) {
			if(userRole.getAuthority().equals(role.getAuthority())){
				return true;
			}
		}
		return false;
	}
	
	
//	@ModelAttribute("selectedProgram")
//	public Program getProgram(Integer programId) {
//		Program program = programsService.getProgramById(programId);
//		if (program == null) {
//			throw new ResourceNotFoundException();
//		}
//		return program;
//	}

}
