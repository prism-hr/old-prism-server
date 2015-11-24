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
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.dao.WorkflowDAO.targetScopes;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.MONTH;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.YEAR;
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
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;
import static uk.co.alumeni.prism.utils.PrismEnumUtils.values;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.setProperty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
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

import uk.co.alumeni.prism.dao.AdvertDAO;
import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertCategories;
import uk.co.alumeni.prism.domain.advert.AdvertClosingDate;
import uk.co.alumeni.prism.domain.advert.AdvertCompetence;
import uk.co.alumeni.prism.domain.advert.AdvertFinancialDetail;
import uk.co.alumeni.prism.domain.advert.AdvertFunction;
import uk.co.alumeni.prism.domain.advert.AdvertIndustry;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.advert.AdvertTargetPending;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
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
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.AdvertApplicationSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertCategoryDTO;
import uk.co.alumeni.prism.dto.AdvertTargetDTO;
import uk.co.alumeni.prism.dto.AdvertUserDTO;
import uk.co.alumeni.prism.dto.EntityOpportunityFilterDTO;
import uk.co.alumeni.prism.dto.json.ExchangeRateLookupResponseDTO;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.OpportunitiesQueryDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCategoriesDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertClosingDateDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCompetenceDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertDetailsDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertFinancialDetailDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertFinancialDetailDTO.AdvertFinancialDetailPayDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceConnectionInvitationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceConnectionInvitationsDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.rest.representation.CompetenceRepresentation;
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

    public AdvertClosingDate getClosingDateById(Integer id) {
        return entityService.getById(AdvertClosingDate.class, id);
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

    public List<uk.co.alumeni.prism.dto.AdvertDTO> getAdvertList(OpportunitiesQueryDTO query, Collection<Integer> advertIds) {
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

    public void updateCustomAdvertTargets(Advert advert, AdvertDTO advertDTO) {
        advertDAO.deleteCustomAdvertTargets(advert);
        List<Integer> customTargetIds = advertDTO.getCustomTargets();
        if (isNotEmpty(customTargetIds)) {
            updateAdvertTargets(advert, customTargetIds);
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

    public boolean acceptAdvertTarget(AdvertTarget advertTarget, boolean accept, boolean notify) {
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

    public void refreshClosingDate(Integer advertId) {
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
            for (PrismScope targetScope : targetScopes) {
                advertDAO.getAdvertTargets(targetScope, "target.advert", "target.targetAdvert", user, connectAdverts, manageAdverts).forEach(at -> {
                    advertTargets.put(at.getOtherAdvertId(), at);
                });
            }
        }

        if (containsAny(asList(STUDY, PERSONAL_DEVELOPMENT), opportunityCategories)) {
            for (PrismScope targetScope : targetScopes) {
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
        for (PrismScope resourceScope : targetScopes) {
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
                stream(targetScopes).forEach(targeterScope -> {
                    if (advertScope.ordinal() > targeterScope.ordinal()) {
                        nodeAdverts.addAll(advertDAO.getAdvertsForTargetResource(targeterScope, resourceScope, resourceId, advertScope, advertStates));
                    }
                });
            });
        } else if (advertId != null) {
            nodeAdverts.add(advertId);
        }

        Set<EntityOpportunityFilterDTO> adverts = Sets.newHashSet();
        Set<Integer> userAdverts = getUserAdverts(user, scopes);
        if (!(resourceScope != null && isEmpty(nodeAdverts) || (isTrue(query.getRecommendation()) && isEmpty(userAdverts)))) {
            for (PrismScope scope : scopes) {
                Collection<PrismState> advertStates = states.get(scope);
                advertStates = isEmpty(advertStates) ? stateService.getActiveResourceStates(scope) : advertStates;
                adverts.addAll(advertDAO.getVisibleAdverts(scope, advertStates, actionCondition, nodeAdverts, userAdverts, query));
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
            for (PrismScope targetScope : targetScopes) {
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
            stream(displayScopes).forEach(displayScope -> {
                stream(targetScopes).forEach(targetScope -> {
                    Collection<PrismState> advertStates = states.get(displayScope);
                    advertStates = isEmpty(advertStates) ? stateService.getActiveResourceStates(displayScope) : advertStates;
                    userAdverts.addAll(advertDAO.getUserTargetAdverts(user, targetScope, displayScope, advertStates));
                });
            });

            stream(displayScopes).forEach(displayScope -> {
                if (displayScope.getScopeCategory().equals(OPPORTUNITY)) {
                    stream(targetScopes).forEach(targeterScope -> {
                        stream(targetScopes).forEach(targetScope -> {
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
        return getAdvertsForWhichUserHasRoles(user, roleExtensions, targetScopes, null, true, AdvertCategoryDTO.class);
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
            for (PrismScope targetScope : targetScopes) {
                for (AdvertUserDTO advertUserDTO : advertDAO.getAdvertUsers(targetScope, adverts)) {
                    advertUsers.put(advertUserDTO.getAdvertId(), advertUserDTO);
                }
            }
        }
        return advertUsers;
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

}
