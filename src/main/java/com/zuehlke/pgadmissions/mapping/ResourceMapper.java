package com.zuehlke.pgadmissions.mapping;

import com.google.common.base.Objects;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.*;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationMonth;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationWeek;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentationYear;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryRepresentation.ResourceCountRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.*;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

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
    private DocumentMapper documentMapper;

    @Inject
    private InstitutionMapper institutionMapper;

    @Inject
    private RoleMapper roleMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private UserService userService;

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
                representation.setTitle(row.getInstitutionTitle());
                representation.setLogoImage(documentMapper.getDocumentRepresentation(row.getInstitutionLogoImageId()));
            } else {
                representation.setInstitution(new ResourceRepresentationSimple().withId(institutionId).withTitle(row.getInstitutionTitle())
                        .withLogoImage(documentMapper.getDocumentRepresentation(row.getInstitutionLogoImageId())));
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

            representation.setActions(actions);
            representation.setSequenceIdentifier(row.getSequenceIdentifier());
            representations.add(representation);
        }

        return representations;
    }

    public <T extends Resource> ResourceRepresentationSimple getResourceRepresentationSimple(T resource) {
        return getResourceRepresentation(resource, ResourceRepresentationSimple.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource, V extends ResourceRepresentationSimple> V getResourceRepresentation(T resource, Class<V> returnType) {
        V representation = BeanUtils.instantiate(returnType);

        representation.setScope(resource.getResourceScope());
        representation.setId(resource.getId());
        representation.setCode(resource.getCode());
        representation.setTitle(resource.getTitle());

        Class<T> resourceClass = (Class<T>) resource.getClass();
        if (Institution.class.equals(resourceClass)) {
            representation.setLogoImage(documentMapper.getDocumentRepresentation(((Institution) resource).getLogoImage()));

            if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
                ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resource;
                representation.setImportedCode(resourceOpportunity.getImportedCode());
            }
        }

        return representation;
    }

    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationExtended(T resource) throws Exception {
        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource, V extends ResourceRepresentationExtended> V getResourceRepresentationExtended(T resource, Class<V> returnType) throws Exception {
        DateTime baseline = new DateTime();
        User currentUser = userService.getCurrentUser();

        V representation = getResourceRepresentation(resource, returnType);

        representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser()));

        for (PrismScope parentScope : scopeService.getParentScopesDescending(resource.getResourceScope())) {
            if (!parentScope.equals(SYSTEM)) {
                Resource parentResource = resource.getEnclosingResource(parentScope);
                if (parentResource != null) {
                    representation.setParentResource(getResourceRepresentationSimple(parentResource));
                }
            }
        }

        representation.setState(stateMapper.getStateRepresentationSimple(resource.getState()));
        representation.setPreviousState(stateMapper.getStateRepresentationSimple(resource.getPreviousState()));
        representation.setSecondaryStates(stateMapper.getSecondaryStateRepresentations(resource));

        List<ActionRepresentationExtended> actions = actionMapper.getActionRepresentations(resource, currentUser);
        DateTime updatedTimestamp = resource.getUpdatedTimestamp();

        representation.setCreatedTimestamp(resource.getCreatedTimestamp());
        representation.setUpdatedTimestamp(updatedTimestamp);

        setRaisesUrgentFlag((ResourceRepresentationStandard) representation, (List<ActionRepresentationSimple>) (List<?>) actions);
        setRaisesUpdateFlag((ResourceRepresentationStandard) representation, baseline, updatedTimestamp);

        representation.setActions(actions);
        representation.setTimeline(commentMapper.getTimelineRepresentation(resource, currentUser));
        representation.setUserRoles(roleMapper.getResourceUserRoleRepresentations(resource));

        representation.setWorkflowConfigurations(resourceService.getWorkflowPropertyConfigurations(resource));
        representation.setConditions(getResourceConditionRepresentations(resource));

        return representation;
    }

    public <T extends ResourceParent, V extends ResourceParentRepresentation> V getResourceParentRepresentation(T resource,
                                                                                                                Class<V> returnType) throws Exception {
        V representation = getResourceRepresentationExtended(resource, returnType);

        representation.setAdvert(advertMapper.getAdvertRepresentationSimple(resource.getAdvert()));
        representation.setPartnerActions(actionService.getPartnerActions(resource));

        return representation;
    }

    public <T extends ResourceOpportunity, V extends ResourceOpportunityRepresentation> V getResourceOpportunityRepresentation(T resource,
                                                                                                                               Class<V> returnType) throws Exception {
        V representation = getResourceParentRepresentation(resource, returnType);

        List<ImportedEntityResponse> studyOptions = resourceService.getStudyOptions(resource).stream()
                .map(studyOption -> (ImportedEntityResponse) importedEntityMapper.getImportedEntityRepresentation(studyOption))
                .collect(Collectors.toList());
        representation.setStudyOptions(studyOptions);
        representation.setStudyLocations(resourceService.getStudyLocations(resource));

        return representation;

    }

    public <T extends ResourceParent, V extends ResourceParentRepresentationClient> V getResourceParentRepresentationClient(T resource, Class<V> returnType)
            throws Exception {
        V representation = getResourceParentRepresentation(resource, returnType);
        representation.setResourceSummary(getResourceSummaryRepresentation(resource));
        return representation;
    }

    public <T extends ResourceOpportunity, V extends ResourceOpportunityRepresentationClient> V getResourceOpportunityRepresentationClient(T resource,
                                                                                                                                           Class<V> returnType) throws Exception {
        V representation = getResourceOpportunityRepresentation(resource, returnType);
        representation.setResourceSummary(getResourceSummaryRepresentation(resource));
        return representation;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationClient(T resource) throws Exception {
        Class<T> resourceClass = (Class<T>) resource.getClass();

        if (resourceClass.equals(Institution.class)) {
            return institutionMapper.getInstitutionRepresentationClient((Institution) resource);
        } else if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            return getResourceParentRepresentationClient((ResourceParent) resource, ResourceParentRepresentationClient.class);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            return getResourceOpportunityRepresentationClient((ResourceOpportunity) resource, ResourceOpportunityRepresentationClient.class);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            return applicationMapper.getApplicationRepresentationClient((Application) resource);
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationExport(T resource) throws Exception {
        Class<T> resourceClass = (Class<T>) resource.getClass();

        if (resourceClass.equals(Institution.class)) {
            return institutionMapper.getInstitutionRepresentation((Institution) resource);
        } else if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            return getResourceParentRepresentation((ResourceParent) resource, ResourceParentRepresentationClient.class);
        } else if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
            return getResourceOpportunityRepresentation((ResourceOpportunity) resource, ResourceOpportunityRepresentationClient.class);
        } else if (Application.class.isAssignableFrom(resourceClass)) {
            return applicationMapper.getApplicationRepresentationExport((Application) resource);
        }

        return getResourceRepresentationExtended(resource, ResourceRepresentationExtended.class);
    }

    public <T extends ResourceParent> ResourceSummaryRepresentation getResourceSummaryRepresentation(T resource) {
        ResourceSummaryRepresentation representation = new ResourceSummaryRepresentation();

        List<ResourceCountRepresentation> counts = Lists.newLinkedList();
        for (PrismScope childScope : scopeService.getChildScopesAscending(resource.getResourceScope())) {
            counts.add(new ResourceCountRepresentation().withResourceScope(childScope).withResourceCount(
                    resourceService.getActiveChildResourceCount(resource, childScope)));
            if (childScope.equals(PROJECT)) {
                break;
            }
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

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource,
                                                                                         List<ResourceReportFilterPropertyDTO> constraints) {
        return applicationService.getApplicationProcessingSummariesByYear(resource, constraints);
    }

    public LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource,
                                                                                                                List<ResourceReportFilterPropertyDTO> constraints) {
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationService.getApplicationProcessingSummariesByMonth(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(processingSummary.getApplicationYear(), processingSummary);
        }
        return index;
    }

    public LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource,
                                                                                                                                List<ResourceReportFilterPropertyDTO> constraints) {
        LinkedHashMultimap<ResourceProcessingMonth, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationService.getApplicationProcessingSummariesByWeek(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(new ResourceProcessingMonth(processingSummary.getApplicationYear(), processingSummary.getApplicationMonth()), processingSummary);
        }
        return index;
    }

    public static class ResourceProcessingMonth {

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
