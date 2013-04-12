package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.SwitchAndLinkUserAccountDTO;
import com.zuehlke.pgadmissions.exceptions.LinkAccountsException;
import com.zuehlke.pgadmissions.services.SwitchUserService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AccountValidator;
import com.zuehlke.pgadmissions.validators.SwitchAndLinkUserAccountDTOValidator;

@Controller
@RequestMapping("/myAccount")
public class AccountController {

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private static final String ACCOUNT_SECTION = "/private/my_account_section";

    private static final String ACCOUNT_PAGE_VIEW_NAME = "/private/my_account";

    private final UserService userService;

    private final AccountValidator accountValidator;

    private final SwitchUserService switchUserService;

    private final SwitchAndLinkUserAccountDTOValidator switchAndLinkAccountDTOValidator;

    public AccountController() {
        this(null, null, null, null);
    }

    @Autowired
    public AccountController(UserService userService,
            AccountValidator accountValidator,
            SwitchAndLinkUserAccountDTOValidator validator,
            SwitchUserService switchUserService) {
        this.userService = userService;
        this.accountValidator = accountValidator;
        this.switchAndLinkAccountDTOValidator = validator;
        this.switchUserService = switchUserService;
    }

    @InitBinder(value = "updatedUser")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(accountValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getMyAccountPage() {
        return ACCOUNT_PAGE_VIEW_NAME;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String saveAccountDetails(@Valid @ModelAttribute("updatedUser") RegisteredUser user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ACCOUNT_SECTION;
        }
        userService.updateCurrentUser(user);
        return "/private/common/ajax_OK";
    }

    @ModelAttribute(value = "updatedUser")
    public RegisteredUser getUpdatedUser() {
        RegisteredUser registeredUser = new RegisteredUser();
        RegisteredUser currentUser = getUser();
        registeredUser.setFirstName(currentUser.getFirstName());
        registeredUser.setFirstName2(currentUser.getFirstName2());
        registeredUser.setFirstName3(currentUser.getFirstName3());
        registeredUser.setLastName(currentUser.getLastName());
        registeredUser.setEmail(currentUser.getEmail());
        registeredUser.setPassword(currentUser.getPassword());
        return registeredUser;
    }

    @ModelAttribute(value = "user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/section")
    public String getMyAccountSection() {
        return ACCOUNT_SECTION;
    }

    // Link Accounts

    @ModelAttribute("switchAndLinkUserAccountDTO")
    public SwitchAndLinkUserAccountDTO getSwitchAndLinkUserAccountDTO() {
        return new SwitchAndLinkUserAccountDTO();
    }

    @InitBinder(value = "switchAndLinkUserAccountDTO")
    public void registerSwitchValidator(WebDataBinder binder) {
        binder.setValidator(switchAndLinkAccountDTOValidator);
    }

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public String linkAccounts(@Valid @ModelAttribute("switchAndLinkUserAccountDTO") SwitchAndLinkUserAccountDTO userDTO, BindingResult result,
            ModelMap modelMap) {
        if (result.hasErrors()) {
            return ACCOUNT_SECTION;
        }

        try {
            userService.linkAccounts(userDTO.getEmail());
        } catch (LinkAccountsException e) {
            result.rejectValue("email", "account.not.enabled");
            return ACCOUNT_SECTION;
        }

        return "/private/common/ajax_OK";
    }

    @RequestMapping(value = "/switch", method = RequestMethod.POST)
    @ResponseBody
    public String switchAccounts(@RequestParam String email, HttpServletRequest request) {
        try {
            RegisteredUser desiredAccount = userService.getUserByEmail(email);
            RegisteredUser currentAccount = userService.getCurrentUser();
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(currentAccount, desiredAccount);
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = switchUserService.authenticate(token);
            logger.info(String.format("User [%s] is switching to [%s]", currentAccount.getEmail(), desiredAccount.getEmail()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "OK";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "NOK";
    }

    @RequestMapping(value = "/deleteLinkedAccount", method = RequestMethod.POST)
    @ResponseBody
    public String deleteLinkedAccount(@RequestParam String email) {
        try {
            userService.deleteLinkedAccount(email);
            return "OK";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "NOK";
    }
}
