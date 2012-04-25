package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.EthnicityDAO;
import com.zuehlke.pgadmissions.domain.Ethnicity;

@Service
public class EthnicityService {

	@Autowired
	private EthnicityDAO ethnicityDAO;

	public List<Ethnicity> getAllEthnicities() {
		return ethnicityDAO.getAllEthnicities();
	}

	public Ethnicity getEthnicityById(Integer ethnicityId) {
		return ethnicityDAO.getEthnicityById(ethnicityId);
	}
}
