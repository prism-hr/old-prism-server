package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.MONTH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
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
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFilterCategory;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;
import com.zuehlke.pgadmissions.domain.program.AdvertStudyOption;
import com.zuehlke.pgadmissions.domain.program.AdvertStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.dto.json.ExchangeRateLookupResponseDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertCategoriesDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertClosingDateDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertFeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.rest.dto.AdvertResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.FinancialDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Service
@Transactional
public class AdvertService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertService.class);

    private final HashMap<LocalDate, HashMap<String, BigDecimal>> exchangeRates = Maps.newHashMap();

    @Value("${integration.yahoo.exchange.rate.api.uri}")
    private String yahooExchangeRateApiUri;

    @Value("${integration.yahoo.exchange.rate.api.schema}")
    private String yahooExchangeRateApiSchema;

    @Value("${integration.yahoo.exchange.rate.api.table}")
    private String yahooExchangeRateApiTable;

    @Inject
    private AdvertDAO advertDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private StateService stateService;

    @Inject
    private GeocodableLocationService geocodableLocationService;

    @Inject
    private RestTemplate restTemplate;

    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }

    public void save(Advert advert) {
        Set<AdvertStudyOption> advertStudyOptions = advert.getAdvertStudyOptions();
        for (AdvertStudyOption advertStudyOption : advertStudyOptions) {
            entityService.save(advertStudyOption);
        }
        entityService.save(advert);
    }

    public AdvertClosingDate getClosingDateById(Integer id) {
        return entityService.getById(AdvertClosingDate.class, id);
    }

    public List<Advert> getAdverts(OpportunitiesQueryDTO queryDTO, List<PrismState> programStates, List<PrismState> projectStates) {
        programStates = queryDTO.getPrograms() == null ? programStates : stateService.getProgramStates();
        projectStates = queryDTO.getProjects() == null ? projectStates : stateService.getProjectStates();

        if (queryDTO.isResourceAction()) {
            Resource resource = resourceService.getById(queryDTO.getActionId().getScope().getResourceClass(), queryDTO.getResourceId());
            Resource parentResource = resource.getParentResource();
            PrismScope parentResourceScope = parentResource.getResourceScope();

            switch (parentResourceScope) {
            case INSTITUTION:
            case PROGRAM:
            case PROJECT:
                PrismReflectionUtils.setProperty(queryDTO, parentResourceScope.getLowerCamelName() + "s", new Integer[] { parentResource.getId() });
                break;
            case SYSTEM:
                break;
            default:
                throw new Error();
            }
        }

        List<Integer> adverts = advertDAO.getAdverts(programStates, projectStates, queryDTO);

        if (adverts.isEmpty()) {
            return Lists.newArrayList();
        } else {
            Integer[] programs = queryDTO.getPrograms();
            return advertDAO.getActiveAdverts(adverts, programs != null && programs.length == 1);
        }
    }

    public List<AdvertRecommendationDTO> getRecommendedAdverts(User user) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        List<Integer> advertsRecentlyAppliedFor = advertDAO.getAdvertsRecentlyAppliedFor(user, new LocalDate().minusYears(1));
        return advertDAO.getRecommendedAdverts(user, activeProgramStates, activeProjectStates, advertsRecentlyAppliedFor);
    }

    public void updateResource(PrismScope resourceScope, Integer resourceId, AdvertResourceDTO advertResourceDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        if (BooleanUtils.isNotTrue(resource.getImported())) {
            advertDAO.deleteAdvertStudyOptionInstances(advert);
            advertDAO.deleteAdvertStudyOptions(advert);
            advert.getAdvertStudyOptions().clear();
            copyStudyOptions(advert, advertResourceDTO);
            for (AdvertStudyOption studyOption : advert.getAdvertStudyOptions()) {
                entityService.save(studyOption);
            }
        }
    }

    public void updateDetail(PrismScope resourceScope, Integer resourceId, AdvertDetailsDTO advertDetailsDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        InstitutionAddressDTO addressDTO = advertDetailsDTO.getAddress();
        InstitutionDomicile domicile = entityService.getById(InstitutionDomicile.class, addressDTO.getDomicile());

        advert.setDescription(advertDetailsDTO.getDescription());
        advert.setHomepage(advertDetailsDTO.getHomepage());

        InstitutionAddress address = advert.getAddress();
        address.setDomicile(domicile);
        address.setInstitution(resource.getInstitution());
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressRegion(addressDTO.getAddressDistrict());
        address.setAddressCode(addressDTO.getAddressCode());

        geocodableLocationService.setLocation(address);
        resourceService.executeUpdate(resource, PrismDisplayPropertyDefinition.valueOf(resourceScope.name() + "_COMMENT_UPDATED_ADVERT"));
    }

    public void updateFeesAndPayments(PrismScope resourceScope, Integer resourceId, AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        LocalDate baseline = new LocalDate();
        String currencyAtLocale = getCurrencyAtLocale(advert);

        FinancialDetailsDTO feeDTO = feesAndPaymentsDTO.getFee();
        updateFee(baseline, advert, currencyAtLocale, feeDTO);

        FinancialDetailsDTO payDTO = feesAndPaymentsDTO.getPay();
        updatePay(baseline, advert, currencyAtLocale, payDTO);

        advert.setLastCurrencyConversionDate(baseline);
        resourceService.executeUpdate(resource, PrismDisplayPropertyDefinition.valueOf(resourceScope.name() + "_COMMENT_UPDATED_FEE_AND_PAYMENT"));
    }

    @SuppressWarnings("unchecked")
    public void updateCategories(PrismScope resourceScope, Integer resourceId, AdvertCategoriesDTO categoriesDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        for (String propertyName : new String[] { "domain", "industry", "function", "competency", "theme", "institution", "programType" }) {
            String propertySetterName = "add" + WordUtils.capitalize(propertyName);
            List<Object> values = (List<Object>) PrismReflectionUtils.getProperty(categoriesDTO, pluralize(propertyName));

            if (values != null) {
                Collection<?> persistentMetadata = (Collection<?>) PrismReflectionUtils.getProperty(advert, pluralize(propertyName));
                persistentMetadata.clear();
                entityService.flush();

                boolean isInstitutionsProperty = propertyName.equals("institution");
                for (Object value : values) {
                    value = isInstitutionsProperty ? institutionService.getById((Integer) value) : value;
                    PrismReflectionUtils.invokeMethod(advert, propertySetterName, value);
                }
            }
        }

        resourceService.executeUpdate(resource, PrismDisplayPropertyDefinition.valueOf(resourceScope.name() + "_COMMENT_UPDATED_CATEGORY"));
    }

    public AdvertClosingDate createClosingDate(PrismScope resourceScope, Integer resourceId, AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        if (advert != null) {
            AdvertClosingDate advertClosingDate = new AdvertClosingDate().withAdvert(advert).withClosingDate(advertClosingDateDTO.getClosingDate())
                    .withStudyPlaces(advertClosingDateDTO.getStudyPlaces());
            advert.getClosingDates().add(advertClosingDate);
            entityService.flush();
            advert.setClosingDate(getNextAdvertClosingDate(advert));
            resourceService.executeUpdate(resource, PrismDisplayPropertyDefinition.valueOf(resourceScope.name() + "_COMMENT_CLOSING_DATE"));
            return advertClosingDate;
        }

        return null;
    }

    public void updateClosingDate(PrismScope resourceScope, Integer resourceId, Integer closingDateId, AdvertClosingDateDTO advertClosingDateDTO)
            throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        AdvertClosingDate advertClosingDate = getClosingDateById(closingDateId);
        if (advert.getId().equals(advertClosingDate.getAdvert().getId())) {
            advertClosingDate.setClosingDate(advertClosingDateDTO.getClosingDate());
            advertClosingDate.setStudyPlaces(advertClosingDateDTO.getStudyPlaces());
            entityService.flush();
            advert.setClosingDate(getNextAdvertClosingDate(advert));
            resourceService.executeUpdate(resource, PrismDisplayPropertyDefinition.valueOf(resourceScope.name() + "_COMMENT_CLOSING_DATE"));
        } else {
            throw new Error();
        }
    }

    public void deleteClosingDate(PrismScope resourceScope, Integer resourceId, Integer closingDateId) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        AdvertClosingDate advertClosingDate = getClosingDateById(closingDateId);
        if (advert.getId().equals(advertClosingDate.getAdvert().getId())) {
            advert.setClosingDate(null);
            entityService.flush();
            advert.getClosingDates().remove(advertClosingDate);
            advert.setClosingDate(getNextAdvertClosingDate(advert));
            resourceService.executeUpdate(resource, PrismDisplayPropertyDefinition.valueOf(resourceScope.name() + "_COMMENT_CLOSING_DATE"));
        } else {
            throw new Error();
        }
    }

    public List<Integer> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
        return advertDAO.getAdvertsWithElapsedClosingDates(baseline);
    }

    public void refreshClosingDate(Integer advertId, LocalDate baseline) {
        Advert advert = getById(advertId);
        advert.setClosingDate(getNextAdvertClosingDate(advert));
    }

    public void updateCurrencyConversion(Integer advertId) {
        Advert advert = getById(advertId);
        LocalDate baseline = new LocalDate();

        if (advert.hasConvertedFee()) {
            updateConvertedMonetaryValues(advert.getFee(), baseline);
        }

        if (advert.hasConvertedPay()) {
            updateConvertedMonetaryValues(advert.getPay(), baseline);
        }

        advert.setLastCurrencyConversionDate(baseline);
    }

    public List<Integer> getAdvertsWithElapsedCurrencyConversions(LocalDate baseline) {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return advertDAO.getAdvertsWithElapsedCurrencyConversions(baseline, activeProgramStates, activeProjectStates);
    }

    public InstitutionAddress createAddressCopy(InstitutionAddress address) {
        InstitutionAddress newAddress = new InstitutionAddress().withDomicile(address.getDomicile()).withInstitution(address.getInstitution())
                .withAddressLine1(address.getAddressLine1()).withAddressLine2(address.getAddressLine2()).withAddressTown(address.getAddressTown())
                .withAddressRegion(address.getAddressRegion()).withAddressCode(address.getAddressCode());

        GeographicLocation oldLocation = address.getLocation();
        if (oldLocation != null) {
            GeographicLocation newLocation = new GeographicLocation().withLocationX(oldLocation.getLocationX()).withLocationY(oldLocation.getLocationY())
                    .withLocationViewNeX(oldLocation.getLocationViewNeX()).withLocationViewNeY(oldLocation.getLocationViewNeY())
                    .withLocationViewSwX(oldLocation.getLocationViewSwX()).withLocationViewSwY(oldLocation.getLocationViewSwY());
            newAddress.setLocation(newLocation);
        }

        entityService.save(newAddress);
        return newAddress;
    }

    public List<String> getLocalizedTags(Institution institution, Class<? extends AdvertFilterCategory> clazz) {
        return advertDAO.getLocalizedTags(institution, clazz);
    }

    public List<String> getLocalizedThemes(Application application) {
        if (application.isProgramApplication()) {
            return advertDAO.getLocalizedProgramThemes(application.getProgram());
        } else {
            List<String> themes = advertDAO.getLocalizedProjectThemes(application.getProject());
            if (themes.isEmpty()) {
                return advertDAO.getLocalizedProgramThemes(application.getProgram());
            }
            return themes;
        }
    }

    public void setSequenceIdentifier(Advert advert, String prefix) {
        advert.setSequenceIdentifier(prefix + String.format("%010d", advert.getId()));
    }

    public SocialMetadataDTO getSocialMetadata(Advert advert) {
        Resource parentResource = advert.getResourceParent();
        return new SocialMetadataDTO().withAuthor(parentResource.getUser().getFullName()).withTitle(advert.getTitle()).withDescription(advert.getSummary())
                .withThumbnailUrl(resourceService.getSocialThumbnailUrl(parentResource)).withResourceUrl(resourceService.getSocialResourceUrl(parentResource))
                .withLocale(resourceService.getOperativeLocale(parentResource).toString());
    }

    public boolean getAcceptingApplications(List<PrismState> activeProgramStates, List<PrismState> activeProjectStates, Advert advert) {
        return (advert.isProgramAdvert() && activeProgramStates.contains(advert.getProgram().getState().getId()))
                || (advert.isProjectAdvert() && activeProjectStates.contains(advert.getProject().getState().getId()));
    }

    public AdvertStudyOptionInstance getFirstEnabledProgramStudyOptionInstance(Advert advert, StudyOption studyOption) {
        return advertDAO.getFirstEnabledAdvertStudyOptionInstance(advert, studyOption);
    }

    public List<AdvertStudyOption> getAdvertEnabledProgramStudyOptions(Advert advert) {
        return advertDAO.getEnabledAdvertStudyOptions(advert);
    }

    public AdvertStudyOption getEnabledAdvertStudyOption(Advert advert, StudyOption studyOption) {
        return advertDAO.getEnabledAdvertStudyOption(advert, studyOption);
    }

    public void disableAdvertProgramStudyOptions() {
        LocalDate baseline = new LocalDate();
        advertDAO.disableElapsedAdvertStudyOptions(baseline);
        advertDAO.disableElapsedAdvertStudyOptionInstances(baseline);
    }

    private String getCurrencyAtLocale(Advert advert) {
        InstitutionAddress localeAddress = advert.getAddress();
        localeAddress = localeAddress == null ? advert.getAddress() : localeAddress;
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

    private void updateConvertedMonetaryValues(AdvertFinancialDetail financialDetails, LocalDate baseline) {
        String currencySpecified = financialDetails.getCurrencySpecified();
        String currencyAtLocale = financialDetails.getCurrencyAtLocale();

        try {
            BigDecimal rate = getExchangeRate(currencySpecified, currencyAtLocale, baseline);

            BigDecimal minimumSpecified;
            BigDecimal maximumSpecified;
            BigDecimal minimumGenerated;
            BigDecimal maximumGenerated;

            PrismDurationUnit interval = financialDetails.getInterval();
            String intervalPrefixGenerated;

            if (interval == PrismDurationUnit.MONTH) {
                minimumSpecified = financialDetails.getMonthMinimumSpecified();
                maximumSpecified = financialDetails.getMonthMaximumSpecified();
                minimumGenerated = financialDetails.getYearMinimumSpecified();
                maximumGenerated = financialDetails.getYearMaximumSpecified();
                intervalPrefixGenerated = PrismDurationUnit.YEAR.name().toLowerCase();
            } else {
                minimumSpecified = financialDetails.getYearMinimumSpecified();
                maximumSpecified = financialDetails.getYearMaximumSpecified();
                minimumGenerated = financialDetails.getMonthMinimumSpecified();
                maximumGenerated = financialDetails.getMonthMaximumSpecified();
                intervalPrefixGenerated = PrismDurationUnit.MONTH.name().toLowerCase();
            }

            setConvertedMonetaryValues(financialDetails, interval.name().toLowerCase(), minimumSpecified, maximumSpecified, intervalPrefixGenerated,
                    minimumGenerated, maximumGenerated, rate);
        } catch (Exception e) {
            LOGGER.error("Unable to perform currency conversion", e);
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
        if (feeDTO == null) {
            advert.setFee(null);
            return;
        }
        if (advert.getFee() == null) {
            advert.setFee(new AdvertFinancialDetail());
        }
        updateFinancialDetails(advert.getFee(), feeDTO, currencyAtLocale, baseline);
    }

    private void updatePay(LocalDate baseline, Advert advert, String currencyAtLocale, FinancialDetailsDTO payDTO) throws Exception {
        if (payDTO == null) {
            advert.setPay(null);
            return;
        }
        if (advert.getPay() == null) {
            advert.setPay(new AdvertFinancialDetail());
        }
        updateFinancialDetails(advert.getPay(), payDTO, currencyAtLocale, baseline);
    }

    private void updateFinancialDetails(AdvertFinancialDetail financialDetails, FinancialDetailsDTO financialDetailsDTO, String currencyAtLocale,
            LocalDate baseline) throws Exception {
        PrismDurationUnit interval = financialDetailsDTO.getInterval();
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

        if (interval == MONTH) {
            intervalPrefixGenerated = YEAR.name().toLowerCase();
            minimumGenerated = minimumSpecified.multiply(new BigDecimal(12));
            maximumGenerated = maximumSpecified.multiply(new BigDecimal(12));
        } else {
            intervalPrefixGenerated = MONTH.name().toLowerCase();
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
                LOGGER.error("Problem performing currency conversion", e);
            }
        }
    }

    private AdvertClosingDate getNextAdvertClosingDate(Advert advert) {
        return advertDAO.getNextAdvertClosingDate(advert, new LocalDate());
    }

    private void copyStudyOptions(Advert advert, AdvertResourceDTO advertResourceDTO) {
        for (PrismStudyOption prismStudyOption : advertResourceDTO.getStudyOptions()) {
            StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, advert.getInstitution(), prismStudyOption.name());
            AdvertStudyOption advertStudyOption = new AdvertStudyOption().withStudyOption(studyOption).withApplicationStartDate(new LocalDate())
                    .withApplicationCloseDate(advert.getResourceParent().getEndDate()).withEnabled(true).withAdvert(advert);
            advert.getAdvertStudyOptions().add(advertStudyOption);
        }
    }

}
