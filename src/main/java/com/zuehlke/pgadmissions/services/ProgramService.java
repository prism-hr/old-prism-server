package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ProgramType;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.AdvertStudyOption;
import com.zuehlke.pgadmissions.domain.program.AdvertStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class ProgramService {

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ApplicationContext applicationContext;

    public Program getById(Integer id) {
        return entityService.getById(Program.class, id);
    }

    public void save(Program program) {
        advertService.save(program.getAdvert());
        entityService.save(program);
    }

    public Program getProgramByImportedCode(String importedCode) {
        return getProgramByImportedCode(null, importedCode);
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        institution = institution == null ? institutionService.getUclInstitution() : institution;
        return programDAO.getProgramByImportedCode(institution, importedCode);
    }

    public List<Program> getPrograms() {
        return programDAO.getPrograms();
    }

    public Program create(User user, ProgramDTO programDTO) {
        Institution institution = entityService.getById(Institution.class, programDTO.getInstitutionId());
        Program program = new Program().withUser(user).withSystem(systemService.getSystem()).withInstitution(institution).withImported(false)
                .withRequireProjectDefinition(false);
        updateProgramDetails(program, programDTO);
        program.setEndDate(new LocalDate().plusMonths(3));
        copyStudyOptions(program, programDTO);
        return program;
    }

    public void postProcessProgram(Program program, Comment comment) {
        DateTime updatedTimestamp = program.getUpdatedTimestamp();
        program.setUpdatedTimestampSitemap(updatedTimestamp);
        program.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);

        if (comment.isProgramApproveOrDeactivateComment()) {
            projectService.synchronizeProjects(program);
            if (comment.isProgramRestoreComment()) {
                projectService.restoreProjects(program, comment.getCreatedTimestamp().toLocalDate());
            }
        }

        advertService.setSequenceIdentifier(program.getAdvert(), program.getSequenceIdentifier().substring(0, 13));
    }

    public List<ProgramRepresentation> getSimilarPrograms(Integer institutionId, String searchTerm) {
        return programDAO.getSimilarPrograms(institutionId, searchTerm);
    }

    public ActionOutcomeDTO executeAction(Integer programId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Program program = getById(programId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        boolean viewEditAction = actionId == PrismAction.PROGRAM_VIEW_EDIT;

        String commentContent = viewEditAction ? applicationContext.getBean(PropertyLoader.class).localize(program)
                .load(PrismDisplayPropertyDefinition.PROGRAM_COMMENT_UPDATED) : commentDTO.getContent();

        ProgramDTO programDTO = (ProgramDTO) commentDTO.fetchResourceDTO();
        LocalDate dueDate = programDTO.getEndDate();

        State transitionState = stateService.getById(commentDTO.getTransitionState());
        if (viewEditAction && !program.getImported() && transitionState == null && dueDate.isAfter(new LocalDate())) {
            transitionState = programDAO.getPreviousState(program);
        }

        Comment comment = new Comment().withContent(commentContent).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);
        commentService.appendCommentProperties(comment, commentDTO);

        update(programId, programDTO);

        return actionService.executeUserAction(program, action, comment);
    }

    public List<String> getPossibleLocations(Program program) {
        return programDAO.getPossibleLocations(program);
    }

    public List<String> getSuggestedDivisions(Integer programId, String location) {
        Program program = getById(programId);
        return programDAO.getSuggestedDivisions(program, location);
    }

    public List<String> getSuggestedStudyAreas(Integer programId, String location, String division) {
        Program program = getById(programId);
        return programDAO.getSuggestedStudyAreas(program, location, division);
    }

    public Integer getActiveProgramCount(Institution institution) {
        Long count = programDAO.getActiveProgramCount(institution);
        return count == null ? null : count.intValue();
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return programDAO.getLatestUpdatedTimestampSitemap(states);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        return programDAO.getSitemapEntries(activeProgramStates);
    }

    public SocialMetadataDTO getSocialMetadata(Program program) {
        return advertService.getSocialMetadata(program.getAdvert());
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer programId) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        SearchEngineAdvertDTO searchEngineDTO = programDAO.getSearchEngineAdvert(programId, activeProgramStates);

        if (searchEngineDTO != null) {
            searchEngineDTO.setRelatedProjects(projectService.getActiveProjectsByProgram(programId));

            List<String> relatedUsers = Lists.newArrayList();
            List<User> programAcademics = userService.getUsersForResourceAndRoles(getById(programId), PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR);
            for (User programAcademic : programAcademics) {
                relatedUsers.add(programAcademic.getSearchEngineRepresentation());
            }
            searchEngineDTO.setRelatedUsers(relatedUsers);
        }

        return searchEngineDTO;
    }

    public List<ResourceSearchEngineDTO> getActiveProgramsByInstitution(Integer institutionId) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        return programDAO.getActiveProgramsByInstitution(institutionId, activeProgramStates);
    }

    public List<Integer> getProjects(Integer program) {
        return programDAO.getProjects(program);
    }

    public List<Integer> getApplications(Integer program) {
        return programDAO.getApplications(program);
    }

    private void update(Integer programId, ProgramDTO programDTO) {
        Program program = entityService.getById(Program.class, programId);
        updateProgramDetails(program, programDTO);


    }

    private void updateProgramDetails(Program program, ProgramDTO programDTO) {
        Advert advert;
        if (program.getAdvert() == null) {
            advert = new Advert();
            advert.setAddress(advertService.createAddressCopy(program.getInstitution().getAdvert().getAddress()));
            program.setAdvert(advert);
        } else {
            advert = program.getAdvert();
        }

        if (!program.getImported()) {
            String title = programDTO.getTitle();
            program.setTitle(title);
            PrismProgramType prismProgramType = programDTO.getProgramType();
            program.setProgramType(prismProgramType);
            program.setLocale(programDTO.getLocale());
            advert.setTitle(title);
            program.setImportedProgramType((ProgramType) importedEntityService.getImportedEntityByCode(ProgramType.class, program.getInstitution(),
                    prismProgramType.name()));
            program.setEndDate(programDTO.getEndDate());
        }

        program.getLocations().clear();
        entityService.flush();

        for (String location : programDTO.getLocations()) {
            program.addLocation(location);
        }

        advert.setSummary(programDTO.getSummary());
        advert.setApplyHomepage(programDTO.getApplyHomepage());

        advert.setStudyDurationMinimum(programDTO.getStudyDurationMinimum());
        advert.setStudyDurationMaximum(programDTO.getStudyDurationMaximum());
    }

}
