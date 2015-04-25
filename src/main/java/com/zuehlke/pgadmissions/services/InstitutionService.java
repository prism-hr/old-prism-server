package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;

import java.util.List;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.FileCategory;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.iso.jaxb.InstitutionDomiciles;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
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
    private DocumentService documentService;

    @Inject
    private EntityService entityService;

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
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private GeocodableLocationService geocodableLocationService;

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

    public Institution create(User user, InstitutionDTO institutionDTO) {
        InstitutionAddressDTO institutionAddressDTO = institutionDTO.getAddress();
        InstitutionDomicile institutionAddressCountry = entityService.getById(InstitutionDomicile.class, institutionAddressDTO.getDomicile());

        InstitutionAddress address = new InstitutionAddress().withAddressLine1(institutionAddressDTO.getAddressLine1())
                .withAddressLine2(institutionAddressDTO.getAddressLine2()).withAddressTown(institutionAddressDTO.getAddressTown())
                .withAddressRegion(institutionAddressDTO.getAddressDistrict()).withAddressCode(institutionAddressDTO.getAddressCode())
                .withDomicile(institutionAddressCountry);

        InstitutionDomicile institutionCountry = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());

        Institution institution = new Institution().withSystem(systemService.getSystem()).withDomicile(institutionCountry).withAddress(address)
                .withTitle(institutionDTO.getTitle()).withSummary(institutionDTO.getSummary()).withHomepage(institutionDTO.getHomepage())
                .withUclInstitution(false).withGoogleId(institutionDTO.getGoogleIdentifier()).withCurrency(institutionDTO.getCurrency()).withUser(user);

        address.setInstitution(institution);
        setLogoImage(institution, institutionDTO, PrismAction.SYSTEM_CREATE_INSTITUTION);
        return institution;
    }

    public void update(Integer institutionId, InstitutionDTO institutionDTO) {
        Institution institution = entityService.getById(Institution.class, institutionId);

        InstitutionAddress address = institution.getAddress();
        InstitutionAddressDTO addressDTO = institutionDTO.getAddress();
        InstitutionDomicile domicile = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());

        institution.setDomicile(domicile);
        institution.setTitle(institutionDTO.getTitle());
        institution.setSummary(institutionDTO.getSummary());
        institution.setDescription(institutionDTO.getDescription());

        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressRegion(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());

        geocodableLocationService.setLocation(address);

        institution.setCurrency(institutionDTO.getCurrency());
        institution.setHomepage(institutionDTO.getHomepage());

        setLogoImage(institution, institutionDTO, PrismAction.INSTITUTION_VIEW_EDIT);
    }

    public List<String> listAvailableCurrencies() {
        return institutionDAO.listAvailableCurrencies();
    }

    public void save(Institution institution) {
        InstitutionAddress institutionAddress = institution.getAddress();
        entityService.save(institution);
        entityService.save(institutionAddress);
        geocodableLocationService.setLocation(institutionAddress);
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

        InstitutionDTO institutionDTO = (InstitutionDTO) commentDTO.fetchResourceDTO();
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

    public SocialMetadataDTO getSocialMetadata(Institution institution) {
        return new SocialMetadataDTO().withAuthor(institution.getUser().getFullName()).withTitle(institution.getTitle())
                .withDescription(institution.getSummary()).withThumbnailUrl(resourceService.getSocialThumbnailUrl(institution))
                .withResourceUrl(resourceService.getSocialResourceUrl(institution));
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
        return month <= businessYearStartMonth ? (month - (businessYearStartMonth - 1)) : (month + (12 - (businessYearStartMonth - 1)));
    }

    public void changeInsitutionBusinessYear(Institution institution, Integer businessYearStartMonth) throws Exception {
        if (!businessYearStartMonth.equals(institution.getBusinessYearStartMonth())) {
            Integer businessYearEndMonth = businessYearStartMonth == 1 ? 12 : businessYearStartMonth - 1;
            institutionDAO.changeInstitutionBusinessYear(institution.getId(), businessYearEndMonth);
        }
    }

    private void setLogoImage(Institution institution, InstitutionDTO institutionDTO, PrismAction actionId) {
        FileDTO logoImageDTO = institutionDTO.getLogoImage();
        if (logoImageDTO != null) {
            Document image = documentService.getById(logoImageDTO.getId(), FileCategory.IMAGE);
            Preconditions.checkState(image.getContentType().equals("image/jpeg"), "Unexpected image type: " + image.getContentType());
            institution.setLogoImage(image);
        }
    }

}
