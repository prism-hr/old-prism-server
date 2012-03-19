package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Program;

@Service
public class ProgramsService {

	private final ProgramDAO programDAO;
	
	ProgramsService(){
		this(null);
	}
	
	@Autowired
	public ProgramsService(ProgramDAO programDAO) {
		this.programDAO = programDAO;		
	}
	

	public List<Program> getAllPrograms() {
		return programDAO.getAllPrograms();
	}
}
