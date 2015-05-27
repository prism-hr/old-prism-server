package com.zuehlke.pgadmissions.workflow.resource.seo.search;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.ProjectService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class InstitutionSearchRepresentationBuilder implements SearchRepresentationBuilder {

    @Inject
    private InstitutionService institutionService;

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
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();

        SearchEngineAdvertDTO searchEngineDTO = institutionService.getSearchEngineAdvert(resourceId, activeInstitutionStates, activeProgramStates,
                activeProjectStates);

        if (searchEngineDTO != null) {
            searchEngineDTO.setRelatedPrograms(programService.getActiveProgramsByInstitution(resourceId));
            searchEngineDTO.setRelatedProjects(projectService.getActiveProjectsByInstitution(resourceId));

            List<String> relatedUsers = Lists.newArrayList();
            List<User> institutionAcademics = userService.getUsersForResourceAndRoles(institutionService.getById(resourceId),
                    PROJECT_SUPERVISOR_GROUP.getRoles());
            for (User institutionAcademic : institutionAcademics) {
                relatedUsers.add(institutionAcademic.getSearchEngineRepresentation());
            }
            searchEngineDTO.setRelatedUsers(relatedUsers);
        }

        return searchEngineDTO;
    }

}
