package uk.co.alumeni.prism.domain.resource;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateActionPending;

import com.google.common.collect.Sets;

@Entity
@Table(name = "system")
public class System extends Resource {

    @Id
    private Integer id;

    @OneToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id", unique = true)
    private Advert advert;

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

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
    
    @Column(name = "activity_cached_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime activityCachedTimestamp;

    @Column(name = "cipher_salt", nullable = false)
    private String cipherSalt;

    @Column(name = "amazon_access_key")
    private String amazonAccessKey;

    @Column(name = "amazon_secret_key")
    private String amazonSecretKey;

    @Column(name = "shared", nullable = false)
    private Boolean shared;

    @Column(name = "last_amazon_cleanup_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastAmazonCleanupDate;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @OneToMany(mappedBy = "system")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "system")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "system_id")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "system")
    private Set<Institution> institutions = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "system")
    private Set<Program> programs = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "system")
    private Set<Project> projects = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "system")
    private Set<Application> applications = Sets.newHashSet();

    @OneToMany(mappedBy = "system")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "system")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "system")
    private Set<StateActionPending> stateActionPendings = Sets.newHashSet();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public System getSystem() {
        return this;
    }

    @Override
    public void setSystem(System system) {
    }

    @Override
    public Institution getInstitution() {
        return null;
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
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public String getOpportunityCategories() {
        return null;
    }

    @Override
    public void setOpportunityCategories(String opportunityCategories) {
        return;
    }

    @Override
    public Application getApplication() {
        return null;
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
    
    @Override
    public DateTime getActivityCachedTimestamp() {
        return activityCachedTimestamp;
    }

    @Override
    public void setActivityCachedTimestamp(DateTime activityCachedTimestamp) {
        this.activityCachedTimestamp = activityCachedTimestamp;
    }

    public final String getCipherSalt() {
        return cipherSalt;
    }

    public final void setCipherSalt(String cipherSalt) {
        this.cipherSalt = cipherSalt;
    }

    public final String getAmazonAccessKey() {
        return amazonAccessKey;
    }

    public final void setAmazonAccessKey(String amazonAccessKey) {
        this.amazonAccessKey = amazonAccessKey;
    }

    public final String getAmazonSecretKey() {
        return amazonSecretKey;
    }

    public final void setAmazonSecretKey(String amazonSecretKey) {
        this.amazonSecretKey = amazonSecretKey;
    }

    @Override
    public Boolean getShared() {
        return shared;
    }

    @Override
    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public final LocalDate getLastAmazonCleanupDate() {
        return lastAmazonCleanupDate;
    }

    public final void setLastAmazonCleanupDate(LocalDate lastAmazonCleanupDate) {
        this.lastAmazonCleanupDate = lastAmazonCleanupDate;
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
    public final Set<ResourceState> getResourceStates() {
        return resourceStates;
    }

    @Override
    public final Set<ResourcePreviousState> getResourcePreviousStates() {
        return resourcePreviousStates;
    }

    @Override
    public Set<ResourceCondition> getResourceConditions() {
        return resourceConditions;
    }

    public final Set<Institution> getInstitutions() {
        return institutions;
    }

    public final Set<Program> getPrograms() {
        return programs;
    }

    public final Set<Application> getApplications() {
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
    public Set<StateActionPending> getStateActionPendings() {
        return stateActionPendings;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public System withId(Integer id) {
        this.id = id;
        return this;
    }

    public System withName(String name) {
        this.name = name;
        return this;
    }

    public System withUser(User user) {
        this.user = user;
        return this;
    }

    public System withShared(Boolean shared) {
        this.shared = shared;
        return this;
    }

    public System withState(State state) {
        this.state = state;
        return this;
    }

    public System withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public System withUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }

    public System withCipherSalt(String cipherSalt) {
        this.cipherSalt = cipherSalt;
        return this;
    }

    public boolean isDocumentExportEnabled() {
        return !(amazonAccessKey == null || amazonSecretKey == null);
    }

    @Override
    public UniqueEntity.EntitySignature getEntitySignature() {
        return new UniqueEntity.EntitySignature().addProperty("name", name);
    }

}
