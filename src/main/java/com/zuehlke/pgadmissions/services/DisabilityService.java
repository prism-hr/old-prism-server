package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.DisabilityDAO;
import com.zuehlke.pgadmissions.domain.Disability;

@Service
public class DisabilityService {
	private DisabilityDAO disabilityDAO;

	public DisabilityService() {
		this(null);
	}

	@Autowired
	public DisabilityService(DisabilityDAO disabilityDAO) {
		this.disabilityDAO = disabilityDAO;
	}

	public List<Disability> getAllDisabilities() {
		return disabilityDAO.getAllDisabilities();
	}

	public Disability getDisabilityById(Integer disabilityId) {
		return disabilityDAO.getDisabilityById(disabilityId);
	}
}
