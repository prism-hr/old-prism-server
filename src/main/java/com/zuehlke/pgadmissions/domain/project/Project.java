package com.zuehlke.pgadmissions.domain.project;

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
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.ResourceAction;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "PROJECT")
public class Project extends ResourceParent {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code", unique = true)
    private String code;

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

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "referrer")
    private String referrer;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "application_created_count")
    private Integer applicationCreatedCount;

    @Column(name = "application_submitted_count")
    private Integer applicationSubmittedCount;

    @Column(name = "application_approved_count")
    private Integer applicationApprovedCount;

    @Column(name = "application_rejected_count")
    private Integer applicationRejectedCount;

    @Column(name = "application_withdrawn_count")
    private Integer applicationWithdrawnCount;

    @Column(name = "application_rating_count")
    private Integer applicationRatingCount;

    @Column(name = "application_rating_count_average_non_zero")
    private BigDecimal applicationRatingCountAverageNonZero;

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

    @OneToMany(mappedBy = "project")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<Application> applications = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<ResourceAction> resourceActions = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
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

    public Department getDepartment() {
        return department;
    }

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

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Integer getApplicationCreatedCount() {
        return applicationCreatedCount;
    }

    @Override
    public void setApplicationCreatedCount(Integer applicationCreatedCount) {
        this.applicationCreatedCount = applicationCreatedCount;
    }

    @Override
    public Integer getApplicationSubmittedCount() {
        return applicationSubmittedCount;
    }

    @Override
    public void setApplicationSubmittedCount(Integer applicationSubmittedCount) {
        this.applicationSubmittedCount = applicationSubmittedCount;
    }

    @Override
    public Integer getApplicationApprovedCount() {
        return applicationApprovedCount;
    }

    @Override
    public void setApplicationApprovedCount(Integer applicationApprovedCount) {
        this.applicationApprovedCount = applicationApprovedCount;
    }

    @Override
    public Integer getApplicationRejectedCount() {
        return applicationRejectedCount;
    }

    @Override
    public void setApplicationRejectedCount(Integer applicationRejectedCount) {
        this.applicationRejectedCount = applicationRejectedCount;
    }

    @Override
    public Integer getApplicationWithdrawnCount() {
        return applicationWithdrawnCount;
    }

    @Override
    public void setApplicationWithdrawnCount(Integer applicationWithdrawnCount) {
        this.applicationWithdrawnCount = applicationWithdrawnCount;
    }

    @Override
    public Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    @Override
    public void setApplicationRatingCount(Integer applicationRatingCountSum) {
        this.applicationRatingCount = applicationRatingCountSum;
    }

    @Override
    public BigDecimal getApplicationRatingCountAverageNonZero() {
        return applicationRatingCountAverageNonZero;
    }

    @Override
    public void setApplicationRatingCountAverageNonZero(BigDecimal applicationRatingCountAverage) {
        this.applicationRatingCountAverageNonZero = applicationRatingCountAverage;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

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
    public Project getProject() {
        return this;
    }

    @Override
    public void setProject(Project project) {
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
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
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

    public Project withUser(User user) {
        this.user = user;
        return this;
    }

    public Project withSystem(System system) {
        this.system = system;
        return this;
    }

    public Project withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public Project withDepartment(Department department) {
        this.department = department;
        return this;
    }

    public Project withProgram(Program program) {
        this.program = program;
        return this;
    }

    public Project withTitle(String title) {
        this.title = title;
        return this;
    }

    public Project withCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("user", getUser()).addProperty("program", program).addProperty("title", title)
                .addExclusion("state.id", PrismState.PROJECT_DISABLED_COMPLETED).addExclusion("state.id", PrismState.PROJECT_REJECTED)
                .addExclusion("state.id", PrismState.PROJECT_WITHDRAWN);
    }

}
