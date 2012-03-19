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
	private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();
	
	private List<RegisteredUser> superadministrators = new ArrayList<RegisteredUser>();

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
	
	@ManyToMany
	@JoinTable(name = "PROGRAM_APPROVER_LINK", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "registered_user_id") })
	@Access(AccessType.PROPERTY)
	public List<RegisteredUser> getApprovers() {
		return approvers;
	}
	

	public void setApprovers(List<RegisteredUser> approvers) {
		//THIS IS A HACK. To be changed.
		if(this.approvers.size() == approvers.size() && this.approvers.containsAll(approvers)){
			return;
		}
		this.approvers.clear();
		this.approvers.addAll(approvers);
	}
	
	@ManyToMany
	@JoinTable(name = "PROGRAM_APPROVER_LINK", joinColumns = { @JoinColumn(name = "program_id") }, inverseJoinColumns = { @JoinColumn(name = "registered_user_id") })
	@Access(AccessType.PROPERTY)
	public List<RegisteredUser> getSuperadministrators() {
		return superadministrators;
	}
	
	public void setSuperadministrators(List<RegisteredUser> superadministrators) {
		this.superadministrators = superadministrators;
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

}
