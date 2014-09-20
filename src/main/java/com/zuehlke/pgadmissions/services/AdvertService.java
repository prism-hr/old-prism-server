package com.zuehlke.pgadmissions.services;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.FeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.rest.dto.FinancialDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AdvertService {

    @Autowired
    private AdvertDAO advertDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private StateService stateService;

    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }

    // TODO: user filters
    public List<Advert> getActiveAdverts() {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return advertDAO.getActiveAdverts(activeProgramStates, activeProjectStates);
    }

    public List<Advert> getRecommendedAdverts(User user) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return advertDAO.getRecommendedAdverts(user, activeProgramStates, activeProjectStates);
    }

    // TODO: internal application link and other summary information (e.g. pay/fee according to user requirements)
    public String getRecommendedAdvertsForEmail(User user) {
        List<Advert> adverts = getRecommendedAdverts(user);
        List<String> recommendations = Lists.newLinkedList();

        for (Advert advert : adverts) {
            Project project = advert.getProject();
            String applyLink = advert.getApplyLink();

            recommendations.add(advert.getProgram().getTitle() + "<br/>" + project == null ? ""
                    : project.getTitle() + "<br/>" + applyLink == null ? "whatever the internal application link is" : applyLink);
        }

        return Joiner.on("<br/>").join(recommendations);
    }

    public List<Advert> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
        return advertDAO.getAdvertsWithElapsedClosingDates(baseline);
    }

    public void updateAdvertClosingDate(LocalDate baseline, Advert advert) {
        AdvertClosingDate nextClosingDate = advertDAO.getNextAdvertClosingDate(advert, baseline);
        advert.setClosingDate(nextClosingDate);

        if (advert.isProjectAdvert() && nextClosingDate == null) {
            advert.getProject().setDueDate(baseline);
        }
    }

    public void saveAdvertDetails(Class<? extends Resource> resourceClass, Integer resourceId, AdvertDetailsDTO advertDetailsDTO) throws Exception {
        Resource resource = entityService.getById(resourceClass, resourceId);
        Advert advert = (Advert) PropertyUtils.getSimpleProperty(resource, "advert");
        InstitutionAddress address = advert.getAddress();
        InstitutionAddressDTO addressDTO = advertDetailsDTO.getAddress();

        InstitutionDomicile country = entityService.getById(InstitutionDomicile.class, addressDTO.getCountry());
        InstitutionDomicileRegion region = entityService.getById(InstitutionDomicileRegion.class, addressDTO.getRegion());

        advert.setDescription(advertDetailsDTO.getDescription());
        advert.setApplyLink(advertDetailsDTO.getApplyLink());

        address.setDomicile(country);
        address.setRegion(region);
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressDistrict(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());
    }


    public void saveFeesAndPayments(Class<? extends Resource> resourceClass, Integer resourceId, FeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        Resource resource = entityService.getById(resourceClass, resourceId);
        Advert advert = (Advert) PropertyUtils.getSimpleProperty(resource, "advert");

        updateFinancialDetails(advert.getFee(), feesAndPaymentsDTO.getFee());
        updateFinancialDetails(advert.getPay(), feesAndPaymentsDTO.getPay());
    }

    private void updateFinancialDetails(FinancialDetails financialDetails, FinancialDetailsDTO financialDetailsDTO) throws Exception {
        DurationUnit interval = financialDetailsDTO.getInterval();
        if(interval != null){
            financialDetails.setCurrency(financialDetailsDTO.getCurrency());
            financialDetails.setInterval(interval);

            BigDecimal minimum = financialDetailsDTO.getMinimum();
            BigDecimal maximum = financialDetailsDTO.getMaximum();

            DurationUnit otherInterval = interval == DurationUnit.YEAR ? DurationUnit.MONTH : DurationUnit.YEAR;
            PropertyUtils.setSimpleProperty(financialDetails, interval.name().toLowerCase() + "MinimumSpecified", minimum);
            PropertyUtils.setSimpleProperty(financialDetails, interval.name().toLowerCase() + "MaximumSpecified", maximum);
            PropertyUtils.setSimpleProperty(financialDetails, otherInterval.name().toLowerCase() + "MinimumSpecified", null);
            PropertyUtils.setSimpleProperty(financialDetails, otherInterval.name().toLowerCase() + "MaximumSpecified", null);

        }
    }
}
