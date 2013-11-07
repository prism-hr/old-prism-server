package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.utils.MathUtils;

@Entity(name = "APPLICATION_FORM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationForm implements Comparable<ApplicationForm>, FormSectionObject, Serializable {

    private static final long serialVersionUID = -7671357234815343496L;

    @Id
    @GeneratedValue
    private Integer id;

    @Transient
    private boolean acceptedTerms;

    @Column(name = "application_number")
    private String applicationNumber;

    /**
     * This field indicates that user is application interview organiser.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_administrator_id")
    private RegisteredUser applicationAdministrator;

    @OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "rejection_id")
    private Rejection rejection;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "application_form_id")
    private List<NotificationRecord> notificationRecords = new ArrayList<NotificationRecord>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "application_form_id")
    private List<Event> events = new ArrayList<Event>();

    @Enumerated(EnumType.STRING)
    private ApplicationFormStatus status = ApplicationFormStatus.UNSUBMITTED;

    @OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "current_address_id")
    @Valid
    private Address currentAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "contact_address_id")
    @Valid
    private Address contactAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id")
    private Document cv = null;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "accepted_terms")
    @Enumerated(EnumType.STRING)
    private CheckedStatus acceptedTermsOnSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private RegisteredUser applicant = null;

    @Column(name = "project_title")
    private String projectTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
    @Valid
    private PersonalDetails personalDetails;

    @OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
    @Valid
    private ProgrammeDetails programmeDetails;

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "application_form_id")
    private List<ReviewRound> reviewRounds = new ArrayList<ReviewRound>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "application_form_id")
    private List<ApprovalRound> approvalRounds = new ArrayList<ApprovalRound>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @OrderBy("createdDate desc")
    @JoinColumn(name = "application_form_id")
    private List<Interview> interviews = new ArrayList<Interview>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "application")
    private List<Comment> applicationComments = new ArrayList<Comment>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Qualification> qualifications = new ArrayList<Qualification>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Funding> fundings = new ArrayList<Funding>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Referee> referees = new ArrayList<Referee>();

    @OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private AdditionalInformation additionalInformation;

    @Column(name = "reject_notification_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date rejectNotificationDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "latest_interview_id")
    private Interview latestInterview;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "latest_approval_round_id")
    private ApprovalRound latestApprovalRound;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "latest_review_round_id")
    private ReviewRound latestReviewRound;

    @Column(name = "ip_address")
    private byte[] ipAddress;

    @Column(name = "ucl_booking_ref_number")
    private String uclBookingReferenceNumber;

    @Column(name = "is_editable_by_applicant")
    private Boolean isEditableByApplicant = true;

    @Column(name = "suppress_state_change_notifications")
    private Boolean suppressStateChangeNotifications = false;

    @Column(name = "withdrawn_before_submit")
    private Boolean withdrawnBeforeSubmit = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @OneToMany(mappedBy = "applicationForm", fetch = FetchType.LAZY)
    private List<ApplicationFormUserRole> applicationFormUserRoles = new ArrayList<ApplicationFormUserRole>();

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    public List<Qualification> getQualifications() {
        return qualifications;
    }

    public Boolean getSuppressStateChangeNotifications() {
        return suppressStateChangeNotifications;
    }

    public void setSuppressStateChangeNotifications(Boolean suppressStateChangeNotifications) {
        this.suppressStateChangeNotifications = suppressStateChangeNotifications;
    }

    public void setQualifications(List<Qualification> qualifications) {
        this.qualifications.clear();
        this.qualifications.addAll(qualifications);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public RegisteredUser getApplicant() {
        return applicant;
    }

    public void setApplicant(RegisteredUser user) {
        this.applicant = user;
    }

    public Date getApplicationTimestamp() {
        return applicationTimestamp;
    }

    public void setApplicationTimestamp(Date applicationTimestamp) {
        this.applicationTimestamp = applicationTimestamp;
    }

    public boolean isModifiable() {
        if (status == ApplicationFormStatus.REJECTED || status == ApplicationFormStatus.APPROVED || status == ApplicationFormStatus.WITHDRAWN
                || !getIsEditableByApplicant()) {
            return false;
        }
        return true;
    }

    public boolean isSubmitted() {
        return status != ApplicationFormStatus.UNSUBMITTED;
    }

    public InterviewEvaluationComment getLastInterviewEvaluationComment() {
        for (int i = applicationComments.size() - 1; i >= 0; i--) {
            if (applicationComments.get(i) instanceof InterviewEvaluationComment) {
                return (InterviewEvaluationComment) applicationComments.get(i);
            }
        }
        return null;
    }

    public boolean hasInterviewEvaluationComment() {
        for (Comment comment : this.getApplicationComments()) {
            if (comment instanceof InterviewEvaluationComment) {
                return true;
            }
        }
        return false;
    }

    public boolean hasReviewEvaluationComment() {
        for (Comment comment : this.getApplicationComments()) {
            if (comment instanceof ReviewEvaluationComment) {
                return true;
            }
        }
        return false;
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

    public boolean isTerminated() {
        return isDecided() || isWithdrawn();
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

    public boolean isPersonalDetailsNull() {
        return personalDetails == null;
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
        ArrayList<Comment> returnList = new ArrayList<Comment>();
        if (user.isInRole(Authority.APPLICANT)) {
            for (Comment comment : applicationComments) {
                if (comment instanceof InterviewVoteComment && comment.getUser().getId().equals(user.getId())) {
                    returnList.add(comment);
                } else if (comment instanceof InterviewScheduleComment) {
                    returnList.add(comment);
                }
            }
        }

        if (user.isRefereeOfApplicationForm(this) && !user.hasStaffRightsOnApplicationForm(this)) {
            for (Comment comment : applicationComments) {
                if (comment instanceof ReferenceComment && ((ReferenceComment) comment).getReferee().getUser().getId().equals(user.getId())) {
                    returnList.add(comment);
                }
            }
            Collections.sort(returnList);
            return returnList;
        }

        if (user.hasStaffRightsOnApplicationForm(this) || user.isApplicationAdministrator(this) || user.isViewerOfProgramme(this, user)
                || user.isInRole(Authority.ADMITTER)) {
            returnList.addAll(applicationComments);
            Collections.sort(returnList);
            return returnList;
        }

        return returnList;
    }

    public boolean shouldOpenFirstSection() {
        return isNull(programmeDetails, personalDetails, currentAddress, contactAddress, personalStatement, cv, additionalInformation)
                && isEmpty(fundings, referees, employmentPositions, qualifications);
    }

    private boolean isEmpty(List<?>... objects) {
        for (List<?> obj : objects) {
            if (obj.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return true;
            }
        }
        return false;
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
        if (getProject() != null) {
            return getProject().getAdvert().getTitle();
        } else {
            return projectTitle;
        }
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

    public boolean isInReviewStage() {
        return status == ApplicationFormStatus.REVIEW;
    }

    public boolean isInInterviewStage() {
        return status == ApplicationFormStatus.INTERVIEW;
    }

    public boolean isInApprovalStage() {
        return status == ApplicationFormStatus.APPROVAL;
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

    public boolean addNotificationRecord(NotificationRecord record) {
        for (NotificationRecord existingRecord : notificationRecords) {
            if (existingRecord.getNotificationType() == record.getNotificationType()) {
                existingRecord.setDate(record.getDate());
                existingRecord.setUser(record.getUser());
                return false;
            }
        }
        return notificationRecords.add(record);
    }

    public boolean removeNotificationRecord(NotificationRecord record) {
        return notificationRecords.remove(record);
    }

    public void removeNotificationRecord(final NotificationType... recordTypes) {
        CollectionUtils.filter(notificationRecords, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                NotificationRecord existingRecord = (NotificationRecord) object;
                for (NotificationType type : recordTypes) {
                    if (type == existingRecord.getNotificationType()) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    public boolean hasAcceptedTheTerms() {
        return acceptedTermsOnSubmission == CheckedStatus.YES;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public CheckedStatus getAcceptedTermsOnSubmission() {
        return acceptedTermsOnSubmission;
    }

    public void setAcceptedTermsOnSubmission(CheckedStatus acceptedTerms) {
        this.acceptedTermsOnSubmission = acceptedTerms;
    }

    public boolean isInState(String strStatus) {
        try {
            return status == ApplicationFormStatus.valueOf(strStatus);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isInState(ApplicationFormStatus enumStatus) {
        return status == enumStatus;
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
        ArrayList<RegisteredUser> usersWillingToInterview = new ArrayList<RegisteredUser>();
        for (Comment comment : applicationComments) {
            if (comment instanceof ReviewComment) {
                ReviewComment reviewComment = (ReviewComment) comment;
                if (BooleanUtils.isTrue(reviewComment.getWillingToInterview())) {
                    usersWillingToInterview.add(reviewComment.getUser());
                }
            }
        }
        return usersWillingToInterview;
    }

    public List<RegisteredUser> getReviewersWillingToWorkWithApplicant() {
        ArrayList<RegisteredUser> usersWillingToWorkWithApplicant = new ArrayList<RegisteredUser>();
        for (Comment comment : applicationComments) {
            if (comment instanceof ReviewComment) {
                ReviewComment reviewComment = (ReviewComment) comment;
                if (BooleanUtils.isTrue(reviewComment.getWillingToWorkWithApplicant())) {
                    usersWillingToWorkWithApplicant.add(reviewComment.getUser());
                }
            }
        }
        return usersWillingToWorkWithApplicant;
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
        ArrayList<StateChangeEvent> stateChangeEvents = new ArrayList<StateChangeEvent>();
        Comparator<StateChangeEvent> dateComparator = new Comparator<StateChangeEvent>() {
            @Override
            public int compare(StateChangeEvent event1, StateChangeEvent event2) {
                return event1.getDate().compareTo(event2.getDate());
            }
        };
        for (Event event : events) {
            if (event instanceof StateChangeEvent) {
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

    public String getUclBookingReferenceNumber() {
        return uclBookingReferenceNumber;
    }

    public void setUclBookingReferenceNumber(String uclBookingReferenceNumber) {
        this.uclBookingReferenceNumber = uclBookingReferenceNumber;
    }

    public Date getBatchDeadline() {
        return batchDeadline;
    }

    public void setBatchDeadline(Date batchDeadline) {
        this.batchDeadline = batchDeadline;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    @Override
    public ApplicationForm getApplication() {
        return this;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(byte[] ipAddress) {
        this.ipAddress = Arrays.copyOf(ipAddress, ipAddress.length);
    }

    public String getIpAddressAsString() {
        try {
            return InetAddress.getByAddress(ipAddress).getHostAddress();
        } catch (UnknownHostException e) {
            return StringUtils.EMPTY;
        }
    }

    public void setIpAddressAsString(String ipAddress) throws UnknownHostException {
        this.ipAddress = InetAddress.getByName(ipAddress).getAddress();
    }

    public Boolean getIsEditableByApplicant() {
        return isEditableByApplicant;
    }

    public void setIsEditableByApplicant(Boolean isEditableByApplicant) {
        this.isEditableByApplicant = isEditableByApplicant;
    }

    public RequestRestartComment getLatestsRequestRestartComment() {
        List<RequestRestartComment> requestRestartComments = new ArrayList<RequestRestartComment>();
        for (Comment comment : applicationComments) {
            if (comment instanceof RequestRestartComment) {
                requestRestartComments.add((RequestRestartComment) comment);
            }
        }
        Collections.sort(requestRestartComments);
        if (!requestRestartComments.isEmpty()) {
            return requestRestartComments.get(0);
        }
        return null;
    }

    public ApplicationFormStatus getOutcomeOfStage() {
        if (ApplicationFormStatus.REVIEW == status) {
            return resolveOutcomeOfStageForReviewStatus();
        }
        if (ApplicationFormStatus.INTERVIEW == status) {
            return resolveOutcomeOfStageForInterviewStatus();
        }
        if (ApplicationFormStatus.APPROVAL == status) {
            return resolveOutcomeOfStageForApprovalStatus();
        }
        if (!approvalRounds.isEmpty()) {
            return ApplicationFormStatus.APPROVAL;
        }
        if (!interviews.isEmpty()) {
            return ApplicationFormStatus.INTERVIEW;
        }
        if (!reviewRounds.isEmpty()) {
            return ApplicationFormStatus.REVIEW;
        }
        return ApplicationFormStatus.VALIDATION;
    }

    private ApplicationFormStatus resolveOutcomeOfStageForApprovalStatus() {
        if (approvalRounds.size() > 1) {
            return ApplicationFormStatus.APPROVAL;
        }
        if (!interviews.isEmpty()) {
            return ApplicationFormStatus.INTERVIEW;
        }
        if (!reviewRounds.isEmpty()) {
            return ApplicationFormStatus.REVIEW;
        }
        return ApplicationFormStatus.VALIDATION;
    }

    private ApplicationFormStatus resolveOutcomeOfStageForInterviewStatus() {
        if (interviews.size() > 1) {
            return ApplicationFormStatus.INTERVIEW;
        }
        if (!reviewRounds.isEmpty()) {
            return ApplicationFormStatus.REVIEW;
        }
        return ApplicationFormStatus.VALIDATION;
    }

    private ApplicationFormStatus resolveOutcomeOfStageForReviewStatus() {
        if (reviewRounds.size() > 1) {
            return ApplicationFormStatus.REVIEW;
        }

        return ApplicationFormStatus.VALIDATION;
    }

    public List<Document> getQualificationsToSendToPortico() {
        List<Document> result = new ArrayList<Document>(2);
        for (Qualification qualification : getQualifications()) {
            if (BooleanUtils.isTrue(qualification.getSendToUCL())) {
                result.add(qualification.getProofOfAward());
            }
        }
        return result;
    }

    public List<Integer> getQualicationsToSendToPorticoIds() {
        List<Integer> qualificationIdsToSend = new ArrayList<Integer>();
        for (int i = 0; i < getQualifications().size(); i++) {
            if (getQualifications().get(i).getSendToUCL()) {
                qualificationIdsToSend.add(getQualifications().get(i).getId());
            }
        }
        if (qualificationIdsToSend.isEmpty()) {
            return null;
        }
        return qualificationIdsToSend;
    }

    public boolean hasQualificationsWithTranscripts() {
        for (Qualification qualification : getQualifications()) {
            if (qualification.getProofOfAward() != null) {
                return true;
            }
        }
        return false;
    }

    public List<ReferenceComment> getReferencesToSendToPortico() {
        List<ReferenceComment> result = new ArrayList<ReferenceComment>(2);
        for (Referee referee : getReferees()) {
            if (BooleanUtils.isTrue(referee.getSendToUCL())) {
                result.add(referee.getReference());
            }
        }
        return result;
    }

    public List<Referee> getRefereesToSendToPortico() {
        List<Referee> result = new ArrayList<Referee>(2);
        for (Referee referee : getReferees()) {
            if (BooleanUtils.isTrue(referee.getSendToUCL())) {
                result.add(referee);
            }
        }
        return result;
    }

    public List<Integer> getRefereesToSendToPorticoIds() {
        List<Integer> refereeIdsToSend = new ArrayList<Integer>();
        for (int i = 0; i < getReferees().size(); i++) {
            if (getReferees().get(i).getSendToUCL()) {
                refereeIdsToSend.add(getReferees().get(i).getId());
            }
        }
        if (refereeIdsToSend.isEmpty()) {
            return null;
        }
        return refereeIdsToSend;
    }

    public boolean hasEnoughReferencesToSendToPortico() {
        if (getReferencesToSendToPortico().size() == 2) {
            return true;
        }
        return false;
    }

    public boolean hasEnoughQualificationsToSendToPortico() {
        if (getQualificationsToSendToPortico().size() > 0 && getQualificationsToSendToPortico().size() <= 2) {
            return true;
        }
        return false;
    }

    public Boolean getWithdrawnBeforeSubmit() {
        return withdrawnBeforeSubmit;
    }

    public void setWithdrawnBeforeSubmit(final Boolean withdrawnBeforeSubmit) {
        this.withdrawnBeforeSubmit = withdrawnBeforeSubmit;
    }

    public boolean isNotInState(ApplicationFormStatus status) {
        return !isInState(status);
    }

    public boolean hasConfirmElegibilityComment() {
        for (Comment comment : applicationComments) {
            if (comment instanceof AdmitterComment) {
                return true;
            }
        }
        return false;
    }

    public boolean isDueDateExpired() {
        if (dueDate == null) {
            return false;
        }
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        return today.after(dueDate);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getProgramAndProjectTitle() {
        if (getProjectTitle() == null) {
            return getProgram().getTitle();
        }
        String format = "%s (project: %s)";
        return String.format(format, getProgram().getTitle(), getProjectTitle());
    }

    public String getProjectOrProgramTitle() {
        if (getProjectTitle() != null) {
            return getProjectTitle();
        }
        return getProgram().getTitle();
    }

    public StateChangeComment getLatestStateChangeComment() {
        StateChangeComment stateChangeComment = null;
        switch (getStatus()) {
        case APPROVAL:
            stateChangeComment = getEvaluationCommentForLatestApprovalRound();
            break;
        case REVIEW:
            stateChangeComment = getEvaluationCommentForLatestRoundOfReview();
            break;
        case VALIDATION:
            stateChangeComment = getValidationComment();
            break;
        case INTERVIEW:
            stateChangeComment = getEvaluationCommentForLatestInterview();
            break;
        default:
        }
        if (stateChangeComment != null) {
            return stateChangeComment;
        }
        return null;
    }

    public ValidationComment getValidationComment() {
        List<Comment> applicationComments = getApplicationCommentsReversed();
        for (Comment comment : applicationComments) {
            if (comment instanceof ValidationComment && comment.getType() != CommentType.ADMITTER_COMMENT) {
                return (ValidationComment) comment;
            }
        }
        return null;
    }

    public ApplicationFormStatus getNextStatus() {
        StateChangeComment latestStateChangeComment = getLatestStateChangeComment();
        if (latestStateChangeComment != null) {
            return latestStateChangeComment.getNextStatus();
        }
        return null;
    }

    public Integer getAverageRatingPercent() {
        return MathUtils.convertRatingToPercent(getAverageRating());
    }

    public String getAverageRatingFormatted() {
        return MathUtils.formatRating(getAverageRating());
    }

    private List<Comment> getApplicationCommentsReversed() {
        return Lists.reverse(getApplicationComments());
    }

    private ReviewEvaluationComment getEvaluationCommentForLatestRoundOfReview() {
        ReviewRound latestReviewRound = getLatestReviewRound();
        if (latestReviewRound != null) {
            Integer latestReviewRoundId = latestReviewRound.getId();
            for (Comment comment : getApplicationCommentsReversed()) {
                if (comment instanceof ReviewEvaluationComment) {
                    ReviewEvaluationComment reviewEvaluationComment = (ReviewEvaluationComment) comment;
                    Integer reviewEvaluationCommentId = reviewEvaluationComment.getReviewRound().getId();
                    if (latestReviewRoundId.equals(reviewEvaluationCommentId)) {
                        return reviewEvaluationComment;
                    }
                }
            }
        }
        return null;
    }

    private ApprovalEvaluationComment getEvaluationCommentForLatestApprovalRound() {
        ApprovalRound latestApprovalRound = getLatestApprovalRound();
        if (latestApprovalRound != null) {
            Integer latestApprovalRoundId = latestApprovalRound.getId();
            for (Comment comment : getApplicationCommentsReversed()) {
                if (comment instanceof ApprovalEvaluationComment) {
                    ApprovalEvaluationComment approvalEvaluationComment = (ApprovalEvaluationComment) comment;
                    Integer approvalEvaluationCommentId = approvalEvaluationComment.getApprovalRound().getId();
                    if (latestApprovalRoundId.equals(approvalEvaluationCommentId)) {
                        return approvalEvaluationComment;
                    }
                }
            }
        }
        return null;
    }

    private InterviewEvaluationComment getEvaluationCommentForLatestInterview() {
        Interview latestInterview = getLatestInterview();
        if (latestInterview != null) {
            Integer latestInterviewId = latestInterview.getId();
            for (Comment comment : getApplicationCommentsReversed()) {
                if (comment instanceof InterviewEvaluationComment) {
                    InterviewEvaluationComment interviewEvaluationComment = (InterviewEvaluationComment) comment;
                    Integer interviewEvaluationCommentId = interviewEvaluationComment.getInterview().getId();
                    if (latestInterviewId.equals(interviewEvaluationCommentId)) {
                        return interviewEvaluationComment;
                    }
                }
            }
        }
        return null;
    }

}
