package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "APPLICATION_FORM_USER_ROLE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationFormUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ApplicationFormUserRoleId id;

    @Column(name = "is_current_role")
    private Boolean currentRole;

    public ApplicationFormUserRoleId getId() {
        return id;
    }

    public void setId(ApplicationFormUserRoleId id) {
        this.id = id;
    }

    public Boolean isCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(Boolean currentRole) {
        this.currentRole = currentRole;
    }

}
