package com.zuehlke.pgadmissions.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.MONTH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismJoinResourceContext.VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext.APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext.EMPLOYER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
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
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.AdvertApplicationSummaryDTO;
import com.zuehlke.pgadmissions.dto.AdvertTargetDTO;
import com.zuehlke.pgadmissions.dto.EntityOpportunityFilterDTO;
import com.zuehlke.pgadmissions.dto.json.ExchangeRateLookupResponseDTO;
import com.zuehlke.pgadmissions.mapping.AdvertMapper;
import com.zuehlke.pgadmissions.rest.dto.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertCategoriesDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertClosingDateDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertCompetenceDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDetailsDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertFinancialDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceTargetDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.representation.CompetenceRepresentation;

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
    private StateService stateService;

    @Inject
    private SystemService systemService;

    @Inject
    private AddressService addressService;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

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

    public AdvertApplicationSummaryDTO getAdvertApplicationSummary(Advert advert) {
        return advertDAO.getAdvertApplicationSummary(advert);
    }

    public List<com.zuehlke.pgadmissions.dto.AdvertDTO> getAdvertList(OpportunitiesQueryDTO query, Collection<Integer> advertIds) {
        return advertIds.isEmpty() ? Lists.newArrayList() : advertDAO.getAdverts(query, advertIds);
    }

    public LinkedHashMultimap<Integer, PrismActionCondition> getAdvertActionConditions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, PrismActionCondition> actions = LinkedHashMultimap.create();
        if (isNotEmpty(resourceIds)) {
            advertDAO.getAdvertActionConditions(resourceScope, resourceIds).forEach(action -> {
                actions.put(action.getAdvertId(), action.getActionCondition());
            });
        }
        return actions;
    }

    public LinkedHashMultimap<Integer, PrismStudyOption> getAdvertStudyOptions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, PrismStudyOption> options = LinkedHashMultimap.create();
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            advertDAO.getAdvertStudyOptions(resourceScope, resourceIds).forEach(option -> {
                options.put(option.getAdvertId(), option.getStudyOption());
            });
        }
        return options;
    }

    public LinkedHashMultimap<Integer, PrismAdvertIndustry> getAdvertIndustries(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, PrismAdvertIndustry> industries = LinkedHashMultimap.create();
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            advertDAO.getAdvertIndustries(resourceScope, resourceIds).forEach(industry -> {
                industries.put(industry.getAdvertId(), industry.getIndustry());
            });
        }
        return industries;
    }

    public LinkedHashMultimap<Integer, PrismAdvertFunction> getAdvertFunctions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, PrismAdvertFunction> functions = LinkedHashMultimap.create();
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            advertDAO.getAdvertFunctions(resourceScope, resourceIds).forEach(function -> {
                functions.put(function.getAdvertId(), function.getFunction());
            });
        }
        return functions;
    }

    public Advert createAdvert(Resource parentResource, AdvertDTO advertDTO, String resourceName, User user) {
        Advert advert = new Advert();
        advert.setUser(user);
        advert.setName(resourceName);
        advert.setGloballyVisible(advertDTO.getGloballyVisible());
        entityService.save(advert);
        updateAdvert(parentResource, advert, advertDTO, resourceName);
        return advert;
    }

    public void updateAdvert(Resource parentResource, Advert advert, AdvertDTO advertDTO, String resourceName) {
        advert.setSummary(advertDTO.getSummary());
        advert.setHomepage(advertDTO.getHomepage());
        advert.setApplyHomepage(advertDTO.getApplyHomepage());
        advert.setTelephone(advertDTO.getTelephone());

        Address address = advert.getAddress();
        AddressDTO addressDTO = advertDTO.getAddress();
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
    }

    public void updateDetail(PrismScope resourceScope, Integer resourceId, AdvertDetailsDTO advertDetailsDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        advert.setDescription(advertDetailsDTO.getDescription());
        advert.setHomepage(advertDetailsDTO.getHomepage());
        updateAddress(advert, advertDetailsDTO.getAddress());
        executeUpdate(resource, "COMMENT_UPDATED_ADVERT");
    }

    public void updateFinancialDetails(PrismScope resourceScope, Integer resourceId, AdvertFinancialDetailDTO financialDetailDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();

        LocalDate baseline = new LocalDate();
        String currencyAtLocale = getCurrencyAtLocale(advert);
        updateFinancialDetail(baseline, advert, currencyAtLocale, financialDetailDTO);

        advert.setLastCurrencyConversionDate(baseline);
        executeUpdate(resource, "COMMENT_UPDATED_FEE_AND_PAYMENT");
    }

    public void updateFinancialDetails(Advert advert, String newCurrency) {
        Resource resource = advert.getResource();
        AdvertFinancialDetailDTO financialDetailDTO = getFinancialDetailDTO(advert.getPay(), newCurrency);
        updateFinancialDetails(resource.getResourceScope(), resource.getId(), financialDetailDTO);
    }

    public void updateCategories(PrismScope resourceScope, Integer resourceId, AdvertCategoriesDTO categoriesDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateCategories(advert, categoriesDTO);
        executeUpdate(resource, "COMMENT_UPDATED_CATEGORY");
    }

    public void createAdvertTarget(PrismScope resourceScope, Integer resourceId, ResourceTargetDTO target) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        User user = resource.getUser();

        ResourceDTO resourceTargetDTO = target.getResource();
        ResourceParent resourceTarget = (ResourceParent) resourceService.getById(resourceTargetDTO.getScope(), resourceTargetDTO.getId());
        Advert advertTarget = resourceTarget.getAdvert();

        User userTarget = null;
        UserDTO userTargetDTO = target.getUser();
        if (userTargetDTO != null) {
            userTarget = resourceService.joinResource(resourceTarget, userTargetDTO, VIEWER);
        }

        if (target.getContext().equals(EMPLOYER)) {
            createAdvertTarget(advertTarget, userTarget, advert, user, advertTarget, null, ENDORSEMENT_PENDING);
            createAdvertTarget(advertTarget, userTarget, advert, user, advertTarget, userTarget, ENDORSEMENT_PENDING);
        } else {
            createAdvertTarget(advert, user, advertTarget, userTarget, advertTarget, null, ENDORSEMENT_PENDING);
            createAdvertTarget(advert, user, advertTarget, userTarget, advertTarget, userTarget, ENDORSEMENT_PENDING);
        }
    }

    public boolean updateAdvertTarget(Integer advertTargetId, Boolean accept) {
        boolean performed = false;

        AdvertTarget advertTarget = advertDAO.getAdvertTargetById(advertTargetId);
        if (advertTarget != null) {
            User user = userService.getCurrentUser();

            if (user != null) {
                ResourceParent acceptResource = advertTarget.getAcceptAdvert().getResource();
                User acceptUser = advertTarget.getAcceptAdvertUser();

                PrismPartnershipState partnershipState = toBoolean(accept) ? ENDORSEMENT_PROVIDED : ENDORSEMENT_REVOKED;
                if (user.equals(acceptUser)) {
                    processAdvertTarget(advertTargetId, acceptResource, acceptUser, partnershipState);
                    performed = true;
                } else {
                    String acceptResourceReference = acceptResource.getResourceScope().name();
                    if (roleService.hasUserRole(acceptResource, acceptUser, PrismRole.valueOf(acceptResourceReference + "_ADMINISTRATOR"),
                            PrismRole.valueOf(acceptResourceReference + "_APPROVER"))) {
                        processAdvertTarget(advertTargetId, acceptResource, acceptUser, partnershipState);
                        performed = true;
                    }
                }
            }
        }

        return performed;
    }

    public void updateCompetences(PrismScope resourceScope, Integer resourceId, List<AdvertCompetenceDTO> competencesDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateCompetences(advert, competencesDTO);
        executeUpdate(resource, "COMMENT_UPDATED_COMPETENCE");
    }

    public AdvertClosingDate createClosingDate(PrismScope resourceScope, Integer resourceId, AdvertClosingDateDTO advertClosingDateDTO) {
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

    public void deleteClosingDate(PrismScope resourceScope, Integer resourceId, Integer closingDateId) {
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

    public List<AdvertTargetDTO> getAdvertTargets(ResourceParent resource) {
        return advertDAO.getAdvertTargets(resource);
    }

    public List<AdvertTargetDTO> getAdvertTargets(User user) {
        List<Integer> connectAdverts = getAdvertsForWhichUserCanManageConnections(user);
        Set<AdvertTargetDTO> advertTargets = newHashSet(getAdvertTargetsReceived(user, connectAdverts, false));
        advertTargets.addAll(getAdvertTargetsRequested(user, connectAdverts, advertTargets.stream().map(at -> at.getAdvertTargetId()).collect(toList())));
        return newArrayList(advertTargets);
    }

    public List<AdvertTargetDTO> getAdvertTargetsReceived(User user) {
        List<Integer> connectAdverts = getAdvertsForWhichUserCanManageConnections(user);
        return getAdvertTargetsReceived(user, connectAdverts, true);
    }

    public List<AdvertTargetDTO> getAdvertTargetsRequested(User user, List<Integer> connectAdverts, List<Integer> exclusions) {
        List<AdvertTargetDTO> advertTargets = Lists.newArrayList();
        for (PrismScope resourceScope : new PrismScope[] { INSTITUTION, DEPARTMENT }) {
            for (String advertReference : new String[] { "advert", "targetAdvert" }) {
                advertTargets.addAll(advertDAO.getAdvertTargetsRequested(resourceScope, advertReference, advertReference.equals("advert") ? "targetAdvert" : "advert", user,
                        connectAdverts, exclusions));
            }
        }
        return advertTargets;
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

    public List<CompetenceRepresentation> getCompetences(String searchTerm) {
        return advertDAO.searchCompetences(searchTerm).stream()
                .map(competence -> new CompetenceRepresentation().withId(competence.getId()).withName(competence.getName()).withDescription(competence.getDescription()))
                .collect(toList());
    }

    public Map<Integer, Integer> getCompetenceImportances(Advert advert) {
        Map<Integer, Integer> importances = Maps.newHashMap();
        advert.getCompetences().forEach(c -> {
            importances.put(c.getCompetence().getId(), c.getImportance());
        });
        return importances;
    }

    public Set<EntityOpportunityFilterDTO> getVisibleAdverts(OpportunitiesQueryDTO query, PrismScope[] scopes) {
        User user = userService.getCurrentUser();
        PrismMotivationContext context = query.getContext();
        Set<EntityOpportunityFilterDTO> adverts = Sets.newHashSet();
        PrismActionCondition actionCondition = context == APPLICANT ? ACCEPT_APPLICATION : ACCEPT_PROJECT;

        Integer resourceId = null;
        PrismScope resourceScope = null;
        ResourceDTO resource = query.getResource();
        if (resource != null) {
            resourceId = resource.getId();
            resourceScope = resource.getScope();
        }

        Set<Integer> possibleTargets = Sets.newHashSet();
        if (user != null) {
            if (roleService.hasUserRole(systemService.getSystem(), user, SYSTEM_ADMINISTRATOR)) {
                appendSystemUserTargets(resourceScope, resourceId, possibleTargets);
            } else {
                appendResourceUserTargets(user, resourceScope, resourceId, possibleTargets);
            }
        }

        for (PrismScope scope : scopes) {
            adverts.addAll(advertDAO.getVisibleAdverts(scope, stateService.getActiveResourceStates(scope), actionCondition, query, user, possibleTargets));
        }

        return adverts;
    }

    public void recordPartnershipStateTransition(Resource resource, Comment comment) {
        if (comment.isPartnershipStateTransitionComment()) {
            PrismPartnershipState partnershipState = isTrue(comment.getDeclinedResponse()) ? ENDORSEMENT_REVOKED : comment.getAction().getPartnershipTransitionState();

            User user = comment.getUser();
            Advert advert = resource.getAdvert();
            PrismScope resourceScope = resource.getResourceScope();

            Set<Advert> targetAdverts = Sets.newHashSet();
            for (PrismScope partnerScope : new PrismScope[] { DEPARTMENT, INSTITUTION, SYSTEM }) {
                targetAdverts.addAll(advertDAO.getAdvertsTargetsForWhichUserCanEndorse(advert, user, resourceScope, partnerScope));
            }

            targetAdverts.forEach(targetAdvert -> {
                createAdvertTarget(advert, targetAdvert, partnershipState);
            });
        }
    }

    private List<Integer> getAdvertsForWhichUserCanManageConnections(User user) {
        List<Integer> connectAdverts = Lists.newArrayList();
        for (PrismScope resourceScope : new PrismScope[] { INSTITUTION, DEPARTMENT }) {
            connectAdverts.addAll(advertDAO.getAdvertsForWhichUserCanManageConnections(user, resourceScope));
        }
        return connectAdverts;
    }

    private List<AdvertTargetDTO> getAdvertTargetsReceived(User user, List<Integer> connectAdverts, boolean pending) {
        List<AdvertTargetDTO> advertTargets = Lists.newArrayList();
        for (PrismScope resourceScope : new PrismScope[] { INSTITUTION, DEPARTMENT }) {
            for (String advertReference : new String[] { "advert", "targetAdvert" }) {
                advertTargets.addAll(advertDAO.getAdvertTargetsReceived(resourceScope, advertReference, "acceptAdvert", user, connectAdverts, pending));
            }
        }

        advertTargets.forEach(advertTarget -> {
            advertTarget.setCanAccept(true);
        });

        return advertTargets;
    }

    private void appendSystemUserTargets(PrismScope resourceScope, Integer resourceId, Set<Integer> possibleTargets) {
        if (resourceScope == null) {
            possibleTargets.addAll(advertDAO.getAdvertIds(INSTITUTION));
            possibleTargets.addAll(advertDAO.getAdvertIds(DEPARTMENT));
        } else if (resourceScope.equals(INSTITUTION)) {
            possibleTargets.add(advertDAO.getAdvertId(INSTITUTION, resourceId));
            possibleTargets.addAll(advertDAO.getAdvertIds(INSTITUTION, resourceId, DEPARTMENT));
        } else {
            possibleTargets.add(advertDAO.getAdvertId(DEPARTMENT, resourceId));
        }
    }

    private void appendResourceUserTargets(User user, PrismScope resourceScope, Integer resourceId, Set<Integer> possibleTargets) {
        if (resourceScope == null) {
            possibleTargets.addAll(advertDAO.getUserAdvertIds(INSTITUTION, user));
            possibleTargets.addAll(advertDAO.getUserAdvertIds(DEPARTMENT, user));
        } else if (resourceScope.equals(INSTITUTION)) {
            possibleTargets.addAll(asList(advertDAO.getUserAdvertId(INSTITUTION, resourceId, user)));
            possibleTargets.addAll(advertDAO.getUserAdvertIds(INSTITUTION, resourceId, DEPARTMENT, user));
        } else {
            possibleTargets.addAll(asList(advertDAO.getUserAdvertId(DEPARTMENT, resourceId, user)));
        }
    }

    private AdvertTarget createAdvertTarget(Advert advert, Advert targetAdvert, PrismPartnershipState partnershipState) {
        return entityService.createOrUpdate(new AdvertTarget().withAdvert(advert).withTargetAdvert(targetAdvert).withPartnershipState(partnershipState));
    }

    private void createAdvertTarget(Advert advert, User advertUser, Advert targetAdvert, User targetAdvertUser, Advert acceptAdvert, User acceptAdvertUser,
            PrismPartnershipState partnershipState) {
        AdvertTarget advertTarget = entityService.getOrCreate(new AdvertTarget().withAdvert(advert).withAdvertUser(advertUser).withTargetAdvert(targetAdvert)
                .withTargetAdvertUser(targetAdvertUser).withAcceptAdvert(acceptAdvert).withAcceptAdvertUser(acceptAdvertUser).withPartnershipState(partnershipState));
        if (!(acceptAdvertUser == null && updateAdvertTarget(advertTarget.getId(), true))) {
            // TODO - send the connection request
        }
    }

    private void processAdvertTarget(Integer advertTargetId, ResourceParent acceptResource, User acceptUser, PrismPartnershipState partnershipState) {
        if (partnershipState.equals(ENDORSEMENT_PROVIDED)) {
            resourceService.activateResource(acceptResource, acceptUser);
            // TODO - send the connection confirmation
        }
        advertDAO.processAdvertTarget(advertTargetId, partnershipState);
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

            entityService.flush();
        }

        Set<AdvertIndustry> subjectAreas = categories.getIndustries();
        categoriesDTO.getIndustries().stream().forEach(categoryDTO -> {
            AdvertIndustry category = new AdvertIndustry().withAdvert(advert).withIndustry(categoryDTO);
            entityService.save(category);
            subjectAreas.add(category);
        });

        Set<AdvertFunction> functions = categories.getFunctions();
        categoriesDTO.getFunctions().stream().forEach(categoryDTO -> {
            AdvertFunction category = new AdvertFunction().withAdvert(advert).withFunction(categoryDTO);
            entityService.save(category);
            functions.add(category);
        });
    }

    private void updateCompetences(Advert advert, List<AdvertCompetenceDTO> competenceDTOs) {
        Set<AdvertCompetence> competences = advert.getCompetences();

        competences.stream().forEach(advertCompetence -> {
            Competence competence = advertCompetence.getCompetence();
            competence.setAdoptedCount(competence.getAdoptedCount() - 1);
            entityService.delete(advertCompetence);
        });
        competences.clear();
        entityService.flush();

        for (AdvertCompetenceDTO competenceDTO : competenceDTOs) {
            Competence competence = getOrCreateCompetence(competenceDTO);
            String customDescription = null;
            if (!competence.getDescription().equals(competenceDTO.getDescription())) {
                customDescription = competenceDTO.getDescription();
            }
            AdvertCompetence advertCompetence = new AdvertCompetence();
            advertCompetence.setAdvert(advert);
            advertCompetence.setCompetence(competence);
            advertCompetence.setDescription(customDescription);
            advertCompetence.setImportance(competenceDTO.getImportance());
            advert.getCompetences().add(advertCompetence);
            entityService.save(advertCompetence);
        }
    }

    private String getCurrencyAtLocale(Advert advert) {
        Address addressAtLocale = advert.getAddress();
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
            minimumSpecified = minimumSpecified.multiply(rate).setScale(2, HALF_UP);
            maximumSpecified = maximumSpecified.multiply(rate).setScale(2, HALF_UP);
            minimumGenerated = minimumGenerated.multiply(rate).setScale(2, HALF_UP);
            maximumGenerated = maximumGenerated.multiply(rate).setScale(2, HALF_UP);
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

    private void updateFinancialDetail(LocalDate baseline, Advert advert, String currencyAtLocale, AdvertFinancialDetailDTO financialDetailDTO) {
        if (financialDetailDTO == null) {
            advert.setPay(null);
            return;
        }
        if (advert.getPay() == null) {
            advert.setPay(new AdvertFinancialDetail());
        }
        updateFinancialDetails(advert.getPay(), financialDetailDTO, currencyAtLocale, baseline);
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

    private void executeUpdate(ResourceParent resource, String message) {
        resourceService.executeUpdate(resource, userService.getCurrentUser(), PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_" + message));
    }

    private void updateAddress(Advert advert, AddressDTO addressDTO) {
        Address address = advert.getAddress();
        if (address == null) {
            address = new Address();
            addressService.copyAddress(address, addressDTO, advert.getName());
            entityService.save(address);
            advert.setAddress(address);
        } else {
            addressService.copyAddress(address, addressDTO, advert.getName());
        }
    }

    private Address getResourceAddress(Resource resource) {
        Advert advert = resource.getAdvert();
        if (advert == null) {
            return null;
        }

        Address address = advert.getAddress();
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
        advertClosingDate.setClosingDate(advertClosingDateDTO.getClosingDate());
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
