package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
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
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
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
    private ImportedEntityService importedEntityService;

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
    private ApplicationContext applicationContext;

    public Institution getById(Integer id) {
        return entityService.getById(Institution.class, id);
    }

    public List<InstitutionDomicile> getDomiciles() {
        return institutionDAO.getDomciles();
    }

    public List<Institution> listApprovedInstitutionsByCountry(InstitutionDomicile domicile) {
        return institutionDAO.listApprovedInstitutionsByCountry(domicile);
    }

    public Institution getUclInstitution() {
        return institutionDAO.getUclInstitution();
    }

    public Institution create(User user, InstitutionDTO institutionDTO) {
        InstitutionAddressDTO addressDTO = institutionDTO.getAddress();
        InstitutionDomicile addressDomicile = entityService.getById(InstitutionDomicile.class, addressDTO.getDomicile());

        InstitutionAddress address = new InstitutionAddress().withAddressLine1(addressDTO.getAddressLine1())
                .withAddressLine2(addressDTO.getAddressLine2()).withAddressTown(addressDTO.getAddressTown())
                .withAddressRegion(addressDTO.getAddressDistrict()).withAddressCode(addressDTO.getAddressCode())
                .withDomicile(addressDomicile);

        String title = institutionDTO.getTitle();

        Advert advert = new Advert().withTitle(title).withLocale(institutionDTO.getLocale()).withCurrency(institutionDTO.getCurrency())
                .withSummary(institutionDTO.getSummary()).withHomepage(institutionDTO.getHomepage()).withAddress(address);
        advertService.setImages(advert, institutionDTO.getImages());

        InstitutionDomicile institutionDomicile = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());
        Institution institution = new Institution().withUser(user).withSystem(systemService.getSystem()).withDomicile(institutionDomicile)
                .withTitle(title).withUclInstitution(false).withGoogleId(institutionDTO.getGoogleIdentifier());

        if (BooleanUtils.isTrue(institutionDTO.getDefaultLocale())) {
            institution.setAdvert(advert);
        }

        advert.setInstitution(institution);
        address.setInstitution(institution);
        return institution;
    }

    public void update(Integer institutionId, InstitutionDTO institutionDTO) {
        Institution institution = entityService.getById(Institution.class, institutionId);

        InstitutionAddressDTO addressDTO = institutionDTO.getAddress();
        InstitutionDomicile domicile = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());

        institution.setDomicile(domicile);

        String title = institutionDTO.getTitle();
        institution.setTitle(title);

        Advert advert = advertService.getAdvertByLocale(institution, institutionDTO.getLocale());

        advert.setTitle(title);
        advert.setLocale(institutionDTO.getLocale());
        advert.setCurrency(institutionDTO.getCurrency());
        advert.setSummary(institutionDTO.getSummary());
        advert.setDescription(institutionDTO.getDescription());
        advert.setHomepage(institutionDTO.getHomepage());
        advertService.setImages(advert, institutionDTO.getImages());
        
        if (BooleanUtils.isTrue(institutionDTO.getDefaultLocale())) {
            institution.setAdvert(advert);
        }

        InstitutionAddress address = advert.getInstitutionAddress();
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressRegion(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());
    }
    

    public List<String> listAvailableCurrencies() {
        return institutionDAO.listAvailableCurrencies();
    }

    public void save(Institution institution) {
        Advert advert = institution.getAdvert();
        InstitutionAddress address = advert.getAddress();
        geocodableLocationService.setLocation(address);

        entityService.save(address);
        entityService.save(advert);
        entityService.save(institution);

        address.setInstitution(institution);
        advert.setInstitution(institution);
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

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> programStates, List<PrismState> projectStates) {
        return institutionDAO.getLatestUpdatedTimestampSitemap(programStates, projectStates);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getSitemapEntries(activeProgramStates, activeProjectStates);
    }

    public SocialMetadataDTO getSocialMetadata(Institution institution) {
        return advertService.getSocialMetadata(institution.getAdvert());
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

}
