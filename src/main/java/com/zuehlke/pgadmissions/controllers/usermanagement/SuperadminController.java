package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.SuperadminUserDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class SuperadminController {

	private static final String SUPERADMIN_VIEW = "private/staff/superAdmin/superadmin_management";
	private final UserService userService;
	private final SuperadminUserDTOValidator userDTOValidator;

	SuperadminController() {
		this(null, null);
	}

	@Autowired
	public SuperadminController(UserService userService, SuperadminUserDTOValidator userDTOValidator) {
		this.userService = userService;
		this.userDTOValidator = userDTOValidator;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/superadmins")
	public String getSuperadminPage() {
		if (!userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		return SUPERADMIN_VIEW;
	}

	@ModelAttribute("superadmins")
	public List<RegisteredUser> getSuperadmins() {
		List<RegisteredUser> superadmins = userService.getUsersInRole(Authority.SUPERADMINISTRATOR);

		Collections.sort(superadmins, new Comparator<RegisteredUser>() {

			@Override
			public int compare(RegisteredUser o1, RegisteredUser o2) {
				if (!o1.getLastName().equals(o2.getLastName())) {
					return o1.getLastName().compareTo(o2.getLastName());
				}
				return o1.getFirstName().compareTo(o2.getFirstName());
			}
		});
		return superadmins;
	}

	@InitBinder(value = "userDTO")
	public void registerValidator(WebDataBinder binder) {
		binder.setValidator(userDTOValidator);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }
        
    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/superadmins")
	public String handleAddSuperAdmin(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result) {
		if (!userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		if (result.hasErrors()) {
			return SUPERADMIN_VIEW;

		}
		RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(userDTO.getEmail());
		if (existingUser != null) {
			userService.updateUserWithNewRoles(existingUser, null, Authority.SUPERADMINISTRATOR);
		} else {
			existingUser = userService.createNewUserForProgramme(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), null,
					Authority.SUPERADMINISTRATOR);
		}

		return "redirect:/manageUsers/superadmins";

	}

	@ModelAttribute("userDTO")
	public UserDTO getUserDTO() {
		return new UserDTO();
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}
}
