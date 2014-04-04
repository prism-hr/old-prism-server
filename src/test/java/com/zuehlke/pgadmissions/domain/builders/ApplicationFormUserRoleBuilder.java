package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

public class ApplicationFormUserRoleBuilder {

    private Integer id;

    private Boolean interestedInApplicant = false;

    private List<ApplicationFormActionRequired> actions = new ArrayList<ApplicationFormActionRequired>();
    
    private ApplicationForm applicationForm;

    private RegisteredUser user;

    private Role role;
    
    private Boolean raisesUpdateFlag = false;
    
    private Boolean raisesUrgentFlag = false;
    
    private Date updateTimestamp;

    public ApplicationFormUserRoleBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationFormUserRoleBuilder interestedInApplicant(Boolean interestedInApplicant) {
        this.interestedInApplicant = interestedInApplicant;
        return this;
    }

    public ApplicationFormUserRoleBuilder actions(ApplicationFormActionRequired... actions) {
        this.actions.addAll(Arrays.asList(actions));
        return this;
    }
    
    public ApplicationFormUserRoleBuilder actions(List<ApplicationFormActionRequired> actions) {
        this.actions.addAll(actions);
        return this;
    }

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

    public ApplicationFormUserRoleBuilder raisesUpdateFlag(Boolean raisesUpdateFlag) {
        this.raisesUpdateFlag = raisesUpdateFlag;
        return this;
    }
    
    public ApplicationFormUserRoleBuilder raisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
        return this;
    }
    
    public ApplicationFormUserRoleBuilder updateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
        return this;
    }
    
    public ApplicationFormUserRole build() {
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRole();
//        applicationFormUserRole.setId(id);
//        applicationFormUserRole.setInterestedInApplicant(interestedInApplicant);
//        applicationFormUserRole.getActions().addAll(actions);
//        applicationFormUserRole.setApplicationForm(applicationForm);
//        applicationFormUserRole.setUser(user);
//        applicationFormUserRole.setRole(role);
//        applicationFormUserRole.setRaisesUpdateFlag(raisesUpdateFlag);
//        applicationFormUserRole.setRaisesUrgentFlag(raisesUrgentFlag);
//        applicationFormUserRole.setUpdateTimestamp(updateTimestamp);
        return applicationFormUserRole;
    }

}
