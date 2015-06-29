package com.zuehlke.pgadmissions.services.integration;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceReportFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotConstraintRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationWeek;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryRepresentation;
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
public class IntegrationResourceService {

    @Inject
    private ActionService actionService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private IntegrationActionService integrationActionService;

    @Inject
    private IntegrationAdvertService integrationAdvertService;

    @Inject
    private IntegrationApplicationService integrationApplicationService;

    @Inject
    private IntegrationCommentService integrationCommentService;

    @Inject
    private IntegrationRoleService integrationRoleService;

    @Inject
    private IntegrationStateService integrationStateService;

    @Inject
    private IntegrationUserService integrationUserService;

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

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationSimple getResourceRepresentationSimple(T resource) {
        ResourceRepresentationSimple representation = new ResourceRepresentationSimple().withId(resource.getId()).withCode(resource.getCode())
                .withTitle(resource.getTitle());

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
        User currentUser = userService.getCurrentUser();

        ResourceRepresentationExtended representation = (ResourceRepresentationExtended) getResourceRepresentationSimple(resource);
        representation.setUser(integrationUserService.getUserRepresentation(resource.getUser()));

        for (PrismScope parentScope : scopeService.getParentScopesDescending(resource.getResourceScope())) {
            Resource parentResource = resource.getEnclosingResource(parentScope);
            if (parentResource != null) {
                representation.setParentResource(getResourceRepresentationSimple(parentResource));
            }
        }

        representation.setState(integrationStateService.getStateRepresentation(resource.getState()));
        representation.setPreviousState(integrationStateService.getStateRepresentation(resource.getPreviousState()));
        representation.setSecondaryStates(integrationStateService.getSecondaryStateRepresentations(resource));

        representation.setCreatedTimestamp(resource.getCreatedTimestamp());
        representation.setUpdatedTimestamp(resource.getUpdatedTimestamp());

        representation.setTimeline(integrationCommentService.getTimelineRepresentation(resource, currentUser));
        representation.setActions(integrationActionService.getActionRepresentations(resource, currentUser));
        representation.setUserRoles(integrationRoleService.getResourceUserRoleRepresentations(resource));

        representation.setWorkflowConfigurations(resourceService.getWorkflowPropertyConfigurations(resource));
        representation.setConditions(getResourceConditionRepresentations(resource));

        Class<T> resourceClass = (Class<T>) resource.getClass();
        if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            ResourceParent resourceParent = (ResourceParent) resource;
            ResourceParentRepresentation representationParent = (ResourceParentRepresentation) representation;
            representationParent.setAdvert(integrationAdvertService.getAdvertRepresentation(resourceParent.getAdvert()));
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
    public <T extends Resource> ResourceRepresentationExtended getResourceClientRepresentation(T resource) throws Exception {
        Class<T> resourceClass = (Class<T>) resource.getClass();

        if (resourceClass.equals(Application.class)) {
            return integrationApplicationService.getApplicationClientRepresentation((Application) resource);
        } else if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            ResourceParentClientRepresentation representation = (ResourceParentClientRepresentation) getResourceRepresentationExtended(resource);
            representation.setResourceSummary(getResourceSummaryRepresentation((ResourceParent) resource));
            return representation;
        } else if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunityClientRepresentation representation = (ResourceOpportunityClientRepresentation) getResourceRepresentationExtended(resource);
            representation.setResourceSummary(getResourceSummaryRepresentation((ResourceParent) resource));
            return representation;
        }

        return getResourceRepresentationExtended(resource);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationExtended getResourceExportRepresentation(T resource) throws Exception {
        Class<T> resourceClass = (Class<T>) resource.getClass();

        if (resourceClass.equals(Application.class)) {
            return integrationApplicationService.getApplicationExportRepresentation((Application) resource);
        }

        return getResourceRepresentationExtended(resource);
    }

    public ResourceSummaryRepresentation getResourceSummaryRepresentation(ResourceParent resource) {
        PrismScope resourceScope = resource.getResourceScope();
        ResourceSummaryRepresentation representation = new ResourceSummaryRepresentation();

        if (Arrays.asList(INSTITUTION, DEPARTMENT).contains(resourceScope)) {
            representation.setProgramCount(programService.getActiveProgramCount((Institution) resource));
            representation.setProjectCount(projectService.getActiveProjectCount(resource));
        } else if (resourceScope == PROGRAM) {
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

            ApplicationProcessingSummaryRepresentationYear yearRepresentation = (ApplicationProcessingSummaryRepresentationYear) integrationApplicationService
                    .getApplicationProcessingSummaryRepresentation(yearSummary);
            yearRepresentation.setApplicationYear(applicationYear);

            List<ApplicationProcessingSummaryRepresentationMonth> monthRepresentations = Lists.newLinkedList();
            for (ApplicationProcessingSummaryDTO monthSummary : monthSummaries.get(applicationYear)) {
                ApplicationProcessingSummaryRepresentationMonth monthRepresentation = (ApplicationProcessingSummaryRepresentationMonth) integrationApplicationService
                        .getApplicationProcessingSummaryRepresentation(monthSummary);
                monthRepresentation.setApplicationMonth(monthSummary.getApplicationMonth());
                monthRepresentations.add(monthRepresentation);

                Integer applicationMonth = monthSummary.getApplicationMonth();
                List<ApplicationProcessingSummaryRepresentationWeek> weekRepresentations = Lists.newLinkedList();
                for (ApplicationProcessingSummaryDTO weekSummary : weekSummaries.get(new ApplicationProcessingMonth(applicationYear, applicationMonth))) {
                    ApplicationProcessingSummaryRepresentationWeek weekRepresentation = (ApplicationProcessingSummaryRepresentationWeek) integrationApplicationService
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

}
