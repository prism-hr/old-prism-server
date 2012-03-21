package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
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
	private final ApplicationsService applicationsService;

	RegisterController() {
		this(null, null, null, null);
	}

	@Autowired
	public RegisterController(ApplicantRecordValidator validator, UserService userService, RegistrationService registrationService,
			ApplicationsService applicationsService) {
		this.validator = validator;
		this.userService = userService;
		this.registrationService = registrationService;
		this.applicationsService = applicationsService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getRegisterPage() {
		RegisterPageModel model = new RegisterPageModel();
		model.setRecord(new RegistrationDTO());
		return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView submitRegistration(@ModelAttribute("record") RegistrationDTO record, BindingResult errors) {
		validator.validate(record, errors);
		if (errors.hasErrors()) {
			RegisterPageModel model = new RegisterPageModel();
			model.setRecord(record);
			model.setResult(errors);
			return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
		}
		registrationService.generateAndSaveNewUser(record);
		return new ModelAndView("redirect:" + REGISTER_COMPLETE_VIEW_NAME);

	}

	@RequestMapping(value = "/activateAccount", method = RequestMethod.GET)
	public ModelAndView activateAccountSubmit(@RequestParam String activationCode) {
		RegisteredUser user = registrationService.findUserForActivationCode(activationCode);
		if (user == null) {
			RegisterPageModel pageModel = new RegisterPageModel();
			pageModel.setMessage("Sorry, the system was unable to process the activation request.");
			return new ModelAndView(REGISTER_INFO_VIEW_NAME, "model", pageModel);
		}
		user.setEnabled(true);
		userService.save(user);
		String redirectView = "redirect:";
		if (user.getProjectOriginallyAppliedTo() != null) {
			ApplicationForm newApplicationForm = applicationsService.createAndSaveNewApplicationForm(user, user.getProjectOriginallyAppliedTo());
			redirectView = redirectView + "/application?id=" + newApplicationForm.getId();
		} else {
			redirectView = redirectView + "/applications";
		}

		return new ModelAndView(redirectView);

	}

	@ModelAttribute("record")
	public RegistrationDTO getApplicantRecord(Integer id) {
		return new RegistrationDTO();
	}

}
