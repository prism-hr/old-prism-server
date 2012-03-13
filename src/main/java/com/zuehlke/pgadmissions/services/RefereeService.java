package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Referee;

@Service
public class RefereeService {

	private final RefereeDAO refereeDAO;

	RefereeService() {
		this(null);
	}

	@Autowired
	public RefereeService(RefereeDAO refereeDAO) {
		this.refereeDAO = refereeDAO;

	}

	@Transactional
	public Referee getRefereeById(Integer id) {
		return refereeDAO.getRefereeById(id);
	}
	
	@Transactional
	public void save(Referee referee) {
		refereeDAO.save(referee);
	}

}
