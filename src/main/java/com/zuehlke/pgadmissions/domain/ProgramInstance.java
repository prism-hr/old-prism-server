package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity(name = "PROGRAM_INSTANCE")
@Access(AccessType.FIELD)
public class ProgramInstance extends DomainObject<Integer> implements ProgramInstanceInterface, ImportedObject {

	private static final long serialVersionUID = -5414750352686318033L;

	@Column(name="deadline")
	@Temporal(value = TemporalType.DATE)
	private Date applicationDeadline;
	
	@Column(name="start_date")
    @Temporal(value = TemporalType.DATE)
    private Date applicationStartDate;
    
	@Column(name="academic_year")
    private String academicYear;
    
	@Column(name = "study_option")
	private String studyOption;
	
	@Column(name = "identifier")
    private String identifier;
	
	@Column(name = "study_code")
    private String studyOptionCode;
	
	@Column(name = "enabled")
    private Boolean enabled;
    
	@Transient
	private int sequence;
	
	@ManyToOne(fetch = FetchType.LAZY)
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

    public Date getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(Date applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public String getAcademic_year() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(String studyOption) {
        this.studyOption = studyOption;
    }

    public String getStudyOptionCode() {
        return studyOptionCode;
    }

    public void setStudyOptionCode(String studyCode) {
        this.studyOptionCode = studyCode;
    }

	@Override
	public String getStringCode() {
		return program.getCode();
	}

	@Override
	public String getName() {
		return program.getTitle();
	}

	@Override
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Boolean getEnabled() {
		return this.enabled;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
