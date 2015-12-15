package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.containsAny;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.PrismConstants.ADVERT_LIST_PAGE_ROW_COUNT;
import static uk.co.alumeni.prism.PrismConstants.WORK_DAYS_IN_WEEK;
import static uk.co.alumeni.prism.PrismConstants.WORK_HOURS_IN_DAY;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.dao.WorkflowDAO.opportunityScopes;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.HOUR;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.getDurationUnitAsHours;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.PERSONAL_DEVELOPMENT;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.STUDY;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.WORK;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.APPLICANT;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.EMPLOYER;
import static uk.co.alumeni.prism.domain.definitions.PrismRoleContext.VIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;
import static uk.co.alumeni.prism.utils.PrismEnumUtils.values;
import static uk.co.alumeni.prism.utils.PrismListUtils.getRowsToReturn;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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

import uk.co.alumeni.prism.dao.AdvertDAO;
import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.Theme;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertCategories;
import uk.co.alumeni.prism.domain.advert.AdvertCompetence;
import uk.co.alumeni.prism.domain.advert.AdvertFinancialDetail;
import uk.co.alumeni.prism.domain.advert.AdvertFunction;
import uk.co.alumeni.prism.domain.advert.AdvertIndustry;
import uk.co.alumeni.prism.domain.advert.AdvertLocation;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.advert.AdvertTargetPending;
import uk.co.alumeni.prism.domain.advert.AdvertTheme;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertBenefit;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.AdvertApplicationSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertCategoryDTO;
import uk.co.alumeni.prism.dto.AdvertLocationAddressPartSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertTargetDTO;
import uk.co.alumeni.prism.dto.AdvertUserDTO;
import uk.co.alumeni.prism.dto.EntityOpportunityCategoryDTO;
import uk.co.alumeni.prism.dto.json.ExchangeRateLookupResponseDTO;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.OpportunitiesQueryDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCategoriesDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCompetenceDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertFinancialDetailDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertSettingsDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertVisibilityDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceConnectionInvitationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceConnectionInvitationsDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceParentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.utils.PrismJsonMappingUtils;

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
    private ActivityService activityService;

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
    private TagService tagService;

    @Inject
    private UserService userService;

    @Inject
    private InvitationService invitationService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private RestTemplate restTemplate;

    @Inject
    private PrismJsonMappingUtils prismJsonMappingUtils;

    public Advert getById(Integer id) {
        return entityService.getById(Advert.class, id);
    }

    public AdvertTarget getAdvertTargetById(Integer id) {
        return entityService.getById(AdvertTarget.class, id);
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

    public Collection<uk.co.alumeni.prism.dto.AdvertDTO> getAdvertList(OpportunitiesQueryDTO query, Collection<EntityOpportunityCategoryDTO<?>> advertDTOs) {
        TreeMap<String, uk.co.alumeni.prism.dto.AdvertDTO> adverts = Maps.newTreeMap();
        if (!advertDTOs.isEmpty()) {
            Map<Integer, Boolean> advertIndex = getRowsToReturn(advertDTOs, query.getOpportunityCategory(), query.getOpportunityTypes(), query.getLastSequenceIdentifier(),
                    ADVERT_LIST_PAGE_ROW_COUNT);

            advertDAO.getAdverts(query, advertIndex.keySet()).forEach(advert -> {
                Boolean recommended = BooleanUtils.toBoolean(advertIndex.get(advert.getAdvertId()));
                String sequenceIdentifier = (recommended ? 1 : 0) + advert.getSequenceIdentifier();
                advert.setSequenceIdentifier(sequenceIdentifier);
                advert.setRecommended(recommended);
                adverts.put(sequenceIdentifier, advert);
            });
        }

        return adverts.descendingMap().values();
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

    public Advert createAdvert(ResourceParentDTO resourceDTO, User user) {
        Advert advert = new Advert();
        advert.setUser(user);

        updateAdvert(advert, resourceDTO);
        entityService.save(advert);
        return advert;
    }

    public void updateResourceDetails(PrismScope resourceScope, Integer resourceId, ResourceParentDTO resourceDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateAdvert(advert, resourceDTO);
        updateAdvertVisibility(advert, resourceDTO);
        executeUpdate(resource, "COMMENT_UPDATED_ADVERT");
    }

    public void updateAdvert(Advert advert, ResourceParentDTO resourceDTO) {
        advert.setName(resourceDTO.getName());
        advert.setSummary(resourceDTO.getSummary());
        advert.setDescription(resourceDTO.getDescription());
        advert.setTelephone(resourceDTO.getTelephone());
        advert.setHomepage(resourceDTO.getHomepage());
    }

    public void updateFinancialDetail(Advert advert, AdvertFinancialDetailDTO payDTO) {
        if (payDTO == null) {
            advert.setPay(null);
        } else {
            AdvertFinancialDetail pay = advert.getPay();
            if (pay == null) {
                pay = new AdvertFinancialDetail();
                advert.setPay(pay);
            }

            pay.setInterval(payDTO.getInterval());
            pay.setHoursWeekMinimum(payDTO.getHoursWeekMinimum());
            pay.setHoursWeekMaximum(payDTO.getHoursWeekMaximum());
            pay.setOption(payDTO.getPaymentOption());
            pay.setCurrency(payDTO.getCurrency());
            pay.setMinimum(payDTO.getMinimum());
            pay.setMaximum(payDTO.getMaximum());
            updateFinancialDetailNormalization(advert);

            String benefitString = null;
            String benefitDescription = null;
            List<PrismAdvertBenefit> benefitList = payDTO.getBenefits();
            if (isNotEmpty(benefitList)) {
                benefitString = benefitList.stream().map(benefit -> benefit.name()).collect(Collectors.joining("|"));
                benefitDescription = payDTO.getBenefitsDescription();
            }

            pay.setBenefit(benefitString);
            pay.setBenefitDescription(benefitDescription);
        }
    }

    public void updateAdvertSettings(PrismScope resourceScope, Integer resourceId, AdvertSettingsDTO advertSettingsDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        advert.setApplyHomepage(advertSettingsDTO.getApplyHomepage());
        resourceService.setResourceConditions(resource, advertSettingsDTO.getConditions());

        if (advertSettingsDTO.getVisibility() != null) {
            updateAdvertVisibility(advert, advertSettingsDTO.getVisibility());
        }
        executeUpdate(resource, "COMMENT_UPDATED_SETTINGS");
    }

    public void updateAddress(Resource parentResource, Advert advert) {
        updateAddress(parentResource, advert, null);
    }

    public void updateAddress(Resource parentResource, Advert advert, AddressDTO addressDTO) {
        Address address = advert.getAddress();
        if (addressDTO != null) {
            setAdvertAddress(advert, addressDTO);
        } else if (address == null) {
            if (ResourceParent.class.isAssignableFrom(parentResource.getClass())) {
                address = getResourceAddress(parentResource);
                addressDTO = advertMapper.getAddressDTO(address);
                setAdvertAddress(advert, addressDTO);
            } else {
                throw new Error();
            }
        }
    }

    public void updateLocations(ResourceOpportunity resource, List<ResourceRelationDTO> locations) {
        Advert advert = resource.getAdvert();
        AdvertCategories categories = advert.getCategories();
        Set<AdvertLocation> advertLocations = categories.getLocations();
        if (CollectionUtils.isNotEmpty(locations)) {
            PrismResourceContext context = PrismScope.getResourceContexts(resource.getOpportunityCategories()).iterator().next();
            User user = resource.getUser();
            for (ResourceRelationDTO locationDTO : locations) {
                ResourceParent locationResource = resourceService.createResourceRelation(locationDTO, context, user);
                createAdvertLocation(advert, advertLocations, locationResource.getAdvert());
            }
        }
    }

    public void updateAdvertVisibility(Advert advert, ResourceParentDTO resourceDTO) {
        if (ResourceOpportunityDTO.class.isAssignableFrom(resourceDTO.getClass())) {
            ResourceOpportunityDTO opportunityDTO = (ResourceOpportunityDTO) resourceDTO;
            if (opportunityDTO.getAdvertVisibility() != null) {
                updateAdvertVisibility(advert, opportunityDTO.getAdvertVisibility());
            }
        }
    }

    public void updateAdvertVisibility(Advert advert, AdvertVisibilityDTO advertVisibilityDTO) {
        advert.setGloballyVisible(advertVisibilityDTO.getGloballyVisible());
        advert.setClosingDate(advertVisibilityDTO.getClosingDate());
        advertDAO.deleteCustomAdvertTargets(advert);
        List<Integer> customTargetIds = advertVisibilityDTO.getCustomTargets();
        if (isNotEmpty(customTargetIds)) {
            updateAdvertTargets(advert, customTargetIds);
        }
    }

    public void updateAdvertPayCurrency(List<Integer> adverts, String currency) {
        advertDAO.updateAdvertPayCurrency(adverts, currency);
    }

    public void updateCategories(PrismScope resourceScope, Integer resourceId, AdvertCategoriesDTO categoriesDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        updateCategories(resource.getAdvert(), categoriesDTO);
        executeUpdate(resource, "COMMENT_UPDATED_CATEGORY");
    }

    public AdvertTarget createAdvertTarget(ResourceCreationDTO resourceDTO, ResourceRelationCreationDTO targetDTO) {
        User user = userService.getCurrentUser();
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceDTO.getScope(), resourceDTO.getId());
        return createAdvertTarget(user, resource, targetDTO);
    }

    public AdvertTarget createAdvertTarget(ResourceParent resource, ResourceRelationCreationDTO targetDTO) {
        return createAdvertTarget(resource.getUser(), resource, targetDTO);
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
                invitationsSerial = prismJsonMappingUtils.writeValue(invitations);
            }

            if (isNotEmpty(connections)) {
                connectionsSerial = prismJsonMappingUtils.writeValue(connections);
            }

            AdvertTargetPending advertTargetPending = new AdvertTargetPending().withAdvert(resource.getAdvert()).withUser(user).withAdvertTargetInviteList(invitationsSerial)
                    .withAdvertTargetConnectList(connectionsSerial).withAdvertTargetMessage(targets.getMessage());
            entityService.save(advertTargetPending);
            return advertTargetPending;
        }

        return null;
    }

    public boolean acceptAdvertTarget(Integer advertTargetId, boolean accept) {
        return acceptAdvertTarget(getAdvertTargetById(advertTargetId), accept, true);
    }

    public void updateAdvertTarget(Integer advertTargetId, boolean severed) {
        User user = userService.getCurrentUser();
        AdvertTarget advertTarget = getAdvertTargetById(advertTargetId);

        Advert advert = advertTarget.getAdvert();
        Advert targetAdvert = advertTarget.getTargetAdvert();

        Integer advertId = advert.getId();
        Integer targetAdvertId = targetAdvert.getId();

        Set<String> properties = Sets.newHashSet();
        List<Advert> processedAdverts = Lists.newArrayList();
        if (isNotEmpty(getAdvertsForWhichUserHasRolesStrict(user, new String[] { "ADMINISTRATOR" }, newArrayList(advertId)))) {
            properties.add("advert");
            processedAdverts.add(advert);
        }

        if (isNotEmpty(getAdvertsForWhichUserHasRolesStrict(user, new String[] { "ADMINISTRATOR" }, newArrayList(targetAdvertId)))) {
            properties.add("targetAdvert");
            processedAdverts.add(targetAdvert);
        }

        advertDAO.updateAdvertTargetGroup(advertTarget, properties, severed);
        processedAdverts.stream().forEach(processedAdvert -> {
            ResourceParent resource = (ResourceParent) processedAdvert.getResource();
            executeUpdate(resource, "COMMENT_UPDATED_TARGET");
        });
    }

    public void updateCompetences(PrismScope resourceScope, Integer resourceId, List<AdvertCompetenceDTO> competencesDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateCompetences(advert, competencesDTO);
        executeUpdate(resource, "COMMENT_UPDATED_COMPETENCE");
    }

    public void updateFinancialDetailNormalization(Integer advertId) {
        updateFinancialDetailNormalization(getById(advertId));
    }

    public List<Integer> getAdvertsWithoutPayConversions(Institution institution) {
        List<Integer> adverts = Lists.newArrayList();
        Arrays.stream(opportunityScopes).forEach(scope -> adverts.addAll(advertDAO.getAdvertsWithoutPayConversions(institution, scope)));
        return adverts;
    }

    public List<Integer> getAdvertsWithElapsedPayConversions(LocalDate baseline) {
        List<Integer> adverts = Lists.newArrayList();
        Arrays.stream(opportunityScopes).forEach(scope -> adverts.addAll(advertDAO.getAdvertsWithElapsedPayConversions(scope, baseline)));
        return adverts;
    }

    public void setSequenceIdentifier(Advert advert, String prefix) {
        advert.setSequenceIdentifier(prefix + String.format("%010d", advert.getId()));
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

        Department department = resource.getDepartment();
        if (department != null) {
            connectAdverts.add(department.getAdvert().getId());
        }
        connectAdverts.add(resource.getInstitution().getAdvert().getId());

        User user = userService.getCurrentUser();
        PrismScopeCategory scopeCategory = advert.getResource().getResourceScope().getScopeCategory();

        boolean isOrganization = scopeCategory.equals(ORGANIZATION);
        List<Integer> manageAdverts = isOrganization ? getAdvertsForWhichUserCanManageConnections(user) : emptyList();

        Map<Integer, AdvertTargetDTO> advertTargets = Maps.newHashMap();
        String[] opportunityCategoriesSplit = resource.getOpportunityCategories().split("\\|");
        List<PrismOpportunityCategory> opportunityCategories = asList(opportunityCategoriesSplit).stream().map(PrismOpportunityCategory::valueOf).collect(toList());
        if (containsAny(asList(EXPERIENCE, WORK), opportunityCategories)) {
            for (PrismScope targetScope : organizationScopes) {
                advertDAO.getAdvertTargets(targetScope, "target.advert", "target.targetAdvert", user, connectAdverts, manageAdverts).forEach(at -> {
                    advertTargets.put(at.getOtherAdvertId(), at);
                });
            }
        }

        if (containsAny(asList(STUDY, PERSONAL_DEVELOPMENT), opportunityCategories)) {
            for (PrismScope targetScope : organizationScopes) {
                advertDAO.getAdvertTargets(targetScope, "target.targetAdvert", "target.advert", user, connectAdverts, manageAdverts).forEach(at -> {
                    advertTargets.put(at.getOtherAdvertId(), at);
                });
            }
        }

        boolean superAdmin = roleService.hasUserRole(systemService.getSystem(), user, SYSTEM_ADMINISTRATOR);
        advertTargets.values().stream()
                .filter(at -> (isOrganization ? manageAdverts : getAdvertsForWhichUserCanManageConnections(user)).contains(at.getThisAdvertId()) || superAdmin)
                .forEach(at -> at.setCanManage(true));

        if (!isOrganization) {
            Set<Integer> customDepartments = Sets.newHashSet();
            Set<Integer> customInstitutions = Sets.newHashSet();

            advertDAO.getCustomAdvertTargets(advert).forEach(customTarget -> {
                Resource customResource = customTarget.getAdvert().getResource();
                Resource customTargetResource = customTarget.getTargetAdvert().getResource();

                if (customResource.getResourceScope().getScopeCategory().equals(OPPORTUNITY)) {
                    reconcileCustomTargetResources(customTargetResource, customDepartments, customInstitutions);
                } else if (customTargetResource.getResourceScope().getScopeCategory().equals(OPPORTUNITY)) {
                    reconcileCustomTargetResources(customResource, customDepartments, customInstitutions);
                }
            });

            advertTargets.values().forEach(advertTarget -> {
                if (customDepartments.contains(advertTarget.getOtherDepartmentId()) || customInstitutions.contains(advertTarget.getOtherInstitutionId())) {
                    advertTarget.setSelected(true);
                }
            });
        }

        return newArrayList(advertTargets.values());
    }

    public List<AdvertTargetDTO> getAdvertTargetsReceived(User user) {
        List<Integer> connectAdverts = getAdvertsForWhichUserCanManageConnections(user);
        List<AdvertTargetDTO> advertTargets = Lists.newArrayList();
        for (PrismScope resourceScope : organizationScopes) {
            for (String advertReference : new String[] { "advert", "targetAdvert" }) {
                String targetAdvertReference = advertReference.equals("advert") ? "targetAdvert" : "advert";
                advertTargets.addAll(advertDAO.getAdvertTargetsReceived(resourceScope, "target." + targetAdvertReference, "target." + advertReference, user, connectAdverts));
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

    public Map<Integer, Integer> getCompetenceImportances(Advert advert) {
        Map<Integer, Integer> importances = Maps.newHashMap();
        advert.getCompetences().forEach(c -> {
            importances.put(c.getCompetence().getId(), c.getImportance());
        });
        return importances;
    }

    public Set<EntityOpportunityCategoryDTO<?>> getVisibleAdverts(User user, OpportunitiesQueryDTO query, PrismScope[] scopes) {
        PrismResourceContext context = query.getContext();
        PrismActionCondition actionCondition = context == APPLICANT ? ACCEPT_APPLICATION : ACCEPT_PROJECT;

        Integer advertId = query.getAdvertId();
        PrismScope resourceScope = query.getResourceScope();

        Set<Integer> nodeAdverts = Sets.newHashSet();
        HashMultimap<PrismScope, PrismState> states = HashMultimap.create();
        if (resourceScope != null) {
            Integer resourceId = query.getResourceId();
            stream(scopes).forEach(advertScope -> {
                List<PrismState> advertStates = stateService.getActiveResourceStates(advertScope);
                states.putAll(advertScope, advertStates);

                nodeAdverts.addAll(advertDAO.getAdvertsForEnclosingResource(resourceScope, resourceId, advertScope, advertStates));
                stream(organizationScopes).forEach(targeterScope -> {
                    if (advertScope.ordinal() > targeterScope.ordinal()) {
                        nodeAdverts.addAll(advertDAO.getAdvertsForTargetResource(targeterScope, resourceScope, resourceId, advertScope, advertStates));
                    }
                });
            });
        } else if (advertId != null) {
            nodeAdverts.add(advertId);
        }

        Set<EntityOpportunityCategoryDTO<?>> adverts = Sets.newTreeSet();
        Set<Integer> userAdverts = getUserAdverts(user, scopes);
        if (!(resourceScope != null && isEmpty(nodeAdverts) || (isTrue(query.getRecommendation()) && isEmpty(userAdverts)))) {
            for (PrismScope scope : scopes) {
                Collection<PrismState> advertStates = states.get(scope);
                advertStates = isEmpty(advertStates) ? stateService.getActiveResourceStates(scope) : advertStates;

                advertDAO.getVisibleAdverts(scope, advertStates, actionCondition, nodeAdverts, userAdverts, query).forEach(advert -> {
                    Integer visibleAdvertId = advert.getId();
                    boolean prioritize = userAdverts.contains(visibleAdvertId);
                    advert.setPrioritize(prioritize);
                    adverts.add(advert);
                });
            }
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
                for (PrismScope targeterScope : organizationScopes) {
                    if (targeterScope.ordinal() < resourceScope.ordinal()) {
                        ResourceParent targeterResource = (ResourceParent) getProperty(advert, targeterScope.getLowerCamelName());
                        if (targeterResource != null) {
                            for (PrismScope targetScope : organizationScopes) {
                                targetAdverts.addAll(advertDAO.getAdvertsTargetsForWhichUserCanEndorse(targeterResource.getAdvert(), user, resourceScope, targeterScope,
                                        targetScope, targeterEntities));
                            }
                        }
                    }
                }
            }

            targetAdverts.forEach(targetAdvert -> {
                createAdvertTarget(targetAdvert, partnershipState);
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
            List<ResourceRelationCreationDTO> invitations = prismJsonMappingUtils.readCollection(invitationsSerial, List.class, ResourceRelationCreationDTO.class);
            for (Iterator<ResourceRelationCreationDTO> iterator = invitations.iterator(); iterator.hasNext();) {
                ResourceRelationCreationDTO invitation = iterator.next();
                resourceService.inviteResourceRelation(advertTargetPending.getAdvert().getResource(), advertTargetPending.getUser(), invitation,
                        advertTargetPending.getAdvertTargetMessage());

                iterator.remove();
                advertTargetPending.setAdvertTargetInviteList(invitations.isEmpty() ? null : prismJsonMappingUtils.writeValue(invitations));
                return;
            }
        } else if (connectionsSerial != null) {
            List<ResourceRelationCreationDTO> connections = prismJsonMappingUtils.readCollection(connectionsSerial, List.class, ResourceRelationCreationDTO.class);
            for (Iterator<ResourceRelationCreationDTO> iterator = connections.iterator(); iterator.hasNext();) {
                ResourceRelationCreationDTO targetDTO = iterator.next();
                ResourceCreationDTO ResourceRelationCreationDTO = targetDTO.getResource().getResource();
                ResourceParent resourceTarget = (ResourceParent) resourceService.getById(ResourceRelationCreationDTO.getScope(), ResourceRelationCreationDTO.getId());
                createAdvertTarget(advertTargetPending.getAdvert().getResource(), advertTargetPending.getUser(), resourceTarget, targetDTO.getUser(),
                        ResourceRelationCreationDTO.getContext(), targetDTO.getMessage(), false);

                iterator.remove();
                advertTargetPending.setAdvertTargetConnectList(connections.isEmpty() ? null : prismJsonMappingUtils.writeValue(connections));
                return;
            }
        } else {
            entityService.delete(advertTargetPending);
        }
    }

    public List<Integer> getAdvertsForWhichUserCanManageConnections(User user) {
        return getAdvertsForWhichUserHasRolesStrict(user, new String[] { "ADMINISTRATOR" }, null);
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
            for (PrismScope targetScope : organizationScopes) {
                List<PrismRole> roles = values(PrismRole.class, targetScope, new String[] { "ADMINISTRATOR" });
                List<Integer> resources = resourceService.getResourcesForWhichUserHasRoles(user, roles);
                if (isNotEmpty(resources)) {
                    students.putAll(targetScope, userService.getUsersWithRoles(targetScope, resources, PrismRole.valueOf(targetScope.name() + "_STUDENT")));
                }

                if (targetScope.equals(DEPARTMENT)) {
                    roles = values(PrismRole.class, INSTITUTION, new String[] { "ADMINISTRATOR" });
                    resources = resourceService.getResourcesForWhichUserHasRoles(user, roles);
                    if (isNotEmpty(resources)) {
                        students.putAll(targetScope, userService.getUsersWithRoles(targetScope, INSTITUTION, resources, PrismRole.valueOf(targetScope.name() + "_STUDENT")));
                    }
                }
            }

            for (PrismScope targeterScope : organizationScopes) {
                for (PrismScope targetScope : organizationScopes) {
                    targeterEntities.addAll(applicationService.getApplicationsForTargets(user, targeterScope, targetScope, students.get(targetScope)));
                }
            }
        } else if (opportunityCategory) {
            for (PrismScope targetScope : organizationScopes) {
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
        stream(organizationScopes).forEach(memberScope -> {
            Collection<PrismState> memberStates = states.get(memberScope);
            memberStates = isEmpty(memberStates) ? stateService.getActiveResourceStates(memberScope) : memberStates;
            memberAdverts.addAll(advertDAO.getUserAdverts(user, memberScope, memberStates));
        });

        if (isNotEmpty(memberAdverts)) {
            stream(displayScopes).forEach(displayScope -> {
                stream(organizationScopes).forEach(targetScope -> {
                    Collection<PrismState> advertStates = states.get(displayScope);
                    advertStates = isEmpty(advertStates) ? stateService.getActiveResourceStates(displayScope) : advertStates;
                    userAdverts.addAll(advertDAO.getUserTargetAdverts(user, targetScope, displayScope, advertStates));
                });
            });

            stream(displayScopes).forEach(displayScope -> {
                if (displayScope.getScopeCategory().equals(OPPORTUNITY)) {
                    stream(organizationScopes).forEach(targeterScope -> {
                        stream(organizationScopes).forEach(targetScope -> {
                            Collection<PrismState> advertStates = states.get(displayScope);
                            advertStates = isEmpty(advertStates) ? stateService.getActiveResourceStates(displayScope) : advertStates;
                            userAdverts.addAll(advertDAO.getUserTargetAdverts(user, targeterScope, targetScope, displayScope, advertStates));
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
        if (isNotEmpty(userAdverts)) {
            stream(displayScopes).forEach(scope -> adverts.addAll(advertDAO.getUserAdverts(scope, stateService.getActiveResourceStates(scope), userAdverts)));
        }

        return adverts;
    }

    public List<AdvertCategoryDTO> getAdvertsForWhichUserHasRolesStrict(User user, String[] roleExtensions) {
        return getAdvertsForWhichUserHasRoles(user, roleExtensions, organizationScopes, null, true, AdvertCategoryDTO.class);
    }

    public List<Integer> getAdvertsForWhichUserHasRolesStrict(User user, String[] roleExtensions, Collection<Integer> advertIds) {
        return getAdvertsForWhichUserHasRoles(user, roleExtensions, advertIds, true, Integer.class);
    }

    public <T> List<T> getAdvertsForWhichUserHasRoles(User user, String[] roleExtensions, Collection<Integer> advertIds, boolean strict, Class<T> responseClass) {
        return getAdvertsForWhichUserHasRoles(user, roleExtensions, advertScopes, advertIds, strict, responseClass);
    }

    public List<AdvertTarget> getAdvertTargetsForAdverts(Collection<Integer> adverts) {
        return advertDAO.getAdvertTargetsForAdverts(adverts);
    }

    public Map<Integer, AdvertUserDTO> getAdvertUsers(Collection<Integer> adverts) {
        Map<Integer, AdvertUserDTO> advertUsers = Maps.newHashMap();
        if (isNotEmpty(adverts)) {
            for (PrismScope organizationScope : organizationScopes) {
                for (AdvertUserDTO advertUserDTO : advertDAO.getAdvertUsers(organizationScope, adverts)) {
                    advertUsers.put(advertUserDTO.getAdvertId(), advertUserDTO);
                }
            }
        }
        return advertUsers;
    }

    public void createAdvertLocation(Advert advert, Advert locationAdvert) {
        AdvertCategories advertCategories = advert.getCategories();
        if (advertCategories == null) {
            advertCategories = new AdvertCategories();
            advert.setCategories(advertCategories);
        }
        createAdvertLocation(advert, advertCategories.getLocations(), locationAdvert);
    }

    public Set<Advert> getPossibleAdvertLocations(Advert advert) {
        Set<Advert> locations = Sets.newTreeSet();
        advert.getParentResources().stream().forEach(resource -> locations.add(resource.getAdvert()));
        locations.addAll(advertDAO.getPossibleAdvertLocations(advert, locations));
        return locations;
    }

    public Set<AdvertLocationAddressPartSummaryDTO> getAdvertLocationSummaries(String searchTerm) {
        Set<Integer> userAdverts = null;
        User user = userService.getCurrentUser();
        if (user != null) {
            userAdverts = getUserAdverts(user, opportunityScopes);
        }

        Map<Integer, AdvertLocationAddressPartSummaryDTO> summaries = Maps.newHashMap();
        for (PrismScope opportunityScope : opportunityScopes) {
            advertDAO.getAdvertLocationSummaries(opportunityScope, userAdverts, searchTerm).forEach(summary -> {
                Integer id = summary.getId();
                AdvertLocationAddressPartSummaryDTO existingSummary = summaries.get(id);
                if (existingSummary == null) {
                    summaries.put(id, summary);
                } else {
                    existingSummary.setAdvertCount(existingSummary.getAdvertCount() + summary.getAdvertCount());
                }
            });
        }

        return Sets.newTreeSet(summaries.values());
    }

    private <T> List<T> getAdvertsForWhichUserHasRoles(User user, String[] roleExtensions, PrismScope[] advertScopes, Collection<Integer> advertIds, boolean strict,
            Class<T> responseClass) {
        List<T> adverts = Lists.newArrayList();
        if (user != null) {
            for (PrismScope scope : advertScopes) {
                List<PrismState> states = stateService.getActiveResourceStates(scope);
                roleExtensions = getFilteredRoleExtensions(scope, roleExtensions);
                if (roleExtensions.length > 0) {
                    adverts.addAll(advertDAO.getAdvertsForWhichUserHasRoles(user, scope, states, roleExtensions, advertIds, strict, responseClass));
                }
            }
        }
        return adverts;
    }

    private String[] getFilteredRoleExtensions(PrismScope scope, String[] roleExtensions) {
        String scopeName = scope.name();
        List<String> permittedRoleExtensions = Lists.newArrayList();
        roleService.getRolesByScope(scope).forEach(role -> {
            String roleName = role.name();
            permittedRoleExtensions.add(roleName.replace(scopeName + "_", ""));
        });

        List<String> filteredRoleExtensions = stream(roleExtensions).filter(permittedRoleExtensions::contains).collect(toList());
        return filteredRoleExtensions.toArray(new String[filteredRoleExtensions.size()]);
    }

    private AdvertTarget createAdvertTarget(User user, ResourceParent resource, ResourceRelationCreationDTO targetDTO) {
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

    private AdvertTarget createAdvertTarget(Advert advert, User user, Advert advertTarget, User userTarget, Advert advertAccept, User userAccept, String message) {
        AdvertTarget targetAdmin = createAdvertTarget(advert, user, advertTarget, userTarget, advertAccept, null, ENDORSEMENT_PENDING);

        AdvertTarget targetUserAccept = null;
        if (userTarget != null) {
            targetUserAccept = createAdvertTarget(advert, user, advertTarget, userTarget, advertAccept, userAccept, ENDORSEMENT_PENDING);
        }

        Invitation invitation = invitationService.createInvitation(targetAdmin.getOtherUser(), message);

        if (!acceptAdvertTarget(targetAdmin, true, false)) {
            targetAdmin.setInvitation(invitation);
        }

        if (!(targetUserAccept == null || acceptAdvertTarget(targetUserAccept, true, false))) {
            targetUserAccept.setInvitation(invitation);
        }

        return targetUserAccept == null ? targetAdmin : targetUserAccept;
    }

    private AdvertTarget createAdvertTarget(Advert advert, User advertUser, Advert targetAdvert, User targetAdvertUser, Advert acceptAdvert, User acceptAdvertUser,
            PrismPartnershipState partnershipState) {
        return entityService.getOrCreate(new AdvertTarget().withAdvert(advert).withAdvertUser(advertUser).withAdvertSevered(false).withTargetAdvert(targetAdvert)
                .withTargetAdvertUser(targetAdvertUser).withTargetAdvertSevered(false).withAcceptAdvert(acceptAdvert).withAcceptAdvertUser(acceptAdvertUser)
                .withPartnershipState(partnershipState));
    }

    private AdvertTarget createAdvertTarget(Advert targetAdvert, PrismPartnershipState partnershipState) {
        return createAdvertTarget(targetAdvert, targetAdvert, targetAdvert, partnershipState);
    }

    private AdvertTarget createAdvertTarget(Advert advert, Advert targetAdvert, Advert acceptAdvert, PrismPartnershipState partnershipState) {
        AdvertTarget advertTarget = entityService.createOrUpdate(new AdvertTarget().withAdvert(advert).withAdvertSevered(false).withTargetAdvert(targetAdvert)
                .withTargetAdvertSevered(false).withAcceptAdvert(acceptAdvert).withPartnershipState(partnershipState));
        setAdvertTargetSequenceIdentifier(advertTarget, partnershipState, now());
        return advertTarget;
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

            Set<AdvertTheme> themes = categories.getThemes();
            for (AdvertTheme advertTheme : themes) {
                Theme theme = advertTheme.getTheme();
                theme.setAdoptedCount(theme.getAdoptedCount() - 1);
                entityService.delete(advertTheme);
            }

            advertDAO.deleteAdvertAttributes(advert, AdvertLocation.class);
            categories.getLocations().clear();

            entityService.flush();
        }

        Set<AdvertIndustry> advertIndustries = categories.getIndustries();
        categoriesDTO.getIndustries().stream().forEach(industryDTO -> {
            AdvertIndustry advertIndustry = new AdvertIndustry().withAdvert(advert).withIndustry(industryDTO);
            entityService.save(advertIndustry);
            advertIndustries.add(advertIndustry);
        });

        Set<AdvertFunction> advertFunctions = categories.getFunctions();
        categoriesDTO.getFunctions().stream().forEach(functionDTO -> {
            AdvertFunction advertFunction = new AdvertFunction().withAdvert(advert).withFunction(functionDTO);
            entityService.save(advertFunction);
            advertFunctions.add(advertFunction);
        });

        Set<AdvertTheme> advertThemes = categories.getThemes();
        categoriesDTO.getThemes().stream().forEach(themeDTO -> {
            Theme theme = tagService.createOrUpdateTag(Theme.class, themeDTO);
            AdvertTheme advertTheme = new AdvertTheme();
            advertTheme.setAdvert(advert);
            advertTheme.setTheme(theme);
            entityService.save(advertTheme);
            advertThemes.add(advertTheme);
        });

    }

    private boolean acceptAdvertTarget(AdvertTarget advertTarget, boolean accept, boolean notify) {
        boolean performed = false;
        if (advertTarget != null) {
            User user = userService.getCurrentUser();

            Set<PrismPartnershipState> oldPartnershipStates = Sets.newHashSet();
            if (user != null) {
                PrismPartnershipState partnershipState = accept ? ENDORSEMENT_PROVIDED : ENDORSEMENT_REVOKED;

                DateTime baseline = now();
                Integer acceptAdvertId = advertTarget.getAcceptAdvert().getId();
                if (isNotEmpty(getAdvertsForWhichUserHasRolesStrict(user, new String[] { "ADMINISTRATOR" }, newArrayList(acceptAdvertId)))) {
                    advertDAO.getAdvertTargetAdmin(advertTarget).stream().forEach(targetAdmin -> {
                        oldPartnershipStates.add(targetAdmin.getPartnershipState());
                        setAdvertTargetPartnershipState(targetAdmin, partnershipState, baseline, accept);
                    });
                    performed = true;
                }

                AdvertTarget targetUserAccept = advertDAO.getAdvertTargetAccept(advertTarget, user);
                if (targetUserAccept != null) {
                    oldPartnershipStates.add(targetUserAccept.getPartnershipState());
                    setAdvertTargetPartnershipState(targetUserAccept, partnershipState, baseline, accept);
                    performed = true;
                }

                if (performed && accept && notify && !oldPartnershipStates.contains(ENDORSEMENT_PROVIDED)) {
                    notificationService.sendConnectionNotification(userService.getCurrentUser(), advertTarget.getOtherUser(), advertTarget);
                }
            }
        }

        return performed;
    }

    private void updateAdvertTargets(Advert advert, List<Integer> customTargetIds) {
        Department department = advert.getDepartment();
        Institution institution = advert.getInstitution();

        Advert departmentAdvert = department == null ? null : department.getAdvert();
        Advert institutionAdvert = institution == null ? null : institution.getAdvert();

        Integer departmentAdvertId = departmentAdvert == null ? null : departmentAdvert.getId();
        Integer institutionAdvertId = institutionAdvert == null ? null : institutionAdvert.getId();

        List<AdvertTarget> customTargets = advertDAO.getActiveAdvertTargets(customTargetIds);
        customTargets.stream().forEach(customTarget -> {
            Advert customAdvert = customTarget.getAdvert();
            Advert customTargetAdvert = customTarget.getTargetAdvert();
            Advert customAcceptAdvert = customTarget.getAcceptAdvert();

            Integer customAdvertId = customAdvert.getId();
            Integer customTargetAdvertId = customTargetAdvert.getId();

            if (customAdvertId.equals(departmentAdvertId) || customAdvertId.equals(institutionAdvertId)) {
                customAdvert = advert;
            } else if (customTargetAdvertId.equals(departmentAdvertId) || customTargetAdvertId.equals(institutionAdvertId)) {
                customTargetAdvert = advert;
            }

            createAdvertTarget(customAdvert, customTargetAdvert, customAcceptAdvert, ENDORSEMENT_PROVIDED);
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
            Competence competence = tagService.createOrUpdateTag(Competence.class, competenceDTO);
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

    private void updateFinancialDetailNormalization(Advert advert) {
        AdvertFinancialDetail pay = advert.getPay();
        BigDecimal minimum = pay.getMinimum();
        BigDecimal maximum = pay.getMaximum();

        if (!(minimum == null || maximum == null)) {
            BigDecimal durationAsHours = new BigDecimal(getDurationUnitAsHours(pay.getInterval()));
            BigDecimal minimumNormalized = pay.getMinimum().divide(durationAsHours, 2, HALF_UP);
            BigDecimal maximumNormalized = pay.getMaximum().divide(durationAsHours, 2, HALF_UP);

            BigDecimal minimumNormalizedHour = minimumNormalized;
            BigDecimal maximumNormalizedHour = maximumNormalized;
            if (!pay.getInterval().equals(HOUR)) {
                BigDecimal workHoursInWeek = new BigDecimal(WORK_DAYS_IN_WEEK * WORK_HOURS_IN_DAY);
                BigDecimal workHoursInWeekAdjustment = workHoursInWeek.divide(new BigDecimal(pay.getHoursWeekMaximum()), 2, HALF_UP);

                minimumNormalizedHour = minimumNormalizedHour.multiply(workHoursInWeekAdjustment).setScale(2, HALF_UP);
                maximumNormalizedHour = maximumNormalizedHour.multiply(workHoursInWeekAdjustment).setScale(2, HALF_UP);
            }

            String currency = pay.getCurrency();
            String currencyNormalized = advert.getResourceParent().getAdvert().getAddress().getDomicile().getCurrency();
            if (!currency.equals(currencyNormalized)) {
                try {
                    LocalDate baseline = LocalDate.now();
                    BigDecimal conversionRate = getExchangeRate(currency, currencyNormalized, baseline);
                    minimumNormalized = minimumNormalized.multiply(conversionRate).setScale(2, HALF_UP);
                    maximumNormalized = maximumNormalized.multiply(conversionRate).setScale(2, HALF_UP);
                    minimumNormalizedHour = minimumNormalizedHour.multiply(conversionRate).setScale(2, HALF_UP);
                    maximumNormalizedHour = maximumNormalizedHour.multiply(conversionRate).setScale(2, HALF_UP);
                    pay.setLastConversionDate(baseline);
                } catch (Exception e) {
                    logger.error("Problem performing currency conversion", e);
                }
            }

            pay.setMinimumNormalized(minimumNormalized);
            pay.setMaximumNormalized(maximumNormalized);
            pay.setMinimumNormalizedHour(minimumNormalizedHour);
            pay.setMaximumNormalizedHour(maximumNormalizedHour);
        }
    }

    private BigDecimal getExchangeRate(String currency, String currencyNormalized, LocalDate baseline) throws IOException {
        exchangeRates.keySet().stream().filter(day -> day.isBefore(baseline)).forEach(exchangeRates::remove);

        String pair = currency + currencyNormalized;
        Map<String, BigDecimal> todaysRates = exchangeRates.get(baseline);

        BigDecimal todaysRate;
        if (todaysRates != null) {
            todaysRate = todaysRates.get(pair);
            if (todaysRate != null) {
                return todaysRate;
            }
        }

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

        return todaysRate;
    }

    private void executeUpdate(ResourceParent resource, String message) {
        resourceService.executeUpdate(resource, userService.getCurrentUser(), PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_" + message));
    }

    private void setAdvertAddress(Advert advert, AddressDTO addressDTO) {
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

    private void setAdvertTargetPartnershipState(AdvertTarget advertTarget, PrismPartnershipState partnershipState, DateTime baseline, boolean activateResource) {
        advertTarget.setPartnershipState(partnershipState);
        setAdvertTargetSequenceIdentifier(advertTarget, partnershipState, baseline);

        if (activateResource) {
            resourceService.activateResource(systemService.getSystem().getUser(), advertTarget.getOtherAdvert().getResource());
        }
    }

    private void setAdvertTargetSequenceIdentifier(AdvertTarget advertTarget, PrismPartnershipState partnershipState, DateTime baseline) {
        if (partnershipState.equals(ENDORSEMENT_PROVIDED)) {
            advertTarget.setAcceptedTimestamp(baseline);
            activityService.setSequenceIdentifier(advertTarget, baseline);
        }
    }

    private void reconcileCustomTargetResources(Resource customResource, Set<Integer> customDepartments, Set<Integer> customInstitutions) {
        Department customDepartment = customResource.getDepartment();
        Institution customInstitution = customResource.getInstitution();

        if (customDepartment != null) {
            customDepartments.add(customDepartment.getId());
        } else if (customInstitution != null) {
            customInstitutions.add(customInstitution.getId());
        }
    }

    private boolean createAdvertLocation(Advert advert, Set<AdvertLocation> advertLocations, Advert locationAdvert) {
        return advertLocations.add(entityService.getOrCreate(new AdvertLocation().withAdvert(advert).withLocationAdvert(locationAdvert)));
    }

}
