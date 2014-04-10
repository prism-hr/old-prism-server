package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.AdvertType;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

@Entity(name = "PROGRAM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Program extends Advert implements PrismScope {

    private static final long serialVersionUID = -9073611033741317582L;

    @Column(name = "code")
    private String code;

    @Column(name = "atas_required")
    private Boolean atasRequired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private ProgramFeed programFeed;

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

    @Column(name = "locked")
    private boolean locked;

    public Program() {
        super.setAdvertType(AdvertType.PROGRAM);
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public List<ProgramInstance> getInstances() {
        return instances;
    }

    public Boolean getAtasRequired() {
        return atasRequired;
    }

    public void setAtasRequired(final Boolean atasRequired) {
        this.atasRequired = atasRequired;
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

    public ProgramFeed getProgramFeed() {
        return programFeed;
    }

    public void setProgramFeed(ProgramFeed programFeed) {
        this.programFeed = programFeed;
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

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public Program getProgram() {
        return this;
    }

    @Override
    public Project getProject() {
        return null;
    }

}
