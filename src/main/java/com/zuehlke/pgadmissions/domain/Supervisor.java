package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.AwareStatus;
import com.zuehlke.pgadmissions.domain.enums.PrimaryStatus;

@Entity(name="SUPERVISOR")
@Access(AccessType.FIELD)
public class Supervisor extends DomainObject<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -189828903532203309L;

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.PrimaryStatusEnumUserType")
	@Column(name="primary_supervisor")
	private PrimaryStatus primarySupervisor;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.AwareStatusEnumUserType")
	@Column(name="aware_supervisor")
	private AwareStatus awareSupervisor;
	
	@ManyToOne
	@JoinColumn(name="programme_detail_id")
	private ProgrammeDetail programmeDetail = null;
	
	private String email;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public ProgrammeDetail getProgrammeDetail() {
		return programmeDetail;
	}
	
	public void setProgrammeDetail(ProgrammeDetail programmeDetail) {
		this.programmeDetail = programmeDetail;
	}
	
	public PrimaryStatus getPrimarySupervisor() {
		return primarySupervisor;
	}
	
	public void setPrimarySupervisor(PrimaryStatus primarySupervisor) {
		this.primarySupervisor = primarySupervisor;
	}
	
	public AwareStatus getAwareSupervisor() {
		return awareSupervisor;
	}
	
	public void setAwareSupervisor(AwareStatus awareSupervisor) {
		this.awareSupervisor = awareSupervisor;
	}
}
