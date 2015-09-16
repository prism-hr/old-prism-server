package com.zuehlke.pgadmissions.services;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.visualization.datasource.datatable.value.ValueType.TEXT;
import static com.zuehlke.pgadmissions.PrismConstants.ANGULAR_HASH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_LINK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PrismActionEnhancementGroup.APPLICATION_EQUAL_OPPORTUNITIES_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismReportColumn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationAppointmentDTO;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReportListRowDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceRatingSummaryDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.validation.ProfileValidator;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

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
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

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
    private ProfileValidator applicationValidator;

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

        PrismScope scopeId = APPLICATION;
        List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(APPLICATION, SYSTEM);

        User user = userService.getCurrentUser();
        boolean hasRedactions = actionService.hasRedactions(user, scopeId);

        resourceListFilterService.saveOrGetByUserAndScope(user, scopeId, filter);
        List<Integer> assignedApplications = resourceService.getResources(user, scopeId, parentScopeIds, filter).stream().map(a -> a.getId()).collect(Collectors.toList());

        DataTable dataTable = new DataTable();

        List<ColumnDescription> headers = Lists.newLinkedList();
        List<PrismReportColumn> columns = Lists.newLinkedList();
        List<String> columnAccessors = Lists.newLinkedList();
        for (PrismReportColumn column : PrismReportColumn.values()) {
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
            for (PrismReportColumn column : columns) {
                String value = null;
                String getMethod = "get" + WordUtils.capitalize(column.getAccessor()) + "Display";
                switch (column.getAccessorType()) {
                case DATE:
                    value = (String) PrismReflectionUtils.invokeMethod(reportRow, getMethod, dateFormat);
                    break;
                case DISPLAY_PROPERTY:
                    Enum<?> index = (Enum<?>) PrismReflectionUtils.invokeMethod(reportRow, getMethod);
                    value = index == null ? "" : loader.loadLazy((PrismDisplayPropertyDefinition) PrismReflectionUtils.getProperty(index, "displayProperty"));
                    break;
                case STRING:
                    value = (String) PrismReflectionUtils.invokeMethod(reportRow, getMethod);
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
        ValidationUtils.invokeValidator(applicationValidator, application, errors);
        return errors;
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints, HashMultimap<PrismImportedEntity, Integer> transformedConstraints) {
        return applicationDAO.getApplicationProcessingSummariesByYear(resource, constraints, transformedConstraints);
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints, HashMultimap<PrismImportedEntity, Integer> transformedConstraints) {
        return applicationDAO.getApplicationProcessingSummariesByMonth(resource, constraints, transformedConstraints);
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints, HashMultimap<PrismImportedEntity, Integer> transformedConstraints) {
        return applicationDAO.getApplicationProcessingSummariesByWeek(resource, constraints, transformedConstraints);
    }

    public boolean isCanViewEqualOpportunitiesData(Application application, User user) {
        List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(application, user);
        if (actionEnhancements.size() > 0) {
            actionEnhancements.retainAll(Arrays.asList(APPLICATION_EQUAL_OPPORTUNITIES_VIEWER.getActionEnhancements()));
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

    public List<Integer> getApplicationsByImportedInstitution(ResourceParent parent, Collection<Integer> importedInstitutions) {
        return applicationDAO.getApplicationsByImportedInstitution(parent, importedInstitutions);
    }

    public List<Integer> getApplicationsByImportedQualificationType(ResourceParent parent, Collection<Integer> importedQualificationTypes) {
        return applicationDAO.getApplicationsByImportedQualificationType(parent, importedQualificationTypes);
    }

    public List<Integer> getApplicationsByImportedRejectionReason(ResourceParent parent, Collection<Integer> importedRejectionReasons) {
        return applicationDAO.getApplicationsByImportedRejectionReason(parent, importedRejectionReasons);
    }

    public List<ApplicationAppointmentDTO> getApplicationAppointments(User user) {
        return newLinkedList(newLinkedHashSet(applicationDAO.getApplicationAppointments(user)));
    }

    public void updateProgramDetail(Integer applicationId, ApplicationProgramDetailDTO programDetailDTO) throws Exception {
        Application application = getById(applicationId);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail == null) {
            programDetail = new ApplicationProgramDetail();
        }

        PrismOpportunityType prismOpportunityType = programDetailDTO.getOpportunityType();
        if (prismOpportunityType == null) {
            ResourceParent parent = application.getParentResource();
            if (ResourceOpportunity.class.isAssignableFrom(parent.getClass())) {
                ImportedEntitySimple opportunityType = ((ResourceOpportunity) parent).getOpportunityType();
                setApplicationOpportunityType(application, programDetail, opportunityType);
            }
        } else {
            ImportedEntitySimple opportunityType = importedEntityService.getByName(ImportedEntitySimple.class, prismOpportunityType.name());
            setApplicationOpportunityType(application, programDetail, opportunityType);
        }

        ImportedEntitySimple studyOption = importedEntityService.getById(ImportedEntitySimple.class, programDetailDTO.getStudyOption().getId());
        programDetail.setStudyOption(studyOption);
        programDetail.setStartDate(programDetailDTO.getStartDate());
        programDetail.setLastUpdatedTimestamp(DateTime.now());

        application.setProgramDetail(programDetail);
        resourceService.executeUpdate(application, APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL);
    }

    private void setApplicationOpportunityType(Application application, ApplicationProgramDetail programDetail, ImportedEntitySimple opportunityType) {
        programDetail.setOpportunityType(opportunityType);
        application.setOpportunityCategories(PrismOpportunityType.valueOf(opportunityType.getName()).getCategory().name());
    }

}
