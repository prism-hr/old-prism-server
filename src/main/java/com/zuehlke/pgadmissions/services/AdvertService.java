package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Advert;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.definitions.OpportunityListType;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

@Service
@Transactional
public class AdvertService {

    @Autowired
    private AdvertDAO advertDAO;

    public List<AdvertDTO> getAdvertFeed(OpportunityListType feedKey, String feedKeyValue, String advertId) {
        List<AdvertDTO> advertDTOs = new ArrayList<AdvertDTO>();

        if (advertId != null) {
            advertDTOs.addAll(advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYADVERTID, advertId, null));
        }
        
        Integer selectedAdvertId = null;
        if (!advertDTOs.isEmpty() && !OpportunityListType.neverHasSelectedAdvertListType(feedKey)) {
            selectedAdvertId = advertDTOs.get(0).getId();
        }
        
        List<AdvertDTO> feedAdvertDTOs = advertDAO.getAdvertFeed(feedKey, feedKeyValue, selectedAdvertId);
        
        if (BooleanUtils.isTrue(OpportunityListType.shouldBeRandomisedForDisplay(feedKey))) {
            Collections.shuffle(feedAdvertDTOs);
        }
        
        advertDTOs.addAll(feedAdvertDTOs);
        return advertDTOs;

    }

    public List<Advert> getAdverts() {
        return advertDAO.getAdverts();
    }
    
}
