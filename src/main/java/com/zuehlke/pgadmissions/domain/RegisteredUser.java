package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "REGISTERED_USER")
public class RegisteredUser extends Authorisable implements UserDetails, Comparable<RegisteredUser>, Serializable {

    private static final long serialVersionUID = 7913035836949510857L;

    @Id
    @GeneratedValue
    private Integer id;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    private String firstName;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    private String firstName2;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
    private String firstName3;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
    private String lastName;

    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ROLE_LINK", joinColumns = { @JoinColumn(name = "REGISTERED_USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "APPLICATION_ROLE_ID") })
    private List<Role> roles = new ArrayList<Role>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_ADMINISTRATOR_LINK", joinColumns = { @JoinColumn(name = "administrator_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    private List<Program> programsOfWhichAdministrator = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_APPROVER_LINK", joinColumns = { @JoinColumn(name = "registered_user_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    private List<Program> programsOfWhichApprover = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_REVIEWER_LINK", joinColumns = { @JoinColumn(name = "reviewer_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    private List<Program> programsOfWhichReviewer = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_INTERVIEWER_LINK", joinColumns = { @JoinColumn(name = "interviewer_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    private List<Program> programsOfWhichInterviewer = new ArrayList<Program>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PROGRAM_SUPERVISOR_LINK", joinColumns = { @JoinColumn(name = "supervisor_id") }, inverseJoinColumns = { @JoinColumn(name = "program_id") })
    private List<Program> programsOfWhichSupervisor = new ArrayList<Program>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<ApplicationsFilter> applicationsFilters = new ArrayList<ApplicationsFilter>();

    @Column(name = "ucl_user_id")
    private String uclUserId;
    
    public List<ApplicationsFilter> getApplicationsFilters() {
        return applicationsFilters;
    }
    
    public void setApplicationsFilters(final List<ApplicationsFilter> applicationsFilters) {
        this.applicationsFilters = applicationsFilters;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstLame) {
        this.firstName = firstLame;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(final String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public void setFirstName3(final String firstName3) {
        this.firstName3 = firstName3;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUclUserId() {
        return uclUserId;
    }

    public void setUclUserId(final String uclUserId) {
        this.uclUserId = uclUserId;
    }

    @Override
    @Transient
    public Collection<Role> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(final boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(final boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountNonExpired(final boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isInRole(Authority authority) {
        return isInRole(this, authority);
    }

    public boolean isInRole(String strAuthority) {
        return isInRole(this, strAuthority);
    }

    public boolean isInRoleInProgram(final String strAuthority, final Program programme) {
        return isInRoleInProgramme(programme, this, strAuthority);
    }

    public boolean canSee(ApplicationForm applicationForm) {
        return canSeeApplication(applicationForm, this);
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
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

    public void setProgramsOfWhichAdministrator(final List<Program> programsOfWhichAdministrator) {
        this.programsOfWhichAdministrator = programsOfWhichAdministrator;
    }

    public List<Program> getProgramsOfWhichApprover() {
        return programsOfWhichApprover;
    }

    public void setProgramsOfWhichApprover(final List<Program> programsOfWhichApprover) {
        this.programsOfWhichApprover = programsOfWhichApprover;
    }

    public List<Program> getProgramsOfWhichReviewer() {
        return programsOfWhichReviewer;
    }

    public void setProgramsOfWhichReviewer(final List<Program> programsOfWhichReviewer) {
        this.programsOfWhichReviewer = programsOfWhichReviewer;
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

    public boolean isInRoleInProgram(final Authority authority, final Program programme) {
        return isInRoleInProgramme(programme, this, authority);
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isReviewerInProgramme(final Program programme) {
        return isReviewerInProgramme(programme, this);
    }

    public boolean isAdminOrReviewerInProgramme(final Program programme) {
        return isAdminOrReviewerInProgramme(programme, this);
    }

    public boolean isAdminInProgramme(final Program programme) {
        return isAdminInProgramme(programme, this);
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public void setReferees(final List<Referee> referees) {
        this.referees = referees;
    }

    public Referee getCurrentReferee() {
        return currentReferee;
    }

    public void setCurrentReferee(final Referee currentReferee) {
        this.currentReferee = currentReferee;
    }

    public boolean isRefereeOfApplicationForm(final ApplicationForm form) {
        return isRefereeOfApplication(form, this);
    }

    public boolean isReviewerInLatestReviewRoundOfApplicationForm(final ApplicationForm form) {
        return isReviewerInLatestReviewRoundOfApplication(form, this);
    }

    public boolean isPastOrPresentReviewerOfApplicationForm(final ApplicationForm form) {
        return isPastOrPresentReviewerOfApplication(form, this);
    }

    public boolean isInterviewerOfApplicationForm(final ApplicationForm form) {
        return isInterviewerOfApplication(form, this);
    }

    public boolean isPastOrPresentInterviewerOfApplicationForm(final ApplicationForm form) {
        return isPastOrPresentInterviewerOfApplication(form, this);
    }

    public boolean isSupervisorOfApplicationForm(final ApplicationForm form) {
        return isSupervisorOfApplicationForm(form, this);
    }

    public boolean isPastOrPresentSupervisorOfApplicationForm(final ApplicationForm form) {
        return isPastOrPresentSupervisorOfApplication(form, this);
    }

    public boolean isInterviewerOfProgram(final Program programme) {
        return isInterviewerOfProgram(programme, this);
    }

    public boolean hasRefereesInApplicationForm(final ApplicationForm form) {
        return getRefereeForApplicationForm(form) != null;
    }

    public boolean canSeeReference(final ReferenceComment reference) {
        return canSeeReference(reference, this);
    }

    public Referee getRefereeForApplicationForm(final ApplicationForm form) {
        for (Referee referee : referees) {
            if (referee.getApplication() != null && referee.getApplication().getId().equals(form.getId()) && !referee.isDeclined()) {
                return referee;
            }
        }
        return null;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<NotificationRecord> getNotificationRecords() {
        return notificationRecords;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setNotificationRecords(final List<NotificationRecord> notificationRecords) {
        this.notificationRecords.clear();
        this.notificationRecords.addAll(notificationRecords);
    }

    public boolean hasRespondedToProvideReviewForApplication(final ApplicationForm form) {
        for (Comment comment : comments) {
            if (comment.getApplication().getId().equals(form.getId()) && comment.getType().equals(CommentType.REVIEW)) {
                return true;
            }
        }
        return false;
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
            if (interviewer.getInterview().getId().equals(form.getLatestInterview().getId()) && this.getId().equals(interviewer.getUser().getId()) && interviewer.getInterviewComment() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRespondedToProvideReviewForApplicationLatestRound(final ApplicationForm form) {
        List<Reviewer> reviewers = form.getLatestReviewRound().getReviewers();
        for (Reviewer reviewer : reviewers) {
            if (reviewer.getReviewRound().getId().equals(form.getLatestReviewRound().getId()) && this.getId().equals(reviewer.getUser().getId()) && reviewer.getReview() != null) {
                return true;
            }
        }
        return false;
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

    public List<Program> getProgramsOfWhichInterviewer() {
        return programsOfWhichInterviewer;
    }

    public void setProgramsOfWhichInterviewer(final List<Program> programsOfWhichInterviewer) {
        this.programsOfWhichInterviewer = programsOfWhichInterviewer;
    }

    public Reviewer getReviewerForCurrentUserFromLatestReviewRound(final ApplicationForm form) {
        ReviewRound latestReviewRound = form.getLatestReviewRound();

        if (latestReviewRound == null) {
            throw new IllegalStateException(String.format("latestReviewRound is null for application[applicationNumber=%s]",
                    form.getApplicationNumber()));
        }

        List<Reviewer> formReviewers = latestReviewRound.getReviewers();
        for (Reviewer reviewer : formReviewers) {
            if (this.getId().equals(reviewer.getUser().getId())) {
                return reviewer;
            }
        }
        throw new IllegalStateException(String.format("Reviewer object could not be found for user [id=%d]", getId()));
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

    public List<PendingRoleNotification> getPendingRoleNotifications() {
        return pendingRoleNotifications;
    }

    public void setPendingRoleNotifications(final List<PendingRoleNotification> pendingRoleNotifications) {
        this.pendingRoleNotifications.clear();
        this.pendingRoleNotifications.addAll(pendingRoleNotifications);
    }

    @Override
    public String toString() {
        return "RegisteredUser [id=" + id + ", username=" + username + "]";
    }

    public List<Program> getProgramsOfWhichSupervisor() {
        return programsOfWhichSupervisor;
    }

    public void setProgramsOfWhichSupervisor(final List<Program> programsOfWhichSupervisor) {
        this.programsOfWhichSupervisor = programsOfWhichSupervisor;
    }

    public boolean hasAdminRightsOnApplication(final ApplicationForm form) {
        return hasAdminRightsOnApplication(form, this);
    }

    public String getDirectToUrl() {
        return directToUrl;
    }

    public void setDirectToUrl(final String directToUrl) {
        this.directToUrl = directToUrl;
    }

    @Override
    public int compareTo(final RegisteredUser other) {
        int firstNameResult = this.firstName.compareTo(other.firstName);
        if (firstNameResult == 0) {
            return this.lastName.compareTo(other.lastName);
        }
        return firstNameResult;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOriginalApplicationQueryString() {
        return originalApplicationQueryString;
    }

    public void setOriginalApplicationQueryString(final String queryString) {
        this.originalApplicationQueryString = queryString;
    }

    public boolean isReviewerInReviewRound(final ReviewRound reviewRound) {
        return isReviewerInReviewRound(reviewRound, this);
    }

    public boolean isInterviewerInInterview(final Interview interview) {
        return isInterviewerInInterview(interview, this);
    }

    public boolean isSupervisorInApprovalRound(final ApprovalRound approvalRound) {
        return isSupervisorInApprovalRound(approvalRound, this);
    }

    public boolean hasStaffRightsOnApplicationForm(final ApplicationForm form) {
        return hasStaffRightsOnApplication(form, this);
    }

    public List<RegisteredUser> getLinkedAccounts() {
        return linkedAccounts;
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

    public void setLinkedAccounts(final List<RegisteredUser> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public String getDisplayName() {
        StringBuilder userNameBuilder = new StringBuilder(getFirstName());
        userNameBuilder.append(StringUtils.trimToEmpty(" " + getFirstName2()));
        userNameBuilder.append(StringUtils.trimToEmpty(" " + getFirstName3()));
        userNameBuilder.append(StringUtils.trimToEmpty(" " + getLastName()));
        return userNameBuilder.toString();
    }

    public RegisteredUser getPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(final RegisteredUser primary) {
        this.primaryAccount = primary;
    }
}
