package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.zuehlke.pgadmissions.security.PgAdmissionSwitchUserAuthenticationProvider;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AccountValidator;
import com.zuehlke.pgadmissions.validators.SwitchAndLinkUserAccountDTOValidator;

@Controller
@RequestMapping("/myAccount")
public class AccountController {

    private final Logger logger = Logger.getLogger(AccountController.class);
    
	private static final String ACCOUNT_SECTION = "/private/my_account_section";

	private static final String ACCOUNT_PAGE_VIEW_NAME = "/private/my_account";

	private final UserService userService;
	
	private final AccountValidator accountValidator;

	private final PgAdmissionSwitchUserAuthenticationProvider authenticationProvider;
	    
	private final SwitchAndLinkUserAccountDTOValidator switchAndLinkAccountDTOValidator;

	AccountController() {
		this(null, null, null, null);
	}

	@Autowired
	public AccountController(UserService userService, AccountValidator accountValidator, 
	        SwitchAndLinkUserAccountDTOValidator validator, 
	        PgAdmissionSwitchUserAuthenticationProvider authenticationProvider) {
		this.userService = userService;
		this.accountValidator = accountValidator;
        this.switchAndLinkAccountDTOValidator = validator;
        this.authenticationProvider = authenticationProvider;
	}
	
	@InitBinder(value="updatedUser")
	public void registerValidator(WebDataBinder binder) {
		binder.setValidator(accountValidator);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getMyAccountPage() {
		return ACCOUNT_PAGE_VIEW_NAME;
	}
	
	@RequestMapping(value="/submit", method = RequestMethod.POST)
	public String saveAccountDetails(@Valid @ModelAttribute("updatedUser") RegisteredUser user, BindingResult bindingResult) {
		if(bindingResult.hasErrors()){
			return ACCOUNT_SECTION;
		}
		userService.updateCurrentUser(user);
		return "/private/common/ajax_OK";
	}

	@ModelAttribute(value="updatedUser")
	public RegisteredUser getUpdatedUser() {
		RegisteredUser registeredUser = new RegisteredUser();		
		RegisteredUser currentUser = getUser();
		registeredUser.setFirstName(currentUser.getFirstName());
		registeredUser.setLastName(currentUser.getLastName());
		registeredUser.setEmail(currentUser.getEmail());
		registeredUser.setPassword(currentUser.getPassword());
		return registeredUser;
	}
	
	@ModelAttribute(value="user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@RequestMapping(method = RequestMethod.GET, value="/section")
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
    public String linkAccounts(@Valid @ModelAttribute("switchAndLinkUserAccountDTO") SwitchAndLinkUserAccountDTO userDTO, BindingResult result, ModelMap modelMap) {
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
            Authentication authentication = authenticationProvider.authenticate(token);
            logger.info(String.format("User [%s] is switching to [%s]", currentAccount.getEmail(), desiredAccount.getEmail()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "OK";
        } catch (Exception e) {
            logger.error(e);
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
            logger.error(e);
        }
        return "NOK";
    }
}
