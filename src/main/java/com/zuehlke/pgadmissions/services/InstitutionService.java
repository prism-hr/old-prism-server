package com.zuehlke.pgadmissions.services;

import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.Coordinates;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.mapping.InstitutionMapper;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationTargeting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private InstitutionMapper institutionMapper;

    @Inject
    private ResourceService resourceService;

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
            changeInstitutionCurrency(institution, newCurrency);
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
        return month == 1 ? businessYear.toString() : (businessYear.toString() + "/" + Integer.toString(businessYear + 1));
    }

    private void changeInstitutionCurrency(Institution institution, String newCurrency) throws Exception {
        List<Advert> advertsWithFeesAndPays = advertService.getAdvertsWithFinancialDetails(institution);
        for (Advert advertWithFeesAndPays : advertsWithFeesAndPays) {
            advertService.updateFinancialDetails(advertWithFeesAndPays, newCurrency);
        }
        institution.setCurrency(newCurrency);
    }

    private void changeInstitutionBusinessYear(Institution institution, Integer businessYearStartMonth) throws Exception {
        institution.setBusinessYearStartMonth(businessYearStartMonth);
        Integer businessYearEndMonth = businessYearStartMonth == 1 ? 12 : businessYearStartMonth - 1;
        institutionDAO.changeInstitutionBusinessYear(institution.getId(), businessYearEndMonth);
    }

    public List<InstitutionRepresentationTargeting> getInstitutionBySubjectAreas(Coordinates coordinates, List<Integer> subjectAreas) {
        Set<Integer> subjectAreaFamily = importedEntityService.getImportedSubjectAreaFamily(subjectAreas.toArray(new Integer[subjectAreas.size()]));
        return institutionDAO.getInstitutionBySubjectAreas(coordinates, subjectAreaFamily).stream()
                .map(target -> institutionMapper.getInstitutionRepresentationTargeting(getById(target.getId()), target.getRelevance(), target.getDistance()))
                .collect(Collectors.toList());
    }

}
