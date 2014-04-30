package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "APPLICATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationForm implements Comparable<ApplicationForm>, Serializable, PrismScope {

    private static final long serialVersionUID = -7671357234815343496L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "application_number")
    private String applicationNumber;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rejection_id")
    private Rejection rejection;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_address_id")
    @Valid
    private ApplicationAddress applicationAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_document_id")
    @Valid
    private ApplicationDocument applicationDocument;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "created_timestamp", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    @Column(name = "submitted_timestamp")
    private Date submittedTimestamp;

    @Column(name = "closing_date")
    private Date closingDate = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User applicant = null;

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
    @JoinColumn(name = "application_personal_detail_id")
    private PersonalDetails personalDetails;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_program_detail_id")
    @Valid
    private ProgramDetails programDetails;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date")
    @JoinColumn(name = "application_form_id")
    private List<Comment> applicationComments = new ArrayList<Comment>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Qualification> qualifications = new ArrayList<Qualification>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Funding> fundings = new ArrayList<Funding>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_form_id")
    @Valid
    private List<Referee> referees = new ArrayList<Referee>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_additional_information_id")
    @Valid
    private AdditionalInformation additionalInformation;

    @Column(name = "reject_notification_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date rejectNotificationDate;

    @Column(name = "submitted_ip_address")
    private byte[] submittedIpAddress;

    @Column(name = "ucl_booking_ref_number")
    private String uclBookingReferenceNumber;

    @Column(name = "use_custom_reference_questions")
    private Boolean useCustomReferenceQuestions = false;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "applicationForm")
    private ApplicationFormTransfer applicationFormTransfer;

    @Transient
    private boolean acceptedTerms;

    @Transient
    private Boolean acceptedTermsOnSubmission;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public Rejection getRejection() {
        return rejection;
    }

    public void setRejection(Rejection rejection) {
        this.rejection = rejection;
    }

    public State getState() {
        return state;
    }

    public void setState(State status) {
        this.state = status;
    }

    public ApplicationAddress getApplicationAddress() {
        return applicationAddress;
    }

    public void setApplicationAddress(ApplicationAddress applicationAddress) {
        this.applicationAddress = applicationAddress;
    }

    public ApplicationDocument getApplicationDocument() {
        return applicationDocument;
    }

    public void setApplicationDocument(ApplicationDocument applicationDocument) {
        this.applicationDocument = applicationDocument;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getAcceptedTermsOnSubmission() {
        return acceptedTermsOnSubmission;
    }

    public void setAcceptedTermsOnSubmission(Boolean acceptedTermsOnSubmission) {
        this.acceptedTermsOnSubmission = acceptedTermsOnSubmission;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getSubmittedTimestamp() {
        return submittedTimestamp;
    }

    public void setSubmittedTimestamp(Date submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
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

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    public ProgramDetails getProgramDetails() {
        return programDetails;
    }

    public void setProgramDetails(ProgramDetails programDetails) {
        this.programDetails = programDetails;
    }

    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Date getRejectNotificationDate() {
        return rejectNotificationDate;
    }

    public void setRejectNotificationDate(Date rejectNotificationDate) {
        this.rejectNotificationDate = rejectNotificationDate;
    }

    public byte[] getSubmittedIpAddress() {
        return submittedIpAddress;
    }

    public void setSubmittedIpAddress(byte[] ipAddress) {
        this.submittedIpAddress = ipAddress;
    }

    public String getUclBookingReferenceNumber() {
        return uclBookingReferenceNumber;
    }

    public void setUclBookingReferenceNumber(String uclBookingReferenceNumber) {
        this.uclBookingReferenceNumber = uclBookingReferenceNumber;
    }

    public Boolean getUseCustomReferenceQuestions() {
        return useCustomReferenceQuestions;
    }

    public void setUseCustomReferenceQuestions(Boolean useCustomReferenceQuestions) {
        this.useCustomReferenceQuestions = useCustomReferenceQuestions;
    }

    public ApplicationFormTransfer getApplicationFormTransfer() {
        return applicationFormTransfer;
    }

    public void setApplicationFormTransfer(ApplicationFormTransfer applicationFormTransfer) {
        this.applicationFormTransfer = applicationFormTransfer;
    }

    public List<Comment> getApplicationComments() {
        return applicationComments;
    }

    public List<Qualification> getQualifications() {
        return qualifications;
    }

    public List<Funding> getFundings() {
        return fundings;
    }

    public List<EmploymentPosition> getEmploymentPositions() {
        return employmentPositions;
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public String getIpAddressAsString() {
        try {
            return InetAddress.getByAddress(submittedIpAddress).getHostAddress();
        } catch (UnknownHostException e) {
            return StringUtils.EMPTY;
        }
    }

    public void setIpAddressAsString(String ipAddress) throws UnknownHostException {
        this.submittedIpAddress = InetAddress.getByName(ipAddress).getAddress();
    }

    @Override
    public int compareTo(ApplicationForm appForm) {
        if (appForm.getSubmittedTimestamp() != null && this.getSubmittedTimestamp() == null) {
            return -1;
        }
        if (appForm.getSubmittedTimestamp() == null && this.getSubmittedTimestamp() != null) {
            return 1;
        }
        if (appForm.getSubmittedTimestamp() == null && this.getSubmittedTimestamp() == null) {
            return this.createdTimestamp.compareTo(appForm.getCreatedTimestamp());
        }
        return this.submittedTimestamp.compareTo(appForm.getSubmittedTimestamp());
    }

}
