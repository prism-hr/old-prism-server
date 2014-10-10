package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation;

@Service
@Transactional
public class InstitutionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    private UserService userService;

    @Autowired
    private GeocodableLocationService geocodableLocationService;

    @Autowired
    private SocialPresenceService socialPresenceService;

    @Autowired
    private RestTemplate restTemplate;

    public Institution getById(Integer id) {
        return entityService.getById(Institution.class, id);
    }

    public List<InstitutionDomicile> getDomiciles() {
        return institutionDAO.getDomciles();
    }

    public List<InstitutionDomicileRegion> getRegionsByDomicile(InstitutionDomicile domicile) {
        return institutionDAO.getRegionsByDomicile(domicile);
    }

    public List<Institution> listApprovedInstitutionsByCountry(InstitutionDomicile domicile) {
        return institutionDAO.listApprovedInstitutionsByCountry(domicile);
    }

    public Institution getUclInstitution() {
        return institutionDAO.getUclInstitution();
    }

    public Institution create(User user, InstitutionDTO institutionDTO) throws InterruptedException, IOException, JAXBException {
        InstitutionAddressDTO institutionAddressDTO = institutionDTO.getAddress();
        InstitutionDomicile institutionAddressCountry = entityService.getById(InstitutionDomicile.class, institutionAddressDTO.getDomicile());
        InstitutionDomicileRegion institutionAddressRegion = entityService.getById(InstitutionDomicileRegion.class, institutionAddressDTO.getRegion());

        InstitutionAddress address = new InstitutionAddress().withAddressLine1(institutionAddressDTO.getAddressLine1())
                .withAddressLine2(institutionAddressDTO.getAddressLine2()).withAddressTown(institutionAddressDTO.getAddressTown())
                .withAddressDistrict(institutionAddressDTO.getAddressDistrict()).withAddressCode(institutionAddressDTO.getAddressCode())
                .withRegion(institutionAddressRegion).withDomicile(institutionAddressCountry);

        InstitutionDomicile institutionCountry = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());

        Institution institution = new Institution().withSystem(systemService.getSystem()).withDomicile(institutionCountry).withAddress(address)
                .withTitle(institutionDTO.getTitle()).withLocale(institutionDTO.getLocale()).withSummary(institutionDTO.getSummary())
                .withHomepage(institutionDTO.getHomepage()).withUclInstitution(false).withDefaultProgramType(institutionDTO.getDefaultProgramType())
                .withDefaultStudyOption(institutionDTO.getDefaultStudyOption()).withGoogleId(institutionDTO.getGoogleIdentifier())
                .withLinkedinUri(institutionDTO.getLinkedinIdentifier()).withCurrency(institutionDTO.getCurrency()).withUser(user);

        setLogoDocument(institution, institutionDTO, PrismAction.SYSTEM_CREATE_INSTITUTION);
        return institution;
    }

    public void update(Integer institutionId, InstitutionDTO institutionDTO) {
        Institution institution = entityService.getById(Institution.class, institutionId);

        InstitutionAddress address = institution.getAddress();
        InstitutionAddressDTO addressDTO = institutionDTO.getAddress();
        InstitutionDomicile domicile = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());
        InstitutionDomicileRegion region = entityService.getById(InstitutionDomicileRegion.class, addressDTO.getRegion());

        institution.setDomicile(domicile);
        institution.setTitle(institutionDTO.getTitle());
        institution.setLocale(institutionDTO.getLocale());
        institution.setSummary(institutionDTO.getSummary());
        institution.setDescription(institutionDTO.getDescription());

        address.setRegion(region);
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressDistrict(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());

        geocodableLocationService.setLocation(address);

        institution.setCurrency(institutionDTO.getCurrency());
        institution.setHomepage(institutionDTO.getHomepage());
        institution.setGoogleId(institutionDTO.getGoogleIdentifier());
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
        entityService.save(institutionAddress);
        entityService.save(institution);
        geocodableLocationService.setLocation(institutionAddress);
    }

    public void populateDefaultImportedEntityFeeds() throws DeduplicationException {
        for (Institution institution : institutionDAO.getInstitutionsWithoutImportedEntityFeeds()) {
            for (PrismImportedEntity importedEntityType : PrismImportedEntity.values()) {
                if (importedEntityType.getDefaultLocation() != null) {
                    importedEntityService.getOrCreateImportedEntityFeed(institution, importedEntityType, importedEntityType.getDefaultLocation());
                }
            }
        }
    }

    public ActionOutcomeDTO performAction(Integer institutionId, CommentDTO commentDTO) throws DeduplicationException {
        Institution institution = entityService.getById(Institution.class, institutionId);
        PrismAction actionId = commentDTO.getAction();

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(user).withAction(action).withTransitionState(transitionState)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);

        InstitutionDTO institutionDTO = commentDTO.getInstitution();
        if (institutionDTO != null) {
            update(institutionId, institutionDTO);
        }

        return actionService.executeUserAction(institution, action, comment);
    }

    public SocialPresenceRepresentation getSocialProfiles(String institutionTitle) throws IOException {
        return socialPresenceService.getPotentialInstitutionProfiles(institutionTitle);
    }

    private void setLogoDocument(Institution institution, InstitutionDTO institutionDTO, PrismAction actionId) {
        Integer logoDocumentId = institutionDTO.getLogoDocumentId();
        String logoDocumentLink = institutionDTO.getLogoUri();

        if (logoDocumentId == null && logoDocumentLink == null) {
            return;
        } else if (logoDocumentId == null) {
            try {
                institution.setLogoDocument(documentService.getExternalDocument(logoDocumentLink));
            } catch (IOException e) {
                logger.error("Unable to download logo document for: " + institution.getTitle() + " (" + logoDocumentLink + ")", e);
                Action action = actionService.getById(actionId);
                actionService.throwWorkflowPermissionException(institution, action);
            }
        } else {
            institution.setLogoDocument(documentService.getById(logoDocumentId));
        }
    }

}
