package com.zuehlke.pgadmissions.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientPrivilegesException;
import com.zuehlke.pgadmissions.services.UclIrisProfileService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/users")
public class RegisteredUserUpiController {

    private final UclIrisProfileService irisService;

    private final UserService userService;
    
    private final MessageSource messageSource;
    
    @Autowired
    public RegisteredUserUpiController(final UclIrisProfileService irisService, final UserService userService,
            final MessageSource messageSource) {
        this.irisService = irisService;
        this.userService = userService;
        this.messageSource = messageSource;
    }
    
    public RegisteredUserUpiController() {
        this(null, null, null);
    }
    
    @RequestMapping(value = "/IRIS/{upi}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, Object> upiExists(final @PathVariable String upi) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        if (!StringUtils.isAlphanumeric(upi)) {
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
    
    @RequestMapping(value = "/{userid}/IRIS/{upi}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, Object> getUpi(final @PathVariable String userid, final @PathVariable String upi) {
        Map<String, Object> result = new HashMap<String, Object>();
        RegisteredUser currentUser = userService.getCurrentUser();
        RegisteredUser userToUpdate = userService.getUser(Integer.valueOf(userid));
        
        if (!currentUser.getId().equals(userToUpdate.getId())) {
            throw new InsufficientPrivilegesException();
        }
        
        if (!StringUtils.isAlphanumeric(upi)) {
            result.put("success", false);
            result.put("irisProfile", messageSource.getMessage("account.iris.upi.invalid", null, null));
            return result;
        }
        
        if (!irisService.profileExists(upi)) {
            result.put("success", false);
            result.put("irisProfile", messageSource.getMessage("account.iris.notexists", null, null));
            return result;
        }
        
        userToUpdate.setUpi(upi);
        userService.save(userToUpdate);
        return Collections.<String, Object>singletonMap("success", true);
    }
}
