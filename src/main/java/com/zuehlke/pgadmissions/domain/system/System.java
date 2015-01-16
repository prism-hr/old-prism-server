package com.zuehlke.pgadmissions.domain.system;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "SYSTEM")
public class System extends Resource {

    @Id
    private Integer id;

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "locale", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "helpdesk", nullable = false)
    private String helpdesk;

    @Column(name = "last_data_import_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastDataImportDate;

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

    @Column(name = "cipher_salt", nullable = false)
    private String cipherSalt;

    @Column(name = "amazon_access_key")
    private String amazonAccessKey;

    @Column(name = "amazon_secret_key")
    private String amazonSecretKey;
    
    @Column(name = "last_amazon_cleanup_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastAmazonCleanupDate;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @OneToMany(mappedBy = "system")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "system")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public final PrismLocale getLocale() {
        return locale;
    }

    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public final String getHelpdesk() {
        return helpdesk;
    }

    public final void setHelpdesk(String helpdesk) {
        this.helpdesk = helpdesk;
    }

    public final LocalDate getLastDataImportDate() {
        return lastDataImportDate;
    }

    public final void setLastDataImportDate(LocalDate lastDataImportDate) {
        this.lastDataImportDate = lastDataImportDate;
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
    public String getReferrer() {
        return null;
    }

    @Override
    public void setReferrer(String referrer) {
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
    public final LocalDate getLastRemindedRequestIndividual() {
        return lastRemindedRequestIndividual;
    }

    @Override
    public final void setLastRemindedRequestIndividual(LocalDate lastRemindedRequestIndividual) {
        this.lastRemindedRequestIndividual = lastRemindedRequestIndividual;
    }

    @Override
    public final LocalDate getLastRemindedRequestSyndicated() {
        return lastRemindedRequestSyndicated;
    }

    @Override
    public final void setLastRemindedRequestSyndicated(LocalDate lastRemindedRequestSyndicated) {
        this.lastRemindedRequestSyndicated = lastRemindedRequestSyndicated;
    }

    @Override
    public final LocalDate getLastNotifiedUpdateSyndicated() {
        return lastNotifiedUpdateSyndicated;
    }

    @Override
    public final void setLastNotifiedUpdateSyndicated(LocalDate lastNotifiedUpdateSyndicated) {
        this.lastNotifiedUpdateSyndicated = lastNotifiedUpdateSyndicated;
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
    public Integer getWorkflowPropertyConfigurationVersion() {
        return null;
    }

    @Override
    public void setWorkflowPropertyConfigurationVersion(Integer workflowResourceConfigurationVersion) {
        return;
    }

    @Override
    public final Set<ResourceState> getResourceStates() {
        return resourceStates;
    }

    @Override
    public final Set<ResourcePreviousState> getResourcePreviousStates() {
        return resourcePreviousStates;
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

    public Set<Project> getProjects() {
        return projects;
    }

    public System withTitle(String title) {
        this.title = title;
        return this;
    }

    public System withId(Integer id) {
        this.id = id;
        return this;
    }

    public System withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public System withHelpdesk(String helpdesk) {
        this.helpdesk = helpdesk;
        return this;
    }

    public System withUser(User user) {
        this.user = user;
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
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("title", title);
    }

}
