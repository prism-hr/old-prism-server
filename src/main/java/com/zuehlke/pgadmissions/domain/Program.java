package com.zuehlke.pgadmissions.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class Program extends Advert {

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

    @Column(name = "program_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;

    @Column(name = "require_project_definition", nullable = false)
    private Boolean requireProjectDefinition;
    
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

    public Set<ProgramInstance> getProgramInstances() {
        return programInstances;
    }

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }
    
    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
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
        setId(id);
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

    public Program withDescription(String description) {
        setDescription(description);
        return this;
    }
    
    public Program withImmediateStart(boolean immediateStart) {
        setImmediateStart(immediateStart);
        return this;
    }

    public Program withState(State state) {
        this.state = state;
        return this;
    }

    public Program withUser(User user) {
        setUser(user);
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

    public Program withClosingDates(AdvertClosingDate... closingDates) {
        getClosingDates().addAll(Arrays.asList(closingDates));
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

    public Program withStudyDuration(Integer studyDuration) {
        setStudyDuration(studyDuration);
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
    public Application getApplication() {
        return null;
    }


    @Override
    public LocalDate getDueDateBaseline() {
        return new LocalDate();
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
