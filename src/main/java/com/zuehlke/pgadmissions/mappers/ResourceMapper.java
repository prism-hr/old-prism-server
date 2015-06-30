package com.zuehlke.pgadmissions.mappers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceReportFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationListExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationListSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotConstraintRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationWeek;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ApplicationService.ApplicationProcessingMonth;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.ProjectService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class ResourceMapper {

    @Inject
    private ActionService actionService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ActionMapper actionMapper;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private ApplicationMapper applicationMapper;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private RoleMapper roleMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ProgramService programService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private UserService userService;

    public List<ResourceRepresentationListExtended> getResourceRepresentations(PrismScope resourceScope, List<ResourceListRowDTO> rows) {
        DateTime baseline = new DateTime();
        List<ResourceRepresentationListExtended> representations = Lists.newArrayListWithCapacity(rows.size());

        for (ResourceListRowDTO row : rows) {
            ResourceRepresentationListExtended representation = new ResourceRepresentationListExtended().withResourceScope(resourceScope).withId(row.getResourceId());

            Integer institutionId = row.getInstitutionId();
            Integer departmentId = row.getDepartmentId();
            Integer programId = row.getProgramId();
            Integer projectId = row.getProjectId();

            if (resourceScope.equals(INSTITUTION)) {
                representation.setTitle(row.getInstitutionTitle());
                representation.setLogoImage(row.getInstitutionLogoImageId());
            } else {
                representation.setInstitution(new ResourceRepresentationSimple().withId(institutionId).withTitle(row.getInstitutionTitle())
                        .withLogoImage(row.getInstitutionLogoImageId()));
            }

            if (resourceScope.equals(DEPARTMENT)) {
                representation.setTitle(row.getDepartmentTitle());
            } else if (departmentId != null) {
                representation.setDepartment(new ResourceRepresentationSimple().withId(departmentId).withTitle(row.getDepartmentTitle()));
            }

            if (resourceScope.equals(PROGRAM)) {
                representation.setTitle(row.getProgramTitle());
            } else if (programId != null) {
                representation.setProgram(new ResourceRepresentationSimple().withId(programId).withTitle(row.getProgramTitle()));
            }

            if (resourceScope.equals(PROJECT)) {
                representation.setTitle(row.getProjectTitle());
            } else if (projectId != null) {
                representation.setProject(new ResourceRepresentationSimple().withId(projectId).withTitle(row.getProjectTitle()));
            }

            representation.setCode(row.getCode());

            representation.setUser(new UserRepresentationSimple().withId(row.getUserId()).withFirstName(row.getUserFirstName())
                    .withFirstName2(row.getUserFirstName2()).withFirstName3(row.getUserFirstName3()).withLastName(row.getUserLastName())
                    .withEmail(row.getUserEmail()).withAccountImageUrl(row.getUserAccountImageUrl()));

            representation.setApplicationRatingAverage(row.getApplicationRatingAverage());

            representation.setState(stateMapper.getStateRepresentationSimple(row.getStateId()));
            representation.setSecondaryStates(stateMapper.getStateRepresentations(row.getSecondaryStateIds()));

            List<ActionRepresentationSimple> actions = actionMapper.getActionRepresentations(row.getActions());
            DateTime updatedTimestamp = row.getUpdatedTimestamp();

            representation.setCreatedTimestamp(row.getCreatedTimestamp());
            representation.setUpdatedTimestamp(updatedTimestamp);

            setRaisesUrgentFlag(representation, actions);
            setRaisesUpdateFlag(representation, baseline, updatedTimestamp);

            representation.setActions(actions);
            representation.setSequenceIdentifier(row.getSequenceIdentifier());
            representations.add(representation);
        }

        return representations;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationSimple getResourceRepresentationSimple(T resource) {
        ResourceRepresentationSimple representation = new ResourceRepresentationSimple().withResourceScope(resource.getResourceScope())
                .withId(resource.getId()).withCode(resource.getCode()).withTitle(resource.getTitle());

        Class<T> resourceClass = (Class<T>) resource.getClass();
        if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            representation.setLogoImage(resource.getInstitution().getLogoImage().getId());

            if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
                ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resource;
                representation.setImportedCode(resourceOpportunity.getImportedCode());
            }
        }

        return representation;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationExtended(T resource) throws Exception {
        DateTime baseline = new DateTime();
        User currentUser = userService.getCurrentUser();

        ResourceRepresentationExtended representation = (ResourceRepresentationExtended) getResourceRepresentationSimple(resource);
        representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser()));

        for (PrismScope parentScope : scopeService.getParentScopesDescending(resource.getResourceScope())) {
            Resource parentResource = resource.getEnclosingResource(parentScope);
            if (parentResource != null) {
                representation.setParentResource(getResourceRepresentationSimple(parentResource));
            }
        }

        representation.setState(stateMapper.getStateRepresentationSimple(resource.getState()));
        representation.setPreviousState(stateMapper.getStateRepresentationSimple(resource.getPreviousState()));
        representation.setSecondaryStates(stateMapper.getSecondaryStateRepresentations(resource));

        List<ActionRepresentationExtended> actions = actionMapper.getActionRepresentations(resource, currentUser);
        DateTime updatedTimestamp = resource.getUpdatedTimestamp();

        representation.setCreatedTimestamp(resource.getCreatedTimestamp());
        representation.setUpdatedTimestamp(updatedTimestamp);

        setRaisesUrgentFlag((ResourceRepresentationListSimple) representation, (List<ActionRepresentationSimple>) (List<?>) actions);
        setRaisesUpdateFlag((ResourceRepresentationListSimple) representation, baseline, updatedTimestamp);

        representation.setActions(actions);
        representation.setTimeline(commentMapper.getTimelineRepresentation(resource, currentUser));
        representation.setUserRoles(roleMapper.getResourceUserRoleRepresentations(resource));

        representation.setWorkflowConfigurations(resourceService.getWorkflowPropertyConfigurations(resource));
        representation.setConditions(getResourceConditionRepresentations(resource));

        Class<T> resourceClass = (Class<T>) resource.getClass();
        if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            ResourceParent resourceParent = (ResourceParent) resource;
            ResourceParentRepresentation representationParent = (ResourceParentRepresentation) representation;
            representationParent.setAdvert(advertMapper.getAdvertRepresentation(resourceParent.getAdvert()));
            representationParent.setBackgroundImage(resourceService.getBackgroundImage(resourceParent));
            representationParent.setPartnerActions(actionService.getPartnerActions(resourceParent));

            if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
                ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resourceParent;
                ResourceOpportunityRepresentation representationOpportunity = (ResourceOpportunityRepresentation) representationParent;
                representationOpportunity.setStudyOptions(resourceService.getStudyOptions(resourceOpportunity));
                representationOpportunity.setStudyLocations(resourceService.getStudyLocations(resourceOpportunity));

                return representationOpportunity;
            }

            return representationParent;
        }

        return representation;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationClient(T resource) throws Exception {
        Class<T> resourceClass = (Class<T>) resource.getClass();

        if (resourceClass.equals(Application.class)) {
            return applicationMapper.getApplicationClientRepresentation((Application) resource);
        } else if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            ResourceParentClientRepresentation representation = (ResourceParentClientRepresentation) getResourceRepresentationExtended(resource);
            representation.setResourceSummary(getResourceSummaryRepresentation((ResourceParent) resource));
            return representation;
        } else if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunityClientRepresentation representation = (ResourceOpportunityClientRepresentation) getResourceRepresentationExtended(resource);
            representation.setResourceSummary(getResourceSummaryRepresentation((ResourceOpportunity) resource));
            return representation;
        }

        return getResourceRepresentationExtended(resource);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationExport(T resource) throws Exception {
        Class<T> resourceClass = (Class<T>) resource.getClass();

        if (resourceClass.equals(Application.class)) {
            return applicationMapper.getApplicationExportRepresentation((Application) resource);
        }

        return getResourceRepresentationExtended(resource);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceParent> ResourceSummaryRepresentation getResourceSummaryRepresentation(T resource) {
        ResourceSummaryRepresentation representation = new ResourceSummaryRepresentation();

        Class<T> resourceClass = (Class<T>) resource.getClass();
        if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            representation.setProgramCount(programService.getActiveProgramCount((Institution) resource));
            representation.setProjectCount(projectService.getActiveProjectCount(resource));
        } else if (Program.class.equals(resourceClass)) {
            representation.setProjectCount(projectService.getActiveProjectCount(resource));
        }

        representation.setPlot(getResourceSummaryPlotRepresentation(resource, null));
        return representation;
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

        List<ApplicationProcessingSummaryRepresentationYear> yearRepresentations = Lists.newLinkedList();
        List<ApplicationProcessingSummaryDTO> yearSummaries = applicationService.getApplicationProcessingSummariesByYear(resource, constraints);
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> monthSummaries = applicationService.getApplicationProcessingSummariesByMonth(resource,
                constraints);
        LinkedHashMultimap<ApplicationProcessingMonth, ApplicationProcessingSummaryDTO> weekSummaries = applicationService
                .getApplicationProcessingSummariesByWeek(resource, constraints);

        for (ApplicationProcessingSummaryDTO yearSummary : yearSummaries) {
            String applicationYear = yearSummary.getApplicationYear();

            ApplicationProcessingSummaryRepresentationYear yearRepresentation = (ApplicationProcessingSummaryRepresentationYear) applicationMapper
                    .getApplicationProcessingSummaryRepresentation(yearSummary);
            yearRepresentation.setApplicationYear(applicationYear);

            List<ApplicationProcessingSummaryRepresentationMonth> monthRepresentations = Lists.newLinkedList();
            for (ApplicationProcessingSummaryDTO monthSummary : monthSummaries.get(applicationYear)) {
                ApplicationProcessingSummaryRepresentationMonth monthRepresentation = (ApplicationProcessingSummaryRepresentationMonth) applicationMapper
                        .getApplicationProcessingSummaryRepresentation(monthSummary);
                monthRepresentation.setApplicationMonth(monthSummary.getApplicationMonth());
                monthRepresentations.add(monthRepresentation);

                Integer applicationMonth = monthSummary.getApplicationMonth();
                List<ApplicationProcessingSummaryRepresentationWeek> weekRepresentations = Lists.newLinkedList();
                for (ApplicationProcessingSummaryDTO weekSummary : weekSummaries.get(new ApplicationProcessingMonth(applicationYear, applicationMonth))) {
                    ApplicationProcessingSummaryRepresentationWeek weekRepresentation = (ApplicationProcessingSummaryRepresentationWeek) applicationMapper
                            .getApplicationProcessingSummaryRepresentation(weekSummary);
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

    private List<ResourceConditionRepresentation> getResourceConditionRepresentations(Resource resource) {
        List<ResourceConditionRepresentation> representations = Lists.newLinkedList();
        for (ResourceCondition condition : resource.getResourceConditions()) {
            representations.add(new ResourceConditionRepresentation().withActionCondition(condition.getActionCondition()).withPartnerMode(
                    condition.getPartnerMode()));
        }
        return representations;
    }

    private List<ResourceSummaryPlotConstraintRepresentation> getResourceSummaryPlotConstraintRepresentation(ResourceReportFilterDTO filterDTO) {
        List<ResourceSummaryPlotConstraintRepresentation> constraint = Lists.newLinkedList();
        for (ResourceReportFilterPropertyDTO propertyDTO : filterDTO.getProperties()) {
            constraint.add(new ResourceSummaryPlotConstraintRepresentation().withEntityId(propertyDTO.getEntityId()).withType(propertyDTO.getEntityType()));
        }
        return constraint;
    }

    private void setRaisesUrgentFlag(ResourceRepresentationListSimple representation, List<ActionRepresentationSimple> actions) {
        for (ActionRepresentationSimple action : actions) {
            if (BooleanUtils.isTrue(action.getRaisesUrgentFlag())) {
                representation.setRaisesUrgentFlag(true);
                break;
            }
        }
    }

    private void setRaisesUpdateFlag(ResourceRepresentationListSimple representation, DateTime baseline, DateTime updatedTimestamp) {
        representation.setRaisesUpdateFlag(updatedTimestamp.isAfter(baseline.minusDays(1)));
    }

}
