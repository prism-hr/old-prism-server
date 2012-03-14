package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.domain.PersonalDetail;

@Service
public class PersonalDetailsService {

	private final PersonalDetailDAO personalDetailDAO;

	PersonalDetailsService() {
		this(null);
	}

	@Autowired
	public PersonalDetailsService(PersonalDetailDAO personalDetailDAO) {
		this.personalDetailDAO = personalDetailDAO;

	}

	public PersonalDetail getPersonalDetailsById(Integer id) {
		return personalDetailDAO.getPersonalDetailsById(id);
	}
	
	@Transactional
	public void save(PersonalDetail personalDetails) {
		personalDetailDAO.save(personalDetails);
		
	}

}
