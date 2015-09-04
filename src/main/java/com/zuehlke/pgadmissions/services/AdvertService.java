package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.MONTH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static com.zuehlke.pgadmissions.utils.PrismWordUtils.pluralize;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertSubjectArea;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargetAdvert;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargets;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.EntityOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.dto.json.ExchangeRateLookupResponseDTO;
import com.zuehlke.pgadmissions.mapping.AdvertMapper;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertCategoriesDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertClosingDateDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertCompetenceDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertFinancialDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertFinancialDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertTargetResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertTargetsDTO;
import com.zuehlke.pgadmissions.rest.representation.advert.CompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;

@Service
@Transactional
public class AdvertService {

    private static final Logger logger = LoggerFactory.getLogger(AdvertService.class);

    private final Map<LocalDate, Map<String, BigDecimal>> exchangeRates = Maps.newHashMap();

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
    private RoleService roleService;

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

    public List<com.zuehlke.pgadmissions.dto.AdvertDTO> getAdvertList(OpportunitiesQueryDTO query, Collection<Integer> advertIds) {
        PrismScope[] scopes = new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION };
        if (query.isResourceAction()) {
            Resource resource = resourceService.getById(query.getActionId().getScope(), query.getResourceId());
            for (PrismScope resourceScope : scopes) {
                Resource enclosing = resource.getEnclosingResource(resourceScope);
                if (enclosing != null) {
                    setProperty(query, pluralize(resourceScope.getLowerCamelName()), new Integer[] { enclosing.getId() });
                    break;
                }
            }
        }

        PrismActionCondition actionCondition = query.getActionCondition();
        actionCondition = actionCondition == null ? ACCEPT_APPLICATION : actionCondition;
        query.setActionCondition(actionCondition);

        scopes = actionCondition.equals(ACCEPT_PROJECT) ? new PrismScope[] { PROGRAM, DEPARTMENT, INSTITUTION } : scopes;

        return advertIds.isEmpty() ? Lists.newArrayList() : advertDAO.getAdverts(query, advertIds);
    }

    public List<AdvertRecommendationDTO> getRecommendedAdverts(User user) {
        List<Integer> advertsRecentlyAppliedFor = advertDAO.getAdvertsRecentlyAppliedFor(user, new LocalDate().minusYears(1));
        return advertDAO.getRecommendedAdverts(user, getAdvertScopes(), advertsRecentlyAppliedFor);
    }

    public HashMultimap<Integer, ResourceConditionRepresentation> getAdvertActionConditions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        HashMultimap<Integer, ResourceConditionRepresentation> conditions = HashMultimap.create();
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            advertDAO.getAdvertActionConditions(resourceScope, resourceIds).forEach(condition -> {
                conditions.put(condition.getAdvertId(),
                        new ResourceConditionRepresentation().withActionCondition(condition.getActionCondition()).withPartnerMode(condition.getPartnerMode()));
            });
        }
        return conditions;
    }

    public HashMultimap<Integer, PrismStudyOption> getAdvertStudyOptions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        HashMultimap<Integer, PrismStudyOption> options = HashMultimap.create();
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            advertDAO.getAdvertStudyOptions(resourceScope, resourceIds).forEach(option -> {
                options.put(option.getAdvertId(), PrismStudyOption.valueOf(option.getStudyOption()));
            });
        }
        return options;
    }

    public Advert createAdvert(Resource parentResource, AdvertDTO advertDTO, String resourceName, User user) {
        Advert advert = new Advert();
        advert.setUser(user);
        advert.setName(resourceName);
        entityService.save(advert);
        updateAdvert(parentResource, advert, advertDTO, resourceName);
        return advert;
    }

    public void updateAdvert(Resource parentResource, Advert advert, AdvertDTO advertDTO, String resourceName) {
        advert.setSummary(advertDTO.getSummary());
        advert.setHomepage(advertDTO.getHomepage());
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

    public void updateFinancialDetails(PrismScope resourceScope, Integer resourceId, AdvertFinancialDetailsDTO financialDetailsDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        LocalDate baseline = new LocalDate();
        String currencyAtLocale = getCurrencyAtLocale(advert);

        AdvertFinancialDetailDTO feeDTO = financialDetailsDTO.getFee();
        updateFee(baseline, advert, currencyAtLocale, feeDTO);

        AdvertFinancialDetailDTO payDTO = financialDetailsDTO.getPay();
        updatePay(baseline, advert, currencyAtLocale, payDTO);

        advert.setLastCurrencyConversionDate(baseline);
        executeUpdate(resource, "COMMENT_UPDATED_FEE_AND_PAYMENT");
    }

    public void updateFinancialDetails(Advert advert, String newCurrency) throws Exception {
        Resource resource = advert.getResource();
        AdvertFinancialDetailDTO feeDTO = getFinancialDetailDTO(advert.getFee(), newCurrency);
        AdvertFinancialDetailDTO payDTO = getFinancialDetailDTO(advert.getPay(), newCurrency);
        updateFinancialDetails(resource.getResourceScope(), resource.getId(), new AdvertFinancialDetailsDTO().withFee(feeDTO).withPay(payDTO));
    }

    public void updateCategories(PrismScope resourceScope, Integer resourceId, AdvertCategoriesDTO categoriesDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateCategories(advert, categoriesDTO);
        executeUpdate(resource, "COMMENT_UPDATED_CATEGORY");
    }

    public void updateTargets(PrismScope resourceScope, Integer resourceId, AdvertTargetsDTO targetsDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateTargets(advert, targetsDTO);
        executeUpdate(resource, "COMMENT_UPDATED_TARGET");
    }

    public void updateCompetences(PrismScope resourceScope, Integer resourceId, List<AdvertCompetenceDTO> competencesDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateCompetences(advert, competencesDTO);
        executeUpdate(resource, "COMMENT_UPDATED_COMPETENCE");
    }

    public AdvertClosingDate createClosingDate(PrismScope resourceScope, Integer resourceId, AdvertClosingDateDTO advertClosingDateDTO) throws Exception {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        if (advert != null) {
            AdvertClosingDate advertClosingDate = createAdvertClosingDate(advert, advertClosingDateDTO);
            advertClosingDate = entityService.getOrCreate(advertClosingDate);
            advert.setClosingDate(getNextAdvertClosingDate(advert));
            executeUpdate(resource, "COMMENT_UPDATED_CLOSING_DATE");
            return advertClosingDate;
        }

        return null;
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
        return advertDAO.getAdvertsWithElapsedCurrencyConversions(baseline, getAdvertScopes());
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

    public Set<String> getAvailableAdvertThemes(Advert advert, Set<String> themes) {
        themes = themes == null ? Sets.newTreeSet() : themes;
        themes.addAll(getAdvertThemes(advert));

        Resource parentResource = advert.getResource().getParentResource();
        if (ResourceParent.class.isAssignableFrom(parentResource.getClass())) {
            getAvailableAdvertThemes(parentResource.getAdvert(), themes);
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

    public List<CompetenceRepresentation> searchCompetences(String q) {
        return advertDAO.searchCompetences(q).stream()
                .map(competence -> new CompetenceRepresentation().withId(competence.getId()).withName(competence.getName()).withDescription(competence.getDescription()))
                .collect(Collectors.toList());
    }

    public List<Integer> getAdvertTargetAdverts(Advert advert, boolean selected) {
        return advertDAO.getAdvertTargetAdverts(advert, selected);
    }

    public List<Integer> getAdvertTargetResources(Advert advert, PrismScope resourceScope, boolean selected) {
        return advertDAO.getAdvertTargetResources(advert, resourceScope, selected);
    }

    public void identifyForAdverts(User user, List<Integer> adverts) {
        advertDAO.identifyForAdverts(user, adverts);
    }

    public List<Integer> getAdvertsUserIdentifiedFor(User user) {
        return advertDAO.getAdvertsUserIdentifiedFor(user);
    }

    public List<Integer> getAdvertSelectedTargetAdverts(Advert advert) {
        return advertDAO.getAdvertSelectedTargetAdverts(advert);
    }

    public List<Integer> getAdvertsToIdentifyUserFor(User user, List<Integer> adverts) {
        return advertDAO.getAdvertsToIdentifyUserFor(user, adverts);
    }

    public void recordPartnershipStateTransition(Resource resource, Comment comment) {
        if (comment.isPartnershipStateTransitionComment()) {
            List<Advert> targetAdverts = Lists.newArrayList();
            PrismPartnershipState partnershipTransitionState =  isTrue(comment.getDeclinedResponse()) ? ENDORSEMENT_REVOKED : comment.getAction().getPartnershipTransitionState();
            for (UserRole userRole : roleService.getActionPerformerUserRoles(comment.getUser(),
                    new PrismAction[] { PROJECT_ENDORSE, PROGRAM_ENDORSE, DEPARTMENT_ENDORSE, INSTITUTION_ENDORSE })) {
                Resource userResource = userRole.getResource();
                if (userResource.getResourceScope().equals(SYSTEM)) {
                    advertDAO.endorseForAdvertTargets(resource.getAdvert(), partnershipTransitionState);
                    break;
                }
                targetAdverts.add(userResource.getAdvert());
            }

            if (!targetAdverts.isEmpty()) {
                advertDAO.endorseForAdvertTargets(resource.getAdvert(), targetAdverts, partnershipTransitionState);
            }
        }
    }

    public Set<EntityOpportunityCategoryDTO> getVisibleAdverts(OpportunitiesQueryDTO queryDTO, PrismScope[] scopes) {
        Set<EntityOpportunityCategoryDTO> adverts = Sets.newHashSet();
        for (PrismScope scope : scopes) {
            adverts.addAll(advertDAO.getVisibleAdverts(scope, stateService.getActiveResourceStates(scope), queryDTO));
        }
        return adverts;
    }

    private void updateCategories(Advert advert, AdvertCategoriesDTO categoriesDTO) {
        AdvertCategories categories = advert.getCategories();
        if (categories == null) {
            categories = new AdvertCategories();
            advert.setCategories(categories);
        } else {
            advertDAO.deleteAdvertAttributes(advert, AdvertIndustry.class);
            categories.getIndustries().clear();

            advertDAO.deleteAdvertAttributes(advert, AdvertFunction.class);
            categories.getFunctions().clear();

            advertDAO.deleteAdvertAttributes(advert, AdvertTheme.class);
            categories.getThemes().clear();

            entityService.flush();
        }

        Set<AdvertIndustry> subjectAreas = categories.getIndustries();
        categoriesDTO.getIndustries().stream().forEach(categoryDTO -> {
            AdvertIndustry category = new AdvertIndustry().withAdvert(advert).withValue(categoryDTO);
            entityService.save(category);
            subjectAreas.add(category);
        });

        Set<AdvertFunction> functions = categories.getFunctions();
        categoriesDTO.getFunctions().stream().forEach(categoryDTO -> {
            AdvertFunction category = new AdvertFunction().withAdvert(advert).withValue(categoryDTO);
            entityService.save(category);
            functions.add(category);
        });

        Set<AdvertTheme> themes = categories.getThemes();
        categoriesDTO.getThemes().stream().forEach(categoryDTO -> {
            AdvertTheme category = new AdvertTheme().withAdvert(advert).withValue(categoryDTO);
            entityService.save(category);
            themes.add(category);
        });
    }

    private void updateTargets(Advert advert, AdvertTargetsDTO targetsDTO) {
        AdvertTargets targets = advert.getTargets();
        if (targets == null) {
            targets = new AdvertTargets();
            advert.setTargets(targets);
        } else {
            advertDAO.deleteAdvertAttributes(advert, AdvertSubjectArea.class);
            targets.getSubjectAreas().clear();
            entityService.flush();
        }

        Set<AdvertSubjectArea> subjectAreas = targets.getSubjectAreas();
        if (targetsDTO.getSubjectAreas() != null) {
            targetsDTO.getSubjectAreas().stream().forEach(targetDTO -> {
                AdvertSubjectArea target = new AdvertSubjectArea().withAdvert(advert).withValue(entityService.getById(ImportedSubjectArea.class, targetDTO.getId()));
                entityService.save(target);
                subjectAreas.add(target);
            });
        }

        List<Integer> newTargetValues = Lists.newArrayList();
        Set<AdvertTargetAdvert> adverts = targets.getAdverts();
        if (targetsDTO.getResources() != null) {
            targetsDTO.getResources().stream().forEach(targetDTO -> {
                AdvertTargetAdvert target = createAdvertTargetAdvert(advert, targetDTO, false);
                entityService.createOrUpdate(target);
                newTargetValues.add(target.getValueId());
                adverts.add(target);
            });
        }

        if (targetsDTO.getSelectedResources() != null) {
            targetsDTO.getSelectedResources().stream().forEach(targetDTO -> {
                AdvertTargetAdvert target = createAdvertTargetAdvert(advert, targetDTO, true);
                entityService.createOrUpdate(target);
                newTargetValues.add(target.getValueId());
                adverts.add(target);
            });
        }

        if (newTargetValues.isEmpty()) {
            advertDAO.deleteAdvertAttributes(advert, AdvertTargetAdvert.class);
        } else {
            advertDAO.deleteAdvertTargetAdverts(advert, newTargetValues);
        }
    }

    private AdvertTargetAdvert createAdvertTargetAdvert(Advert advert, AdvertTargetResourceDTO targetDTO, boolean selected) {
        return new AdvertTargetAdvert().withAdvert(advert).withValue(resourceService.getById(targetDTO.getScope(), targetDTO.getId()).getAdvert())
                .withSelected(selected).withPartnershipState(ENDORSEMENT_PENDING);
    }

    private void updateCompetences(Advert advert, List<AdvertCompetenceDTO> competenceDTOs) {
        AdvertTargets targets = advert.getTargets();
        if (targets == null) {
            targets = new AdvertTargets();
            advert.setTargets(targets);
        }

        targets.getCompetences().stream().forEach(advertCompetence -> {
            Competence competence = advertCompetence.getValue();
            competence.setAdoptedCount(competence.getAdoptedCount() - 1);
            entityService.delete(advertCompetence);
        });
        targets.getCompetences().clear();
        entityService.flush();

        for (AdvertCompetenceDTO competenceDTO : competenceDTOs) {
            Competence competence = getOrCreateCompetence(competenceDTO);
            String customDescription = null;
            if (!competence.getDescription().equals(competenceDTO.getDescription())) {
                customDescription = competenceDTO.getDescription();
            }
            AdvertCompetence advertCompetence = new AdvertCompetence();
            advertCompetence.setAdvert(advert);
            advertCompetence.setValue(competence);
            advertCompetence.setDescription(customDescription);
            advertCompetence.setImportance(competenceDTO.getImportance());
            advert.getTargets().getCompetences().add(advertCompetence);
            entityService.save(advertCompetence);
        }
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
            logger.error("Unable to perform currency conversion", e);
        }
    }

    private BigDecimal getExchangeRate(String currencySpecified, String currencyConverted, LocalDate baseline) {
        removeExpiredExchangeRates(baseline);

        String pair = currencySpecified + currencyConverted;
        Map<String, BigDecimal> todaysRates = exchangeRates.get(baseline);

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
                logger.error("Problem performing currency conversion", e);
            }
        }
    }

    private AdvertFinancialDetailDTO getFinancialDetailDTO(AdvertFinancialDetail detail, String newCurrency) {
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
        geocodableLocationService.setLocation(addressDTO.getGoogleId(), advert.getName(), address);
    }

    private void updateAddress(AddressAdvertDTO addressDTO, AddressAdvert address) {
        address.setDomicile(entityService.getById(ImportedAdvertDomicile.class, addressDTO.getDomicile().getId()));
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

    private AdvertClosingDate createAdvertClosingDate(Advert advert, AdvertClosingDateDTO advertClosingDateDTO) {
        AdvertClosingDate advertClosingDate = new AdvertClosingDate();
        advertClosingDate.setAdvert(advert);
        advertClosingDate.setValue(advertClosingDateDTO.getClosingDate());
        return advertClosingDate;
    }

    private Competence getOrCreateCompetence(AdvertCompetenceDTO competenceDTO) {
        DateTime baseline = new DateTime();
        Competence transientCompetence = new Competence().withName(competenceDTO.getName()).withDescription(competenceDTO.getDescription()).withAdoptedCount(1)
                .withCreatedTimestamp(baseline).withUpdatedTimestamp(baseline);
        Competence persistentCompetence = entityService.getDuplicateEntity(transientCompetence);
        if (persistentCompetence == null) {
            entityService.save(transientCompetence);
            return transientCompetence;
        } else {
            persistentCompetence.setAdoptedCount(persistentCompetence.getAdoptedCount() + 1);
            persistentCompetence.setUpdatedTimestamp(baseline);
        }
        return persistentCompetence;
    }

    private HashMultimap<PrismScope, PrismState> getAdvertScopes() {
        HashMultimap<PrismScope, PrismState> scopes = HashMultimap.create();
        for (PrismScope scope : new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION }) {
            scopes.putAll(scope, stateService.getActiveResourceStates(scope));
        }
        return scopes;
    }

}
