package com.zuehlke.pgadmissions.security;

import java.util.List;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

//TODO: This is work in progress (ked)

@Component
public class ProgramACR extends AbstractAccessControlRule {

    public ProgramACR() {
    }
    
    @Override
    public boolean supports(final Class<?> clazz) {
        return Program.class.equals(clazz);
    }

    @Override
    public boolean hasPermission(final Object object, final UserAction action, final RegisteredUser user) {
        Program program = (Program) object;
        switch(action) {
        case IS_APPROVER:
            return isApprover(program, user);
        default:
            return false;
        }
    }
    
    public boolean isApprover(final Program program, final RegisteredUser user) {
        return checkUserRole(user, Authority.APPROVER, program.getApprovers());
    }

    private boolean checkUserRole(final RegisteredUser user, final Authority authority, final List<RegisteredUser> lookup) {
        if (isNotInRole(authority, user)) {
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
