package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.ReferenceDAO;
import com.zuehlke.pgadmissions.domain.Reference;

@Service
public class ReferenceService {

	private final ReferenceDAO referenceDAO;

	ReferenceService() {
		this(null);
	}

	@Autowired
	public ReferenceService(ReferenceDAO referenceDAO) {
		this.referenceDAO = referenceDAO;
	}

	public Reference getReferenceById(Integer id) {
		return referenceDAO.getReferenceById(id);
	}

}
