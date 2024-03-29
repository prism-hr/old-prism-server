package uk.co.alumeni.prism.domain.user;

import com.google.common.collect.Sets;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.advert.AdvertTargetPending;
import uk.co.alumeni.prism.domain.application.*;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageNotification;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.*;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.workflow.StateActionPending;
import uk.co.alumeni.prism.rest.UserDescriptorExtended;
import uk.co.alumeni.prism.workflow.user.UserReassignmentProcessor;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "user")
public class User extends UserDescriptorExtended<Document, User> implements UserDetails, UniqueEntity, UserAssignment<UserReassignmentProcessor> {

    private static final long serialVersionUID = 5910410212695389060L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "first_name_2")
    private String firstName2;

    @Column(name = "first_name_3")
    private String firstName3;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Lob
    @Column(name = "email_bounced_message")
    private String emailBouncedMessage;

    @Column(name = "activation_code", unique = true)
    private String activationCode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name = "creator_user_id")
    private User creatorUser;

    @ManyToOne
    @JoinColumn(name = "parent_user_id")
    private User parentUser;

    @Column(name = "last_logged_in_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastLoggedInTimestamp;

    @OrderBy(clause = "last_name asc, first_name asc")
    @OneToMany(mappedBy = "parentUser")
    private Set<User> childUsers = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Application> applications = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Project> projects = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Program> programs = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Department> departments = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Institution> institutions = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<uk.co.alumeni.prism.domain.resource.System> systems = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Advert> adverts = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<ApplicationQualification> applicationQualifications = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<ApplicationEmploymentPosition> applicationEmploymentPositions = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<ApplicationReferee> applicationReferees = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<CommentAssignedUser> commentAssignedUsers = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<ApplicationHiringManager> applicationHiringManagers = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Document> documents = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<UserNotification> userNotifications = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<UserCompetence> userCompetences = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<UserFeedback> userFeedbacks = Sets.newHashSet();

    @OneToMany(mappedBy = "advertUser")
    private Set<AdvertTarget> advertTargets = Sets.newHashSet();

    @OneToMany(mappedBy = "targetAdvertUser")
    private Set<AdvertTarget> advertTargetsTarget = Sets.newHashSet();

    @OneToMany(mappedBy = "acceptAdvertUser")
    private Set<AdvertTarget> advertTargetsAccept = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<UserQualification> userQualifications = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<UserEmploymentPosition> userEmploymentPositions = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<UserReferee> userReferees = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Invitation> invitations = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<StateActionPending> stateActionPendings = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<AdvertTargetPending> advertTargetPendings = Sets.newHashSet();

    @OneToMany(mappedBy = "searchUser")
    private Set<MessageThread> threads = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<Message> messages = Sets.newHashSet();

    @OneToMany(mappedBy = "user")
    private Set<MessageNotification> messageNotifications = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public void setFirstName3(String firstName3) {
        this.firstName3 = firstName3;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailBouncedMessage() {
        return emailBouncedMessage;
    }

    public void setEmailBouncedMessage(String emailBouncedMessage) {
        this.emailBouncedMessage = emailBouncedMessage;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public User getCreatorUser() {
        return creatorUser;
    }

    @Override
    public void setCreatorUser(User creatorUser) {
        this.creatorUser = creatorUser;
    }

    public User getParentUser() {
        return parentUser;
    }

    public void setParentUser(User parentUser) {
        this.parentUser = parentUser;
    }

    public DateTime getLastLoggedInTimestamp() {
        return lastLoggedInTimestamp;
    }

    public void setLastLoggedInTimestamp(DateTime lastLoggedInTimestamp) {
        this.lastLoggedInTimestamp = lastLoggedInTimestamp;
    }

    public Set<User> getChildUsers() {
        return childUsers;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Program> getPrograms() {
        return programs;
    }

    public Set<Department> getDepartments() {
        return departments;
    }

    public Set<Institution> getInstitutions() {
        return institutions;
    }

    public Set<System> getSystems() {
        return systems;
    }

    public Set<Advert> getAdverts() {
        return adverts;
    }

    public Set<ApplicationQualification> getApplicationQualifications() {
        return applicationQualifications;
    }

    public Set<ApplicationEmploymentPosition> getApplicationEmploymentPositions() {
        return applicationEmploymentPositions;
    }

    public Set<ApplicationReferee> getApplicationReferees() {
        return applicationReferees;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Set<CommentAssignedUser> getCommentAssignedUsers() {
        return commentAssignedUsers;
    }

    public Set<ApplicationHiringManager> getApplicationHiringManagers() {
        return applicationHiringManagers;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public Set<UserNotification> getUserNotifications() {
        return userNotifications;
    }

    public Set<UserCompetence> getUserCompetences() {
        return userCompetences;
    }

    public Set<UserFeedback> getUserFeedbacks() {
        return userFeedbacks;
    }

    public Set<AdvertTarget> getAdvertTargets() {
        return advertTargets;
    }

    public Set<AdvertTarget> getAdvertTargetsTarget() {
        return advertTargetsTarget;
    }

    public Set<AdvertTarget> getAdvertTargetsAccept() {
        return advertTargetsAccept;
    }

    public Set<UserQualification> getUserQualifications() {
        return userQualifications;
    }

    public Set<UserEmploymentPosition> getUserEmploymentPositions() {
        return userEmploymentPositions;
    }

    public Set<UserReferee> getUserReferees() {
        return userReferees;
    }

    public Set<Invitation> getInvitations() {
        return invitations;
    }

    public Set<StateActionPending> getStateActionPendings() {
        return stateActionPendings;
    }

    public Set<AdvertTargetPending> getAdvertTargetPendings() {
        return advertTargetPendings;
    }

    public Set<MessageThread> getThreads() {
        return threads;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public Set<MessageNotification> getMessageNotifications() {
        return messageNotifications;
    }

    public User withId(Integer id) {
        this.id = id;
        return this;
    }

    public User withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public User withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public User withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public User withCreatorUser(User creatorUser) {
        this.creatorUser = creatorUser;
        return this;
    }

    public String getRobotRepresentation() {
        return fullName + ": " + email.replace("@", "-").replace(".", "-");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String getPassword() {
        return userAccount != null ? userAccount.getPassword() : null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Boolean getEnabled() {
        return userAccount == null ? null : userAccount.getEnabled();
    }

    @Override
    public void setEnabled(Boolean enabled) {
        if (userAccount != null) {
            userAccount.setEnabled(enabled);
        }

    }

    @Override
    public String getLinkedinProfileUrl() {
        return userAccount == null ? null : userAccount.getLinkedinProfileUrl();
    }

    @Override
    public void setLinkedinProfileUrl(String linkedinProfileUrl) {
        if (userAccount != null) {
            userAccount.setLinkedinProfileUrl(linkedinProfileUrl);
        }
    }

    @Override
    public String getLinkedinImageUrl() {
        return userAccount == null ? null : userAccount.getLinkedinImageUrl();
    }

    @Override
    public void setLinkedinImageUrl(String linkedinImageUrl) {
        if (userAccount != null) {
            userAccount.setLinkedinImageUrl(linkedinImageUrl);
        }
    }

    @Override
    public Document getPortraitImage() {
        return userAccount == null ? null : userAccount.getPortraitImage();
    }

    @Override
    public void setPortraitImage(Document portraitImage) {
        if (userAccount != null) {
            userAccount.setPortraitImage(portraitImage);
        }
    }

    @Override
    public Class<UserReassignmentProcessor> getUserReassignmentProcessor() {
        return UserReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("email", email);
    }

}
