package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Sets.newTreeSet;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.joda.time.Days.daysBetween;
import static uk.co.alumeni.prism.PrismConstants.ANGULAR_HASH;
import static uk.co.alumeni.prism.PrismConstants.ORDERING_PRECISION;
import static uk.co.alumeni.prism.PrismConstants.RESOURCE_LIST_PAGE_ROW_COUNT;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EXTERNAL_HOMEPAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.getResourceContexts;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;
import static uk.co.alumeni.prism.utils.PrismListUtils.getSummaryRepresentations;
import static uk.co.alumeni.prism.utils.PrismListUtils.processRowDescriptors;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.setProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismFilterEntity;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.definitions.PrismResourceListConstraint;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeSectionDefinition;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.ApplicationProcessingSummaryDTO;
import uk.co.alumeni.prism.dto.ResourceChildCreationDTO;
import uk.co.alumeni.prism.dto.ResourceConnectionDTO;
import uk.co.alumeni.prism.dto.ResourceFlatToNestedDTO;
import uk.co.alumeni.prism.dto.ResourceIdentityDTO;
import uk.co.alumeni.prism.dto.ResourceListRowDTO;
import uk.co.alumeni.prism.dto.ResourceLocationDTO;
import uk.co.alumeni.prism.dto.ResourceOpportunityCategoryDTO;
import uk.co.alumeni.prism.dto.ResourceSimpleDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceReportFilterDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.address.AddressCoordinatesRepresentation;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.ProgramRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ProjectRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ResourceConditionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceCountRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceListFilterRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceListFilterRepresentation.FilterExpressionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceListRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceListRowRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceLocationRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceOpportunityRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceOpportunityRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ResourceOpportunityRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceOpportunityRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.ResourceParentRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceParentRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.ResourceParentRepresentationSummary;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationChildCreation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationIdentity;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationLocation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRobot;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRobotMetadata;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationStandard;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSummary;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotConstraintRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotDataRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationWeek;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentation;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.ScopeService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Service
@Transactional
public class ResourceMapper {

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private StateService stateService;

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private ApplicationMapper applicationMapper;

    @Inject
    private DepartmentMapper departmentMapper;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private InstitutionMapper institutionMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    public ResourceListRepresentation getResourceListRepresentation(PrismScope scope, ResourceListFilterDTO filter, String lastSequenceIdentifier)
            throws Exception {
        DateTime baseline = new DateTime();
        User user = userService.getCurrentUser();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

        Map<String, Integer> summaries = Maps.newHashMap();
        Set<Integer> onlyAsPartnerResourceIds = Sets.newHashSet();
        List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);

        if (isTrue(filter.getWithNewMessages())) {
            List<Integer> resourceIds = resourceService.getResourcesWithUnreadMessages(scope, user);
            filter.setResourceIds(resourceIds.size() > 0 ? resourceIds : newArrayList(0));
        }

        Set<ResourceOpportunityCategoryDTO> orderedResources = newTreeSet();
        Map<Integer, ResourceOpportunityCategoryDTO> indexedResources = newHashMap();
        Set<ResourceOpportunityCategoryDTO> resources = resourceService.getResources(user, scope, parentScopes, targeterEntities, filter);
        if (isNotEmpty(resources)) {
            resourceService.setResourceMessageAttributes(scope, resources, user);

            LocalDate baselineDate = baseline.toLocalDate();
            resources.forEach(resource -> {
                Integer unreadMessageCount = resource.getUnreadMessageCount();
                boolean prioritize = (isTrue(resource.getRaisesUrgentFlag()) || (unreadMessageCount == null ? 0 : unreadMessageCount) > 0);
                Integer daysSinceLastUpdated = daysBetween(resource.getUpdatedTimestamp().toLocalDate(), baselineDate).getDays();
                resource.setPriority(prioritize ? new BigDecimal(1).setScale(ORDERING_PRECISION) : new BigDecimal(1).divide(
                        new BigDecimal(1).add(new BigDecimal(daysSinceLastUpdated)), HALF_UP).setScale(ORDERING_PRECISION));
                indexedResources.put(resource.getId(), resource);
                orderedResources.add(resource);
            });
        }

        TreeMap<String, ResourceListRowRepresentation> rowIndex = newTreeMap();
        processRowDescriptors(orderedResources, onlyAsPartnerResourceIds, summaries);
        resourceService.getResourceList(user, scope, parentScopes, targeterEntities, orderedResources, filter, lastSequenceIdentifier,
                RESOURCE_LIST_PAGE_ROW_COUNT, onlyAsPartnerResourceIds, true).forEach(row -> {
            ResourceListRowRepresentation representation = new ResourceListRowRepresentation();
            representation.setScope(scope);
            Integer resourceId = row.getResourceId();
            representation.setId(resourceId);

            Integer institutionId = row.getInstitutionId();
            Integer departmentId = row.getDepartmentId();
            Integer programId = row.getProgramId();
            Integer projectId = row.getProjectId();

            if (scope.equals(INSTITUTION)) {
                representation.setName(row.getInstitutionName());
                setInstitutionLogoImage(row, representation);
            } else {
                representation.setInstitution(new ResourceRepresentationSimple().withScope(INSTITUTION)
                        .withId(institutionId).withName(row.getInstitutionName()));

                Integer logoImageId = row.getLogoImageId();
                if (logoImageId != null) {
                    representation.setLogoImage(documentMapper.getDocumentRepresentation(institutionId));
                }
            }

            if (scope.equals(DEPARTMENT)) {
                representation.setName(row.getDepartmentName());
            } else if (departmentId != null) {
                representation.setDepartment(new ResourceRepresentationSimple().withScope(DEPARTMENT)
                        .withId(departmentId).withName(row.getDepartmentName()));
            }

            if (scope.equals(PROGRAM)) {
                representation.setName(row.getProgramName());
            } else if (programId != null) {
                representation.setProgram(new ResourceRepresentationSimple().withScope(PROGRAM)
                        .withId(programId).withName(row.getProgramName()));
            }

            if (scope.equals(PROJECT)) {
                representation.setName(row.getProjectName());
            } else if (projectId != null) {
                representation.setProject(new ResourceRepresentationSimple().withScope(PROJECT)
                        .withId(projectId).withName(row.getProjectName()));
            }

            representation.setCode(row.getCode());
            representation.setUser(userMapper.getUserRepresentationSimple(row, user));
            representation.setApplicationRatingAverage(row.getApplicationRatingAverage());

            representation.setState(stateMapper.getStateRepresentationSimple(row.getStateId()));
            representation.setSecondaryStates(stateMapper.getStateRepresentations(row.getSecondaryStateIds()));

            List<ActionRepresentationSimple> actions = actionMapper.getActionRepresentations(row);
            DateTime updatedTimestamp = row.getUpdatedTimestamp();

            representation.setCreatedTimestamp(row.getCreatedTimestamp());
            representation.setUpdatedTimestamp(updatedTimestamp);

            setRaisesUrgentFlag(representation, actions);

            ResourceOpportunityCategoryDTO indexResource = indexedResources.get(resourceId);

            Integer readMessageCount = indexResource.getReadMessageCount();
            Integer unreadMessageCount = indexResource.getUnreadMessageCount();

            representation.setReadMessageCount(readMessageCount == null ? 0 : readMessageCount);
            representation.setUnreadMessageCount(unreadMessageCount == null ? 0 : unreadMessageCount);

            setRaisesUpdateFlag(representation, baseline, updatedTimestamp);

            String sequenceIdentifier = indexResource.toString();
            representation.setSequenceIdentifier(sequenceIdentifier);

            representation.setAdvertIncompleteSections(getResourceAdvertIncompleteSectionRepresentation(row.getAdvertIncompleteSection()));
            representation.setStateActionPendingCount(row.getStateActionPendingCount().intValue());

            representation.setActions(actions);
            rowIndex.put(sequenceIdentifier, representation);
        });

        Map<String, Integer> urgentSummaries = Maps.newHashMap();
        Set<ResourceOpportunityCategoryDTO> urgentResources = resources.stream().filter(resource -> isTrue(resource.getRaisesUrgentFlag())).collect(toSet());
        processRowDescriptors(urgentResources, urgentSummaries);

        return new ResourceListRepresentation().withRows(newLinkedList(rowIndex.descendingMap().values())).withSummaries(getSummaryRepresentations(summaries))
                .withUrgentSummaries(getSummaryRepresentations(urgentSummaries));
    }

    public ResourceRepresentationLocation getResourceRepresentationLocation(Resource resource) {
        ResourceRepresentationLocation representation = getResourceRepresentationSimple(resource, ResourceRepresentationLocation.class);
        representation.setAddress(advertMapper.getAdvertAddressRepresentation(resource.getAdvert()));
        return representation;
    }

    public <T extends ResourceSimpleDTO> ResourceRepresentationLocation getResourceRepresentationLocation(T resource, User user) {
        return getResourceRepresentationLocation(resource, ResourceRepresentationLocation.class, user);
    }

    public <T extends Resource> ResourceRepresentationSimple getResourceRepresentationSimple(T resource) {
        return getResourceRepresentationSimple(resource, ResourceRepresentationSimple.class);
    }

    public <T extends ResourceOpportunity> ResourceOpportunityRepresentationSimple getResourceOpportunityRepresentationSimple(T resource) {
        ResourceOpportunityRepresentationSimple representation = getResourceRepresentationSimple(resource, ResourceOpportunityRepresentationSimple.class);

        Advert advert = resource.getAdvert();
        representation.setDurationMinimum(advert.getDurationMinimum());
        representation.setDurationMaximum(advert.getDurationMaximum());
        return representation;
    }

    public ResourceRepresentationIdentity getResourceRepresentationChildCreation(ResourceChildCreationDTO resourceDTO) {
        ResourceRepresentationChildCreation representation = getResourceRepresentation(resourceDTO.getScope(), resourceDTO,
                ResourceRepresentationChildCreation.class);
        representation.setCreateDirectly(resourceDTO.getCreateDirectly());
        return representation;
    }

    public <T extends Resource, V extends ResourceRepresentationSimple> V getResourceRepresentationSimple(T resource, Class<V> returnType) {
        V representation = getResourceRepresentation(resource, returnType);
        representation.setCode(resource.getCode());
        representation.setState(stateMapper.getStateRepresentationSimple(resource.getState()));

        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resource;
            representation.setImportedCode(resourceOpportunity.getImportedCode());
        }

        return representation;
    }

    public <T extends Resource, V extends ResourceRepresentationExtended> V getResourceRepresentationExtended(T resource, Class<V> returnType,
            List<PrismRole> overridingRoles, User currentUser) {
        List<ActionRepresentationExtended> actions = actionMapper.getActionRepresentations(resource, currentUser);
        V representation = getResourceRepresentationStandard(resource, returnType, actions, overridingRoles, currentUser);
        representation.setStateActionPendingCount(resource.getStateActionPendings().size());
        representation.setActions(actions);
        representation.setConditions(getResourceConditionRepresentations(resource));
        return representation;
    }

    public <T extends Resource> ResourceRepresentationStandard getResourceRepresentationStandard(T resource, User currentUser) {
        List<ActionRepresentationExtended> actions = actionMapper.getActionRepresentations(resource, currentUser);
        return getResourceRepresentationStandard(resource, ResourceRepresentationStandard.class, actions,
                roleService.getRolesOverridingRedactions(resource, currentUser), currentUser);
    }

    public <T extends ResourceFlatToNestedDTO> ResourceRepresentationRelation getResourceRepresentationActivity(T resource) {
        ResourceRepresentationRelation representation = new ResourceRepresentationRelation().withScope(resource.getScope()).withId(resource.getId());
        ResourceFlatToNestedDTO project = resource.getEnclosingResource(PROJECT);
        if (project != null) {
            representation.setProject(new ResourceRepresentationSimple().withScope(PROJECT)
                    .withId(resource.getProjectId()).withName(resource.getProjectName()));
        }

        ResourceFlatToNestedDTO program = resource.getEnclosingResource(PROGRAM);
        if (program != null) {
            representation.setProgram(new ResourceRepresentationSimple().withScope(PROGRAM)
                    .withId(resource.getProgramId()).withName(resource.getProgramName()));
        }

        ResourceFlatToNestedDTO department = resource.getEnclosingResource(DEPARTMENT);
        if (department != null) {
            representation.setDepartment(new ResourceRepresentationSimple().withScope(DEPARTMENT)
                    .withId(resource.getDepartmentId()).withName(resource.getDepartmentName()));
        }

        ResourceFlatToNestedDTO institution = resource.getEnclosingResource(INSTITUTION);
        if (institution != null) {
            representation.setInstitution(new ResourceRepresentationSimple().withScope(INSTITUTION)
                    .withId(resource.getInstitutionId()).withName(resource.getInstitutionName())
                    .withLogoImage(documentMapper.getDocumentRepresentation(resource.getLogoImageId())));
        }

        return representation;
    }

    public <T extends ResourceParent, V extends ResourceParentRepresentation> V getResourceParentRepresentation(T resource, Class<V> returnType,
            List<PrismRole> overridingRoles, User currentUser) {
        V representation = getResourceRepresentationExtended(resource, returnType, overridingRoles, currentUser);
        representation.setAdvert(advertMapper.getAdvertRepresentationSimple(resource.getAdvert()));
        representation.setOpportunityCategories(asList(resource.getOpportunityCategories().split("\\|")).stream().map(PrismOpportunityCategory::valueOf)
                .collect(toList()));
        representation.setContexts(getResourceContexts(resource.getOpportunityCategories()));
        representation.setSuggestedThemes(advertService.getSuggestedAdvertThemes(resource.getAdvert()));
        representation.setAdvertIncompleteSections(getResourceAdvertIncompleteSectionRepresentation(resource.getAdvertIncompleteSection()));
        return representation;
    }

    public <T extends ResourceOpportunity, V extends ResourceOpportunityRepresentation> V getResourceOpportunityRepresentation(
            T resource, Class<V> returnType, List<PrismRole> overridingRoles, User currentUser) {
        V representation = getResourceParentRepresentation(resource, returnType, overridingRoles, currentUser);

        representation.setOpportunityType(resource.getOpportunityType().getId());
        representation.setOpportunityCategory(PrismOpportunityCategory.valueOf(resource.getOpportunityCategories()));
        representation.setStudyOptions(resourceService.getStudyOptions(resource));

        Advert advert = resource.getAdvert();
        representation.setDurationMinimum(advert.getDurationMinimum());
        representation.setDurationMaximum(advert.getDurationMaximum());
        return representation;
    }

    public <T extends ResourceOpportunity, V extends ResourceOpportunityRepresentationClient> V getResourceOpportunityRepresentationClient(T resource,
            Class<V> returnType, List<PrismRole> overridingRoles, User currentUser) {
        V representation = getResourceOpportunityRepresentation(resource, returnType, overridingRoles, currentUser);
        appendResourceParentRepresentationSummary(resource, representation);
        return representation;
    }

    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationClient(T resource, User currentUser) {
        resourceService.validateViewResource(resource, currentUser);

        Class<?> resourceClass = resource.getClass();
        List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(resource, currentUser);

        if (Institution.class.equals(resourceClass)) {
            return institutionMapper.getInstitutionRepresentationClient((Institution) resource, overridingRoles, currentUser);
        } else if (Department.class.equals(resourceClass)) {
            return departmentMapper.getDepartmentRepresentationClient((Department) resource, overridingRoles, currentUser);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            Class<? extends ResourceOpportunityRepresentationClient> representationType = Program.class.equals(resourceClass) ? ProgramRepresentationClient.class
                    : ProjectRepresentationClient.class;
            return getResourceOpportunityRepresentationClient((ResourceOpportunity) resource, representationType, overridingRoles, currentUser);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            return applicationMapper.getApplicationRepresentationClient((Application) resource, overridingRoles, currentUser);
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class, overridingRoles, currentUser);
    }

    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationExport(T resource, User currentUser) {
        resourceService.validateViewResource(resource, currentUser);

        PrismScope resourceScope = resource.getResourceScope();
        List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(resource, currentUser);

        if (resourceScope.equals(INSTITUTION)) {
            return institutionMapper.getInstitutionRepresentation((Institution) resource, overridingRoles, currentUser);
        } else if (resourceScope.equals(DEPARTMENT)) {
            return departmentMapper.getDepartmentRepresentation((Department) resource, overridingRoles, currentUser);
        } else if (resourceScope.getScopeCategory().equals(OPPORTUNITY)) {
            return getResourceOpportunityRepresentation((ResourceOpportunity) resource, ResourceOpportunityRepresentation.class, overridingRoles, currentUser);
        } else if (resourceScope.equals(APPLICATION)) {
            return applicationMapper.getApplicationRepresentationExtended((Application) resource, overridingRoles, currentUser);
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class, overridingRoles, currentUser);
    }

    public <T extends Resource> ResourceRepresentationSummary getResourceRepresentationSummary(T resource) {
        resourceService.validateViewResource(resource, userService.getCurrentUser());

        PrismScope resourceScope = resource.getResourceScope();
        PrismScopeCategory resourceScopeCategory = resourceScope.getScopeCategory();
        if (resourceScopeCategory.equals(OPPORTUNITY) || resourceScopeCategory.equals(ORGANIZATION)) {
            ResourceParentRepresentationSummary representation = getResourceSummaryRepresentation(resource, ResourceParentRepresentationSummary.class);
            appendResourceParentRepresentationSummary((ResourceParent) resource, representation);
            return representation;
        } else if (resourceScope.equals(APPLICATION)) {
            return applicationMapper.getApplicationRepresentationSummary((Application) resource);
        }

        return getResourceSummaryRepresentation(resource, ResourceRepresentationSummary.class);
    }

    public <T extends Resource, U extends ResourceRepresentationSummary> U getResourceSummaryRepresentation(T resource, Class<U> representationClass) {
        U representation = BeanUtils.instantiate(representationClass);
        representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser(), userService.getCurrentUser()));
        representation.setCreatedTimestamp(resource.getCreatedTimestamp());
        return representation;
    }

    public <T extends ResourceParent, U extends ResourceParentRepresentationClient> void appendResourceParentRepresentationSummary(T resource, U representation) {
        List<ResourceCountRepresentation> counts = Lists.newLinkedList();
        for (PrismScope childScope : scopeService.getChildScopesAscending(resource.getResourceScope(), PROJECT)) {
            counts.add(new ResourceCountRepresentation().withResourceScope(childScope).withResourceCount(
                    resourceService.getActiveChildResourceCount(resource, childScope)));
            if (childScope.equals(PROJECT)) {
                break;
            }
        }
        representation.setCounts(counts);
        representation.setPlot(getResourceSummaryPlotRepresentation(resource, null));
    }

    public ResourceSummaryPlotRepresentation getResourceSummaryPlotRepresentation(ResourceParent resource, ResourceReportFilterDTO filterDTO) {
        if (filterDTO == null) {
            ResourceSummaryPlotDataRepresentation plotDataRepresentation = getResourceSummaryPlotDataRepresentation(resource, null);
            return new ResourceSummaryPlotRepresentation().withConstraints(null).withData(plotDataRepresentation);
        } else {
            List<ResourceSummaryPlotConstraintRepresentation> constraint = getResourceSummaryPlotConstraintRepresentation(filterDTO);
            ResourceSummaryPlotDataRepresentation plotDataRepresentation = getResourceSummaryPlotDataRepresentation(resource, filterDTO.getProperties());
            return new ResourceSummaryPlotRepresentation().withConstraints(constraint).withData(plotDataRepresentation);
        }
    }

    public ResourceSummaryPlotDataRepresentation getResourceSummaryPlotDataRepresentation(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        ResourceSummaryPlotDataRepresentation summary = new ResourceSummaryPlotDataRepresentation();

        HashMultimap<PrismFilterEntity, String> transformedConstraints = HashMultimap.create();
        if (constraints != null) {
            HashMultimap<PrismFilterEntity, String> constraintsToTransform = HashMultimap.create();
            constraints.forEach(c -> {
                PrismFilterEntity prismFilterEntity = c.getEntityType();
                if (prismFilterEntity.getFilterSelector() == null) {
                    transformedConstraints.put(prismFilterEntity, c.getEntityId());
                } else {
                    constraintsToTransform.put(prismFilterEntity, c.getEntityId());
                }
            });

            for (PrismFilterEntity filterEntity : constraintsToTransform.keySet()) {
                List<String> possibleConstraints = applicationContext.getBean(filterEntity.getFilterSelector()).getPossible(resource, APPLICATION,
                        constraintsToTransform.get(filterEntity));
                transformedConstraints.putAll(filterEntity, possibleConstraints);
            }
        }

        List<ApplicationProcessingSummaryRepresentationYear> yearRepresentations = Lists.newLinkedList();
        List<ApplicationProcessingSummaryDTO> yearSummaries = getApplicationProcessingSummariesByYear(resource, transformedConstraints);
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> monthSummaries = getApplicationProcessingSummariesByMonth(resource, transformedConstraints);
        LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> weekSummaries = getApplicationProcessingSummariesByWeek(resource,
                transformedConstraints);

        for (ApplicationProcessingSummaryDTO yearSummary : yearSummaries) {
            String applicationYear = yearSummary.getApplicationYear();

            ApplicationProcessingSummaryRepresentationYear yearRepresentation = applicationMapper.getApplicationProcessingSummaryRepresentation(yearSummary,
                    ApplicationProcessingSummaryRepresentationYear.class);
            yearRepresentation.setApplicationYear(applicationYear);

            List<ApplicationProcessingSummaryRepresentationMonth> monthRepresentations = Lists.newLinkedList();
            for (ApplicationProcessingSummaryDTO monthSummary : monthSummaries.get(applicationYear)) {
                ApplicationProcessingSummaryRepresentationMonth monthRepresentation = applicationMapper.getApplicationProcessingSummaryRepresentation(
                        monthSummary, ApplicationProcessingSummaryRepresentationMonth.class);
                monthRepresentation.setApplicationMonth(monthSummary.getApplicationMonth());
                monthRepresentations.add(monthRepresentation);

                Integer applicationMonth = monthSummary.getApplicationMonth();
                List<ApplicationProcessingSummaryRepresentationWeek> weekRepresentations = Lists.newLinkedList();
                for (ApplicationProcessingSummaryDTO weekSummary : weekSummaries.get(new ResourceProcessingMonth(applicationYear, applicationMonth))) {
                    ApplicationProcessingSummaryRepresentationWeek weekRepresentation = applicationMapper.getApplicationProcessingSummaryRepresentation(
                            weekSummary, ApplicationProcessingSummaryRepresentationWeek.class);
                    weekRepresentation.setApplicationWeek(weekSummary.getApplicationWeek());
                    weekRepresentations.add(weekRepresentation);
                }

                monthRepresentation.setProcessingSummaries(weekRepresentations);
            }

            yearRepresentation.setProcessingSummaries(monthRepresentations);
            yearRepresentations.add(yearRepresentation);
        }

        summary.setProcessingSummaries(yearRepresentations);
        return summary;
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource,
            HashMultimap<PrismFilterEntity, String> constraints) {
        return applicationService.getApplicationProcessingSummariesByYear(resource, constraints);
    }

    public LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(
            ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationService.getApplicationProcessingSummariesByMonth(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(processingSummary.getApplicationYear(), processingSummary);
        }
        return index;
    }

    public LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(
            ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
        LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationService.getApplicationProcessingSummariesByWeek(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(new ResourceProcessingMonth(processingSummary.getApplicationYear(), processingSummary.getApplicationMonth()), processingSummary);
        }
        return index;
    }

    public List<ResourceConditionRepresentation> getResourceConditionRepresentations(Resource resource) {
        return resource.getResourceConditions().stream()
                .map(condition -> new ResourceConditionRepresentation().withActionCondition(condition.getActionCondition())
                        .withInternalMode(condition.getInternalMode()).withExternalMode(condition.getExternalMode()))
                .collect(Collectors.toList());
    }

    public <T extends ResourceParent> ResourceRepresentationRobot getResourceRepresentationRobot(T resource) {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource);

        ResourceRepresentationRobot representation = new ResourceRepresentationRobot(loader.loadLazy(SYSTEM_EXTERNAL_HOMEPAGE), applicationUrl);
        setRobotResourceRepresentation(representation, resource);

        PrismScope resourceScope = resource.getResourceScope();

        for (PrismScope parentScope : scopeService.getParentScopesDescending(resourceScope, INSTITUTION)) {
            Resource parentResource = resource.getEnclosingResource(parentScope);
            if (parentResource != null) {
                setRobotResourceRepresentation(representation, parentResource);
            }
        }

        for (PrismScope childScope : scopeService.getChildScopesAscending(resourceScope, PROJECT)) {
            String childScopeReference = "related" + childScope.getUpperCamelName() + "s";
            setProperty(representation, childScopeReference, resourceService.getResourceRobotRelatedRepresentations(resource, childScope,
                    loader.loadLazy(PrismDisplayPropertyDefinition.valueOf("SYSTEM_OPPORTUNITIES_RELATED_" + childScope.name() + "S"))));
        }

        return representation;
    }

    public String getResourceThumbnailUrlRobot(Resource resource) {
        String defaultSocialThumbnail = applicationUrl + "/images/fbimg.jpg";
        if (resource.getResourceScope() == SYSTEM) {
            return defaultSocialThumbnail;
        }
        Document logoImage = resource.getInstitution().getLogoImage();
        return logoImage == null ? defaultSocialThumbnail : applicationApiUrl + "/images/" + logoImage.getId().toString();
    }

    public String getResourceUrlRobot(Resource resource) {
        return applicationUrl + "/" + ANGULAR_HASH + "/?" + resource.getResourceScope().getLowerCamelName() + "=" + resource.getId();
    }

    public <T extends Resource> ResourceRepresentationRelation getResourceRepresentationRelation(T resource, User currentUser) {
        return getResourceRepresentationRelation(resource, ResourceRepresentationRelation.class, currentUser);
    }

    public <T extends Resource> ResourceLocationRepresentationRelation getResourceLocationRepresentationRelation(T resource, User currentUser) {
        return getResourceRepresentationRelation(resource, ResourceLocationRepresentationRelation.class, currentUser);
    }

    public <T extends Resource> ResourceRepresentationRelation getResourceOpportunityRepresentationRelation(T resource, User currentUser) {
        return getResourceRepresentationRelation(resource, ResourceOpportunityRepresentationRelation.class, currentUser);
    }

    public List<ResourceListFilterRepresentation> getResourceListFilterRepresentations() {
        List<ResourceListFilterRepresentation> filters = Lists.newArrayListWithCapacity(PrismResourceListConstraint.values().length);
        for (PrismResourceListConstraint property : PrismResourceListConstraint.values()) {
            List<FilterExpressionRepresentation> filterExpressions = property.getPermittedExpressions().stream()
                    .map(filterExpression -> new FilterExpressionRepresentation(filterExpression, filterExpression.isNegatable()))
                    .collect(Collectors.toList());
            filters.add(new ResourceListFilterRepresentation(property, filterExpressions, property.getPropertyType(), property.getPermittedScopes(), property
                    .isPermittedInBulkMode()));
        }
        return filters;
    }

    public ResourceRepresentationConnection getResourceRepresentationConnection(ResourceConnectionDTO resource) {
        return getResourceRepresentationConnection(resource.getInstitutionId(), resource.getInstitutionName(), resource.getLogoImageId(),
                resource.getDepartmentId(),
                resource.getDepartmentName(), resource.getOpportunityCategories());
    }

    public ResourceRepresentationConnection getResourceRepresentationConnection(Integer institutionId, String institutionName, Integer logoImageId,
            Integer departmentId, String departmentName) {
        return getResourceRepresentationConnection(institutionId, institutionName, logoImageId, departmentId, departmentName, null, null);
    }

    public ResourceRepresentationConnection getResourceRepresentationConnection(Integer institutionId, String institutionName, Integer logoImageId,
            Integer departmentId, String departmentName, Integer backgroundImageId) {
        return getResourceRepresentationConnection(institutionId, institutionName, logoImageId, departmentId, departmentName, backgroundImageId, null);
    }

    public ResourceRepresentationConnection getResourceRepresentationConnection(Integer institutionId, String institutionName, Integer logoImageId,
            Integer departmentId, String departmentName, String opportunityCategories) {
        return getResourceRepresentationConnection(institutionId, institutionName, logoImageId, departmentId, departmentName, null, opportunityCategories);
    }

    public ResourceRepresentationConnection getResourceRepresentationConnection(Integer institutionId, String institutionName, Integer logoImageId,
            Integer departmentId, String departmentName, Integer backgroundImageId, String opportunityCategories) {
        ResourceRepresentationConnection representation = new ResourceRepresentationConnection().withInstitution(new ResourceRepresentationSimple()
                .withScope(INSTITUTION)
                .withId(institutionId).withName(institutionName).withLogoImage(documentMapper.getDocumentRepresentation(logoImageId)));

        if (departmentId != null) {
            representation.setDepartment(new ResourceRepresentationSimple().withScope(DEPARTMENT).withId(departmentId).withName(departmentName));
        }

        representation.setBackgroundImage(documentMapper.getDocumentRepresentation(backgroundImageId));
        representation.setContexts(getResourceContexts(opportunityCategories));
        return representation;
    }

    public List<ResourceRepresentationConnection> getUserResourceConnectionRepresentations(User user, PrismResourceContext motivation, String searchTerm) {
        return getUserResourceConnectionRepresentations(user, null, motivation, searchTerm);
    }

    public List<ResourceRepresentationConnection> getUserResourceConnectionRepresentations(User user, ResourceParent resourceTarget,
            PrismResourceContext motivation, String searchTerm) {
        List<ResourceRepresentationConnection> representations = newLinkedList();
        resourceService.getResourcesForWhichUserCanConnect(user, resourceTarget, motivation, searchTerm).forEach(resourceConnect -> {
            representations.add(getResourceRepresentationConnection(resourceConnect));
        });
        return representations;
    }

    private <T extends Resource, V extends ResourceRepresentationRelation> V getResourceRepresentationRelation(T resource, Class<V> returnType, User currentUser) {
        V representation = getResourceRepresentationSimple(resource, returnType);

        if (!resource.getResourceScope().equals(SYSTEM)) {
            Advert advert = resource.getAdvert();
            representation.setAdvert(new AdvertRepresentationSimple().withId(advert.getId()).withSummary(advert.getSummary()));
        }

        if (ResourceOpportunityRepresentationRelation.class.isAssignableFrom(returnType) && ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ((ResourceOpportunityRepresentationRelation) representation).setOpportunityType(((ResourceOpportunity) resource).getOpportunityType().getId());
            ((ResourceOpportunityRepresentationRelation) representation).setSummary(resource.getAdvert().getSummary());
        }

        if (ResourceRepresentationStandard.class.isAssignableFrom(returnType)) {
            representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser(), currentUser));
        }

        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(resource.getResourceScope(), INSTITUTION);
        ResourceFlatToNestedDTO resourceWithParents = resourceService.getResourceWithParentResources(resource, parentScopes);

        for (PrismScope parentScope : parentScopes) {
            ResourceFlatToNestedDTO parentResource = resourceWithParents.getEnclosingResource(parentScope);
            if (parentResource != null) {
                ResourceRepresentationSimple parentRepresentation = new ResourceRepresentationSimple().withScope(parentScope).withId(parentResource.getId())
                        .withName(parentResource.getName());
                if (parentScope.equals(INSTITUTION)) {
                    parentRepresentation.setLogoImage(documentMapper.getDocumentRepresentation(parentResource.getLogoImageId(),
                            parentResource.getLogoImageFileName()));
                }
                representation.setParentResource(parentRepresentation);
            }
        }

        if (returnType.equals(ResourceLocationRepresentationRelation.class)) {
            ((ResourceLocationRepresentationRelation) representation).setAddress(advertMapper.getAdvertAddressRepresentation(resource.getAdvert()));
        }

        return representation;
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource, V extends ResourceRepresentationStandard> V getResourceRepresentationStandard(
            T resource, Class<V> returnType, List<ActionRepresentationExtended> actions, List<PrismRole> overridingRoles, User user) {
        V representation = getResourceRepresentationRelation(resource, returnType, user);

        DateTime updatedTimestamp = resource.getUpdatedTimestamp();

        setRaisesUrgentFlag(representation, (List<ActionRepresentationSimple>) (List<?>) actions);
        setRaisesUpdateFlag(representation, new DateTime(), updatedTimestamp);

        Integer readMessageCount = resourceService.getResourceReadMessageCount(resource, user);
        Integer unreadMessageCount = resourceService.getResourceUnreadMessageCount(resource, user);

        representation.setReadMessageCount(readMessageCount == null ? 0 : readMessageCount);
        representation.setUnreadMessageCount(unreadMessageCount == null ? 0 : unreadMessageCount);

        Class<T> resourceClass = (Class<T>) resource.getClass();
        if (!resourceClass.equals(System.class) && actionService.getRedactions(resource, userService.getCurrentUser(), overridingRoles).isEmpty()) {
            representation.setApplicationRatingAverage((BigDecimal) getProperty(resource, "applicationRatingAverage"));
        }

        representation.setPreviousState(stateMapper.getStateRepresentationSimple(resource.getPreviousState()));
        representation.setSecondaryStates(stateMapper.getSecondaryStateRepresentations(resource));

        representation.setCreatedTimestamp(resource.getCreatedTimestamp());
        representation.setUpdatedTimestamp(updatedTimestamp);
        representation.setSequenceIdentifier(resource.getSequenceIdentifier());
        return representation;
    }

    private <T extends ResourceSimpleDTO, U extends ResourceRepresentationLocation> U getResourceRepresentationLocation(T resource, Class<U> returnType,
            User user) {
        U representation = BeanUtils.instantiate(returnType);

        representation.setId(resource.getId());
        representation.setScope(resource.getScope());
        representation.setName(resource.getName());
        representation.setLogoImage(documentMapper.getDocumentRepresentation(resource.getLogoImageId()));

        if (resource.getClass().equals(ResourceLocationDTO.class)) {
            ResourceLocationDTO resourceLocation = (ResourceLocationDTO) resource;
            AddressRepresentation addressRepresentation = new AddressRepresentation().withAddressLine1(resourceLocation.getAddressLine1())
                    .withAddressLine2(resourceLocation.getAddressLine2())
                    .withAddressTown(resourceLocation.getAddressTown()).withAddressRegion(resourceLocation.getAddressRegion())
                    .withAddressCode(resourceLocation.getAddressCode())
                    .withDomicile(resourceLocation.getAddressDomicileId()).withGoogleId(resourceLocation.getAddressGoogleId());

            representation.setUser(new UserRepresentation().withId(resourceLocation.getUserId()).withFirstName(resourceLocation.getUserFirstName())
                    .withLastName(resourceLocation.getUserLastName()).withEmail(userService.getSecuredUserEmailAddress(resourceLocation.getUserEmail(), user)));

            AddressCoordinatesRepresentation coordinatesRepresentation = null;
            BigDecimal locationLatitude = resourceLocation.getAddressCoordinateLatitude();
            if (locationLatitude != null) {
                coordinatesRepresentation = new AddressCoordinatesRepresentation().withLatitude(locationLatitude).withLongitude(
                        resourceLocation.getAddressCoordinateLongitude());
            }
            addressRepresentation.setCoordinates(coordinatesRepresentation);

            representation.setAddress(addressRepresentation);
        }

        if (resource.getStateId().name().endsWith("_UNSUBMITTED")) {
            representation.setCompletionAction(actionMapper.getActionRepresentation(PrismAction.valueOf(resource.getScope().name() + "_COMPLETE")));
        }

        return representation;
    }

    private <T extends Resource, V extends ResourceRepresentationIdentity> V getResourceRepresentation(T resource, Class<V> returnType) {
        V representation = BeanUtils.instantiate(returnType);

        representation.setScope(resource.getResourceScope());
        representation.setId(resource.getId());

        if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            representation.setName(((ResourceParent) resource).getName());
            representation.setLogoImage(documentMapper.getDocumentRepresentation(resource.getInstitution().getLogoImage()));
        }

        return representation;
    }

    private <T extends ResourceIdentityDTO, V extends ResourceRepresentationIdentity> V getResourceRepresentation(PrismScope resourceScope, T resourceDTO,
            Class<V> returnType) {
        V representation = BeanUtils.instantiate(returnType);

        representation.setScope(resourceScope);
        representation.setId(resourceDTO.getId());

        String name = resourceDTO.getName();
        if (name != null) {
            representation.setName(name);
        }

        Integer logoImageId = resourceDTO.getLogoImageId();
        if (logoImageId != null) {
            representation.setLogoImage(documentMapper.getDocumentRepresentation(logoImageId));
        }

        return representation;
    }

    private List<ResourceSummaryPlotConstraintRepresentation> getResourceSummaryPlotConstraintRepresentation(ResourceReportFilterDTO filterDTO) {
        return filterDTO.getProperties().stream()
                .map(propertyDTO -> new ResourceSummaryPlotConstraintRepresentation().withEntityId(propertyDTO.getEntityId())
                        .withType(propertyDTO.getEntityType())).collect(Collectors.toList());
    }

    private void setRaisesUrgentFlag(ResourceRepresentationStandard representation, List<ActionRepresentationSimple> actions) {
        for (ActionRepresentationSimple action : actions) {
            if (BooleanUtils.isTrue(action.getRaisesUrgentFlag())) {
                representation.setRaisesUrgentFlag(true);
                break;
            }
        }
    }

    private void setRaisesUpdateFlag(ResourceRepresentationStandard representation, DateTime baseline, DateTime updatedTimestamp) {
        representation.setRaisesUpdateFlag(updatedTimestamp.isAfter(baseline.minusDays(1)));
    }

    private void setInstitutionLogoImage(ResourceListRowDTO row, ResourceListRowRepresentation representation) {
        Integer institutionLogoImage = row.getLogoImageId();
        if (institutionLogoImage != null) {
            representation.setLogoImage(documentMapper.getDocumentRepresentation(institutionLogoImage));
        }
    }

    private void setRobotResourceRepresentation(ResourceRepresentationRobot representation, Resource resource) {
        PrismScope resourceScope = resource.getResourceScope();
        ResourceRepresentationRobotMetadata resourceRepresentation = resourceService.getResourceRobotMetadataRepresentation(resource,
                stateService.getPublishedResourceStates(resourceScope), scopeService.getChildScopesWithActiveStates(resourceScope, PROJECT));
        resourceRepresentation.setAuthor(resource.getUser().getRobotRepresentation());
        resourceRepresentation.setThumbnailUrl(getResourceThumbnailUrlRobot(resource));
        resourceRepresentation.setResourceUrl(getResourceUrlRobot(resource));
        setProperty(representation, resourceScope.getLowerCamelName(), resourceRepresentation);
    }

    private List<PrismScopeSectionDefinition> getResourceAdvertIncompleteSectionRepresentation(String advertIncompleteSection) {
        List<PrismScopeSectionDefinition> incompleteSections = Lists.newLinkedList();
        if (advertIncompleteSection != null) {
            for (String section : Splitter.on("|").omitEmptyStrings().split(advertIncompleteSection)) {
                incompleteSections.add(PrismScopeSectionDefinition.valueOf(section));
            }
        }
        return incompleteSections;
    }

    private static class ResourceProcessingMonth {

        private String year;

        private Integer month;

        public ResourceProcessingMonth(String year, Integer month) {
            this.year = year;
            this.month = month;
        }

        public String getYear() {
            return year;
        }

        public Integer getMonth() {
            return month;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(year, month);
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            final ResourceProcessingMonth other = (ResourceProcessingMonth) object;
            return Objects.equal(year, other.getYear()) && Objects.equal(month, other.getMonth());
        }

    }

}
