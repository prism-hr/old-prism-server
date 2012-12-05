package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.zuehlke.pgadmissions.domain.enums.Authority;

@Entity(name = "PROGRAM")
@Access(AccessType.FIELD)
public class Program extends DomainObject<Integer> {

	private static final long serialVersionUID = -9073611033741317582L;
	
	private String code;

	private String title;
	
	private boolean enabled;

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
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "program")
	private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();
	
	@OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BADGE", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "id") })
    private List<Badge> badges = new ArrayList<Badge>();
	
	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public List<Badge> getBadges() {
        return badges;
    }

    public void setBadges(List<Badge> badges) {
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

	public void setApprovers(List<RegisteredUser> approvers) {
		this.approvers.clear();
		this.approvers.addAll(approvers);
	}

	public List<RegisteredUser> getAdministrators() {
		return administrators;
	}

	public void setAdministrators(List<RegisteredUser> administrators) {
		this.administrators.clear();
		this.administrators.addAll(administrators);

	}

	public List<RegisteredUser> getProgramReviewers() {
		return programReviewers;
	}

	public void setProgramReviewers(List<RegisteredUser> reviewers) {
		this.programReviewers.clear();
		this.programReviewers.addAll(reviewers);
	}

	public boolean isApprover(RegisteredUser user) {
		return checkUserRole(user, Authority.APPROVER, approvers);
	}

	public boolean isAdministrator(RegisteredUser user) {
		return checkUserRole(user, Authority.ADMINISTRATOR, administrators);
	}

	private boolean checkUserRole(RegisteredUser user, Authority authority, Iterable<RegisteredUser> lookup) {
		if (!user.isInRole(authority)) {
			return false;
		}
		for (RegisteredUser lookupUser : lookup) {
			if (lookupUser.equals(user)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isInterviewerOfProgram(RegisteredUser interviewer) {
		return interviewers.contains(interviewer);
	}

	public List<ProgramInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<ProgramInstance> instances) {
		this.instances = instances;
	}

	public List<RegisteredUser> getInterviewers() {
		return interviewers;
	}

	public void setInterviewers(List<RegisteredUser> interviewers) {
		this.interviewers = interviewers;
	}

	public List<RegisteredUser> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(List<RegisteredUser> supervisors) {
		this.supervisors = supervisors;
	}

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
