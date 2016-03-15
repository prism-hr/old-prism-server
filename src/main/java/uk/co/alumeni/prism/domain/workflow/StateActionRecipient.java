package uk.co.alumeni.prism.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import uk.co.alumeni.prism.domain.UniqueEntity;

@Entity
@Table(name = "state_action_recipient", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_assignment_id", "role_id", "external_mode" }) })
public class StateActionRecipient implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_action_assignment_id", nullable = false)
    private StateActionAssignment stateActionAssignment;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "external_mode", nullable = false)
    private Boolean externalMode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateActionAssignment getStateActionAssignment() {
        return stateActionAssignment;
    }

    public void setStateActionAssignment(StateActionAssignment stateActionAssignment) {
        this.stateActionAssignment = stateActionAssignment;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public void setExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
    }

    public StateActionRecipient withStateActionAssignment(StateActionAssignment stateActionAssignment) {
        this.stateActionAssignment = stateActionAssignment;
        return this;
    }

    public StateActionRecipient withRole(Role role) {
        this.role = role;
        return this;
    }

    public StateActionRecipient withExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("stateActionAssignment", stateActionAssignment).addProperty("role", role)
                .addProperty("externalMode", externalMode);
    }

}
