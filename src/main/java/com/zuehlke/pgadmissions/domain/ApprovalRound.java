package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity(name = "APPROVAL_ROUND")
public class ApprovalRound implements Serializable {

	private static final long serialVersionUID = 1068777060574638531L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "approval_round_id")
	private List<Supervisor> supervisors = new ArrayList<Supervisor>();
	
	@Column(name = "missing_qualification_explanation")
	private String missingQualificationExplanation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_form_id")	
	private ApplicationForm application;	
	
	@Column(name = "created_date", insertable = false)
	@Generated(GenerationTime.INSERT)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}


	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}
	
	public String getMissingQualificationExplanation() {
        return missingQualificationExplanation;
    }

    public void setMissingQualificationExplanation(String missingQualificationExplanation) {
        this.missingQualificationExplanation = missingQualificationExplanation;
    }

    public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public List<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(List<Supervisor> supervisors) {		
		this.supervisors.clear();
		for (Supervisor supervisor : supervisors) {
			if(supervisor != null){
				this.supervisors.add(supervisor);
			}
		}
	}

}
