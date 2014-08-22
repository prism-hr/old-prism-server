package com.zuehlke.pgadmissions.services;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private EntityService entityService;
    
    @Autowired
    private ResourceService resourceService;

    public Project create(User user, ProjectDTO projectDTO) {
        // TODO: remember to set due date to value of linked closing date, and on update
        return null;
    }
    
    public void save(Project project) {
        entityService.save(project);
    }

    public LocalDate resolveDueDateBaseline(Project project) {
        AdvertClosingDate closingDate = project.getClosingDate();
        return closingDate == null ? new LocalDate() : closingDate.getClosingDate();
    }

    public void buildApplicationSummary() {
        // TODO Auto-generated method stub
        
    }
    
}
