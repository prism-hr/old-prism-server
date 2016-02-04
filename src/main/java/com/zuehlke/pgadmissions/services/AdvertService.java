package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.MONTH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.SPONSOR_RESOURCE;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static com.zuehlke.pgadmissions.utils.WordUtils.pluralize;
import static org.joda.time.LocalDate.now;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.dozer.Mapper;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFilterCategory;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentSponsorship;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.json.ExchangeRateLookupResponseDTO;
import com.zuehlke.pgadmissions.exceptions.PrismForbiddenException;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertCategoriesDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertClosingDateDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertFeesAndPaymentsDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertSponsorshipDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.FinancialDetailsDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;
import com.zuehlke.pgadmissions.services.helpers.AdvertToRepresentationFunction;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;
import com.zuehlke.pgadmissions.utils.ToPropertyFunction;

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
	private ApplicationService applicationService;

	@Inject
	private CommentService commentService;

	@Inject
	private DepartmentService departmentService;

	@Inject
	private EntityService entityService;

	@Inject
	private ResourceService resourceService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private StateService stateService;

	@Inject
	private GeocodableLocationService geocodableLocationService;

	@Inject
	private Mapper mapper;

	@Inject
	private RestTemplate restTemplate;

	private AdvertToRepresentationFunction advertToRepresentationFunction = new AdvertToRepresentationFunction();

	public Advert getById(Integer id) {
		return entityService.getById(Advert.class, id);
	}

	public AdvertClosingDate getClosingDateById(Integer id) {
		return entityService.getById(AdvertClosingDate.class, id);
	}

	public Advert getAdvert(PrismScope resourceScope, Integer resourceId) {
		return advertDAO.getAdvert(resourceScope, resourceId);
	}

	public List<Advert> getAdverts(OpportunitiesQueryDTO queryDTO, List<PrismState> programStates,
			List<PrismState> projectStates) {
		programStates = queryDTO.getPrograms() == null ? programStates : stateService.getProgramStates();
		projectStates = queryDTO.getProjects() == null ? projectStates : stateService.getProjectStates();

		if (queryDTO.isResourceAction()) {
			Resource resource = resourceService.getById(queryDTO.getActionId().getScope(), queryDTO.getResourceId());
			if (resource.getInstitution() != null) {
				queryDTO.setInstitutions(new Integer[] { resource.getInstitution().getId() });
			}
		}

		Integer rejectedApplicant = queryDTO.getRejectedApplicant();
		if (rejectedApplicant != null) {
			Institution institution = null;
			Integer[] departmentIds = queryDTO.getDepartments();
			if (!ArrayUtils.isEmpty(departmentIds)) {
				institution = departmentService.getById(departmentIds[0]).getInstitution();
			} else {
				institution = institutionService.getById(queryDTO.getInstitutions()[0]);
			}

			LocalDate baseline = now();
			String currentApplicationYear = institutionService.getBusinessYear(institution, baseline.getYear(),
					baseline.getMonthOfYear());

			queryDTO.setExclusions(advertDAO.getExcludedAdvertsForApplicationRejectionRecommendation(rejectedApplicant,
					currentApplicationYear, queryDTO.getRejectedApplication()));
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
		List<Integer> advertsRecentlyAppliedFor = advertDAO.getAdvertsRecentlyAppliedFor(user,
				new LocalDate().minusYears(1));
		return advertDAO.getRecommendedAdverts(user, activeProgramStates, activeProjectStates,
				advertsRecentlyAppliedFor);
	}

	public Advert createAdvert(Resource parentResource, AdvertDTO advertDTO) {
		Advert advert = new Advert();
		updateAdvert(parentResource, advert, advertDTO);
		entityService.save(advert);
		return advert;
	}

	public void updateAdvert(Resource parentResource, Advert advert, AdvertDTO advertDTO) {
		if (BooleanUtils.isFalse(advert.getImported())) {
			advert.setTitle(advertDTO.getTitle());
		}

		advert.setSummary(advertDTO.getSummary());
		advert.setApplyHomepage(advertDTO.getApplyHomepage());
		advert.setTelephone(advertDTO.getTelephone());

		InstitutionAddress address = advert.getAddress();
		InstitutionAddressDTO addressDTO = advertDTO.getAddress();
		if (addressDTO != null) {
			updateAddress(advert, addressDTO);
		} else if (address == null) {
			address = getResourceAddress(parentResource);
			addressDTO = mapper.map(address, InstitutionAddressDTO.class);
			updateAddress(advert, addressDTO);
		}

		advert.setSponsorshipTarget(advertDTO.getSponsorshipRequired());
	}

	public void updateDetail(PrismScope resourceScope, Integer resourceId, AdvertDetailsDTO advertDetailsDTO)
			throws Exception {
		ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
		Advert advert = resource.getAdvert();
		advert.setDescription(advertDetailsDTO.getDescription());
		advert.setHomepage(advertDetailsDTO.getHomepage());
		updateAddress(advert, advertDetailsDTO.getAddress());
		executeUpdate(resource, "COMMENT_UPDATED_ADVERT");
	}

	public void updateFeesAndPayments(PrismScope resourceScope, Integer resourceId,
			AdvertFeesAndPaymentsDTO feesAndPaymentsDTO) throws Exception {
		ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
		Advert advert = resource.getAdvert();

		LocalDate baseline = new LocalDate();
		String currencyAtLocale = getCurrencyAtLocale(advert);

		FinancialDetailsDTO feeDTO = feesAndPaymentsDTO.getFee();
		updateFee(baseline, advert, currencyAtLocale, feeDTO);

		FinancialDetailsDTO payDTO = feesAndPaymentsDTO.getPay();
		updatePay(baseline, advert, currencyAtLocale, payDTO);

		advert.setLastCurrencyConversionDate(baseline);
		executeUpdate(resource, "COMMENT_UPDATED_FEE_AND_PAYMENT");
	}

	public void updateFeesAndPayments(Advert advert, String newCurrency) throws Exception {
		Resource resource = advert.getResource();
		FinancialDetailsDTO feeDTO = getFinancialDetailDTO(advert.getFee(), newCurrency);
		FinancialDetailsDTO payDTO = getFinancialDetailDTO(advert.getPay(), newCurrency);
		updateFeesAndPayments(resource.getResourceScope(), resource.getId(),
				new AdvertFeesAndPaymentsDTO().withFee(feeDTO).withPay(payDTO));
	}

	@SuppressWarnings("unchecked")
	public void updateCategories(PrismScope resourceScope, Integer resourceId, AdvertCategoriesDTO categoriesDTO)
			throws Exception {
		ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
		Advert advert = resource.getAdvert();

		for (String propertyName : new String[] { "domain", "industry", "function", "competency", "theme" }) {
			String propertySetterName = "add" + WordUtils.capitalize(propertyName);
			List<Object> values = (List<Object>) PrismReflectionUtils.getProperty(categoriesDTO,
					pluralize(propertyName));

			if (values != null) {
				Collection<?> persistentMetadata = (Collection<?>) PrismReflectionUtils.getProperty(advert,
						pluralize(propertyName));
				persistentMetadata.clear();
				entityService.flush();

				boolean isInstitutionsProperty = propertyName.equals("institution");
				for (Object value : values) {
					value = isInstitutionsProperty ? institutionService.getById((Integer) value) : value;
					PrismReflectionUtils.invokeMethod(advert, propertySetterName, value);
				}
			}
		}

		executeUpdate(resource, "COMMENT_UPDATED_CATEGORY");
	}

	public AdvertClosingDate createClosingDate(PrismScope resourceScope, Integer resourceId,
			AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
		ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
		Advert advert = resource.getAdvert();

		if (advert != null) {
			AdvertClosingDate advertClosingDate = new AdvertClosingDate().withAdvert(advert)
					.withClosingDate(advertClosingDateDTO.getClosingDate())
					.withStudyPlaces(advertClosingDateDTO.getStudyPlaces());
			entityService.getOrCreate(advertClosingDate);
			advert.setClosingDate(getNextAdvertClosingDate(advert));
			executeUpdate(resource, "COMMENT_UPDATED_CLOSING_DATE");
			return advertClosingDate;
		}

		return null;
	}

	public void updateClosingDate(PrismScope resourceScope, Integer resourceId, Integer closingDateId,
			AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
		ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
		Advert advert = resource.getAdvert();

		AdvertClosingDate persistentAdvertClosingDate = getClosingDateById(closingDateId);
		if (advert.getId().equals(persistentAdvertClosingDate.getAdvert().getId())) {
			AdvertClosingDate duplicateAdvertClosingDate = entityService.getDuplicateEntity(
					new AdvertClosingDate().withAdvert(advert).withClosingDate(advertClosingDateDTO.getClosingDate())
							.withStudyPlaces(advertClosingDateDTO.getStudyPlaces()));
			if (!duplicateAdvertClosingDate.getId().equals(persistentAdvertClosingDate.getId())) {
				entityService.delete(persistentAdvertClosingDate);
			} else {
				persistentAdvertClosingDate.setClosingDate(advertClosingDateDTO.getClosingDate());
				persistentAdvertClosingDate.setStudyPlaces(advertClosingDateDTO.getStudyPlaces());
			}
			advert.setClosingDate(getNextAdvertClosingDate(advert));
			executeUpdate(resource, "COMMENT_UPDATED_CLOSING_DATE");
		} else {
			throw new Error();
		}
	}

	public void deleteClosingDate(PrismScope resourceScope, Integer resourceId, Integer closingDateId)
			throws Exception {
		ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
		Advert advert = resource.getAdvert();

		AdvertClosingDate advertClosingDate = getClosingDateById(closingDateId);
		if (advert.getId().equals(advertClosingDate.getAdvert().getId())) {
			AdvertClosingDate currentAdvertClosingDate = advert.getClosingDate();
			if (currentAdvertClosingDate != null
					&& advertClosingDate.getId().equals(currentAdvertClosingDate.getId())) {
				advert.setClosingDate(null);
			}
			entityService.delete(advertClosingDate);
			entityService.flush();
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

	public InstitutionAddress createAddressCopy(InstitutionAddress address) {
		InstitutionAddress newAddress = new InstitutionAddress().withDomicile(address.getDomicile())
				.withAddressLine1(address.getAddressLine1()).withAddressLine2(address.getAddressLine2())
				.withAddressTown(address.getAddressTown()).withAddressRegion(address.getAddressRegion())
				.withAddressCode(address.getAddressCode());

		GeographicLocation oldLocation = address.getLocation();
		if (oldLocation != null) {
			GeographicLocation newLocation = new GeographicLocation().withLocationX(oldLocation.getLocationX())
					.withLocationY(oldLocation.getLocationY());
			newAddress.setLocation(newLocation);
		}

		entityService.save(newAddress);
		return newAddress;
	}

	public List<String> getAdvertTags(Institution institution, Class<? extends AdvertFilterCategory> clazz) {
		return advertDAO.getAdvertTags(institution, clazz);
	}

	public List<String> getAdvertThemes(Application application) {
		for (ResourceParent resource : new ResourceParent[] { application.getProject(), application.getProgram(),
				application.getInstitution() }) {
			if (resource != null) {
				List<String> themes = advertDAO.getAdvertThemes(resource.getAdvert());
				if (!themes.isEmpty()) {
					return themes;
				}
			}
		}
		return Lists.newArrayList();
	}

	public void setSequenceIdentifier(Advert advert, String prefix) {
		advert.setSequenceIdentifier(prefix + String.format("%010d", advert.getId()));
	}

	public void updateSponsorship(PrismScope resourceScope, Integer resourceId, AdvertSponsorshipDTO sponsorshipDTO)
			throws Exception {
		ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
		Advert advert = resource.getAdvert();

		advert.setSponsorshipPurpose(sponsorshipDTO.getSponsorshipPurpose());
		advert.setSponsorshipTarget(sponsorshipDTO.getSponsorshipTarget());

		executeUpdate(resource, "COMMENT_UPDATED_SPONSORSHIP_TARGET");
	}

	public void updateSponsorship(Advert advert, String oldCurrency, String newCurrency) {
		BigDecimal exchangeRate = getExchangeRate(oldCurrency, newCurrency, new LocalDate());

		BigDecimal sponsorshipTarget = advert.getSponsorshipTarget();
		if (!(sponsorshipTarget == null || sponsorshipTarget.compareTo(new BigDecimal(0.00)) == 0)) {
			advert.setSponsorshipTarget(sponsorshipTarget.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));
		}

		BigDecimal sponsorshipSecured = advert.getSponsorshipSecured();
		if (!(sponsorshipSecured == null || sponsorshipSecured.compareTo(new BigDecimal(0.00)) == 0)) {
			advert.setSponsorshipSecured(sponsorshipSecured.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));
		}
	}

	public void rejectSponsorship(PrismScope resourceScope, Integer resourceId, Integer commentId) throws Exception {
		ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
		Advert advert = resource.getAdvert();
		Comment comment = commentService.getById(commentId);

		Preconditions.checkState(comment.getResource().getId().equals(resource.getId()));
		CommentSponsorship sponsorship = comment.getSponsorship();
		if (sponsorship == null || comment.getAction().getActionCategory() != SPONSOR_RESOURCE
				|| sponsorship.getRejection() != null) {
			throw new PrismForbiddenException("Cannot decline given sponsorship");
		}

		advert.setSponsorshipSecured(advert.getSponsorshipSecured().subtract(sponsorship.getAmountConverted()));
		Comment rejection = executeUpdate(resource, "COMMENT_REJECTED_SPONSORSHIP");
		sponsorship.setRejection(rejection);
	}

	public void synchronizeSponsorship(ResourceParent resource, Comment comment) {
		Advert advert = resource.getAdvert();

		BigDecimal advertRequired = advert.getSponsorshipTarget();
		BigDecimal advertSecured = advert.getSponsorshipSecured();

		CommentSponsorship sponsorship = comment.getSponsorship();
		String currencySpecified = sponsorship.getCurrencySpecified();
		String currencyConverted = sponsorship.getCurrencyConverted();

		BigDecimal sponsorshipConverted = sponsorship.getAmountSpecified();
		if (!currencySpecified.equals(currencyConverted)) {
			BigDecimal exchangeRate = getExchangeRate(currencySpecified, currencyConverted, new LocalDate());
			sponsorshipConverted = sponsorshipConverted.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
		}

		sponsorship.setAmountConverted(sponsorshipConverted);
		advertSecured = advertSecured == null ? sponsorshipConverted : advertSecured.add(sponsorshipConverted);

		advert.setSponsorshipSecured(advertSecured);
		if (advertRequired != null && advertSecured != null) {
			sponsorship.setTargetFulfilled(advertRequired.compareTo(advertSecured) >= 0);
		}
	}

	public List<AdvertRepresentation> getRecommendedAdverts(Integer applicationId) {
		Application application = applicationService.getById(applicationId);
		List<AdvertRecommendationDTO> advertRecommendations = getRecommendedAdverts(application.getUser());
		return Lists.transform(advertRecommendations, Functions.compose(advertToRepresentationFunction,
				new ToPropertyFunction<AdvertRecommendationDTO, Advert>("advert")));
	}

	public InstitutionAddress getAddressCopy(InstitutionAddress address) {
		return new InstitutionAddress().withDomicile(address.getDomicile()).withAddressLine1(address.getAddressLine1())
				.withAddressLine2(address.getAddressLine2()).withAddressTown(address.getAddressTown())
				.withAddressRegion(address.getAddressRegion()).withAddressCode(address.getAddressCode())
				.withGoogleId(address.getGoogleId()).withLocation(address.getLocation());
	}

	public List<Advert> getAdvertsWithFeesAndPays(Institution institution) {
		return advertDAO.getAdvertsWithFeesAndPays(institution);
	}

	public List<Advert> getAdvertsWithSponsorship(Institution institution) {
		return advertDAO.getAdvertsWithSponsorship(institution);
	}

	private String getCurrencyAtLocale(Advert advert) {
		InstitutionAddress addressAtLocale = advert.getAddress();
		addressAtLocale = addressAtLocale == null ? advert.getResource().getInstitution().getAdvert().getAddress()
				: addressAtLocale;
		return addressAtLocale.getDomicile().getCurrency();
	}

	private void setMonetaryValues(AdvertFinancialDetail financialDetails, String intervalPrefixSpecified,
			BigDecimal minimumSpecified, BigDecimal maximumSpecified, String intervalPrefixGenerated,
			BigDecimal minimumGenerated, BigDecimal maximumGenerated, String context) {
		try {
			PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixSpecified + "Minimum" + context,
					minimumSpecified);
			PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixSpecified + "Maximum" + context,
					maximumSpecified);
			PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixGenerated + "Minimum" + context,
					minimumGenerated);
			PropertyUtils.setSimpleProperty(financialDetails, intervalPrefixGenerated + "Maximum" + context,
					maximumGenerated);
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	private void setConvertedMonetaryValues(AdvertFinancialDetail financialDetails, String intervalPrefixSpecified,
			BigDecimal minimumSpecified, BigDecimal maximumSpecified, String intervalPrefixGenerated,
			BigDecimal minimumGenerated, BigDecimal maximumGenerated, BigDecimal rate)
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

		setMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified, maximumSpecified,
				intervalPrefixGenerated, minimumGenerated, maximumGenerated, "AtLocale");
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

			setConvertedMonetaryValues(financialDetails, interval.name().toLowerCase(), minimumSpecified,
					maximumSpecified, intervalPrefixGenerated, minimumGenerated, maximumGenerated, rate);
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
			String query = URLEncoder.encode(
					"select Rate from " + yahooExchangeRateApiTable + " where pair = \"" + pair + "\"", "UTF-8");
			URI request = new DefaultResourceLoader().getResource(yahooExchangeRateApiUri + "?q=" + query + "&env="
					+ URLEncoder.encode(yahooExchangeRateApiSchema, "UTF-8") + "&format=json").getURI();
			ExchangeRateLookupResponseDTO response = restTemplate.getForObject(request,
					ExchangeRateLookupResponseDTO.class);

			todaysRate = response.getQuery().getResults().getRate().getRate();

			if (todaysRates == null) {
				todaysRates = new HashMap<String, BigDecimal>();
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
		for (LocalDate day : exchangeRates.keySet()) {
			if (day.isBefore(baseline)) {
				exchangeRates.remove(day);
			}
		}
	}

	private void updateFee(LocalDate baseline, Advert advert, String currencyAtLocale, FinancialDetailsDTO feeDTO) {
		if (feeDTO == null) {
			advert.setFee(null);
			return;
		}
		if (advert.getFee() == null) {
			advert.setFee(new AdvertFinancialDetail());
		}
		updateFinancialDetails(advert.getFee(), feeDTO, currencyAtLocale, baseline);
	}

	private void updatePay(LocalDate baseline, Advert advert, String currencyAtLocale, FinancialDetailsDTO payDTO) {
		if (payDTO == null) {
			advert.setPay(null);
			return;
		}
		if (advert.getPay() == null) {
			advert.setPay(new AdvertFinancialDetail());
		}
		updateFinancialDetails(advert.getPay(), payDTO, currencyAtLocale, baseline);
	}

	private void updateFinancialDetails(AdvertFinancialDetail financialDetails, FinancialDetailsDTO financialDetailsDTO,
			String currencyAtLocale, LocalDate baseline) {
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

		setMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified, maximumSpecified,
				intervalPrefixGenerated, minimumGenerated, maximumGenerated, "Specified");
		if (currencySpecified.equals(currencyAtLocale)) {
			setMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified, maximumSpecified,
					intervalPrefixGenerated, minimumGenerated, maximumGenerated, "AtLocale");
		} else {
			try {
				BigDecimal rate = getExchangeRate(currencySpecified, currencyAtLocale, baseline);
				setConvertedMonetaryValues(financialDetails, intervalPrefixSpecified, minimumSpecified,
						maximumSpecified, intervalPrefixGenerated, minimumGenerated, maximumGenerated, rate);
			} catch (Exception e) {
				LOGGER.error("Problem performing currency conversion", e);
			}
		}
	}

	public FinancialDetailsDTO getFinancialDetailDTO(AdvertFinancialDetail detail, String newCurrency) {
		if (detail != null) {
			FinancialDetailsDTO detailDTO = new FinancialDetailsDTO();
			detailDTO.setCurrency(newCurrency);

			PrismDurationUnit interval = detail.getInterval();
			String intervalPrefix = interval.name().toLowerCase();
			detailDTO.setInterval(interval);

			String oldCurrency = detail.getCurrencySpecified();

			BigDecimal exchangeRate = getExchangeRate(oldCurrency, newCurrency, new LocalDate());

			BigDecimal minimumSpecified = (BigDecimal) getProperty(detail, intervalPrefix + "MinimumSpecified");
			BigDecimal maximumSpecified = (BigDecimal) getProperty(detail, intervalPrefix + "MaximumSpecified");

			setProperty(detailDTO, "minimum",
					minimumSpecified.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));
			setProperty(detailDTO, "maximum",
					maximumSpecified.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));

			return detailDTO;
		}
		return null;
	}

	private AdvertClosingDate getNextAdvertClosingDate(Advert advert) {
		return advertDAO.getNextAdvertClosingDate(advert, new LocalDate());
	}

	private Comment executeUpdate(ResourceParent resource, String message) throws Exception {
		return resourceService.executeUpdate(resource,
				PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_" + message));
	}

	private InstitutionAddress createAddress(InstitutionAddressDTO addressDTO) {
		InstitutionAddress address = new InstitutionAddress();
		updateAddress(addressDTO, address);
		return address;
	}

	private void updateAddress(Advert advert, InstitutionAddressDTO addressDTO) {
		InstitutionAddress address = advert.getAddress();
		if (address == null) {
			address = createAddress(addressDTO);
			entityService.save(address);
			advert.setAddress(address);
		} else {
			updateAddress(addressDTO, address);
		}
		geocodableLocationService.setLocation(addressDTO.getGoogleId(), advert.getTitle(), address);
	}

	private void updateAddress(InstitutionAddressDTO addressDTO, InstitutionAddress address) {
		address.setDomicile(entityService.getById(InstitutionDomicile.class, addressDTO.getDomicile()));
		address.setAddressLine1(addressDTO.getAddressLine1());
		address.setAddressLine2(addressDTO.getAddressLine2());
		address.setAddressTown(addressDTO.getAddressTown());
		address.setAddressRegion(addressDTO.getAddressRegion());
		address.setAddressCode(addressDTO.getAddressCode());
		address.setGoogleId(addressDTO.getGoogleId());
	}

	private InstitutionAddress getResourceAddress(Resource resource) {
		Advert advert = resource.getAdvert();
		if (advert == null) {
			return null;
		}

		InstitutionAddress address = advert.getAddress();
		if (address == null) {
			Resource parentResource = resource.getParentResource();
			if (parentResource.sameAs(resource)) {
				return null;
			}
			getResourceAddress(resource.getParentResource());
		}

		return address;
	}

}
