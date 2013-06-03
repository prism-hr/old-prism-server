package com.zuehlke.pgadmissions.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Entity(name = "PROGRAM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Program extends Authorisable implements Serializable {

    private static final long serialVersionUID = -9073611033741317582L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "code")
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "atas_required")
    private Boolean atasRequired;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichApprover")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichAdministrator")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichReviewer")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<RegisteredUser> programReviewers = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichInterviewer")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<RegisteredUser> interviewers = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichSupervisor")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<RegisteredUser> supervisors = new ArrayList<RegisteredUser>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "programsOfWhichViewer")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<RegisteredUser> viewers = new ArrayList<RegisteredUser>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "program")
    private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "program")
    private List<ProgramClosingDate> closingDates = new ArrayList<ProgramClosingDate>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BADGE", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "id") })
    private List<Badge> badges = new ArrayList<Badge>();

    @MapKey(name = "stage")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id")
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_advert_id")
    private ProgramAdvert advert;

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

    public Map<ScoringStage, ScoringDefinition> getScoringDefinitions() {
        return scoringDefinitions;
    }

    public ProgramAdvert getAdvert() {
        return advert;
    }

    public void setAdvert(ProgramAdvert advert) {
        this.advert = advert;
    }
    
    public List<ProgramClosingDate> getClosingDates() {
        return closingDates;
    }

    public void setClosingDates(List<ProgramClosingDate> closingDates) {
        this.closingDates = closingDates;
    }

	public void addClosingDate(ProgramClosingDate closingDate) {
		checkNotNull(closingDate);
		if(containsClosingDate(closingDate.getClosingDate())){
			throw new IllegalArgumentException("Already Exists"); 
		}
		closingDates.add(closingDate);
		closingDate.setProgram(this);
	}

	public void removeClosingDate(ProgramClosingDate closingDate) {
		if(closingDate == null){
			return;
		}
		removeClosingDate(closingDate.getId());
	}
	
	public void removeClosingDate(Integer closingDateId) {
		if(closingDateId == null){
			return;
		}
		int closingDateIndex = getClosingDateIndexById(closingDateId);
		if(closingDateIndex>=0){
			closingDates.remove(closingDateIndex);
		}
	}
	
	public ProgramClosingDate getClosingDate(Date date){
		checkNotNull(date);
		int closingDateIndex = getClosingDateIndexByDate(date);
		return (closingDateIndex>=0)? closingDates.get(closingDateIndex):null;
	}
	
	public boolean containsClosingDate(Date date){
		return getClosingDateIndexByDate(date) >= 0 ;
	}
	
	private int getClosingDateIndexByDate(Date date){
		Date day = DateUtils.truncateToDay(date);
		for(int i=0;i<closingDates.size();i++){
			ProgramClosingDate closingDate = closingDates.get(i);
			if(day.equals(closingDate.getClosingDate())){
				return i;
			}
		}
		return -1;
	}

	private int getClosingDateIndexById(Integer id){
		checkNotNull(id);
		for(int i=0;i<closingDates.size();i++){
			ProgramClosingDate closingDate = closingDates.get(i);
			if(id.equals(closingDate.getId())){
				return i;
			}
		}
		return -1;
	}

	public void updateClosingDate(ProgramClosingDate closingDate) {
		checkNotNull(closingDate);
		int closingDateIndex = getClosingDateIndexById(closingDate.getId());
		if(closingDateIndex>=0){
			ProgramClosingDate storeDate = closingDates.get(closingDateIndex);
			storeDate.setClosingDate(closingDate.getClosingDate());
			storeDate.setStudyPlaces(closingDate.getStudyPlaces());
		}
		
	}

	
	
}
