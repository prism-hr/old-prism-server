package uk.co.alumeni.prism.domain.workflow;

import uk.co.alumeni.prism.domain.UniqueEntity;

import javax.persistence.*;

@Entity
@Table(name = "state_action_recipient", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_assignment_id", "role_id", "external_mode" }) })
public class StateActionRecipient implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_action_assignment_id", nullable = false)
    private StateActionAssignment assignment;

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

    public StateActionAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(StateActionAssignment assignment) {
        this.assignment = assignment;
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

    public StateActionRecipient withAssignment(final StateActionAssignment assignment) {
        this.assignment = assignment;
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
        return new EntitySignature().addProperty("assignment", assignment).addProperty("role", role)
                .addProperty("externalMode", externalMode);
    }

}
