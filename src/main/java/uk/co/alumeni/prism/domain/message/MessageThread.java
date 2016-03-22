package uk.co.alumeni.prism.domain.message;

import static com.google.common.base.Objects.equal;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.co.alumeni.prism.domain.activity.ActivityEditable;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.user.UserAccount;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

@Entity
@Table(name = "message_thread")
public class MessageThread {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @OneToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

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

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
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

    public ActivityEditable getActivity() {
        return comment == null ? userAccount : comment.getResource();
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
        MessageThread other = (MessageThread) object;
        return equal(id, other.getId());
    }

}
