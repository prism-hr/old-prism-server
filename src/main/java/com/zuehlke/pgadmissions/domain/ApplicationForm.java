package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rejection_id")
    private Rejection rejection;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationFormStatus status = ApplicationFormStatus.UNSUBMITTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_status")
    private ApplicationFormStatus nextStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private ApplicationFormStatus previousStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "current_address_id")
    @Valid
    private Address currentAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_address_id")
    @Valid
    private Address contactAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id")
    private Document cv = null;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "accepted_terms")
    private Boolean acceptedTermsOnSubmission;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_statement_id")
    private Document personalStatement;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private RegisteredUser applicant = null;

    @Column(name = "project_title")
    private String projectTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_id")
    private Advert advert;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "personal_detail_id")
    private PersonalDetails personalDetails;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "programme_details_id")
    @Valid
    private ProgrammeDetails programmeDetails;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date")
    @JoinColumn(name = "application_form_id")
    private List<Comment> applicationComments = new ArrayList<Comment>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Qualification> qualifications = new ArrayList<Qualification>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Funding> fundings = new ArrayList<Funding>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Referee> referees = new ArrayList<Referee>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "additional_info_id")
    @Valid
    private AdditionalInformation additionalInformation;

    @Column(name = "reject_notification_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date rejectNotificationDate;

    @Column(name = "ip_address")
    private byte[] ipAddress;

    @Column(name = "ucl_booking_ref_number")
    private String uclBookingReferenceNumber;

    @OneToMany(mappedBy = "applicationForm")
    private List<ApplicationFormUserRole> applicationFormUserRoles = new ArrayList<ApplicationFormUserRole>();

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "use_custom_reference_questions")
    private Boolean useCustomReferenceQuestions = false;

    @OneToOne(mappedBy = "applicationForm", fetch = FetchType.LAZY)
    private ApplicationFormTransfer applicationFormTransfer;

    public List<Qualification> getQualifications() {
        return qualifications;
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

    public boolean hasInterviewEvaluationComment() {
        for (Comment comment : this.getApplicationComments()) {
            if (comment instanceof CompleteInterviewComment) {
                return true;
            }
        }
        return false;
    }

    public boolean hasReviewEvaluationComment() {
        for (Comment comment : this.getApplicationComments()) {
            if (comment instanceof CompleteReviewComment) {
                return true;
            }
        }
        return false;
    }

    public boolean isDecided() {
        if (status == ApplicationFormStatus.REJECTED || status == ApplicationFormStatus.APPROVED || status == ApplicationFormStatus.WITHDRAWN) {
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
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    public ProgrammeDetails getProgrammeDetails() {
        return programmeDetails;
    }

    public void setProgrammeDetails(ProgrammeDetails programmeDetails) {
        this.programmeDetails = programmeDetails;
    }

    public AdditionalInformation getAdditionalInformation() {
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
        if (cv != null) {
            cv.setIsReferenced(true);
        }
        this.cv = cv;
    }

    public Document getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(Document personalStatement) {
        if (personalStatement != null) {
            personalStatement.setIsReferenced(true);
        }
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

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
        if (advert != null) {
            Program program = null;
            Project project = advert.getProject();
            if (project != null) {
                program = project.getProgram();
            } else {
                program = advert.getProgram();
            }
            this.program = program;
            this.project = project;
        }
    }
    
    public Program getProgram() {
        return program;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }  

    public String getProjectTitle() {
        Project project = getProject();
        if (project != null) {
            return project.getTitle();
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

    public void setStatus(ApplicationFormStatus status) {
        // TODO move this code into ApplicationsService

        this.previousStatus = this.status;
        this.status = status;
        this.nextStatus = null;
    }

    public ApplicationFormStatus getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(ApplicationFormStatus nextStatus) {
        this.nextStatus = nextStatus;
    }

    public ApplicationFormStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ApplicationFormStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public Date getRejectNotificationDate() {
        return rejectNotificationDate;
    }

    public void setRejectNotificationDate(Date rejectNotificationDate) {
        this.rejectNotificationDate = rejectNotificationDate;
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

    public boolean hasAcceptedTheTerms() {
        return BooleanUtils.isTrue(acceptedTermsOnSubmission);
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getAcceptedTermsOnSubmission() {
        return acceptedTermsOnSubmission;
    }

    public void setAcceptedTermsOnSubmission(Boolean acceptedTerms) {
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

    public Rejection getRejection() {
        return rejection;
    }

    public void setRejection(Rejection rejection) {
        this.rejection = rejection;
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
        return Arrays
                .asList(ApplicationFormStatus.UNSUBMITTED, ApplicationFormStatus.VALIDATION, ApplicationFormStatus.REVIEW, ApplicationFormStatus.INTERVIEW)
                .contains(status);
    }

    public ApplicationFormTransfer getApplicationFormTransfer() {
        return applicationFormTransfer;
    }

    public void setApplicationFormTransfer(ApplicationFormTransfer applicationFormTransfer) {
        this.applicationFormTransfer = applicationFormTransfer;
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
        // FIXME should not rely on referees, move to service and count reference comments
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

    public String getProgramAndProjectTitle() {
        if (getProjectTitle() == null) {
            return getAdvert().getTitle();
        }
        String format = "%s (project: %s)";
        return String.format(format, getAdvert().getTitle(), getProjectTitle());
    }

    public String getProjectOrProgramTitle() {
        if (getProjectTitle() != null) {
            return getProjectTitle();
        }
        return getAdvert().getTitle();
    }

    public Integer getAverageRatingPercent() {
        return MathUtils.convertRatingToPercent(getAverageRating());
    }

    public String getAverageRatingFormatted() {
        return MathUtils.formatRating(getAverageRating());
    }

    public ValidationComment getValidationComment() {
        ValidationComment validationComment = null;
        for (Comment comment : getApplicationComments()) {
            if (comment instanceof ValidationComment) {
                validationComment = (ValidationComment) comment;
                break;
            }
        }
        return validationComment;
    }

    public OfferRecommendedComment getOfferRecommendedComment() {
        List<Comment> commentsReversed = Lists.reverse(getApplicationComments());
        OfferRecommendedComment offerRecommendedComment = null;
        for (Comment comment : commentsReversed) {
            if (comment instanceof OfferRecommendedComment) {
                offerRecommendedComment = (OfferRecommendedComment) comment;
            }
        }
        return offerRecommendedComment;
    }

    public Boolean getUseCustomReferenceQuestions() {
        return useCustomReferenceQuestions;
    }

    public boolean isUseCustomReferenceQuestions() {
        return useCustomReferenceQuestions;
    }

    public void setUseCustomReferenceQuestions(Boolean useCustomReferenceQuestions) {
        this.useCustomReferenceQuestions = useCustomReferenceQuestions;
    }

    public List<ApplicationFormUserRole> getApplicationFormUserRoles() {
        return applicationFormUserRoles;
    }

    public void setApplicationFormUserRoles(List<ApplicationFormUserRole> applicationFormUserRoles) {
        this.applicationFormUserRoles = applicationFormUserRoles;
    }

}
