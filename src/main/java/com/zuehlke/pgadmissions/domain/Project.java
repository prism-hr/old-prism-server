package com.zuehlke.pgadmissions.domain;



import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity(name="PROJECT")
@Access(AccessType.FIELD)
public class Project extends DomainObject<Integer>{

	private static final long serialVersionUID = -4047837257993195167L;
	private String code;
	private String description;
	private String title;
	
	@ManyToOne
	@JoinColumn(name="program_id")
	private Program program;

	public Program getProgram() {
		return program;
	}

	public String getTitle() {
		return title;
	}

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

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public void setCode(String code) {
		this.code = code;
		
	}

	public void setDescription(String description) {
		this.description = description;
		
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

}
