package com.zuehlke.pgadmissions.services;

import java.util.Arrays;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectDAO projectDAO;
    
    @Autowired
    private EntityService entityService;

    public Project create(User user, ProjectDTO projectDTO) {
        // TODO: remember to set due date to value of linked closing date, and on update
        // TODO: remember to set title in advert to value of project title
        return null;
    }
    
    public void save(Project project) {
        entityService.save(project);
    }

    public LocalDate resolveDueDateBaseline(Project project) {
        AdvertClosingDate closingDate = project.getAdvert().getClosingDate();
        return closingDate == null ? new LocalDate() : closingDate.getClosingDate();
    }

    public void postProcessProject(Project project, Comment comment) {
        PrismActionCategory actionCategory = comment.getAction().getActionCategory();
        if (Arrays.asList(PrismActionCategory.CREATE_RESOURCE, PrismActionCategory.VIEW_EDIT_RESOURCE).contains(actionCategory)) {
            project.setSequenceIdentifier(project.getSequenceIdentifier() + "-" + project.getResourceScope().getShortCode());
        }
    }

}
