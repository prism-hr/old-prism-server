package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "REGISTERED_USER")
public class RegisteredUser implements UserDetails, Comparable<RegisteredUser>, Serializable {

	private static final long serialVersionUID = 7913035836949510857L;
	
    @Id
    @GeneratedValue
    private Integer id;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
	private String firstName;
	
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
	
	private boolean accountNonLocked ;
	
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

    @Column(name = "ucl_user_id")
    private String uclUserId;

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

	public void setFirstName(String firstLame) {
		this.firstName = firstLame;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
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

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public String getUclUserId() {
        return uclUserId;
    }

    public void setUclUserId(String uclUserId) {
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

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;

	}

	public boolean isInRole(Authority authority) {
		for (Role role : roles) {
			if (role.getAuthorityEnum() == authority) {
				return true;
			}
		}
		return false;
	}

	public boolean isInRole(String strAuthority) {
		try {
			return isInRole(Authority.valueOf(strAuthority));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean isInRoleInProgram(String strAuthority, Program program) {
		try {
			return isInRoleInProgram(Authority.valueOf(strAuthority), program);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean canSee(ApplicationForm applicationForm) {

		if (applicationForm.getStatus() == ApplicationFormStatus.UNSUBMITTED && !isInRole(Authority.APPLICANT)) {
			return false;
		}

		if (isInRole(Authority.SUPERADMINISTRATOR)) {
			return true;
		}

		if (applicationForm.getApplicationAdministrator() != null && this.getId().equals(applicationForm.getApplicationAdministrator().getId())) {
			return true;
		}
		if (isInRole(Authority.ADMINISTRATOR)) {
			if (listContainsId(this, applicationForm.getProgram().getAdministrators())) {
				return true;
			}
		}

		if (applicationForm.getStatus() == ApplicationFormStatus.REVIEW) {
			for (Reviewer reviewer : applicationForm.getLatestReviewRound().getReviewers()) {
				if (this.getId().equals(reviewer.getUser().getId())) {
					return true;
				}
			}
		}

		Interview latestInterview = applicationForm.getLatestInterview();
		if (latestInterview != null && applicationForm.getStatus() == ApplicationFormStatus.INTERVIEW) {
			for (Interviewer interviewer : latestInterview.getInterviewers()) {
				if (this.getId().equals(interviewer.getUser().getId())) {
					return true;
				}
			}
		}

		ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();
		if (latestApprovalRound != null && (applicationForm.getStatus() == ApplicationFormStatus.APPROVAL  ||applicationForm.getStatus() == ApplicationFormStatus.APPROVED )  ) {
			for (Supervisor surevisor : latestApprovalRound.getSupervisors()) {
				if (this.getId().equals(surevisor.getUser().getId())) {
					return true;
				}
			}
		}
		if (isInRole(Authority.APPROVER) && applicationForm.getStatus() == ApplicationFormStatus.APPROVAL) {
			if (applicationForm.getProgram().isApprover(this)) {
				return true;
			}
		}

		if (isInRole(Authority.REFEREE)) {
			List<Referee> referees = applicationForm.getReferees();
			for (Referee referee : referees) {
				if (!referee.isDeclined() && referee.getUser() != null) {
					if (referee.getUser().getId().equals(this.getId()) || (listContainsId(referee, this.getReferees()))) {
						return true;
					}
				}
			}
		}

		if (applicationForm.getApplicant() != null && this.getId().equals(applicationForm.getApplicant().getId())) {
			return true;
		}

		return false;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public List<Program> getProgramsOfWhichAdministrator() {
		return programsOfWhichAdministrator;
	}

	public void setProgramsOfWhichAdministrator(List<Program> programsOfWhichAdministrator) {
		this.programsOfWhichAdministrator = programsOfWhichAdministrator;
	}

	public List<Program> getProgramsOfWhichApprover() {
		return programsOfWhichApprover;
	}

	public void setProgramsOfWhichApprover(List<Program> programsOfWhichApprover) {
		this.programsOfWhichApprover = programsOfWhichApprover;
	}

	public List<Program> getProgramsOfWhichReviewer() {
		return programsOfWhichReviewer;
	}

	public void setProgramsOfWhichReviewer(List<Program> programsOfWhichReviewer) {
		this.programsOfWhichReviewer = programsOfWhichReviewer;
	}

	public List<Authority> getAuthoritiesForProgram(Program program) {
		List<Authority> authorities = new ArrayList<Authority>();
		if (listContainsId(program, getProgramsOfWhichAdministrator())) {
			authorities.add(Authority.ADMINISTRATOR);
		}
		if (listContainsId(program, getProgramsOfWhichReviewer())) {
			authorities.add(Authority.REVIEWER);
		}
		if (listContainsId(program, getProgramsOfWhichInterviewer())) {
			authorities.add(Authority.INTERVIEWER);
		}
		if (listContainsId(program, getProgramsOfWhichApprover())) {
			authorities.add(Authority.APPROVER);
		}
		if (listContainsId(program, getProgramsOfWhichSupervisor())) {
			authorities.add(Authority.SUPERVISOR);
		}
		return authorities;

	}

	public String getAuthoritiesForProgramAsString(Program program) {
		List<Authority> authoritiesForProgram = getAuthoritiesForProgram(program);
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

	public boolean isInRoleInProgram(Authority authority, Program program) {
		if (Authority.SUPERADMINISTRATOR == authority && isInRole(Authority.SUPERADMINISTRATOR)) {
			return true;
		}
		return getAuthoritiesForProgram(program).contains(authority);
	}

	public Role getRoleByAuthority(Authority authority) {
		for (Role role : roles) {
			if (role.getAuthorityEnum() == authority) {
				return role;
			}
		}
		return null;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public boolean isReviewerInProgramme(Program program) {
		if (listContainsId(this, program.getProgramReviewers())) {
			return true;
		}
		return false;
	}

	public boolean isAdminOrReviewerInProgramme(Program program) {
		if (listContainsId(this, program.getAdministrators()) || listContainsId(this, program.getProgramReviewers())) {
			return true;
		}
		return false;
	}

	public boolean isAdminInProgramme(Program program) {
		if (listContainsId(this, program.getAdministrators())) {
			return true;
		}
		return false;
	}

	public List<Referee> getReferees() {
		return referees;
	}

	public void setReferees(List<Referee> referees) {
		this.referees = referees;
	}

	public Referee getCurrentReferee() {
		return currentReferee;
	}

	public void setCurrentReferee(Referee currentReferee) {
		this.currentReferee = currentReferee;
	}

	public boolean isRefereeOfApplicationForm(ApplicationForm form) {
		return this.isInRole(Authority.REFEREE) && hasRefereesInApplicationForm(form);
	}

	public boolean isReviewerInLatestReviewRoundOfApplicationForm(ApplicationForm form) {
		ReviewRound latestReviewRound = form.getLatestReviewRound();
		if (latestReviewRound == null) {
			return false;
		}
		for (Reviewer reviewer : latestReviewRound.getReviewers()) {
			if (reviewer != null && this.getId().equals(reviewer.getUser().getId())) {
				return true;
			}
		}
		return false;

	}

	public boolean isPastOrPresentReviewerOfApplicationForm(ApplicationForm applicationForm) {
		for (ReviewRound reviewRound : applicationForm.getReviewRounds()) {
			for (Reviewer reviewer : reviewRound.getReviewers()) {
				if (reviewer != null && this.getId().equals(reviewer.getUser().getId())) {
					return true;
				}
			}
		}
		return false;

	}

	public boolean isInterviewerOfApplicationForm(ApplicationForm form) {
		Interview latestInterview = form.getLatestInterview();
		if (latestInterview != null) {
			for (Interviewer interviewer : latestInterview.getInterviewers()) {
				if (interviewer != null && this.getId().equals(interviewer.getUser().getId())) {
					return true;
				}
			}
		}
		return false;

	}
	public boolean isPastOrPresentInterviewerOfApplicationForm(ApplicationForm applicationForm) {
		for (Interview interview : applicationForm.getInterviews()) {
			for (Interviewer interviewer : interview.getInterviewers()) {
				if (interviewer != null && this.getId().equals(interviewer.getUser().getId())) {
					return true;
				}
			}
		}
		return false;

	}

	public boolean isSupervisorOfApplicationForm(ApplicationForm form) {
		ApprovalRound approvalRound = form.getLatestApprovalRound();
		if (approvalRound != null) {
			for (Supervisor supervisor : approvalRound.getSupervisors()) {
				if (supervisor != null && this.getId().equals(supervisor.getUser().getId())) {
					return true;
				}
			}
		}
		return false;

	}
	
	public boolean isPastOrPresentSupervisorOfApplicationForm(ApplicationForm applicationForm) {
		for (ApprovalRound approvalRound : applicationForm.getApprovalRounds()) {
			for (Supervisor supervisor : approvalRound.getSupervisors()) {
				if (supervisor != null && this.getId().equals(supervisor.getUser().getId())) {
					return true;
				}
			}
		}
		return false;

	}


	public boolean isInterviewerOfProgram(Program program) {
		for (RegisteredUser interviewer : program.getInterviewers()) {
			if (this.getId().equals(interviewer.getId())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasRefereesInApplicationForm(ApplicationForm form) {
		return getRefereeForApplicationForm(form) != null;
	}

	public boolean canSeeReference(ReferenceComment reference) {
		if (this.isInRole(Authority.APPLICANT)) {
			return false;
		}
		if (!this.canSee(reference.getReferee().getApplication())) {
			return false;
		}
		if (this.isRefereeOfApplicationForm(reference.getReferee().getApplication()) && !this.getId().equals(reference.getReferee().getUser().getId())) {
			return false;
		}
		return true;
	}

	public Referee getRefereeForApplicationForm(ApplicationForm applicationForm) {
		for (Referee referee : referees) {
			if (referee.getApplication() != null && referee.getApplication().getId().equals(applicationForm.getId()) && !referee.isDeclined()) {
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

	public void setNotificationRecords(List<NotificationRecord> notificationRecords) {
		this.notificationRecords.clear();
		this.notificationRecords.addAll(notificationRecords);
	}

	public boolean hasRespondedToProvideReviewForApplication(ApplicationForm application) {	
		
		for (Comment comment : comments) {
			if (comment.getApplication().getId().equals(application.getId()) && comment.getType().equals(CommentType.REVIEW)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasRespondedToProvideInterviewFeedbackForApplication(ApplicationForm application) {
		for (Comment comment : comments) {
			if (comment.getApplication().getId().equals(application.getId()) && comment.getType().equals(CommentType.INTERVIEW)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(ApplicationForm application) {
			List<Interviewer> interviewers = application.getLatestInterview().getInterviewers();
			for (Interviewer interviewer : interviewers) {
				if(interviewer.getInterview().getId().equals(application.getLatestInterview().getId()) && this.getId().equals(interviewer.getUser().getId()) && interviewer.getInterviewComment() != null){
					return true;
				}
			}
			return false;
	}
	
	public boolean hasRespondedToProvideReviewForApplicationLatestRound(ApplicationForm application) {
			List<Reviewer> reviewers = application.getLatestReviewRound().getReviewers();
			for (Reviewer reviewer : reviewers) {
				if(reviewer.getReviewRound().getId().equals(application.getLatestReviewRound().getId()) && this.getId().equals(reviewer.getUser().getId()) && reviewer.getReview() != null){
					return true;
				}
			}
		return false;
	}

	public boolean hasDeclinedToProvideReviewForApplication(ApplicationForm application) {

		for (Comment comment : comments) {
			if (comment.getApplication().getId().equals(application.getId()) && comment.getType().equals(CommentType.REVIEW)) {
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

	public void setProgramsOfWhichInterviewer(List<Program> programsOfWhichInterviewer) {
		this.programsOfWhichInterviewer = programsOfWhichInterviewer;
	}

	public List<Reviewer> getReviewersForApplicationForm(ApplicationForm applicationForm) {
		List<Reviewer> reviewers = new ArrayList<Reviewer>();
		ReviewRound latestReviewRound = applicationForm.getLatestReviewRound();
		if (latestReviewRound == null) {
			return reviewers;
		}
		List<Reviewer> formReviewers = latestReviewRound.getReviewers();
		for (Reviewer reviewer : formReviewers) {
			if (this.getId().equals(reviewer.getUser().getId())) {
				reviewers.add(reviewer);
			}
		}
		return reviewers;
	}

	public List<Interviewer> getInterviewersForApplicationForm(ApplicationForm applicationForm) {
		List<Interviewer> interviewers = new ArrayList<Interviewer>();
		List<Interviewer> formInterviewers = applicationForm.getLatestInterview().getInterviewers();
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

	public void setPendingRoleNotifications(List<PendingRoleNotification> pendingRoleNotifications) {
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

	public void setProgramsOfWhichSupervisor(List<Program> programsOfWhichSupervisor) {
		this.programsOfWhichSupervisor = programsOfWhichSupervisor;
	}

	public boolean hasAdminRightsOnApplication(ApplicationForm applicationForm) {
		if(ApplicationFormStatus.UNSUBMITTED == applicationForm.getStatus()){
			return false;
		}
		if(this.isInRole(Authority.SUPERADMINISTRATOR)){
			return true;
		}
		if (applicationForm.getApplicationAdministrator() != null && this.getId().equals(applicationForm.getApplicationAdministrator().getId())) {
			return true;
		}
		if(listContainsId(this, applicationForm.getProgram().getAdministrators())) {
			return true;
		}
		return false;

	}

	public String getDirectToUrl() {
		return directToUrl;
	}

	public void setDirectToUrl(String directToUrl) {
		this.directToUrl = directToUrl;
	}

	@Override
	public int compareTo(RegisteredUser other) {
		int firstNameResult = this.firstName.compareTo(other.firstName);
		if (firstNameResult == 0) {
			return this.lastName.compareTo(other.lastName);
		}
		return firstNameResult;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOriginalApplicationQueryString() {
		return originalApplicationQueryString;
	}

	public void setOriginalApplicationQueryString(String queryString) {
		this.originalApplicationQueryString = queryString;
	}

	public boolean isReviewerInReviewRound(ReviewRound reviewRound) {
		if(reviewRound == null){
			return false;
		}
		for (Reviewer reviewer : reviewRound.getReviewers()) {
			if(this.getId().equals(reviewer.getUser().getId())){
				return true;
			}
		}
		return false;
	}

	public boolean isInterviewerInInterview(Interview interview) {
		if(interview == null){
			return false;
		}
		for (Interviewer interviewer : interview.getInterviewers()) {
			if(this.getId().equals(interviewer.getUser().getId())){
				return true;
			}
		}
		return false;
	}

	public boolean isSupervisorInApprovalRound(ApprovalRound approvalRound) {
		if(approvalRound == null){
			return false;
		}
		for (Supervisor supervisor : approvalRound.getSupervisors()) {
			if(this.getId().equals(supervisor.getUser().getId())){
				return true;
			}
		}
		return false;
	}

	public boolean hasStaffRightsOnApplicationForm(ApplicationForm applicationForm) {
		if(hasAdminRightsOnApplication(applicationForm)){
			return true;
		}
		if(isPastOrPresentReviewerOfApplicationForm(applicationForm)){
			return true;
		}
		if(isPastOrPresentInterviewerOfApplicationForm(applicationForm)){
			return true;
		}
		if(isPastOrPresentSupervisorOfApplicationForm(applicationForm)){
			return true;
		}
		if(isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())){
			return true;
		}
		return false;
	}

    public List<RegisteredUser> getLinkedAccounts() {
        return linkedAccounts;
    }
    
    public List<RegisteredUser> getAllLinkedAccounts() {
        List<RegisteredUser> linkedAccounts = new ArrayList<RegisteredUser>();
        
        if (this.primaryAccount == null) {
            linkedAccounts.addAll(getLinkedAccounts());
        } else {
            linkedAccounts.add(getPrimaryAccount());
            for (RegisteredUser u : getPrimaryAccount().getLinkedAccounts()) {
                if (!u.getId().equals(this.id)) {
                    linkedAccounts.add(u);
                }
            }
        }
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<RegisteredUser> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }
    
    private boolean listContainsId(Referee user, List<Referee> users) {
        for (Referee entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    } 
    
    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean listContainsId(Program program, List<Program> programs) {
        for (Program entry : programs) {
            if (entry.getId().equals(program.getId())) {
                return true;
            }
        }
        return false;
    }

    public RegisteredUser getPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(RegisteredUser primary) {
        this.primaryAccount = primary;
    }
}
