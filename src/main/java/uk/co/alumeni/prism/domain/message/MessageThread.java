package uk.co.alumeni.prism.domain.message;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.domain.comment.Comment;

import com.google.common.collect.Sets;

@Entity
@Table(name = "message_thread")
public class MessageThread {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @OneToOne(mappedBy = "thread")
    private Comment comment;

    @OneToMany(mappedBy = "thread")
    private Set<Message> messages = Sets.newHashSet();

    @OneToMany(mappedBy = "thread")
    private Set<MessageThreadParticipant> participants = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public Set<MessageThreadParticipant> getParticipants() {
        return participants;
    }

    public MessageThread withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MessageThread addMessage(Message message) {
        this.messages.add(message);
        return this;
    }

    public MessageThread addParticipant(MessageThreadParticipant participant) {
        this.participants.add(participant);
        return this;
    }

}
