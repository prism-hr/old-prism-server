package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Entity
@Table(name = "APPLICATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Application extends PrismResourceTransient {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "application_number", nullable = false)
    private String applicationNumber;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "previous_state_id", nullable = true)
    private State previousState;

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

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime submittedTimestamp;

    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

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
    @JoinColumn(name = "application_id")
    private List<Comment> applicationComments = new ArrayList<Comment>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id")
    @Valid
    private List<Qualification> qualifications = new ArrayList<Qualification>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id")
    @Valid
    private List<Funding> fundings = new ArrayList<Funding>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id")
    @Valid
    private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id")
    @Valid
    private List<Referee> referees = new ArrayList<Referee>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_additional_information_id")
    @Valid
    private AdditionalInformation additionalInformation;

    @Transient
    private Boolean acceptedTerms;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
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

    public Boolean getAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getSubmittedTimestamp() {
        return submittedTimestamp;
    }

    public void setSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public Advert getAdvert() {
        return Objects.firstNonNull(getProject(), getProgram());
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

    public Application withId(Integer id) {
        this.id = id;
        return this;
    }

    public Application withApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
        return this;
    }
    
    public Application withSystem(System system) {
        this.system = system;
        return this;
    }
    
    public Application withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public Application withProgram(Program program) {
        this.program = program;
        return this;
    }

    public Application withUser(User user) {
        this.user = user;
        return this;
    }

    public Application withState(State state) {
        this.state = state;
        return this;
    }

    public Application withDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }
    
    public Application withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Application withSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
        return this;
    }

    public Application withPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
        return this;
    }

    public Application withProgramDetails(ProgramDetails programDetails) {
        this.programDetails = programDetails;
        return this;
    }

    public Application withAddress(ApplicationAddress applicationAddress) {
        this.applicationAddress = applicationAddress;
        return this;
    }

    public Application withDocument(ApplicationDocument applicationDocument) {
        this.applicationDocument = applicationDocument;
        return this;
    }

    public Application withAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public Application withAcceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
        return this;
    }

    public Application withReferees(Referee... referees) {
        this.referees.addAll(Arrays.asList(referees));
        return this;
    }

    @Override
    public System getSystem() {
        return getInstitution().getSystem();
    }

    @Override
    public void setSystem(System system) {
        this.system = system;
    }

    @Override
    public Institution getInstitution() {
        return getProgram().getInstitution();
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public State getPreviousState() {
        return previousState;
    }

    @Override
    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public Application getApplication() {
        return this;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public UniqueResourceSignature getUniqueResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("user", user);
        properties.put("program", program);
        if (project != null) {
            properties.put("project", project);
        }
        propertiesWrapper.add(properties);
        HashMap<String, Object> exclusions = Maps.newHashMap();   
        exclusions.put("state.id", PrismState.APPLICATION_APPROVED_COMPLETED);
        exclusions.put("state.id", PrismState.APPLICATION_REJECTED_COMPLETED);
        exclusions.put("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED);
        return new UniqueResourceSignature(propertiesWrapper, exclusions);
    }

}
