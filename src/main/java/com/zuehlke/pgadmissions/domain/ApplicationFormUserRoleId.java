package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class ApplicationFormUserRoleId implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "application_form_id")
    private ApplicationForm applicationForm;

    @ManyToOne
    @JoinColumn(name = "registered_user_id")
    private RegisteredUser user;

    @ManyToOne
    @JoinColumn(name = "application_role_id")
    private Role role;

    public ApplicationFormUserRoleId() {
    }

    public ApplicationFormUserRoleId(ApplicationForm applicationForm, RegisteredUser user, Role role) {
        this.applicationForm = applicationForm;
        this.user = user;
        this.role = role;
    }

    public ApplicationForm getApplicationForm() {
        return applicationForm;
    }

    public RegisteredUser getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }

}
