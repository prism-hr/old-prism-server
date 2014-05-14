package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.AdvertType;
import com.zuehlke.pgadmissions.domain.enums.ProgramState;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "PROGRAM", uniqueConstraints = { @UniqueConstraint(columnNames = { "code", "institution_id" }),
        @UniqueConstraint(columnNames = { "title", "institution_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Program extends Advert implements PrismScope {

    private static final long serialVersionUID = -9073611033741317582L;

    @Column(name = "state_id")
    @Enumerated(EnumType.STRING)
    private ProgramState state;

    @Column(name = "code")
    private String code;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "title")
    private String title;

    @Column(name = "require_project_definition")
    private Boolean requireProjectDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "is_imported", nullable = false)
    private boolean imported;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "program")
    @OrderBy("applicationStartDate")
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();

    @MapKey(name = "stage")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id")
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "program")
    private List<Project> projects = new ArrayList<Project>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_type_id")
    private ProgramType programType;

    public Program() {
        super.setAdvertType(AdvertType.PROGRAM);
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
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

    public List<Project> getProjects() {
        return projects;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public ProgramState getState() {
        return state;
    }

    public void setState(ProgramState state) {
        this.state = state;
    }

    @Override
    public Program getProgram() {
        return this;
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return state == ProgramState.PROGRAM_APPROVED;
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

    public Program withState(ProgramState state) {
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

    @Override
    public String getScopeName() {
        return "program";
    }
    
    @Override
    public PrismSystem getSystem() {
        return getInstitution().getSystem();
    }
    
    @Override
    public String getType() {
        return "program";
    }

}
