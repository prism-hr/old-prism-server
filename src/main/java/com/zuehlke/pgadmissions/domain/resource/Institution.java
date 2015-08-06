package com.zuehlke.pgadmissions.domain.resource;

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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.TargetEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "institution", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "name"})})
public class Institution extends ResourceParent implements TargetEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @OneToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id", nullable = false, unique = true)
    private Advert advert;

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "logo_image_id")
    private Document logoImage;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "business_year_start_month", nullable = false)
    private Integer businessYearStartMonth;

    @Column(name = "minimum_wage", nullable = false)
    private BigDecimal minimumWage;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "ucl_institution", nullable = false)
    private Boolean uclInstitution;

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

    @OneToOne
    @JoinColumn(name = "imported_institution_id", unique = true)
    private ImportedInstitution importedInstitution;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "institution_id")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<Department> departments = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<Program> programs = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<Project> projects = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<Application> applications = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<Advert> adverts = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Document getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(Document logoImage) {
        this.logoImage = logoImage;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getBusinessYearStartMonth() {
        return businessYearStartMonth;
    }

    public void setBusinessYearStartMonth(Integer businessYearStartMonth) {
        this.businessYearStartMonth = businessYearStartMonth;
    }

    public BigDecimal getMinimumWage() {
        return minimumWage;
    }

    public void setMinimumWage(BigDecimal minimumWage) {
        this.minimumWage = minimumWage;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public Boolean getUclInstitution() {
        return uclInstitution;
    }

    public void setUclInstitution(Boolean uclInstitution) {
        this.uclInstitution = uclInstitution;
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
    public Set<ResourceCondition> getResourceConditions() {
        return resourceConditions;
    }

    @Override
    public Set<ResourceState> getResourceStates() {
        return resourceStates;
    }

    @Override
    public Set<ResourcePreviousState> getResourcePreviousStates() {
        return resourcePreviousStates;
    }

    public Set<Department> getDepartments() {
        return departments;
    }

    public Set<Program> getPrograms() {
        return programs;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    @Override
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

    public ImportedInstitution getImportedInstitution() {
        return importedInstitution;
    }

    public void setImportedInstitution(ImportedInstitution importedInstitution) {
        this.importedInstitution = importedInstitution;
    }

    public Institution withParentResource(Resource parentResource) {
        setParentResource(parentResource);
        return this;
    }

    public Institution withSystem(System system) {
        this.system = system;
        return this;
    }

    public Institution withUser(User user) {
        this.user = user;
        return this;
    }

    public Institution withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public Institution withName(String name) {
        this.name = name;
        return this;
    }

    public Institution withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public Institution withBusinessYearStartMonth(Integer businessYearStartMonth) {
        this.businessYearStartMonth = businessYearStartMonth;
        return this;
    }

    public Institution withMinimumWage(BigDecimal minimumWage) {
        this.minimumWage = minimumWage;
        return this;
    }

    public Institution withState(State state) {
        this.state = state;
        return this;
    }

    public Institution withGoogleId(String googleId) {
        this.googleId = googleId;
        return this;
    }

    public Institution withUclInstitution(boolean uclInstitution) {
        this.uclInstitution = uclInstitution;
        return this;
    }

    public Institution withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Institution withUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }

    public Institution withLogoImage(final Document logoImage) {
        this.logoImage = logoImage;
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
        return this;
    }

    @Override
    public void setInstitution(Institution institution) {
    }

    @Override
    public Department getDepartment() {
        return null;
    }

    @Override
    public void setDepartment(Department department) {
    }

    @Override
    public Program getProgram() {
        return null;
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

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
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
    public ResourceSignature getResourceSignature() {
        return googleId == null ? super.getResourceSignature().addProperty("user", user) : super.getResourceSignature().addProperty("googleId", googleId);
    }

}
