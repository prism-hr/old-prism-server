package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.Supervisor;

@Service
public class SupervisorService {

	
	private final SupervisorDAO supervisorDAO;

	SupervisorService() {
		this(null);
	}

	@Autowired
	public SupervisorService(SupervisorDAO supervisorDAO) {
		this.supervisorDAO = supervisorDAO;

	}

	@Transactional
	public Supervisor getSupervisorWithId(Integer id) {
		return supervisorDAO.getSupervisorWithId(id);
	}
}
