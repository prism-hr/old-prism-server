package com.zuehlke.pgadmissions.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.google.common.base.Predicate;
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "program")
    @Sort(type=SortType.COMPARATOR, comparator = ProgramClosingDate.class)
    private SortedSet<ProgramClosingDate> closingDates = new TreeSet<ProgramClosingDate>();

    @MapKey(name = "stage")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "program_id")
    private Map<ScoringStage, ScoringDefinition> scoringDefinitions = new HashMap<ScoringStage, ScoringDefinition>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id")
    private Advert advert;

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

    public boolean hasSupervisors() {
        return !supervisors.isEmpty();
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

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public SortedSet<ProgramClosingDate> getClosingDates() {
        return closingDates;
    }

    public void setClosingDates(SortedSet<ProgramClosingDate> closingDates) {
        this.closingDates = closingDates;
    }

    public boolean addClosingDate(ProgramClosingDate closingDate) {
        checkNotNull(closingDate);
        if (containsClosingDate(closingDate.getClosingDate())) {
            throw new IllegalArgumentException("Already Exists");
        }
        closingDate.setProgram(this);
        return closingDates.add(closingDate);
    }

    public ProgramClosingDate getClosingDate(final Date date) {
        checkNotNull(date);
        Predicate<ProgramClosingDate> findByDate = new Predicate<ProgramClosingDate>() {
        	@Override
        	public boolean apply(ProgramClosingDate closingDate){
        		return DateUtils.truncateToDay(date).equals(closingDate.getClosingDate());
        	}
		};
        return getClosingDateByPredicate(findByDate);
    }
    
	public void removeClosingDate(Integer closingDateId) {
        checkNotNull(closingDateId);
        closingDates.remove(getClosingDate(closingDateId));
    }
	
	public boolean containsClosingDate(Date date) {
        return getClosingDate(date) != null;
    }
	
    public boolean updateClosingDate(ProgramClosingDate closingDate) {
        checkNotNull(closingDate);
        ProgramClosingDate storedDate = getClosingDate(closingDate.getId());
        if(closingDate.compareTo(storedDate)!=0 && containsClosingDate(closingDate.getClosingDate())){
        	throw new IllegalArgumentException("Already Exists");
        }
        if(storedDate!=null){
	        storedDate.setClosingDate(closingDate.getClosingDate());
	        storedDate.setStudyPlaces(closingDate.getStudyPlaces());
	        return true;
        }
        return false;
    }
    
    private ProgramClosingDate getClosingDate(final Integer id) {
   	 checkNotNull(id);
   	 Predicate<ProgramClosingDate> findById = new Predicate<ProgramClosingDate>() {
        	@Override
        	public boolean apply(ProgramClosingDate closingDate){
        		return id.equals(closingDate.getId());
        	}
		};
		return getClosingDateByPredicate(findById);
	}
    
    private ProgramClosingDate getClosingDateByPredicate(Predicate<ProgramClosingDate> matchClosingDate) {
    	for (ProgramClosingDate closingDate:closingDates) {
            if(matchClosingDate.apply(closingDate)){
            	return closingDate;
            }
        }
    	return null;
	}

	

}
