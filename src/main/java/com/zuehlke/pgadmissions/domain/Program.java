package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.zuehlke.pgadmissions.domain.enums.Authority;

@Entity(name = "PROGRAM")
@Access(AccessType.FIELD)
public class Program extends DomainObject<Integer> {

	private static final long serialVersionUID = -9073611033741317582L;
	private String code;
	private String title;


	@ManyToMany(mappedBy = "programsOfWhichApprover")
	private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();

	@ManyToMany(mappedBy = "programsOfWhichAdministrator")
	private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();

	@ManyToMany(mappedBy = "programsOfWhichReviewer")
	private List<RegisteredUser> programReviewers = new ArrayList<RegisteredUser>();
	
	@ManyToMany(mappedBy = "programsOfWhichInterviewer")
	private List<RegisteredUser> interviewers = new ArrayList<RegisteredUser>();

	@OneToMany(mappedBy = "program")
	private List<ProgramInstance> instances = new ArrayList<ProgramInstance>();
	
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
		if (!user.isInRole(Authority.APPROVER)) {
			return false;
		}
		for (RegisteredUser approver : approvers) {
			if (approver.equals(user)) {
				return true;
			}
		}
		return false;
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
}
