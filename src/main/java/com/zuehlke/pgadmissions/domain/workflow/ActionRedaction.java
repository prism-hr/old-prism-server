package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;

import javax.persistence.*;

@Entity
@Table(name = "ACTION_REDACTION", uniqueConstraints = {@UniqueConstraint(columnNames = {"action_id", "role_id"})})
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
    private PrismActionRedactionType redactionType;

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

    public PrismActionRedactionType getRedactionType() {
        return redactionType;
    }

    public void setRedactionType(PrismActionRedactionType redactionType) {
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

    public ActionRedaction withRedactionType(PrismActionRedactionType redactionType) {
        this.redactionType = redactionType;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("action", action).addProperty("role", role);
    }

}
