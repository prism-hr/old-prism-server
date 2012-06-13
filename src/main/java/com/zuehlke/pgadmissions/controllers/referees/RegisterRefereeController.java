package com.zuehlke.pgadmissions.controllers.referees;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RefereePropertyEditor;
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
	private final RefereePropertyEditor refereeproperrtyEditor;

	RegisterRefereeController(){
		this(null, null, null, null, null);
	}
	
	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
//		binder.setValidator(validator);
		binder.registerCustomEditor(Referee.class, refereeproperrtyEditor);
	}
	
	@Autowired
	public RegisterRefereeController(UserService userService, RefereeService refereeService,
			RegisterFormValidator validator, EncryptionUtils encryptionUtils, RefereePropertyEditor refereeproperrtyEditor){
		this.userService = userService;
		this.refereeService = refereeService;
		this.validator = validator;
		this.encryptionUtils = encryptionUtils;
		this.refereeproperrtyEditor = refereeproperrtyEditor;
	}

	@RequestMapping(value = "/submit",method = RequestMethod.POST)
	public ModelAndView submitRefereeAndGetLoginPage(@ModelAttribute RegisteredUser refereeUser, BindingResult result, ModelMap modelMap) {
		validator.validate(refereeUser, result);
		if(result.hasErrors()){
			modelMap.put("referee", refereeUser);
			modelMap.put("result", result);
			return new ModelAndView(REGISTER_REFEREE_VIEW_NAME, modelMap);
		}
		refereeUser.setPassword(encryptionUtils.getMD5Hash(refereeUser.getPassword()));
		refereeUser.setEnabled(true);
		refereeUser.setAccountNonExpired(true);
		refereeUser.setAccountNonLocked(true);
		refereeUser.setCredentialsNonExpired(true);
		userService.saveAndEmailRegisterConfirmationToReferee(refereeUser);
		return  new ModelAndView("redirect:" + REGISTER_COMPLETE_VIEW_NAME);

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
