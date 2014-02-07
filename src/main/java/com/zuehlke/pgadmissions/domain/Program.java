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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

@Entity(name = "PROGRAM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Program extends Advert {

    private static final long serialVersionUID = -9073611033741317582L;

    @Column(name = "code")
    private String code;

    @Column(name = "atas_required")
    private Boolean atasRequired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private QualificationInstitution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private ProgramFeed programFeed;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichApprover")
    private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichAdministrator")
    private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichViewer")
    private List<RegisteredUser> viewers = new ArrayList<RegisteredUser>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "program")
    @OrderBy("applicationStartDate")
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id", nullable = false)
    private List<ProgramClosingDate> closingDates = new ArrayList<ProgramClosingDate>();

    @MapKey(name = "stage")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id")
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "program")
    private List<Project> projects = new ArrayList<Project>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "program")
    private List<ApplicationForm> applications = new ArrayList<ApplicationForm>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_type_id")
    private ProgramType programType;

    public void setCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
    public List<RegisteredUser> getApprovers() {
        return approvers;
    }

    public void setApprovers(final List<RegisteredUser> approvers) {
        this.approvers.clear();
        this.approvers.addAll(approvers);
    }

    public List<RegisteredUser> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(final List<RegisteredUser> administrators) {
        this.administrators.clear();
        this.administrators.addAll(administrators);
    }

    public boolean isApprover(final RegisteredUser user) {
        return (getApprovers().contains(user));
    }

    public boolean isAdministrator(final RegisteredUser user) {
        return (getAdministrators().contains(user));
    }

    public boolean isViewer(final RegisteredUser user) {
        return (getViewers().contains(user));
    }
    
    public List<ProgramInstance> getInstances() {
        return instances;
    }

    public void setInstances(final List<ProgramInstance> instances) {
        this.instances = instances;
    }

    public Boolean getAtasRequired() {
        return atasRequired;
    }

    public void setAtasRequired(final Boolean atasRequired) {
        this.atasRequired = atasRequired;
    }

    public List<RegisteredUser> getViewers() {
        return viewers;
    }

    public void setViewers(final List<RegisteredUser> viewers) {
        this.viewers.clear();
        this.approvers.addAll(viewers);
    }

    public Map<ScoringStage, ScoringDefinition> getScoringDefinitions() {
        return scoringDefinitions;
    }

    public List<ProgramClosingDate> getClosingDates() {
        return closingDates;
    }

    public void setClosingDates(List<ProgramClosingDate> closingDates) {
        this.closingDates = closingDates;
    }

    public QualificationInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(QualificationInstitution institution) {
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

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<ApplicationForm> getApplications() {
        return applications;
    }
    
    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

}
