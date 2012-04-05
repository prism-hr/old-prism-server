package com.zuehlke.pgadmissions.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

public class RegisterRefereeController {
	
	private final UserService userService;
	private final RefereeService refereeService;
	private final RegisterFormValidator validator;
	private static final String REGISTER_REFEREE_VIEW_NAME = "private/referees/register_referee";
	private final EncryptionUtils encryptionUtils;

	RegisterRefereeController(){
		this(null, null, null, null);
	}
	
	@Autowired
	public RegisterRefereeController(UserService userService, RefereeService refereeService,
			RegisterFormValidator validator, EncryptionUtils encryptionUtils){
		this.userService = userService;
		this.refereeService = refereeService;
		this.validator = validator;
		this.encryptionUtils = encryptionUtils;
	}

	@RequestMapping(value = "/register/submit",method = RequestMethod.POST)
	public String submitRefereeAndGetLoginPage(@Valid RegisteredUser referee, BindingResult result) {
		if(result.hasErrors()){
			return REGISTER_REFEREE_VIEW_NAME;
		}
		referee.setPassword(encryptionUtils.getMD5Hash(referee.getPassword()));
		referee.setEnabled(true);
		userService.save(referee);
		return "redirect:/";

	}
	
	@ModelAttribute
	public RegisteredUser getReferee(@RequestParam Integer refereeId) {
		RegisteredUser referee = userService.getUser(refereeId);
		if (referee == null || referee.getReferee() == null) {
			throw new ResourceNotFoundException();
		}
		return referee;
	}

}
