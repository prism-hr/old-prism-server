package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_CREATE_INSTITUTION;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.facebook.api.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.ByteStreams;
import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.document.PrismFileCategory;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceLocationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.InstitutionDTO;

@Service
@Transactional
public class InstitutionService {

    private static Logger logger = LoggerFactory.getLogger(InstitutionService.class);

    @Inject
    private InstitutionDAO institutionDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private DocumentService documentService;

    @Inject
    private EntityService entityService;

    @Inject
    private AddressService geocodableLocationService;

    @Inject
    private ResourceService resourceService;

    public Institution getById(Integer id) {
        return entityService.getById(Institution.class, id);
    }

    public Institution getInstitutionByImportedCode(String importedCode) {
        return institutionDAO.getInstitutionByImportedCode(importedCode);
    }

    public Institution createInstitution(User user, InstitutionDTO institutionDTO, String facebookId, Page facebookPage) {
        ActionOutcomeDTO outcome = resourceService.createResource(user, actionService.getById(SYSTEM_CREATE_INSTITUTION), institutionDTO);
        Institution institution = (Institution) outcome.getResource();
        Integer institutionId = institution.getId();
        if (facebookId != null) {
            try {
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpEntity logoEntity = httpclient.execute(new HttpGet("http://graph.facebook.com/" + facebookId + "/picture?type=large")).getEntity();
                byte[] logoImageContent = ByteStreams.toByteArray(logoEntity.getContent());
                documentService.createImage("" + institutionId + "_logo", logoImageContent, logoEntity.getContentType().getValue(), institutionId,
                        PrismFileCategory.PrismImageCategory.INSTITUTION_LOGO);

                if (facebookPage.getCover() != null) {
                    HttpEntity backgroundEntity = httpclient.execute(new HttpGet(facebookPage.getCover().getSource())).getEntity();
                    byte[] backgroundImageContent = ByteStreams.toByteArray(backgroundEntity.getContent());
                    documentService.createImage("" + institutionId + "_background", backgroundImageContent, backgroundEntity.getContentType().getValue(),
                            institutionId, PrismFileCategory.PrismImageCategory.INSTITUTION_BACKGROUND);
                }
            } catch (IOException e) {
                logger.error("Could not load facebook image for institution ID: " + institutionId, e);
            }
        }

        try {
            geocodableLocationService.geocodeAddressAsLocation(institution.getAdvert().getAddress(), institution.getName());
        } catch (Exception e) {
            logger.error("Could not set geocoded location for institution ID: " + institutionId, e);
        }
        return institution;
    }

    public void update(Institution institution, InstitutionDTO institutionDTO) {
        resourceService.updateResource(institution, institutionDTO);
        institution.setGoogleId(institution.getAdvert().getAddress().getGoogleId());

        String oldCurrency = institution.getCurrency();
        String newCurrency = institutionDTO.getCurrency();
        if (!oldCurrency.equals(newCurrency)) {
            changeInstitutionCurrency(institution, newCurrency);
        }

        Integer oldBusinessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer newBusinessYearStartMonth = institutionDTO.getBusinessYearStartMonth();
        if (!oldBusinessYearStartMonth.equals(newBusinessYearStartMonth)) {
            changeInstitutionBusinessYear(institution, newBusinessYearStartMonth);
        }
    }

    public List<String> getAvailableCurrencies() {
        return institutionDAO.getAvailableCurrencies();
    }

    public void save(Institution institution) {
        entityService.save(institution);
    }

    public Institution getInstitutionByGoogleId(String googleId) {
        return institutionDAO.getInstitutionByGoogleId(googleId);
    }

    public List<ResourceLocationDTO> getInstitutions(String query, String[] googleIds) {
        return institutionDAO.getInstitutions(query, googleIds);
    }

    public String getBusinessYear(Institution institution, Integer year, Integer month) {
        Integer businessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer businessYear = month < businessYearStartMonth ? (year - 1) : year;
        return month == 1 ? businessYear.toString()
                : (businessYear.toString() + "/" + Integer.toString(businessYear + 1));
    }

    private void changeInstitutionCurrency(Institution institution, String newCurrency) {
        List<Advert> advertsWithFeesAndPays = advertService.getAdvertsWithFinancialDetails(institution);
        for (Advert advertWithFeesAndPays : advertsWithFeesAndPays) {
            advertService.updateFinancialDetails(advertWithFeesAndPays, newCurrency);
        }
        institution.setCurrency(newCurrency);
    }

    private void changeInstitutionBusinessYear(Institution institution, Integer businessYearStartMonth) {
        institution.setBusinessYearStartMonth(businessYearStartMonth);
        Integer businessYearEndMonth = businessYearStartMonth == 1 ? 12 : businessYearStartMonth - 1;
        institutionDAO.changeInstitutionBusinessYear(institution.getId(), businessYearEndMonth);
    }

}
