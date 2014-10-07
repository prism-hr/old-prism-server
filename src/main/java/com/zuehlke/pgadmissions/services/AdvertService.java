package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.FinancialDetails;
import com.zuehlke.pgadmissions.domain.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.dto.json.ExchangeRateLookupResponseDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFilterMetadataDTO;
import com.zuehlke.pgadmissions.rest.dto.FinancialDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
public class AdvertService {

    private static final Logger lOGGER = LoggerFactory.getLogger(AdvertService.class);

    private final HashMap<LocalDate, HashMap<String, BigDecimal>> exchangeRates = Maps.newHashMap();

    @Value("${integration.yahoo.exchange.rate.api.uri}")
    private String yahooExchangeRateApiUri;

    @Value("${integration.yahoo.exchange.rate.api.schema}")
    private String yahooExchangeRateApiSchema;

    @Value("${integration.yahoo.exchange.rate.api.table}")
    private String yahooExchangeRateApiTable;

    @Autowired
    private AdvertDAO advertDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private StateService stateService;

    @Autowired
    private GeocodableLocationService geocodableLocationService;

    @Autowired
    private RestTemplate restTemplate;

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

    public void updateAdvertClosingDate(Advert transientAdvert, LocalDate baseline) {
        Advert persistentAdvert = getById(transientAdvert.getId());
        AdvertClosingDate nextClosingDate = advertDAO.getNextAdvertClosingDate(persistentAdvert, baseline);
        persistentAdvert.setClosingDate(nextClosingDate);

        if (persistentAdvert.isProjectAdvert() && nextClosingDate == null) {
            persistentAdvert.getProject().setDueDate(baseline);
        }
    }

    public void saveAdvertDetails(Class<? extends Resource> resourceClass, Integer resourceId, AdvertDetailsDTO advertDetailsDTO)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException, IOException, JAXBException {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Advert advert = (Advert) PropertyUtils.getSimpleProperty(resource, "advert");
        InstitutionAddressDTO addressDTO = advertDetailsDTO.getAddress();

        InstitutionDomicile country = entityService.getById(InstitutionDomicile.class, addressDTO.getDomicile());
        InstitutionDomicileRegion region = entityService.getById(InstitutionDomicileRegion.class, addressDTO.getRegion());

        advert.setDescription(advertDetailsDTO.getDescription());
        advert.setApplyLink(advertDetailsDTO.getApplyLink());

        InstitutionAddress address = advert.getAddress();
        address.setDomicile(country);
        address.setRegion(region);
        address.setInstitution(resource.getInstitution());
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressDistrict(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());

        geocodableLocationService.setLocation(address);
    }

    public void saveFeesAndPayments(Class<? extends Resource> resourceClass, Integer resourceId, AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        LocalDate baseline = new LocalDate();

        Resource resource = resourceService.getById(resourceClass, resourceId);
        Advert advert = (Advert) PropertyUtils.getSimpleProperty(resource, "advert");

        String currencyAtLocale = getCurrencyAtLocale(advert);

        FinancialDetailsDTO feeDTO = feesAndPaymentsDTO.getFee();
        if (feeDTO.getInterval() != null) {
            updateFee(baseline, advert, currencyAtLocale, feeDTO);
        }

        FinancialDetailsDTO payDTO = feesAndPaymentsDTO.getPay();
        if (payDTO.getInterval() != null) {
            updatePay(baseline, advert, currencyAtLocale, payDTO);
        }

        advert.setLastCurrencyConversionDate(baseline);
    }

    @SuppressWarnings("unchecked")
    public void saveFilterMetadata(Class<? extends Resource> resourceClass, Integer resourceId, AdvertFilterMetadataDTO metadataDTO) {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Advert advert = (Advert) ReflectionUtils.getProperty(resource, "advert");

        Field[] properties = metadataDTO.getClass().getDeclaredFields();
        for (Field property : properties) {
            String propertyName = property.getName();
            List<Object> values = (List<Object>) ReflectionUtils.getProperty(metadataDTO, propertyName);

            if (values != null) {
                Set<Object> persistentMetadata = (Set<Object>) ReflectionUtils.getProperty(advert, propertyName);
                persistentMetadata.clear();

                boolean isTargetInstitutionsProperty = propertyName.equals("targetInstitution");
                for (Object value : values) {
                    value = isTargetInstitutionsProperty ? institutionService.getById((Integer) value) : value;
                    persistentMetadata.add(value);
                }
            }
        }
    }

    public void updateCurrencyConversion(Advert transientAdvert) throws IOException, JAXBException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Advert peristentAdvert = getById(transientAdvert.getId());
        LocalDate baseline = new LocalDate();

        if (peristentAdvert.hasCovertedFee()) {
            updateConvertedMonetaryValues(peristentAdvert.getFee(), baseline);
        }

        if (peristentAdvert.hasConvertedPay()) {
            updateConvertedMonetaryValues(peristentAdvert.getPay(), baseline);
        }

        peristentAdvert.setLastCurrencyConversionDate(baseline);
    }

    public List<Advert> getAdvertsWithElapsedCurrencyConversions(LocalDate baseline) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return advertDAO.getAdvertsWithElapsedCurrencyConversions(baseline, activeProgramStates, activeProjectStates);
    }

    private void updateFinancialDetails(FinancialDetails financialDetails, FinancialDetailsDTO financialDetailsDTO, String currencyAtLocale, LocalDate baseline)
            throws Exception {
        DurationUnit interval = financialDetailsDTO.getInterval();
        String currencySpecified = financialDetailsDTO.getCurrency();

        financialDetails.setInterval(interval);
        financialDetails.setCurrencySpecified(currencySpecified);
        financialDetails.setCurrencyAtLocale(currencyAtLocale);

        String intervalPrefixSpecified = interval.name().toLowerCase();
        BigDecimal minimumSpecified = financialDetailsDTO.getMinimum();
        BigDecimal maximumSpecified = financialDetailsDTO.getMaximum();

        String intervalPrefixGenerated;
        BigDecimal minimumGenerated;
        BigDecimal maximumGenerated;

        if (interval == DurationUnit.MONTH) {
            intervalPrefixGenerated = DurationUnit.YEAR.name().toLowerCase();
            minimumGenerated = minimumSpecified.multiply(new BigDecimal(12));
            maximumGenerated = maximumSpecified.multiply(new BigDecimal(12));
        } else {
            intervalPrefixGenerated = DurationUnit.MONTH.name().toLowerCase();
            minimumGenerated = minimumSpecified.divide(new BigDecimal(12), 2, RoundingMode.HALF_UP);
            maximumGenerated = maximumSpecified.divide(new BigDecimal(12), 2, RoundingMode.HALF_UP);
        }

        setMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified, maximumSpecified, intervalPrefixGenerated, minimumGenerated,
                maximumGenerated, "Specified");
        if (currencySpecified.equals(currencyAtLocale)) {
            setMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified, maximumSpecified, intervalPrefixGenerated, minimumGenerated,
                    maximumGenerated, "AtLocale");
        } else {
            try {
                BigDecimal rate = getExchangeRate(currencySpecified, currencyAtLocale, baseline);
                setConvertedMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified, maximumSpecified, intervalPrefixGenerated,
                        minimumGenerated, maximumGenerated, rate);
            } catch (Exception e) {
                lOGGER.error("Problem performing currency conversion", e);
            }
        }
    }

    private String getCurrencyAtLocale(Advert advert) {
        InstitutionAddress localeAddress = advert.getAddress();
        localeAddress = localeAddress == null ? advert.getInstitution().getAddress() : localeAddress;
        return localeAddress.getDomicile().getCurrency();
    }

    private void setMonetaryValues(FinancialDetails financialDetails, String intervalPrefixSpecified, BigDecimal minimumSpecified, BigDecimal maximumSpecified,
            String intervalPrefixGenerated, BigDecimal minimumGenerated, BigDecimal maximumGenerated, String context) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixSpecified + "Minimum" + context, minimumSpecified);
        PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixSpecified + "Maximum" + context, maximumSpecified);
        PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixGenerated + "Minimum" + context, minimumGenerated);
        PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixGenerated + "Maximum" + context, maximumGenerated);
    }

    private void setConvertedMonetaryValues(FinancialDetails financialDetails, String intervalPrefixSpecified, BigDecimal minimumSpecified,
            BigDecimal maximumSpecified, String intervalPrefixGenerated, BigDecimal minimumGenerated, BigDecimal maximumGenerated, BigDecimal rate)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (rate.compareTo(new BigDecimal(0)) == 1) {
            minimumSpecified = minimumSpecified.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            maximumSpecified = maximumSpecified.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            minimumGenerated = minimumGenerated.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            maximumGenerated = maximumGenerated.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            financialDetails.setConverted(true);
        } else {
            financialDetails.setConverted(false);
        }

        setMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified, maximumSpecified, intervalPrefixGenerated, minimumGenerated,
                maximumGenerated, "AtLocale");
    }

    private void updateConvertedMonetaryValues(FinancialDetails financialDetails, LocalDate baseline) throws IOException, JAXBException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String currencySpecified = financialDetails.getCurrencySpecified();
        String currencyAtLocale = financialDetails.getCurrencyAtLocale();

        try {
            BigDecimal rate = getExchangeRate(currencySpecified, currencyAtLocale, baseline);

            BigDecimal minimumSpecified;
            BigDecimal maximumSpecified;
            BigDecimal minimumGenerated;
            BigDecimal maximumGenerated;

            DurationUnit interval = financialDetails.getInterval();
            String intervalPrefixGenerated;

            if (interval == DurationUnit.MONTH) {
                minimumSpecified = financialDetails.getMonthMinimumSpecified();
                maximumSpecified = financialDetails.getMonthMaximumSpecified();
                minimumGenerated = financialDetails.getYearMinimumSpecified();
                maximumGenerated = financialDetails.getYearMaximumSpecified();
                intervalPrefixGenerated = DurationUnit.YEAR.name().toLowerCase();
            } else {
                minimumSpecified = financialDetails.getYearMinimumSpecified();
                maximumSpecified = financialDetails.getYearMaximumSpecified();
                minimumGenerated = financialDetails.getMonthMinimumSpecified();
                maximumGenerated = financialDetails.getMonthMaximumSpecified();
                intervalPrefixGenerated = DurationUnit.MONTH.name().toLowerCase();
            }

            setConvertedMonetaryValues(financialDetails, interval.name().toLowerCase(), minimumSpecified, maximumSpecified, intervalPrefixGenerated,
                    minimumGenerated, maximumGenerated, rate);
        } catch (Exception e) {
            lOGGER.error("Unable to perform currency conversion", e);
        }
    }

    private BigDecimal getExchangeRate(String specifiedCurrency, String currencyAtLocale, LocalDate baseline) throws IOException {
        removeExpiredExchangeRates(baseline);

        String pair = specifiedCurrency + currencyAtLocale;
        HashMap<String, BigDecimal> todaysRates = exchangeRates.get(baseline);

        if (todaysRates != null) {
            BigDecimal todaysRate = todaysRates.get(pair);
            if (todaysRate != null) {
                return todaysRate;
            }
        }

        String query = URLEncoder.encode("select Rate from " + yahooExchangeRateApiTable + " where pair = \"" + pair + "\"", "UTF-8");
        URI request = new DefaultResourceLoader().getResource(
                yahooExchangeRateApiUri + "?q=" + query + "&env=" + URLEncoder.encode(yahooExchangeRateApiSchema, "UTF-8") + "&format=json").getURI();
        ExchangeRateLookupResponseDTO response = restTemplate.getForObject(request, ExchangeRateLookupResponseDTO.class);

        BigDecimal todaysRate = response.getQuery().getResults().getRate().getRate();

        if (todaysRates == null) {
            todaysRates = new HashMap<String, BigDecimal>();
            todaysRates.put(pair, todaysRate);
            exchangeRates.put(baseline, todaysRates);
        } else {
            todaysRates.put(pair, todaysRate);
        }

        return todaysRate;
    }

    private void removeExpiredExchangeRates(LocalDate baseline) {
        for (LocalDate day : exchangeRates.keySet()) {
            if (day.isBefore(baseline)) {
                exchangeRates.remove(day);
            }
        }
    }

    private void updateFee(LocalDate baseline, Advert advert, String currencyAtLocale, FinancialDetailsDTO feeDTO) throws Exception {
        if (advert.getFee() == null) {
            advert.setFee(new FinancialDetails());
        }
        updateFinancialDetails(advert.getFee(), feeDTO, currencyAtLocale, baseline);
    }

    private void updatePay(LocalDate baseline, Advert advert, String currencyAtLocale, FinancialDetailsDTO payDTO) throws Exception {
        if (advert.getPay() == null) {
            advert.setPay(new FinancialDetails());
        }
        updateFinancialDetails(advert.getPay(), payDTO, currencyAtLocale, baseline);
    }

}
