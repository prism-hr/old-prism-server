package com.zuehlke.pgadmissions.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.UserService;

//TODO: This is work in progress (ked)

@Service
public class SecurityService {

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
        for (AccessControlRuleSupport acr : accessControlRules) {
            if (acr.supports(object.getClass())) {
                return acr.hasPermission(object, action, userService.getCurrentUser());
            }
        }
        return false;
    }
}
