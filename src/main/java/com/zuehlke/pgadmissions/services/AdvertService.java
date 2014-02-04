package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

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
        return advertDAO.getActiveAdverts();
    }
    
    public List<Advert> getRecommendedAdverts(RegisteredUser user) {
        return advertDAO.getRecommendedAdverts(user);
    }
    
    public List<Advert> getAdvertsByUserUPI(String userUPI) {
        return advertDAO.getAdvertsByUserUPI(userUPI);
    }
    
    public List<Advert> getAdvertsByUserUsername(String username) {
        return advertDAO.getAdvertsByUserUsername(username);
    }

    public List<Advert> getAdvertsByFeedId(Integer feedId) {
        return advertDAO.getAdvertsByFeedId(feedId);
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
	
	public Advert getAdvertFromSession(String advertId, String programCode, String projectId) {
	    if (advertId != null) {
	        return advertDAO.getAdvertById(Integer.parseInt(advertId));
	    } else if (programCode != null) {
	        return advertDAO.getProgramAdvertByProgramCode(programCode);
	    } else if (projectId != null) {
	        return advertDAO.getProjectAdvertByProjectId(Integer.parseInt(projectId));
	    }
	    return null;
	}

}
