package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity(name = "PROGRAM")
public class Program extends Authorisable implements Serializable {

    private static final long serialVersionUID = -9073611033741317582L;

    @Id
    @GeneratedValue
    private Integer id;

    private String code;

    private String title;

    private boolean enabled;

    @Column(name = "atas_required")
    private Boolean atasRequired;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichApprover")
    private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichAdministrator")
    private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichReviewer")
    private List<RegisteredUser> programReviewers = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichInterviewer")
    private List<RegisteredUser> interviewers = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichSupervisor")
    private List<RegisteredUser> supervisors = new ArrayList<RegisteredUser>();
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichViewer")
    private List<RegisteredUser> viewers = new ArrayList<RegisteredUser>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "program")
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BADGE", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "id") })
    private List<Badge> badges = new ArrayList<Badge>();

    public Program() {
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public void setBadges(final List<Badge> badges) {
        this.badges = badges;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public List<RegisteredUser> getProgramReviewers() {
        return programReviewers;
    }

    public void setProgramReviewers(final List<RegisteredUser> reviewers) {
        this.programReviewers.clear();
        this.programReviewers.addAll(reviewers);
    }

    public boolean isApprover(final RegisteredUser user) {
        return isApproverInProgramme(this, user);
    }

    public boolean isAdministrator(final RegisteredUser user) {
        return isAdminInProgramme(this, user);
    }

    public boolean isInterviewerOfProgram(final RegisteredUser user) {
        return isInterviewerOfProgram(this, user);
    }

    public List<ProgramInstance> getInstances() {
        return instances;
    }

    public void setInstances(final List<ProgramInstance> instances) {
        this.instances = instances;
    }

    public List<RegisteredUser> getInterviewers() {
        return interviewers;
    }

    public void setInterviewers(final List<RegisteredUser> interviewers) {
        this.interviewers = interviewers;
    }

    public List<RegisteredUser> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(final List<RegisteredUser> supervisors) {
        this.supervisors = supervisors;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
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
}
