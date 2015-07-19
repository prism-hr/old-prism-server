package com.zuehlke.pgadmissions.domain.application;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReserveStatus;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.workflow.validation.PrismConstraintRequired;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_REQUIRED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.IMMEDIATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.*;

@Entity
@Table(name = "application")
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
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id")
    private Advert advert;

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

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationSupervisor> supervisors = Sets.newHashSet();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_personal_detail_id", unique = true)
    private ApplicationPersonalDetail personalDetail;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_address_id", unique = true)
    private ApplicationAddress address;

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationQualification> qualifications = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationEmploymentPosition> employmentPositions = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationFunding> fundings = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationPrize> prizes = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationReferee> referees = Sets.newHashSet();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_document_id", unique = true)
    private ApplicationDocument document;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_additional_information_id", unique = true)
    private ApplicationAdditionalInformation additionalInformation;

    @Lob
    @Column(name = "primary_theme")
    private String primaryTheme;

    @Lob
    @Column(name = "secondary_theme")
    private String secondaryTheme;

    @Column(name = "application_rating_count")
    private Integer applicationRatingCount;

    @Column(name = "application_rating_average")
    private BigDecimal applicationRatingAverage;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_reserve_status")
    private PrismApplicationReserveStatus applicationReserveStatus;

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

    @Column(name = "application_year")
    private String applicationYear;

    @Column(name = "application_month")
    private Integer applicationMonth;

    @Column(name = "application_month_sequence")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationMonthSequence;

    @Column(name = "application_week")
    private Integer applicationWeek;

    @Column(name = "application_week_sequence")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationWeekSequence;

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
    @PrismConstraintRequired(error = SYSTEM_VALIDATION_REQUIRED)
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<UserRole> userRoles = Sets.newHashSet();

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
    public Department getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(Department department) {
        this.department = department;
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

    public final Advert getAdvert() {
        return advert;
    }

    public final void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public Application getApplication() {
        return this;
    }

    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public void setName(String name) {
        return;
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

    public String getApplicationYear() {
        return applicationYear;
    }

    public void setApplicationYear(String applicationYear) {
        this.applicationYear = applicationYear;
    }

    public Integer getApplicationMonth() {
        return applicationMonth;
    }

    public void setApplicationMonth(Integer applicationMonth) {
        this.applicationMonth = applicationMonth;
    }

    public LocalDate getApplicationMonthSequence() {
        return applicationMonthSequence;
    }

    public void setApplicationMonthSequence(LocalDate applicationMonthSequence) {
        this.applicationMonthSequence = applicationMonthSequence;
    }

    public Integer getApplicationWeek() {
        return applicationWeek;
    }

    public void setApplicationWeek(Integer applicationWeek) {
        this.applicationWeek = applicationWeek;
    }

    public LocalDate getApplicationWeekSequence() {
        return applicationWeekSequence;
    }

    public void setApplicationWeekSequence(LocalDate applicationWeekSequence) {
        this.applicationWeekSequence = applicationWeekSequence;
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

    public PrismApplicationReserveStatus getApplicationReserveStatus() {
        return applicationReserveStatus;
    }

    public void setApplicationReserveStatus(PrismApplicationReserveStatus applicationReserveRating) {
        this.applicationReserveStatus = applicationReserveRating;
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

    @Override
    public Set<ResourceCondition> getResourceConditions() {
        return resourceConditions;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    @Override
    public Set<UserRole> getUserRoles() {
        return userRoles;
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

    public Application withAdvert(Advert advert) {
        this.advert = advert;
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

    public Application withRetain(Boolean retain) {
        this.retain = retain;
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
        for (ResourceParent parentResource : new ResourceParent[] { project, program, department, institution }) {
            if (parentResource != null) {
                parentResources.add(parentResource);
            }
        }
        return parentResources;
    }

    public String getCreatedTimestampDisplay(String dateFormat) {
        return createdTimestamp == null ? null : createdTimestamp.toString(dateFormat);
    }

    public String getSubmittedTimestampDisplay(String dateFormat) {
        return submittedTimestamp == null ? null : submittedTimestamp.toString(dateFormat);
    }

    public String getClosingDateDisplay(String dateFormat) {
        return closingDate == null ? null : closingDate.toString(dateFormat);
    }

    public String getPrimaryThemeDisplay() {
        return primaryTheme == null ? null : primaryTheme.replace("|", ", ");
    }

    public String getSecondaryThemeDisplay() {
        return secondaryTheme == null ? null : secondaryTheme.replace("|", ", ");
    }

    public boolean isSubmitted() {
        return submittedTimestamp != null;
    }

    public String getParentResourceTitleDisplay() {
        ResourceParent parent = (ResourceParent) getParentResource();
        return parent.getName();
    }

    public String getParentResourceCodeDisplay() {
        ResourceParent parent = (ResourceParent) getParentResource();
        return parent.getCode();
    }

    public PrismOpportunityType getOpportunityType() {
        Resource resourceParent = getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(resourceParent.getClass())) {
            return PrismOpportunityType.valueOf(((ResourceOpportunity) resourceParent).getOpportunityType().getName());
        }
        return null;
    }

    public PrismProgramStartType getDefaultStartType() {
        PrismOpportunityType opportunityType = getOpportunityType();
        return opportunityType == null ? IMMEDIATE : opportunityType.getDefaultStartType();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        Resource parentResource = getParentResource();
        return new ResourceSignature()
                .addProperty("user", user)
                .addProperty(parentResource.getResourceScope().getLowerCamelName(), parentResource)
                .addExclusion("state.id", APPLICATION_APPROVED_COMPLETED)
                .addExclusion("state.id", APPLICATION_APPROVED_PENDING_EXPORT)
                .addExclusion("state.id", APPLICATION_APPROVED_PENDING_CORRECTION)
                .addExclusion("state.id", APPLICATION_APPROVED_COMPLETED_PURGED)
                .addExclusion("state.id", APPLICATION_APPROVED_COMPLETED_RETAINED)
                .addExclusion("state.id", APPLICATION_REJECTED_COMPLETED)
                .addExclusion("state.id", APPLICATION_REJECTED_PENDING_EXPORT)
                .addExclusion("state.id", APPLICATION_REJECTED_PENDING_CORRECTION)
                .addExclusion("state.id", APPLICATION_REJECTED_COMPLETED_PURGED)
                .addExclusion("state.id", APPLICATION_REJECTED_COMPLETED_RETAINED)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_COMPLETED)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_PENDING_EXPORT)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_PENDING_CORRECTION)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_COMPLETED_PURGED)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_COMPLETED_RETAINED)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_RETAINED);
    }

}
