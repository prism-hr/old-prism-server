package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.StudyOption;

@Entity(name = "PROGRAM_INSTANCE")
@Access(AccessType.FIELD)
public class ProgramInstance extends DomainObject<Integer> {


	private static final long serialVersionUID = -5414750352686318033L;

	@Column(name="deadline")
	@Temporal(value = TemporalType.DATE)
	private Date applicationDeadline;
	
	@Column(name = "study_option")
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.StudyOptionEnumUserType")
	private StudyOption studyOption;
	
	private int sequence;
	
	@ManyToOne
	@JoinColumn(name = "program_id")
	private Program program;
	
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

	public Date getApplicationDeadline() {
		return applicationDeadline;
	}

	public void setApplicationDeadline(Date applicationDeadline) {
		this.applicationDeadline = applicationDeadline;
	}

	public StudyOption getStudyOption() {
		return studyOption;
	}

	public void setStudyOption(StudyOption studyOption) {
		this.studyOption = studyOption;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

}
