package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;

import javax.persistence.*;

@Entity
@Table(name = "STATE_ACTION_ASSIGNMENT", uniqueConstraints = {@UniqueConstraint(columnNames = {"state_action_id", "role_id"})})
public class StateActionAssignment implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_action_id", nullable = false)
    private StateAction stateAction;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "action_enhancement")
    @Enumerated(EnumType.STRING)
    private PrismActionEnhancement actionEnhancement;

    @ManyToOne
    @JoinColumn(name = "delegated_action_id")
    private Action delegatedAction;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateAction getStateAction() {
        return stateAction;
    }

    public void setStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public void setActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
    }

    public Action getDelegatedAction() {
        return delegatedAction;
    }

    public void setDelegatedAction(Action delegatedAction) {
        this.delegatedAction = delegatedAction;
    }

    public StateActionAssignment withStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
        return this;
    }

    public StateActionAssignment withRole(Role role) {
        this.role = role;
        return this;
    }

    public StateActionAssignment withActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
        return this;
    }

    public StateActionAssignment withDelegatedAction(Action delegatedAction) {
        this.delegatedAction = delegatedAction;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("stateAction", stateAction).addProperty("role", role);
    }

}
