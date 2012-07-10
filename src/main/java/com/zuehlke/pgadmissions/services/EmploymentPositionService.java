package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EmploymentPositionDAO;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;

@Service
public class EmploymentPositionService {

	private final EmploymentPositionDAO employmentPositionDAO;
	
	EmploymentPositionService(){
		this(null);
	}

	@Autowired
	public EmploymentPositionService(EmploymentPositionDAO employmentPositionDAO) {
		this.employmentPositionDAO = employmentPositionDAO;

	}

	public EmploymentPosition getEmploymentPositionById(Integer id) {
		return employmentPositionDAO.getEmploymentPositionById(id);
	}

	@Transactional
	public void save(EmploymentPosition employmentPosition) {
		employmentPositionDAO.save(employmentPosition);		
	}

	@Transactional
	public void delete(EmploymentPosition employmentPosition) {
		employmentPositionDAO.delete(employmentPosition);		
	}
}
