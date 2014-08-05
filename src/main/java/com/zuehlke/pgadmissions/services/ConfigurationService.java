package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ConfigurationDAO;
import com.zuehlke.pgadmissions.domain.Configuration;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;

@Service
@Transactional
public class ConfigurationService {

    @Autowired
    private ConfigurationDAO configurationDAO;
    
    public List<Configuration> getConfigurations(Resource resource) {
        return configurationDAO.getConfigurations(resource);
    }

    public void saveServiceLevels(ServiceLevelsDTO serviceLevelsDTO) {
        // TODO implement
    }
    
    public ServiceLevelsDTO getServiceLevels() {
        // TODO implement
        return null;
    }

}