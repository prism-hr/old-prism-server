package com.zuehlke.pgadmissions.domain.program;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.*;

@Entity
@Table(name = "PROGRAM")
public class Program extends ResourceOpportunity {

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
    @JoinColumn(name = "institution_partner_id")
    private Institution partner;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "opportunity_type_id", nullable = false)
    private OpportunityType opportunityType;

    @Column(name = "referrer")
    private String referrer;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToOne
    @JoinColumn(name = "background_image_id")
    private Document backgroundImage;

    @Column(name = "duration_minimum")
    private Integer durationMinimum;

    @Column(name = "duration_maximum")
    private Integer durationMaximum;

    @Column(name = "require_project_definition", nullable = false)
    private Boolean requireProjectDefinition;

    @Column(name = "imported", nullable = false)
    private Boolean imported;

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

    @Column(name = "end_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate endDate;

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
    private Set<ResourceStudyOption> studyOptions = Sets.newHashSet();

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
    public Institution getPartner() {
        return partner;
    }

    @Override
    public void setPartner(Institution partner) {
        this.partner = partner;
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
        return;
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public void setProject(Project project) {
        return;
    }

    @Override
    public Application getApplication() {
        return null;
    }

    @Override
    public String getReferrer() {
        return referrer;
    }

    @Override
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public OpportunityType getOpportunityType() {
        return opportunityType;
    }

    @Override
    public void setOpportunityType(OpportunityType opportunityType) {
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
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Document getBackgroundImage() {
        return backgroundImage;
    }

    @Override
    public void setBackgroundImage(Document backgroundImage) {
        this.backgroundImage = backgroundImage;
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

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }

    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
    }

    @Override
    public Boolean getImported() {
        return imported;
    }

    public void setImported(Boolean imported) {
        this.imported = imported;
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

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
    public Set<ResourceStudyOption> getStudyOptions() {
        return studyOptions;
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

    public Program withOpportunityType(OpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public Program withTitle(String title) {
        setTitle(title);
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

    public Program withRequireProjectDefinition(boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
        return this;
    }

    public Program withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public Program withImported(Boolean imported) {
        this.imported = imported;
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
        ResourceSignature signature;
        if (BooleanUtils.isTrue(imported)) {
            signature = new ResourceSignature().addProperty("importedCode", importedCode);
        } else {
            signature = super.getResourceSignature();
        }
        signature.addExclusion("state.id", PROGRAM_REJECTED);
        signature.addExclusion("state.id", PROGRAM_WITHDRAWN);
        signature.addExclusion("state.id", PROGRAM_DISABLED_COMPLETED);
        return signature;
    }

}
