package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.zuehlke.pgadmissions.domain.enums.Authority;


@Entity(name="PROGRAM")
@Access(AccessType.FIELD)
public class Program extends DomainObject<Integer> {

	private static final long serialVersionUID = -9073611033741317582L;
	private String code;
	private String title;
	private String description;
	
	@ManyToMany
	@JoinTable(name = "PROGRAM_APPROVER_LINK", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "registered_user_id") })
	private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();
	
	@ManyToMany
	@JoinTable(name = "PROGRAM_ADMINISTRATOR_LINK", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "administrator_id") })
	private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();
	
	@ManyToMany
	@JoinTable(name = "PROGRAM_REVIEWER_LINK", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "reviewer_id") })
	private List<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();

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

	public void setDescription(String description) {
		this.description = description;	
		
	}
	
	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
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
		this.administrators.clear() ;
		this.administrators.addAll(administrators);
		
	}
	


	public List<RegisteredUser> getReviewers() {
		return reviewers;
	}
	
	public void setReviewers(List<RegisteredUser> reviewers) {
		this.reviewers.clear();
		this.reviewers.addAll(reviewers);
	}
	
	public boolean isApprover(RegisteredUser user) {
		if(!user.isInRole(Authority.APPROVER)){
			return false;
		}
		for (RegisteredUser approver : approvers) {
			if(approver.equals(user)){
				return true;
			}
		}
		return false;
	}
	
	public void addUserToRightRoleList(RegisteredUser user, Role role){
		if(role.getAuthorityEnum().equals(Authority.ADMINISTRATOR)){
			administrators.add(user);
		}
		if(role.getAuthorityEnum().equals(Authority.REVIEWER)){
			reviewers.add(user);
		}
		if(role.getAuthorityEnum().equals(Authority.APPROVER)){
			approvers.add(user);
		}
	}
	
	public boolean isUserWithRoleInProgram(RegisteredUser user, Role role){
		if(role.getAuthorityEnum().equals(Authority.ADMINISTRATOR)){
			if(administrators.contains(user)){
				return true;
			}
		}
		if(role.getAuthorityEnum().equals(Authority.REVIEWER)){
			if(reviewers.contains(user)){
				return true;
			}
		}
		if(role.getAuthorityEnum().equals(Authority.APPROVER)){
			if(approvers.contains(user)){
				return true;
			}
		}
		return false;
	}

}
