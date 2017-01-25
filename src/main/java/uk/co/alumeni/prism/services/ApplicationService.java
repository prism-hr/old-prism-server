package uk.co.alumeni.prism.services;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import uk.co.alumeni.prism.dao.ApplicationDAO;
import uk.co.alumeni.prism.domain.Theme;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.UniqueEntity.EntitySignature;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.*;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.definitions.*;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;
import uk.co.alumeni.prism.dto.ApplicationAppointmentDTO;
import uk.co.alumeni.prism.dto.ApplicationProcessingSummaryDTO;
import uk.co.alumeni.prism.dto.ApplicationReportListRowDTO;
import uk.co.alumeni.prism.dto.ResourceRatingSummaryDTO;
import uk.co.alumeni.prism.exceptions.WorkflowPermissionException;
import uk.co.alumeni.prism.rest.dto.application.ApplicationLocationDTO;
import uk.co.alumeni.prism.rest.dto.application.ApplicationProgramDetailDTO;
import uk.co.alumeni.prism.rest.dto.application.ApplicationThemeDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationDTO;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationYearRepresentation;
import uk.co.alumeni.prism.rest.validation.ProfileValidator;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.visualization.datasource.datatable.value.ValueType.TEXT;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.text.WordUtils.capitalize;
import static uk.co.alumeni.prism.PrismConstants.*;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PrismActionEnhancementGroup.APPLICATION_EQUAL_OPPORTUNITIES_VIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup.APPLICATION_UNSUBMITTED;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.invokeMethod;

@Service
@Transactional
public class ApplicationService {
    
    @Value("${application.url}")
    private String applicationUrl;
    
    @Inject
    private ApplicationDAO applicationDAO;
    
    @Inject
    private ActionService actionService;
    
    @Inject
    private AdvertService advertService;
    
    @Inject
    private EntityService entityService;
    
    @Inject
    private PrismService prismService;
    
    @Inject
    private ProfileService profileService;
    
    @Inject
    private ResourceService resourceService;
    
    @Inject
    private ResourceListFilterService resourceListFilterService;
    
    @Inject
    private ScopeService scopeService;
    
    @Inject
    private SystemService systemService;
    
    @Inject
    private TagService tagService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private ProfileValidator profileValidator;
    
    @Inject
    private ApplicationContext applicationContext;
    
    public Application getById(Integer id) {
        return entityService.getById(Application.class, id);
    }
    
    public ApplicationTheme getApplicationThemeById(Integer id) {
        return entityService.getById(ApplicationTheme.class, id);
    }
    
    public ApplicationLocation getApplicationLocationById(Integer id) {
        return entityService.getById(ApplicationLocation.class, id);
    }
    
    public Application getByCode(String code) {
        return entityService.getByProperty(Application.class, "code", code);
    }
    
    public List<User> getApplicationRefereesNotResponded(Application application) {
        return applicationDAO.getApplicationRefereesNotResponded(application);
    }
    
    public DataTable getApplicationReport(ResourceListFilterDTO filter) throws Exception {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
        
        PrismScope scope = APPLICATION;
        List<PrismScope> parentScopes = scopeService.getParentScopesDescending(APPLICATION, SYSTEM);
        
        User user = userService.getCurrentUser();
        
        resourceListFilterService.saveOrGetByUserAndScope(user, scope, filter);
        List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);
        List<Integer> applications = resourceService.getResources(user, scope, parentScopes, targeterEntities, filter).stream().map(a -> a.getId()).collect(toList());
        
        boolean hasRedactions = actionService.hasRedactions(user, scope, applications);
        DataTable table = new DataTable();
        
        List<ColumnDescription> headers = Lists.newLinkedList();
        List<PrismApplicationReportColumn> columns = Lists.newLinkedList();
        List<String> columnAccessors = Lists.newLinkedList();
        for (PrismApplicationReportColumn column : PrismApplicationReportColumn.values()) {
            if (column.getDefinitions().isEmpty() && !(hasRedactions && column.isHasRedactions())) {
                headers.add(new ColumnDescription(column.getAccessor(), TEXT, loader.loadLazy(column.getTitle())));
                columns.add(column);
                columnAccessors.add(column.getColumnAccessor());
            }
        }
        
        headers.add(new ColumnDescription("link", TEXT, loader.loadLazy(SYSTEM_LINK)));
        table.addColumns(headers);
        
        String dateFormat = loader.loadLazy(SYSTEM_DATE_FORMAT);
        List<ApplicationReportListRowDTO> reportRows = applicationDAO.getApplicationReport(applications, Joiner.on(", ").join(columnAccessors));
        
        String groupConcatSeparator = SEMI_COLON + SPACE;
        Map<Integer, List<String>> reportIndex = Maps.newLinkedHashMap();
        HashMultimap<String, String> reportValueIndex = HashMultimap.create();
        for (ApplicationReportListRowDTO reportRow : reportRows) {
            boolean grouping = false;
            Integer id = reportRow.getId();
            
            List<String> values = reportIndex.get(id);
            if (values == null) {
                values = newLinkedList();
            } else {
                grouping = true;
            }
            
            int columnIndex = 0;
            for (PrismApplicationReportColumn column : columns) {
                String value = null;
                String getMethod = "get" + capitalize(column.getAccessor()) + "Display";
                switch (column.getAccessorType()) {
                    case DATE:
                        value = (String) invokeMethod(reportRow, getMethod, dateFormat);
                        break;
                    case DISPLAY_PROPERTY:
                        PrismLocalizableDefinition index = (PrismLocalizableDefinition) invokeMethod(reportRow, getMethod);
                        value = index == null ? "" : loader.loadLazy(index.getDisplayProperty());
                        break;
                    case STRING:
                        value = (String) invokeMethod(reportRow, getMethod);
                        break;
                }
                
                value = trim(value);
                String valueKey = id + "|" + columnIndex;
                if (grouping) {
                    if (!reportValueIndex.containsEntry(valueKey, value)) {
                        values.set(columnIndex, values.get(columnIndex) + groupConcatSeparator + value);
                    }
                } else {
                    values.add(value);
                    reportValueIndex.put(valueKey, value);
                }
                
                columnIndex++;
            }
            
            if (!grouping) {
                values.add(applicationUrl + "/" + ANGULAR_HASH + "/application/" + reportRow.getIdDisplay() + "/view");
                reportIndex.put(id, values);
            }
        }
        
        for (List<String> values : reportIndex.values()) {
            TableRow row = new TableRow();
            values.stream().forEach(value -> row.addCell(value));
            table.addRow(row);
        }
        
        return table;
    }
    
    public void prepopulateApplication(Application application) {
        profileService.fillApplication(application);
        BeanPropertyBindingResult errors = validateApplication(application);
        for (ObjectError error : errors.getAllErrors()) {
            Object property = getProperty(application, error.getObjectName());
            if (ApplicationSection.class.isAssignableFrom(property.getClass())) {
                ApplicationSection section = (ApplicationSection) property;
                section.setLastUpdatedTimestamp(null);
            }
        }
    }
    
    public ResourceRatingSummaryDTO getApplicationRatingSummary(Application application) {
        return applicationDAO.getApplicationRatingSummary(application);
    }
    
    public ResourceRatingSummaryDTO getApplicationRatingSummary(ResourceParent resource) {
        return applicationDAO.getApplicationRatingSummary(resource);
    }
    
    public ApplicationReferee getApplicationReferee(Application application, User user) {
        return applicationDAO.getApplicationReferee(application, user);
    }
    
    public <T extends Application> BeanPropertyBindingResult validateApplication(T application) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(application, "application");
        ValidationUtils.invokeValidator(profileValidator, application, errors);
        return errors;
    }
    
    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource,
            HashMultimap<PrismFilterEntity, String> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByYear(resource, constraints);
    }
    
    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource,
            HashMultimap<PrismFilterEntity, String> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByMonth(resource, constraints);
    }
    
    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource,
            HashMultimap<PrismFilterEntity, String> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByWeek(resource, constraints);
    }
    
    public boolean isCanViewEqualOpportunities(Application application, User user) {
        List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(user, application);
        if (actionEnhancements.size() > 0) {
            actionEnhancements.retainAll(asList(APPLICATION_EQUAL_OPPORTUNITIES_VIEWER.getActionEnhancements()));
            return actionEnhancements.size() > 0;
        }
        return false;
    }
    
    public boolean isApproved(Integer applicationId) {
        Application application = getById(applicationId);
        return resourceService.getResourceStateGroups(application).contains(PrismStateGroup.APPLICATION_APPROVED)
                && !application.getState().equals(APPLICATION_APPROVED);
    }
    
    public void synchronizeApplicationRating(Application application) {
        ResourceRatingSummaryDTO ratingSummary = getApplicationRatingSummary(application);
        application.setApplicationRatingCount(ratingSummary.getRatingCount().intValue());
        application.setApplicationRatingAverage(BigDecimal.valueOf(ratingSummary.getRatingAverage()));
    }
    
    public List<Integer> getApplicationsByRejectionReason(ResourceParent parent, Collection<String> rejectionReasons) {
        return applicationDAO.getApplicationsByRejectionReason(parent, rejectionReasons);
    }
    
    public List<ApplicationAppointmentDTO> getApplicationAppointments(User user) {
        return applicationDAO.getApplicationAppointments(user);
    }
    
    public void updateProgramDetail(Integer applicationId, ApplicationProgramDetailDTO programDetailDTO) {
        Application application = getById(applicationId);
        
        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail == null) {
            programDetail = new ApplicationProgramDetail();
        }
        
        PrismOpportunityType prismOpportunityType = programDetailDTO.getOpportunityType();
        if (prismOpportunityType == null) {
            ResourceParent parent = application.getParentResource();
            if (ResourceOpportunity.class.isAssignableFrom(parent.getClass())) {
                OpportunityType opportunityType = ((ResourceOpportunity) parent).getOpportunityType();
                setApplicationOpportunityType(application, programDetail, opportunityType);
            }
        } else {
            setApplicationOpportunityType(application, programDetail, prismService.getOpportunityTypeById(prismOpportunityType));
        }
        
        programDetail.setStudyOption(programDetailDTO.getStudyOption());
        programDetail.setStartDate(programDetailDTO.getStartDate());
        
        application.getThemes().clear();
        application.getLocations().clear();
        entityService.flush();
        
        if (programDetailDTO.getThemes() != null) {
            for (ApplicationThemeDTO themeDTO : programDetailDTO.getThemes()) {
                application.getThemes().add(getOrCreateTheme(application, themeDTO));
            }
        }
        
        if (programDetailDTO.getLocations() != null) {
            for (ApplicationLocationDTO locationDTO : programDetailDTO.getLocations()) {
                application.getLocations().add(getOrCreateLocation(application, locationDTO));
            }
        }
        
        programDetail.setLastUpdatedTimestamp(DateTime.now());
        application.setProgramDetail(programDetail);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL);
    }
    
    public void executeUpdate(Application application, PrismDisplayPropertyDefinition message, CommentAssignedUser... assignees) {
        User currentUser = userService.getCurrentUser();
        if (application.getState().getStateGroup().getId().equals(APPLICATION_UNSUBMITTED)) {
            Action action = actionService.getById(APPLICATION_COMPLETE);
            if (!actionService.checkActionExecutable(application, action, currentUser)) {
                throw new WorkflowPermissionException(application, action);
            }
        } else {
            resourceService.executeUpdate(application, currentUser, message, assignees);
        }
    }
    
    public List<Integer> getApplicationsByQualifyingResourceScope(ResourceParent parent, PrismScope resourceScope, Collection<String> resources) {
        return applicationDAO.getApplicationsByQualifyingResourceScope(parent, resourceScope, resources);
    }
    
    public List<Integer> getApplicationsByEmployingResourceScope(ResourceParent parent, PrismScope resourceScope, Collection<String> resources) {
        return applicationDAO.getApplicationsByEmployingResourceScope(parent, resourceScope, resources);
    }
    
    public Boolean getApplicationOnCourse(Application application) {
        return applicationDAO.getApplicationOnCourse(application);
    }
    
    public List<Integer> getApplications(Collection<Integer> adverts, Collection<Integer> students) {
        return isEmpty(adverts) || isEmpty(students) ? emptyList() : applicationDAO.getApplications(adverts, students);
    }
    
    public List<Integer> getApplicationsByTheme(String theme, PrismResourceListFilterExpression expression, Boolean preference) {
        return applicationDAO.getApplicationsByTheme(theme, expression, preference);
    }
    
    public List<Integer> getApplicationsByLocation(String location, PrismResourceListFilterExpression expression, Boolean preference) {
        return applicationDAO.getApplicationsByLocation(location, expression, preference);
    }
    
    public void updateApplicationOpportunityCategories(Advert advert) {
        applicationDAO.updateApplicationOpportunityCategories(advert);
    }
    
    public List<Integer> getApplicationsByApplicationTheme(List<Integer> themes, List<Integer> secondaryThemes) {
        return applicationDAO.getApplicationsByApplicationTheme(themes, secondaryThemes);
    }
    
    public List<Integer> getApplicationsByApplicationLocation(List<Integer> locations, List<Integer> secondaryLocations) {
        return applicationDAO.getApplicationsByApplicationLocation(locations, secondaryLocations);
    }
    
    public List<Integer> getApplicationsWithReferencesPending(Resource parentResource) {
        return applicationDAO.getApplicationsWithReferencesPending(parentResource);
    }
    
    public List<Integer> getApplicationsWithReferencesProvided(Resource parentResource) {
        return applicationDAO.getApplicationsWithReferencesProvided(parentResource);
    }
    
    public ResourceRatingSummaryDTO getApplicationRatingSummary(User user) {
        return applicationDAO.getApplicationRatingSummary(user);
    }
    
    public void deleteApplicationHiringManagers(Application application) {
        applicationDAO.deleteApplicationHiringManagers(application);
    }
    
    public List<ApplicationYearRepresentation> getApplicationYears() {
        return applicationDAO.getApplicationYears();
    }
    
    private void setApplicationOpportunityType(Application application, ApplicationProgramDetail programDetail, OpportunityType opportunityType) {
        programDetail.setOpportunityType(opportunityType);
        application.setOpportunityCategories(opportunityType.getOpportunityCategory().name());
    }
    
    private ApplicationTheme getOrCreateTheme(Application application, ApplicationThemeDTO themeDTO) {
        Theme theme = tagService.getById(Theme.class, themeDTO.getId());
        boolean preference = BooleanUtils.isTrue(themeDTO.getPreference());
        
        ApplicationTheme duplicateApplicationTheme = getDuplicateApplicationTag(ApplicationTheme.class, application, theme);
        if (duplicateApplicationTheme == null) {
            ApplicationTheme applicationTheme = new ApplicationTheme();
            applicationTheme.setTag(theme);
            applicationTheme.setPreference(preference);
            applicationTheme.setLastUpdatedTimestamp(DateTime.now());
            
            if (preference) {
                applicationDAO.togglePrimaryApplicationTag(ApplicationTheme.class, application, theme);
            }
            
            return applicationTheme;
        }
        
        return duplicateApplicationTheme;
    }
    
    private ApplicationLocation getOrCreateLocation(Application application, ApplicationLocationDTO locationDTO) {
        Integer organizationId = null;
        PrismScope organizationScope = null;
        ResourceRelationDTO resourceRelation = locationDTO.getResource();
        for (ResourceCreationDTO organization : resourceRelation.getOrganizations()) {
            Integer thisOrganizationId = organization.getId();
            if (thisOrganizationId == null) {
                break;
            } else {
                organizationId = thisOrganizationId;
            }
            organizationScope = organization.getScope();
        }
        
        ResourceParent organization = (ResourceParent) resourceService.getById(organizationScope, organizationId);
        Advert locationAdvert = resourceService.createResourceRelation(resourceRelation,
                PrismScope.getResourceContexts(organization.getOpportunityCategories()).iterator().next(),
                resourceRelation.getResource().getScope().getScopeCategory().equals(OPPORTUNITY) ? userService.getCurrentUser() : organization.getUser())
                .getAdvert();
        boolean preference = BooleanUtils.isTrue(locationDTO.getPreference());
        
        ApplicationLocation duplicateApplicationLocation = getDuplicateApplicationTag(ApplicationLocation.class, application, locationAdvert);
        if (duplicateApplicationLocation == null) {
            ApplicationLocation applicationLocation = new ApplicationLocation();
            applicationLocation.setTag(locationAdvert);
            applicationLocation.setDescription(locationDTO.getDescription());
            
            LocalDate descriptionDate = locationDTO.getDescriptionDate();
            if (descriptionDate != null) {
                applicationLocation.setDescriptionYear(descriptionDate.getYear());
                applicationLocation.setDescriptionMonth(descriptionDate.getMonthOfYear());
            }
            
            applicationLocation.setPreference(preference);
            applicationLocation.setLastUpdatedTimestamp(DateTime.now());
            
            if (preference) {
                applicationDAO.togglePrimaryApplicationTag(ApplicationLocation.class, application, locationAdvert);
            }
            
            return applicationLocation;
        }
        
        return duplicateApplicationLocation;
    }
    
    private <T extends ApplicationTagSection<U>, U extends UniqueEntity> T getDuplicateApplicationTag(Class<T> applicationTagClass, Application application,
            U tag) {
        return entityService.getDuplicateEntity(applicationTagClass, new EntitySignature().addProperty("association", application).addProperty("tag", tag));
    }
    
}
