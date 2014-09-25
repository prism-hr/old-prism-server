package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
import com.zuehlke.pgadmissions.dto.json.InstitutionLookupResponseDTO;
import com.zuehlke.pgadmissions.dto.json.InstitutionSearchResponseDTO;
import com.zuehlke.pgadmissions.dto.json.InstitutionSearchResponseDTO.Item;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;

@Service
@Transactional
public class InstitutionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${integration.google.search.api.uri}")
    private String googleSearchApiUri;

    @Value("${integration.google.search.api.cse.key}")
    private String googleCustomSearchEngineKey;

    @Value("${linkedin.company.uri.prefix}")
    private String linkedinCompanyUriPrefix;

    @Value("${integration.linkedin.api.token}")
    private String linkedinApiToken;

    @Value("${integration.linkedin.companies.api.uri}")
    private String linkedinCompaniesApiUri;

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
                .withTitle(institutionDTO.getTitle()).withHomepage(institutionDTO.getHomepage()).withDefaultProgramType(institutionDTO.getDefaultProgramType())
                .withDefaultStudyOption(institutionDTO.getDefaultStudyOption()).withGoogleIdentifier(institutionDTO.getGoogleIdentifier())
                .withLinkedinIdentifier(institutionDTO.getLinkedinIdentifier()).withCurrency(institutionDTO.getCurrency()).withUser(user);

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

        address.setRegion(region);
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressDistrict(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());

        geocodableLocationService.setLocation(address);

        institution.setCurrency(institutionDTO.getCurrency());
        institution.setHomepage(institutionDTO.getHomepage());
        institution.setGoogleIdentifier(institutionDTO.getGoogleIdentifier());
        institution.setLinkedinIndentifier(institutionDTO.getLinkedinIdentifier());

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

    public InstitutionLookupResponseDTO getLinkedinInstitution(String institutionTitle) throws IOException {
        String institutionTitleEncoded = URLEncoder.encode(institutionTitle, "UTF-8");
        URI searchRequest = new DefaultResourceLoader().getResource(googleSearchApiUri + "?query=" + institutionTitleEncoded + //
                "&key=" + googleApiKey + "&cx=" + googleCustomSearchEngineKey + "&format=json").getURI();
        InstitutionSearchResponseDTO searchResponse = restTemplate.getForObject(searchRequest, InstitutionSearchResponseDTO.class);

        List<Item> searchResults = searchResponse.getItems();
        if (!searchResults.isEmpty()) {
            String linkedinIdentifier = searchResults.get(0).getMetaData().getUri().replace(linkedinCompanyUriPrefix, "");

            String searchTermEncoded = URLEncoder.encode(linkedinIdentifier, "UTF-8");
            URI request = new DefaultResourceLoader().getResource(linkedinCompaniesApiUri + "?universal-name=" + searchTermEncoded + //
                    ":(universal-name,description,website-url,square-logo-url)").getURI();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-li-format", "json");
            headers.set("Authorization", "Bearer " + linkedinApiToken);
            HttpEntity<String> requestEntity = new HttpEntity<String>("parameters", headers);
            
            ResponseEntity<InstitutionLookupResponseDTO> responseEntity = restTemplate.exchange(request, HttpMethod.GET, requestEntity,
                    InstitutionLookupResponseDTO.class);
            return responseEntity.getBody();
        }

        return null;
    }

    private void setLogoDocument(Institution institution, InstitutionDTO institutionDTO, PrismAction actionId) {
        Integer logoDocumentId = institutionDTO.getLogoDocumentId();
        String logoDocumentUriString = institutionDTO.getLogoUri();

        if (logoDocumentId == null && logoDocumentUriString == null) {
            return;
        } else if (logoDocumentId == null) {
            try {
                URL logoDocumentUri = new DefaultResourceLoader().getResource(logoDocumentUriString).getURL();
                URLConnection connection = logoDocumentUri.openConnection();
                InputStream stream = connection.getInputStream();
                byte[] content = IOUtils.toByteArray(stream);
                String contentType = connection.getContentType();
                String fileType = FilenameUtils.getExtension(logoDocumentUriString);
                String fileName = institution.getLinkedinIndentifier().replace("-", "") + "." + fileType;
                Document logoDocument = documentService.create(fileName, content, contentType);
                institution.setLogoDocument(logoDocument);
            } catch (IOException e) {
                logger.error("Unable to download logo document: " + logoDocumentUriString, e);
                Action action = actionService.getById(actionId);
                actionService.throwWorkflowPermissionException(institution, action);
            }
        } else {
            institution.setLogoDocument(documentService.getById(logoDocumentId));
        }
    }

}
