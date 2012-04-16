package com.zuehlke.pgadmissions.controllers;

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

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

@Controller
@RequestMapping(value = { "/refereeRegistration" })
public class RegisterRefereeController {
	
	private final UserService userService;
	private final RefereeService refereeService;
	private final RegisterFormValidator validator;
	private static final String REGISTER_REFEREE_VIEW_NAME = "private/referees/register_referee";
	private static final String REGISTER_COMPLETE_VIEW_NAME = "/register/complete";
	private final EncryptionUtils encryptionUtils;

	RegisterRefereeController(){
		this(null, null, null, null);
	}
	
	@InitBinder(value="referee")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@Autowired
	public RegisterRefereeController(UserService userService, RefereeService refereeService,
			RegisterFormValidator validator, EncryptionUtils encryptionUtils){
		this.userService = userService;
		this.refereeService = refereeService;
		this.validator = validator;
		this.encryptionUtils = encryptionUtils;
	}

	@RequestMapping(value = "/submit",method = RequestMethod.POST)
	public String submitRefereeAndGetLoginPage(@Valid RegisteredUser referee, BindingResult result) {
		if(result.hasErrors()){
			return REGISTER_REFEREE_VIEW_NAME;
		}
		referee.setPassword(encryptionUtils.getMD5Hash(referee.getPassword()));
		referee.setEnabled(true);
		referee.setAccountNonExpired(true);
		referee.setAccountNonLocked(true);
		referee.setCredentialsNonExpired(true);
		userService.saveAndEmailRegisterConfirmationToReferee(referee);
		return "redirect:" + REGISTER_COMPLETE_VIEW_NAME;

	}
	
	@ModelAttribute
	public RegisteredUser getReferee(@RequestParam Integer recordId) {
		RegisteredUser referee = userService.getUser(recordId);
		if (referee == null || referee.getReferees() == null) {
			throw new ResourceNotFoundException();
		}
		return referee;
	}

}
