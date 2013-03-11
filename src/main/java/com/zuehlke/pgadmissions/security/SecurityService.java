package com.zuehlke.pgadmissions.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.UserService;

//TODO: This is work in progress (ked)

@Service
public class SecurityService {

    private Logger log = LoggerFactory.getLogger(SecurityService.class);
    
    private final UserService userService;
    
    private final List<AccessControlRuleSupport> accessControlRules;;
    
    public SecurityService() {
        this(null, null);
    }
    
    @Autowired
    public SecurityService(final UserService userService, final List<AccessControlRuleSupport> accessControlRules) {
        this.userService = userService;
        this.accessControlRules = accessControlRules;
    }
    
    public boolean hasPermission(final Object object, final String action) {
        return hasPermission(object, UserAction.valueOf(action));
    }
    
    public boolean hasPermission(final Object object, final UserAction action) {
        try {
            for (AccessControlRuleSupport acr : accessControlRules) {
                if (acr.supports(object.getClass())) {
                    return acr.hasPermission(object, action, userService.getCurrentUser());
                }
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
