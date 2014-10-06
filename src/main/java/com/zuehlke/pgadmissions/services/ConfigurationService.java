package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ConfigurationDAO;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.WorkflowResource;

@Service
@Transactional
public class ConfigurationService {
    
    @Autowired
    private ConfigurationDAO configurationDAO;
    
    public <T extends WorkflowResource> T getConfiguration(Class<T> entityClass, Resource resource, WorkflowDefinition parameter) {
        return configurationDAO.getConfiguration(entityClass, resource, parameter);
    }
    
}
