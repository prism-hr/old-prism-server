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

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.activity.ActivityEditable;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.MessageThreadReassignmentProcessor;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

@Entity
@Table(name = "message_thread")
public class MessageThread implements UserAssignment<MessageThreadReassignmentProcessor>, UniqueEntity {

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

    @ManyToOne
    @JoinColumn(name = "search_user_id", nullable = false)
    private User searchUser;

    @ManyToOne
    @JoinColumn(name = "search_advert_id")
    private Advert searchAdvert;

    @Column(name = "search_resource_code")
    private String searchResourceCode;

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

    public User getSearchUser() {
        return searchUser;
    }

    public void setSearchUser(User searchUser) {
        this.searchUser = searchUser;
    }

    public Advert getSearchAdvert() {
        return searchAdvert;
    }

    public void setSearchAdvert(Advert searchAdvert) {
        this.searchAdvert = searchAdvert;
    }

    public String getSearchResourceCode() {
        return searchResourceCode;
    }

    public void setSearchResourceCode(String searchResourceCode) {
        this.searchResourceCode = searchResourceCode;
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

    public MessageThread withComment(Comment comment) {
        this.comment = comment;
        return this;
    }

    public MessageThread withSearchUser(User searchUser) {
        this.searchUser = searchUser;
        return this;
    }

    public MessageThread withSearchAdvert(Advert searchAdvert) {
        this.searchAdvert = searchAdvert;
        return this;
    }

    public MessageThread withSearchResourceCode(String searchResourceCode) {
        this.searchResourceCode = searchResourceCode;
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
    public Class<MessageThreadReassignmentProcessor> getUserReassignmentProcessor() {
        return MessageThreadReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
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
    
    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("id", id);
    }

}
