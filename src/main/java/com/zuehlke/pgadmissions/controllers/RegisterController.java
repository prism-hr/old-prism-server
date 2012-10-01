package com.zuehlke.pgadmissions.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.ApplicationQueryStringParser;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {
	private final Logger log = Logger.getLogger(RegisterController.class);
	private static final String REGISTER_USERS_VIEW_NAME = "public/register/register_applicant";
	private static final String REGISTER_INFO_VIEW_NAME = "public/register/activation_failed";
	private static final String REGISTER_COMPLETE_VIEW_NAME = "public/register/registration_complete";
	private static final String REGISTER_NOT_COMPLETE_VIEW_NAME = "public/register/registration_not_complete";
	private final UserService userService;
	private final RegisterFormValidator registerFormValidator;
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
		this.registerFormValidator = validator;
		this.userService = userService;
		this.registrationService = registrationService;
		this.applicationsService = applicationsService;
		this.programService = programService;
		this.applicationQueryStringParser = applicationQueryStringParser;
		this.encryptionHelper = encryptionHelper;
	}



	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public String submitRegistration( @Valid @ModelAttribute("pendingUser") RegisteredUser pendingUser,	BindingResult result, HttpServletRequest request) {
		
		if (result.hasErrors()) {
		    return REGISTER_USERS_VIEW_NAME;
		}

		RegisteredUser existingDisabledUser = userService.getUserByEmailDisabledAccountsOnly(pendingUser.getEmail());
		if (existingDisabledUser != null && StringUtils.isBlank(pendingUser.getActivationCode())) {
		    // Kevin: This means a user tries to register without using the link provided in the registration email.
		    registrationService.sendInstructionsToRegisterIfActivationCodeIsMissing(existingDisabledUser);
		    return REGISTER_NOT_COMPLETE_VIEW_NAME;
		}
		
		String queryString = (String) request.getSession().getAttribute("applyRequest");
		registrationService.updateOrSaveUser(pendingUser, queryString);

		return REGISTER_COMPLETE_VIEW_NAME;

	}

	@RequestMapping(method = RequestMethod.GET, value = "/resendConfirmation")
	public String resendConfirmation(@RequestParam String activationCode) {
	
		RegisteredUser user = userService.getUserByActivationCode(activationCode);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		registrationService.sendConfirmationEmail(user);

		return REGISTER_COMPLETE_VIEW_NAME;
	}

	@RequestMapping(value = "/activateAccount", method = RequestMethod.GET)
	public String activateAccountSubmit(@RequestParam String activationCode) {
		RegisteredUser user = userService.getUserByActivationCode(activationCode);
		if (user == null) {				
			return REGISTER_INFO_VIEW_NAME;
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

		return redirectView;

	}

	private String createApplicationAndReturnApplicationViewValue(RegisteredUser user, String redirectView) {
		String[] params = applicationQueryStringParser.parse(user.getOriginalApplicationQueryString());		
		Program program = programService.getProgramByCode(params[0]);
		Date batchDeadline = null;
		if (params[2] != null) {
			try {
				batchDeadline = new SimpleDateFormat("dd-MMM-yyyy").parse(params[2]);
			} catch (ParseException ignore) {
				try {
					batchDeadline = new SimpleDateFormat("dd MMM yyyy").parse(params[2]);
				} catch (ParseException e) {
					//log, but don't prevent user activating account!
					log.warn("unparseable date in stored querystring:", e);		
				}
						
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
	
	@RequestMapping(method = RequestMethod.GET)
	public String getRegisterPage(@ModelAttribute("pendingUser") RegisteredUser pendingUser) {
		if(pendingUser != null && pendingUser.getDirectToUrl() != null && pendingUser.isEnabled()){
			return "redirect:" +  pendingUser.getDirectToUrl();
		}
		return REGISTER_USERS_VIEW_NAME;
	}

	@ModelAttribute("pendingUser")
	public RegisteredUser getPendingUser(@RequestParam(required=false) String activationCode, @RequestParam(required=false) String directToUrl) {
		if(StringUtils.isBlank(activationCode)){
			return new RegisteredUser();
		}
		RegisteredUser pendingUser = userService.getUserByActivationCode(activationCode);
		if(pendingUser == null){
			throw new ResourceNotFoundException();
		}
		if(directToUrl != null){
			pendingUser.setDirectToUrl(directToUrl);
		}
		return pendingUser;
	}
	
	@InitBinder(value = "pendingUser")
	public void registerValidator(WebDataBinder binder) {
		binder.setValidator(registerFormValidator);
		
	}

}
