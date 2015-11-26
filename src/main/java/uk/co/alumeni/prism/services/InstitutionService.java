package uk.co.alumeni.prism.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.dao.InstitutionDAO;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.dto.ResourceLocationDTO;
import uk.co.alumeni.prism.rest.dto.resource.InstitutionDTO;

import javax.inject.Inject;
import java.util.List;

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

    public Institution getById(Integer id) {
        return entityService.getById(Institution.class, id);
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
