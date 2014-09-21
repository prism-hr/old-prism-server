package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.FeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.rest.dto.FinancialDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.utils.ConversionUtils;
import com.zuehlke.pgadmissions.yahoo.jaxb.Query;

@Service
@Transactional
public class AdvertService {

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
    private StateService stateService;

    @Autowired
    private GeocodableLocationService geocodableLocationService;

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

    public void updateAdvertClosingDate(Advert advert, LocalDate baseline) {
        advert = getById(advert.getId());
        AdvertClosingDate nextClosingDate = advertDAO.getNextAdvertClosingDate(advert, baseline);
        advert.setClosingDate(nextClosingDate);

        if (advert.isProjectAdvert() && nextClosingDate == null) {
            advert.getProject().setDueDate(baseline);
        }
    }

    public void saveAdvertDetails(Class<? extends Resource> resourceClass, Integer resourceId, AdvertDetailsDTO advertDetailsDTO)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException, IOException, JAXBException {
        Resource resource = entityService.getById(resourceClass, resourceId);
        Advert advert = (Advert) PropertyUtils.getSimpleProperty(resource, "advert");
        InstitutionAddressDTO addressDTO = advertDetailsDTO.getAddress();

        InstitutionDomicile country = entityService.getById(InstitutionDomicile.class, addressDTO.getCountry());
        InstitutionDomicileRegion region = entityService.getById(InstitutionDomicileRegion.class, addressDTO.getRegion());

        advert.setDescription(advertDetailsDTO.getDescription());
        advert.setApplyLink(advertDetailsDTO.getApplyLink());

        InstitutionAddress address = advert.getAddress();
        address.setDomicile(country);
        address.setRegion(region);
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressDistrict(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());

        geocodableLocationService.setLocation(address);
    }

    public void saveFeesAndPayments(Class<? extends Resource> resourceClass, Integer resourceId, FeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        LocalDate baseline = new LocalDate();

        Resource resource = entityService.getById(resourceClass, resourceId);
        Advert advert = (Advert) PropertyUtils.getSimpleProperty(resource, "advert");

        String currencyAtLocale = getCurrencyAtLocale(advert);

        FinancialDetailsDTO feeDTO = feesAndPaymentsDTO.getFee();
        if (feeDTO.getInterval() != null) {
            updateFinancialDetails(advert.getFee(), feeDTO, currencyAtLocale, baseline);
        }

        FinancialDetailsDTO payDTO = feesAndPaymentsDTO.getPay();
        if (payDTO.getInterval() != null) {
            updateFinancialDetails(advert.getPay(), payDTO, currencyAtLocale, baseline);
        }

        advert.setLastCurrencyConversionDate(baseline);
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
            BigDecimal rate = getExchangeRate(currencySpecified, currencyAtLocale, baseline);
            setConvertedMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified, maximumSpecified, intervalPrefixGenerated,
                    minimumGenerated, maximumGenerated, rate);
        }
    }

    public void updateCurrencyConversion(Advert advert) throws IOException, JAXBException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        LocalDate baseline = new LocalDate();
        advert = getById(advert.getId());

        if (advert.hasCovertedFee()) {
            updateConvertedMonetaryValues(advert.getFee(), baseline);
        }

        if (advert.hasConvertedPay()) {
            updateConvertedMonetaryValues(advert.getPay(), baseline);
        }

        advert.setLastCurrencyConversionDate(baseline);
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
    }

    private BigDecimal getExchangeRate(String specifiedCurrency, String currencyAtLocale, LocalDate baseline) throws IOException, JAXBException {
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
        URL request = new DefaultResourceLoader().getResource(
                yahooExchangeRateApiUri + "?q=" + query + "&env=" + URLEncoder.encode(yahooExchangeRateApiSchema, "UTF-8")).getURL();
        JAXBContext jaxbContext = JAXBContext.newInstance(Query.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Query response = (Query) JAXBIntrospector.getValue(unmarshaller.unmarshal(request));
        BigDecimal todaysRate = ConversionUtils.floatToBigDecimal(response.getResults().getRate().getRate(), 4);

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

}
