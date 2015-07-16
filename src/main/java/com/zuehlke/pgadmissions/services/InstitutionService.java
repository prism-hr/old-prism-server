package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

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
    private ResourceService resourceService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    public Institution getById(Integer id) {
        return entityService.getById(Institution.class, id);
    }

    public List<Integer> getApprovedInstitutions() {
        return institutionDAO.getApprovedInstitutions();
    }

    public List<Institution> getApprovedInstitutionsByDomicile(ImportedAdvertDomicile domicile) {
        return institutionDAO.getApprovedInstitutionsByDomicile(domicile);
    }

    public Institution getUclInstitution() {
        return institutionDAO.getUclInstitution();
    }

    public void update(Institution institution, InstitutionDTO institutionDTO) throws Exception {
        AdvertDTO advertDTO = institutionDTO.getAdvert();
        Advert advert = institution.getAdvert();
        advertService.updateAdvert(institution.getParentResource(), advert, advertDTO);
        institution.setGoogleId(advert.getAddress().getGoogleId());

        institution.setTitle(advert.getTitle());

        String oldCurrency = institution.getCurrency();
        String newCurrency = institutionDTO.getCurrency();
        if (!oldCurrency.equals(newCurrency)) {
            changeInstitutionCurrency(institution, oldCurrency, newCurrency);
        }

        Integer oldBusinessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer newBusinessYearStartMonth = institutionDTO.getBusinessYearStartMonth();
        if (!oldBusinessYearStartMonth.equals(newBusinessYearStartMonth)) {
            changeInstitutionBusinessYear(institution, newBusinessYearStartMonth);
        }

        institution.setMinimumWage(institutionDTO.getMinimumWage());

        resourceService.setResourceConditions(institution, institutionDTO.getConditions());
    }

    public List<String> listAvailableCurrencies() {
        return institutionDAO.listAvailableCurrencies();
    }

    public void save(Institution institution) {
        entityService.save(institution);
    }

    public List<Institution> list() {
        return institutionDAO.list();
    }

    public Institution getActivatedInstitutionByGoogleId(String googleId) {
        return institutionDAO.getActivatedInstitutionByGoogleId(googleId);
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> programStates, List<PrismState> projectStates) {
        return institutionDAO.getLatestUpdatedTimestampSitemap(programStates, projectStates);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getSitemapEntries(activeInstitutionStates, activeProgramStates, activeProjectStates);
    }

    public List<ResourceSearchEngineDTO> getActiveInstitutions() {
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getRelatedInstitutions(activeInstitutionStates, activeProgramStates, activeProjectStates);
    }

    public List<Institution> getInstitutions(String searchTerm, String[] googleIds) {
        return institutionDAO.getInstitutions(searchTerm, googleIds);
    }

    public void disableInstitutionDomiciles(List<String> updates) {
        institutionDAO.disableInstitutionDomiciles(updates);
    }

    public String getBusinessYear(Institution institution, Integer year, Integer month) {
        Integer businessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer businessYear = month < businessYearStartMonth ? (year - 1) : year;
        return month == 1 ? businessYear.toString() : (businessYear.toString() + "/" + new Integer(businessYear + 1).toString());
    }

    public List<ResourceChildCreationDTO> getInstitutionsForWhichUserCanCreateProgram() {
        List<PrismState> states = stateService.getActiveInstitutionStates();
        boolean userLoggedIn = userService.getCurrentUser() != null;
        return institutionDAO.getInstitutionsForWhichUserCanCreateProgram(states, userLoggedIn);
    }

    public List<ResourceChildCreationDTO> getInstitutionsForWhichUserCanCreateProject() {
        Map<Integer, ResourceChildCreationDTO> index = Maps.newHashMap();
        Map<String, ResourceChildCreationDTO> institutions = Maps.newTreeMap();

        List<PrismState> institutionStates = stateService.getActiveInstitutionStates();
        boolean userLoggedIn = userService.getCurrentUser() != null;

        List<ResourceChildCreationDTO> institutionProjectParents = institutionDAO
                .getInstitutionsForWhichUserCanCreateProject(institutionStates, userLoggedIn);
        for (ResourceChildCreationDTO institutionProjectParent : institutionProjectParents) {
            ResourceChildCreationDTO institution = new ResourceChildCreationDTO()
                    .withResource(institutionProjectParent.getResource()).withPartnerMode(institutionProjectParent.getPartnerMode());
            index.put(institutionProjectParent.getResource().getId(), institution);
            institutions.put(institutionProjectParent.getResource().getTitle(), institution);
        }

        List<PrismState> programStates = stateService.getActiveProgramStates();

        List<ResourceChildCreationDTO> institutionProgramProjectParents = institutionDAO
                .getInstitutionsWhichHaveProgramsForWhichUserCanCreateProject(programStates, userLoggedIn);
        for (ResourceChildCreationDTO institutionProgramProjectParent : institutionProgramProjectParents) {
            ResourceChildCreationDTO institution = index.get(institutionProgramProjectParent.getResource().getId());
            if (institution == null) {
                institutions.put(institutionProgramProjectParent.getResource().getTitle(),
                        new ResourceChildCreationDTO().withResource(institutionProgramProjectParent.getResource()));
            }
        }

        return Lists.newLinkedList(institutions.values());
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer institutionId, List<PrismState> activeInstitutionStates, List<PrismState> activeProgramStates,
            List<PrismState> activeProjectStates) {
        return institutionDAO.getSearchEngineAdvert(institutionId, activeInstitutionStates, activeProgramStates, activeProjectStates);
    }

    private void changeInstitutionCurrency(Institution institution, String oldCurrency, String newCurrency) throws Exception {
        List<Advert> advertsWithFeesAndPays = advertService.getAdvertsWithFinancialDetails(institution);
        for (Advert advertWithFeesAndPays : advertsWithFeesAndPays) {
            advertService.updateFeesAndPayments(advertWithFeesAndPays, newCurrency);
        }
        institution.setCurrency(newCurrency);
    }

    private void changeInstitutionBusinessYear(Institution institution, Integer businessYearStartMonth) throws Exception {
        institution.setBusinessYearStartMonth(businessYearStartMonth);
        Integer businessYearEndMonth = businessYearStartMonth == 1 ? 12 : businessYearStartMonth - 1;
        institutionDAO.changeInstitutionBusinessYear(institution.getId(), businessYearEndMonth);
    }

}
