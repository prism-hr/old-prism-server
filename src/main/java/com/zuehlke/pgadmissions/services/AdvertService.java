package com.zuehlke.pgadmissions.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.advertScopes;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.targetScopes;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.MONTH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.PERSONAL_DEVELOPMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext.APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext.EMPLOYER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismRoleContext.VIEWER;
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
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static com.zuehlke.pgadmissions.utils.PrismEnumUtils.values;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.containsAny;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.toBoolean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

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

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.Invitation;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargetPending;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
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
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertFinancialDetailDTO.AdvertFinancialDetailPayDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceConnectionInvitationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceConnectionInvitationsDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.representation.CompetenceRepresentation;
import com.zuehlke.pgadmissions.utils.PrismMappingUtils;

import jersey.repackaged.com.google.common.base.Objects;

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
    private NotificationService notificationService;

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
    private InvitationService invitationService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private RestTemplate restTemplate;

    @Inject
    private PrismMappingUtils prismMappingUtils;

    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }

    public AdvertClosingDate getClosingDateById(Integer id) {
        return entityService.getById(AdvertClosingDate.class, id);
    }

    public AdvertTargetPending getAdvertTargetPendingById(Integer id) {
        return entityService.getById(AdvertTargetPending.class, id);
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
        List<PrismOpportunityType> targetOpportunityTypes = advertDTO.getTargetOpportunityTypes();
        if (isNotEmpty(targetOpportunityTypes)) {
            advert.setTargetOpportunityTypes(Joiner.on("|").join(targetOpportunityTypes.stream().map(tot -> tot.name()).collect(toList())));
        }

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

    public AdvertTarget createAdvertTarget(ResourceCreationDTO resourceDTO, ResourceRelationCreationDTO targetDTO) {
        User user = userService.getCurrentUser();
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceDTO.getScope(), resourceDTO.getId());
        return createAdvertTarget(user, resource, targetDTO, true);
    }

    public AdvertTarget createAdvertTarget(ResourceParent resource, ResourceRelationCreationDTO targetDTO) {
        return createAdvertTarget(resource.getUser(), resource, targetDTO, false);
    }

    public AdvertTarget createAdvertTarget(ResourceParent resource, User user, ResourceParent resourceTarget, User userTarget, PrismResourceContext context, String message) {
        Advert advert = resource.getAdvert();
        Advert advertTarget = resourceTarget.getAdvert();

        if (context.equals(EMPLOYER)) {
            return createAdvertTarget(advertTarget, userTarget, advert, user, advertTarget, userTarget, message);
        } else {
            return createAdvertTarget(advert, user, advertTarget, userTarget, advertTarget, userTarget, message);
        }
    }

    public AdvertTargetPending createAdvertTargetPending(ResourceConnectionInvitationsDTO targets) {
        User user = userService.getCurrentUser();
        ResourceDTO resourceDTO = targets.getResourceDTO();
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceDTO.getScope(), resourceDTO.getId());
        if (resourceService.getResourceForWhichUserCanConnect(user, resource) != null) {
            List<ResourceRelationCreationDTO> invitations = targets.getInvitations();
            List<ResourceConnectionInvitationDTO> connections = targets.getConnections();

            String invitationsSerial = null;
            String connectionsSerial = null;
            if (isNotEmpty(invitations)) {
                invitationsSerial = prismMappingUtils.writeValue(invitations);
            }

            if (isNotEmpty(connections)) {
                connectionsSerial = prismMappingUtils.writeValue(connections);
            }

            AdvertTargetPending advertTargetPending = new AdvertTargetPending().withAdvert(resource.getAdvert()).withUser(user).withAdvertTargetInviteList(invitationsSerial)
                    .withAdvertTargetConnectList(connectionsSerial).withAdvertTargetMessage(targets.getMessage());
            entityService.save(advertTargetPending);
            return advertTargetPending;
        }

        return null;
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
                boolean isAdmin = roleService.hasUserRole(acceptResource, user, PrismRole.valueOf(acceptResource.getResourceScope().name() + "_ADMINISTRATOR"));
                if (Objects.equal(user, acceptUser) || isAdmin) {
                    boolean endorsementProvided = partnershipState.equals(ENDORSEMENT_PROVIDED);
                    if (endorsementProvided) {
                        resourceService.activateResource(systemService.getSystem().getUser(), advertTarget.getOtherAdvert().getResource());
                    }
                    advertDAO.processAdvertTarget(advertTargetId, partnershipState);

                    Set<PrismPartnershipState> oldPartnershipStates = Sets.newHashSet();
                    if (isAdmin) {
                        advertDAO.getSimilarAdvertTarget(advertTarget, user).forEach(similarAdvertTarget -> {
                            oldPartnershipStates.add(similarAdvertTarget.getPartnershipState());
                            similarAdvertTarget.setPartnershipState(partnershipState);
                        });
                    }

                    if (endorsementProvided && !oldPartnershipStates.contains(ENDORSEMENT_PROVIDED)) {
                        notificationService.sendConnectionNotification(userService.getCurrentUser(), advertTarget.getOtherUser(), advertTarget);
                    }

                    performed = true;
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

    public List<AdvertTargetDTO> getAdvertTargets(Advert advert) {
        ResourceParent resource = advert.getResource();
        List<Integer> connectAdverts = Lists.newArrayList();
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            Department department = resource.getDepartment();
            if (department != null) {
                connectAdverts.add(department.getAdvert().getId());
            }
            connectAdverts.add(resource.getInstitution().getAdvert().getId());
        } else {
            connectAdverts.add(resource.getAdvert().getId());
        }

        Map<Integer, AdvertTargetDTO> advertTargets = Maps.newHashMap();
        String[] opprortunityCategoriesSplit = resource.getOpportunityCategories().split("\\|");
        List<PrismOpportunityCategory> opportunityCategories = asList(opprortunityCategoriesSplit).stream().map(PrismOpportunityCategory::valueOf).collect(toList());
        if (containsAny(asList(EXPERIENCE, WORK), opportunityCategories)) {
            for (PrismScope targetScope : targetScopes) {
                advertDAO.getAdvertTargets(targetScope, "advert", "targetAdvert", null, connectAdverts, null).forEach(at -> {
                    advertTargets.put(at.getId(), at);
                });
            }
        }

        if (containsAny(asList(STUDY, PERSONAL_DEVELOPMENT), opportunityCategories)) {
            for (PrismScope targetScope : targetScopes) {
                advertDAO.getAdvertTargets(targetScope, "targetAdvert", "advert", null, connectAdverts, null).forEach(at -> {
                    advertTargets.put(at.getId(), at);
                });
            }
        }

        User user = userService.getCurrentUser();
        List<Integer> userAdverts = getAdvertsForWhichUserCanManageConnections(user);

        List<Integer> userAdvertTargets = advertDAO.getAdvertTargetsUserCanManage(user, userAdverts);
        advertTargets.keySet().forEach(at -> {
            if (userAdvertTargets.contains(at)) {
                advertTargets.get(at).setCanManage(true);
            }
        });

        return newArrayList(advertTargets.values());
    }

    public List<AdvertTargetDTO> getAdvertTargets(User user) {
        List<Integer> connectAdverts = getAdvertsForWhichUserCanManageConnections(user);
        Set<AdvertTargetDTO> advertTargets = newHashSet(getAdvertTargetsReceived(user, connectAdverts, false));
        advertTargets.addAll(getAdvertTargets(user, connectAdverts, advertTargets.stream().map(at -> at.getId()).collect(toList())));
        return newArrayList(advertTargets);
    }

    public List<AdvertTargetDTO> getAdvertTargets(User user, List<Integer> connectAdverts, List<Integer> exclusions) {
        List<AdvertTargetDTO> advertTargets = Lists.newArrayList();
        for (PrismScope resourceScope : new PrismScope[] { INSTITUTION, DEPARTMENT }) {
            for (String advertReference : new String[] { "advert", "targetAdvert" }) {
                String otherAdvertReference = advertReference.equals("advert") ? "targetAdvert" : "advert";
                advertTargets.addAll(advertDAO.getAdvertTargets(resourceScope, advertReference, otherAdvertReference, user, connectAdverts, exclusions));
            }
        }
        return advertTargets;
    }

    public List<AdvertTargetDTO> getAdvertTargetsReceived(User user) {
        List<Integer> connectAdverts = getAdvertsForWhichUserCanManageConnections(user);
        return getAdvertTargetsReceived(user, connectAdverts, true);
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

    public Set<EntityOpportunityFilterDTO> getVisibleAdverts(User user, OpportunitiesQueryDTO query, PrismScope[] scopes) {
        PrismResourceContext context = query.getContext();
        Set<EntityOpportunityFilterDTO> adverts = Sets.newHashSet();
        PrismActionCondition actionCondition = context == APPLICANT ? ACCEPT_APPLICATION : ACCEPT_PROJECT;

        Set<Integer> nodeAdverts = Sets.newHashSet();
        PrismScope resourceScope = query.getResourceScope();
        HashMultimap<PrismScope, PrismState> states = HashMultimap.create();
        if (resourceScope != null) {
            Integer resourceId = query.getResourceId();
            stream(scopes).forEach(advertScope -> {
                List<PrismState> advertStates = stateService.getActiveResourceStates(advertScope);
                states.putAll(advertScope, advertStates);

                nodeAdverts.addAll(advertDAO.getAdvertsForEnclosingResource(resourceScope, resourceId, advertScope, advertStates));
                stream(targetScopes).forEach(targeterScope -> {
                    if (advertScope.ordinal() > targeterScope.ordinal()) {
                        nodeAdverts.addAll(advertDAO.getAdvertsForTargetResource(targeterScope, resourceScope, resourceId, advertScope, advertStates));
                    }
                });
            });
        }

        Set<Integer> userAdverts = getUserAdverts(user, scopes);
        for (PrismScope scope : scopes) {
            Collection<PrismState> advertStates = states.get(scope);
            advertStates = isEmpty(advertStates) ? stateService.getActiveResourceStates(scope) : advertStates;
            adverts.addAll(advertDAO.getVisibleAdverts(scope, advertStates, actionCondition, nodeAdverts, userAdverts, query));
        }

        return adverts;
    }

    public void recordPartnershipStateTransition(Resource resource, Comment comment) {
        if (comment.isPartnershipStateTransitionComment()) {
            PrismPartnershipState partnershipState = comment.getAction().getPartnershipTransitionState();

            User user = comment.getUser();
            Advert advert = resource.getAdvert();
            PrismScope resourceScope = resource.getResourceScope();

            Set<Advert> targetAdverts = Sets.newHashSet();
            List<Integer> targeterEntities = getAdvertTargeterEntities(user, resourceScope);
            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : targetScopes) {
                    if (targeterScope.ordinal() < resourceScope.ordinal()) {
                        ResourceParent targeterResource = (ResourceParent) getProperty(advert, targeterScope.getLowerCamelName());
                        if (targeterResource != null) {
                            for (PrismScope targetScope : targetScopes) {
                                targetAdverts.addAll(advertDAO.getAdvertsTargetsForWhichUserCanEndorse(targeterResource.getAdvert(), user, resourceScope, targeterScope,
                                        targetScope, targeterEntities));
                            }
                        }
                    }
                }
            }

            targetAdverts.forEach(targetAdvert -> {
                createAdvertTarget(advert, targetAdvert, partnershipState);
            });
        }
    }

    public List<Integer> getAdvertTargetPendings() {
        return advertDAO.getAdvertTargetPendings();
    }

    @SuppressWarnings("unchecked")
    public void processAdvertTargetPending(Integer advertTargetPendingId) {
        AdvertTargetPending advertTargetPending = getAdvertTargetPendingById(advertTargetPendingId);

        String invitationsSerial = advertTargetPending.getAdvertTargetInviteList();
        String connectionsSerial = advertTargetPending.getAdvertTargetConnectList();
        if (invitationsSerial != null) {
            List<ResourceRelationCreationDTO> invitations = prismMappingUtils.readValue(invitationsSerial, List.class, ResourceRelationCreationDTO.class);
            for (Iterator<ResourceRelationCreationDTO> iterator = invitations.iterator(); iterator.hasNext();) {
                ResourceRelationCreationDTO invitation = iterator.next();
                resourceService.inviteResourceRelation(advertTargetPending.getAdvert().getResource(), advertTargetPending.getUser(), invitation,
                        advertTargetPending.getAdvertTargetMessage());

                iterator.remove();
                advertTargetPending.setAdvertTargetInviteList(invitations.isEmpty() ? null : prismMappingUtils.writeValue(invitations));
                return;
            }
        } else if (connectionsSerial != null) {
            List<ResourceRelationCreationDTO> connections = prismMappingUtils.readValue(connectionsSerial, List.class, ResourceRelationCreationDTO.class);
            for (Iterator<ResourceRelationCreationDTO> iterator = connections.iterator(); iterator.hasNext();) {
                ResourceRelationCreationDTO targetDTO = iterator.next();
                ResourceCreationDTO ResourceRelationCreationDTO = targetDTO.getResource().getResource();
                ResourceParent resourceTarget = (ResourceParent) resourceService.getById(ResourceRelationCreationDTO.getScope(), ResourceRelationCreationDTO.getId());
                createAdvertTarget(advertTargetPending.getAdvert().getResource(), advertTargetPending.getUser(), resourceTarget, targetDTO.getUser(),
                        ResourceRelationCreationDTO.getContext(), targetDTO.getMessage(), false);

                iterator.remove();
                advertTargetPending.setAdvertTargetConnectList(connections.isEmpty() ? null : prismMappingUtils.writeValue(connections));
                return;
            }
        } else {
            entityService.delete(advertTargetPending);
        }
    }

    private List<Integer> getAdvertsForWhichUserCanManageConnections(User user) {
        return getAdvertsForWhichUserHasRolesStrict(user, new String[] { "ADMINISTRATOR", "APPROVER" }, null);
    }

    public List<Integer> getAdvertTargeterEntities(PrismScope scope) {
        return getAdvertTargeterEntities(null, scope);
    }

    public List<Integer> getAdvertTargeterEntities(User user, PrismScope scope) {
        PrismScopeCategory scopeCategory = scope.getScopeCategory();
        boolean applicationCategory = scopeCategory.equals(APPLICATION);
        boolean opportunityCategory = scopeCategory.equals(OPPORTUNITY);

        Set<Integer> targeterEntities = Sets.newHashSet();
        if (user == null || roleService.hasUserRole(systemService.getSystem(), user, SYSTEM_ADMINISTRATOR)) {
            targeterEntities.addAll(applicationCategory ? applicationService.getApplicationsForTargets() : advertDAO.getAdvertsForTargets());
        } else if (applicationCategory) {
            HashMultimap<PrismScope, Integer> students = HashMultimap.create();
            for (PrismScope targetScope : targetScopes) {
                List<PrismRole> roles = values(PrismRole.class, targetScope, new String[] { "ADMINISTRATOR", "APPROVER" });
                List<Integer> resources = resourceService.getResourcesForWhichUserHasRoles(user, roles);
                if (isNotEmpty(resources)) {
                    students.putAll(targetScope, userService.getUsersWithRoles(targetScope, resources, PrismRole.valueOf(targetScope.name() + "_STUDENT")));
                }

                if (targetScope.equals(DEPARTMENT)) {
                    roles = values(PrismRole.class, INSTITUTION, new String[] { "ADMINISTRATOR", "APPROVER" });
                    resources = resourceService.getResourcesForWhichUserHasRoles(user, roles);
                    if (isNotEmpty(resources)) {
                        students.putAll(targetScope, userService.getUsersWithRoles(targetScope, INSTITUTION, resources, PrismRole.valueOf(targetScope.name() + "_STUDENT")));
                    }
                }
            }

            for (PrismScope targeterScope : targetScopes) {
                for (PrismScope targetScope : targetScopes) {
                    targeterEntities.addAll(applicationService.getApplicationsForTargets(user, targeterScope, targetScope, students.get(targetScope)));
                }
            }
        } else if (opportunityCategory) {
            for (PrismScope targetScope : targetScopes) {
                targeterEntities.addAll(advertDAO.getAdvertsForTargets(user, targetScope));
            }
        }

        return newArrayList(targeterEntities);
    }

    public Set<Integer> getUserAdverts(User user, PrismScope... displayScopes) {
        Set<Integer> userAdverts = Sets.newHashSet();

        HashMultimap<PrismScope, PrismState> states = HashMultimap.create();
        stream(displayScopes).forEach(displayScope -> {
            stream(advertScopes).forEach(advertScope -> {
                if (displayScope.ordinal() >= advertScope.ordinal()) {
                    Collection<PrismState> advertStates = stateService.getActiveResourceStates(displayScope);
                    states.putAll(displayScope, advertStates);
                    userAdverts.addAll(advertDAO.getUserAdverts(user, advertScope, displayScope, advertStates));
                }
            });
        });

        Set<Integer> memberAdverts = Sets.newHashSet();
        stream(targetScopes).forEach(memberScope -> {
            Collection<PrismState> memberStates = states.get(memberScope);
            memberStates = isEmpty(memberStates) ? stateService.getActiveResourceStates(memberScope) : memberStates;
            memberAdverts.addAll(advertDAO.getUserAdverts(user, memberScope, memberStates));
        });

        if (isNotEmpty(memberAdverts)) {
            List<Integer> revokedAdverts = advertDAO.getRevokedAdverts(memberAdverts);
            stream(displayScopes).forEach(displayScope -> {
                if (displayScope.getScopeCategory().equals(OPPORTUNITY)) {
                    stream(targetScopes).forEach(targeterScope -> {
                        stream(targetScopes).forEach(targetScope -> {
                            Collection<PrismState> advertStates = states.get(displayScope);
                            advertStates = isEmpty(advertStates) ? stateService.getActiveResourceStates(displayScope) : advertStates;
                            userAdverts.addAll(advertDAO.getUserAdverts(user, targeterScope, targetScope, displayScope, advertStates, revokedAdverts));
                        });
                    });
                }
            });
        }

        if (isNotEmpty(userAdverts)) {
            stream(advertScopes).forEach(scope -> {
                if (!scope.equals(INSTITUTION)) {
                    Collection<PrismState> advertStates = states.get(scope);
                    advertStates = isEmpty(advertStates) ? stateService.getActiveResourceStates(scope) : advertStates;
                    userAdverts.addAll(advertDAO.getAdvertsForEnclosedAdverts(scope, advertStates, userAdverts));
                }
            });
        }

        Set<Integer> adverts = Sets.newHashSet();
        Arrays.stream(displayScopes).forEach(scope -> adverts.addAll(advertDAO.getVisibleAdverts(scope, stateService.getActiveResourceStates(scope), userAdverts)));
        return adverts;
    }

    public List<Integer> getAdvertsForWhichUserHasRolesStrict(User user, String[] roleExtensions) {
        return getAdvertsForWhichUserHasRoles(user, roleExtensions, null, true);
    }

    public List<Integer> getAdvertsForWhichUserHasRolesStrict(User user, String[] roleExtensions, Collection<Integer> advertIds) {
        return getAdvertsForWhichUserHasRoles(user, roleExtensions, advertIds, true);
    }

    public List<Integer> getAdvertsForWhichUserHasRoles(User user, String[] roleExtensions, Collection<Integer> advertIds, boolean strict) {
        List<Integer> adverts = Lists.newArrayList();
        if (user != null) {
            for (PrismScope scope : advertScopes) {
                List<PrismState> states = stateService.getActiveResourceStates(scope);
                roleExtensions = getFilteredRoleExtensions(scope, roleExtensions);
                if (roleExtensions.length > 0) {
                    adverts.addAll(advertDAO.getAdvertsForWhichUserHasRoles(user, scope, states, roleExtensions, advertIds, strict));
                }
            }
        }
        return adverts;
    }

    public List<AdvertTarget> getAdvertTargetsForAdverts(Collection<Integer> adverts) {
        return advertDAO.getAdvertTargetsForAdverts(adverts);
    }

    private String[] getFilteredRoleExtensions(PrismScope scope, String[] roleExtensions) {
        String scopeName = scope.name();
        List<String> permittedRoleExtensions = Lists.newArrayList();
        roleService.getRolesByScope(scope).forEach(role -> {
            String roleName = role.name();
            permittedRoleExtensions.add(roleName.replace(scopeName + "_", ""));
        });

        List<String> filteredRoleExtensions = stream(roleExtensions).filter(roleExtension -> permittedRoleExtensions.contains(roleExtension)).collect(toList());
        return filteredRoleExtensions.toArray(new String[filteredRoleExtensions.size()]);
    }

    private List<AdvertTargetDTO> getAdvertTargetsReceived(User user, List<Integer> connectAdverts, boolean pending) {
        List<AdvertTargetDTO> advertTargets = Lists.newArrayList();
        for (PrismScope resourceScope : targetScopes) {
            for (String advertReference : new String[] { "advert", "targetAdvert" }) {
                advertTargets.addAll(advertDAO.getAdvertTargetsReceived(resourceScope, "acceptAdvert", advertReference, user, connectAdverts, pending));
            }
        }

        advertTargets.forEach(advertTarget -> {
            advertTarget.setCanManage(true);
        });

        return advertTargets;
    }

    private AdvertTarget createAdvertTarget(User user, ResourceParent resource, ResourceRelationCreationDTO targetDTO, boolean validate) {
        ResourceCreationDTO ResourceRelationCreationDTO = targetDTO.getResource().getResource();
        ResourceParent resourceTarget = (ResourceParent) resourceService.getById(ResourceRelationCreationDTO.getScope(), ResourceRelationCreationDTO.getId());
        return createAdvertTarget(resource, user, resourceTarget, targetDTO.getUser(), targetDTO.getContext().getContext(), targetDTO.getMessage(), true);
    }

    private AdvertTarget createAdvertTarget(ResourceParent resource, User user, ResourceParent resourceTarget, UserDTO userTargetDTO, PrismResourceContext context, String message,
            boolean validate) {
        if (!(validate && resourceService.getResourceForWhichUserCanConnect(user, resource) == null)) {
            User userTarget = null;
            if (userTargetDTO != null) {
                userTarget = resourceService.joinResource(resourceTarget, userTargetDTO, VIEWER);
            }

            return createAdvertTarget(resource, user, resourceTarget, userTarget, context, message);
        }

        return null;
    }

    private AdvertTarget createAdvertTarget(Advert advert, Advert targetAdvert, PrismPartnershipState partnershipState) {
        return entityService
                .createOrUpdate(new AdvertTarget().withAdvert(advert).withTargetAdvert(targetAdvert).withAcceptAdvert(targetAdvert).withPartnershipState(partnershipState));
    }

    private AdvertTarget createAdvertTarget(Advert advert, User user, Advert advertTarget, User userTarget, Advert advertAccept, User userAccept, String message) {
        AdvertTarget targetUser = null;
        if (userTarget != null) {
            targetUser = createAdvertTarget(advert, user, advertTarget, userTarget, advertAccept, userAccept, ENDORSEMENT_PENDING);
        }

        AdvertTarget targetAdmin = createAdvertTarget(advert, user, advertTarget, userTarget, advertAccept, null, ENDORSEMENT_PENDING);
        Invitation invitation = invitationService.createInvitation(targetAdmin.getOtherUser(), message);

        if (!(targetUser == null || updateAdvertTarget(targetUser.getId(), true))) {
            targetUser.setInvitation(invitation);
        }

        if (!updateAdvertTarget(targetAdmin.getId(), true)) {
            targetAdmin.setInvitation(invitation);
        }

        return targetUser == null ? targetAdmin : targetUser;
    }

    private AdvertTarget createAdvertTarget(Advert advert, User advertUser, Advert targetAdvert, User targetAdvertUser, Advert acceptAdvert, User acceptAdvertUser,
            PrismPartnershipState partnershipState) {
        return entityService.getOrCreate(new AdvertTarget().withAdvert(advert).withAdvertUser(advertUser).withTargetAdvert(targetAdvert)
                .withTargetAdvertUser(targetAdvertUser).withAcceptAdvert(acceptAdvert).withAcceptAdvertUser(acceptAdvertUser).withPartnershipState(partnershipState));
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

        for (AdvertCompetence advertCompetence : competences) {
            Competence competence = advertCompetence.getCompetence();
            competence.setAdoptedCount(competence.getAdoptedCount() - 1);
            entityService.delete(advertCompetence);
        }

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
        setProperty(financialDetails, intervalPrefixSpecified + "Minimum" + context, minimumSpecified);
        setProperty(financialDetails, intervalPrefixSpecified + "Maximum" + context, maximumSpecified);
        setProperty(financialDetails, intervalPrefixGenerated + "Minimum" + context, minimumGenerated);
        setProperty(financialDetails, intervalPrefixGenerated + "Maximum" + context, maximumGenerated);
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
        AdvertFinancialDetailPayDTO payDTO = financialDetailsDTO.getPay();
        if (payDTO != null) {
            PrismDurationUnit interval = payDTO.getInterval();
            String currencySpecified = payDTO.getCurrency();

            financialDetails.setInterval(interval);
            financialDetails.setCurrencySpecified(currencySpecified);
            financialDetails.setCurrencyAtLocale(currencyAtLocale);

            String intervalPrefixSpecified = interval.name().toLowerCase();
            BigDecimal minimumSpecified = payDTO.getMinimum();
            BigDecimal maximumSpecified = payDTO.getMaximum();

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
    }

    private AdvertFinancialDetailDTO getFinancialDetailDTO(AdvertFinancialDetail detail, String newCurrency) {
        if (detail != null) {
            AdvertFinancialDetailPayDTO payDTO = new AdvertFinancialDetailPayDTO();
            payDTO.setCurrency(newCurrency);

            PrismDurationUnit interval = detail.getInterval();
            String intervalPrefix = interval.name().toLowerCase();
            payDTO.setInterval(interval);

            String oldCurrency = detail.getCurrencySpecified();

            BigDecimal exchangeRate = getExchangeRate(oldCurrency, newCurrency, new LocalDate());

            BigDecimal minimumSpecified = (BigDecimal) getProperty(detail, intervalPrefix + "MinimumSpecified");
            BigDecimal maximumSpecified = (BigDecimal) getProperty(detail, intervalPrefix + "MaximumSpecified");

            setProperty(payDTO, "minimum", minimumSpecified.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));
            setProperty(payDTO, "maximum", maximumSpecified.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));

            return new AdvertFinancialDetailDTO().withPay(payDTO);
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
            return persistentCompetence;
        }
    }

    private HashMultimap<PrismScope, PrismState> getAdvertScopes() {
        HashMultimap<PrismScope, PrismState> scopes = HashMultimap.create();
        for (PrismScope scope : new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION }) {
            scopes.putAll(scope, stateService.getActiveResourceStates(scope));
        }
        return scopes;
    }

}
