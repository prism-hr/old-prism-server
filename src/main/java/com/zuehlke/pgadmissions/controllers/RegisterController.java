package com.zuehlke.pgadmissions.controllers;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping(value = "/register")
public class RegisterController {
    
    private final Logger log = LoggerFactory.getLogger(RegisterController.class);
    
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

	public RegisterController() {
		this(null, null, null, null, null, null, null);
	}

	@Autowired
    public RegisterController(RegisterFormValidator validator, UserService userService,
            RegistrationService registrationService, ApplicationsService applicationsService,
            ProgramsService programService, ApplicationQueryStringParser applicationQueryStringParser,
            EncryptionHelper encryptionHelper) {
		this.registerFormValidator = validator;
		this.userService = userService;
		this.registrationService = registrationService;
		this.applicationsService = applicationsService;
		this.programService = programService;
		this.applicationQueryStringParser = applicationQueryStringParser;
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public String submitRegistration(@ModelAttribute("pendingUser") RegisteredUser pendingUser, BindingResult result, Model model, HttpServletRequest request) {
		
	    registerFormValidator.validate(pendingUser, result);
	    
		if (result.hasErrors()) {
		    model.addAttribute("pendingUser", pendingUser);
		    return REGISTER_USERS_VIEW_NAME;
		}

		RegisteredUser existingDisabledUser = userService.getUserByEmailDisabledAccountsOnly(pendingUser.getEmail());
		if (existingDisabledUser != null && StringUtils.isBlank(pendingUser.getActivationCode())) {
		    // Kevin: This means a user tries to register without using the link provided in the registration email.
		    registrationService.sendInstructionsToRegisterIfActivationCodeIsMissing(existingDisabledUser);
		    return REGISTER_NOT_COMPLETE_VIEW_NAME;
		}
		
		String queryString = (String) request.getSession().getAttribute("applyRequest");
		RegisteredUser registeredUser = registrationService.updateOrSaveUser(pendingUser, queryString);
		model.addAttribute("pendingUser", registeredUser);
		return REGISTER_COMPLETE_VIEW_NAME;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/resendConfirmation")
	public String resendConfirmation(@RequestParam String activationCode, Model model) {
	
		RegisteredUser user = userService.getUserByActivationCode(activationCode);
		if (user == null) {
			throw new ResourceNotFoundException();
		}
		registrationService.sendConfirmationEmail(user);
		model.addAttribute("pendingUser", user);
		return REGISTER_COMPLETE_VIEW_NAME;
	}

	@RequestMapping(value = "/activateAccount", method = RequestMethod.GET)
	public String activateAccountSubmit(@RequestParam String activationCode, HttpServletRequest request) {
		RegisteredUser user = userService.getUserByActivationCode(activationCode);
		
		if (user == null) {				
			return REGISTER_INFO_VIEW_NAME;
		}
		
		user.setEnabled(true);
		userService.save(user);
		
		String redirectView = "redirect:";
		
		if (user.getOriginalApplicationQueryString() != null) {
			redirectView = createApplicationAndReturnApplicationViewValue(user, redirectView);
		} else if (user.getDirectToUrl() != null) {
		    redirectView += user.getDirectToUrl();
		} else if (StringUtils.isNotBlank((String) request.getSession().getAttribute("directToUrl"))) {
		    redirectView += (String) request.getSession().getAttribute("directToUrl");
		} else {
		    redirectView += "/applications";
		}
		
		if (StringUtils.contains(redirectView, "?")) {
		    redirectView += "&";
		} else {
		    redirectView += "?";
		}

		redirectView += "activationCode=" + user.getActivationCode();
		
		return redirectView;
	}

	private String createApplicationAndReturnApplicationViewValue(final RegisteredUser user, final String redirectView) {
		String[] params = applicationQueryStringParser.parse(user.getOriginalApplicationQueryString());		
		Program program = programService.getProgramByCode(params[0]);
		Date batchDeadline = null;
		if (params[2] != null && StringUtils.isNotBlank(params[2])) {
            try {
                batchDeadline = DateUtils.parseDate(params[2], new String[] {"dd-MMM-yyyy", "dd MMM yyyy"});
            } catch (ParseException e) {
                //log, but don't prevent user activating account!
                log.warn(String.format("Unparseable date in stored querystring: %s", params[2]), e);
            }
		}
		String researchHomePage = params[1];
		if(!UrlValidator.getInstance().isValid(researchHomePage)){
			researchHomePage = null;
		}
		ApplicationForm newApplicationForm = applicationsService.createOrGetUnsubmittedApplicationForm(user, program, batchDeadline, params[3],researchHomePage);
		return redirectView + "/application?applicationId=" + newApplicationForm.getApplicationNumber();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getRegisterPage(@RequestParam(required=false) String activationCode, @RequestParam(required=false) String directToUrl, Model modelMap, HttpServletRequest request) {
        RegisteredUser pendingUser = getPendingUser(activationCode, directToUrl);
        if (pendingUser == null && !StringUtils.containsIgnoreCase(getReferrerFromHeader(request), "pgadmissions") && !isAnApplyNewRequest(request)) {
            return "redirect:/login";
        }
	    
	    if (pendingUser != null && pendingUser.getDirectToUrl() != null && pendingUser.isEnabled()) {
            return "redirect:" + pendingUser.getDirectToUrl();
        } 
	    
	    if (pendingUser != null && !pendingUser.isEnabled() && StringUtils.isNotBlank(pendingUser.getDirectToUrl())) {
            request.getSession().setAttribute("directToUrl", pendingUser.getDirectToUrl());
        } 
	    
	    if (pendingUser == null && !StringUtils.containsIgnoreCase(getReferrerFromHeader(request), "pgadmissions") && !isAnApplyNewRequest(request)) {
            return "redirect:/login";
        } 
        
	    if (pendingUser == null) {
	        pendingUser = new RegisteredUser();
	    }
	    pendingUser.setDirectToUrl(directToUrl);
	    modelMap.addAttribute("pendingUser", pendingUser);
	    return REGISTER_USERS_VIEW_NAME;
	}

	public RegisteredUser getPendingUser(final String activationCode, final String directToUrl) {
        if (StringUtils.isBlank(activationCode)) {
            return null;
        }
        
        RegisteredUser pendingUser = userService.getUserByActivationCode(activationCode);
        if (pendingUser == null) {
            throw new ResourceNotFoundException();
        }
        
        if (directToUrl != null) {
            pendingUser.setDirectToUrl(directToUrl);
        }
        
        return pendingUser;
	}
	
	private String getReferrerFromHeader(final HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getHeader("referer"));
    }
	
	private DefaultSavedRequest getDefaultSavedRequest(final HttpServletRequest request) {
        return (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
    }
    
	private boolean isAnApplyNewRequest(final HttpServletRequest request) {
        DefaultSavedRequest defaultSavedRequest = getDefaultSavedRequest(request);
        return defaultSavedRequest != null && StringUtils.contains(defaultSavedRequest.getRequestURL(), "/apply/new");
    }
}
