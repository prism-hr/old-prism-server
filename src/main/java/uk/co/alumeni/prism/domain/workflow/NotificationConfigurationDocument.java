package uk.co.alumeni.prism.domain.workflow;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.document.Document;

import javax.persistence.*;

@Entity
@Table(name = "notification_configuration_document", uniqueConstraints = { @UniqueConstraint(columnNames = { "notification_configuration_id", "document_id" }) })
public class NotificationConfigurationDocument implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "notification_configuration_id", nullable = false)
    private NotificationConfiguration notificationConfiguration;

    @OneToOne
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NotificationConfiguration getNotificationConfiguration() {
        return notificationConfiguration;
    }

    public void setNotificationConfiguration(NotificationConfiguration notificationConfiguration) {
        this.notificationConfiguration = notificationConfiguration;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public NotificationConfigurationDocument withNotificationConfiguration(NotificationConfiguration notificationConfiguration) {
        this.notificationConfiguration = notificationConfiguration;
        return this;
    }

    public NotificationConfigurationDocument withDocument(Document document) {
        this.document = document;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("notificationConfiguration", notificationConfiguration).addProperty("document", document);
    }

}
