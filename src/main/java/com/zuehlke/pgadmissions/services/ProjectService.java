package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobot;

@Service
@Transactional
public class ProjectService {

    @Inject
    private ProjectDAO projectDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private StateService stateService;

    public Project getById(Integer id) {
        return entityService.getById(Project.class, id);
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return projectDAO.getLatestUpdatedTimestampSitemap(states);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return projectDAO.getSitemapEntries(activeProjectStates);
    }

    public ResourceRepresentationRobot getSearchEngineAdvert(Integer projectId, List<PrismState> activeProjectStates) {
        return projectDAO.getSearchEngineAdvert(projectId, activeProjectStates);
    }

}
