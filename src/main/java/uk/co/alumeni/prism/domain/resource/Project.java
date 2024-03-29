package uk.co.alumeni.prism.domain.resource;

import com.google.common.collect.Sets;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateActionPending;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "project")
public class Project extends ResourceOpportunity {

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
    private uk.co.alumeni.prism.domain.resource.System system;

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
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id", unique = true)
    private Advert advert;

    @Lob
    @Column(name = "advert_incomplete_section")
    private String advertIncompleteSection;

    @ManyToOne
    @JoinColumn(name = "opportunity_type_id", nullable = false)
    private OpportunityType opportunityType;

    @Column(name = "opportunity_category", nullable = false)
    private String opportunityCategories;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "application_rating_count")
    private Integer applicationRatingCount;

    @Column(name = "application_rating_frequency")
    private BigDecimal applicationRatingFrequency;

    @Column(name = "application_rating_average")
    private BigDecimal applicationRatingAverage;

    @Column(name = "shared", nullable = false)
    private Boolean shared;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "previous_state_id")
    private State previousState;

    @Column(name = "recent_update")
    private Boolean recentUpdate;

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;

    @Column(name = "activity_cached_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime activityCachedTimestamp;

    @Column(name = "updated_timestamp_sitemap", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestampSitemap;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OrderBy(clause = "study_option")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    private Set<ResourceStudyOption> resourceStudyOptions = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<Application> applications = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<StateActionPending> stateActionPendings = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
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
    public String getImportedCode() {
        return importedCode;
    }

    @Override
    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    @Override
    public uk.co.alumeni.prism.domain.resource.System getSystem() {
        return system;
    }

    @Override
    public void setSystem(uk.co.alumeni.prism.domain.resource.System system) {
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

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public String getAdvertIncompleteSection() {
        return advertIncompleteSection;
    }

    @Override
    public void setAdvertIncompleteSection(String advertIncompleteSection) {
        this.advertIncompleteSection = advertIncompleteSection;
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
    public String getOpportunityCategories() {
        return opportunityCategories;
    }

    @Override
    public void setOpportunityCategories(String opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
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
    public Boolean getShared() {
        return shared;
    }

    @Override
    public void setShared(Boolean shared) {
        this.shared = shared;
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
    public Set<Program> getPrograms() {
        return Collections.emptySet();
    }

    @Override
    public Set<Project> getProjects() {
        return Collections.emptySet();
    }

    @Override
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
    public Set<StateActionPending> getStateActionPendings() {
        return stateActionPendings;
    }

    @Override
    public Set<Advert> getAdverts() {
        return adverts;
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
    public State getPreviousState() {
        return previousState;
    }

    @Override
    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    @Override
    public Boolean getRecentUpdate() {
        return recentUpdate;
    }

    @Override
    public void setRecentUpdate(Boolean recentUpdate) {
        this.recentUpdate = recentUpdate;
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
    public DateTime getActivityCachedTimestamp() {
        return activityCachedTimestamp;
    }

    @Override
    public void setActivityCachedTimestamp(DateTime activityCachedTimestamp) {
        this.activityCachedTimestamp = activityCachedTimestamp;
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
    public Set<ResourceStudyOption> getResourceStudyOptions() {
        return resourceStudyOptions;
    }

    @Override
    public void setResourceStudyOptions(Set<ResourceStudyOption> resourceStudyOptions) {
        this.resourceStudyOptions = resourceStudyOptions;
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

    public Project withParentResource(Resource parentResource) {
        setParentResource(parentResource);
        return this;
    }

    public Project withImportedCode(String importedCode) {
        this.importedCode = importedCode;
        return this;
    }

    public Project withUser(User user) {
        this.user = user;
        return this;
    }

    public Project withSystem(uk.co.alumeni.prism.domain.resource.System system) {
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

    public Project withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public Project withOpportunityType(OpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public Project withName(String name) {
        this.name = name;
        return this;
    }

    public Project withCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature()
                .addProperty("user", user)
                .addProperty("program", getProgram())
                .addProperty("department", getDepartment())
                .addExclusion("state.id", PrismState.PROJECT_DISABLED_COMPLETED)
                .addExclusion("state.id", PrismState.PROJECT_REJECTED)
                .addExclusion("state.id", PrismState.PROJECT_WITHDRAWN);
    }

}
