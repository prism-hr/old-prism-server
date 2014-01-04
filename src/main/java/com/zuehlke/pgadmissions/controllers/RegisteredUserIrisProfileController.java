package com.zuehlke.pgadmissions.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UclIrisProfileService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/users")
public class RegisteredUserIrisProfileController {

    private final UclIrisProfileService irisService;

    private final UserService userService;
    
    private final MessageSource messageSource;
    
    public RegisteredUserIrisProfileController() {
        this(null, null, null);
    }
    
    @Autowired
    public RegisteredUserIrisProfileController(final UclIrisProfileService irisService, final UserService userService, 
    		final MessageSource messageSource) {
        this.irisService = irisService;
        this.userService = userService;
        this.messageSource = messageSource;
    }
    
    private boolean isNotValidUpi(final String upi) {
        return !StringUtils.isAlphanumeric(upi) || StringUtils.length(upi) != 7;
    }
    
    @RequestMapping(value = "/IRIS/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, Object> getUpiForCurrentUser() {
        return Collections.<String, Object>singletonMap("upi", StringUtils.trimToEmpty(userService.getCurrentUser().getUpi()));
    }
    
    @RequestMapping(value = "/IRIS/{upi}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, Object> irisProfileExists(final @PathVariable String upi) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        if (isNotValidUpi(upi)) {
            result.put("success", false);
            result.put("irisProfile", messageSource.getMessage("account.iris.upi.invalid", null, null));
            return result;
        }
        
        if (!irisService.profileExists(upi)) {
            result.put("success", false);
            result.put("irisProfile", messageSource.getMessage("account.iris.notexists", null, null));
            return result;
        }
        
        return Collections.<String, Object>singletonMap("success", true);
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/IRIS/", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, Object> setIrisProfileForCurrentUser(final @RequestParam String upi) {
        Map<String, Object> result = new HashMap<String, Object>();
        RegisteredUser currentUser = userService.getCurrentUser();
        
        if (isNotValidUpi(upi)) {
            result.put("success", false);
            result.put("irisProfile", messageSource.getMessage("account.iris.upi.invalid", null, null));
            return result;
        }
        
        if (!irisService.profileExists(upi)) {
            result.put("success", false);
            result.put("irisProfile", messageSource.getMessage("account.iris.notexists", null, null));
            return result;
        }
        
        List<RegisteredUser> usersWithUpi = userService.getUsersWithUpi(upi);
        List<RegisteredUser> linkedAccounts = currentUser.getAllLinkedAccounts();
        List<RegisteredUser> intersection = ListUtils.subtract(usersWithUpi, linkedAccounts);
        
        if (intersection.isEmpty()) {
            currentUser.setUpi(upi);
            userService.save(currentUser);
            for (RegisteredUser linkedAccount : linkedAccounts) {
                linkedAccount.setUpi(upi);
                userService.save(linkedAccount);
            }
            return Collections.<String, Object>singletonMap("success", true);            
        } else {
            result.put("success", false);
            result.put("irisProfile", messageSource.getMessage("account.iris.upi.registered", null, null));
            return result;
        }
    }
    
    @RequestMapping(value = "/IRIS/", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public void unlinkIrisProfileForCurrentUser() {
        RegisteredUser currentUser = userService.getCurrentUser();
        List<RegisteredUser> linkedAccounts = currentUser.getAllLinkedAccounts();
        for(RegisteredUser account : linkedAccounts){
            account.setUpi(null);
            userService.save(account);
        }
        
    }
    
}