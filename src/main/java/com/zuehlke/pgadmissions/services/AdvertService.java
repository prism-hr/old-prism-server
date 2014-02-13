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
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

@Service
@Transactional
public class AdvertService {

    private final AdvertDAO advertDAO;
    private final ProgramDAO programDAO;

    AdvertService() {
        this(null, null);
    }

    @Autowired
    public AdvertService(AdvertDAO advertDAO, ProgramDAO programDAO) {
        this.advertDAO = advertDAO;
        this.programDAO = programDAO;
    }
    
    public Advert getAdvertById(int advertId) {
        return advertDAO.getAdvertById(advertId);
    }
    
    public List<AdvertDTO> getAdvertFeed(OpportunityListType feedKey, String feedKeyValue, HttpServletRequest request) {
        List<AdvertDTO> advertDTOs = new ArrayList<AdvertDTO>();
        
        String advertId = getAdvertIdFromRequestOrSavedRequest(request); 
        if (advertId != null) {
            advertDTOs.addAll(advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYADVERTID, advertId, null));
        }
        
        Integer selectedAdvertId = null;
        if (!advertDTOs.isEmpty() && !OpportunityListType.neverHasSelectedAdvertListType(feedKey)) {
            selectedAdvertId = advertDTOs.get(0).getId();
        }
        
        advertDTOs.addAll(shuffleAdverts(advertDAO.getAdvertFeed(feedKey, feedKeyValue, selectedAdvertId)));
        return advertDTOs;

    }
    
    private List<AdvertDTO> shuffleAdverts(List<AdvertDTO> advertDTOs) {
        Collections.shuffle(advertDTOs);
        return advertDTOs;
    }
    
    private String getAdvertIdFromRequestOrSavedRequest(HttpServletRequest request) {
        List<String> possibleRequestParameters = Arrays.asList("advert", "project", "program");
        List<String> advertSynonyms = Arrays.asList("advert", "project");
        
        String found = null;
        
        for (String parameter : possibleRequestParameters) {
            found = request.getParameter(parameter);
            if (!StringUtils.isBlank(found)) {
                if (advertSynonyms.contains(parameter)) {
                    return found;
                }
                return programDAO.getProgramIdByCode(found);
            }
        }
        
        DefaultSavedRequest savedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        
        if (savedRequest != null) {
            for (String parameter : possibleRequestParameters) {
                String[] values = savedRequest.getParameterValues(parameter);
                if (!ArrayUtils.isEmpty(values)) {
                    found = values[0];
                    if (!StringUtils.isBlank(found)) {
                        if (advertSynonyms.contains(parameter)) {
                            return found;
                        }
                        return programDAO.getProgramIdByCode(found);
                    }
                }
            }
        }
        
        return null;
    }
    
}
