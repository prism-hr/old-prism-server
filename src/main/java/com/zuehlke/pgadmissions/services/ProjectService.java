package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.rest.domain.ResourceConsoleListRowRepresentation;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ResourceService resourceService;

    public List<ResourceConsoleListRowRepresentation> getConsoleListBlock(Integer page, Integer perPage) {
        return resourceService.getConsoleListBlock(Project.class, page, perPage);
    }
    
}
