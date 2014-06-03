package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

@Controller
@RequestMapping(value = "/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private RegisterFormValidator registerFormValidator;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ApplicationService applicationFormService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ActionService actionService;

    @RequestMapping(value = "/submit", method = RequestMethod.GET)
    public String defaultGet(@ModelAttribute("pendingUser") User pendingUser, Model model, HttpSession session) {
        model.addAttribute("pendingUser", pendingUser);
        return TemplateLocation.REGISTRATION_FORM;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submitRegistration(@ModelAttribute("pendingUser") User pendingUser, BindingResult result, Model model, HttpServletRequest request) {
        registerFormValidator.validate(pendingUser, result);

        if (result.hasErrors()) {
            model.addAttribute("pendingUser", pendingUser);
            return TemplateLocation.REGISTRATION_FORM;
        }

        Integer advertId = (Integer) request.getSession().getAttribute("requestAdvertId");
        Advert advert = programService.getById(advertId);
        User user = registrationService.submitRegistration(pendingUser, advert);
        model.addAttribute("pendingUser", user);
        return TemplateLocation.REGISTRATION_SUCCESS_CONFIRMATION;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/resendConfirmation")
    public String resendConfirmation(@RequestParam String activationCode, Model model) {

        User user = userService.getUserByActivationCode(activationCode);
        if (user == null) {
            throw new ResourceNotFoundException();
        }
        // TODO resend confirmation email
        registrationService.sendConfirmationEmail(user);
        model.addAttribute("pendingUser", user);
        return TemplateLocation.REGISTRATION_SUCCESS_CONFIRMATION;
    }

    @RequestMapping(value = "/activateAccount", params = "action", method = RequestMethod.GET)
    public String activateAccountSubmit(@RequestParam String activationCode, @RequestParam PrismAction action, Integer resourceId) throws Exception {

        User user = registrationService.activateAccount(activationCode);

        if (user == null) {
            return TemplateLocation.REGISTRATION_FAILURE_CONFIRMATION;
        }

        ActionOutcome actionOutcome = actionService.executeAction(resourceId, user, action, new Comment());
        return actionOutcome.createRedirectionUrl();
    }

    @RequestMapping(value = "/activateAccount", method = RequestMethod.GET)
    public String activateAccountSubmit(@RequestParam String activationCode, Integer scopeId) {
        // TODO check type of scope
        return "redirect:/activateAccount?activationCode=" + activationCode + "action=PROGRAM_CREATE_ACTION&scopeId=" + scopeId;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getRegisterPage(@RequestParam(required = false) String activationCode, @RequestParam(required = false) String advert, Model modelMap,
            HttpServletRequest request, HttpSession session) {
        session.removeAttribute("CLICKED_ON_ALREADY_REGISTERED");
        User pendingUser = getPendingUser(activationCode);
        if (pendingUser == null && !StringUtils.containsIgnoreCase(getReferrerFromHeader(request), "pgadmissions") && !isAnApplyNewRequest(request)) {
            return RedirectLocation.LOGIN;
        }

        // TODO perform redirection based on action column
        // if (pendingUser != null && pendingUser.getDirectToUrl() != null && pendingUser.isEnabled()) {
        // return RedirectLocation.REDIRECT + pendingUser.getDirectToUrl();
        // }

        // TODO perform redirection based on action column
        // if (pendingUser != null && !pendingUser.isEnabled() && StringUtils.isNotBlank(pendingUser.getDirectToUrl())) {
        // request.getSession().setAttribute("directToUrl", pendingUser.getDirectToUrl());
        // }

        if (pendingUser == null) {
            pendingUser = new User();
        }

        // TODO set action instead direct to URL
        // pendingUser.setDirectToUrl(directToUrl);
        modelMap.addAttribute("pendingUser", pendingUser);

        if (advert != null) {
            Integer requestedAdvertId = Integer.valueOf(advert);
            Advert requestedAdvert = programService.getById(requestedAdvertId);
            modelMap.addAttribute("title", requestedAdvert.getTitle());
            modelMap.addAttribute("description", requestedAdvert.getDescriptionForFacebook());
            modelMap.addAttribute("advertId", requestedAdvertId);
        }

        return TemplateLocation.REGISTRATION_FORM;
    }

    public User getPendingUser(final String activationCode) {
        if (StringUtils.isBlank(activationCode)) {
            return null;
        }

        User pendingUser = userService.getUserByActivationCode(activationCode);
        if (pendingUser == null) {
            throw new ResourceNotFoundException();
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
        StringBuffer requestUrl = request.getRequestURL();
        String requestQuery = request.getQueryString();
        DefaultSavedRequest defaultSavedRequest = getDefaultSavedRequest(request);
        if (defaultSavedRequest != null && StringUtils.contains(defaultSavedRequest.getRequestURL(), "/apply/new")
                || (StringUtils.contains(requestUrl.toString(), "register")) && StringUtils.contains(requestQuery, "advert")) {
            return true;
        }
        return false;
    }

}
