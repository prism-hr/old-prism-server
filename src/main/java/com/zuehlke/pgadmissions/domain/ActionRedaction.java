package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRedactionType;

@Entity
@Table(name = "ACTION_REDACTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "action_id", "role_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActionRedaction implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "redaction_type", nullable = false)
    private PrismRedactionType redactionType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PrismRedactionType getRedactionType() {
        return redactionType;
    }

    public void setRedactionType(PrismRedactionType redactionType) {
        this.redactionType = redactionType;
    }

    public ActionRedaction withAction(Action action) {
        this.action = action;
        return this;
    }
    
    public ActionRedaction withRole(Role role) {
        this.role = role;
        return this;
    }
    
    public ActionRedaction withRedactionType(PrismRedactionType redactionType) {
        this.redactionType = redactionType;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("action", action).addProperty("role", role);
    }

}
