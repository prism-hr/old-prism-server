package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Supervisor;

@Service
public class ProgrammeService {

	
	private final ProgrammeDetailDAO programmeDetailDAO;

	ProgrammeService() {
		this(null);
	}

	@Autowired
	public ProgrammeService(ProgrammeDetailDAO programmeDetailDAO) {
		this.programmeDetailDAO = programmeDetailDAO;

	}

	@Transactional
	public ProgrammeDetail getProgrammeDetailsById(Integer id) {
		return programmeDetailDAO.getProgrammeDetailWithId(id);
	}
	
	@Transactional
	public void save(ProgrammeDetail pd) {
		programmeDetailDAO.save(pd);
	}
	
}
