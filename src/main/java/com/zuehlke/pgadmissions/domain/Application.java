package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.*;
import javax.validation.Valid;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Entity
@Table(name = "APPLICATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Application extends PrismResourceDynamic {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "code", unique = true)
    private String code;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
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
    
    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_personal_detail_id", unique = true)
    private ApplicationPersonalDetails applicationPersonalDetails;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_program_detail_id", unique = true)
    @Valid
    private ApplicationProgramDetails applicationProgramDetails;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_address_id", unique = true)
    @Valid
    private ApplicationAddress applicationAddress;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    @Valid
    private List<ApplicationQualification> applicationQualifications = new ArrayList<ApplicationQualification>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    @Valid
    private List<ApplicationEmploymentPosition> applicationEmploymentPositions = new ArrayList<ApplicationEmploymentPosition>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    @Valid
    private List<ApplicationFunding> applicationFundings = new ArrayList<ApplicationFunding>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    @Valid
    private List<ApplicationReferee> applicationReferees = new ArrayList<ApplicationReferee>();
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_document_id", unique = true)
    @Valid
    private ApplicationDocument applicationDocument;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_additional_information_id", unique = true)
    @Valid
    private ApplicationAdditionalInformation additionalInformation;
    
    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime submittedTimestamp;
    
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_state_id")
    private State previousState;

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;

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

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
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

    @Override
    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
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

    public ApplicationPersonalDetails getPersonalDetails() {
        return applicationPersonalDetails;
    }

    public void setPersonalDetails(ApplicationPersonalDetails personalDetails) {
        this.applicationPersonalDetails = personalDetails;
    }

    public ApplicationProgramDetails getProgramDetails() {
        return applicationProgramDetails;
    }

    public void setProgramDetails(ApplicationProgramDetails programDetails) {
        this.applicationProgramDetails = programDetails;
    }

    public ApplicationAdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(ApplicationAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public List<ApplicationQualification> getApplicationQualifications() {
        return applicationQualifications;
    }

    public List<ApplicationFunding> getApplicationFundings() {
        return applicationFundings;
    }

    public List<ApplicationEmploymentPosition> getApplicationEmploymentPositions() {
        return applicationEmploymentPositions;
    }

    public List<ApplicationReferee> getApplicationReferees() {
        return applicationReferees;
    }

    public Application withId(Integer id) {
        this.id = id;
        return this;
    }

    public Application withCode(String code) {
        this.code = code;
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

    public Application withPersonalDetails(ApplicationPersonalDetails personalDetails) {
        this.applicationPersonalDetails = personalDetails;
        return this;
    }

    public Application withProgramDetails(ApplicationProgramDetails programDetails) {
        this.applicationProgramDetails = programDetails;
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

    public Application withAdditionalInformation(ApplicationAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public Application withAcceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
        return this;
    }

    public Application withReferees(ApplicationReferee... referees) {
        this.applicationReferees.addAll(Arrays.asList(referees));
        return this;
    }

    public Application withQualifications(ApplicationQualification... qualifications) {
        this.applicationQualifications.addAll(Arrays.asList(qualifications));
        return this;
    }

    public Application withInitialData(User user, Program program, Project project) {
        this.user = Preconditions.checkNotNull(user);
        this.program = Preconditions.checkNotNull(program);
        this.project = project;
        this.createdTimestamp = new DateTime();
        return this;
    }

    @Override
    public System getSystem() {
        return system;
    }

    @Override
    public void setSystem(System system) {
        this.system = system;
    }

    @Override
    public Institution getInstitution() {
        return institution;
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
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("user", user);
        properties.put("program", program);
        properties.put("project", project);
        propertiesWrapper.add(properties);
        HashMultimap<String, Object> exclusions = HashMultimap.create();
        exclusions.put("state.id", PrismState.APPLICATION_APPROVED_COMPLETED);
        exclusions.put("state.id", PrismState.APPLICATION_REJECTED_COMPLETED);
        exclusions.put("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED);
        return new ResourceSignature(propertiesWrapper, exclusions);
    }

    @Override
    public String generateCode() {
        return program.getCode() + "-" + getCreatedTimestamp().getYear() + "-" + String.format("%010d", getId());
    }

    @Override
    public LocalDate getDueDateBaseline() {
        LocalDate dueDateBaseline = new LocalDate();
        if (state.getParentState().getId() == PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK && closingDate != null && closingDate.isAfter(dueDateBaseline)) {
            return closingDate;
        }
        return dueDateBaseline;
    }

}
