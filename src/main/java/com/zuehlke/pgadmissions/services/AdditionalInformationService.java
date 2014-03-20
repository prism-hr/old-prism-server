package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdditionalInformationDAO;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;

@Service
@Transactional
public class AdditionalInformationService {

    @Autowired
	private AdditionalInformationDAO infoDAO;

	public void save(AdditionalInformation info) {
		infoDAO.save(info);
	}
}
