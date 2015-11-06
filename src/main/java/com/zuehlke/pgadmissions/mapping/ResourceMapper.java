package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.PrismConstants.ANGULAR_HASH;
import static com.zuehlke.pgadmissions.PrismConstants.RESOURCE_LIST_PAGE_ROW_COUNT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EXTERNAL_HOMEPAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.getSummaryRepresentations;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.processRowDescriptors;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListConstraint;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.ResourceConnectionDTO;
import com.zuehlke.pgadmissions.dto.ResourceFlatToNestedDTO;
import com.zuehlke.pgadmissions.dto.ResourceIdentityDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.ResourceLocationDTO;
import com.zuehlke.pgadmissions.dto.ResourceOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.dto.ResourceSimpleDTO;
import com.zuehlke.pgadmissions.exceptions.PrismForbiddenException;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.address.AddressCoordinatesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ProjectRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceCountRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListFilterRepresentation.FilterExpressionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentationRelation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationChildCreation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationConnection;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationIdentity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationLocation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRelation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobot;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadata;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationStandard;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotConstraintRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationWeek;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismListUtils;

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
    private EntityService entityService;

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

    public ResourceListRepresentation getResourceListRepresentation(PrismScope scope, ResourceListFilterDTO filter, String sequenceId) throws Exception {
        DateTime baseline = new DateTime();
        List<ResourceListRowRepresentation> representations = Lists.newArrayList();

        User user = userService.getCurrentUser();
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);

        Set<Integer> resourceIds = Sets.newHashSet();
        Map<String, Integer> summaries = Maps.newHashMap();
        Set<Integer> onlyAsPartnerResourceIds = Sets.newHashSet();
        List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);
        Set<ResourceOpportunityCategoryDTO> resources = resourceService.getResources(user, scope, parentScopes, targeterEntities, filter);
        processRowDescriptors(resources, resourceIds, onlyAsPartnerResourceIds, summaries);

        resourceService.getResourceList(user, scope, parentScopes, targeterEntities, filter, RESOURCE_LIST_PAGE_ROW_COUNT, sequenceId, resourceIds, onlyAsPartnerResourceIds, true)
                .forEach(row -> {
                    ResourceListRowRepresentation representation = new ResourceListRowRepresentation();
                    representation.setScope(scope);
                    representation.setId(row.getResourceId());

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
                    representation.setUser(userMapper.getUserRepresentationSimple(row));
                    representation.setApplicationRatingAverage(row.getApplicationRatingAverage());

                    representation.setState(stateMapper.getStateRepresentationSimple(row.getStateId()));
                    representation.setSecondaryStates(stateMapper.getStateRepresentations(row.getSecondaryStateIds()));

                    List<ActionRepresentationSimple> actions = actionMapper.getActionRepresentations(row);
                    DateTime updatedTimestamp = row.getUpdatedTimestamp();

                    representation.setCreatedTimestamp(row.getCreatedTimestamp());
                    representation.setUpdatedTimestamp(updatedTimestamp);

                    setRaisesUrgentFlag(representation, actions);
                    setRaisesUpdateFlag(representation, baseline, updatedTimestamp);
                    representation.setSequenceIdentifier(row.getSequenceIdentifier());

                    representation.setAdvertIncompleteSections(getResourceAdvertIncompleteSectionRepresentation(row.getAdvertIncompleteSection()));
                    representation.setActions(actions);
                    representations.add(representation);
                });

        Map<String, Integer> urgentSummaries = Maps.newHashMap();
        Set<ResourceOpportunityCategoryDTO> urgentResources = resources.stream().filter(r -> BooleanUtils.isTrue(r.getRaisesUrgentFlag())).collect(Collectors.toSet());
        PrismListUtils.processRowSummaries(urgentResources, urgentSummaries);

        return new ResourceListRepresentation().withRows(representations).withSummaries(getSummaryRepresentations(summaries))
                .withUrgentSummaries(getSummaryRepresentations(urgentSummaries));
    }

    public ResourceRepresentationLocation getResourceRepresentationLocation(Resource resource) {
        ResourceRepresentationLocation representation = getResourceRepresentationSimple(resource, ResourceRepresentationLocation.class);
        representation.setAddress(advertMapper.getAdvertAddressRepresentation(resource.getAdvert()));
        return representation;
    }

    public <T extends ResourceSimpleDTO> ResourceRepresentationLocation getResourceRepresentationCreation(T resource) {
        return getResourceRepresentationLocation(resource, ResourceRepresentationLocation.class);
    }

    public <T extends Resource> ResourceRepresentationSimple getResourceRepresentationSimple(T resource) {
        return getResourceRepresentationSimple(resource, ResourceRepresentationSimple.class);
    }

    public <T extends ResourceOpportunity> ResourceOpportunityRepresentationSimple getResourceOpportunityRepresentationSimple(T resource) {
        ResourceOpportunityRepresentationSimple representation = getResourceRepresentationSimple(resource, ResourceOpportunityRepresentationSimple.class);
        representation.setAvailableDate(resource.getAvailableDate());
        representation.setDurationMinimum(resource.getDurationMinimum());
        representation.setDurationMaximum(resource.getDurationMaximum());
        return representation;
    }

    public ResourceRepresentationIdentity getResourceRepresentationChildCreation(ResourceChildCreationDTO resourceDTO) {
        ResourceRepresentationChildCreation representation = getResourceRepresentation(resourceDTO.getScope(), resourceDTO, ResourceRepresentationChildCreation.class);
        representation.setCreateDirectly(resourceDTO.getCreateDirectly());
        return representation;
    }

    public <T extends ResourceSimpleDTO> ResourceRepresentationSimple getResourceRepresentationSimple(PrismScope resourceScope, T resourceDTO) {
        return getResourceRepresentationSimple(resourceScope, resourceDTO, ResourceRepresentationSimple.class);
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

    public <T extends ResourceSimpleDTO, V extends ResourceRepresentationSimple> V getResourceRepresentationSimple(PrismScope resourceScope, T resourceDTO, Class<V> returnType) {
        V representation = getResourceRepresentation(resourceScope, resourceDTO, returnType);
        representation.setCode(resourceDTO.getCode());
        representation.setImportedCode(resourceDTO.getImportedCode());

        PrismState stateId = resourceDTO.getStateId();
        if (stateId != null) {
            representation.setState(stateMapper.getStateRepresentationSimple(stateId));
        }

        return representation;
    }

    public <T extends Resource, V extends ResourceRepresentationExtended> V getResourceRepresentationExtended(T resource, Class<V> returnType, List<PrismRole> overridingRoles) {
        User currentUser = userService.getCurrentUser();
        List<ActionRepresentationExtended> actions = actionMapper.getActionRepresentations(resource, currentUser);
        V representation = getResourceRepresentationRelation(resource, returnType, actions, overridingRoles);

        representation.setActions(actions);

        representation.setConditions(getResourceConditionRepresentations(resource));
        return representation;
    }

    public <T extends Resource> ResourceRepresentationStandard getResourceRepresentationStandard(T resource) {
        User currentUser = userService.getCurrentUser();
        List<ActionRepresentationExtended> actions = actionMapper.getActionRepresentations(resource, currentUser);
        return getResourceRepresentationRelation(resource, ResourceRepresentationStandard.class, actions, roleService.getRolesOverridingRedactions(resource));
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

    public <T extends ResourceParent, V extends ResourceParentRepresentation> V getResourceParentRepresentation(T resource, Class<V> returnType, List<PrismRole> overridingRoles) {
        resourceService.setResourceAdvertIncompleteSection(resource);
        entityService.flush();

        V representation = getResourceRepresentationExtended(resource, returnType, overridingRoles);
        representation.setAdvert(advertMapper.getAdvertRepresentationSimple(resource.getAdvert()));
        representation.setOpportunityCategories(asList(resource.getOpportunityCategories().split("\\|")).stream().map(PrismOpportunityCategory::valueOf).collect(toList()));
        representation.setAdvertIncompleteSections(getResourceAdvertIncompleteSectionRepresentation(resource.getAdvertIncompleteSection()));
        return representation;
    }

    public <T extends ResourceOpportunity, V extends ResourceOpportunityRepresentation> V getResourceOpportunityRepresentation(T resource, Class<V> returnType,
            List<PrismRole> overridingRoles) {
        V representation = getResourceParentRepresentation(resource, returnType, overridingRoles);

        representation.setOpportunityType(resource.getOpportunityType().getId());
        representation.setOpportunityCategory(PrismOpportunityCategory.valueOf(resource.getOpportunityCategories()));
        representation.setStudyOptions(resourceService.getStudyOptions(resource));

        representation.setAvailableDate(resource.getAvailableDate());
        representation.setDurationMinimum(resource.getDurationMinimum());
        representation.setDurationMaximum(resource.getDurationMaximum());
        return representation;
    }

    public <T extends ResourceOpportunity, V extends ResourceOpportunityRepresentationClient> V getResourceOpportunityRepresentationClient(T resource, Class<V> returnType,
            List<PrismRole> overridingRoles) {
        V representation = getResourceOpportunityRepresentation(resource, returnType, overridingRoles);
        appendResourceSummaryRepresentation(resource, representation);
        return representation;
    }

    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationClient(T resource) {
        validateViewerPermission(resource);
        Class<?> resourceClass = resource.getClass();
        List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(resource);

        if (Institution.class.equals(resourceClass)) {
            return institutionMapper.getInstitutionRepresentationClient((Institution) resource, overridingRoles);
        } else if (Department.class.equals(resourceClass)) {
            return departmentMapper.getDepartmentRepresentationClient((Department) resource, overridingRoles);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            Class<? extends ResourceOpportunityRepresentationClient> representationType = Program.class.equals(resourceClass) ? ProgramRepresentationClient.class
                    : ProjectRepresentationClient.class;
            return getResourceOpportunityRepresentationClient((ResourceOpportunity) resource, representationType, overridingRoles);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            ApplicationRepresentationClient representation = applicationMapper.getApplicationRepresentationClient((Application) resource, overridingRoles);
            return representation;
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class, overridingRoles);
    }

    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationExport(T resource) {
        validateViewerPermission(resource);
        Class<?> resourceClass = resource.getClass();
        List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(resource);

        if (Institution.class.equals(resourceClass)) {
            return institutionMapper.getInstitutionRepresentation((Institution) resource, overridingRoles);
        } else if (Department.class.equals(resourceClass)) {
            return departmentMapper.getDepartmentRepresentation((Department) resource, overridingRoles);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            return getResourceOpportunityRepresentation((ResourceOpportunity) resource, ResourceOpportunityRepresentation.class, overridingRoles);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            return applicationMapper.getApplicationRepresentationExtended((Application) resource, overridingRoles);
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class, overridingRoles);
    }

    public <T extends ResourceParent, U extends ResourceRepresentationClient> void appendResourceSummaryRepresentation(T resource, U representation) {
        List<ResourceCountRepresentation> counts = Lists.newLinkedList();
        for (PrismScope childScope : scopeService.getChildScopesAscending(resource.getResourceScope(), SYSTEM)) {
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

    public ResourceSummaryPlotDataRepresentation getResourceSummaryPlotDataRepresentation(ResourceParent resource, List<ResourceReportFilterPropertyDTO> constraints) {
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

            constraintsToTransform.keySet().forEach(c -> {
                transformedConstraints.putAll(c, applicationContext.getBean(c.getFilterSelector()).getPossible(resource, APPLICATION, constraintsToTransform.get(c)));
            });
        }

        List<ApplicationProcessingSummaryRepresentationYear> yearRepresentations = Lists.newLinkedList();
        List<ApplicationProcessingSummaryDTO> yearSummaries = getApplicationProcessingSummariesByYear(resource, transformedConstraints);
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> monthSummaries = getApplicationProcessingSummariesByMonth(resource, transformedConstraints);
        LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> weekSummaries = getApplicationProcessingSummariesByWeek(resource, transformedConstraints);

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

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
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

    public <T extends Resource> ResourceRepresentationRelation getResourceRepresentationRelation(T resource) {
        return getResourceRepresentationRelation(resource, ResourceRepresentationRelation.class);
    }

    public <T extends Resource> ResourceRepresentationRelation getResourceOpportunityRepresentationRelation(T resource) {
        return getResourceRepresentationRelation(resource, ResourceOpportunityRepresentationRelation.class);
    }

    public List<ResourceListFilterRepresentation> getResourceListFilterRepresentations() {
        List<ResourceListFilterRepresentation> filters = Lists.newArrayListWithCapacity(PrismResourceListConstraint.values().length);
        for (PrismResourceListConstraint property : PrismResourceListConstraint.values()) {
            List<FilterExpressionRepresentation> filterExpressions = property.getPermittedExpressions().stream()
                    .map(filterExpression -> new FilterExpressionRepresentation(filterExpression, filterExpression.isNegatable()))
                    .collect(Collectors.toList());
            filters.add(new ResourceListFilterRepresentation(property, filterExpressions, property.getPropertyType(), property.getPermittedScopes()));
        }
        return filters;
    }

    public ResourceRepresentationConnection getResourceRepresentationConnection(
            Integer institutionId, String institutionName, Integer logoImageId, Integer departmentId, String departmentName) {
        return getResourceRepresentationConnection(institutionId, institutionName, logoImageId, departmentId, departmentName, null);
    }

    public ResourceRepresentationConnection getResourceRepresentationConnection(
            Integer institutionId, String institutionName, Integer logoImageId, Integer departmentId, String departmentName, Integer backgroundImageId) {
        ResourceRepresentationConnection representation = new ResourceRepresentationConnection().withInstitution(new ResourceRepresentationSimple().withScope(INSTITUTION)
                .withId(institutionId).withName(institutionName).withLogoImage(documentMapper.getDocumentRepresentation(logoImageId)));

        if (departmentId != null) {
            representation.setDepartment(new ResourceRepresentationSimple().withScope(DEPARTMENT).withId(departmentId).withName(departmentName));
        }

        if (backgroundImageId != null) {
            representation.setBackgroundImage(documentMapper.getDocumentRepresentation(backgroundImageId));
        }

        return representation;
    }

    public ResourceRepresentationConnection getResourceRepresentationConnection(ResourceConnectionDTO resource) {
        return getResourceRepresentationConnection(resource.getInstitutionId(), resource.getInstitutionName(), resource.getInstitutionLogoImageId(), resource.getDepartmentId(),
                resource.getDepartmentName());
    }

    private <T extends Resource> void validateViewerPermission(T resource) {
        User user = userService.getCurrentUser();
        Action action = actionService.getViewEditAction(resource);
        List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, resource.getResourceScope());
        if (action == null || !actionService.checkActionVisible(resource, action, user, targeterEntities)) {
            throw new PrismForbiddenException("User cannot view or edit the given resource");
        }
    }

    private <T extends Resource, V extends ResourceRepresentationRelation> V getResourceRepresentationRelation(T resource, Class<V> returnType) {
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
            representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser()));
        }

        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(resource.getResourceScope(), INSTITUTION);
        ResourceFlatToNestedDTO resourceWithParents = resourceService.getResourceWithParentResources(resource, parentScopes);

        for (PrismScope parentScope : parentScopes) {
            ResourceFlatToNestedDTO parentResource = resourceWithParents.getEnclosingResource(parentScope);
            if (parentResource != null) {
                ResourceRepresentationSimple parentRepresentation = new ResourceRepresentationSimple().withScope(parentScope).withId(parentResource.getId())
                        .withName(parentResource.getName());
                if (parentScope.equals(INSTITUTION)) {
                    parentRepresentation.setLogoImage(documentMapper.getDocumentRepresentation(parentResource.getLogoImageId()));
                }
                representation.setParentResource(parentRepresentation);
            }
        }

        return representation;
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource, V extends ResourceRepresentationStandard> V getResourceRepresentationRelation(
            T resource, Class<V> returnType, List<ActionRepresentationExtended> actions, List<PrismRole> overridingRoles) {
        V representation = getResourceRepresentationRelation(resource, returnType);

        DateTime updatedTimestamp = resource.getUpdatedTimestamp();

        setRaisesUrgentFlag(representation, (List<ActionRepresentationSimple>) (List<?>) actions);
        setRaisesUpdateFlag(representation, new DateTime(), updatedTimestamp);

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

    private <T extends ResourceSimpleDTO, U extends ResourceRepresentationLocation> U getResourceRepresentationLocation(T resource, Class<U> returnType) {
        U representation = BeanUtils.instantiate(returnType);

        representation.setId(resource.getId());
        representation.setScope(resource.getScope());
        representation.setName(resource.getName());
        representation.setLogoImage(documentMapper.getDocumentRepresentation(resource.getLogoImageId()));

        if (resource.getClass().equals(ResourceLocationDTO.class)) {
            ResourceLocationDTO resourceLocation = (ResourceLocationDTO) resource;
            AddressRepresentation addressRepresentation = new AddressRepresentation().withAddressLine1(resourceLocation.getAddressLine1())
                    .withAddressLine2(resourceLocation.getAddressLine2())
                    .withAddressTown(resourceLocation.getAddressTown()).withAddressRegion(resourceLocation.getAddressRegion()).withAddressCode(resourceLocation.getAddressCode())
                    .withDomicile(resourceLocation.getAddressDomicileId()).withGoogleId(resourceLocation.getAddressGoogleId());

            representation.setUser(new UserRepresentation().withId(resourceLocation.getUserId()).withFirstName(resourceLocation.getUserFirstName())
                    .withLastName(resourceLocation.getUserLastName()).withEmail(resourceLocation.getUserEmail()));

            AddressCoordinatesRepresentation coordinatesRepresentation = null;
            BigDecimal locationLatitude = resourceLocation.getAddressCoordinateLatitude();
            if (locationLatitude != null) {
                coordinatesRepresentation = new AddressCoordinatesRepresentation().withLatitude(locationLatitude).withLongitude(resourceLocation.getAddressCoordinateLongitude());
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

    private <T extends ResourceIdentityDTO, V extends ResourceRepresentationIdentity> V getResourceRepresentation(PrismScope resourceScope, T resourceDTO, Class<V> returnType) {
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
                .map(propertyDTO -> new ResourceSummaryPlotConstraintRepresentation().withEntityId(propertyDTO.getEntityId()).withType(propertyDTO.getEntityType()))
                .collect(Collectors.toList());
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
                stateService.getActiveResourceStates(resourceScope), scopeService.getChildScopesWithActiveStates(resourceScope, PROJECT));
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
