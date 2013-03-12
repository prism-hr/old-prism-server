package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EmploymentPositionDAO;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;

@Service
@Transactional
public class EmploymentPositionService {

	private final EmploymentPositionDAO employmentPositionDAO;
	
	public EmploymentPositionService(){
		this(null);
	}

	@Autowired
	public EmploymentPositionService(EmploymentPositionDAO employmentPositionDAO) {
		this.employmentPositionDAO = employmentPositionDAO;
	}

	public EmploymentPosition getEmploymentPositionById(Integer id) {
		return employmentPositionDAO.getEmploymentPositionById(id);
	}

	public void save(EmploymentPosition employmentPosition) {
		employmentPositionDAO.save(employmentPosition);		
	}

	public void delete(EmploymentPosition employmentPosition) {
		employmentPositionDAO.delete(employmentPosition);		
	}
}
