package com.zuehlke.pgadmissions.security;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

//TODO: This is work in progress (ked)

public class ProgramACR extends AbstractAccessControlRule {

    public static final String ACTION_IS_APPROVER = "IS_APPROVER";
    
    public ProgramACR() {
    }
    
    @Override
    public boolean supports(final Class<?> clazz) {
        return Program.class.equals(clazz);
    }

    @Override
    public boolean hasPermission(final Object object, final String action, final RegisteredUser user) {
        Program program = (Program) object;
        if (StringUtils.equalsIgnoreCase(ACTION_IS_APPROVER, action)) {
            return isApprover(program, user);
        }
        return false;
    }
    
    public boolean isApprover(final Program program, final RegisteredUser user) {
        return checkUserRole(user, Authority.APPROVER, program.getApprovers());
    }

    private boolean checkUserRole(final RegisteredUser user, final Authority authority, final List<RegisteredUser> lookup) {
        if (isNotInRole(user, authority)) {
            return false;
        }
        for (RegisteredUser lookupUser : lookup) {
            if (areEqual(lookupUser, user)) {
                return true;
            }
        }
        return false;
    }
}
