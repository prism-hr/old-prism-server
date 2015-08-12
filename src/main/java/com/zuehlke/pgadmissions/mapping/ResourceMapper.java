package com.zuehlke.pgadmissions.mapping;

import com.google.common.base.Objects;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO.ResourceChildCreationDTOComparator;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.*;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationWeek;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.*;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EXTERNAL_HOMEPAGE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITIES_RELATED_USERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static com.zuehlke.pgadmissions.utils.PrismConstants.ANGULAR_HASH;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

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
                if (row.getInstitutionLogoImageId() != null) {
                    representation.setLogoImage(documentMapper.getDocumentRepresentation(row.getInstitutionLogoImageId()));
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

            representation.setAdvertIncompleteSection(row.getAdvertIncompleteSection());
            representation.setActions(actions);
            representations.add(representation);
        }

        return representations;
    }

    public <T extends Resource<?>> ResourceRepresentationSimple getResourceRepresentationSimple(T resource) {
        return getResourceRepresentationSimple(resource, ResourceRepresentationSimple.class);
    }

    public <T extends Resource<?>, V extends ResourceRepresentationSimple> V getResourceRepresentationSimple(T resource, Class<V> returnType) {
        V representation = getResourceRepresentation(resource, returnType);
        representation.setCode(resource.getCode());
        representation.setState(stateMapper.getStateRepresentationSimple(resource.getState()));

        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity<?> resourceOpportunity = (ResourceOpportunity<?>) resource;
            representation.setImportedCode(resourceOpportunity.getImportedCode());
        }

        return representation;
    }

    public <T extends Resource<?>> ResourceRepresentationExtended getResourceRepresentationExtended(T resource) throws Exception {
        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource<?>, V extends ResourceRepresentationExtended> V getResourceRepresentationExtended(T resource, Class<V> returnType) {
        DateTime baseline = new DateTime();
        User currentUser = userService.getCurrentUser();

        V representation = getResourceRepresentationSimple(resource, returnType);
        representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser()));

        for (PrismScope parentScope : scopeService.getParentScopesDescending(resource.getResourceScope())) {
            if (!parentScope.equals(SYSTEM)) {
                Resource<?> parentResource = resource.getEnclosingResource(parentScope);
                if (parentResource != null) {
                    representation.setParentResource(getResourceRepresentationSimple(parentResource));
                }
            }
        }

        DateTime updatedTimestamp = resource.getUpdatedTimestamp();
        List<ActionRepresentationExtended> actions = actionMapper.getActionRepresentations(resource, currentUser);

        setRaisesUrgentFlag(representation, (List<ActionRepresentationSimple>) (List<?>) actions);
        setRaisesUpdateFlag(representation, baseline, updatedTimestamp);

        if (ResourceParent.class.isAssignableFrom(resource.getClass()) && !actionService.hasRedactions(resource, userService.getCurrentUser())) {
            representation.setApplicationRatingAverage(((ResourceParent<?>) resource).getApplicationRatingAverage());
        }

        representation.setPreviousState(stateMapper.getStateRepresentationSimple(resource.getPreviousState()));
        representation.setSecondaryStates(stateMapper.getSecondaryStateRepresentations(resource));

        representation.setCreatedTimestamp(resource.getCreatedTimestamp());
        representation.setUpdatedTimestamp(updatedTimestamp);
        representation.setSequenceIdentifier(resource.getSequenceIdentifier());

        representation.setActions(actions);
        representation.setTimeline(commentMapper.getCommentTimelineRepresentation(resource, currentUser));
        representation.setUserRoles(roleMapper.getResourceUserRoleRepresentations(resource));

        representation.setWorkflowConfigurations(resourceService.getWorkflowPropertyConfigurations(resource));
        representation.setConditions(getResourceConditionRepresentations(resource));

        return representation;
    }

    public <T extends ResourceParent<?>, V extends ResourceParentRepresentation> V getResourceParentRepresentation(T resource, Class<V> returnType) {
        V representation = getResourceRepresentationExtended(resource, returnType);

        representation.setAdvert(advertMapper.getAdvertRepresentationSimple(resource.getAdvert()));
        representation.setAdvertIncompleteSection(resource.getAdvertIncompleteSection());

        representation.setPartnerActions(actionService.getPartnerActions(resource));

        return representation;
    }

    public <T extends ResourceOpportunity<?>, V extends ResourceOpportunityRepresentation> V getResourceOpportunityRepresentation(
            T resource, Class<V> returnType) {
        V representation = getResourceParentRepresentation(resource, returnType);

        List<ImportedEntityResponse> studyOptions = Lists.newLinkedList();
        for (ImportedEntitySimple studyOption : resourceService.getStudyOptions(resource)) {
            studyOptions.add(importedEntityMapper.getImportedEntityRepresentation(studyOption));
        }

        representation.setOpportunityType(PrismOpportunityType.valueOf(resource.getOpportunityType().getName()));
        representation.setStudyOptions(studyOptions);
        representation.setStudyLocations(resourceService.getStudyLocations(resource));
        representation.setDurationMinimum(resource.getDurationMinimum());
        representation.setDurationMaximum(resource.getDurationMaximum());
        return representation;

    }

    public <T extends Department, V extends DepartmentRepresentationClient> V getDepartmentRepresentationClient(T resource, Class<V> returnType) {
        V representation = getResourceParentRepresentation(resource, returnType);
        List<ImportedEntityResponse> programRepresentations = resource.getImportedPrograms().stream()
                .map(program -> (ImportedEntityResponse) importedEntityMapper.getImportedEntityRepresentation(program)).collect(Collectors.toList());
        representation.setImportedPrograms(programRepresentations);
        appendResourceSummaryRepresentation(resource, representation);
        return representation;
    }

    public <T extends ResourceOpportunity<?>, V extends ResourceOpportunityRepresentationClient> V getResourceOpportunityRepresentationClient(
            T resource, Class<V> returnType) {
        V representation = getResourceOpportunityRepresentation(resource, returnType);
        appendResourceSummaryRepresentation(resource, representation);
        return representation;
    }

    public <T extends Resource<?>> ResourceRepresentationExtended getResourceRepresentationClient(T resource) {
        Class<?> resourceClass = resource.getClass();

        if (Institution.class.equals(resourceClass)) {
            return institutionMapper.getInstitutionRepresentationClient((Institution) resource);
        } else if (Department.class.equals(resourceClass)) {
            return getDepartmentRepresentationClient((Department) resource, DepartmentRepresentationClient.class);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            return getResourceOpportunityRepresentationClient((ResourceOpportunity<?>) resource, ResourceOpportunityRepresentationClient.class);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            return applicationMapper.getApplicationRepresentationClient((Application) resource);
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class);
    }

    public <T extends Resource<?>> ResourceRepresentationExtended getResourceRepresentationExport(T resource) throws Exception {
        Class<?> resourceClass = resource.getClass();

        if (resourceClass.equals(Institution.class)) {
            return institutionMapper.getInstitutionRepresentation((Institution) resource);
        } else if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            return getResourceParentRepresentation((ResourceParent<?>) resource, DepartmentRepresentationClient.class);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            return getResourceOpportunityRepresentation((ResourceOpportunity<?>) resource, ResourceOpportunityRepresentationClient.class);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            return applicationMapper.getApplicationRepresentationExport((Application) resource);
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class);
    }

    public <T extends ResourceParent<?>, U extends ResourceRepresentationClient> void appendResourceSummaryRepresentation(T resource, U representation) {
        List<ResourceCountRepresentation> counts = Lists.newLinkedList();
        for (PrismScope childScope : scopeService.getChildScopesAscending(resource.getResourceScope())) {
            counts.add(new ResourceCountRepresentation().withResourceScope(childScope).withResourceCount(
                    resourceService.getActiveChildResourceCount(resource, childScope)));
            if (childScope.equals(PROJECT)) {
                break;
            }
        }
        representation.setCounts(counts);

        representation.setPlot(getResourceSummaryPlotRepresentation(resource, null));
    }

    public ResourceSummaryPlotRepresentation getResourceSummaryPlotRepresentation(ResourceParent<?> resource, ResourceReportFilterDTO filterDTO) {
        if (filterDTO == null) {
            ResourceSummaryPlotDataRepresentation plotDataRepresentation = getResourceSummaryPlotDataRepresentation(resource, null);
            return new ResourceSummaryPlotRepresentation().withConstraints(null).withData(plotDataRepresentation);
        } else {
            List<ResourceSummaryPlotConstraintRepresentation> constraint = getResourceSummaryPlotConstraintRepresentation(filterDTO);
            ResourceSummaryPlotDataRepresentation plotDataRepresentation = getResourceSummaryPlotDataRepresentation(resource, filterDTO.getProperties());
            return new ResourceSummaryPlotRepresentation().withConstraints(constraint).withData(plotDataRepresentation);
        }
    }

    public ResourceSummaryPlotDataRepresentation getResourceSummaryPlotDataRepresentation(
            ResourceParent<?> resource, List<ResourceReportFilterPropertyDTO> constraints) {
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
            ResourceParent<?> resource, List<ResourceReportFilterPropertyDTO> constraints) {
        return applicationService.getApplicationProcessingSummariesByYear(resource, constraints);
    }

    public LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(
            ResourceParent<?> resource, List<ResourceReportFilterPropertyDTO> constraints) {
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationService.getApplicationProcessingSummariesByMonth(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(processingSummary.getApplicationYear(), processingSummary);
        }
        return index;
    }

    public LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(
            ResourceParent<?> resource, List<ResourceReportFilterPropertyDTO> constraints) {
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

    public List<ResourceConditionRepresentation> getResourceConditionRepresentations(Resource<?> resource) {
        return resource.getResourceConditions().stream()
                .map(condition -> new ResourceConditionRepresentation().withActionCondition(condition.getActionCondition())
                        .withPartnerMode(condition.getPartnerMode())).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceParent<?>> ResourceRepresentationRobot getResourceRepresentationRobot(T resource) {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);

        ResourceRepresentationRobot representation = new ResourceRepresentationRobot(loader.load(SYSTEM_EXTERNAL_HOMEPAGE), applicationUrl);
        setRobotResourceRepresentation(representation, resource);

        PrismScope resourceScope = resource.getResourceScope();

        for (PrismScope parentScope : scopeService.getParentScopesDescending(resourceScope)) {
            if (!parentScope.equals(SYSTEM)) {
                Resource<?> parentResource = resource.getEnclosingResource(parentScope);
                if (parentResource != null) {
                    setRobotResourceRepresentation(representation, parentResource);
                }
            }
        }

        for (PrismScope childScope : scopeService.getChildScopesAscending(resourceScope)) {
            if (!childScope.equals(APPLICATION)) {
                String childScopeReference = "related" + childScope.getUpperCamelName() + "s";
                setProperty(representation, childScopeReference, resourceService.getResourceRobotRelatedRepresentations(resource, childScope,
                        loader.load(PrismDisplayPropertyDefinition.valueOf("SYSTEM_OPPORTUNITIES_RELATED_" + childScope.name() + "S"))));
            }
        }

        List<User> users = userService.getUsersForResourcesAndRoles((Set<Resource<?>>) (resourceScope.equals(PROJECT) ? Sets.newHashSet(resource)
                : PrismReflectionUtils.getProperty(resource, "projects")), PROJECT_SUPERVISOR_GROUP.getRoles());
        List<String> relatedUsers = users.stream().map(User::getRobotRepresentation).collect(Collectors.toList());

        if (!relatedUsers.isEmpty()) {
            representation.setRelatedUsers(new ResourceRepresentationMetadataUserRelated().withLabel(loader.load(SYSTEM_OPPORTUNITIES_RELATED_USERS))
                    .withUsers(relatedUsers));
        }

        return representation;
    }

    public String getResourceThumbnailUrlRobot(Resource<?> resource) {
        String defaultSocialThumbnail = applicationUrl + "/images/fbimg.jpg";
        if (resource.getResourceScope() == SYSTEM) {
            return defaultSocialThumbnail;
        }
        Document logoImage = resource.getInstitution().getLogoImage();
        return logoImage == null ? defaultSocialThumbnail : applicationApiUrl + "/images/" + logoImage.getId().toString();
    }

    public String getResourceUrlRobot(Resource<?> resource) {
        return applicationUrl + "/" + ANGULAR_HASH + "/?" + resource.getResourceScope().getLowerCamelName() + "=" + resource.getId();
    }

    public List<ResourceChildCreationRepresentation> getResourceChildCreationRepresentations(
            PrismScope parentScope, Integer parentId, PrismScope targetScope) {
        LinkedHashMap<PrismScope, TreeSet<ResourceChildCreationDTO>> resources = Maps.newLinkedHashMap();

        PrismScope immediateChildScope = null;
        for (PrismScope resourceScope : scopeService.getChildScopesAscending(parentScope, PROGRAM)) {
            immediateChildScope = immediateChildScope == null ? resourceScope : immediateChildScope;
            TreeSet<ResourceChildCreationDTO> sortedResources = Sets.newTreeSet(new ResourceChildCreationDTOComparator());
            sortedResources.addAll(resourceService.getResourcesWhichPermitChildResourceCreation(resourceScope, parentScope, parentId, targetScope));
            resources.put(resourceScope, sortedResources);
        }

        for (Entry<PrismScope, TreeSet<ResourceChildCreationDTO>> resourceEntries : Lists.reverse(Lists.newLinkedList(resources.entrySet()))) {
            PrismScope resourceEntryScope = resourceEntries.getKey();
            List<PrismScope> parentEntryScopes = scopeService.getParentScopesDescending(resourceEntryScope, immediateChildScope);
            for (ResourceChildCreationDTO resourceEntry : resourceEntries.getValue()) {
                for (PrismScope parentEntryScope : parentEntryScopes) {
                    ResourceParent<?> parentEntry = (ResourceParent<?>) resourceEntry.getResource().getEnclosingResource(parentEntryScope);
                    if (parentEntry != null) {
                        resources.get(parentEntryScope).add(new ResourceChildCreationDTO().withResource(parentEntry).withPartnerMode(false));
                    }
                }
            }
        }

        int counter = 0;
        Map<Integer, ResourceChildCreationRepresentation> index = Maps.newHashMap();
        Set<ResourceChildCreationRepresentation> representations = Sets.newLinkedHashSet();
        for (Entry<PrismScope, TreeSet<ResourceChildCreationDTO>> resourceEntry : resources.entrySet()) {
            if (!(counter > 0 && parentScope.equals(SYSTEM))) {
                for (ResourceChildCreationDTO resource : resourceEntry.getValue()) {
                    ResourceChildCreationRepresentation representation = getResourceChildCreationRepresentation(resource);
                    if (counter == 0) {
                        representations.add(representation);
                    } else {
                        index.get(resource.getResource().getParentResource().getId()).addChildResource(representation);
                    }
                    index.put(resource.getResource().getId(), representation);
                }
            }
            counter++;
        }

        return Lists.newLinkedList(representations);
    }

    private <T extends Resource<?>> ResourceChildCreationRepresentation getResourceChildCreationRepresentation(ResourceChildCreationDTO resource) {
        return getResourceRepresentation(resource.getResource(), ResourceChildCreationRepresentation.class).withPartnerMode(resource.getPartnerMode());
    }

    private <T extends Resource<?>, V extends ResourceRepresentationIdentity> V getResourceRepresentation(T resource, Class<V> returnType) {
        V representation = BeanUtils.instantiate(returnType);

        representation.setScope(resource.getResourceScope());
        representation.setId(resource.getId());

        if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            representation.setName(((ResourceParent<?>) resource).getName());
            representation.setLogoImage(documentMapper.getDocumentRepresentation(resource.getInstitution().getLogoImage()));
        }

        return representation;
    }

    private List<ResourceSummaryPlotConstraintRepresentation> getResourceSummaryPlotConstraintRepresentation(ResourceReportFilterDTO filterDTO) {
        return filterDTO.getProperties().stream()
                .map(propertyDTO ->
                        new ResourceSummaryPlotConstraintRepresentation().withEntityId(propertyDTO.getEntityId()).withType(propertyDTO.getEntityType()))
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

    private void setRobotResourceRepresentation(ResourceRepresentationRobot representation, Resource<?> resource) {
        PrismScope resourceScope = resource.getResourceScope();
        ResourceRepresentationRobotMetadata resourceRepresentation = resourceService.getResourceRobotMetadataRepresentation(resource,
                stateService.getActiveResourceStates(resourceScope), scopeService.getChildScopesWithActiveStates(resourceScope, APPLICATION));
        resourceRepresentation.setAuthor(resource.getUser().getRobotRepresentation());
        resourceRepresentation.setThumbnailUrl(getResourceThumbnailUrlRobot(resource));
        resourceRepresentation.setResourceUrl(getResourceUrlRobot(resource));
        setProperty(representation, resourceScope.getLowerCamelName(), resourceRepresentation);
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
