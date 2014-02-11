package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
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
    
    public Advert getAdvertById(int advertId) {
        return advertDAO.getAdvertById(advertId);
    }
    
    public List<AdvertDTO> getAdvertFeed(OpportunityListType feedKey, String feedKeyValue, HttpServletRequest request) {
        List<AdvertDTO> advertDTOs = new ArrayList<AdvertDTO>(); 
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        int selectedAdvertId = 0;
        
        String advertId = getSavedRequestParam(defaultSavedRequest, "advertId");
        if (advertId == null) {
            advertId = getSavedRequestParam(defaultSavedRequest, "programId");
            if (advertId == null) {
                advertId = getSavedRequestParam(defaultSavedRequest, "projectId");
            }
        }
        
        if (advertId != null) {
            advertDTOs.addAll(advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYADVERTID, advertId, selectedAdvertId));
        } else {
            String programCode = getSavedRequestParam(defaultSavedRequest, "programCode");
            if (programCode != null) {
                advertDTOs.addAll(advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYPROGRAMCODE, advertId, selectedAdvertId));
            }
        }  
        
        if (!advertDTOs.isEmpty()) {
            selectedAdvertId = advertDTOs.get(0).getId();
        }
        
        advertDTOs.addAll(shuffleAdverts(advertDAO.getAdvertFeed(feedKey, feedKeyValue, selectedAdvertId)));
        return advertDTOs;

    }
    
    private List<AdvertDTO> shuffleAdverts(List<AdvertDTO> advertDTOs) {
        Collections.shuffle(advertDTOs);
        return advertDTOs;
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

}
