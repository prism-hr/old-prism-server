package com.zuehlke.pgadmissions.services;

import static com.google.visualization.datasource.datatable.value.ValueType.TEXT;
import static com.zuehlke.pgadmissions.PrismConstants.ANGULAR_HASH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_LINK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PrismActionEnhancementGroup.APPLICATION_EQUAL_OPPORTUNITIES_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_UNSUBMITTED;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.invokeMethod;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSection;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReportColumn;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.OpportunityType;
import com.zuehlke.pgadmissions.dto.ApplicationAppointmentDTO;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReportListRowDTO;
import com.zuehlke.pgadmissions.dto.ResourceRatingSummaryDTO;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.validation.ProfileValidator;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

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
    private UserService userService;

    @Inject
    private ProfileValidator profileValidator;

    @Inject
    private ApplicationContext applicationContext;

    public Application getById(Integer id) {
        return entityService.getById(Application.class, id);
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
        boolean hasRedactions = actionService.hasRedactions(user, scope);

        resourceListFilterService.saveOrGetByUserAndScope(user, scope, filter);
        List<Integer> targeterEntities = advertService.getAdvertTargeterEntities(user, scope);
        List<Integer> assignedApplications = resourceService.getResources(user, scope, parentScopes, targeterEntities, filter).stream().map(a -> a.getId()).collect(toList());

        DataTable dataTable = new DataTable();

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
        dataTable.addColumns(headers);

        String dateFormat = loader.loadLazy(SYSTEM_DATE_FORMAT);
        List<ApplicationReportListRowDTO> reportRows = applicationDAO.getApplicationReport(assignedApplications, Joiner.on(", ").join(columnAccessors));

        for (ApplicationReportListRowDTO reportRow : reportRows) {
            TableRow row = new TableRow();
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
                row.addCell(value);
            }
            row.addCell(applicationUrl + "/" + ANGULAR_HASH + "/application/" + reportRow.getIdDisplay() + "/view");
            dataTable.addRow(row);
        }

        return dataTable;
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

    public <T extends Application> ResourceRatingSummaryDTO getApplicationRatingSummary(T application) {
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

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByYear(resource, constraints);
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByMonth(resource, constraints);
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource, HashMultimap<PrismFilterEntity, String> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByWeek(resource, constraints);
    }

    public boolean isCanViewEqualOpportunitiesData(Application application, User user) {
        List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(application, user);
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

    public <T extends Application> void syncronizeApplicationRating(T application) {
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
        programDetail.setLastUpdatedTimestamp(DateTime.now());

        application.setProgramDetail(programDetail);
        executeUpdate(application, APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL);
    }

    public void executeUpdate(Application application, PrismDisplayPropertyDefinition message, CommentAssignedUser... assignees) {
        User currentUser = userService.getCurrentUser();
        if (application.getState().getStateGroup().getId().equals(APPLICATION_UNSUBMITTED)) {
            Action action = actionService.getById(APPLICATION_COMPLETE);
            if (!actionService.checkActionExecutable(application, action, currentUser, false)) {
                throw new WorkflowPermissionException(application, action);
            }
        } else {
            resourceService.executeUpdate(application, currentUser, APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL, assignees);
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

    public List<Integer> getSharedApplicationsForAdverts(List<Integer> adverts) {
        return applicationDAO.getSharedApplicationsForAdverts(adverts);
    }

    private void setApplicationOpportunityType(Application application, ApplicationProgramDetail programDetail, OpportunityType opportunityType) {
        programDetail.setOpportunityType(opportunityType);
        application.setOpportunityCategories(opportunityType.getOpportunityCategory().name());
    }

}
