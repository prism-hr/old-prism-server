package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Location;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.exceptions.WorkflowDuplicateResourceException;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedAdvertDomicileDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.scraping.InstitutionUcasScraper;

@Component
public class InstitutionServiceHelper extends PrismServiceHelperAbstract {

    private static Logger logger = LoggerFactory.getLogger(InstitutionServiceHelper.class);

    @Value("${auth.facebook.appSecret}")
    private String facebookAppSecret;

    @Value("${auth.facebook.clientId}")
    private String facebookClientId;
    
    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private InstitutionUcasScraper institutionUcasScraper;
    
    @Inject
    private SystemService systemService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        System system = systemService.getSystem();

        FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider(facebookClientId, facebookAppSecret, null);
        AccessGrant accessGrant = facebookServiceProvider.getOAuthOperations().authenticateClient();
        Facebook facebookApi = facebookServiceProvider.getApi(accessGrant.getAccessToken());

        List<ImportedInstitution> unimportedUcasInstitutions = importedEntityService.getUnimportedUcasInstitutions();
        for (ImportedInstitution importedInstitution : unimportedUcasInstitutions) {
            importInstitution(system, importedInstitution, facebookApi);
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void importInstitution(System system, ImportedInstitution importedInstitution, Facebook facebookApi) {
        if (!isShuttingDown()) {
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
                logger.error("Could not read UCAS data for imported institution ID: " + importedInstitution.getId());
                return;
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

            AddressAdvertDTO ucasAddress = ucasInstitutionData.getAddress();
            if (ucasAddress != null) {
                address.setAddressLine1(ObjectUtils.firstNonNull(address.getAddressLine1(), ucasAddress.getAddressLine1()));
                address.setAddressCode(ObjectUtils.firstNonNull(address.getAddressCode(), ucasAddress.getAddressCode()));
                address.setAddressTown(ObjectUtils.firstNonNull(address.getAddressCode(), ucasAddress.getAddressTown()));
                if (address.getDomicile() == null) {
                    address.setDomicile(ucasAddress.getDomicile());
                }
            }

            if (address.getDomicile().getId() == null) {
                List<ImportedAdvertDomicile> domiciles = importedEntityService.searchByName(ImportedAdvertDomicile.class, address.getDomicile().getName());
                if (domiciles.isEmpty()) {
                    logger.error("Expected exactly one imported advert domicile for given search term: " + address.getDomicile().getName());
                    return;
                }
                address.getDomicile().setId(domiciles.get(0).getId());
            }

            try {
                institutionService.createInstitution(system.getUser(), institutionDTO, facebookId, facebookPage);
            } catch (WorkflowDuplicateResourceException e) {
                logger.error("Could not import institution: " + importedInstitution.getName(), e);
            }
        }
    }

}
