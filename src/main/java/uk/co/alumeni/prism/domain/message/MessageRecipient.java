package uk.co.alumeni.prism.domain.message;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.workflow.user.MessageRecipientReassignmentProcessor;

@Entity
@Table(name = "message_recipient")
public class MessageRecipient implements UserAssignment<MessageRecipientReassignmentProcessor>, UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "send_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime sendTimestamp;

    @Column(name = "view_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime viewTimestamp;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public DateTime getSendTimestamp() {
        return sendTimestamp;
    }

    public void setSendTimestamp(DateTime sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    public DateTime getViewTimestamp() {
        return viewTimestamp;
    }

    public void setViewTimestamp(DateTime viewTimestamp) {
        this.viewTimestamp = viewTimestamp;
    }

    public MessageRecipient withMessage(Message message) {
        this.message = message;
        return this;
    }

    public MessageRecipient withUser(User user) {
        this.user = user;
        return this;
    }

    public MessageRecipient withRole(Role role) {
        this.role = role;
        return this;
    }

    public MessageRecipient withSendTimestamp(DateTime sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
        return this;
    }

    public MessageRecipient withViewTimestamp(DateTime viewTimestamp) {
        this.viewTimestamp = viewTimestamp;
        return this;
    }

    @Override
    public Class<MessageRecipientReassignmentProcessor> getUserReassignmentProcessor() {
        return MessageRecipientReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("message", message).addProperty("user", user).addProperty("role", role);
    }

}
