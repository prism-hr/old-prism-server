package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Advert;

@Service
@Transactional
public class AdvertService {

	private final AdvertDAO advertDAO;

	AdvertService() {
		this( null);
	}

	@Autowired
	public AdvertService(AdvertDAO advertDAO) {
		this.advertDAO = advertDAO;
	}

	public List<Advert> getActiveAdverts(){
		return advertDAO.getActiveProgramAdverts();
	}

}
