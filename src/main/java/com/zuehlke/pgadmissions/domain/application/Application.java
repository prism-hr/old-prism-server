package com.zuehlke.pgadmissions.domain.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.IMMEDIATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.LocaleUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "APPLICATION")
public class Application extends Resource {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "code_legacy")
    private String codeLegacy;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "referrer")
    private String referrer;

    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_program_detail_id", unique = true)
    private ApplicationProgramDetail programDetail;

    @Column(name = "previous_application")
    private Boolean previousApplication;

    @Embedded
    private ApplicationStudyDetail studyDetail;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationSupervisor> supervisors = Sets.newHashSet();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_personal_detail_id", unique = true)
    private ApplicationPersonalDetail personalDetail;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_address_id", unique = true)
    private ApplicationAddress address;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationQualification> qualifications = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationEmploymentPosition> employmentPositions = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationFunding> fundings = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationPrize> prizes = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationReferee> referees = Sets.newHashSet();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_document_id", unique = true)
    private ApplicationDocument document;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_additional_information_id", unique = true)
    private ApplicationAdditionalInformation additionalInformation;

    @Column(name = "primary_theme")
    private String primaryTheme;

    @Column(name = "secondary_theme")
    private String secondaryTheme;

    @Column(name = "application_rating_count")
    private Integer applicationRatingCount;

    @Column(name = "application_rating_average")
    private BigDecimal applicationRatingAverage;

    @Column(name = "completion_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate completionDate;

    @Column(name = "confirmed_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate confirmedStartDate;

    @Column(name = "confirmed_offer_type")
    @Enumerated(EnumType.STRING)
    private PrismOfferType confirmedOfferType;

    @Column(name = "retain", nullable = false)
    private Boolean retain;

    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime submittedTimestamp;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
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

    @Column(name = "last_reminded_request_individual")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastRemindedRequestIndividual;

    @Column(name = "last_reminded_request_syndicated")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastRemindedRequestSyndicated;

    @Column(name = "last_notified_update_syndicated")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastNotifiedUpdateSyndicated;

    @Column(name = "workflow_property_configuration_version")
    private Integer workflowPropertyConfigurationVersion;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @OneToMany(mappedBy = "application")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<ApplicationProcessing> processings = Sets.newHashSet();

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

    public final String getCodeLegacy() {
        return codeLegacy;
    }

    public final void setCodeLegacy(String codeLegacy) {
        this.codeLegacy = codeLegacy;
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
    public PrismLocale getLocale() {
        return program.getLocale();
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
    public Application getApplication() {
        return this;
    }

    @Override
    public String getReferrer() {
        return referrer;
    }

    @Override
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public ApplicationAddress getAddress() {
        return address;
    }

    public void setAddress(ApplicationAddress address) {
        this.address = address;
    }

    public ApplicationDocument getDocument() {
        return document;
    }

    public void setDocument(ApplicationDocument document) {
        this.document = document;
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

    @Override
    public LocalDate getLastRemindedRequestIndividual() {
        return lastRemindedRequestIndividual;
    }

    @Override
    public void setLastRemindedRequestIndividual(LocalDate lastRemindedRequestIndividual) {
        this.lastRemindedRequestIndividual = lastRemindedRequestIndividual;
    }

    @Override
    public LocalDate getLastRemindedRequestSyndicated() {
        return lastRemindedRequestSyndicated;
    }

    @Override
    public void setLastRemindedRequestSyndicated(LocalDate lastRemindedRequestSyndicated) {
        this.lastRemindedRequestSyndicated = lastRemindedRequestSyndicated;
    }

    @Override
    public LocalDate getLastNotifiedUpdateSyndicated() {
        return lastNotifiedUpdateSyndicated;
    }

    @Override
    public void setLastNotifiedUpdateSyndicated(LocalDate lastNotifiedUpdateSyndicated) {
        this.lastNotifiedUpdateSyndicated = lastNotifiedUpdateSyndicated;
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

    public ApplicationPersonalDetail getPersonalDetail() {
        return personalDetail;
    }

    public void setPersonalDetail(ApplicationPersonalDetail personalDetail) {
        this.personalDetail = personalDetail;
    }

    public ApplicationProgramDetail getProgramDetail() {
        return programDetail;
    }

    public void setProgramDetail(ApplicationProgramDetail programDetail) {
        this.programDetail = programDetail;
    }

    public final Boolean getPreviousApplication() {
        return previousApplication;
    }

    public final void setPreviousApplication(Boolean previousApplication) {
        this.previousApplication = previousApplication;
    }

    public final ApplicationStudyDetail getStudyDetail() {
        return studyDetail;
    }

    public final void setStudyDetail(ApplicationStudyDetail studyDetail) {
        this.studyDetail = studyDetail;
    }

    public Set<ApplicationSupervisor> getSupervisors() {
        return supervisors;
    }

    public ApplicationAdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(ApplicationAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getPrimaryTheme() {
        return primaryTheme;
    }

    public void setPrimaryTheme(String primaryTheme) {
        this.primaryTheme = primaryTheme;
    }

    public String getSecondaryTheme() {
        return secondaryTheme;
    }

    public void setSecondaryTheme(String secondaryTheme) {
        this.secondaryTheme = secondaryTheme;
    }

    public Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public void setApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDate getConfirmedStartDate() {
        return confirmedStartDate;
    }

    public void setConfirmedStartDate(LocalDate confirmedStartDate) {
        this.confirmedStartDate = confirmedStartDate;
    }

    public PrismOfferType getConfirmedOfferType() {
        return confirmedOfferType;
    }

    public void setConfirmedOfferType(PrismOfferType confirmedOfferType) {
        this.confirmedOfferType = confirmedOfferType;
    }

    public Boolean getRetain() {
        return retain;
    }

    public void setRetain(Boolean retain) {
        this.retain = retain;
    }

    public Set<ApplicationQualification> getQualifications() {
        return qualifications;
    }

    public Set<ApplicationFunding> getFundings() {
        return fundings;
    }

    public Set<ApplicationPrize> getPrizes() {
        return prizes;
    }

    public Set<ApplicationEmploymentPosition> getEmploymentPositions() {
        return employmentPositions;
    }

    public Set<ApplicationReferee> getReferees() {
        return referees;
    }

    @Override
    public Set<ResourceState> getResourceStates() {
        return resourceStates;
    }

    @Override
    public Set<ResourcePreviousState> getResourcePreviousStates() {
        return resourcePreviousStates;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    @Override
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public final Set<ApplicationProcessing> getProcessings() {
        return processings;
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

    public Application withProject(Project project) {
        this.project = project;
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

    public Application withPersonalDetail(ApplicationPersonalDetail personalDetail) {
        this.personalDetail = personalDetail;
        return this;
    }

    public Application withProgramDetail(ApplicationProgramDetail programDetail) {
        this.programDetail = programDetail;
        return this;
    }

    public Application withAddress(ApplicationAddress applicationAddress) {
        this.address = applicationAddress;
        return this;
    }

    public Application withDocument(ApplicationDocument applicationDocument) {
        this.document = applicationDocument;
        return this;
    }

    public Application withAdditionalInformation(ApplicationAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public Application withDoRetain(Boolean doRetain) {
        this.retain = doRetain;
        return this;
    }

    public Application withAcceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
        return this;
    }

    public Application withReferees(ApplicationReferee... referees) {
        this.referees.addAll(Arrays.asList(referees));
        return this;
    }

    public Application withQualifications(ApplicationQualification... qualifications) {
        this.qualifications.addAll(Arrays.asList(qualifications));
        return this;
    }

    public Application withParentResource(Resource parentResource) {
        setParentResource(parentResource);
        return this;
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
    public Integer getWorkflowPropertyConfigurationVersion() {
        return workflowPropertyConfigurationVersion;
    }

    @Override
    public void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion) {
        this.workflowPropertyConfigurationVersion = workflowPropertyConfigurationVersion;
    }

    @Override
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public Set<ResourceParent> getParentResources() {
        Set<ResourceParent> parentResources = Sets.newLinkedHashSet();
        if (project != null) {
            parentResources.add(project);
        }
        parentResources.add(program);
        parentResources.add(institution);
        return parentResources;
    }

    public String getInstitutionDisplay() {
        return institution == null ? null : institution.getTitle();
    }

    public String getProgramDisplay() {
        return program == null ? null : program.getTitle();
    }

    public String getProjectDisplay() {
        return project == null ? null : project.getTitle();
    }

    public String getCreatedTimestampDisplay(String dateFormat) {
        return createdTimestamp == null ? null : createdTimestamp.toString(dateFormat, LocaleUtils.toLocale(this.getLocale().toString()));
    }

    public String getSubmittedTimestampDisplay(String dateFormat) {
        return submittedTimestamp == null ? null : submittedTimestamp.toString(dateFormat, LocaleUtils.toLocale(this.getLocale().toString()));
    }

    public String getClosingDateDisplay(String dateFormat) {
        return closingDate == null ? null : closingDate.toString(dateFormat, LocaleUtils.toLocale(this.getLocale().toString()));
    }

    public String getConfirmedStartDateDisplay(String dateFormat) {
        return confirmedStartDate == null ? null : confirmedStartDate.toString(dateFormat, LocaleUtils.toLocale(this.getLocale().toString()));
    }

    public String getApplicationRatingAverageDisplay() {
        return applicationRatingAverage == null ? null : applicationRatingAverage.toPlainString();
    }

    public String getPrimaryThemeDisplay() {
        return primaryTheme == null ? null : primaryTheme.replace("|", ", ");
    }

    public String getSecondaryThemeDisplay() {
        return secondaryTheme == null ? null : secondaryTheme.replace("|", ", ");
    }

    public boolean isApproved() {
        return state.getStateGroup().getId() == PrismStateGroup.APPLICATION_APPROVED && state.getId() != PrismState.APPLICATION_APPROVED;
    }

    public boolean isSubmitted() {
        return submittedTimestamp != null;
    }

    public String getProjectOrProgramTitleDisplay() {
        return project == null ? program.getTitle() : project.getTitle();
    }

    public String getProjectOrProgramCodeDisplay() {
        return project == null ? program.getCode() : project.getCode();
    }

    public PrismProgramStartType getDefaultStartType() {
        return project == null && program.getProgramType().getPrismProgramType().getDefaultStartType() == SCHEDULED ? SCHEDULED : IMMEDIATE;
    }

    public boolean isProgramApplication() {
        return project == null;
    }

    public Advert getAdvert() {
        return isProgramApplication() ? program.getAdvert() : project.getAdvert();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("user", user).addProperty("program", program).addProperty("project", project)
                .addExclusion("state.id", PrismState.APPLICATION_APPROVED_COMPLETED).addExclusion("state.id", PrismState.APPLICATION_APPROVED_PENDING_EXPORT)
                .addExclusion("state.id", PrismState.APPLICATION_APPROVED_PENDING_CORRECTION)
                .addExclusion("state.id", PrismState.APPLICATION_APPROVED_COMPLETED_PURGED).addExclusion("state.id", PrismState.APPLICATION_REJECTED_COMPLETED)
                .addExclusion("state.id", PrismState.APPLICATION_REJECTED_PENDING_EXPORT)
                .addExclusion("state.id", PrismState.APPLICATION_REJECTED_PENDING_CORRECTION)
                .addExclusion("state.id", PrismState.APPLICATION_REJECTED_COMPLETED_PURGED)
                .addExclusion("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED).addExclusion("state.id", PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT)
                .addExclusion("state.id", PrismState.APPLICATION_WITHDRAWN_PENDING_CORRECTION)
                .addExclusion("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED_PURGED)
                .addExclusion("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED)
                .addExclusion("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED);
    }

}
