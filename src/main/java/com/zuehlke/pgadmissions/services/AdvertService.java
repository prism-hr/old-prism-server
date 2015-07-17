package com.zuehlke.pgadmissions.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.TargetEntity;
import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.advert.*;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.json.ExchangeRateLookupResponseDTO;
import com.zuehlke.pgadmissions.mapping.AdvertMapper;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.PrismAdvertAttribute.getByValueClass;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.MONTH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

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
    private StateService stateService;

    @Inject
    private GeocodableLocationService geocodableLocationService;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private RestTemplate restTemplate;

    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }

    public AdvertClosingDate getClosingDateById(Integer id) {
        return entityService.getById(AdvertClosingDate.class, id);
    }

    public Advert getAdvert(PrismScope resourceScope, Integer resourceId) {
        return advertDAO.getAdvert(resourceScope, resourceId);
    }

    public List<Advert> getAdverts(OpportunitiesQueryDTO queryDTO, List<PrismState> programStates, List<PrismState> projectStates) {
        programStates = queryDTO.getPrograms() == null ? programStates : stateService.getProgramStates();
        projectStates = queryDTO.getProjects() == null ? projectStates : stateService.getProjectStates();

        if (queryDTO.isResourceAction()) {
            Resource resource = resourceService.getById(queryDTO.getActionId().getScope(), queryDTO.getResourceId());
            if (resource.getInstitution() != null) {
                queryDTO.setInstitutions(new Integer[] { resource.getInstitution().getId() });
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

    public Advert createAdvert(Resource parentResource, AdvertDTO advertDTO) throws Exception {
        Advert advert = new Advert();
        updateAdvert(parentResource, advert, advertDTO);
        entityService.save(advert);
        return advert;
    }

    public void updateAdvert(Resource parentResource, Advert advert, AdvertDTO advertDTO) throws Exception {
        advert.setSummary(advertDTO.getSummary());
        advert.setApplyHomepage(advertDTO.getApplyHomepage());
        advert.setTelephone(advertDTO.getTelephone());

        AddressAdvert address = advert.getAddress();
        AddressAdvertDTO addressDTO = advertDTO.getAddress();
        if (addressDTO != null) {
            updateAddress(advert, addressDTO);
        } else if (address == null) {
            if (ResourceParent.class.isAssignableFrom(parentResource.getClass())) {
                address = getResourceAddress(parentResource);
                addressDTO = advertMapper.getAddressDTO(address);
                updateAddress(advert, addressDTO);
            } else {
                throw new Error();
            }
        }

        AdvertCategoriesDTO categoriesDTO = advertDTO.getCategories();
        if (categoriesDTO != null) {
            updateCategories(advert, categoriesDTO);
        }

        AdvertTargetsDTO targetsDTO = advertDTO.getTargets();
        if (targetsDTO != null) {
            updateTargets(advert, targetsDTO);
        }

    }

    public void updateDetail(PrismScope resourceScope, Integer resourceId, AdvertDetailsDTO advertDetailsDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        advert.setDescription(advertDetailsDTO.getDescription());
        advert.setHomepage(advertDetailsDTO.getHomepage());
        updateAddress(advert, advertDetailsDTO.getAddress());
        executeUpdate(resource, "COMMENT_UPDATED_ADVERT");
    }

    public void updateFeesAndPayments(PrismScope resourceScope, Integer resourceId, AdvertFinancialDetailsDTO feesAndPaymentsDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        LocalDate baseline = new LocalDate();
        String currencyAtLocale = getCurrencyAtLocale(advert);

        AdvertFinancialDetailDTO feeDTO = feesAndPaymentsDTO.getFee();
        updateFee(baseline, advert, currencyAtLocale, feeDTO);

        AdvertFinancialDetailDTO payDTO = feesAndPaymentsDTO.getPay();
        updatePay(baseline, advert, currencyAtLocale, payDTO);

        advert.setLastCurrencyConversionDate(baseline);
        executeUpdate(resource, "COMMENT_UPDATED_FEE_AND_PAYMENT");
    }

    public void updateFeesAndPayments(Advert advert, String newCurrency) throws Exception {
        Resource resource = advert.getResource();
        AdvertFinancialDetailDTO feeDTO = getFinancialDetailDTO(advert.getFee(), newCurrency);
        AdvertFinancialDetailDTO payDTO = getFinancialDetailDTO(advert.getPay(), newCurrency);
        updateFeesAndPayments(resource.getResourceScope(), resource.getId(), new AdvertFinancialDetailsDTO().withFee(feeDTO).withPay(payDTO));
    }

    public void updateCategories(PrismScope resourceScope, Integer resourceId, AdvertCategoriesDTO categoriesDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateCategories(advert, categoriesDTO);
        executeUpdate(resource, "COMMENT_UPDATED_CATEGORY");
    }

    private void updateCategories(Advert advert, AdvertCategoriesDTO categoriesDTO) throws Exception {
        AdvertCategories categories = advert.getCategories();
        if (categories == null) {
            categories = new AdvertCategories();
            advert.setCategories(categories);
        }

        Class<?> valueClass = null;
        Class<? extends AdvertAttribute<?>> categoryClass = null;
        for (Object category : categoriesDTO.getCategories()) {
            Class<?> newValueClass = category.getClass();
            if (valueClass == null || !newValueClass.equals(valueClass)) {
                valueClass = newValueClass;
                clearAdvertAttributes(categories, valueClass);
                categoryClass = getByValueClass(valueClass).getAttributeClass();
            }

            AdvertAttribute<?> entityCategory = createAdvertAttribute(advert, categoryClass, category);
            entityService.getOrCreate(entityCategory);
            categories.storeAttribute(entityCategory);
        }
    }

    public void updateTargets(PrismScope resourceScope, Integer resourceId, AdvertTargetsDTO targetsDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateTargets(advert, targetsDTO);
        executeUpdate(resource, "COMMENT_UPDATED_TARGET");
    }

    private void updateTargets(Advert advert, AdvertTargetsDTO targetsDTO) throws Error, Exception {
        AdvertTargets targets = advert.getTargets();
        if (targets == null) {
            targets = new AdvertTargets();
            advert.setTargets(targets);
        }

        Class<?> valueClass = null;
        Class<? extends AdvertAttribute<?>> targetClass = null;
        for (AdvertTargetDTO target : targetsDTO.getTargets()) {
            Class<?> newValueClass = target.getClass();
            if (valueClass == null || !newValueClass.equals(valueClass)) {
                valueClass = newValueClass;
                clearAdvertAttributes(targets, valueClass);
                targetClass = getByValueClass(valueClass).getAttributeClass();
            }

            TargetEntity value;
            Integer valueId = target.getValue();
            if (valueId == null && target.getClass().equals(AdvertCompetenceDTO.class)) {
                AdvertCompetenceDTO competenceDTO = (AdvertCompetenceDTO) target;
                value = getOrCreateCompetence(competenceDTO);
            } else if (valueId != null) {
                value = (TargetEntity) entityService.getById(valueClass, target.getValue());
            } else {
                throw new Error();
            }

            AdvertTarget<?> entityTarget = createAdvertTarget(advert, targetClass, value, target.getImportance());
            targets.storeAttribute(entityTarget);
        }
    }

    public AdvertClosingDate createClosingDate(PrismScope resourceScope, Integer resourceId, AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        if (advert != null) {
            AdvertClosingDate advertClosingDate = createAdvertClosingDate(advert, advertClosingDateDTO);
            entityService.getOrCreate(advertClosingDate);
            advert.setClosingDate(getNextAdvertClosingDate(advert));
            executeUpdate(resource, "COMMENT_UPDATED_CLOSING_DATE");
            return advertClosingDate;
        }

        return null;
    }

    public void updateClosingDate(PrismScope resourceScope, Integer resourceId, Integer closingDateId, AdvertClosingDateDTO advertClosingDateDTO)
            throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        AdvertClosingDate persistentAdvertClosingDate = getClosingDateById(closingDateId);
        if (advert.getId().equals(persistentAdvertClosingDate.getAdvert().getId())) {
            AdvertClosingDate transientAdvertClosingDate = createAdvertClosingDate(advert, advertClosingDateDTO);
            AdvertClosingDate duplicateAdvertClosingDate = entityService.getDuplicateEntity(transientAdvertClosingDate);
            if (!duplicateAdvertClosingDate.getId().equals(persistentAdvertClosingDate.getId())) {
                entityService.delete(persistentAdvertClosingDate);
            } else {
                persistentAdvertClosingDate.setClosingDate(advertClosingDateDTO.getClosingDate());
            }
            advert.setClosingDate(getNextAdvertClosingDate(advert));
            executeUpdate(resource, "COMMENT_UPDATED_CLOSING_DATE");
        } else {
            throw new Error();
        }
    }

    public void deleteClosingDate(PrismScope resourceScope, Integer resourceId, Integer closingDateId) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        AdvertClosingDate advertClosingDate = getClosingDateById(closingDateId);
        if (advert.getId().equals(advertClosingDate.getAdvert().getId())) {
            AdvertClosingDate currentAdvertClosingDate = advert.getClosingDate();
            if (currentAdvertClosingDate != null && advertClosingDate.getId().equals(currentAdvertClosingDate.getId())) {
                advert.setClosingDate(null);
            }
            entityService.delete(advertClosingDate);
            advert.setClosingDate(getNextAdvertClosingDate(advert));
            executeUpdate(resource, "COMMENT_UPDATED_CLOSING_DATE");
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

    public Set<String> getAvailableAdvertThemes(Advert advert, Set<String> themes) {
        return advertDAO.getAvailableAdvertThemes(advert, themes);
    }

    public void setSequenceIdentifier(Advert advert, String prefix) {
        advert.setSequenceIdentifier(prefix + String.format("%010d", advert.getId()));
    }

    public List<Advert> getAdvertsWithFinancialDetails(Institution institution) {
        return advertDAO.getAdvertsWithFinancialDetails(institution);
    }

    public AdvertCategories getAdvertCategories(Advert advert) {
        AdvertCategories categories = advert.getCategories();
        if (categories == null) {
            Resource resourceParent = advert.getResource().getParentResource();
            if (ResourceParent.class.isAssignableFrom(resourceParent.getClass())) {
                return getAdvertCategories(resourceParent.getAdvert());
            }
            return null;
        }
        return categories;
    }

    public AdvertTargets getAdvertTargets(Advert advert) {
        AdvertTargets targets = advert.getTargets();
        if (targets == null) {
            Resource resourceParent = advert.getResource().getParentResource();
            if (ResourceParent.class.isAssignableFrom(resourceParent.getClass())) {
                return getAdvertTargets(resourceParent.getAdvert());
            }
            return null;
        }
        return targets;
    }

    public List<String> getAdvertThemes(Advert advert) {
        List<String> themes = Lists.newLinkedList();
        AdvertCategories categories = getAdvertCategories(advert);
        for (AdvertTheme theme : categories.getThemes()) {
            themes.add(theme.getValue());
        }
        return themes;
    }

    public List<ImportedAdvertDomicile> getAdvertDomiciles() {
        return advertDAO.getAdvertDomiciles();
    }

    public Integer getBackgroundImage(Advert advert) {
        Document backgroundImage = advert.getBackgroundImage();
        if (backgroundImage == null) {
            Resource parentResource = advert.getResource().getParentResource();
            if (ResourceParent.class.isAssignableFrom(parentResource.getClass())) {
                return getBackgroundImage(parentResource.getAdvert());
            }
            return null;
        }
        return backgroundImage.getId();
    }

    private String getCurrencyAtLocale(Advert advert) {
        AddressAdvert addressAtLocale = advert.getAddress();
        addressAtLocale = addressAtLocale == null ? advert.getResource().getInstitution().getAdvert().getAddress() : addressAtLocale;
        return addressAtLocale.getDomicile().getCurrency();
    }

    private void setMonetaryValues(AdvertFinancialDetail financialDetails, String intervalPrefixSpecified, BigDecimal minimumSpecified,
            BigDecimal maximumSpecified, String intervalPrefixGenerated, BigDecimal minimumGenerated, BigDecimal maximumGenerated, String context) {
        try {
            PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixSpecified + "Minimum" + context, minimumSpecified);
            PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixSpecified + "Maximum" + context, maximumSpecified);
            PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixGenerated + "Minimum" + context, minimumGenerated);
            PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixGenerated + "Maximum" + context, maximumGenerated);
        } catch (Exception e) {
            throw new Error(e);
        }
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

    private BigDecimal getExchangeRate(String currencySpecified, String currencyConverted, LocalDate baseline) {
        removeExpiredExchangeRates(baseline);

        String pair = currencySpecified + currencyConverted;
        HashMap<String, BigDecimal> todaysRates = exchangeRates.get(baseline);

        if (todaysRates != null) {
            BigDecimal todaysRate = todaysRates.get(pair);
            if (todaysRate != null) {
                return todaysRate;
            }
        }

        BigDecimal todaysRate;
        try {
            String query = URLEncoder.encode("select Rate from " + yahooExchangeRateApiTable + " where pair = \"" + pair + "\"", "UTF-8");
            URI request = new DefaultResourceLoader().getResource(
                    yahooExchangeRateApiUri + "?q=" + query + "&env=" + URLEncoder.encode(yahooExchangeRateApiSchema, "UTF-8") + "&format=json").getURI();
            ExchangeRateLookupResponseDTO response = restTemplate.getForObject(request, ExchangeRateLookupResponseDTO.class);

            todaysRate = response.getQuery().getResults().getRate().getRate();

            if (todaysRates == null) {
                todaysRates = new HashMap<>();
                todaysRates.put(pair, todaysRate);
                exchangeRates.put(baseline, todaysRates);
            } else {
                todaysRates.put(pair, todaysRate);
            }
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }

        return todaysRate;
    }

    private void removeExpiredExchangeRates(LocalDate baseline) {
        exchangeRates.keySet().stream()
                .filter(day -> day.isBefore(baseline))
                .forEach(exchangeRates::remove);
    }

    private void updateFee(LocalDate baseline, Advert advert, String currencyAtLocale, AdvertFinancialDetailDTO feeDTO) {
        if (feeDTO == null) {
            advert.setFee(null);
            return;
        }
        if (advert.getFee() == null) {
            advert.setFee(new AdvertFinancialDetail());
        }
        updateFinancialDetails(advert.getFee(), feeDTO, currencyAtLocale, baseline);
    }

    private void updatePay(LocalDate baseline, Advert advert, String currencyAtLocale, AdvertFinancialDetailDTO payDTO) {
        if (payDTO == null) {
            advert.setPay(null);
            return;
        }
        if (advert.getPay() == null) {
            advert.setPay(new AdvertFinancialDetail());
        }
        updateFinancialDetails(advert.getPay(), payDTO, currencyAtLocale, baseline);
    }

    private void updateFinancialDetails(AdvertFinancialDetail financialDetails, AdvertFinancialDetailDTO financialDetailsDTO, String currencyAtLocale,
            LocalDate baseline) {
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

    public AdvertFinancialDetailDTO getFinancialDetailDTO(AdvertFinancialDetail detail, String newCurrency) {
        if (detail != null) {
            AdvertFinancialDetailDTO detailDTO = new AdvertFinancialDetailDTO();
            detailDTO.setCurrency(newCurrency);

            PrismDurationUnit interval = detail.getInterval();
            String intervalPrefix = interval.name().toLowerCase();
            detailDTO.setInterval(interval);

            String oldCurrency = detail.getCurrencySpecified();

            BigDecimal exchangeRate = getExchangeRate(oldCurrency, newCurrency, new LocalDate());

            BigDecimal minimumSpecified = (BigDecimal) getProperty(detail, intervalPrefix + "MinimumSpecified");
            BigDecimal maximumSpecified = (BigDecimal) getProperty(detail, intervalPrefix + "MaximumSpecified");

            setProperty(detailDTO, "minimum", minimumSpecified.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));
            setProperty(detailDTO, "maximum", maximumSpecified.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));

            return detailDTO;
        }
        return null;
    }

    private AdvertClosingDate getNextAdvertClosingDate(Advert advert) {
        return advertDAO.getNextAdvertClosingDate(advert, new LocalDate());
    }

    private Comment executeUpdate(ResourceParent resource, String message) throws Exception {
        return resourceService.executeUpdate(resource, PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_" + message));
    }

    private AddressAdvert createAddress(AddressAdvertDTO addressDTO) {
        AddressAdvert address = new AddressAdvert();
        updateAddress(addressDTO, address);
        return address;
    }

    private void updateAddress(Advert advert, AddressAdvertDTO addressDTO) {
        AddressAdvert address = advert.getAddress();
        if (address == null) {
            address = createAddress(addressDTO);
            entityService.save(address);
            advert.setAddress(address);
        } else {
            updateAddress(addressDTO, address);
        }
        geocodableLocationService.setLocation(addressDTO.getGoogleId(), advert.getTitle(), address);
    }

    private void updateAddress(AddressAdvertDTO addressDTO, AddressAdvert address) {
        address.setDomicile(entityService.getById(ImportedAdvertDomicile.class, addressDTO.getDomicile()));
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressRegion(addressDTO.getAddressRegion());
        address.setAddressCode(addressDTO.getAddressCode());
        address.setGoogleId(addressDTO.getGoogleId());
    }

    private AddressAdvert getResourceAddress(Resource resource) {
        Advert advert = resource.getAdvert();
        if (advert == null) {
            return null;
        }

        AddressAdvert address = advert.getAddress();
        if (address == null) {
            Resource parentResource = resource.getParentResource();
            if (parentResource.sameAs(resource)) {
                return null;
            }
            getResourceAddress(resource.getParentResource());
        }

        return address;
    }

    private AdvertTarget<?> createAdvertTarget(Advert advert, Class<? extends AdvertAttribute<?>> attributeClass, Object value, BigDecimal importance) {
        AdvertTarget<?> entityTarget = (AdvertTarget<?>) createAdvertAttribute(advert, attributeClass, value);
        entityTarget.setImportance(importance);
        return entityTarget;
    }

    private AdvertAttribute<?> createAdvertAttribute(Advert advert, Class<? extends AdvertAttribute<?>> attributeClass, Object value) {
        AdvertAttribute<?> entityAttribute = BeanUtils.instantiate(attributeClass);
        entityAttribute.setAdvert(advert);
        setProperty(entityAttribute, "value", value);
        return entityAttribute;
    }

    private AdvertClosingDate createAdvertClosingDate(Advert advert, AdvertClosingDateDTO advertClosingDateDTO) {
        AdvertClosingDate advertClosingDate = new AdvertClosingDate();
        advertClosingDate.setAdvert(advert);
        advertClosingDate.setClosingDate(advertClosingDateDTO.getClosingDate());
        return advertClosingDate;
    }

    private Competence getOrCreateCompetence(AdvertCompetenceDTO competenceDTO) {
        Competence transientCompetence = new Competence().withTitle(competenceDTO.getTitle()).withDescription(competenceDTO.getDescription());
        Competence persistentCompetence = entityService.getDuplicateEntity(transientCompetence);
        if (persistentCompetence == null) {
            entityService.save(transientCompetence);
            return transientCompetence;
        } else {
            return persistentCompetence;
        }
    }

    private void clearAdvertAttributes(AdvertAttributes attributes, Class<?> valueClass) {
        attributes.clearAttributes(valueClass);
        entityService.flush();
    }

}
