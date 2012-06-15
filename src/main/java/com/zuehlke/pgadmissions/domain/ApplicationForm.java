package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

@Entity(name = "APPLICATION_FORM")
@Access(AccessType.FIELD)
public class ApplicationForm extends DomainObject<Integer> implements Comparable<ApplicationForm> {

	private static final long serialVersionUID = -7671357234815343496L;

	@Column(name = "application_number")
	private String applicationNumber;

	@ManyToOne
	@JoinColumn(name = "app_administrator_id")
	private RegisteredUser applicationAdministrator;

	@OneToOne(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "rejection_id")
	private Rejection rejection;

	@OneToMany(orphanRemoval = true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<NotificationRecord> notificationRecords = new ArrayList<NotificationRecord>();

	@OneToMany(orphanRemoval = true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<Event> events = new ArrayList<Event>();

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationFormStatusEnumUserType")
	private ApplicationFormStatus status = ApplicationFormStatus.UNSUBMITTED;

	@OneToOne(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "current_address_id")
	private Address currentAddress;

	@OneToOne(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "contact_address_id")
	private Address contactAddress;

	@ManyToOne
	@JoinColumn(name = "cv_id")
	private Document cv = null;

	@Temporal(TemporalType.DATE)
	@Column(name = "due_date")
	private Date dueDate;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CheckedStatusEnumUserType")
	@Column(name = "accepted_terms")
	private CheckedStatus acceptedTerms;

	@ManyToOne
	@JoinColumn(name = "personal_statement_id")
	private Document personalStatement = null;

	@Column(name = "app_date_time", insertable = false)
	@Generated(GenerationTime.INSERT)
	@Temporal(TemporalType.TIMESTAMP)
	private Date applicationTimestamp;

	@Column(name = "submitted_on_timestamp")
	private Date submittedDate;
	
	@Column(name = "batch_deadline")
	private Date batchDeadline;
	
	@Column(name = "last_updated")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastUpdated;

	@ManyToOne
	@JoinColumn(name = "applicant_id")
	private RegisteredUser applicant = null;

	@ManyToOne
	@JoinColumn(name = "approver_user_id")
	private RegisteredUser approver = null;

	@Column(name = "project_title")
	private String projectTitle;

	@ManyToOne
	@JoinColumn(name = "program_id")
	private Program program;

	@OneToOne(mappedBy = "application")
	private PersonalDetails personalDetails;

	@OneToOne(mappedBy = "application")
	private ProgrammeDetails programmeDetails;

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<ReviewRound> reviewRounds = new ArrayList<ReviewRound>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<ApprovalRound> approvalRounds = new ArrayList<ApprovalRound>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@OrderBy("createdDate desc")
	@JoinColumn(name = "application_form_id")
	private List<Interview> interviews = new ArrayList<Interview>();

	@OneToMany(mappedBy = "application")
	private List<Comment> applicationComments = new ArrayList<Comment>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<Qualification> qualifications = new ArrayList<Qualification>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<Funding> fundings = new ArrayList<Funding>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<Referee> referees = new ArrayList<Referee>();

	@OneToOne(mappedBy = "application")
	private AdditionalInformation additionalInformation;

	@Column(name = "reject_notification_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date rejectNotificationDate;

	@OneToOne
	@JoinColumn(name = "latest_interview_id")
	private Interview latestInterview;

	@OneToOne
	@JoinColumn(name = "latest_approval_round_id")
	private ApprovalRound latestApprovalRound;

	@OneToOne
	@JoinColumn(name = "latest_review_round_id")
	private ReviewRound latestReviewRound;

	public List<Qualification> getQualifications() {
		return qualifications;
	}

	public void setQualifications(List<Qualification> qualifications) {
		this.qualifications.clear();
		this.qualifications.addAll(qualifications);
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public RegisteredUser getApplicant() {
		return applicant;
	}

	public void setApplicant(RegisteredUser user) {
		this.applicant = user;
	}

	public RegisteredUser getApprover() {
		return approver;
	}

	public void setApprover(RegisteredUser approver) {
		this.approver = approver;
	}

	public Date getApplicationTimestamp() {
		return applicationTimestamp;
	}

	public void setApplicationTimestamp(Date applicationTimestamp) {
		this.applicationTimestamp = applicationTimestamp;
	}

	public boolean isModifiable() {
		if (status == ApplicationFormStatus.REJECTED || status == ApplicationFormStatus.APPROVED || status == ApplicationFormStatus.WITHDRAWN) {
			return false;
		}
		return true;
	}

	public boolean isSubmitted() {
		return status != ApplicationFormStatus.UNSUBMITTED;
	}

	public boolean isDecided() {
		if (status == ApplicationFormStatus.REJECTED || status == ApplicationFormStatus.APPROVED) {
			return true;
		}
		return false;
	}

	public boolean isWithdrawn() {
		return status == ApplicationFormStatus.WITHDRAWN;
	}

	public List<Comment> getApplicationComments() {
		return applicationComments;
	}

	public void setApplicationComments(List<Comment> applicationComments) {
		this.applicationComments = applicationComments;
	}

	public boolean hasComments() {
		return applicationComments != null && !applicationComments.isEmpty();
	}

	public boolean hasQualifications() {
		return !qualifications.isEmpty();
	}

	public List<Funding> getFundings() {
		return fundings;
	}

	public void setFundings(List<Funding> fundings) {
		this.fundings = fundings;
	}

	public List<EmploymentPosition> getEmploymentPositions() {
		return employmentPositions;
	}

	public void setEmploymentPositions(List<EmploymentPosition> employmentPositions) {
		this.employmentPositions = employmentPositions;
	}

	public List<Referee> getReferees() {
		return referees;
	}

	public void setReferees(List<Referee> referees) {
		this.referees = referees;
	}

	public PersonalDetails getPersonalDetails() {
		if (personalDetails == null) {
			return new PersonalDetails();
		}
		return personalDetails;
	}

	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}

	public ProgrammeDetails getProgrammeDetails() {
		if (programmeDetails == null) {
			return new ProgrammeDetails();
		}
		return programmeDetails;
	}

	public void setProgrammeDetails(ProgrammeDetails programmeDetails) {
		this.programmeDetails = programmeDetails;
	}

	public AdditionalInformation getAdditionalInformation() {
		if (additionalInformation == null) {
			return new AdditionalInformation();
		}
		return additionalInformation;
	}

	public void setAdditionalInformation(AdditionalInformation additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedOn) {
		this.submittedDate = submittedOn;
	}

	public Document getCv() {
		return cv;
	}

	public void setCv(Document cv) {
		this.cv = cv;
	}

	public Document getPersonalStatement() {
		return personalStatement;
	}

	public void setPersonalStatement(Document personalStatement) {
		this.personalStatement = personalStatement;
	}

	public Address getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(Address currentAddress) {
		this.currentAddress = currentAddress;
	}

	public Address getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(Address contactAddress) {
		this.contactAddress = contactAddress;
	}

	public List<Comment> getVisibleComments(RegisteredUser user) {

		if (user.isInRole(Authority.APPLICANT) || user.isRefereeOfApplicationForm(this)) {
			return new ArrayList<Comment>();
		}

		Collections.sort(applicationComments);
		return applicationComments;
	}

	public boolean shouldOpenFirstSection() {
		return this.programmeDetails == null && this.personalDetails == null && this.currentAddress == null//
				&& this.contactAddress == null && this.qualifications.isEmpty() && this.employmentPositions.isEmpty()//
				&& this.fundings.isEmpty() && this.referees.isEmpty() && this.personalStatement == null//
				&& this.cv == null && this.additionalInformation == null;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date validationDueDate) {
		this.dueDate = validationDueDate;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public ApplicationFormStatus getStatus() {
		return status;
	}

	public Date getRejectNotificationDate() {
		return rejectNotificationDate;
	}

	public void setRejectNotificationDate(Date rejectNotificationDate) {
		this.rejectNotificationDate = rejectNotificationDate;
	}

	public void setStatus(ApplicationFormStatus status) {
		this.status = status;
	}


	public boolean isInValidationStage() {
		return status == ApplicationFormStatus.VALIDATION;
	}

	public List<NotificationRecord> getNotificationRecords() {
		return notificationRecords;
	}

	public void setNotificationRecords(List<NotificationRecord> notificationRecords) {
		this.notificationRecords.clear();
		this.notificationRecords.addAll(notificationRecords);
	}

	public NotificationRecord getNotificationForType(NotificationType type) {
		for (NotificationRecord notification : notificationRecords) {
			if (notification.getNotificationType() == type) {
				return notification;
			}
		}
		return null;
	}

	public NotificationRecord getNotificationForType(String strType) {
		return getNotificationForType(NotificationType.valueOf(strType));
	}

	public boolean hasAcceptedTheTerms() {
		return acceptedTerms == CheckedStatus.YES;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public CheckedStatus getAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(CheckedStatus acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}

	public boolean isInState(String strStatus) {
		try {
			return status == ApplicationFormStatus.valueOf(strStatus);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events.clear();
		this.events.addAll(events);
	}

	public List<RegisteredUser> getUsersWillingToSupervise() {
		List<RegisteredUser> usersWillingToSupervise = new ArrayList<RegisteredUser>();
		for (Comment comment : applicationComments) {
			if (comment instanceof InterviewComment) {
				InterviewComment interviewComment = (InterviewComment) comment;
				if (interviewComment.getWillingToSupervise() != null && interviewComment.getWillingToSupervise()) {
					usersWillingToSupervise.add(interviewComment.getUser());
				}
			}
		}
		return usersWillingToSupervise;
	}

	public List<RegisteredUser> getReviewersWillingToInterview() {
		List<RegisteredUser> usersWillingToInterview = new ArrayList<RegisteredUser>();
		for (Comment comment : applicationComments) {
			if (comment instanceof ReviewComment) {
				ReviewComment reviewComment = (ReviewComment) comment;
				if (reviewComment.getWillingToInterview()!= null && reviewComment.getWillingToInterview()) {
					usersWillingToInterview.add(reviewComment.getUser());
				}
			}
		}
		return usersWillingToInterview;
	}

	@Override
	public int compareTo(ApplicationForm appForm) {

		if (appForm.getSubmittedDate() != null && this.getSubmittedDate() == null) {
			return -1;
		}
		if (appForm.getSubmittedDate() == null && this.getSubmittedDate() != null) {
			return 1;
		}
		if (appForm.getSubmittedDate() == null && this.getSubmittedDate() == null) {
			return this.applicationTimestamp.compareTo(appForm.getApplicationTimestamp());
		}
		return this.submittedDate.compareTo(appForm.getSubmittedDate());
	}

	public List<Interview> getInterviews() {
		return interviews;
	}

	public void setInterviews(List<Interview> interviews) {
		this.interviews = interviews;
	}

	public Interview getLatestInterview() {
		return latestInterview;
	}

	public void setLatestInterview(Interview latestInterview) {
		this.latestInterview = latestInterview;
	}

	public List<ReviewRound> getReviewRounds() {
		return reviewRounds;
	}

	public void setReviewRounds(List<ReviewRound> reviewRound) {
		this.reviewRounds = reviewRound;
	}

	public ReviewRound getLatestReviewRound() {
		return latestReviewRound;
	}

	public void setLatestReviewRound(ReviewRound latestReviewRound) {
		this.latestReviewRound = latestReviewRound;
	}

	public List<StateChangeEvent> getStateChangeEventsSortedByDate() {
		List<StateChangeEvent> stateChangeEvents = new ArrayList<StateChangeEvent>();
		Comparator<StateChangeEvent> dateComparator = new Comparator<StateChangeEvent>() {
			@Override
			public int compare(StateChangeEvent event1, StateChangeEvent event2) {
				return event1.getDate().compareTo(event2.getDate());
			}
		};
		for (Event event : events) {
			if(event instanceof StateChangeEvent){
				stateChangeEvents.add((StateChangeEvent) event);
			}
		}
		Collections.sort(stateChangeEvents, dateComparator);
		return stateChangeEvents;
	}

	
	public Rejection getRejection() {
		return rejection;
	}

	public void setRejection(Rejection rejection) {
		this.rejection = rejection;
	}

	public List<ApprovalRound> getApprovalRounds() {
		return approvalRounds;
	}

	public void setApprovalRounds(List<ApprovalRound> approvalRounds) {
		this.approvalRounds = approvalRounds;
	}

	public ApprovalRound getLatestApprovalRound() {
		return latestApprovalRound;
	}

	public void setLatestApprovalRound(ApprovalRound latestApprovalRound) {
		this.latestApprovalRound = latestApprovalRound;
	}

	public RegisteredUser getApplicationAdministrator() {
		return applicationAdministrator;
	}

	public void setApplicationAdministrator(RegisteredUser applicationAdministrator) {
		this.applicationAdministrator = applicationAdministrator;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public Date getBatchDeadline() {
		return batchDeadline;
	}

	public void setBatchDeadline(Date batchDeadline) {
		this.batchDeadline = batchDeadline;
	}

}
