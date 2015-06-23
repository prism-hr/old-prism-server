package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;

import javax.persistence.*;

@Entity
@Table(name = "state_action_assignment", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_id", "role_id", "partner_mode" }) })
public class StateActionAssignment implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_action_id", nullable = false)
    private StateAction stateAction;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "partner_mode", nullable = false)
    private Boolean partnerMode;

    @Column(name = "action_enhancement")
    @Enumerated(EnumType.STRING)
    private PrismActionEnhancement actionEnhancement;

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

    public Boolean getPartnerMode() {
        return partnerMode;
    }

    public void setPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public void setActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
    }

    public StateActionAssignment withStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
        return this;
    }

    public StateActionAssignment withRole(Role role) {
        this.role = role;
        return this;
    }

    public StateActionAssignment withPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
        return this;
    }

    public StateActionAssignment withActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("stateAction", stateAction).addProperty("role", role).addProperty("partnerMode", partnerMode);
    }

}
