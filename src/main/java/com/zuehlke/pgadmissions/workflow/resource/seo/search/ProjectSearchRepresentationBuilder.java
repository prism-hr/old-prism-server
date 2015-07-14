package com.zuehlke.pgadmissions.workflow.resource.seo.search;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.services.ProjectService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class ProjectSearchRepresentationBuilder implements SearchRepresentationBuilder {

    @Inject
    private ProjectService projectService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Override
    public SearchEngineAdvertDTO build(Integer resourceId) throws Exception {
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        SearchEngineAdvertDTO searchEngineDTO = projectService.getSearchEngineAdvert(resourceId, activeProjectStates);

        if (searchEngineDTO != null) {
            List<String> relatedUsers = Lists.newArrayList();
            List<User> projectAcademics = userService.getUsersForResourceAndRoles(projectService.getById(resourceId), PROJECT_SUPERVISOR_GROUP.getRoles());
            for (User projectAcademic : projectAcademics) {
                relatedUsers.add(projectAcademic.getSearchEngineRepresentation());
            }

            searchEngineDTO.setRelatedUsers(relatedUsers);
        }

        return searchEngineDTO;
    }

}
