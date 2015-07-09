package com.zuehlke.pgadmissions.workflow.resource.seo.search;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.ProjectService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class ProgramSearchRepresentationBuilder implements SearchRepresentationBuilder {

    @Inject
    private ProjectService projectService;

    @Inject
    private ProgramService programService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Override
    public SearchEngineAdvertDTO build(Integer resourceId) throws Exception {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        SearchEngineAdvertDTO searchEngineDTO = programService.getSearchEngineAdvert(resourceId, activeProgramStates);

        if (searchEngineDTO != null) {
            searchEngineDTO.setRelatedProjects(projectService.getActiveProjectsByProgram(resourceId));

            List<String> relatedUsers = Lists.newArrayList();
            List<User> programAcademics = userService.getUsersForResourceAndRoles(programService.getById(resourceId), PROJECT_SUPERVISOR_GROUP.getRoles());
            for (User programAcademic : programAcademics) {
                relatedUsers.add(programAcademic.getSearchEngineRepresentation());
            }
            searchEngineDTO.setRelatedUsers(relatedUsers);
        }

        return searchEngineDTO;
    }

}
