package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdditionalInfoDAO;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;

@Service
@Transactional
public class AdditionalInfoService {

	private final AdditionalInfoDAO infoDAO;

	AdditionalInfoService() {
		this(null);
	}

	@Autowired
	public AdditionalInfoService(AdditionalInfoDAO addInfoDAO) {
		this.infoDAO = addInfoDAO;
	}

	public void save(AdditionalInformation info) {
		infoDAO.save(info);
	}
}