package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "PROGRAM", uniqueConstraints = { @UniqueConstraint(columnNames = { "code", "institution_id" }),
        @UniqueConstraint(columnNames = { "title", "institution_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Program extends Advert {

    @Column(name = "code", nullable = true)
    private String code;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "require_project_definition")
    private Boolean requireProjectDefinition;

    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "program")
    private Set<Project> projects = Sets.newHashSet();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Application> applications = Sets.newHashSet();

    @Column(name = "is_imported", nullable = false)
    private boolean imported;

    @OneToMany(mappedBy = "program")
    @OrderBy("applicationStartDate")
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();

    // FIXME: repair the mapping once the new table has been mapped
    @MapKey(name = "stage")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id")
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();

    @ManyToOne
    @JoinColumn(name = "program_type_id", nullable = false)
    private ProgramType programType;

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "previous_state_id", nullable = true)
    private State previousState;
    
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public List<ProgramInstance> getInstances() {
        return instances;
    }

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }

    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
    }

    public Map<ScoringStage, ScoringDefinition> getScoringDefinitions() {
        return scoringDefinitions;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public List<ScoringStage> getCustomQuestionCoverage() {
        return new ArrayList<ScoringStage>(getScoringDefinitions().keySet());
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public Program withId(Integer id) {
        setId(id);
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

    public Program withStudyDuration(Integer studyDuration) {
        setStudyDuration(studyDuration);
        return this;
    }

    public Program withFunding(String funding) {
        setFunding(funding);
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

    public Program withRequireProjectDefinition(boolean flag) {
        requireProjectDefinition = flag;
        return this;
    }

    public Program withInstances(ProgramInstance... instances) {
        this.instances.addAll(Arrays.asList(instances));
        return this;
    }

    public Program withScoringDefinitions(Map<ScoringStage, ScoringDefinition> scoringDefinitions) {
        this.scoringDefinitions.putAll(scoringDefinitions);
        return this;
    }

    public Program withCode(String code) {
        this.code = code;
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

    public Program withImported(boolean imported) {
        this.imported = imported;
        return this;
    }

    public Program withProgramType(ProgramType programType) {
        this.programType = programType;
        return this;
    }
    
    public Program withInitialData(Institution institution, String code, String title) {
        this.institution = Preconditions.checkNotNull(institution);
        this.code = code;
        this.title = Preconditions.checkNotNull(title);
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
    public Application getApplication() {
        return null;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties1 = Maps.newHashMap();    
        properties1.put("institution", institution);
        properties1.put("code", code);
        HashMap<String, Object> properties2 = Maps.newHashMap();    
        properties2.put("institution", institution);
        properties2.put("title", title);  
        propertiesWrapper.add(properties1);
        propertiesWrapper.add(properties2);
        HashMultimap<String, Object> exclusions = HashMultimap.create();
        exclusions.put("state.id", PrismState.PROGRAM_REJECTED);
        exclusions.put("state.id", PrismState.PROGRAM_WITHDRAWN);
        return new ResourceSignature(propertiesWrapper, exclusions);
    }

    @Override
    public String generateCode() {
        String postfix = code;
        if (!imported) {
            postfix = String.format("%010d", getId());
        }
        return String.format("%010d", institution.getCode()) + "-" + postfix;
    }

    @Override
    public LocalDate getDueDateBaseline() {
        return new LocalDate();
    }

}
