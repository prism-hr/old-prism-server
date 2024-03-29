package uk.co.alumeni.prism.domain.message;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.MessageNotificationReassignmentProcessor;

import javax.persistence.*;

import static com.google.common.base.Objects.equal;

@Entity
@Table(name = "message_notification", uniqueConstraints = {@UniqueConstraint(columnNames = {"message_id", "user_id"})})
public class MessageNotification implements UserAssignment<MessageNotificationReassignmentProcessor>, UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MessageNotification withMessage(Message message) {
        this.message = message;
        return this;
    }

    public MessageNotification withUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message, user);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        MessageNotification other = (MessageNotification) object;
        return equal(message, other.getMessage()) && equal(user, other.getUser());
    }

    @Override
    public Class<MessageNotificationReassignmentProcessor> getUserReassignmentProcessor() {
        return MessageNotificationReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("message", message).addProperty("user", user);
    }

}
