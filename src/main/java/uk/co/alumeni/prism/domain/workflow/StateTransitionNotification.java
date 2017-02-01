package uk.co.alumeni.prism.domain.workflow;

import uk.co.alumeni.prism.domain.UniqueEntity;

import javax.persistence.*;

@Entity
@Table(name = "state_transition_notification", uniqueConstraints = {@UniqueConstraint(columnNames = {"state_transition_id", "role_id"})})
public class StateTransitionNotification implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_transition_id", nullable = false)
    private StateTransition stateTransition;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "notification_definition_id", nullable = false)
    private NotificationDefinition notificationDefinition;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateTransition getStateTransition() {
        return stateTransition;
    }

    public void setStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public NotificationDefinition getNotificationDefinition() {
        return notificationDefinition;
    }

    public void setNotificationDefinition(NotificationDefinition notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
    }

    public StateTransitionNotification withStateAction(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
        return this;
    }

    public StateTransitionNotification withRole(Role role) {
        this.role = role;
        return this;
    }

    public StateTransitionNotification withNotificationDefinition(NotificationDefinition notificationTemplate) {
        this.notificationDefinition = notificationTemplate;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("stateTransition", stateTransition).addProperty("role", role);
    }

}
