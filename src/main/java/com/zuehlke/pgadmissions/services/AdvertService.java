package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_COMMENT_UPDATED_ADVERT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_COMMENT_UPDATED_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_COMMENT_UPDATED_CLOSING_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.PROGRAM_COMMENT_UPDATED_FEE_AND_PAYMENT;
import static com.zuehlke.pgadmissions.utils.WordUtils.pluralize;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFilterCategory;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.json.ExchangeRateLookupResponseDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.AdvertCategoriesDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertClosingDateDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFeesAndPaymentsDTO;
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

    public AdvertClosingDate getClosingDateById(Integer id) {
        return entityService.getById(AdvertClosingDate.class, id);
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

    public List<Advert> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
        return advertDAO.getAdvertsWithElapsedClosingDates(baseline);
    }

    public void updateAdvert(Class<? extends Resource> resourceClass, Integer resourceId, AdvertDetailsDTO advertDetailsDTO) throws Exception {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Advert advert = (Advert) ReflectionUtils.getProperty(resource, "advert");

        InstitutionAddressDTO addressDTO = advertDetailsDTO.getAddress();
        InstitutionDomicile country = entityService.getById(InstitutionDomicile.class, addressDTO.getDomicile());
        InstitutionDomicileRegion region = entityService.getById(InstitutionDomicileRegion.class, addressDTO.getRegion());

        advert.setDescription(advertDetailsDTO.getDescription());
        advert.setHomepage(advertDetailsDTO.getHomepage());
        advert.setApplyHomepage(advertDetailsDTO.getApplyHomepage());

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
        resourceService.executeUpdate(resource, PROGRAM_COMMENT_UPDATED_ADVERT);
    }

    public void updateFeesAndPayments(Class<? extends Resource> resourceClass, Integer resourceId, AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        LocalDate baseline = new LocalDate();

        Resource resource = resourceService.getById(resourceClass, resourceId);
        Advert advert = (Advert) ReflectionUtils.getProperty(resource, "advert");

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
        resourceService.executeUpdate(resource, PROGRAM_COMMENT_UPDATED_FEE_AND_PAYMENT);
    }

    @SuppressWarnings("unchecked")
    public void updateCategories(Class<? extends Resource> resourceClass, Integer resourceId, AdvertCategoriesDTO categoriesDTO) throws DeduplicationException {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Advert advert = (Advert) ReflectionUtils.getProperty(resource, "advert");

        for (String propertyName : new String[] { "domain", "industry", "function", "competency", "theme", "institution", "programType" }) {
            String propertySetterName = "add" + WordUtils.capitalize(propertyName);
            List<Object> values = (List<Object>) ReflectionUtils.getProperty(categoriesDTO, pluralize(propertyName));

            if (values != null) {
                Collection<?> persistentMetadata = (Collection<?>) ReflectionUtils.getProperty(advert, pluralize(propertyName));
                persistentMetadata.clear();
                entityService.flush();

                boolean isInstitutionsProperty = propertyName.equals("institution");
                for (Object value : values) {
                    value = isInstitutionsProperty ? institutionService.getById((Integer) value) : value;
                    ReflectionUtils.invokeMethod(advert, propertySetterName, value);
                }
            }
        }

        resourceService.executeUpdate(resource, PROGRAM_COMMENT_UPDATED_CATEGORY);
    }

    public void updateClosingDate(Class<? extends Resource> resourceClass, Integer resourceId, AdvertClosingDateDTO closingDateDTO)
            throws DeduplicationException {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Advert advert = (Advert) ReflectionUtils.getProperty(resource, "advert");

        AdvertClosingDate transientClosingDate = new AdvertClosingDate().withAdvert(advert).withClosingDate(closingDateDTO.getClosingDate())
                .withStudyPlaces(closingDateDTO.getStudyPlaces());
        AdvertClosingDate persistentClosingDate = entityService.getDuplicateEntity(transientClosingDate);

        if (persistentClosingDate == null) {
            advert.addClosingDate(transientClosingDate);
        } else {
            persistentClosingDate.setStudyPlaces(transientClosingDate.getStudyPlaces());
            entityService.evict(transientClosingDate);
        }

        resourceService.executeUpdate(resource, PROGRAM_COMMENT_UPDATED_CLOSING_DATE);
    }

    public void deleteClosingDate(Class<? extends Resource> resourceClass, Integer resourceId, Integer closingDateId) throws DeduplicationException {
        Resource resource = resourceService.getById(resourceClass, resourceId);
        Advert advert = (Advert) ReflectionUtils.getProperty(resource, "advert");
        AdvertClosingDate closingDate = getClosingDateById(closingDateId);
        if (closingDate != null && closingDate.getAdvert().equals(advert)) {
            advert.getClosingDates().remove(closingDate);
            if (closingDate.equals(advert.getClosingDate())) {
                advert.setClosingDate(advertDAO.getNextAdvertClosingDate(advert, new LocalDate()));
            }
        }
        resourceService.executeUpdate(resource, PROGRAM_COMMENT_UPDATED_CLOSING_DATE);
    }

    public void updateClosingDate(Advert transientAdvert, LocalDate baseline) {
        Advert persistentAdvert = getById(transientAdvert.getId());
        AdvertClosingDate nextClosingDate = advertDAO.getNextAdvertClosingDate(persistentAdvert, baseline);
        persistentAdvert.setClosingDate(nextClosingDate);
    }

    public void updateCurrencyConversion(Advert transientAdvert) throws IOException, JAXBException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Advert persistentAdvert = getById(transientAdvert.getId());
        LocalDate baseline = new LocalDate();

        if (persistentAdvert.hasConvertedFee()) {
            updateConvertedMonetaryValues(persistentAdvert.getFee(), baseline);
        }

        if (persistentAdvert.hasConvertedPay()) {
            updateConvertedMonetaryValues(persistentAdvert.getPay(), baseline);
        }

        persistentAdvert.setLastCurrencyConversionDate(baseline);
    }

    public List<Advert> getAdvertsWithElapsedCurrencyConversions(LocalDate baseline) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return advertDAO.getAdvertsWithElapsedCurrencyConversions(baseline, activeProgramStates, activeProjectStates);
    }

    public InstitutionAddress createAddressCopy(InstitutionAddress address) {
        InstitutionAddress newAddress = new InstitutionAddress().withDomicile(address.getDomicile()).withRegion(address.getRegion())
                .withInstitution(address.getInstitution()).withAddressLine1(address.getAddressLine1()).withAddressLine2(address.getAddressLine2())
                .withAddressTown(address.getAddressTown()).withAddressDistrict(address.getAddressDistrict()).withAddressCode(address.getAddressCode());
        entityService.save(newAddress);
        return newAddress;
    }

    public List<String> getLocalizedTags(Institution institution, PrismLocale locale, Class<? extends AdvertFilterCategory> clazz) {
        return advertDAO.getLocalizedTags(institution, locale, clazz);
    }

    public List<String> getLocalizedThemes(Application application) {
        return advertDAO.getLocalizedThemes(application);
    }

    private String getCurrencyAtLocale(Advert advert) {
        InstitutionAddress localeAddress = advert.getAddress();
        localeAddress = localeAddress == null ? advert.getInstitution().getAddress() : localeAddress;
        return localeAddress.getDomicile().getCurrency();
    }

    private void setMonetaryValues(AdvertFinancialDetail financialDetails, String intervalPrefixSpecified, BigDecimal minimumSpecified,
            BigDecimal maximumSpecified, String intervalPrefixGenerated, BigDecimal minimumGenerated, BigDecimal maximumGenerated, String context)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixSpecified + "Minimum" + context, minimumSpecified);
        PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixSpecified + "Maximum" + context, maximumSpecified);
        PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixGenerated + "Minimum" + context, minimumGenerated);
        PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixGenerated + "Maximum" + context, maximumGenerated);
    }

    private void setConvertedMonetaryValues(AdvertFinancialDetail financialDetails, String intervalPrefixSpecified, BigDecimal minimumSpecified,
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

    private void updateConvertedMonetaryValues(AdvertFinancialDetail financialDetails, LocalDate baseline) throws IOException, JAXBException,
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
            advert.setFee(new AdvertFinancialDetail());
        }
        updateFinancialDetails(advert.getFee(), feeDTO, currencyAtLocale, baseline);
    }

    private void updatePay(LocalDate baseline, Advert advert, String currencyAtLocale, FinancialDetailsDTO payDTO) throws Exception {
        if (advert.getPay() == null) {
            advert.setPay(new AdvertFinancialDetail());
        }
        updateFinancialDetails(advert.getPay(), payDTO, currencyAtLocale, baseline);
    }

    private void updateFinancialDetails(AdvertFinancialDetail financialDetails, FinancialDetailsDTO financialDetailsDTO, String currencyAtLocale,
            LocalDate baseline) throws Exception {
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

}
