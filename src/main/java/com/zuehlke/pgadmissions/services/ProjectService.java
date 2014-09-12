package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private EntityService entityService;

    public Project getById(Integer id) {
        return entityService.getById(Project.class, id);
    }

    public Project create(User user, ProjectDTO projectDTO) {
        // TODO: remember to set title in advert to value of project title
        return null;
    }

    public void save(Project project) {
        entityService.save(project);
    }

    public LocalDate resolveDueDateBaseline(Project project, Comment comment) {
        if (comment.isProjectCreateOrUpdateComment()) {
            AdvertClosingDate closingDate = project.getAdvert().getClosingDate();
            if (closingDate == null) {
                return programService.getProgramClosureDate(project.getProgram());
            } else {
                return closingDate.getClosingDate();
            }
        }
        return null;
    }

    public void postProcessProject(Project project, Comment comment) {
        if (comment.isProjectCreateOrUpdateComment()) {
            project.getAdvert().setSequenceIdentifier(project.getSequenceIdentifier() + "-" + project.getResourceScope().getShortCode());
        }
    }

    public void updateProjectsLinkedToProgramDueDate(Program program) {
        List<Project> linkedProjects = projectDAO.getProjectsLinkedToProgramDueDate(program);

        for (Project linkedProject : linkedProjects) {
            linkedProject.setDueDate(program.getDueDate());
        }
    }

}
