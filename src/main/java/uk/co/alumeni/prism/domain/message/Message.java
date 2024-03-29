package uk.co.alumeni.prism.domain.message;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.MessageReassignmentProcessor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "message")
public class Message implements UserAssignment<MessageReassignmentProcessor>, UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "message_thread_id", nullable = false)
    private MessageThread thread;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @OneToMany(mappedBy = "message")
    private Set<MessageNotification> notifications = Sets.newHashSet();

    @OneToMany(mappedBy = "document")
    private Set<MessageDocument> documents = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MessageThread getThread() {
        return thread;
    }

    public void setThread(MessageThread thread) {
        this.thread = thread;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Set<MessageNotification> getNotifications() {
        return notifications;
    }

    public Set<MessageDocument> getDocuments() {
        return documents;
    }

    public Message withUser(User user) {
        this.user = user;
        return this;
    }

    public Message withThread(MessageThread thread) {
        this.thread = thread;
        return this;
    }

    public Message withContent(String content) {
        this.content = content;
        return this;
    }

    public Message withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Message addNotification(MessageNotification notification) {
        this.notifications.add(notification);
        return this;
    }

    public Message addDocument(MessageDocument document) {
        this.documents.add(document);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        Message other = (Message) object;
        return Objects.equal(id, other.getId());
    }

    @Override
    public Class<MessageReassignmentProcessor> getUserReassignmentProcessor() {
        return MessageReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("id", id);
    }

}
