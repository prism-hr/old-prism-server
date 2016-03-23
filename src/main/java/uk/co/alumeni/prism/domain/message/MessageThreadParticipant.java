package uk.co.alumeni.prism.domain.message;

import static com.google.common.base.Objects.equal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.MessageNotificationReassignmentProcessor;

import com.google.common.base.Objects;

@Entity
@Table(name = "message_thread_participant",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "message_thread_id", "user_id", "start_message_id", "close_message_id" }) })
public class MessageThreadParticipant implements UserAssignment<MessageNotificationReassignmentProcessor>, UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "message_thread_id", nullable = false)
    private MessageThread thread;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "start_message_id", nullable = false)
    private Message startMessage;

    @ManyToOne
    @JoinColumn(name = "close_message_id")
    private Message closeMessage;

    @ManyToOne
    @JoinColumn(name = "last_viewed_message_id")
    private Message lastViewedMessage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MessageThread getThread() {
        return thread;
    }

    public void setThread(MessageThread thread) {
        this.thread = thread;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getStartMessage() {
        return startMessage;
    }

    public void setStartMessage(Message startMessage) {
        this.startMessage = startMessage;
    }

    public Message getCloseMessage() {
        return closeMessage;
    }

    public void setCloseMessage(Message closeMessage) {
        this.closeMessage = closeMessage;
    }

    public Message getLastViewedMessage() {
        return lastViewedMessage;
    }

    public void setLastViewedMessage(Message lastViewedMessage) {
        this.lastViewedMessage = lastViewedMessage;
    }

    public MessageThreadParticipant withThread(MessageThread thread) {
        this.thread = thread;
        return this;
    }

    public MessageThreadParticipant withUser(User user) {
        this.user = user;
        return this;
    }

    public MessageThreadParticipant withStartMessage(Message startMessage) {
        this.startMessage = startMessage;
        return this;
    }

    public MessageThreadParticipant withLastViewedMessage(Message lastViewedMessage) {
        this.lastViewedMessage = lastViewedMessage;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(thread, user);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        MessageThreadParticipant other = (MessageThreadParticipant) object;
        return equal(thread, other.getThread()) && equal(user, other.getUser());
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
        return new EntitySignature().addProperty("thread", thread).addProperty("user", user);
    }

}
