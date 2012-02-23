package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ProgramBuilder {

	private Integer id;
	private String code;
	private String title;
	private String description;
	private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();
	
	public ProgramBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ProgramBuilder approver(RegisteredUser... approvers){
		for (RegisteredUser approver : approvers) {
			this.approvers.add(approver);
		}
		return this;
	}
	public ProgramBuilder code(String code){
		this.code = code;
		return this;
	}
	
	public ProgramBuilder title(String title){
		this.title = title;
		return this;
	}
	public ProgramBuilder description(String description){
		this.description = description;
		return this;
	}
	
	public Program toProgram(){
		Program program = new Program();
		program.setId(id);
		program.setCode(code);
		program.setTitle(title);
		program.setDescription(description);
		program.getApprovers().addAll(approvers);
		return program;
	}
}
