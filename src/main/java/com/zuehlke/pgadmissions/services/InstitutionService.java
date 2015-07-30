package com.zuehlke.pgadmissions.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;

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
        resourceService.updateResource(institution, institutionDTO);
        institution.setGoogleId(institution.getAdvert().getAddress().getGoogleId());

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

    // FIXME: generalise for department
    public List<ResourceChildCreationDTO> getInstitutionsForWhichUserCanCreateProgram() {
        List<PrismState> states = stateService.getActiveResourceStates(INSTITUTION);
        boolean userLoggedIn = userService.getCurrentUser() != null;
        return institutionDAO.getInstitutionsForWhichUserCanCreateProgram(states, userLoggedIn);
    }

    public List<ResourceChildCreationDTO> getInstitutionsForWhichUserCanCreateProject() {
        Map<Integer, ResourceChildCreationDTO> index = Maps.newHashMap();
        Map<String, ResourceChildCreationDTO> institutions = Maps.newTreeMap();

        List<PrismState> institutionStates = stateService.getActiveResourceStates(INSTITUTION);
        boolean userLoggedIn = userService.getCurrentUser() != null;

        List<ResourceChildCreationDTO> institutionProjectParents = institutionDAO
                .getInstitutionsForWhichUserCanCreateProject(institutionStates, userLoggedIn);
        for (ResourceChildCreationDTO institutionProjectParent : institutionProjectParents) {
            ResourceChildCreationDTO institution = new ResourceChildCreationDTO()
                    .withResource(institutionProjectParent.getResource()).withPartnerMode(institutionProjectParent.getPartnerMode());
            index.put(institutionProjectParent.getResource().getId(), institution);
            institutions.put(institutionProjectParent.getResource().getName(), institution);
        }

        List<PrismState> programStates = stateService.getActiveResourceStates(PROGRAM);

        List<ResourceChildCreationDTO> institutionProgramProjectParents = institutionDAO
                .getInstitutionsWhichHaveProgramsForWhichUserCanCreateProject(programStates, userLoggedIn);
        for (ResourceChildCreationDTO institutionProgramProjectParent : institutionProgramProjectParents) {
            ResourceChildCreationDTO institution = index.get(institutionProgramProjectParent.getResource().getId());
            if (institution == null) {
                institutions.put(institutionProgramProjectParent.getResource().getName(),
                        new ResourceChildCreationDTO().withResource(institutionProgramProjectParent.getResource()));
            }
        }

        return Lists.newLinkedList(institutions.values());
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

    public ResourceRepresentationSimple getInstitutionBySubjectAreas(List<Integer> subjectAreas) {
        return institutionDAO.getInstitutionBySubjectAreas(subjectAreas);
    }
}
