package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;

@Service
@Transactional
public class AdvertService {

    private final AdvertDAO advertDAO;

    AdvertService() {
        this(null);
    }

    @Autowired
    public AdvertService(AdvertDAO advertDAO) {
        this.advertDAO = advertDAO;
    }

    public List<Advert> getActiveAdverts() {
        return advertDAO.getActiveProgramAdverts();
    }

    public Program getProgram(Advert advert) {
        return advertDAO.getProgram(advert);
    }

    public Project getProject(Advert advert) {
        return advertDAO.getProject(advert);
    }

	public void edit(Advert advert) {
		advertDAO.save(advert);
	}

	public Advert getAdvertById(int advertId) {
		return advertDAO.getAdvertById(advertId);
	}

}
