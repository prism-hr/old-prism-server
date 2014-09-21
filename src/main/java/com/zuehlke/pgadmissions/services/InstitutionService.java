package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;

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
    private UserService userService;

    @Autowired
    private GeocodableLocationService geocodableLocationService;

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
        InstitutionDomicile institutionAddressCountry = entityService.getById(InstitutionDomicile.class, institutionAddressDTO.getCountry());
        InstitutionDomicileRegion institutionAddressRegion = entityService.getById(InstitutionDomicileRegion.class, institutionAddressDTO.getRegion());

        InstitutionAddress institutionAddress = new InstitutionAddress().withAddressLine1(institutionAddressDTO.getAddressLine1())
                .withAddressLine2(institutionAddressDTO.getAddressLine2()).withAddressTown(institutionAddressDTO.getAddressTown())
                .withAddressDistrict(institutionAddressDTO.getAddressDistrict()).withAddressCode(institutionAddressDTO.getAddressCode())
                .withRegion(institutionAddressRegion).withDomicile(institutionAddressCountry);

        geocodableLocationService.setLocation(institutionAddress);

        InstitutionDomicile institutionCountry = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());

        Document logoDocument = documentService.getByid(institutionDTO.getLogoDocumentId());

        return new Institution().withSystem(systemService.getSystem()).withDomicile(institutionCountry).withAddress(institutionAddress)
                .withTitle(institutionDTO.getTitle()).withHomepage(institutionDTO.getHomepage()).withDefaultProgramType(institutionDTO.getDefaultProgramType())
                .withDefaultStudyOption(institutionDTO.getDefaultStudyOption()).withLogoDocument(logoDocument).withCurrency(institutionDTO.getCurrency())
                .withUser(user);
    }

    public void update(Integer institutionId, InstitutionDTO institutionDTO) throws InterruptedException, IOException, JAXBException {
        Institution institution = entityService.getById(Institution.class, institutionId);

        InstitutionAddress address = institution.getAddress();
        InstitutionAddressDTO addressDTO = institutionDTO.getAddress();
        InstitutionDomicile domicile = entityService.getById(InstitutionDomicile.class, institutionDTO.getDomicile());
        InstitutionDomicileRegion region = entityService.getById(InstitutionDomicileRegion.class, addressDTO.getRegion());

        institution.setDomicile(domicile);
        institution.setTitle(institutionDTO.getTitle());

        address.setRegion(region);
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressDistrict(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());

        geocodableLocationService.setLocation(address);

        institution.setCurrency(institutionDTO.getCurrency());
        institution.setHomepage(institutionDTO.getHomepage());

        institution.setDefaultProgramType(institutionDTO.getDefaultProgramType());
        institution.setDefaultStudyOption(institutionDTO.getDefaultStudyOption());
    }

    public List<String> listAvailableCurrencies() {
        return institutionDAO.listAvailableCurrencies();
    }

    public void save(Institution institution) {
        InstitutionAddress institutionAddress = institution.getAddress();
        entityService.save(institutionAddress);
        entityService.save(institution);
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

    public ActionOutcomeDTO performAction(Integer institutionId, CommentDTO commentDTO) throws DeduplicationException, InterruptedException, IOException,
            JAXBException {
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

}
