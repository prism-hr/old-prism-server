package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.PrismConstants.ANGULAR_HASH;
import static com.zuehlke.pgadmissions.PrismConstants.GEOCODING_PRECISION;
import static com.zuehlke.pgadmissions.PrismConstants.TARGETING_PRECISION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EXTERNAL_HOMEPAGE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_RELATED_USERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.decimalObjectToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
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
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeSectionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceIdentityDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceSimpleDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceStandardDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceTargetDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressCoordinatesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceChildCreationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceCountRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationIdentity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationLocation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationMetadataUserRelated;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobot;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationRobotMetadata;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationStandard;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceStudyOptionInstanceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotConstraintRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationWeek;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.ResourceRepresentationTarget;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

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
    private CommentMapper commentMapper;

    @Inject
    private DepartmentMapper departmentMapper;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private InstitutionMapper institutionMapper;

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private RoleMapper roleMapper;

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

    public List<ResourceListRowRepresentation> getResourceListRowRepresentations(PrismScope resourceScope, List<ResourceListRowDTO> rows) {
        DateTime baseline = new DateTime();
        List<ResourceListRowRepresentation> representations = Lists.newArrayListWithCapacity(rows.size());

        for (ResourceListRowDTO row : rows) {
            ResourceListRowRepresentation representation = new ResourceListRowRepresentation();
            representation.setScope(resourceScope);
            representation.setId(row.getResourceId());

            Integer institutionId = row.getInstitutionId();
            Integer departmentId = row.getDepartmentId();
            Integer programId = row.getProgramId();
            Integer projectId = row.getProjectId();

            if (resourceScope.equals(INSTITUTION)) {
                representation.setName(row.getInstitutionName());
                setInstitutionLogoImage(row, representation);
            } else {
                representation.setInstitution(new ResourceRepresentationSimple().withScope(INSTITUTION)
                        .withId(institutionId).withName(row.getInstitutionName()));

                Integer institutionLogoImageId = row.getInstitutionLogoImageId();
                if (institutionLogoImageId != null) {
                    representation.setLogoImage(documentMapper.getDocumentRepresentation(institutionId));
                }
            }

            if (resourceScope.equals(DEPARTMENT)) {
                representation.setName(row.getDepartmentName());
            } else if (departmentId != null) {
                representation.setDepartment(new ResourceRepresentationSimple().withScope(DEPARTMENT)
                        .withId(departmentId).withName(row.getDepartmentName()));
            }

            if (resourceScope.equals(PROGRAM)) {
                representation.setName(row.getProgramName());
            } else if (programId != null) {
                representation.setProgram(new ResourceRepresentationSimple().withScope(PROGRAM)
                        .withId(programId).withName(row.getProgramName()));
            }

            if (resourceScope.equals(PROJECT)) {
                representation.setName(row.getProjectName());
            } else if (projectId != null) {
                representation.setProject(new ResourceRepresentationSimple().withScope(PROJECT)
                        .withId(projectId).withName(row.getProjectName()));
            }

            representation.setCode(row.getCode());

            UserRepresentationSimple userRepresentation = new UserRepresentationSimple();
            userRepresentation.setId(row.getUserId());
            userRepresentation.setFirstName(row.getUserFirstName());
            userRepresentation.setFirstName2(row.getUserFirstName2());
            userRepresentation.setFirstName3(row.getUserFirstName3());
            userRepresentation.setLastName(row.getUserLastName());
            userRepresentation.setEmail(row.getUserEmail());
            userRepresentation.setAccountImageUrl(row.getUserAccountImageUrl());
            representation.setUser(userRepresentation);

            representation.setApplicationRatingAverage(row.getApplicationRatingAverage());

            representation.setState(stateMapper.getStateRepresentationSimple(row.getStateId()));
            representation.setSecondaryStates(stateMapper.getStateRepresentations(row.getSecondaryStateIds()));

            List<ActionRepresentationSimple> actions = actionMapper.getActionRepresentations(row.getActions());
            DateTime updatedTimestamp = row.getUpdatedTimestamp();

            representation.setCreatedTimestamp(row.getCreatedTimestamp());
            representation.setUpdatedTimestamp(updatedTimestamp);

            setRaisesUrgentFlag(representation, actions);
            setRaisesUpdateFlag(representation, baseline, updatedTimestamp);
            representation.setSequenceIdentifier(row.getSequenceIdentifier());

            representation.setAdvertIncompleteSections(getResourceAdvertIncompleteSectionRepresentation(row.getAdvertIncompleteSection()));
            representation.setActions(actions);
            representations.add(representation);
        }

        return representations;
    }

    public ResourceRepresentationLocation getResourceRepresentationLocation(Resource resource) {
        ResourceRepresentationLocation representation = getResourceRepresentationSimple(resource, ResourceRepresentationLocation.class);
        representation.setAddress(advertMapper.getAdvertAddressRepresentation(resource.getAdvert()));
        return representation;
    }

    public ResourceRepresentationLocation getResourceRepresentationLocation(ResourceTargetDTO resource) {
        return getResourceRepresentationLocation(resource, ResourceRepresentationLocation.class);
    }

    public List<ResourceRepresentationTarget> getResourceTargetingRepresentations(Advert currentAdvert, List<Integer> subjectAreas, List<Integer> institutions,
            List<Integer> departments) {
        return resourceService.getResourceTargets(currentAdvert, subjectAreas, institutions, departments).stream().map(this::getResourceRepresentationTargeting)
                .collect(Collectors.toList());
    }

    public ResourceRepresentationTarget getResourceRepresentationTargeting(ResourceTargetDTO resource) {
        ResourceRepresentationTarget representation = getResourceRepresentationLocation(resource, ResourceRepresentationTarget.class);

        BigDecimal targetingRelevance = resource.getTargetingRelevance();
        if (targetingRelevance != null) {
            representation.setRelevance(targetingRelevance.setScale(TARGETING_PRECISION, HALF_UP));
        }

        BigDecimal targetingDistance = resource.getTargetingDistance();
        if (targetingDistance != null) {
            representation.setDistance(targetingDistance.setScale(TARGETING_PRECISION, HALF_UP));
        }

        representation.setSelected(resource.getSelected());

        Set<ResourceTargetDTO> departments = resource.getDepartments();
        if (!departments.isEmpty()) {
            representation.setDepartments(departments.stream().map(this::getResourceRepresentationTargeting).collect(Collectors.toList()));
        }

        return representation;
    }

    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationSimple(T resource) {
        return getResourceRepresentationSimple(resource, ResourceRepresentationExtended.class);
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
        V representation = getResourceRepresentationStandard(resource, returnType, actions, overridingRoles);

        representation.setActions(actions);
        representation.setTimeline(commentMapper.getCommentTimelineRepresentation(resource, currentUser, overridingRoles));
        representation.setUserRoles(roleMapper.getResourceUserRoleRepresentations(resource));

        representation.setWorkflowConfigurations(resourceService.getWorkflowPropertyConfigurations(resource));
        representation.setConditions(getResourceConditionRepresentations(resource));
        return representation;
    }

    public <T extends Resource> ResourceRepresentationStandard getResourceRepresentationStandard(T resource) {
        User currentUser = userService.getCurrentUser();
        List<ActionRepresentationExtended> actions = actionMapper.getActionRepresentations(resource, currentUser);
        return getResourceRepresentationStandard(resource, ResourceRepresentationStandard.class, actions, roleService.getRolesOverridingRedactions(resource));
    }

    public <T extends ResourceParent, V extends ResourceParentRepresentation> V getResourceParentRepresentation(T resource, Class<V> returnType,
            List<PrismRole> overridingRoles) {
        V representation = getResourceRepresentationExtended(resource, returnType, overridingRoles);
        representation.setAdvert(advertMapper.getAdvertRepresentationSimple(resource.getAdvert()));
        representation.setAdvertIncompleteSections(getResourceAdvertIncompleteSectionRepresentation(resource.getAdvertIncompleteSection()));
        representation.setPartnerActions(actionService.getPartnerActions(resource));

        List<ResourceRepresentationIdentity> resourcesNotYetEndorsedFor = Lists.newLinkedList();
        for (PrismScope scope : new PrismScope[] { INSTITUTION, DEPARTMENT }) {
            resourceService.getResourcesNotYetEndorsedFor(resource).forEach(resourceNotYetEndorsedFor -> {
                resourcesNotYetEndorsedFor.add(getResourceRepresentation(scope, resourceNotYetEndorsedFor, ResourceRepresentationIdentity.class));
            });
        }

        representation.setResourcesNotYetEndorsedFor(resourcesNotYetEndorsedFor);
        return representation;
    }

    public <T extends ResourceOpportunity, V extends ResourceOpportunityRepresentation> V getResourceOpportunityRepresentation(T resource, Class<V> returnType,
            List<PrismRole> overridingRoles) {
        V representation = getResourceParentRepresentation(resource, returnType, overridingRoles);

        List<ImportedEntityResponse> studyOptions = Lists.newLinkedList();
        for (ImportedEntitySimple studyOption : resourceService.getStudyOptions(resource)) {
            studyOptions.add(importedEntityMapper.getImportedEntityRepresentation(studyOption));
        }

        representation.setOpportunityType(PrismOpportunityType.valueOf(resource.getOpportunityType().getName()));
        representation.setOpportunityCategory(resource.getOpportunityCategory());
        representation.setStudyOptions(studyOptions);
        representation.setStudyLocations(resourceService.getStudyLocations(resource));
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
        Class<?> resourceClass = resource.getClass();
        List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(resource);

        if (Institution.class.equals(resourceClass)) {
            return institutionMapper.getInstitutionRepresentationClient((Institution) resource, overridingRoles);
        } else if (Department.class.equals(resourceClass)) {
            return departmentMapper.getDepartmentRepresentationClient((Department) resource, overridingRoles);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            return getResourceOpportunityRepresentationClient((ResourceOpportunity) resource, ProgramRepresentationClient.class, overridingRoles);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            ApplicationRepresentationClient representation = applicationMapper.getApplicationRepresentationClient((Application) resource, overridingRoles);
            return representation;
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class, overridingRoles);
    }

    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationExport(T resource) throws Exception {
        Class<?> resourceClass = resource.getClass();
        List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(resource);

        if (Institution.class.equals(resourceClass)) {
            return institutionMapper.getInstitutionRepresentation((Institution) resource, overridingRoles);
        } else if (Department.class.equals(resourceClass)) {
            return departmentMapper.getDepartmentRepresentation((Department) resource, overridingRoles);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            return getResourceOpportunityRepresentation((ResourceOpportunity) resource, ResourceOpportunityRepresentationClient.class, overridingRoles);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            return applicationMapper.getApplicationRepresentationExport((Application) resource, overridingRoles);
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

        List<ApplicationProcessingSummaryRepresentationYear> yearRepresentations = Lists.newLinkedList();
        List<ApplicationProcessingSummaryDTO> yearSummaries = getApplicationProcessingSummariesByYear(resource, constraints);
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> monthSummaries = getApplicationProcessingSummariesByMonth(resource, constraints);
        LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> weekSummaries = getApplicationProcessingSummariesByWeek(resource,
                constraints);

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

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(
            ResourceParent resource, List<ResourceReportFilterPropertyDTO> constraints) {
        return applicationService.getApplicationProcessingSummariesByYear(resource, constraints);
    }

    public LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(
            ResourceParent resource, List<ResourceReportFilterPropertyDTO> constraints) {
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationService.getApplicationProcessingSummariesByMonth(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(processingSummary.getApplicationYear(), processingSummary);
        }
        return index;
    }

    public LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(
            ResourceParent resource, List<ResourceReportFilterPropertyDTO> constraints) {
        LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationService.getApplicationProcessingSummariesByWeek(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(new ResourceProcessingMonth(processingSummary.getApplicationYear(), processingSummary.getApplicationMonth()), processingSummary);
        }
        return index;
    }

    public ResourceStudyOptionInstanceRepresentation getResourceStudyOptionInstanceRepresentation(ResourceStudyOptionInstance resourceStudyOptionInstance) {
        return new ResourceStudyOptionInstanceRepresentation().withApplicationStartDate(resourceStudyOptionInstance.getApplicationStartDate())
                .withApplicationCloseDate(resourceStudyOptionInstance.getApplicationCloseDate())
                .withBusinessYear(resourceStudyOptionInstance.getBusinessYear()).withIdentifier(resourceStudyOptionInstance.getIdentifier());
    }

    public List<ResourceConditionRepresentation> getResourceConditionRepresentations(Resource resource) {
        return resource.getResourceConditions().stream()
                .map(condition -> new ResourceConditionRepresentation().withActionCondition(condition.getActionCondition())
                        .withPartnerMode(condition.getPartnerMode()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceParent> ResourceRepresentationRobot getResourceRepresentationRobot(T resource) {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);

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

        Set<Resource> userResources = resourceScope.equals(PROJECT) ? Sets.newHashSet(resource) : (Set<Resource>) getProperty(resource, "projects");

        if (!userResources.isEmpty()) {
            List<User> users = userService.getUsersForResourcesAndRoles(userResources, PROJECT_SUPERVISOR_GROUP.getRoles());
            List<String> relatedUsers = users.stream().map(User::getRobotRepresentation).collect(Collectors.toList());

            if (!relatedUsers.isEmpty()) {
                representation.setRelatedUsers(new ResourceRepresentationMetadataUserRelated().withLabel(loader.loadLazy(SYSTEM_OPPORTUNITIES_RELATED_USERS))
                        .withUsers(relatedUsers));
            }
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

    public List<ResourceChildCreationRepresentation> getResourceTargetingRepresentations(String searchTerm) {
        PrismScope initialResourceScope = null;
        LinkedHashMap<PrismScope, TreeSet<ResourceTargetDTO>> resources = Maps.newLinkedHashMap();
        for (PrismScope resourceScope : scopeService.getChildScopesAscending(SYSTEM, DEPARTMENT)) {
            initialResourceScope = initialResourceScope == null ? resourceScope : initialResourceScope;
            TreeSet<ResourceTargetDTO> sortedResources = Sets.newTreeSet(resourceService.getResourcesWhichPermitTargeting(resourceScope, searchTerm));
            resources.put(resourceScope, sortedResources);
        }

        return getResourceRepresentationHierarchy(initialResourceScope, resources, PROGRAM);
    }

    public List<ResourceChildCreationRepresentation> getResourceChildCreationRepresentations(
            PrismScope filterScope, Integer filterResourceId, PrismScope creationScope, PrismScope stopScope, String searchTerm) {
        PrismScope initialResourceScope = null;
        LinkedHashMap<PrismScope, TreeSet<ResourceChildCreationDTO>> resources = Maps.newLinkedHashMap();
        for (PrismScope resourceScope : scopeService.getChildScopesAscending(filterScope, PROGRAM)) {
            initialResourceScope = initialResourceScope == null ? resourceScope : initialResourceScope;
            TreeSet<ResourceChildCreationDTO> sortedResources = Sets.newTreeSet(resourceService.getResourcesWhichPermitChildResourceCreation(filterScope,
                    filterResourceId, resourceScope, creationScope, searchTerm));
            resources.put(resourceScope, sortedResources);
        }

        return getResourceRepresentationHierarchy(initialResourceScope, resources, stopScope);
    }

    public <T extends ResourceRepresentationLocation> T getResourceRepresentationLocation(ResourceTargetDTO resource, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        Integer logoImageId = resource.getInstitutionLogoImageId();
        representation.setId(resource.getId());
        representation.setScope(resource.getScope());
        representation.setName(resource.getName());
        representation.setLogoImage(Optional.ofNullable(logoImageId).map(id -> new DocumentRepresentation().withId(logoImageId)).orElse(null));

        representation.setAddress(new AddressAdvertRepresentation().withDomicile(new ImportedAdvertDomicileResponse()
                .withName(resource.getAddressDomicileName())).withAddressLine1(resource.getAddressLine1())
                .withAddressLine2(resource.getAddressLine2()).withAddressTown(resource.getAddressTown())
                .withAddressRegion(resource.getAddressRegion()).withAddressCode(resource.getAddressCode())
                .withGoogleId(resource.getAddressGoogleId()).withCoordinates(new AddressCoordinatesRepresentation()
                        .withLatitude(decimalObjectToBigDecimal(resource.getAddressCoordinateLatitude(), GEOCODING_PRECISION))
                        .withLongitude(decimalObjectToBigDecimal(resource.getAddressCoordinateLongitude(), GEOCODING_PRECISION))));

        return representation;
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource, V extends ResourceRepresentationStandard> V getResourceRepresentationStandard(T resource, Class<V> returnType,
            List<ActionRepresentationExtended> actions, List<PrismRole> overridingRoles) {
        V representation = getResourceRepresentationSimple(resource, returnType);
        representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser()));

        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(resource.getResourceScope(), INSTITUTION);
        ResourceStandardDTO resourceWithParents = resourceService.getResourceWithParents(resource, parentScopes);

        for (PrismScope parentScope : parentScopes) {
            ResourceStandardDTO parentResource = resourceWithParents.getEnclosingResource(parentScope);
            if (parentResource != null) {
                ResourceRepresentationSimple parentRepresentation = new ResourceRepresentationSimple().withScope(parentScope).withId(parentResource.getId())
                        .withName(parentResource.getName());
                if (parentScope.equals(INSTITUTION)) {
                    parentRepresentation.setLogoImage(new DocumentRepresentation().withId(parentResource.getInstitutionLogoImageId()));
                }
                representation.setParentResource(parentRepresentation);
            }
        }

        DateTime updatedTimestamp = resource.getUpdatedTimestamp();

        setRaisesUrgentFlag(representation, (List<ActionRepresentationSimple>) (List<?>) actions);
        setRaisesUpdateFlag(representation, new DateTime(), updatedTimestamp);

        if (ResourceParent.class.isAssignableFrom(resource.getClass()) && !hasRedactions(resource, overridingRoles)) {
            representation.setApplicationRatingAverage(((ResourceParent) resource).getApplicationRatingAverage());
            representation.setOpportunityRatingAverage(((ResourceParent) resource).getOpportunityRatingAverage());
        }

        representation.setPreviousState(stateMapper.getStateRepresentationSimple(resource.getPreviousState()));
        representation.setSecondaryStates(stateMapper.getSecondaryStateRepresentations(resource));

        representation.setCreatedTimestamp(resource.getCreatedTimestamp());
        representation.setUpdatedTimestamp(updatedTimestamp);
        representation.setSequenceIdentifier(resource.getSequenceIdentifier());
        return representation;
    }

    @SuppressWarnings("unchecked")
    private <T extends ResourceStandardDTO> List<ResourceChildCreationRepresentation> getResourceRepresentationHierarchy(
            PrismScope resourceScope, LinkedHashMap<PrismScope, TreeSet<T>> resources, PrismScope stopScope) {
        for (Entry<PrismScope, TreeSet<T>> resourceEntries : Lists.reverse(Lists.newLinkedList(resources.entrySet()))) {
            PrismScope resourceEntryScope = resourceEntries.getKey();
            List<PrismScope> parentEntryScopes = scopeService.getParentScopesDescending(resourceEntryScope, resourceScope);
            for (T resourceEntry : resourceEntries.getValue()) {
                for (PrismScope parentEntryScope : parentEntryScopes) {
                    T parentEntry = (T) resourceEntry.getEnclosingResource(parentEntryScope);
                    if (parentEntry != null) {
                        resources.get(parentEntryScope).add(parentEntry);
                    }
                }
            }
        }

        int level = 0;
        int depth = resources.keySet().size();
        Boolean isResourceUserCreation = null;
        boolean isLoggedInSession = userService.isLoggedInSession();

        Map<T, ResourceChildCreationRepresentation> index = Maps.newHashMap();
        Set<ResourceChildCreationRepresentation> representations = Sets.newLinkedHashSet();
        for (Entry<PrismScope, TreeSet<T>> resourceEntry : resources.entrySet()) {
            if (resourceEntry.getKey().ordinal() <= stopScope.ordinal()) {
                for (T resource : resourceEntry.getValue()) {
                    isResourceUserCreation = isResourceUserCreation == null ? resource.getClass().equals(ResourceChildCreationDTO.class) : isResourceUserCreation;
                    ResourceChildCreationRepresentation resourceChildCreationRepresentation = getResourceChildCreationRepresentation(resource, isLoggedInSession);
                    if (level == 0) {
                        representations.add(resourceChildCreationRepresentation);
                    } else {
                        index.get(resource.getParentResource()).addChildResource(resourceChildCreationRepresentation);
                    }

                    if (!checkNotResourceUserCreationDisabledLeaf(level, depth, isResourceUserCreation, resourceChildCreationRepresentation)) {
                        index.put(resource, resourceChildCreationRepresentation);
                    }
                }
            }
            level++;
        }
        return Lists.newArrayList(representations);
    }

    private boolean checkNotResourceUserCreationDisabledLeaf(int level, int depth, Boolean isResourceUserCreation,
            ResourceChildCreationRepresentation resourceChildCreationRepresentation) {
        return BooleanUtils.isTrue(isResourceUserCreation) && level == (depth - 1) && BooleanUtils.isFalse(resourceChildCreationRepresentation.getPartnerMode())
                && isEmpty(resourceChildCreationRepresentation.getChildResources());
    }

    private <T extends ResourceStandardDTO> ResourceChildCreationRepresentation getResourceChildCreationRepresentation(T resource, boolean isLoggedInSession) {
        ResourceChildCreationRepresentation representation = new ResourceChildCreationRepresentation() //
                .withScope(resource.getScope()).withId(resource.getId()).withName(resource.getName());
        if (resource.getClass().equals(ResourceChildCreationDTO.class)) {
            Boolean partnerMode = ((ResourceChildCreationDTO) resource).getPartnerMode();
            representation.setPartnerMode(isLoggedInSession ? partnerMode != null : BooleanUtils.isTrue(partnerMode));
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

        Integer institutionLogoImageId = resourceDTO.getInstitutionLogoImageId();
        if (institutionLogoImageId != null) {
            representation.setLogoImage(documentMapper.getDocumentRepresentation(institutionLogoImageId));
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
        Integer institutionLogoImage = row.getInstitutionLogoImageId();
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

    private boolean hasRedactions(Resource resource, List<PrismRole> overridingRoles) {
        User user = userService.getCurrentUser();
        return !actionService.getRedactions(resource, user, overridingRoles).isEmpty();
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
