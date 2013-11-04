package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRoleId;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

public class ApplicationFormUserRoleBuilder {

    private ApplicationForm applicationForm;

    private RegisteredUser user;

    private Role role;

    private Boolean currentRole;

    public ApplicationFormUserRoleBuilder applicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
        return this;
    }

    public ApplicationFormUserRoleBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public ApplicationFormUserRoleBuilder role(Role role) {
        this.role = role;
        return this;
    }

    public ApplicationFormUserRoleBuilder currentRole(Boolean currentRole) {
        this.currentRole = currentRole;
        return this;
    }

    public ApplicationFormUserRole build() {
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRole();
        ApplicationFormUserRoleId id = new ApplicationFormUserRoleId(applicationForm, user, role);
        applicationFormUserRole.setId(id);
        applicationFormUserRole.setCurrentRole(currentRole);
        return applicationFormUserRole;
    }

}
