package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_CREATE_INSTITUTION;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Location;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.ByteStreams;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.document.PrismFileCategory;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowDuplicateResourceException;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedAdvertDomicileDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.scraping.InstitutionUcasScraper;

@Component
public class InstitutionServiceHelper implements PrismServiceHelper {

    private static Logger logger = LoggerFactory.getLogger(InstitutionServiceHelper.class);

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ActionService actionService;

    @Inject
    private SystemService systemService;

    @Inject
    private DocumentService documentService;

    @Inject
    private InstitutionUcasScraper institutionUcasScraper;

    @Value("${auth.facebook.appSecret}")
    private String facebookAppSecret;

    @Value("${auth.facebook.clientId}")
    private String facebookClientId;

    @Inject
    private ApplicationContext applicationContext;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        com.zuehlke.pgadmissions.domain.resource.System system = systemService.getSystem();
        User user = system.getUser();

        FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider(facebookClientId, facebookAppSecret, null);
        AccessGrant accessGrant = facebookServiceProvider.getOAuthOperations().authenticateClient();
        Facebook facebookApi = facebookServiceProvider.getApi(accessGrant.getAccessToken());

        List<ImportedInstitution> unimportedUcasInstitutions = importedEntityService.getUnimportedUcasInstitutions();
        for (ImportedInstitution importedInstitution : unimportedUcasInstitutions) {
            if (shuttingDown.get()) {
                return;
            }
            logger.info("Importing institution: " + importedInstitution.getName());
            String facebookId = importedInstitution.getFacebookId();
            Page facebookPage = null;
            if (facebookId != null) {
                try {
                    facebookPage = facebookApi.pageOperations().getPage(facebookId);
                } catch (UncategorizedApiException e) {
                    logger.error("Could not read Facebook data for imported institution ID: " + importedInstitution.getId());
                }
            }
            facebookPage = ObjectUtils.firstNonNull(facebookPage, new Page());

            InstitutionUcasScraper.UcasInstitutionData ucasInstitutionData = institutionUcasScraper.getInstitutionData(importedInstitution.getUcasId());
            if (ucasInstitutionData == null) {
                logger.error("Could not read enough information for imported institution with ID: " + importedInstitution.getId());
                continue;
            }

            AdvertDTO advertDTO = new AdvertDTO();
            InstitutionDTO institutionDTO = new InstitutionDTO();
            institutionDTO.setParentResource(new ResourceDTO().withId(system.getId()).withScope(PrismScope.SYSTEM));
            institutionDTO.setCurrency("GBP");
            institutionDTO.setBusinessYearStartMonth(10);
            institutionDTO.setImportedInstitutionId(importedInstitution.getId());
            institutionDTO.setName(importedInstitution.getName());
            institutionDTO.setAdvert(advertDTO);
            AddressAdvertDTO address = new AddressAdvertDTO();
            advertDTO.setAddress(address);

            String summary = Stream.of(facebookPage.getAbout(), facebookPage.getDescription())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n\n"));
            advertDTO.setSummary(summary);
            advertDTO.setTelephone(ObjectUtils.firstNonNull(ucasInstitutionData.getTelephone(), facebookPage.getPhone()));
            advertDTO.setHomepage(ObjectUtils.firstNonNull(ucasInstitutionData.getHomepage(), facebookPage.getWebsite()));

            if (facebookPage.getLocation() != null) {
                Location location = facebookPage.getLocation();
                address.setDomicile(location.getCountry() != null ? new ImportedAdvertDomicileDTO().withName(location.getCountry()) : null);
                address.setAddressLine1(location.getStreet());
                address.setAddressCode(location.getZip());
                address.setAddressTown(location.getCity());
            }

            // if there is missing address data try to use UCAS values
            AddressAdvertDTO ucasAddress = ucasInstitutionData.getAddress();
            if (ucasAddress != null) {
                address.setAddressLine1(ObjectUtils.firstNonNull(address.getAddressLine1(), ucasAddress.getAddressLine1()));
                address.setAddressCode(ObjectUtils.firstNonNull(address.getAddressCode(), ucasAddress.getAddressCode()));
                address.setAddressTown(ObjectUtils.firstNonNull(address.getAddressCode(), ucasAddress.getAddressTown()));
                if (address.getDomicile() == null) {
                    address.setDomicile(ucasAddress.getDomicile());
                }
            }

            // set domicile ID based on name
            if (address.getDomicile().getId() == null) {
                List<ImportedAdvertDomicile> domiciles = importedEntityService.searchByName(ImportedAdvertDomicile.class, address.getDomicile().getName());
                if (domiciles.isEmpty()) {
                    logger.error("Expected exactly one imported advert domicile for given search term: " + address.getDomicile().getName());
                    continue;
                }
                address.getDomicile().setId(domiciles.get(0).getId());
            }

            InstitutionServiceHelper thisBean = applicationContext.getBean(InstitutionServiceHelper.class);
            try {
                thisBean.createInstitution(user, facebookId, facebookPage, institutionDTO);
            } catch (WorkflowDuplicateResourceException e) {
                logger.error("Could not import institution: " + importedInstitution.getName(), e);
            }
        }

    }

    @Transactional
    protected Institution createInstitution(User user, String facebookId, Page facebookPage, InstitutionDTO institutionDTO) {
        ActionOutcomeDTO outcome = resourceService.createResource(user, actionService.getById(SYSTEM_CREATE_INSTITUTION), institutionDTO);
        Institution institution = (Institution) outcome.getResource();
        Integer institutionId = institution.getId();
        if (facebookId != null) {
            try {
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpEntity logoEntity = httpclient.execute(new HttpGet("http://graph.facebook.com/" + facebookId + "/picture?type=large")).getEntity();
                byte[] logoImageContent = ByteStreams.toByteArray(logoEntity.getContent());
                documentService.createImage("" + institutionId + "_logo", logoImageContent, logoEntity.getContentType().getValue(), institutionId, PrismFileCategory.PrismImageCategory.INSTITUTION_LOGO);

                if (facebookPage.getCover() != null) {
                    HttpEntity backgroundEntity = httpclient.execute(new HttpGet(facebookPage.getCover().getSource())).getEntity();
                    byte[] backgroundImageContent = ByteStreams.toByteArray(backgroundEntity.getContent());
                    documentService.createImage("" + institutionId + "_background", backgroundImageContent, backgroundEntity.getContentType().getValue(), institutionId, PrismFileCategory.PrismImageCategory.INSTITUTION_BACKGROUND);
                }
            } catch (IOException e) {
                logger.error("Could not load image for institution ID: " + institutionId, e);
            }
        }
        return institution;
    }


    @Override
    public void shutdown() {
        shuttingDown.set(true);
    }

}
