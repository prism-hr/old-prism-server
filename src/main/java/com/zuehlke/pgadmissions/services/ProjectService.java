package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private ResourceService resourceService;

    public List<ResourceConsoleListRowDTO> getConsoleListBlock(Integer page, Integer perPage) {
        return resourceService.getConsoleListBlock(Project.class, page, perPage);
    }
    
    public Project create(User user, ProjectDTO projectDTO) {
        return null;
    }
    
    public void save(Project project) {
        entityService.save(project);
    }
    
}
