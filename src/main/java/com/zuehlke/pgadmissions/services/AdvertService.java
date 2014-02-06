package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

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

    public List<AdvertDTO> getAdvertFeed(OpportunityListType feedKey, String feedKeyValue, HttpServletRequest request) {
        List<AdvertDTO> adverts = null;
        List<AdvertDTO> selectedAdvert = getAdvertDTOFromSession(request); 
        
        Integer selectedAdvertId = 0;
        if (!selectedAdvert.isEmpty()) {
            selectedAdvertId = selectedAdvert.get(0).getId();
        }
        
        if (feedKey == null && feedKeyValue == null) {
            adverts = advertDAO.getActiveAdverts(selectedAdvertId);
            Collections.shuffle(adverts);
        } else {
            switch (feedKey) {
            case OPPORTUNITIESBYFEEDID:
                adverts = advertDAO.getAdvertDTOsByFeedId(Integer.parseInt(feedKeyValue), selectedAdvertId);
                Collections.shuffle(adverts);
                break;
            case OPPORTUNITIESBYUSERUPI:
                adverts = advertDAO.getAdvertDTOsByUserUPI(feedKeyValue, selectedAdvertId);
                Collections.shuffle(adverts);
                break;
            case OPPORTUNITIESBYUSERUSERNAME:
                adverts = advertDAO.getAdvertDTOsByUserUsername(feedKeyValue, selectedAdvertId);
                Collections.shuffle(adverts);
                break;
            case RECOMMENDEDOPPORTUNTIIES:
                adverts = advertDAO.getRecommendedAdvertDTOs(Integer.parseInt(feedKeyValue));
                break;
            case CURRENTOPPORTUNITY:
                adverts = Arrays.asList(advertDAO.getAdvertDTOByAdvertId(feedKeyValue));
                break;
            }
        }
        
        if (!selectedAdvert.isEmpty()) {
            adverts.add(0, adverts.get(0));
        }
        
        return adverts;
        
    }
    
    public List<AdvertDTO> getAdvertDTOFromSession(HttpServletRequest request) {
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        String advertId = getSavedRequestParam(defaultSavedRequest, "advert");
        String programCode = getSavedRequestParam(defaultSavedRequest, "program");
        String projectId = getSavedRequestParam(defaultSavedRequest, "project");
        
        List<AdvertDTO> advertDTO = new ArrayList<AdvertDTO>();
        
        if (advertId != null) {
            advertDTO.add(advertDAO.getAdvertDTOByAdvertId(advertId));
            advertDTO.get(0).setSelected(true);
        } else if (programCode != null) {
            advertDTO.add(advertDAO.getAdvertDTOByProgramCode(programCode));
            advertDTO.get(0).setSelected(true);
        } else if (projectId != null) {
            advertDTO.add(advertDAO.getAdvertDTOByProjectId(Integer.parseInt(projectId)));
            advertDTO.get(0).setSelected(true);
        }
        
        return advertDTO;
    }
    
    private String getSavedRequestParam(DefaultSavedRequest savedRequest, String paramName) {
        if (savedRequest != null) {
            String[] values = savedRequest.getParameterValues(paramName);
            if (!ArrayUtils.isEmpty(values) && !StringUtils.isBlank(values[0])) {
                return values[0];
            }
        }
        return null;
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
