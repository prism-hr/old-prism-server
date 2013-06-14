package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.springframework.security.core.userdetails.UserDetails;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "REGISTERED_USER")
@Indexed
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RegisteredUser extends Authorisable implements UserDetails, Comparable<RegisteredUser>, Serializable {

    private static final long serialVersionUID = 7913035836949510857L;

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName2;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String firstName3;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String lastName;

    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String email;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String username;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String password;

    @Transient
    private String newPassword;

    @Transient
    private String confirmPassword;

    @Transient
    private Referee currentReferee;

    @Column(name = "original_querystring")
    private String originalApplicationQueryString;

    @Column(name = "digest_notification_type")
    @Enumerated(EnumType.STRING)
    private DigestNotificationType digestNotificationType = DigestNotificationType.NONE;

    @JoinColumn(name = "filtering_id")
    @OneToOne(fetch = FetchType.LAZY)
    private ApplicationsFiltering filtering;

    @Column(name = "upi")
    private String upi;
    
    private boolean enabled;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private String activationCode;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "user_id")
    @Valid
    private List<Comment> comments = new ArrayList<Comment>();

    @Column(name = "direct_to_url")
    private String directToUrl;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "user_id")
    private List<NotificationRecord> notificationRecords = new ArrayList<NotificationRecord>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "user_id")
    private List<PendingRoleNotification> pendingRoleNotifications = new ArrayList<PendingRoleNotification>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @JoinColumn(name = "registered_user_id")
    private List<Referee> referees = new ArrayList<Referee>();

    @OneToMany(mappedBy = "primaryAccount", fetch = FetchType.LAZY)
    private List<RegisteredUser> linkedAccounts = new ArrayList<RegisteredUser>();

    @ManyToOne
    @JoinColumn(name = "primary_account_id", nullable = true)
    private RegisteredUser primaryAccount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ROLE_LINK", joinColumns = { @JoinColumn(name = "REGISTERED_USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "APPLICATION_ROLE_ID") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Role> roles = new ArrayList<Role>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_ADMINISTRATOR_LINK", joinColumns = { @JoinColumn(name = "administrator_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichAdministrator = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_APPROVER_LINK", joinColumns = { @JoinColumn(name = "registered_user_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichApprover = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_REVIEWER_LINK", joinColumns = { @JoinColumn(name = "reviewer_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichReviewer = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_INTERVIEWER_LINK", joinColumns = { @JoinColumn(name = "interviewer_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichInterviewer = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_SUPERVISOR_LINK", joinColumns = { @JoinColumn(name = "supervisor_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichSupervisor = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_VIEWER_LINK", joinColumns = { @JoinColumn(name = "viewer_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Program> programsOfWhichViewer = new ArrayList<Program>();

    @Column(name = "ucl_user_id")
    private String uclUserId;

    public boolean canSee(ApplicationForm applicationForm) {
        return canSeeApplication(applicationForm, this);
    }

    public boolean canEditAsApplicant(ApplicationForm applicationForm) {
        return canEditApplicationAsApplicant(applicationForm, this);
    }

    public boolean canEditAsAdministrator(ApplicationForm applicationForm) {
        return canEditApplicationAsAdministrator(applicationForm, this);
    }

    public boolean canSeeReference(final ReferenceComment reference) {
        return canSeeReference(reference, this);
    }
    
    public boolean canSeeRestrictedInformation(final ApplicationForm form){
        return canSeeRestrictedInformation(form, this);
    }

    @Override
    public int compareTo(final RegisteredUser other) {
        int firstNameResult = this.firstName.compareTo(other.firstName);
        if (firstNameResult == 0) {
            return this.lastName.compareTo(other.lastName);
        }
        return firstNameResult;
    }
    
    public String getActivationCode() {
        return activationCode;
    }

    public List<RegisteredUser> getAllLinkedAccounts() {
        List<RegisteredUser> linkedAccountsList = new ArrayList<RegisteredUser>();

        if (this.primaryAccount == null) {
            linkedAccountsList.addAll(getLinkedAccounts());
        } else {
            linkedAccountsList.add(getPrimaryAccount());
            for (RegisteredUser u : getPrimaryAccount().getLinkedAccounts()) {
                if (!u.getId().equals(this.id)) {
                    linkedAccountsList.add(u);
                }
            }
        }
        return linkedAccountsList;
    }

    @Override
    @Transient
    public Collection<Role> getAuthorities() {
        return getRoles();
    }

    public List<Authority> getAuthoritiesForProgram(final Program programme) {
        return getAuthoritiesForProgramme(programme, this);
    }

    public String getAuthoritiesForProgramAsString(final Program programme) {
        List<Authority> authoritiesForProgram = getAuthoritiesForProgram(programme);
        StringBuffer stringBuffer = new StringBuffer();
        if (isInRole(Authority.SUPERADMINISTRATOR)) {
            stringBuffer.append("Superadministrator");
        }
        for (Authority authority : authoritiesForProgram) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(StringUtils.capitalize(authority.toString().toLowerCase()));
        }
        return stringBuffer.toString();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public Referee getCurrentReferee() {
        return currentReferee;
    }

    public String getDirectToUrl() {
        return directToUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    public Integer getId() {
        return id;
    }

    public List<Interviewer> getInterviewersForApplicationForm(final ApplicationForm form) {
        List<Interviewer> interviewers = new ArrayList<Interviewer>();
        List<Interviewer> formInterviewers = form.getLatestInterview().getInterviewers();
        for (Interviewer interviewer : formInterviewers) {
            if (this.getId().equals(interviewer.getUser().getId())) {
                interviewers.add(interviewer);
            }
        }
        return interviewers;
    }

    public String getLastName() {
        return lastName;
    }

    public List<RegisteredUser> getLinkedAccounts() {
        return linkedAccounts;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public List<NotificationRecord> getNotificationRecords() {
        return notificationRecords;
    }

    public String getOriginalApplicationQueryString() {
        return originalApplicationQueryString;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public List<PendingRoleNotification> getPendingRoleNotifications() {
        return pendingRoleNotifications;
    }

    public RegisteredUser getPrimaryAccount() {
        return primaryAccount;
    }

    public List<Program> getProgramsOfWhichAdministrator() {
        Collections.sort(programsOfWhichAdministrator, new Comparator<Program>() {
            @Override
            public int compare(Program o1, Program o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        return programsOfWhichAdministrator;
    }

    public List<Program> getProgramsOfWhichApprover() {
        return programsOfWhichApprover;
    }

    public List<Program> getProgramsOfWhichInterviewer() {
        return programsOfWhichInterviewer;
    }

    public List<Program> getProgramsOfWhichReviewer() {
        return programsOfWhichReviewer;
    }

    public List<Program> getProgramsOfWhichSupervisor() {
        return programsOfWhichSupervisor;
    }

    public List<Program> getProgramsOfWhichViewer() {
        return programsOfWhichViewer;
    }

    public Referee getRefereeForApplicationForm(final ApplicationForm form) {
        for (Referee referee : referees) {
            if (referee.getApplication() != null && referee.getApplication().getId().equals(form.getId()) && !referee.isDeclined()) {
                return referee;
            }
        }
        return null;
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public Reviewer getReviewerForCurrentUserFromLatestReviewRound(final ApplicationForm form) {
        ReviewRound latestReviewRound = form.getLatestReviewRound();

        if (latestReviewRound == null) {
            throw new IllegalStateException(String.format("latestReviewRound is null for application[applicationNumber=%s]", form.getApplicationNumber()));
        }

        List<Reviewer> formReviewers = latestReviewRound.getReviewers();
        for (Reviewer reviewer : formReviewers) {
            if (this.getId().equals(reviewer.getUser().getId())) {
                return reviewer;
            }
        }
        throw new IllegalStateException(String.format("Reviewer object could not be found for user [id=%d]", getId()));
    }

    public List<Role> getRoles() {
        return roles;
    }

    public String getUclUserId() {
        return uclUserId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public boolean hasAdminRightsOnApplication(final ApplicationForm form) {
        return hasAdminRightsOnApplication(form, this);
    }

    public boolean hasDeclinedToProvideReviewForApplication(final ApplicationForm form) {
        for (Comment comment : comments) {
            if (comment.getApplication().getId().equals(form.getId()) && comment.getType().equals(CommentType.REVIEW)) {
                ReviewComment reviewComment = (ReviewComment) comment;
                if (reviewComment.isDecline()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasRefereesInApplicationForm(final ApplicationForm form) {
        return getRefereeForApplicationForm(form) != null;
    }

    public boolean hasRespondedToProvideInterviewFeedbackForApplication(final ApplicationForm form) {
        for (Comment comment : comments) {
            if (comment.getApplication().getId().equals(form.getId()) && comment.getType().equals(CommentType.INTERVIEW)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(final ApplicationForm form) {
        List<Interviewer> interviewers = form.getLatestInterview().getInterviewers();
        for (Interviewer interviewer : interviewers) {
            if (interviewer.getInterview().getId().equals(form.getLatestInterview().getId()) && this.getId().equals(interviewer.getUser().getId())
                    && interviewer.getInterviewComment() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRespondedToProvideReviewForApplication(final ApplicationForm form) {
        for (Comment comment : comments) {
            if (comment.getApplication().getId().equals(form.getId()) && comment.getType().equals(CommentType.REVIEW)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRespondedToProvideReviewForApplicationLatestRound(final ApplicationForm form) {
        List<Reviewer> reviewers = form.getLatestReviewRound().getReviewers();
        for (Reviewer reviewer : reviewers) {
            if (reviewer.getReviewRound().getId().equals(form.getLatestReviewRound().getId()) && this.getId().equals(reviewer.getUser().getId())
                    && reviewer.getReview() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasStaffRightsOnApplicationForm(final ApplicationForm form) {
        return hasStaffRightsOnApplication(form, this);
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isAdminInProgramme(final Program programme) {
        return isAdminInProgramme(programme, this);
    }

    public boolean isAdminOrReviewerInProgramme(final Program programme) {
        return isAdminOrReviewerInProgramme(programme, this);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isInRole(final Authority authority) {
        return isInRole(this, authority);
    }

    public boolean isInRole(final String strAuthority) {
        return isInRole(this, strAuthority);
    }

    public boolean isNotInRole(final Authority authority) {
        return !isInRole(this, authority);
    }

    public boolean isNotInRole(final String strAuthority) {
        return !isInRole(this, strAuthority);
    }

    public boolean isInRoleInProgram(final Authority authority, final Program programme) {
        return isInRoleInProgramme(programme, this, authority);
    }

    public boolean isInRoleInProgram(final String strAuthority, final Program programme) {
        return isInRoleInProgramme(programme, this, strAuthority);
    }

    public boolean isNotInRoleInProgram(final Authority authority, Program program) {
        return !isInRoleInProgramme(program, this, authority);
    }

    public boolean isNotInRoleInProgram(final String strAuthority, final Program programme) {
        return !isInRoleInProgramme(programme, this, strAuthority);
    }

    public boolean isInterviewerInInterview(final Interview interview) {
        return isInterviewerInInterview(interview, this);
    }

    public boolean isInterviewerOfApplicationForm(final ApplicationForm form) {
        return isInterviewerOfApplication(form, this);
    }

    public boolean isInterviewerOfProgram(final Program programme) {
        return isInterviewerOfProgram(programme, this);
    }

    public boolean isApproverInProgram(final Program programme) {
        return isApproverInProgramme(programme, this);
    }

    public boolean isViewerOfProgramme(final ApplicationForm form) {
        return isViewerOfProgramme(form, this);
    }

    public boolean isApplicationAdministrator(final ApplicationForm form) {
        return isApplicationAdministrator(form, this);
    }

    public boolean isApplicant(final ApplicationForm form) {
        return isApplicant(form, this);
    }

    public boolean isProgrammeAdministrator(final ApplicationForm form) {
        return isProgrammeAdministrator(form, this);
    }

    public boolean isPastOrPresentInterviewerOfApplicationForm(final ApplicationForm form) {
        return isPastOrPresentInterviewerOfApplication(form, this);
    }

    public boolean isPastOrPresentReviewerOfApplicationForm(final ApplicationForm form) {
        return isPastOrPresentReviewerOfApplication(form, this);
    }

    public boolean isPastOrPresentSupervisorOfApplicationForm(final ApplicationForm form) {
        return isPastOrPresentSupervisorOfApplication(form, this);
    }

    public boolean isRefereeOfApplicationForm(final ApplicationForm form) {
        return isRefereeOfApplication(form, this);
    }

    public boolean isReviewerInLatestReviewRoundOfApplicationForm(final ApplicationForm form) {
        return isReviewerInLatestReviewRoundOfApplication(form, this);
    }

    public boolean isReviewerInProgramme(final Program programme) {
        return isReviewerInProgramme(programme, this);
    }

    public boolean isReviewerInReviewRound(final ReviewRound reviewRound) {
        return isReviewerInReviewRound(reviewRound, this);
    }

    public boolean isSupervisorInApprovalRound(final ApprovalRound approvalRound) {
        return isSupervisorInApprovalRound(approvalRound, this);
    }

    public boolean isSupervisorOfApplicationForm(final ApplicationForm form) {
        return isSupervisorOfApplicationForm(form, this);
    }

    public void setAccountNonExpired(final boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(final boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setCredentialsNonExpired(final boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setCurrentReferee(final Referee currentReferee) {
        this.currentReferee = currentReferee;
    }

    public void setDirectToUrl(final String directToUrl) {
        this.directToUrl = directToUrl;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setFirstName(final String firstLame) {
        this.firstName = firstLame;
    }

    public void setFirstName2(final String firstName2) {
        this.firstName2 = firstName2;
    }

    public void setFirstName3(final String firstName3) {
        this.firstName3 = firstName3;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setLinkedAccounts(final List<RegisteredUser> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    public void setNotificationRecords(final List<NotificationRecord> notificationRecords) {
        this.notificationRecords.clear();
        this.notificationRecords.addAll(notificationRecords);
    }

    public void setOriginalApplicationQueryString(final String queryString) {
        this.originalApplicationQueryString = queryString;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setPendingRoleNotifications(final List<PendingRoleNotification> pendingRoleNotifications) {
        this.pendingRoleNotifications.clear();
        this.pendingRoleNotifications.addAll(pendingRoleNotifications);
    }

    public void setPrimaryAccount(final RegisteredUser primary) {
        this.primaryAccount = primary;
    }

    public void setProgramsOfWhichAdministrator(final List<Program> programsOfWhichAdministrator) {
        this.programsOfWhichAdministrator = programsOfWhichAdministrator;
    }

    public void setProgramsOfWhichApprover(final List<Program> programsOfWhichApprover) {
        this.programsOfWhichApprover = programsOfWhichApprover;
    }

    public void setProgramsOfWhichInterviewer(final List<Program> programsOfWhichInterviewer) {
        this.programsOfWhichInterviewer = programsOfWhichInterviewer;
    }

    public void setProgramsOfWhichReviewer(final List<Program> programsOfWhichReviewer) {
        this.programsOfWhichReviewer = programsOfWhichReviewer;
    }

    public void setProgramsOfWhichSupervisor(final List<Program> programsOfWhichSupervisor) {
        this.programsOfWhichSupervisor = programsOfWhichSupervisor;
    }

    public void setProgramsOfWhichViewer(final List<Program> programsOfWhichViewer) {
        this.programsOfWhichViewer = programsOfWhichViewer;
    }

    public void setReferees(final List<Referee> referees) {
        this.referees = referees;
    }

    public void setUclUserId(final String uclUserId) {
        this.uclUserId = uclUserId;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public DigestNotificationType getDigestNotificationType() {
        return digestNotificationType;
    }

    public void setDigestNotificationType(final DigestNotificationType type) {
        this.digestNotificationType = type;
    }

    public ApplicationsFiltering getFiltering() {
        return filtering;
    }

    public void setFiltering(ApplicationsFiltering filtering) {
        this.filtering = filtering;
    }

    public String getUpi() {
        return upi;
    }
    
    public void setUpi(final String upi) {
        this.upi = upi;
    }

    @Override
    public String toString() {
        return String.format("RegisteredUser [id=%s, firstName=%s, lastName=%s, email=%s, enabled=%s]", id, firstName, lastName, email, enabled);
    }

    public void removeRole(final Authority authority) {
        CollectionUtils.filter(roles, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((Role)object).getAuthorityEnum() != authority;
            }
        });
    }
}
