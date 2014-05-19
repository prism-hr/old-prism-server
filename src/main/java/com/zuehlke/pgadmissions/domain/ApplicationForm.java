package com.zuehlke.pgadmissions.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.base.Objects;

@Entity
@Table(name = "APPLICATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationForm extends PrismScope {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "application_number", nullable = false)
    private String applicationNumber;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rejection_id")
    private Rejection rejection;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_address_id")
    @Valid
    private ApplicationAddress applicationAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_document_id")
    @Valid
    private ApplicationDocument applicationDocument;

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @Column(name = "created_timestamp", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    @Column(name = "submitted_timestamp")
    private Date submittedTimestamp;

    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_transfer_id")
    private ApplicationTransfer transfer;

    @Transient
    private Boolean acceptedTerms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
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

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User applicant) {
        this.user = applicant;
    }

    public Advert getAdvert() {
        return Objects.firstNonNull(getProject(), getProgram());
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

    public ApplicationTransfer getTransfer() {
        return transfer;
    }

    public void setTransfer(ApplicationTransfer transfer) {
        this.transfer = transfer;
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

    public ApplicationForm withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationForm withApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
        return this;
    }

    public ApplicationForm withProgram(Program program) {
        this.program = program;
        return this;
    }

    public ApplicationForm withUser(User user) {
        this.user = user;
        return this;
    }

    public ApplicationForm withState(State state) {
        this.state = state;
        return this;
    }

    public ApplicationForm withDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public ApplicationForm withSubmittedTimestamp(Date submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
        return this;
    }

    public ApplicationForm withPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
        return this;
    }

    public ApplicationForm withProgramDetails(ProgramDetails programDetails) {
        this.programDetails = programDetails;
        return this;
    }

    public ApplicationForm withAddress(ApplicationAddress applicationAddress) {
        this.applicationAddress = applicationAddress;
        return this;
    }

    public ApplicationForm withDocument(ApplicationDocument applicationDocument) {
        this.applicationDocument = applicationDocument;
        return this;
    }

    public ApplicationForm withAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public ApplicationForm withAcceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
        return this;
    }

    public ApplicationForm withReferees(Referee... referees) {
        this.referees.addAll(Arrays.asList(referees));
        return this;
    }

    @Override
    public String getScopeName() {
        return "application";
    }

    @Override
    public PrismSystem getSystem() {
        return getInstitution().getSystem();
    }

    @Override
    public Institution getInstitution() {
        return getProgram().getInstitution();
    }
    
    @Override
    public ApplicationForm getApplication() {
        return this;
    }

}
