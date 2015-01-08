package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.INSTITUTION_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Preconditions;
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
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class InstitutionService {

    @Autowired
    private InstitutionDAO institutionDAO;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserService userService;

    @Autowired
    private GeocodableLocationService geocodableLocationService;

    @Autowired
    private SocialPresenceService socialPresenceService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
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
        InstitutionAddressDTO institutionAddressDTO = institutionDTO.getAddress();
        InstitutionDomicile institutionAddressCountry = entityService.getById(InstitutionDomicile.class, institutionAddressDTO.getDomicile());

        InstitutionAddress address = new InstitutionAddress().withAddressLine1(institutionAddressDTO.getAddressLine1())
                .withAddressLine2(institutionAddressDTO.getAddressLine2()).withAddressTown(institutionAddressDTO.getAddressTown())
                .withAddressDistrict(institutionAddressDTO.getAddressDistrict()).withAddressCode(institutionAddressDTO.getAddressCode())
                .withDomicile(institutionAddressCountry);

        InstitutionDomicile institutionCountry = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());

        Institution institution = new Institution().withSystem(systemService.getSystem()).withDomicile(institutionCountry).withAddress(address)
                .withTitle(institutionDTO.getTitle()).withLocale(institutionDTO.getLocale()).withSummary(institutionDTO.getSummary())
                .withHomepage(institutionDTO.getHomepage()).withUclInstitution(false).withDefaultProgramType(institutionDTO.getDefaultProgramType())
                .withDefaultStudyOption(institutionDTO.getDefaultStudyOption()).withGoogleId(institutionDTO.getGoogleIdentifier())
                .withLinkedinUri(institutionDTO.getLinkedinIdentifier()).withCurrency(institutionDTO.getCurrency()).withUser(user);

        address.setInstitution(institution);
        setLogoDocument(institution, institutionDTO, PrismAction.SYSTEM_CREATE_INSTITUTION);
        return institution;
    }

    public void update(Integer institutionId, InstitutionDTO institutionDTO) {
        Institution institution = entityService.getById(Institution.class, institutionId);

        InstitutionAddress address = institution.getAddress();
        InstitutionAddressDTO addressDTO = institutionDTO.getAddress();
        InstitutionDomicile domicile = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());

        institution.setDomicile(domicile);
        institution.setTitle(institutionDTO.getTitle());
        institution.setLocale(institutionDTO.getLocale());
        institution.setSummary(institutionDTO.getSummary());
        institution.setDescription(institutionDTO.getDescription());

        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressDistrict(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());

        geocodableLocationService.setLocation(address);

        institution.setCurrency(institutionDTO.getCurrency());
        institution.setHomepage(institutionDTO.getHomepage());
        institution.setLinkedinUri(institutionDTO.getLinkedinIdentifier());

        institution.setDefaultProgramType(institutionDTO.getDefaultProgramType());
        institution.setDefaultStudyOption(institutionDTO.getDefaultStudyOption());

        setLogoDocument(institution, institutionDTO, PrismAction.INSTITUTION_VIEW_EDIT);
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
            for (PrismImportedEntity importedEntityType : PrismImportedEntity.getDefaultLocations()) {
                importedEntityService.getOrCreateImportedEntityFeed(institution, importedEntityType, importedEntityType.getDefaultLocation());
            }
        }
    }

    public SocialPresenceRepresentation getSocialProfiles(String institutionTitle) throws IOException {
        return socialPresenceService.getPotentialInstitutionProfiles(institutionTitle);
    }

    public void initializeInstitution(Integer institutionId) throws DeduplicationException, InstantiationException, IllegalAccessException {
        Institution institution = getById(institutionId);
        User user = systemService.getSystem().getUser();
        Action action = actionService.getById(INSTITUTION_STARTUP);
        Comment comment = new Comment().withAction(action)
                .withContent(applicationContext.getBean(PropertyLoader.class).localize(institution, user).load(SYSTEM_COMMENT_INITIALIZED_INSTITUTION))
                .withDeclinedResponse(false).withUser(user).withCreatedTimestamp(new DateTime());
        actionService.executeAction(institution, action, comment);
    }

    public List<Integer> getInstitutionsToActivate() {
        return institutionDAO.getInstitutionsToActivate();
    }

    private void setLogoDocument(Institution institution, InstitutionDTO institutionDTO, PrismAction actionId) {
        FileDTO logoDocumentDTO = institutionDTO.getLogoDocument();
        String logoDocumentLink = institutionDTO.getLogoUri();

        if (logoDocumentDTO == null && logoDocumentLink == null) {
            return;
        } else if (logoDocumentDTO == null) {
            try {
                institution.setLogoDocument(documentService.getExternalFile(FileCategory.IMAGE, logoDocumentLink));
            } catch (IOException e) {
                Action action = actionService.getById(actionId);
                actionService.throwWorkflowPermissionException(institution, action);
            }
        } else {
            Document image = documentService.getById(logoDocumentDTO.getId(), FileCategory.IMAGE);
            Preconditions.checkState(image.getContentType().equals("image/jpeg"), "Unexpected image type: " + image.getContentType());
            institution.setLogoDocument(image);
        }
    }

    public List<Institution> list() {
        return institutionDAO.list();
    }

    public Institution getActivatedInstitutionByGoogleId(String googleId) {
        return institutionDAO.getActivatedInstitutionByGoogleId(googleId);
    }

    public ActionOutcomeDTO executeAction(Integer institutionId, CommentDTO commentDTO) throws DeduplicationException, InstantiationException,
            IllegalAccessException {
        User user = userService.getById(commentDTO.getUser());
        Institution institution = getById(institutionId);

        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);

        String commentContent = actionId == INSTITUTION_VIEW_EDIT ? applicationContext.getBean(PropertyLoader.class).localize(institution, user)
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

}
