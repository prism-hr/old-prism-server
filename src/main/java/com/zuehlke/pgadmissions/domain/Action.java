package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.SystemAction;

@Entity
@Table(name = "ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Action {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private SystemAction id;
    
    @Column(name = "bypass_permission_check", nullable = false)
    private boolean bypassPermissionCheck;

    public SystemAction getId() {
        return id;
    }

    public void setId(SystemAction id) {
        this.id = id;
    }

    public boolean isBypassPermissionCheck() {
        return bypassPermissionCheck;
    }

    public void setBypassPermissionCheck(boolean bypassPermissionCheck) {
        this.bypassPermissionCheck = bypassPermissionCheck;
    }

    public Action withId(SystemAction id) {
        this.id = id;
        return this;
    }
    
    public Action withBypassAdmissionCheck(boolean bypassPermissionCheck) {
        this.bypassPermissionCheck = bypassPermissionCheck;
        return this;
    }

}
