package com.zuehlke.pgadmissions.controllers;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {

	private static final String REGISTER_APPLICANT_VIEW_NAME = "public/register/register_applicant";
	private static final String REGISTER_INFO_VIEW_NAME = "public/register/register_info";
	private static final String REGISTER_COMPLETE_VIEW_NAME = "/register/complete";
	private final UserService userService;
	private final ApplicantRecordValidator validator;
	private final RegistrationService registrationService;

	RegisterController() {
		this(null, null, null);
	}

	@Autowired
	public RegisterController(ApplicantRecordValidator validator,
			UserService userService, RegistrationService registrationService) {
		this.validator = validator;
		this.userService = userService;
		this.registrationService = registrationService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getRegisterPage() throws NoSuchAlgorithmException {
		RegisterPageModel model = new RegisterPageModel();
		model.setRecord(new RegistrationDTO());
		return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView submitRegistration(@ModelAttribute("record") RegistrationDTO record, BindingResult errors)
			 {
		validator.validate(record, errors);
		if(errors.hasErrors()){
			RegisterPageModel model = new RegisterPageModel();		
			model.setRecord(record);
			model.setResult(errors);
			return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
		}
		registrationService.generateAndSaveNewUser(record);		
		return new ModelAndView("redirect:" + REGISTER_COMPLETE_VIEW_NAME);
	
	}




	@RequestMapping(value = "/activateAccount", method = RequestMethod.GET)
	public ModelAndView activateAccountSubmit(@ModelAttribute RegisteredUser regUser,
			@RequestParam String activationCode) {
		RegisteredUser user = userService.getUserByUsername(regUser.getUsername());
		RegisterPageModel model = new RegisterPageModel();
		if (activationCode.equals(user.getActivationCode())) {
			user.setEnabled(true);
			userService.save(user);
			model.setUser(user);
			return new ModelAndView("public/login/login_page", "model", model);
		}
		model.setUser(user);
		model.setMessage("The activation has failed.");
		return new ModelAndView(REGISTER_INFO_VIEW_NAME, "model", model);
	}

	@ModelAttribute("record")
	public RegistrationDTO getApplicantRecord(Integer id) {
		return new RegistrationDTO();
	}

}
