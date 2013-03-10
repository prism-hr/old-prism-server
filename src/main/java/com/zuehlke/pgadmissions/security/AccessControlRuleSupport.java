package com.zuehlke.pgadmissions.security;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

//TODO: This is work in progress (ked)

public interface AccessControlRuleSupport {

    boolean supports(final Class<?> clazz);
    
    boolean hasPermission(final Object object, final UserAction action, final RegisteredUser currentUser);
}
