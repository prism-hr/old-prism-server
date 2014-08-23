package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.validation.annotation.ESAPIConstraint;

@Entity
@Table(name = "PROGRAM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Program extends ParentResource {
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id")
    private Advert advert;
    
    @OneToMany
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "code")
    private String code;
    
    @Column(name = "imported_code")
    private String importedCode;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title", nullable = false)
    @Field(analyzer = @Analyzer(definition = "advertAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;

    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @Column(name = "referrer")
    private String referrer;

    @Column(name = "program_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;

    @Column(name = "require_project_definition", nullable = false)
    private Boolean requireProjectDefinition;
    
    @Column(name = "application_rating_count_percentile_05")
    private Integer applicationRatingCount05;
    
    @Column(name = "application_rating_count_percentile_20")
    private Integer applicationRatingCount20;

    @Column(name = "application_rating_count_percentile_35")
    private Integer applicationRatingCount35;
    
    @Column(name = "application_rating_count_percentile_50")
    private Integer applicationRatingCount50;
    
    @Column(name = "application_rating_count_percentile_65")
    private Integer applicationRatingCount65;
    
    @Column(name = "application_rating_count_percentile_80")
    private Integer applicationRatingCount80;
    
    @Column(name = "application_rating_count_percentile_95")
    private Integer applicationRatingCount95;
    
    @Column(name = "application_rating_average_percentile_05")
    private BigDecimal applicationRatingAverage05;
    
    @Column(name = "application_rating_average_percentile_20")
    private BigDecimal applicationRatingAverage20;

    @Column(name = "application_rating_average_percentile_35")
    private BigDecimal applicationRatingAverage35;
    
    @Column(name = "application_rating_average_percentile_50")
    private BigDecimal applicationRatingAverage50;
    
    @Column(name = "application_rating_average_percentile_65")
    private BigDecimal applicationRatingAverage65;
    
    @Column(name = "application_rating_average_percentile_80")
    private BigDecimal applicationRatingAverage80;
    
    @Column(name = "application_rating_average_percentile_95")
    private BigDecimal applicationRatingAverage95;
    
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
    
    @OneToMany(mappedBy = "program")
    private Set<Project> projects = Sets.newHashSet();

    @OneToMany(mappedBy = "project")
    private Set<Application> applications = Sets.newHashSet();
    
    @OneToMany(mappedBy = "program")
    @OrderBy("applicationStartDate")
    private Set<ProgramInstance> programInstances = Sets.newHashSet();

    @OneToMany(mappedBy = "program")
    private Set<Comment> comments = Sets.newHashSet();

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

    public Set<ProgramInstance> getProgramInstances() {
        return programInstances;
    }

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }
    
    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
    }
    
    @Override
    public final Integer getApplicationRatingCount05() {
        return applicationRatingCount05;
    }

    @Override
    public final void setApplicationRatingCount05(Integer applicationRatingCount05) {
        this.applicationRatingCount05 = applicationRatingCount05;
    }

    @Override
    public final Integer getApplicationRatingCount20() {
        return applicationRatingCount20;
    }

    @Override
    public final void setApplicationRatingCount20(Integer applicationRatingCount20) {
        this.applicationRatingCount20 = applicationRatingCount20;
    }

    @Override
    public final Integer getApplicationRatingCount35() {
        return applicationRatingCount35;
    }

    @Override
    public final void setApplicationRatingCount35(Integer applicationRatingCount35) {
        this.applicationRatingCount35 = applicationRatingCount35;
    }

    @Override
    public final Integer getApplicationRatingCount50() {
        return applicationRatingCount50;
    }

    @Override
    public final void setApplicationRatingCount50(Integer applicationRatingCount50) {
        this.applicationRatingCount50 = applicationRatingCount50;
    }

    @Override
    public final Integer getApplicationRatingCount65() {
        return applicationRatingCount65;
    }

    @Override
    public final void setApplicationRatingCount65(Integer applicationRatingCount65) {
        this.applicationRatingCount65 = applicationRatingCount65;
    }

    @Override
    public final Integer getApplicationRatingCount80() {
        return applicationRatingCount80;
    }

    @Override
    public final void setApplicationRatingCount80(Integer applicationRatingCount80) {
        this.applicationRatingCount80 = applicationRatingCount80;
    }

    @Override
    public final Integer getApplicationRatingCount95() {
        return applicationRatingCount95;
    }

    @Override
    public final void setApplicationRatingCount95(Integer applicationRatingCount95) {
        this.applicationRatingCount95 = applicationRatingCount95;
    }
    
    @Override
    public final BigDecimal getApplicationRatingAverage05() {
        return applicationRatingAverage05;
    }

    @Override
    public final void setApplicationRatingAverage05(BigDecimal applicationRatingAverage05) {
        this.applicationRatingAverage05 = applicationRatingAverage05;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage20() {
        return applicationRatingAverage20;
    }

    @Override
    public final void setApplicationRatingAverage20(BigDecimal applicationRatingAverage20) {
        this.applicationRatingAverage20 = applicationRatingAverage20;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage35() {
        return applicationRatingAverage35;
    }

    @Override
    public final void setApplicationRatingAverage35(BigDecimal applicationRatingAverage35) {
        this.applicationRatingAverage35 = applicationRatingAverage35;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage50() {
        return applicationRatingAverage50;
    }

    @Override
    public final void setApplicationRatingAverage50(BigDecimal applicationRatingAverage50) {
        this.applicationRatingAverage50 = applicationRatingAverage50;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage65() {
        return applicationRatingAverage65;
    }

    @Override
    public final void setApplicationRatingAverage65(BigDecimal applicationRatingAverage65) {
        this.applicationRatingAverage65 = applicationRatingAverage65;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage80() {
        return applicationRatingAverage80;
    }

    @Override
    public final void setApplicationRatingAverage80(BigDecimal applicationRatingAverage80) {
        this.applicationRatingAverage80 = applicationRatingAverage80;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage95() {
        return applicationRatingAverage95;
    }

    @Override
    public final void setApplicationRatingAverage95(BigDecimal applicationRatingAverage95) {
        this.applicationRatingAverage95 = applicationRatingAverage95;
    }
    
    public Set<Project> getProjects() {
        return projects;
    }

    public PrismProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public boolean isImported() {
        return importedCode != null;
    }

    public Set<Comment> getComments() {
        return comments;
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

    public Program withInstances(ProgramInstance... instances) {
        this.programInstances.addAll(Arrays.asList(instances));
        return this;
    }

    public Program withProjects(Project... projects) {
        this.projects.addAll(Arrays.asList(projects));
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

    public Program withProgramType(PrismProgramType programType) {
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
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        if (importedCode == null) {
            properties.put("institution", institution);
            properties.put("title", title);
        } else {
            properties.put("institution", institution);
            properties.put("importedCode", importedCode);
        }
        propertiesWrapper.add(properties);
        HashMultimap<String, Object> exclusions = HashMultimap.create();
        if (importedCode != null) {
            exclusions.put("state.id", PrismState.PROGRAM_DISABLED_COMPLETED);
        }
        exclusions.put("state.id", PrismState.PROGRAM_REJECTED);
        exclusions.put("state.id", PrismState.PROGRAM_WITHDRAWN);
        return new ResourceSignature(propertiesWrapper, exclusions);
    }

}
