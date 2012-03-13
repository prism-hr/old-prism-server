package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;


@Entity(name="APPLICATION_FORM_PROGRAMME_DETAIL")
@Access(AccessType.FIELD) 
public class ProgrammeDetail  extends DomainObject<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	
	@Column(name="programme_name")
	private String programmeName;
	
	@Column(name="study_option")
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.StudyOptionEnumUserType")
	private StudyOption studyOption;
	
	@Column(name="project_name")
	private String projectName;
	
	@Temporal(value = TemporalType.DATE)
	@Column(name="start_date")
	private Date startDate;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ReferrerEnumUserType")
	private Referrer referrer;
	
	@OneToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application = null;
	
	@OneToMany(cascade={javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinColumn(name = "programme_detail_id")
	private List<Supervisor> supervisors = new ArrayList<Supervisor>();
	
	public String getProgrammeName() {
		return programmeName;
	}
	
	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Referrer getReferrer() {
		return referrer;
	}
	
	public void setReferrer(Referrer referrer) {
		this.referrer = referrer;
	}
	
	public StudyOption getStudyOption() {
		return studyOption;
	}
	
	public void setStudyOption(StudyOption studyOption) {
		this.studyOption = studyOption;
	}
	
	public ApplicationForm getApplication() {
		return application;
	}
	
	public void setApplication(ApplicationForm application) {
		this.application = application;
	}
	
	public List<Supervisor> getSupervisors() {
		return supervisors;
	}
	
	public void setSupervisors(List<Supervisor> supervisors) {
		this.supervisors = supervisors;
		if(supervisors != null && !supervisors.isEmpty()){
			int size = supervisors.size();
			for (int i = size -1; i >= 0 ;i--){
				Supervisor supervisor = supervisors.get(i);
				if(supervisor == null){
					supervisors.remove(i);
				}
			}
		}
	}
}
