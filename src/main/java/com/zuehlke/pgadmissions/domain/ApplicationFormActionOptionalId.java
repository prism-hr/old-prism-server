package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Embeddable
public class ApplicationFormActionOptionalId implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "application_role_id")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_id")
    private ApplicationFormStatus status;

    @Column(name = "action_id")
    private String action;

    public ApplicationFormActionOptionalId() {
    }

    public ApplicationFormActionOptionalId(Role role, ApplicationFormStatus status, String action) {
        this.role = role;
        this.status = status;
        this.action = action;
    }

    public Role getRole() {
        return role;
    }

    public ApplicationFormStatus getStatus() {
        return status;
    }

    public String getAction() {
        return action;
    }

}