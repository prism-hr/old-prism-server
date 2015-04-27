package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_CREATE_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.util.List;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.iso.jaxb.InstitutionDomiciles;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.InstitutionDomicileRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class InstitutionService {

    @Inject
    private InstitutionDAO institutionDAO;

    @Inject
    private AdvertService advertService;

    @Inject
    private EntityService entityService;

    @Inject
    DocumentService documentService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private ProgramService programService;

    @Inject
    private ProjectService projectService;

    @Inject
    private Mapper mapper;

    @Inject
    private ApplicationContext applicationContext;

    public Institution getById(Integer id) {
        return entityService.getById(Institution.class, id);
    }

    public List<InstitutionDomicileRepresentation> getInstitutionDomiciles() {
        List<InstitutionDomicileRepresentation> representations = Lists.newLinkedList();
        List<InstitutionDomicile> institutionDomiciles = institutionDAO.getInstitutionDomiciles();
        for (InstitutionDomicile institutionDomicile : institutionDomiciles) {
            representations.add(mapper.map(institutionDomicile, InstitutionDomicileRepresentation.class));
        }
        return representations;
    }

    public List<Institution> getApprovedInstitutionsByCountry(InstitutionDomicile domicile) {
        return institutionDAO.getApprovedInstitutionsByCountry(domicile);
    }

    public Institution getUclInstitution() {
        return institutionDAO.getUclInstitution();
    }

    public Institution createPartner(User user, InstitutionDTO institutionDTO) throws Exception {
        Institution institution = (Institution) applicationContext.getBean(INSTITUTION.getResourceCreator()).create(user, institutionDTO);

        Action action = actionService.getById(SYSTEM_CREATE_INSTITUTION);
        Role creatorRole = roleService.getById(INSTITUTION_ADMINISTRATOR);
        Comment comment = new Comment().withResource(institution).withUser(user).withAction(action).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime()).addAssignedUser(user, creatorRole, CREATE);
        actionService.executeUserAction(institution, action, comment);

        return institution;
    }

    public void update(Integer institutionId, InstitutionDTO institutionDTO) throws Exception {
        Institution institution = entityService.getById(Institution.class, institutionId);

        AdvertDTO advertDTO = institutionDTO.getAdvert();
        Advert advert = institution.getAdvert();
        advertService.updateAdvert(userService.getCurrentUser(), advertDTO, advert);

        institution.setTitle(advert.getTitle());
        institution.setDomicile(advert.getAddress().getDomicile());
        institution.setCurrency(institutionDTO.getCurrency());

        Integer oldBusinessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer newBusinessYearStartMonth = institutionDTO.getBusinessYearStartMonth();
        if (!oldBusinessYearStartMonth.equals(newBusinessYearStartMonth)) {
            changeInsitutionBusinessYear(institution, newBusinessYearStartMonth);
        }

        institution.setGoogleId(advert.getAddress().getLocation().getGoogleId());

        LocalDate endDate = institutionDTO.getEndDate();
        if (endDate != null) {
            institution.setEndDate(endDate);
        }

        setInstitutionImages(institutionDTO, institution);
        resourceService.setAttributes(institution, institutionDTO.getAttributes());
    }

    public List<String> listAvailableCurrencies() {
        return institutionDAO.listAvailableCurrencies();
    }

    public void save(Institution institution) {
        entityService.save(institution);
    }

    public void populateDefaultImportedEntityFeeds() throws DeduplicationException {
        for (Institution institution : institutionDAO.getInstitutionsWithoutImportedEntityFeeds()) {
            for (PrismImportedEntity prismImportedEntity : PrismImportedEntity.values()) {
                String defaultLocation = prismImportedEntity.getDefaultLocation();
                if (defaultLocation != null) {
                    importedEntityService.getOrCreateImportedEntityFeed(institution, prismImportedEntity, defaultLocation);
                }
            }
        }
    }

    public void initializeInstitution(Integer institutionId) throws Exception {
        Institution institution = getById(institutionId);
        User user = systemService.getSystem().getUser();
        Action action = actionService.getById(INSTITUTION_STARTUP);
        Comment comment = new Comment().withAction(action)
                .withContent(applicationContext.getBean(PropertyLoader.class).localize(institution).load(SYSTEM_COMMENT_INITIALIZED_INSTITUTION))
                .withDeclinedResponse(false).withUser(user).withCreatedTimestamp(new DateTime());
        actionService.executeAction(institution, action, comment);
    }

    public List<Integer> getInstitutionsToActivate() {
        return institutionDAO.getInstitutionsToActivate();
    }

    public List<Institution> list() {
        return institutionDAO.list();
    }

    public Institution getActivatedInstitutionByGoogleId(String googleId) {
        return institutionDAO.getActivatedInstitutionByGoogleId(googleId);
    }

    public ActionOutcomeDTO executeAction(Integer institutionId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Institution institution = getById(institutionId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        String commentContent = actionId == INSTITUTION_VIEW_EDIT ? applicationContext.getBean(PropertyLoader.class).localize(institution)
                .load(INSTITUTION_COMMENT_UPDATED) : commentDTO.getContent();

        State transitionState = stateService.getById(commentDTO.getTransitionState());
        Comment comment = new Comment().withContent(commentContent).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);
        commentService.appendCommentProperties(comment, commentDTO);

        InstitutionDTO institutionDTO = (InstitutionDTO) commentDTO.getResource();
        if (institutionDTO != null) {
            update(institutionId, institutionDTO);
        }

        return actionService.executeUserAction(institution, action, comment);
    }

    public boolean hasAuthenticatedFeeds(Institution institution) {
        return institutionDAO.getAuthenticatedFeedCount(institution) > 0;
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> programStates, List<PrismState> projectStates) {
        return institutionDAO.getLatestUpdatedTimestampSitemap(programStates, projectStates);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getSitemapEntries(activeProgramStates, activeProjectStates);
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer institutionId) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        SearchEngineAdvertDTO searchEngineDTO = institutionDAO.getSearchEngineAdvert(institutionId, activeProgramStates, activeProjectStates);

        if (searchEngineDTO != null) {
            searchEngineDTO.setRelatedPrograms(programService.getActiveProgramsByInstitution(institutionId));
            searchEngineDTO.setRelatedProjects(projectService.getActiveProjectsByInstitution(institutionId));

            List<String> relatedUsers = Lists.newArrayList();
            List<User> institutionAcademics = userService.getUsersForResourceAndRoles(getById(institutionId), PROJECT_PRIMARY_SUPERVISOR,
                    PROJECT_SECONDARY_SUPERVISOR);
            for (User institutionAcademic : institutionAcademics) {
                relatedUsers.add(institutionAcademic.getSearchEngineRepresentation());
            }
            searchEngineDTO.setRelatedUsers(relatedUsers);
        }

        return searchEngineDTO;
    }

    public List<ResourceSearchEngineDTO> getActiveInstitions() {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getRelatedInstitutions(activeProgramStates, activeProjectStates);
    }

    public void disableInstitutionDomiciles(List<String> updates) {
        institutionDAO.disableInstitutionDomiciles(updates);
    }

    public String mergeInstitutionDomicile(InstitutionDomiciles.InstitutionDomicile instituitionDomicileDefinition) throws DeduplicationException {
        InstitutionDomicile persistentInstitutionDomicile = entityService.getOrCreate(new InstitutionDomicile()
                .withId(instituitionDomicileDefinition.getIsoCode()).withName(instituitionDomicileDefinition.getName())
                .withCurrency(instituitionDomicileDefinition.getCurrency()).withEnabled(true));
        return persistentInstitutionDomicile.getId();
    }

    public String getBusinessYear(Institution institution, Integer year, Integer month) {
        Integer businessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer businessYear = month < businessYearStartMonth ? (year - 1) : year;
        return month == 1 ? businessYear.toString() : (year.toString() + "/" + new Integer(year + 1).toString());
    }

    public Integer getMonthOfBusinessYear(Institution institution, Integer month) {
        Integer businessYearStartMonth = institution.getBusinessYearStartMonth();
        return month >= businessYearStartMonth ? (month - (businessYearStartMonth - 1)) : (month + (12 - (businessYearStartMonth - 1)));
    }

    public void setInstitutionImages(InstitutionDTO institutionDTO, Institution institution) {
        FileDTO logo = institutionDTO.getLogoImage();
        if (logo != null) {
            Document logoDocument = documentService.getImageDocument(logo);
            institution.setLogoImage(logoDocument);
        }

        FileDTO background = institutionDTO.getBackgroundImage();
        if (background != null) {
            Document backgroundDocument = documentService.getImageDocument(background);
            institution.setBackgroundImage(backgroundDocument);
        }
    }

    private void changeInsitutionBusinessYear(Institution institution, Integer businessYearStartMonth) throws Exception {
        institution.setBusinessYearStartMonth(businessYearStartMonth);
        Integer businessYearEndMonth = businessYearStartMonth == 1 ? 12 : businessYearStartMonth - 1;
        institutionDAO.changeInstitutionBusinessYear(institution.getId(), businessYearEndMonth);
    }

}
