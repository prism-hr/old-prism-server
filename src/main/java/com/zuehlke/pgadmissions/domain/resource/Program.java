package com.zuehlke.pgadmissions.domain.resource;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.TargetEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "program")
public class Program extends ResourceOpportunity implements TargetEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "imported_code")
    private String importedCode;

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
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id", nullable = false, unique = true)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "imported_opportunity_type_id", nullable = false)
    private ImportedEntitySimple opportunityType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "duration_minimum")
    private Integer durationMinimum;

    @Column(name = "duration_maximum")
    private Integer durationMaximum;

    @Column(name = "require_position_definition", nullable = false)
    private Boolean requirePositionDefinition;

    @Column(name = "application_rating_count")
    private Integer applicationRatingCount;

    @Column(name = "application_rating_frequency")
    private BigDecimal applicationRatingFrequency;

    @Column(name = "application_rating_average")
    private BigDecimal applicationRatingAverage;

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

    @Column(name = "updated_timestamp_sitemap", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestampSitemap;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id")
    private Set<ResourceStudyOption> instanceGroups = Sets.newHashSet();

    @OrderBy(clause = "study_location")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id")
    private Set<ResourceStudyLocation> studyLocations = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<Project> projects = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<Application> applications = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<Advert> adverts = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
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
        return this;
    }

    @Override
    public void setProgram(Program program) {
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public void setProject(Project project) {
    }

    @Override
    public Application getApplication() {
        return null;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public ImportedEntitySimple getOpportunityType() {
        return opportunityType;
    }

    @Override
    public void setOpportunityType(ImportedEntitySimple opportunityType) {
        this.opportunityType = opportunityType;
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
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    public String getImportedCode() {
        return importedCode;
    }

    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    @Override
    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    @Override
    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    @Override
    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

    @Override
    public Boolean getRequirePositionDefinition() {
        return requirePositionDefinition;
    }

    @Override
    public void setRequirePositionDefinition(Boolean requirePositionDefinition) {
        this.requirePositionDefinition = requirePositionDefinition;
    }

    @Override
    public Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    @Override
    public void setApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    @Override
    public BigDecimal getApplicationRatingFrequency() {
        return applicationRatingFrequency;
    }

    @Override
    public void setApplicationRatingFrequency(BigDecimal applicationRatingFrequency) {
        this.applicationRatingFrequency = applicationRatingFrequency;
    }

    @Override
    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    @Override
    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
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

    public DateTime getUpdatedTimestampSitemap() {
        return updatedTimestampSitemap;
    }

    public void setUpdatedTimestampSitemap(DateTime updatedTimestampSitemap) {
        this.updatedTimestampSitemap = updatedTimestampSitemap;
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

    @Override
    public Set<ResourceStudyOption> getInstanceGroups() {
        return instanceGroups;
    }

    @Override
    public void setInstanceGroups(Set<ResourceStudyOption> instanceGroups) {
        this.instanceGroups = instanceGroups;
    }

    @Override
    public Set<ResourceStudyLocation> getStudyLocations() {
        return studyLocations;
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

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    @Override
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    @Override
    public Set<Advert> getAdverts() {
        return adverts;
    }

    public Program withParentResource(Resource parentResource) {
        setParentResource(parentResource);
        return this;
    }

    public Program withCode(String code) {
        this.code = code;
        return this;
    }

    public Program withImportedCode(String importedCode) {
        this.importedCode = importedCode;
        return this;
    }

    public Program withState(State state) {
        this.state = state;
        return this;
    }
    
    public Program withUser(User user) {
        this.user = user;
        return this;
    }

    public Program withSystem(System system) {
        this.system = system;
        return this;
    }

    public Program withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public Program withDepartment(Department department) {
        this.department = department;
        return this;
    }

    public Program withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public Program withOpportunityType(ImportedEntitySimple opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public Program withName(String name) {
        this.name = name;
        return this;
    }

    public Program withDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
        return this;
    }

    public Program withDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
        return this;
    }

    public Program withRequirePositionDefinition(boolean requireProjectDefinition) {
        this.requirePositionDefinition = requireProjectDefinition;
        return this;
    }

    public Program withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Program withUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }

    public Program withUpdatedTimestampSitemap(DateTime updatedTimestampSitemap) {
        this.updatedTimestampSitemap = updatedTimestampSitemap;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addExclusion("state.id", PROGRAM_REJECTED).addExclusion("state.id", PROGRAM_WITHDRAWN)
                .addExclusion("state.id", PROGRAM_DISABLED_COMPLETED);
    }

}
