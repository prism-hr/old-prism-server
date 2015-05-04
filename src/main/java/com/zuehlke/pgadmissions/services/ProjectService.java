package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DISABLED_PENDING_REACTIVATION;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;

@Service
@Transactional
public class ProjectService {

    @Inject
    private ProjectDAO projectDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private UserService userService;

    @Inject
    private StateService stateService;

    @Inject
    private ResourceService resourceService;

    public Project getById(Integer id) {
        return entityService.getById(Project.class, id);
    }

    public void synchronizeProjects(Program program) {
        projectDAO.synchronizeProjectDueDates(program);
        projectDAO.synchronizeProjectEndDates(program);
    }

    public void restoreProjects(Program program, LocalDate baseline) {
        List<Project> projects = projectDAO.getProjectsPendingReactivation(program, baseline);
        if (!projects.isEmpty()) {
            State state = stateService.getById(PROJECT_APPROVED);
            State previousState = stateService.getById(PROJECT_DISABLED_PENDING_REACTIVATION);

            for (Project project : projects) {
                project.setState(state);
                project.setPreviousState(previousState);
                project.setDueDate(project.getEndDate());

                project.getResourceStates().clear();
                project.getResourcePreviousStates().clear();
                entityService.flush();

                entityService.createOrUpdate(new ResourceState().withResource(project).withState(state).withPrimaryState(true));
                entityService.createOrUpdate(new ResourcePreviousState().withResource(project).withState(previousState).withPrimaryState(true));
            }
        }
    }

    public Integer getActiveProjectCount(ResourceParent resource) {
        if (resource.getResourceScope() == PROJECT) {
            throw new Error();
        }
        Long count = projectDAO.getActiveProjectCount(resource);
        return count == null ? null : count.intValue();
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return projectDAO.getLatestUpdatedTimestampSitemap(states);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return projectDAO.getSitemapEntries(activeProjectStates);
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer projectId) {
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        SearchEngineAdvertDTO searchEngineDTO = projectDAO.getSearchEngineAdvert(projectId, activeProjectStates);

        if (searchEngineDTO != null) {
            List<String> relatedUsers = Lists.newArrayList();
            List<User> projectAcademics = userService.getUsersForResourceAndRoles(getById(projectId), PROJECT_SUPERVISOR_GROUP.getRoles());
            for (User projectAcademic : projectAcademics) {
                relatedUsers.add(projectAcademic.getSearchEngineRepresentation());
            }

            searchEngineDTO.setRelatedUsers(relatedUsers);
        }

        return searchEngineDTO;
    }

    public List<ResourceSearchEngineDTO> getActiveProjectsByProgram(Integer programId) {
        List<PrismState> activeStates = stateService.getActiveProjectStates();
        return projectDAO.getActiveProjectsByProgram(programId, activeStates);
    }

    public List<ResourceSearchEngineDTO> getActiveProjectsByInstitution(Integer institutionId) {
        List<PrismState> activeStates = stateService.getActiveProjectStates();
        return projectDAO.getActiveProjectsByInstitution(institutionId, activeStates);
    }

    public void update(Integer projectId, OpportunityDTO projectDTO, Comment comment) throws Exception {
        resourceService.update(PROJECT, projectId, projectDTO, comment);
    }

}
