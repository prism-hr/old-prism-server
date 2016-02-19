package uk.co.alumeni.prism.domain.message;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.MessageReassignmentProcessor;

import com.google.common.collect.Sets;

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
    private Set<MessageRecipient> recipients = Sets.newHashSet();

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

    public Set<MessageRecipient> getRecipients() {
        return recipients;
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

    public Message addRecipient(MessageRecipient recipient) {
        recipients.add(recipient);
        return this;
    }

    public Message addDocument(MessageDocument document) {
        documents.add(document);
        return this;
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
