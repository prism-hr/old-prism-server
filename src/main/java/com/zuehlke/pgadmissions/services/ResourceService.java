package com.zuehlke.pgadmissions.services;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.*;
import com.zuehlke.pgadmissions.domain.definitions.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.dto.*;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.*;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDTO.ResourceConditionDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.services.builders.PrismResourceListConstraintBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.concurrency.ActionServiceHelperConcurrency;
import com.zuehlke.pgadmissions.services.helpers.concurrency.ResourceServiceHelperConcurrency;
import com.zuehlke.pgadmissions.services.helpers.concurrency.StateServiceHelperConcurrency;
import com.zuehlke.pgadmissions.utils.PrismConstants;
import com.zuehlke.pgadmissions.workflow.executors.action.ActionExecutor;
import com.zuehlke.pgadmissions.workflow.resource.seo.search.SearchRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.resource.seo.social.SocialRepresentationBuilder;
import com.zuehlke.pgadmissions.workflow.transition.creators.ResourceCreator;
import com.zuehlke.pgadmissions.workflow.transition.persisters.ResourcePersister;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode.ANY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.IMPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static com.zuehlke.pgadmissions.utils.PrismConstants.LIST_PAGE_ROW_COUNT;

@Service
@Transactional
public class ResourceService {

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @Inject
    private ResourceDAO resourceDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ResourceListFilterService resourceListFilterService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private PrismResourceListConstraintBuilder resourceListConstraintBuilder;

    @Inject
    private ApplicationContext applicationContext;

    public Resource getById(PrismScope resourceScope, Integer id) {
        return entityService.getById(resourceScope.getResourceClass(), id);
    }

    public <T extends Resource> T getById(Class<T> resourceClass, Integer id) {
        return entityService.getById(resourceClass, id);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceCreationDTO> ActionOutcomeDTO createResource(User user, Action action, T dto) throws Exception {
        PrismScope creationScope = action.getCreationScope().getId();

        ResourceCreator<T> resourceCreator = (ResourceCreator<T>) applicationContext.getBean(creationScope.getResourceCreator());
        Resource resource = resourceCreator.create(user, dto);

        Integer workflowPropertyConfigurationVersion = dto.getWorkflowPropertyConfigurationVersion();
        if (workflowPropertyConfigurationVersion == null) {
            customizationService.getActiveConfigurationVersion(WORKFLOW_PROPERTY, resource, creationScope);
        }
        resource.setWorkflowPropertyConfigurationVersion(dto.getWorkflowPropertyConfigurationVersion());

        Role role = roleService.getById(PrismRole.valueOf(creationScope.name() + "_ADMINISTRATOR"));
        Comment comment = new Comment().withResource(resource).withUser(user).withAction(action).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime()).addAssignedUser(user, role, CREATE);

        return actionService.executeUserAction(resource, action, comment);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceParentDTO, U extends ResourceParent> U createParentResource(User user, T parentDTO) throws Exception {
        Class<?> parentDTOClass = parentDTO.getClass();
        PrismScope creationScope = PrismScope.getByResourceDTOClass(parentDTOClass);

        if (creationScope != null) {
            boolean isInstitution = creationScope.equals(INSTITUTION);

            if (isInstitution || ((ResourceParentDivisionDTO) parentDTO).getParentResource() != null) {
                PrismScope scope = isInstitution ? SYSTEM : ((ResourceParentDivisionDTO) parentDTO).getParentResource().getScope();

                Action action = actionService.getById(PrismAction.valueOf(scope.name() + "_CREATE_" + creationScope.name()));
                return (U) createResource(user, action, parentDTO).getResource();
            }
        }

        throw new UnsupportedOperationException();
    }

    public void persistResource(Resource resource, Comment comment) {
        if (comment.isCreateComment()) {
            DateTime baseline = new DateTime();
            resource.setCreatedTimestamp(baseline);
            resource.setUpdatedTimestamp(baseline);

            if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
                ResourceParent parent = (ResourceParent) resource;
                parent.setUpdatedTimestampSitemap(baseline);
            }

            Class<? extends ResourcePersister> resourcePersister = resource.getResourceScope().getResourcePersister();
            if (resourcePersister == null) {
                throw new UnsupportedOperationException();
            }

            applicationContext.getBean(resourcePersister).persist(resource);

            resource.setCode(generateResourceCode(resource));
            entityService.save(resource);
            entityService.flush();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceCreationDTO> ActionOutcomeDTO executeAction(User user, Integer resourceId, CommentDTO commentDTO) throws Exception {
        if (commentDTO.getAction().getActionCategory().equals(CREATE_RESOURCE)) {
            T resourceDTO = (T) commentDTO.getNewResource().getResource();
            Action action = actionService.getById(commentDTO.getAction());
            resourceDTO.setParentResource(new ResourceDTO().withScope(action.getScope().getId()).withId(resourceId));
            return createResource(user, action, resourceDTO);
        }

        Class<? extends ActionExecutor> actionExecutor = commentDTO.getAction().getScope().getActionExecutor();
        if (actionExecutor != null) {
            return applicationContext.getBean(actionExecutor).execute(resourceId, commentDTO);
        }

        throw new UnsupportedOperationException();
    }

    public void preProcessResource(Resource resource, Comment comment) throws Exception {
        Class<? extends ResourceProcessor> processor = resource.getResourceScope().getResourcePreprocessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }
    }

    public void recordStateTransition(Resource resource, Comment comment, State state, State transitionState) throws Exception {
        resource.setPreviousState(state);
        resource.setState(transitionState);

        Set<ResourcePreviousState> resourcePreviousStates = resource.getResourcePreviousStates();
        Set<CommentState> commentStates = comment.getCommentStates();
        deleteResourceStates(resourcePreviousStates, commentStates);

        Set<ResourceState> resourceStates = resource.getResourceStates();
        Set<CommentTransitionState> commentTransitionStates = comment.getCommentTransitionStates();
        deleteResourceStates(resourceStates, commentTransitionStates);
        entityService.flush();

        LocalDate baseline = comment.getCreatedTimestamp().toLocalDate();
        insertResourceStates(resource, resourcePreviousStates, commentStates, ResourcePreviousState.class, baseline);
        insertResourceStates(resource, resourceStates, commentTransitionStates, ResourceState.class, baseline);
        entityService.flush();
    }

    public void processResource(Resource resource, Comment comment) throws Exception {
        Class<? extends ResourceProcessor> processor = resource.getResourceScope().getResourceProcessor();
        if (processor != null) {
            applicationContext.getBean(processor).process(resource, comment);
        }

        StateDurationDefinition stateDurationDefinition = resource.getState().getStateDurationDefinition();
        if (comment.isStateTransitionComment() || (stateDurationDefinition != null && BooleanUtils.isTrue(stateDurationDefinition.getEscalation()))) {
            LocalDate baselineCustom = null;
            LocalDate baseline = new LocalDate();

            PrismStateDurationEvaluation stateDurationEvaluation = resource.getState().getStateDurationEvaluation();
            if (stateDurationEvaluation != null) {
                baselineCustom = applicationContext.getBean(stateDurationEvaluation.getResolver()).resolve(resource, comment);
            }

            baseline = baselineCustom == null || baselineCustom.isBefore(baseline) ? baseline : baselineCustom;

            StateDurationConfiguration stateDurationConfiguration = stateDurationDefinition == null ? null //
                    : stateService.getStateDurationConfiguration(resource, comment.getUser(), stateDurationDefinition);
            resource.setDueDate(baseline.plusDays(stateDurationConfiguration == null ? 0 : stateDurationConfiguration.getDuration()));

            entityService.flush();
        }
    }

    public void postProcessResource(Resource resource, Comment comment) throws Exception {
        DateTime baselineTime = new DateTime();

        if (comment.isUserComment() || resource.getSequenceIdentifier() == null) {
            resource.setUpdatedTimestamp(baselineTime);
            resource.setSequenceIdentifier(Long.toString(baselineTime.getMillis()) + String.format("%010d", resource.getId()));
        }

        Class<? extends ResourceProcessor> processor = resource.getResourceScope().getResourcePostprocessor();
        if (processor != null) {
            entityService.flush();
            applicationContext.getBean(processor).process(resource, comment);
        }

        if (comment.isUserCreationComment()) {
            notificationService.resetNotifications(resource);
        }

        if (comment.isStateGroupTransitionComment() && comment.getAction().getCreationScope() == null) {
            createOrUpdateStateTransitionSummary(resource, baselineTime);
        }
    }

    public String generateResourceCode(Resource resource) {
        return "PRiSM-" + PrismScope.getByResourceClass(resource.getClass()).getShortCode() + "-" + String.format("%010d", resource.getId());
    }

    public Comment executeUpdate(Resource resource, PrismDisplayPropertyDefinition messageIndex, CommentAssignedUser... assignees) throws Exception {
        User user = userService.getCurrentUser();
        Action action = actionService.getViewEditAction(resource);

        Comment comment = new Comment().withUser(user).withAction(action)
                .withContent(applicationContext.getBean(PropertyLoader.class).localize(resource).load(messageIndex)).withDeclinedResponse(false)
                .withCreatedTimestamp(new DateTime());

        for (CommentAssignedUser assignee : assignees) {
            comment.addAssignedUser(assignee.getUser(), assignee.getRole(), assignee.getRoleTransitionType());
            entityService.evict(assignee);
        }

        actionService.executeUserAction(resource, action, comment);
        return comment;
    }

    public Resource getOperativeResource(Resource resource, Action action) {
        return Arrays.asList(CREATE_RESOURCE, IMPORT_RESOURCE).contains(action.getActionCategory()) ? resource.getParentResource() : resource;
    }

    public List<Integer> getResourcesToEscalate(PrismScope resourceScope, PrismAction actionId, LocalDate baseline) {
        return resourceDAO.getResourcesToEscalate(resourceScope, actionId, baseline);
    }

    public List<Integer> getResourcesToPropagate(PrismScope propagatingScope, Integer propagatingId, PrismScope propagatedScope, PrismAction actionId) {
        return resourceDAO.getResourcesToPropagate(propagatingScope, propagatingId, propagatedScope, actionId);
    }

    public List<Integer> getResourcesRequiringIndividualReminders(PrismScope resourceScope, LocalDate baseline) {
        return resourceDAO.getResourcesRequiringIndividualReminders(resourceScope, baseline);
    }

    public List<Integer> getResourcesRequiringSyndicatedReminders(PrismScope resourceScope, LocalDate baseline) {
        return resourceDAO.getResourcesRequiringSyndicatedReminders(resourceScope, baseline);
    }

    public List<Integer> getResourcesRequiringSyndicatedUpdates(PrismScope resourceScope, LocalDate baseline, DateTime rangeStart, DateTime rangeClose) {
        return resourceDAO.getResourceRequiringSyndicatedUpdates(resourceScope, baseline, rangeStart, rangeClose);
    }

    public List<ResourceListRowDTO> getResourceList(final PrismScope resourceScope, ResourceListFilterDTO filter, String lastSequenceIdentifier)
            throws Exception {
        final User user = userService.getCurrentUser();
        List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(resourceScope);
        filter = resourceListFilterService.saveOrGetByUserAndScope(user, resourceScope, filter);

        int maxRecords = LIST_PAGE_ROW_COUNT;
        Set<Integer> assignedResources = applicationContext.getBean(ResourceServiceHelperConcurrency.class).getAssignedResources(user, resourceScope,
                parentScopeIds, filter, lastSequenceIdentifier, maxRecords);
        boolean hasRedactions = actionService.hasRedactions(resourceScope, assignedResources, user);

        if (!assignedResources.isEmpty()) {
            final HashMultimap<Integer, ActionDTO> creations = actionService.getCreateResourceActions(resourceScope, assignedResources);
            List<ResourceListRowDTO> rows = resourceDAO.getResourceList(user, resourceScope, parentScopeIds, assignedResources, filter,
                    lastSequenceIdentifier, maxRecords, hasRedactions);

            applicationContext.getBean(StateServiceHelperConcurrency.class).appendSecondaryStates(resourceScope, rows, maxRecords);
            applicationContext.getBean(ActionServiceHelperConcurrency.class).appendActions(resourceScope, user, rows, creations, maxRecords);
            return rows;
        }

        return Lists.newArrayList();
    }

    public Set<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds) throws Exception {
        return applicationContext.getBean(ResourceServiceHelperConcurrency.class).getAssignedResources(user, scopeId, parentScopeIds, null, null, null);
    }

    public Set<Integer> getAssignedResources(User user, PrismScope scopeId, List<PrismScope> parentScopeIds, ResourceListFilterDTO filter) throws Exception {
        return applicationContext.getBean(ResourceServiceHelperConcurrency.class).getAssignedResources(user, scopeId, parentScopeIds, filter, null, null);
    }

    public List<Integer> getAssignedResources(final User user, final PrismScope scopeId, final ResourceListFilterDTO filter,
            final String lastSequenceIdentifier, final Integer recordsToRetrieve, final Junction condition) {
        return resourceDAO.getAssignedResources(user, scopeId, filter, condition, lastSequenceIdentifier, recordsToRetrieve);
    }

    public List<Integer> getAssignedResources(final User user, final PrismScope scopeId, final ResourceListFilterDTO filter,
            final String lastSequenceIdentifier, final Integer recordsToRetrieve, final Junction condition, final PrismScope parentScopeId) {
        return resourceDAO.getAssignedResources(user, scopeId, parentScopeId, filter, condition, lastSequenceIdentifier, recordsToRetrieve);
    }

    @SuppressWarnings("unchecked")
    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations(Resource resource) throws Exception {
        if (resource.getResourceScope().equals(PrismScope.APPLICATION)) {
            return applicationService.getWorkflowPropertyConfigurations((Application) resource);
        }
        return (List<WorkflowPropertyConfigurationRepresentation>) (List<?>) customizationService.getConfigurationRepresentationsWithOrWithoutVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, resource, resource.getWorkflowPropertyConfigurationVersion());
    }

    public SocialMetadataDTO getSocialMetadata(PrismScope resourceScope, Integer resourceId) throws Exception {
        Class<? extends SocialRepresentationBuilder> socialRepresentationBuilder = resourceScope.getSocialRepresentationBuilder();
        if (socialRepresentationBuilder == null) {
            throw new UnsupportedOperationException();
        }
        Resource resource = getById(resourceScope, resourceId);
        return applicationContext.getBean(socialRepresentationBuilder).build(resource.getAdvert());
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(PrismScope resourceScope, Integer resourceId) throws Exception {
        Class<? extends SearchRepresentationBuilder> searchRepresentationBuilder = resourceScope.getSearchRepresentationBuilder();
        if (searchRepresentationBuilder == null) {
            throw new UnsupportedOperationException();
        }
        return applicationContext.getBean(searchRepresentationBuilder).build(resourceId);
    }

    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope propertiesScope) throws Exception {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(resource);
        Map<PrismDisplayPropertyDefinition, String> properties = Maps.newLinkedHashMap();
        for (PrismDisplayPropertyDefinition prismDisplayPropertyDefinition : PrismDisplayPropertyDefinition.getProperties(propertiesScope)) {
            properties.put(prismDisplayPropertyDefinition, loader.load(prismDisplayPropertyDefinition));
        }
        return properties;
    }

    public String getSocialThumbnailUrl(Resource resource) {
        String defaultSocialThumbnail = applicationUrl + "/images/fbimg.jpg";
        if (resource.getResourceScope() == PrismScope.SYSTEM) {
            return defaultSocialThumbnail;
        } else {
            Document logoImage = resource.getInstitution().getLogoImage();
            if (logoImage == null) {
                return defaultSocialThumbnail;
            }
            return applicationApiUrl + "/images/" + logoImage.getId().toString();
        }
    }

    public String getSocialResourceUrl(Resource resource) {
        return applicationUrl + "/" + PrismConstants.ANGULAR_HASH + "/?" + resource.getResourceScope().getLowerCamelName() + "=" + resource.getId();
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> HashMultimap<PrismScope, T> getUserAdministratorResources(User user) {
        HashMultimap<PrismScope, T> userAdministratorResources = HashMultimap.create();
        for (UserAdministratorResourceDTO userAdministratorResource : resourceDAO.getUserAdministratorResources(user)) {
            T resource = (T) userAdministratorResource.getResource();
            userAdministratorResources.put(resource.getResourceScope(), resource);
        }
        userAdministratorResources.putAll(APPLICATION, (List<T>) applicationService.getUserAdministratorApplications(userAdministratorResources));
        return userAdministratorResources;
    }

    public void reassignResources(User oldUser, User newUser) {
        for (PrismScope prismScope : PrismScope.values()) {
            resourceDAO.reassignResources(prismScope, oldUser, newUser);
        }
    }

    public List<Integer> getResourcesByUserMatchingUserAndRole(PrismScope prismScope, String searchTerm, List<PrismRole> prismRoles) {
        return resourceDAO.getResourcesByMatchingUsersAndRole(prismScope, searchTerm, prismRoles);
    }

    public List<Integer> getResourcesByMatchingEnclosingResource(PrismScope enclosingResourceScope, String searchTerm) {
        return resourceDAO.getResourcesByMatchingEnclosingResources(enclosingResourceScope, searchTerm);
    }

    public ResourceStudyOption getStudyOption(ResourceOpportunity resource, ImportedEntitySimple studyOption) {
        if (BooleanUtils.isTrue(resource.getAdvert().isImported())) {
            return resourceDAO.getResourceAttributeStrict(resource, ResourceStudyOption.class, "studyOption", studyOption);
        }
        return resourceDAO.getResourceAttribute(resource, ResourceStudyOption.class, "studyOption", studyOption);
    }

    public List<ImportedEntitySimple> getStudyOptions(ResourceOpportunity resource) {
        if (BooleanUtils.isTrue(resource.getAdvert().isImported())) {
            List<ResourceStudyOption> studyOptions = resourceDAO.getResourceAttributesStrict(resource, ResourceStudyOption.class, "studyOption", "id");
            return studyOptions.stream().map(studyOption -> studyOption.getStudyOption()).collect(Collectors.toList());
        }

        List<ImportedEntitySimple> filteredStudyOptions = Lists.newLinkedList();
        List<ResourceStudyOption> studyOptions = resourceDAO.getResourceAttributes(resource, ResourceStudyOption.class, "studyOption", "id");

        PrismScope lastResourceScope = null;
        for (ResourceStudyOption studyOption : studyOptions) {
            PrismScope thisResourceScope = studyOption.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredStudyOptions.add(studyOption.getStudyOption());
            lastResourceScope = thisResourceScope;
        }

        return filteredStudyOptions;
    }

    public ResourceStudyOptionInstance getFirstStudyOptionInstance(ResourceOpportunity resource, ImportedEntitySimple studyOption) {
        return resourceDAO.getFirstStudyOptionInstance(resource, studyOption);
    }

    public List<String> getStudyLocations(ResourceOpportunity resource) {
        List<String> filteredStudylocations = Lists.newLinkedList();
        List<ResourceStudyLocation> studyLocations = resourceDAO.getResourceAttributes(resource, ResourceStudyLocation.class, "studyLocation", null);

        PrismScope lastResourceScope = null;
        for (ResourceStudyLocation studyLocation : studyLocations) {
            PrismScope thisResourceScope = studyLocation.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredStudylocations.add(studyLocation.getStudyLocation());
            lastResourceScope = thisResourceScope;
        }

        return filteredStudylocations;
    }

    public List<PrismActionCondition> getActionConditions(ResourceParent resource) {
        List<PrismActionCondition> filteredActionConditions = Lists.newLinkedList();
        List<ResourceCondition> actionConditions = resourceDAO.getResourceConditions(resource);

        PrismScope lastResourceScope = null;
        for (ResourceCondition resourceCondition : actionConditions) {
            PrismScope thisResourceScope = resourceCondition.getResource().getResourceScope();
            if (lastResourceScope != null && !thisResourceScope.equals(lastResourceScope)) {
                break;
            }
            filteredActionConditions.add(resourceCondition.getActionCondition());
            lastResourceScope = thisResourceScope;
        }

        return filteredActionConditions;
    }

    public <T extends ResourceParent, U extends ResourceParentDTO> void setResourceAttributes(T resource, U resourceDTO) {
        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resource;
            ResourceOpportunityDTO resourceOpportunityDTO = (ResourceOpportunityDTO) resourceDTO;

            Program program = resource.getProgram();
            if (!program.sameAs(resource) && BooleanUtils.isTrue(program.getAdvert().isImported())) {
                resourceOpportunity.setOpportunityType(program.getOpportunityType());
            } else {
                resourceOpportunity.setOpportunityType(importedEntityService.getByName(ImportedEntitySimple.class, resourceOpportunityDTO
                        .getOpportunityType()
                        .name()));
                setStudyOptions(resourceOpportunity, resourceOpportunityDTO.getStudyOptions(), new LocalDate());
            }

            setStudyLocations(resourceOpportunity, resourceOpportunityDTO.getStudyLocations());
        }

        setResourceConditions(resource, resourceDTO.getResourceConditions());
    }

    public void setResourceConditions(ResourceParent resource, List<ResourceConditionDTO> resourceConditions) {
        resource.getResourceConditions().clear();
        entityService.flush();

        if (resourceConditions == null) {
            List<PrismResourceCondition> defaultResourceConditions;
            if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
                PrismOpportunityType opportunityType = PrismOpportunityType.valueOf(((ResourceOpportunity) resource).getOpportunityType().getName());
                defaultResourceConditions = PrismOpportunityType.getResourceConditions(resource.getResourceScope(), opportunityType);
            } else {
                defaultResourceConditions = PrismOpportunityType.getResourceConditions(resource.getResourceScope());
            }

            for (PrismResourceCondition defaultResourceCondition : defaultResourceConditions) {
                resource.addResourceCondition(new ResourceCondition().withResource(resource)
                        .withActionCondition(defaultResourceCondition.getActionCondition())
                        .withPartnerNode(defaultResourceCondition.isPartnerMode()));
            }
        } else {
            for (ResourceParentDTO.ResourceConditionDTO resourceCondition : resourceConditions) {
                resource.addResourceCondition(new ResourceCondition().withResource(resource)
                        .withActionCondition(resourceCondition.getActionCondition())
                        .withPartnerNode(resourceCondition.getPartnerMode()));
            }
        }
    }

    public void setStudyOptions(ResourceOpportunity resource, List<PrismStudyOption> prismStudyOptions, LocalDate baseline) {
        resource.getInstanceGroups().clear();
        entityService.flush();

        LocalDate close = getResourceEndDate(resource);
        if (prismStudyOptions == null) {
            PrismScope resourceScope = resource.getResourceScope();
            if (!resourceScope.equals(INSTITUTION)) {
                prismStudyOptions = PrismOpportunityType.valueOf(resource.getOpportunityType().getName()).getDefaultStudyOptions();
            }
        }

        for (PrismStudyOption prismStudyOption : prismStudyOptions) {
            if (close == null || close.isAfter(baseline)) {
                ImportedEntitySimple studyOption = importedEntityService.getByName(ImportedEntitySimple.class, prismStudyOption.name());
                resource.addStudyOption(new ResourceStudyOption().withResource(resource).withStudyOption(studyOption).withApplicationStartDate(baseline)
                        .withApplicationCloseDate(close));
            }
        }
    }

    public void setStudyLocations(ResourceOpportunity resource, List<String> studyLocations) {
        resource.getStudyLocations().clear();
        entityService.flush();

        if (studyLocations != null) {
            for (String studyLocation : studyLocations) {
                resource.addStudyLocation(new ResourceStudyLocation().withResource(resource).withStudyLocation(studyLocation));
            }
        }
    }

    public void updateResource(PrismScope resourceScope, Integer resourceId, ResourceOpportunityDTO resourceDTO, Comment comment) throws Exception {
        ResourceOpportunity resource = (ResourceOpportunity) getById(resourceScope, resourceId);

        AdvertDTO advertDTO = resourceDTO.getAdvert();
        Advert advert = resource.getAdvert();
        advertService.updateAdvert(resource.getParentResource(), advert, advertDTO);
        resource.setTitle(advert.getTitle());

        resource.setDurationMinimum(resourceDTO.getDurationMinimum());
        resource.setDurationMaximum(resourceDTO.getDurationMaximum());

        List<ResourceConditionDTO> resourceConditions = resourceDTO.getResourceConditions();
        setResourceConditions(resource, resourceConditions == null ? Lists.<ResourceConditionDTO> newArrayList() : resourceConditions);
        setStudyLocations(resource, resourceDTO.getStudyLocations());

        if (!resource.getAdvert().isImported()) {
            ImportedEntitySimple opportunityType = importedEntityService.getByName(ImportedEntitySimple.class, resourceDTO.getOpportunityType().name());
            resource.setOpportunityType(opportunityType);

            List<PrismStudyOption> studyOptions = resourceDTO.getStudyOptions();
            setStudyOptions(resource, studyOptions == null ? Lists.<PrismStudyOption> newArrayList() : studyOptions, new LocalDate());
        }
    }

    public void deleteElapsedStudyOptions() {
        LocalDate baseline = new LocalDate();
        resourceDAO.deleteElapsedStudyOptionInstances(baseline);
        resourceDAO.deleteElapsedStudyOptions(baseline);
    }

    public Junction getFilterConditions(PrismScope resourceScope, ResourceListFilterDTO filter) {
        Junction conditions = null;
        if (filter.hasConstraints()) {
            conditions = filter.getMatchMode() == ANY ? Restrictions.disjunction() : Restrictions.conjunction();
            for (ResourceListFilterConstraintDTO constraint : filter.getConstraints()) {
                resourceListConstraintBuilder.appendFilter(conditions, resourceScope, constraint);
            }
        }
        return conditions;
    }

    public LocalDate getResourceEndDate(ResourceOpportunity resource) {
        return resourceDAO.getResourceEndDate(resource);
    }

    public <T extends ResourceParent> Integer getActiveChildResourceCount(T resource, PrismScope childResourceScope) {
        Long count = resourceDAO.getActiveChildResourceCount(resource, childResourceScope);
        return count == null ? 0 : count.intValue();
    }

    private void createOrUpdateStateTransitionSummary(Resource resource, DateTime baselineTime) {
        String transitionStateSelection = Joiner.on("|").join(stateService.getCurrentStates(resource));

        ResourceStateTransitionSummary transientTransitionSummary = new ResourceStateTransitionSummary().withResource(resource.getParentResource())
                .withStateGroup(resource.getPreviousState().getStateGroup()).withTransitionStateSelection(transitionStateSelection).withFrequency(1)
                .withUpdatedTimestamp(baselineTime);
        ResourceStateTransitionSummary persistentTransitionSummary = entityService.getDuplicateEntity(transientTransitionSummary);

        if (persistentTransitionSummary == null) {
            entityService.save(transientTransitionSummary);
        } else {
            persistentTransitionSummary.setFrequency(persistentTransitionSummary.getFrequency() + 1);
            persistentTransitionSummary.setUpdatedTimestamp(baselineTime);
        }
    }

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void deleteResourceStates(
            Set<T> resourceStateDefinitions, Set<U> commentStateDefinitions) {
        List<State> preservedStates = Lists.newArrayListWithCapacity(commentStateDefinitions.size());
        for (CommentStateDefinition commentStateDefinition : commentStateDefinitions) {
            preservedStates.add(commentStateDefinition.getState());
        }

        for (T resourceState : resourceStateDefinitions) {
            if (!preservedStates.contains(resourceState.getState())) {
                entityService.delete(resourceState);
            }
        }
        resourceStateDefinitions.clear();
    }

    private <T extends ResourceStateDefinition, U extends CommentStateDefinition> void insertResourceStates(
            Resource resource, Set<T> resourceStateDefinitions, Set<U> commentStateDefinitions, Class<T> resourceStateClass, LocalDate baseline)
            throws Exception {
        for (U commentState : commentStateDefinitions) {
            T transientResourceStateDefinition = resourceStateClass.newInstance();
            transientResourceStateDefinition.setResource(resource);
            transientResourceStateDefinition.setState(commentState.getState());

            T persistentResourceStateDefinition = entityService.getDuplicateEntity(transientResourceStateDefinition);
            if (persistentResourceStateDefinition == null) {
                transientResourceStateDefinition.setPrimaryState(commentState.getPrimaryState());
                transientResourceStateDefinition.setCreatedDate(baseline);
                entityService.save(transientResourceStateDefinition);

                if (transientResourceStateDefinition.getClass().equals(ResourceState.class)) {
                    resource.addResourceState((ResourceState) transientResourceStateDefinition);
                } else {
                    resource.addResourcePreviousState((ResourcePreviousState) transientResourceStateDefinition);
                }
            } else {
                persistentResourceStateDefinition.setPrimaryState(commentState.getPrimaryState());
            }
        }
    }

}
