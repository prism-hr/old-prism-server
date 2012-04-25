package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ProgramBuilder {

	private Integer id;
	private String code;
	private String title;
	
	private List<RegisteredUser> approvers = new ArrayList<RegisteredUser>();
	private List<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();
	
	private List<RegisteredUser> administrators = new ArrayList<RegisteredUser>();
	
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
	
	public ProgramBuilder administrators(RegisteredUser... administrators){
		for (RegisteredUser administrator : administrators) {
			this.administrators.add(administrator);
		}
		return this;
	}
	
	public ProgramBuilder reviewers(RegisteredUser... reviewers){
		for (RegisteredUser reviewer : reviewers) {
			this.reviewers.add(reviewer);
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

	
	public Program toProgram(){
		Program program = new Program();
		program.setId(id);
		program.setCode(code);
		program.setTitle(title);

		program.getApprovers().addAll(approvers);
		program.getAdministrators().addAll(administrators);
		program.getReviewers().addAll(reviewers);
		return program;
	}
}
