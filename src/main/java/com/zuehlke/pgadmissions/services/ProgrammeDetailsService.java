package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;

@Service
public class ProgrammeDetailsService {

	
	private final ProgrammeDetailDAO programmeDetailDAO;

	ProgrammeDetailsService() {
		this(null);
	}

	@Autowired
	public ProgrammeDetailsService(ProgrammeDetailDAO programmeDetailDAO) {
		this.programmeDetailDAO = programmeDetailDAO;

	}

	@Transactional
	public ProgrammeDetails getProgrammeDetailsById(Integer id) {
		return programmeDetailDAO.getProgrammeDetailWithId(id);
	}
	
	@Transactional
	public void save(ProgrammeDetails pd) {
		programmeDetailDAO.save(pd);
	}
	
}
