package com.zuehlke.pgadmissions.domain.institution;

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
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "institution", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "title" }) })
public class Institution extends ResourceParent {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "institution_partner_id")
    private Institution partner;

    @OneToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "referrer")
    private String referrer;

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_id", nullable = false)
    private InstitutionDomicile domicile;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @OneToOne
    @JoinColumn(name = "logo_image_id")
    private Document logoImage;

    @OneToOne
    @JoinColumn(name = "background_image_id")
    private Document backgroundImage;

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
    @JoinColumn(name = "institution_id")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OrderBy(clause = "study_location")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "institution_id")
    private Set<ResourceStudyLocation> studyLocations = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<ImportedEntityFeed> importedEntityFeeds = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "institution")
    private Set<Program> programs = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partner")
    private Set<Program> partnerPrograms = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "institution")
    private Set<Project> projects = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partner")
    private Set<Project> partnerProjects = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "institution")
    private Set<Application> applications = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<UserRole> userRoles = Sets.newHashSet();

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
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public Document getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(Document logoImage) {
        this.logoImage = logoImage;
    }

    public Document getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Document backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public InstitutionDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
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

    public Set<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityFeeds;
    }

    @Override
    public Set<Comment> getComments() {
        return comments;
    }

    @Override
    public Set<UserRole> getUserRoles() {
        return userRoles;
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

    public Institution withDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public Institution withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public Institution withTitle(String title) {
        this.title = title;
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

    public Institution withEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
        return;
    }

    @Override
    public Institution getPartner() {
        return partner;
    }

    @Override
    public void setPartner(Institution partner) {
        return;
    }

    @Override
    public Program getProgram() {
        return null;
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
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
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
    public Set<ResourceStudyLocation> getStudyLocations() {
        return studyLocations;
    }

    @Override
    public Integer getWorkflowPropertyConfigurationVersion() {
        return workflowPropertyConfigurationVersion;
    }

    @Override
    public void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion) {
        this.workflowPropertyConfigurationVersion = workflowPropertyConfigurationVersion;
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
    public ResourceSignature getResourceSignature() {
        if (googleId == null) {
            return new ResourceSignature().addProperty("user", user).addProperty("title", title);
        }
        return new ResourceSignature().addProperty("googleId", googleId).addProperty("title", title);
    }

}
