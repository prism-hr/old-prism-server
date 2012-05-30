package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.Program;

public class MainPageModel extends PageModel{

	private List<Program> programs;

	public List<Program> getPrograms() {
		return programs;
	}

	public void setPrograms(List<Program> projects) {
		this.programs = projects;
	}
	
}
