package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;

public class ProjectBuilder {

	private Integer id;
	private String code;
	private String title;
	private String description;
	private Program program;
	
	public ProjectBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ProjectBuilder code(String code){
		this.code = code;
		return this;
	}
	
	public ProjectBuilder title(String title){
		this.title = title;
		return this;
	}
	public ProjectBuilder description(String description){
		this.description = description;
		return this;
	}
	
	public ProjectBuilder program(Program program){
		this.program = program;
		return this;
	}
	
	public Project toProject(){
		Project project = new Project();
		project.setId(id);
		project.setCode(code);
		project.setTitle(title);
		project.setDescription(description);
		project.setProgram(program);
		return project;
	}
}
