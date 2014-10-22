package com.zuehlke.pgadmissions.domain.program;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.rest.validation.annotation.ESAPIConstraint;

@Entity
@Table(name = "PROGRAM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Program extends ResourceParent {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "imported_code")
    private String importedCode;

    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "referrer")
    private String referrer;

    @ManyToOne
    @JoinColumn(name = "program_type_id", nullable = false)
    private ProgramType programType;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "locale", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "require_project_definition", nullable = false)
    private Boolean requireProjectDefinition;

    @Column(name = "imported", nullable = false)
    private Boolean imported;

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

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "PROGRAM_RELATION", joinColumns = @JoinColumn(name = "program_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "program_relation_id", nullable = false))
    private Set<Program> programRelations = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<ProgramStudyOption> studyOptions = Sets.newHashSet();

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

    @Override
    public String getReferrer() {
        return referrer;
    }

    @Override
    public void setReferrer (String referrer) {
        this.referrer = referrer;
    }

    public final ProgramType getProgramType() {
        return programType;
    }

    public final void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
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

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }

    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
    }

    public final Boolean getImported() {
        return imported;
    }

    public final void setImported(Boolean imported) {
        this.imported = imported;
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
    public final Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    @Override
    public final void setApplicationRatingCount(Integer applicationRatingCountSum) {
        this.applicationRatingCount = applicationRatingCountSum;
    }

    @Override
    public final BigDecimal getApplicationRatingCountAverageNonZero() {
        return applicationRatingCountAverageNonZero;
    }

    @Override
    public final void setApplicationRatingCountAverageNonZero(BigDecimal applicationRatingCountAverage) {
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
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public final Set<Program> getProgramRelations() {
        return programRelations;
    }

    @Override
    public void addComment(Comment comment) {
        comments.add(comment);
    }
    
    public final Set<ProgramStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public final Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public Program withId(Integer id) {
        this.id = id;
        return this;
    }

    public Program withAdvert(Advert advert) {
        this.advert = advert;
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

    public Program withTitle(String title) {
        setTitle(title);
        return this;
    }
    
    public Program withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public Program withDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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

    public Program withRequireProjectDefinition(boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
        return this;
    }

    public Program withImported(Boolean imported) {
        this.imported = imported;
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

    public Program withProgramType(ProgramType programType) {
        this.programType = programType;
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

    @Override
    public ResourceSignature getResourceSignature() {
        ResourceSignature signature = new ResourceSignature().addProperty("institution", institution);
        if (BooleanUtils.isTrue(imported)) {
            signature.addProperty("importedCode", importedCode);
        } else {
            signature.addProperty("title", title);
            signature.addExclusion("state.id", PrismState.PROGRAM_DISABLED_COMPLETED);
        }
        signature.addExclusion("state.id", PrismState.PROGRAM_REJECTED);
        signature.addExclusion("state.id", PrismState.PROGRAM_WITHDRAWN);
        return signature;
    }

}
