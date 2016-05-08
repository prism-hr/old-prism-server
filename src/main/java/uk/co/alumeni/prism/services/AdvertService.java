package uk.co.alumeni.prism.services;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uk.co.alumeni.prism.dao.AdvertDAO;
import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.Theme;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.advert.*;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.*;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.*;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.*;
import uk.co.alumeni.prism.dto.json.ExchangeRateLookupResponseDTO;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.OpportunitiesQueryDTO;
import uk.co.alumeni.prism.rest.dto.TagDTO;
import uk.co.alumeni.prism.rest.dto.advert.*;
import uk.co.alumeni.prism.rest.dto.resource.*;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertThemeRepresentation;
import uk.co.alumeni.prism.utils.PrismJsonMappingUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.*;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.apache.commons.lang3.ObjectUtils.compare;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.co.alumeni.prism.PrismConstants.*;
import static uk.co.alumeni.prism.dao.WorkflowDAO.*;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.HOUR;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.getDurationUnitAsHours;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.*;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.EMPLOYER;
import static uk.co.alumeni.prism.domain.definitions.PrismRoleContext.VIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.getResourceContexts;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.*;
import static uk.co.alumeni.prism.utils.PrismListUtils.getRowsToReturn;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;

@Service
@Transactional
public class AdvertService {

    private static final Logger logger = getLogger(AdvertService.class);

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
    private ScopeService scopeService;

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
    private UserActivityCacheService userActivityCacheService;

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
            Map<Integer, BigDecimal> advertIndex = getRowsToReturn(advertDTOs, query.getOpportunityCategory(), query.getOpportunityTypes(),
                    query.getLastSequenceIdentifier(), query.getMaxAdverts());

            Set<Integer> advertIds = advertIndex.keySet();
            if (CollectionUtils.isNotEmpty(advertIds)) {
                BigDecimal priority = new BigDecimal(1);
                advertDAO.getAdverts(query, advertIndex.keySet()).forEach(advert -> {
                    Boolean recommended = advertIndex.get(advert.getAdvertId()).compareTo(priority) == 0;
                    String sequenceIdentifier = (recommended ? 1 : 0) + advert.getSequenceIdentifier();
                    advert.setSequenceIdentifier(sequenceIdentifier);
                    advert.setRecommended(recommended);
                    adverts.put(sequenceIdentifier, advert);
                });
            }
        }

        return adverts.descendingMap().values();
    }

    public List<Advert> getBadgeAdverts(ResourceParent parentResource, int count){
        return advertDAO.getBadgeAdverts(parentResource, count);
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

    public LinkedHashMultimap<Integer, PrismAdvertIndustry> getAdvertIndustries(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, PrismAdvertIndustry> industries = LinkedHashMultimap.create();
        if (isNotEmpty(resourceIds)) {
            advertDAO.getAdvertIndustries(resourceScope, resourceIds).forEach(industry -> {
                industries.put(industry.getAdvertId(), industry.getIndustry());
            });
        }
        return industries;
    }

    public LinkedHashMultimap<Integer, PrismAdvertFunction> getAdvertFunctions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, PrismAdvertFunction> functions = LinkedHashMultimap.create();
        if (isNotEmpty(resourceIds)) {
            advertDAO.getAdvertFunctions(resourceScope, resourceIds).forEach(function -> {
                functions.put(function.getAdvertId(), function.getFunction());
            });
        }
        return functions;
    }

    public LinkedHashMultimap<Integer, String> getAdvertThemes(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, String> themes = LinkedHashMultimap.create();
        if (isNotEmpty(resourceIds)) {
            advertDAO.getAdvertThemes(resourceScope, resourceIds).forEach(theme -> {
                themes.put(theme.getAdvertId(), theme.getTheme());
            });
        }
        return themes;
    }

    public LinkedHashMultimap<Integer, String> getAdvertLocations(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, String> locations = LinkedHashMultimap.create();
        if (isNotEmpty(resourceIds)) {
            advertDAO.getAdvertLocations(resourceScope, resourceIds).forEach(location -> {
                String[] partsArray = location.getLocation().split("\\|");

                int counter = 0;
                int element = partsArray.length;
                List<String> partsList = newLinkedList();
                while (counter < ADDRESS_LOCATION_PRECISION && element > 0) {
                    partsList.add(partsArray[counter]);
                    counter++;
                    element--;
                }

                locations.put(location.getAdvertId(), Joiner.on(COMMA + SPACE).join(Lists.reverse(partsList)));
            });
        }
        return locations;
    }

    public LinkedHashMultimap<Integer, PrismStudyOption> getAdvertStudyOptions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        LinkedHashMultimap<Integer, PrismStudyOption> studyOptions = LinkedHashMultimap.create();
        if (isNotEmpty(resourceIds)) {
            advertDAO.getAdvertStudyOptions(resourceScope, resourceIds).forEach(studyOption -> {
                studyOptions.put(studyOption.getAdvertId(), studyOption.getStudyOption());
            });
        }
        return studyOptions;
    }

    public Advert createAdvert(ResourceParentDTO resourceDTO, Resource parentResource, User user) {
        Advert advert = new Advert();
        advert.setUser(user);
        updateAdvert(advert, resourceDTO);

        PrismScope resourceScope = resourceDTO.getScope();
        if (resourceScope.getScopeCategory().equals(OPPORTUNITY)) {
            AdvertCategories advertCategories = advert.getCategories();
            if (advertCategories == null) {
                advertCategories = new AdvertCategories();
                advert.setCategories(advertCategories);
            }

            advertCategories.getLocations().add(new AdvertLocation().withAdvert(advert).withLocationAdvert(parentResource.getAdvert()));
            updateFinancialDetail(advert, ((ResourceOpportunityDTO) resourceDTO).getFinancialDetail(), parentResource.getInstitution());
        } else {
            advert.setGloballyVisible(resourceScope.isDefaultShared());
            if (resourceScope.equals(DEPARTMENT)) {
                updateAddress(parentResource, advert);
            } else {
                updateAddress(parentResource, advert, ((InstitutionDTO) resourceDTO).getAddress());
            }
        }

        advert.setSubmitted(false);
        advert.setPublished(false);
        return advert;
    }

    public void persistAdvert(ResourceParent resource, Advert advert) {
        advert.setResource(resource);
        advert.setScope(scopeService.getById(resource.getResourceScope()));

        Address address = advert.getAddress();
        advert.setAddress(null);
        entityService.save(advert);

        AdvertCategories categories = advert.getCategories();
        if (categories != null) {
            categories.getLocations().stream().forEach(entityService::getOrCreate);
        }

        if (address != null) {
            addressService.persistAndGeocodeAddress(address, advert.getName());
            advert.setAddress(address);
        }

        entityService.flush();
    }

    public void updateResourceDetails(PrismScope resourceScope, Integer resourceId, ResourceParentDTO resourceDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateAdvert(advert, resourceDTO);
        executeUpdate(resource, "COMMENT_UPDATED_ADVERT");
    }

    public void updateAdvert(Advert advert, ResourceParentDTO resourceDTO) {
        advert.setName(resourceDTO.getName());
        advert.setSummary(resourceDTO.getSummary());
        advert.setDescription(resourceDTO.getDescription());
        advert.setTelephone(resourceDTO.getTelephone());
        advert.setHomepage(resourceDTO.getHomepage());
    }

    public void updateDuration(Advert advert, Integer durationMinimum, Integer durationMaximum) {
        if (!(durationMinimum == null && durationMaximum == null)) {
            durationMinimum = durationMinimum == null ? durationMaximum : durationMinimum;
            durationMaximum = durationMaximum == null ? durationMinimum : durationMaximum;

            advert.setDurationMinimum(durationMinimum);
            advert.setDurationMaximum(durationMaximum);
        }
    }

    public void updateFinancialDetail(Advert advert, AdvertFinancialDetailDTO payDTO, Institution institution) {
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
            updateFinancialDetailNormalization(advert, institution);

            String benefitString = null;
            String benefitDescription = null;
            List<PrismAdvertBenefit> benefitList = payDTO.getBenefits();
            if (isNotEmpty(benefitList)) {
                benefitString = benefitList.stream().map(Enum::name).collect(Collectors.joining("|"));
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

        ActionOutcomeDTO actionOutcome = executeUpdate(resource, "COMMENT_UPDATED_SETTINGS");

        DateTime baseline = actionOutcome.getComment().getSubmittedTimestamp();
        if (!(baseline == null || advertSettingsDTO.getVisibility() == null)) {
            updateAdvertVisibility(userService.getCurrentUser(), advert, advertSettingsDTO.getVisibility(), baseline);
        }

        resourceService.setResourceAdvertIncompleteSection(resource);
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
        advertDAO.deleteAdvertAttributes(advert, AdvertLocation.class);

        AdvertCategories categories = advert.getCategories();
        Set<AdvertLocation> advertLocations = categories.getLocations();
        if (isNotEmpty(locations)) {
            PrismResourceContext context = getResourceContexts(resource.getOpportunityCategories()).iterator().next();
            User user = resource.getUser();
            for (ResourceRelationDTO locationDTO : locations) {
                ResourceParent locationResource = resourceService.createResourceRelation(locationDTO, context, user);
                persistAdvertLocation(advert, advertLocations, locationResource.getAdvert());
            }
        }

        executeUpdate(resource, "COMMENT_UPDATED_LOCATION");
    }

    public void updateAdvertVisibility(User currentUser, Advert advert, ResourceParentDTO resourceDTO, DateTime baseline) {
        if (ResourceOpportunityDTO.class.isAssignableFrom(resourceDTO.getClass())) {
            ResourceOpportunityDTO opportunityDTO = (ResourceOpportunityDTO) resourceDTO;
            if (opportunityDTO.getAdvertVisibility() != null) {
                updateAdvertVisibility(currentUser, advert, opportunityDTO.getAdvertVisibility(), baseline);
            }
        } else {
            ResourceRelationCreationDTO target = resourceDTO.getTarget();
            if (target != null) {
                createAdvertTarget(currentUser, advert.getResource(), target, baseline);
            }

            Integer targetInvitation = resourceDTO.getTargetInvitation();
            if (targetInvitation != null) {
                acceptAdvertTarget(targetInvitation, true);
            }
        }
    }

    public void updateAdvertVisibility(User currentUser, Advert advert, AdvertVisibilityDTO advertVisibilityDTO, DateTime baseline) {
        advert.setGloballyVisible(advertVisibilityDTO.getGloballyVisible());

        LocalDate closingDate = advertVisibilityDTO.getClosingDate();
        advert.setClosingDate(closingDate);
        advert.getResource().setDueDate(closingDate);

        advertDAO.deleteAdvertTargets(advert);
        List<Integer> customTargetIds = advertVisibilityDTO.getCustomTargets();
        if (isNotEmpty(customTargetIds)) {
            updateAdvertTargets(currentUser, advert, customTargetIds, baseline);
        }
    }

    public void updateAdvertPayCurrency(List<Integer> adverts, String currency) {
        advertDAO.updateAdvertPayCurrency(adverts, currency);
    }

    public void updateCategories(PrismScope resourceScope, Integer resourceId, AdvertCategoriesDTO categoriesDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);

        Advert advert = resource.getAdvert();
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
        List<PrismAdvertIndustry> industries = categoriesDTO.getIndustries();
        if (isNotEmpty(industries)) {
            industries.stream().forEach(industry -> {
                AdvertIndustry advertIndustry = new AdvertIndustry().withAdvert(advert).withIndustry(industry);
                entityService.save(advertIndustry);
                advertIndustries.add(advertIndustry);
            });
        }

        Set<AdvertFunction> advertFunctions = categories.getFunctions();
        List<PrismAdvertFunction> functions = categoriesDTO.getFunctions();
        if (isNotEmpty(functions)) {
            functions.stream().forEach(function -> {
                AdvertFunction advertFunction = new AdvertFunction().withAdvert(advert).withFunction(function);
                entityService.save(advertFunction);
                advertFunctions.add(advertFunction);
            });
        }

        List<TagDTO> themes = categoriesDTO.getThemes();
        Set<AdvertTheme> advertThemes = categories.getThemes();
        if (isNotEmpty(themes)) {
            categoriesDTO.getThemes().stream().forEach(themeDTO -> {
                Theme theme = tagService.createOrUpdateTag(Theme.class, themeDTO);
                AdvertTheme advertTheme = new AdvertTheme();
                advertTheme.setAdvert(advert);
                advertTheme.setTheme(theme);
                entityService.save(advertTheme);
                advertThemes.add(advertTheme);
            });
        }

        executeUpdate(resource, "COMMENT_UPDATED_CATEGORY");
    }

    public AdvertTarget createAdvertTarget(ResourceCreationDTO resourceDTO, ResourceRelationCreationDTO targetDTO) {
        User currentUser = userService.getCurrentUser();
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceDTO.getScope(), resourceDTO.getId());
        return createAdvertTarget(currentUser, resource, targetDTO, now());
    }

    public AdvertTarget createAdvertTarget(ResourceParent resource, User user, ResourceParent resourceTarget, User userTarget, DateTime baseline,
            PrismResourceContext context) {
        if (resourceService.getResourceForWhichUserCanConnect(user, resource) != null) {
            return createAdvertTarget(resource, user, resourceTarget, userTarget, baseline, context, null, false);
        }
        return null;
    }

    public AdvertTarget createAdvertTarget(ResourceParent resource, User user, ResourceParent resourceTarget, UserDTO userTargetDTO,
            DateTime baseline, PrismResourceContext context, String message) {
        return createAdvertTarget(resource, user, resourceTarget, userTargetDTO, baseline, context, message, true);
    }

    public AdvertTarget createAdvertTarget(ResourceParent resource, User currentUser, ResourceParent resourceTarget, User userTarget, DateTime baseline,
            PrismResourceContext context, String message, boolean sendInvitation) {
        Advert advert = resource.getAdvert();
        Advert advertTarget = resourceTarget.getAdvert();

        AdvertTarget target;
        if (context.equals(EMPLOYER)) {
            target = createAdvertTarget(advertTarget, userTarget, advert, currentUser, advertTarget, userTarget, baseline, message, sendInvitation);
        } else {
            target = createAdvertTarget(advert, currentUser, advertTarget, userTarget, advertTarget, userTarget, baseline, message, sendInvitation);
        }

        userActivityCacheService.updateUserActivityCaches(resourceTarget, currentUser, baseline);
        return target;
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

            AdvertTargetPending advertTargetPending = new AdvertTargetPending().withAdvert(resource.getAdvert()).withUser(user)
                    .withAdvertTargetInviteList(invitationsSerial)
                    .withAdvertTargetConnectList(connectionsSerial).withAdvertTargetMessage(targets.getMessage());
            entityService.save(advertTargetPending);
            return advertTargetPending;
        }

        return null;
    }

    public boolean acceptAdvertTarget(Integer advertTargetId, boolean accept) {
        AdvertTarget advertTarget = getAdvertTargetById(advertTargetId);
        return acceptAdvertTarget(advertTarget, accept, true);
    }

    public void updateAdvertTarget(Integer advertTargetId, boolean severed) {
        User currentUser = userService.getCurrentUser();
        AdvertTarget advertTarget = getAdvertTargetById(advertTargetId);

        Advert advert = advertTarget.getAdvert();
        Advert targetAdvert = advertTarget.getTargetAdvert();

        Integer advertId = advert.getId();
        Integer targetAdvertId = targetAdvert.getId();

        Set<String> properties = Sets.newHashSet();
        List<Advert> processedAdverts = Lists.newArrayList();
        if (isNotEmpty(getAdvertsForWhichUserHasRoles(currentUser, new String[] { "ADMINISTRATOR" }, newArrayList(advertId)))) {
            properties.add("advert");
            processedAdverts.add(advert);
        }

        if (isNotEmpty(getAdvertsForWhichUserHasRoles(currentUser, new String[] { "ADMINISTRATOR" }, newArrayList(targetAdvertId)))) {
            properties.add("targetAdvert");
            processedAdverts.add(targetAdvert);
        }

        advertDAO.updateAdvertTargetGroup(advertTarget, properties, severed);
        processedAdverts.stream().forEach(processedAdvert -> {
            ResourceParent resource = processedAdvert.getResource();
            ActionOutcomeDTO actionOutcome = executeUpdate(resource, "COMMENT_UPDATED_TARGET");

            DateTime baseline = actionOutcome.getComment().getSubmittedTimestamp();
            if (baseline != null) {
                userActivityCacheService.updateUserActivityCaches(resource, currentUser, baseline);
            }
        });
    }

    public void updateCompetences(PrismScope resourceScope, Integer resourceId, List<AdvertCompetenceDTO> competencesDTO) {
        ResourceParent resource = (ResourceParent) resourceService.getById(resourceScope, resourceId);
        Advert advert = resource.getAdvert();
        updateCompetences(advert, competencesDTO);
        executeUpdate(resource, "COMMENT_UPDATED_COMPETENCE");
    }

    public void updateFinancialDetailNormalization(Integer advertId) {
        Advert advert = getById(advertId);
        updateFinancialDetailNormalization(advert, advert.getInstitution());
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
        List<PrismOpportunityCategory> opportunityCategories = asList(opportunityCategoriesSplit).stream().map(PrismOpportunityCategory::valueOf)
                .collect(toList());
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
                advertTargets.addAll(advertDAO.getAdvertTargetsReceived(resourceScope, "target." + targetAdvertReference, "target." + advertReference, user,
                        connectAdverts));
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
        Integer advertId = query.getAdvertId();
        PrismScope resourceScope = query.getResourceScope();

        Set<Integer> nodeAdverts = Sets.newHashSet();
        if (resourceScope != null) {
            Integer resourceId = query.getResourceId();
            nodeAdverts.addAll(advertDAO.getResourceAdverts(resourceScope, resourceId, scopes));
            nodeAdverts.addAll(advertDAO.getResourceAdvertsTargeted(resourceScope, resourceId, scopes));
        } else if (advertId != null) {
            nodeAdverts.add(advertId);
        }

        UserAdvertDTO userAdvertDTO = getUserAdverts(user, scopes);
        List<Integer> visibleDirect = userAdvertDTO.getVisibleDirect();
        Set<EntityOpportunityCategoryDTO<?>> adverts = Sets.newTreeSet();
        if (!(resourceScope != null && isEmpty(nodeAdverts) || (isTrue(query.getRecommendation()) && isEmpty(visibleDirect)))) {
            advertDAO.getVisibleAdverts(asList(scopes), nodeAdverts, userAdvertDTO, query).forEach(advert -> {
                Integer visibleAdvertId = advert.getId();
                advert.setPriority(new BigDecimal(visibleDirect.contains(visibleAdvertId) ? 1 : 0));
                adverts.add(advert);
            });
        }

        return adverts;
    }

    public void recordPartnershipStateTransition(Resource resource, Comment comment) {
        if (comment.isPartnershipStateTransitionComment()) {
            PrismPartnershipState partnershipState = comment.getAction().getPartnershipTransitionState();

            User user = comment.getUser();
            Advert advert = resource.getAdvert();
            PrismScope resourceScope = resource.getResourceScope();

            Set<Advert> targetAdverts = newHashSet();
            List<Integer> targeterEntities = getAdvertTargeterEntities(user, resourceScope);
            if (isNotEmpty(targeterEntities)) {
                for (PrismScope targeterScope : organizationScopes) {
                    if (targeterScope.ordinal() < resourceScope.ordinal()) {
                        ResourceParent targeterResource = (ResourceParent) getProperty(advert, targeterScope.getLowerCamelName());
                        if (targeterResource != null) {
                            for (PrismScope targetScope : organizationScopes) {
                                targetAdverts.addAll(advertDAO.getAdvertsTargetsForWhichUserCanEndorse(targeterResource.getAdvert(), user, resourceScope,
                                        targeterScope, targetScope, targeterEntities));
                            }
                        }
                    }
                }
            }

            if (targetAdverts.size() > 0) {
                if (partnershipState.equals(ENDORSEMENT_REVOKED)) {
                    targetAdverts.forEach(targetAdvert -> {
                        createAdvertTarget(advert, comment.getUser(), targetAdvert, comment.getCreatedTimestamp(), partnershipState);
                    });
                } else {
                    advertDAO.deleteAdvertTargets(advert, targetAdverts, ENDORSEMENT_REVOKED);
                }
            }
        }
    }

    public List<Integer> getAdvertTargetPendings() {
        return advertDAO.getAdvertTargetPendings();
    }

    @SuppressWarnings("unchecked")
    public void processAdvertTargetPending(Integer advertTargetPendingId, DateTime baseline) {
        AdvertTargetPending advertTargetPending = getAdvertTargetPendingById(advertTargetPendingId);

        String invitationsSerial = advertTargetPending.getAdvertTargetInviteList();
        String connectionsSerial = advertTargetPending.getAdvertTargetConnectList();
        if (invitationsSerial != null) {
            List<ResourceRelationCreationDTO> invitations = prismJsonMappingUtils.readCollection(invitationsSerial, List.class,
                    ResourceRelationCreationDTO.class);
            for (Iterator<ResourceRelationCreationDTO> iterator = invitations.iterator(); iterator.hasNext();) {
                ResourceRelationCreationDTO invitation = iterator.next();
                resourceService.inviteResourceRelation(advertTargetPending.getAdvert().getResource(), advertTargetPending.getUser(), invitation,
                        advertTargetPending.getAdvertTargetMessage(), baseline);

                iterator.remove();
                advertTargetPending.setAdvertTargetInviteList(invitations.isEmpty() ? null : prismJsonMappingUtils.writeValue(invitations));
                return;
            }
        } else if (connectionsSerial != null) {
            List<ResourceRelationCreationDTO> connections = prismJsonMappingUtils.readCollection(connectionsSerial, List.class,
                    ResourceRelationCreationDTO.class);
            for (Iterator<ResourceRelationCreationDTO> iterator = connections.iterator(); iterator.hasNext();) {
                ResourceRelationCreationDTO targetDTO = iterator.next();
                ResourceCreationDTO ResourceRelationCreationDTO = targetDTO.getResource().getResource();
                ResourceParent resourceTarget = (ResourceParent) resourceService.getById(ResourceRelationCreationDTO.getScope(),
                        ResourceRelationCreationDTO.getId());
                createAdvertTarget(advertTargetPending.getAdvert().getResource(), advertTargetPending.getUser(), resourceTarget, targetDTO.getUser(),
                        baseline, ResourceRelationCreationDTO.getContext(), targetDTO.getMessage(), false);

                iterator.remove();
                advertTargetPending.setAdvertTargetConnectList(connections.isEmpty() ? null : prismJsonMappingUtils.writeValue(connections));
                return;
            }
        } else {
            entityService.delete(advertTargetPending);
        }
    }

    public List<Integer> getAdvertsForWhichUserCanManageConnections(User user) {
        return getAdvertsForWhichUserHasRoles(user, new String[] { "ADMINISTRATOR" }, null).stream().map(advert -> advert.getAdvert()).collect(toList());
    }

    public List<Integer> getAdvertTargeterEntities(User user, PrismScope scope) {
        if (!(user == null || roleService.hasUserRole(systemService.getSystem(), user, SYSTEM_ADMINISTRATOR))) {
            UserResourceDTO userResourceDTO = resourceService.getUserResourceParents(user, false);
            HashMultimap<PrismScope, Integer> userResources = userResourceDTO.getResourcesAll();
            List<Integer> adverts = advertDAO.getUserAdvertsTargeted(userResources, scope, advertScopes);

            if (scope.getScopeCategory().equals(APPLICATION)) {
                List<Integer> students = userService.getUserAssociates(userResources, STUDENT);
                return applicationService.getApplications(adverts, students);
            }

            return adverts;
        }

        return emptyList();
    }

    public UserAdvertDTO getUserAdverts(User user, PrismScope... displayScopes) {
        if (user == null) {
            return new UserAdvertDTO().withAllVisible(false).withVisibleDirect(emptyList()).withInvisibleAdverts(emptyList());
        }

        Set<Integer> visibleDirect = newHashSet();
        Set<Integer> visibleIndirect = newHashSet();
        Set<Integer> invisible = newHashSet();
        if (ArrayUtils.isNotEmpty(displayScopes) && stream(displayScopes).anyMatch(displayScope -> displayScope.getScopeCategory().equals(OPPORTUNITY))) {
            UserResourceDTO userResourceDTO = resourceService.getUserResourceParents(user, true);
            HashMultimap<PrismScope, Integer> userResources = userResourceDTO.getResourcesAll();

            visibleDirect.addAll(advertDAO.getUserAdverts(userResources, displayScopes));

            Set<Integer> visibleDirectIndex = userResourceDTO.getAdvertsDirect();
            advertDAO.getUserAdvertsTargeted(userResources, displayScopes).stream().forEach(advert -> {
                if (visibleDirectIndex.contains(advert.getTargetAdvertId())) {
                    visibleDirect.add(advert.getAdvertId());
                } else {
                    visibleIndirect.add(advert.getAdvertId());
                }
            });

            Set<Integer> visible = visibleDirect;
            visible.addAll(visibleIndirect);
            if (visible.size() > 0) {
                invisible.addAll(newHashSet(advertDAO.getUserAdvertsRevoked(visible, userResources, displayScopes)));
            }
        }

        return new UserAdvertDTO().withAllVisible(roleService.hasUserRole(systemService.getSystem(), user, SYSTEM_ADMINISTRATOR))
                .withVisibleDirect(newArrayList(visibleDirect)).withVisibleIndirect(newArrayList(visibleIndirect))
                .withInvisibleAdverts(newArrayList(invisible));
    }

    public List<AdvertCategoryDTO> getAdvertsForWhichUserHasRoles(User user) {
        return getAdvertsForWhichUserHasRoles(user, new String[0], advertScopes, null);
    }

    public List<AdvertCategoryDTO> getAdvertsForWhichUserHasRoles(User user, String[] roleExtensions) {
        return getAdvertsForWhichUserHasRoles(user, roleExtensions, organizationScopes, null);
    }

    public List<AdvertCategoryDTO> getAdvertsForWhichUserHasRoles(User user, String[] roleExtensions, Collection<Integer> advertIds) {
        return getAdvertsForWhichUserHasRoles(user, roleExtensions, advertScopes, advertIds);
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

    public List<Advert> getPossibleAdvertLocations(Advert advert) {
        List<Advert> locations = advert.getParentResources().stream().map(Resource::getAdvert).collect(Collectors.toList());

        stream(advertScopes)
                .flatMap(advertScope -> advertDAO.getPossibleAdvertLocations(advert, advertScope, locations).stream())
                .collect(Collectors.toCollection(() -> new ArrayList<>(locations)));
        return locations;
    }

    public Set<AdvertLocationAddressPartSummaryDTO> getAdvertLocationSummaries(String searchTerm) {
        UserAdvertDTO userAdvertDTO = getUserAdverts(userService.getCurrentUser(), opportunityScopes);

        Map<Integer, AdvertLocationAddressPartSummaryDTO> summaries = Maps.newHashMap();
        for (PrismScope opportunityScope : opportunityScopes) {
            advertDAO.getAdvertLocationSummaries(opportunityScope, userAdvertDTO, searchTerm).forEach(summary -> {
                Integer id = summary.getId();
                AdvertLocationAddressPartSummaryDTO existingSummary = summaries.get(id);
                if (existingSummary == null) {
                    summaries.put(id, summary);
                } else {
                    existingSummary.setAdvertCount(existingSummary.getAdvertCount() + summary.getAdvertCount());
                }
            });
        }

        return newTreeSet(summaries.values());
    }

    public void retireAdvertClosingDate(Advert advert) {
        if (compare(advert.getClosingDate(), LocalDate.now()) >= 0) {
            advert.setClosingDate(null);
        }
    }

    public List<Advert> getTargetedAdverts(Collection<Advert> adverts) {
        return advertDAO.getTargetedAdverts(adverts);
    }

    public List<Advert> getTargeterAdverts(Collection<Advert> adverts) {
        return advertDAO.getTargeterAdverts(adverts);
    }

    public List<AdvertThemeRepresentation> getSuggestedAdvertThemes(Advert advert) {
        return advertDAO.getSuggestedAdvertThemes(advert);
    }

    public boolean checkAdvertVisible(Advert advert, UserAdvertDTO userAdvertDTO) {
        Integer advertId = advert.getId();
        List<Integer> advertsVisible = userAdvertDTO.getVisibleDirect();
        List<Integer> advertsInvisible = userAdvertDTO.getInvisible();

        if (isTrue(advert.getPublished()) && isEmpty(advertsInvisible) || !advertsInvisible.contains(advertId)) {
            if (isTrue(advert.getGloballyVisible())) {
                return true;
            } else if (userAdvertDTO.isAllVisible()) {
                return true;
            } else if (isNotEmpty(advertsVisible) && advertsVisible.contains(advertId)) {
                return true;
            }
        }

        return false;
    }

    private List<AdvertCategoryDTO> getAdvertsForWhichUserHasRoles(User user, String[] roleExtensions, PrismScope[] advertScopes,
            Collection<Integer> advertIds) {
        List<AdvertCategoryDTO> adverts = newArrayList();
        if (user != null) {
            stream(advertScopes).forEach(scope -> {
                String[] filteredRoleExtensions = ArrayUtils.isEmpty(roleExtensions) ? roleExtensions : getFilteredRoleExtensions(scope, roleExtensions);
                adverts.addAll(advertDAO.getAdvertsForWhichUserHasRoles(user, scope, filteredRoleExtensions, advertIds));
            });
        }
        return adverts;
    }

    private String[] getFilteredRoleExtensions(PrismScope scope, String[] roleExtensions) {
        String scopeName = scope.name();
        List<String> permittedRoleExtensions = newArrayList();
        roleService.getRolesByScope(scope).forEach(role -> {
            String roleName = role.name();
            permittedRoleExtensions.add(roleName.replace(scopeName + "_", ""));
        });

        List<String> filteredRoleExtensions = stream(roleExtensions).filter(permittedRoleExtensions::contains).collect(toList());
        return filteredRoleExtensions.toArray(new String[filteredRoleExtensions.size()]);
    }

    private AdvertTarget createAdvertTarget(User currentUser, ResourceParent resource, ResourceRelationCreationDTO targetDTO, DateTime baseline) {
        ResourceCreationDTO ResourceRelationCreationDTO = targetDTO.getResource().getResource();
        ResourceParent resourceTarget = (ResourceParent) resourceService.getById(ResourceRelationCreationDTO.getScope(), ResourceRelationCreationDTO.getId());
        return createAdvertTarget(resource, currentUser, resourceTarget, targetDTO.getUser(), baseline, targetDTO.getContext().getContext(),
                targetDTO.getMessage(), true);
    }

    private AdvertTarget createAdvertTarget(ResourceParent resource, User currentUser, ResourceParent resourceTarget, User userTarget, DateTime baseline,
            PrismResourceContext context, String message) {
        return createAdvertTarget(resource, currentUser, resourceTarget, userTarget, baseline, context, message, true);
    }

    private AdvertTarget createAdvertTarget(Advert advert, User user, Advert advertTarget, User userTarget, Advert advertAccept, User userAccept,
            DateTime baseline, String message, boolean sendInvitation) {
        AdvertTarget targetAdmin = createAdvertTarget(advert, user, advertTarget, userTarget, advertAccept, null, baseline, ENDORSEMENT_PENDING);

        AdvertTarget targetUserAccept = null;
        if (userTarget != null) {
            targetUserAccept = createAdvertTarget(advert, user, advertTarget, userTarget, advertAccept, userAccept, baseline, ENDORSEMENT_PENDING);
        }

        if (sendInvitation) {
            Invitation invitation = invitationService.createInvitation(targetAdmin.getOtherUser(), message);

            if (inviteAdvertTarget(targetAdmin)) {
                targetAdmin.setInvitation(invitation);
            }

            if (targetUserAccept != null && inviteAdvertTarget(targetUserAccept)) {
                targetUserAccept.setInvitation(invitation);
            }
        }

        return targetUserAccept == null ? targetAdmin : targetUserAccept;
    }

    private AdvertTarget createAdvertTarget(Advert advert, User advertUser, Advert targetAdvert, User targetAdvertUser, Advert acceptAdvert,
            User acceptAdvertUser, DateTime baseline, PrismPartnershipState partnershipState) {
        return entityService.getOrCreate(new AdvertTarget().withAdvert(advert).withAdvertUser(advertUser).withAdvertSevered(false)
                .withTargetAdvert(targetAdvert).withTargetAdvertUser(targetAdvertUser).withTargetAdvertSevered(false).withAcceptAdvert(acceptAdvert)
                .withAcceptAdvertUser(acceptAdvertUser).withCreatedTimestamp(baseline).withPartnershipState(partnershipState));
    }

    private AdvertTarget createAdvertTarget(Advert advert, User currentUser, Advert targetAdvert, DateTime baseline, PrismPartnershipState partnershipState) {
        return createAdvertTarget(advert, currentUser, targetAdvert, targetAdvert, baseline, partnershipState);
    }

    private AdvertTarget createAdvertTarget(Advert advert, User currentUser, Advert targetAdvert, Advert acceptAdvert, DateTime baseline,
            PrismPartnershipState partnershipState) {
        AdvertTarget advertTarget = entityService.createOrUpdate(new AdvertTarget().withAdvert(advert).withAdvertSevered(false).withTargetAdvert(targetAdvert)
                .withTargetAdvertSevered(false).withAcceptAdvert(acceptAdvert).withCreatedTimestamp(baseline).withPartnershipState(partnershipState));
        setAdvertTargetSequenceIdentifier(advertTarget, partnershipState, now());
        userActivityCacheService.updateUserActivityCaches(acceptAdvert.getResource(), currentUser, baseline);
        return advertTarget;
    }

    private AdvertTarget createAdvertTarget(ResourceParent resource, User currentUser, ResourceParent resourceTarget, UserDTO userTargetDTO,
            DateTime baseline, PrismResourceContext context, String message, boolean validate) {
        if (!(validate && resourceService.getResourceForWhichUserCanConnect(currentUser, resource) == null)) {
            User userTarget = null;
            if (userTargetDTO != null) {
                userTarget = resourceService.joinResource(resourceTarget, userTargetDTO, VIEWER);
            }
            return createAdvertTarget(resource, currentUser, resourceTarget, userTarget, baseline, context, message);
        }

        return null;
    }

    private boolean inviteAdvertTarget(AdvertTarget target) {
        return target.getPartnershipState().equals(ENDORSEMENT_PENDING) && !acceptAdvertTarget(target, true, false);
    }

    private boolean acceptAdvertTarget(AdvertTarget advertTarget, boolean accept, boolean notify) {
        boolean performed = false;

        if (advertTarget != null) {
            User currentUser = userService.getCurrentUser();

            if (currentUser != null) {
                Set<PrismPartnershipState> oldPartnershipStates = Sets.newHashSet();
                PrismPartnershipState partnershipState = accept ? ENDORSEMENT_PROVIDED : ENDORSEMENT_REVOKED;

                DateTime baseline = now();
                Advert acceptAdvert = advertTarget.getAcceptAdvert();
                if (isNotEmpty(getAdvertsForWhichUserHasRoles(currentUser, new String[] { "ADMINISTRATOR" }, newArrayList(acceptAdvert.getId())))) {
                    advertDAO.getAdvertTargetAdmin(advertTarget).stream().forEach(targetAdmin -> {
                        oldPartnershipStates.add(targetAdmin.getPartnershipState());
                        setAdvertTargetPartnershipState(targetAdmin, partnershipState, baseline, accept);
                    });
                    performed = true;
                }

                AdvertTarget targetUserAccept = advertDAO.getAdvertTargetAccept(advertTarget, currentUser);
                if (targetUserAccept != null) {
                    oldPartnershipStates.add(targetUserAccept.getPartnershipState());
                    setAdvertTargetPartnershipState(targetUserAccept, partnershipState, baseline, accept);
                    performed = true;
                }

                if (performed && accept && notify && !oldPartnershipStates.contains(ENDORSEMENT_PROVIDED)) {
                    notificationService.sendConnectionNotification(currentUser, advertTarget.getOtherUser(), advertTarget);
                }

                resourceService.setResourceAdvertIncompleteSection(advertTarget.getAdvert().getResource());
                resourceService.setResourceAdvertIncompleteSection(advertTarget.getTargetAdvert().getResource());

                userActivityCacheService.updateUserActivityCaches(acceptAdvert.getResource(), currentUser, baseline);
            }
        }

        return performed;
    }

    private void updateAdvertTargets(User currentUser, Advert advert, List<Integer> customTargetIds, DateTime baseline) {
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

            createAdvertTarget(customAdvert, currentUser, customTargetAdvert, customAcceptAdvert, baseline, ENDORSEMENT_PROVIDED);
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

    private void updateFinancialDetailNormalization(Advert advert, Institution institution) {
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
            String currencyNormalized = institution.getAdvert().getAddress().getDomicile().getCurrency();
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

    private ActionOutcomeDTO executeUpdate(ResourceParent resource, String message) {
        return resourceService.executeUpdate(resource, userService.getCurrentUser(),
                PrismDisplayPropertyDefinition.valueOf(resource.getResourceScope().name() + "_" + message));
    }

    private void setAdvertAddress(Advert advert, AddressDTO addressDTO) {
        Address address = advert.getAddress();
        if (address == null) {
            address = new Address();
            advert.setAddress(address);
            addressService.updateAddress(address, addressDTO);
        } else {
            addressService.updateAndGeocodeAddress(address, addressDTO, advert.getName());
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
            if (parentResource.equals(resource)) {
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

    private boolean persistAdvertLocation(Advert advert, Set<AdvertLocation> advertLocations, Advert locationAdvert) {
        return advertLocations.add(entityService.getOrCreate(new AdvertLocation().withAdvert(advert).withLocationAdvert(locationAdvert)));
    }

}
