package com.zuehlke.pgadmissions.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.timers.RegisteredUserReminderTimerTask;
import com.zuehlke.pgadmissions.utils.ApplicationQueryStringParser;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {
	private final Logger log = Logger.getLogger(RegisterController.class);
	private static final String REGISTER_USERS_VIEW_NAME = "public/register/register_applicant";
	private static final String REGISTER_INFO_VIEW_NAME = "public/register/register_info";
	// private static final String REGISTER_COMPLETE_VIEW_NAME =
	// "/register/complete";
	private static final String REGISTER_COMPLETE_VIEW_NAME = "public/register/registration_complete";
	private final UserService userService;
	private final RegisterFormValidator validator;
	private final RegistrationService registrationService;
	private final ApplicationsService applicationsService;
	private final ProgramsService programService;
	private final ApplicationQueryStringParser applicationQueryStringParser;
	protected final EncryptionHelper encryptionHelper;

	RegisterController() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
	public RegisterController(RegisterFormValidator validator, UserService userService, RegistrationService registrationService,
			ApplicationsService applicationsService, ProgramsService programService, ApplicationQueryStringParser applicationQueryStringParser, EncryptionHelper encryptionHelper) {
		this.validator = validator;
		this.userService = userService;
		this.registrationService = registrationService;
		this.applicationsService = applicationsService;
		this.programService = programService;
		this.applicationQueryStringParser = applicationQueryStringParser;
		this.encryptionHelper = encryptionHelper;
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
			if (suggestedUser.isEnabled() && suggestedUser.getDirectToUrl() != null) {
				return new ModelAndView("redirect:" + suggestedUser.getDirectToUrl());
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
	public ModelAndView submitRegistration(HttpServletRequest request, @ModelAttribute("record") RegisteredUser record,
			@RequestParam(required = false) Integer isSuggestedUser, BindingResult errors, ModelMap modelMap) {
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
		String queryString = (String) request.getSession().getAttribute("applyRequest");
		registrationService.generateAndSaveNewUser(record, isSuggestedUser, queryString);
		modelMap.put("user", record);
		return new ModelAndView(REGISTER_COMPLETE_VIEW_NAME);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/resendConfirmation")
	public String resendConfirmation(@RequestParam String encryptedUserId, ModelMap modelMap) {
		Integer userId = encryptionHelper.decryptToInteger(encryptedUserId);
		RegisteredUser user = userService.getUser(userId);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		registrationService.sendConfirmationEmail(user);
		modelMap.put("user", user);
		return REGISTER_COMPLETE_VIEW_NAME;
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
		if (user.getOriginalApplicationQueryString() != null) {
			redirectView = createApplicationAndReturnApplicationViewValue(user, redirectView);
		} else {
			if (user.getDirectToUrl() != null) {
				redirectView = redirectView + user.getDirectToUrl();
			} else {
				redirectView = redirectView + "/applications";
			}
		}

		return new ModelAndView(redirectView);

	}

	private String createApplicationAndReturnApplicationViewValue(RegisteredUser user, String redirectView) {
		String[] params = applicationQueryStringParser.parse(user.getOriginalApplicationQueryString());		
		Program program = programService.getProgramByCode(params[0]);
		Date batchDeadline = null;
		if (params[2] != null) {
			try {
				batchDeadline = new SimpleDateFormat("dd-MMM-yyyy").parse(params[2]);
			} catch (ParseException e) {
				//log, but don't prevent user activating account!
				log.warn("unparseable date in stored querystring:", e);				
			}
		}
		String researchHomePage = params[1];
		if(!UrlValidator.getInstance().isValid(researchHomePage)){
			researchHomePage = null;
		}
		ApplicationForm newApplicationForm = applicationsService.createAndSaveNewApplicationForm(user, program, batchDeadline, params[3],researchHomePage);
		redirectView = redirectView + "/application?applicationId=" + newApplicationForm.getApplicationNumber();
		return redirectView;
	}

	@ModelAttribute("record")
	public RegisteredUser getApplicantRecord(Integer id) {
		return new RegisteredUser();
	}

}
