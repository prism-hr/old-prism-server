package com.zuehlke.pgadmissions.controllers;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {

	private static final String REGISTER_USERS_VIEW_NAME = "public/register/register_applicant";
	private static final String REGISTER_INFO_VIEW_NAME = "public/register/register_info";
//	private static final String REGISTER_COMPLETE_VIEW_NAME = "/register/complete";
	private static final String REGISTER_COMPLETE_VIEW_NAME = "public/register/registration_complete";
	private final UserService userService;
	private final RegisterFormValidator validator;
	private final RegistrationService registrationService;
	private final ApplicationsService applicationsService;

	RegisterController() {
		this(null, null, null, null);
	}

	@Autowired
	public RegisterController(RegisterFormValidator validator, UserService userService, RegistrationService registrationService,
			ApplicationsService applicationsService) {
		this.validator = validator;
		this.userService = userService;
		this.registrationService = registrationService;
		this.applicationsService = applicationsService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getRegisterPage(@RequestParam(required = false) Integer userId) {
		RegisterPageModel model = new RegisterPageModel();
		RegisteredUser record = new RegisteredUser();
		if (userId != null) {
			RegisteredUser suggestedUser = userService.getUser(userId);
			if (suggestedUser == null) {
				throw new ResourceNotFoundException();
			}
			if(suggestedUser.isEnabled() && suggestedUser.getDirectToUrl() != null){
				return new ModelAndView("redirect:"+ suggestedUser.getDirectToUrl());
			}
			record.setFirstName(suggestedUser.getFirstName());
			record.setLastName(suggestedUser.getLastName());
			record.setEmail(suggestedUser.getEmail());
			model.setIsSuggestedUser(userId);
		}
		model.setRecord(record);
		return new ModelAndView(REGISTER_USERS_VIEW_NAME, "model", model);
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView submitRegistration(@ModelAttribute("record") RegisteredUser record, @RequestParam(required = false) Integer isSuggestedUser,
			BindingResult errors, ModelMap modelMap) {
		if (isSuggestedUser != null) {
			validator.shouldValidateSameEmail(false);
		} else {
			validator.shouldValidateSameEmail(true);
		}
		validator.validate(record, errors);

		if (errors.hasErrors()) {
			RegisterPageModel model = new RegisterPageModel();
			model.setRecord(record);
			model.setResult(errors);
			return new ModelAndView(REGISTER_USERS_VIEW_NAME, "model", model);
		}
		registrationService.generateAndSaveNewUser(record, isSuggestedUser);
		modelMap.put("user", record);
		return new ModelAndView(REGISTER_COMPLETE_VIEW_NAME);

	}
	
	@RequestMapping(method = RequestMethod.GET, value="/resendConfirmation")
	public String resendConfirmation(@RequestParam Integer userId, ModelMap modelMap) {
		RegisteredUser user = userService.getUser(userId);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		registrationService.sendConfirmationEmail(user);
		modelMap.put("user", user);
		return REGISTER_COMPLETE_VIEW_NAME;
	}
	

	@RequestMapping(value = "/activateAccount", method = RequestMethod.GET)
	public ModelAndView activateAccountSubmit(@RequestParam String activationCode) throws ParseException {
		RegisteredUser user = registrationService.findUserForActivationCode(activationCode);
		if (user == null) {
			RegisterPageModel pageModel = new RegisterPageModel();
			pageModel.setMessage("Sorry, the system was unable to process the activation request.");
			return new ModelAndView(REGISTER_INFO_VIEW_NAME, "model", pageModel);
		}
		user.setEnabled(true);
		userService.save(user);
		String redirectView = "redirect:";
		if (user.getProgramOriginallyAppliedTo() != null) {
			ApplicationForm newApplicationForm = applicationsService.createAndSaveNewApplicationForm(user, user.getProgramOriginallyAppliedTo(), null, null);
			redirectView = redirectView + "/application?applicationId=" + newApplicationForm.getApplicationNumber();
		} else {
			if(user.getDirectToUrl() != null){
				redirectView = redirectView + user.getDirectToUrl();
			}
			else{
				redirectView = redirectView + "/applications";
			}
		}

		return new ModelAndView(redirectView);

	}

	@ModelAttribute("record")
	public RegisteredUser getApplicantRecord(Integer id) {
		return new RegisteredUser();
	}

}
