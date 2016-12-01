package uk.co.alumeni.prism.domain.workflow;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationType;

import javax.persistence.*;

@Entity
@Table(name = "notification_definition")
public class NotificationDefinition extends WorkflowDefinition {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismNotificationDefinition id;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismNotificationType notificationType;

    @Column(name = "notification_purpose", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismNotificationPurpose notificationPurpose;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @Override
    public PrismNotificationDefinition getId() {
        return id;
    }

    public void setId(PrismNotificationDefinition id) {
        this.id = id;
    }

    public PrismNotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(PrismNotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public PrismNotificationPurpose getNotificationPurpose() {
        return notificationPurpose;
    }

    public void setNotificationPurpose(PrismNotificationPurpose notificationPurpose) {
        this.notificationPurpose = notificationPurpose;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public NotificationDefinition withId(PrismNotificationDefinition id) {
        this.id = id;
        return this;
    }

    public NotificationDefinition withNotificationType(PrismNotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public NotificationDefinition withNotificationPurpose(PrismNotificationPurpose notificationPurpose) {
        this.notificationPurpose = notificationPurpose;
        return this;
    }

    public NotificationDefinition withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
