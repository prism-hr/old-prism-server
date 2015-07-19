package com.zuehlke.pgadmissions.services;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.application.*;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismReportColumn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.dto.*;
import com.zuehlke.pgadmissions.mapping.ApplicationMapper;
import com.zuehlke.pgadmissions.mapping.ImportedEntityMapper;
import com.zuehlke.pgadmissions.mapping.UserMapper;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.address.AddressApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRefereeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation.OtherApplicationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.validation.ApplicationValidator;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.visualization.datasource.datatable.value.ValueType.TEXT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PrismActionEnhancementGroup.APPLICATION_EQUAL_OPPORTUNITIES_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_POSITION_DETAIL;
import static com.zuehlke.pgadmissions.utils.PrismConstants.ANGULAR_HASH;
import static org.joda.time.DateTimeConstants.MONDAY;

@Service
@Transactional
@SuppressWarnings("unchecked")
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
    private UserService userService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResourceListFilterService resourceListFilterService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationMapper applicationMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private ApplicationValidator applicationValidator;

    @Inject
    private ApplicationContext applicationContext;

    public Application getById(Integer id) {
        return entityService.getById(Application.class, id);
    }

    public Application getByCode(String code) {
        return entityService.getByProperty(Application.class, "code", code);
    }

    public Application getByCodeLegacy(String codeLegacy) {
        return entityService.getByProperty(Application.class, "codeLegacy", codeLegacy);
    }

    public ApplicationStartDateRepresentation getStartDateRepresentation(Integer applicationId, Integer studyOptionId) {
        LocalDate baseline = new LocalDate();
        Application application = getById(applicationId);

        ImportedEntitySimple studyOption = importedEntityService.getById(ImportedEntitySimple.class, studyOptionId);

        ResourceStudyOption resourceStudyOption = null;
        Resource parentResource = application.getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(parentResource.getClass())) {
            resourceStudyOption = resourceService.getStudyOption((ResourceOpportunity) application.getParentResource(), studyOption);
        }

        LocalDate earliestStartDate = getEarliestStartDate(resourceStudyOption, baseline);
        LocalDate latestStartDate = getLatestStartDate(application, resourceStudyOption);

        return new ApplicationStartDateRepresentation().withEarliestDate(earliestStartDate)
                .withRecommendedDate(getRecommendedStartDate(application, earliestStartDate, latestStartDate, baseline)).withLatestDate(latestStartDate);

    }

    public LocalDate getEarliestStartDate(ResourceStudyOption resourceStudyOption, LocalDate baseline) {
        if (resourceStudyOption != null) {
            LocalDate applicationStartDate = resourceStudyOption.getApplicationStartDate();
            LocalDate applicationCloseDate = resourceStudyOption.getApplicationCloseDate();

            if (!(applicationStartDate == null || applicationCloseDate == null)) {
                LocalDate studyOptionStart = resourceStudyOption.getApplicationStartDate();
                LocalDate earliestStartDate = studyOptionStart.isBefore(baseline) ? baseline : studyOptionStart;
                earliestStartDate = earliestStartDate.withDayOfWeek(MONDAY);
                return earliestStartDate.isBefore(studyOptionStart) ? earliestStartDate.plusWeeks(1) : earliestStartDate;
            }
        }

        return new LocalDate().withDayOfWeek(MONDAY);
    }

    public LocalDate getLatestStartDate(Application application, ResourceStudyOption resourceStudyOption) {
        if (resourceStudyOption != null) {
            LocalDate applicationStartDate = resourceStudyOption.getApplicationStartDate();
            LocalDate applicationCloseDate = resourceStudyOption.getApplicationCloseDate();

            if (!(applicationStartDate == null || applicationCloseDate == null)) {
                LocalDate closeDate = resourceStudyOption.getApplicationCloseDate().plusMonths(
                        PrismOpportunityType.valueOf(((ResourceOpportunity) resourceStudyOption.getResource()).getOpportunityType().getName())
                                .getDefaultStartBuffer());
                LocalDate latestStartDate = closeDate.withDayOfWeek(MONDAY);
                return latestStartDate.isAfter(closeDate) ? latestStartDate.minusWeeks(1) : latestStartDate;
            }
        }

        return new LocalDate().plusYears(1);
    }

    public String getApplicationExportReference(Application application) {
        return applicationDAO.getApplicationExportReference(application);
    }

    public String getApplicationCreatorIpAddress(Application application) {
        return applicationDAO.getApplicationCreatorIpAddress(application);
    }

    public List<ApplicationQualification> getApplicationExportQualifications(Application application) {
        return applicationDAO.getApplicationExportQualifications(application);
    }

    public List<ApplicationRefereeRepresentation> getApplicationExportReferees(Application application) throws Exception {
        List<ApplicationReferenceDTO> references = applicationDAO.getApplicationRefereesResponded(application);

        Institution institution = application.getInstitution();
        List<ApplicationRefereeRepresentation> representations = Lists.newArrayListWithCapacity(references.size());
        for (ApplicationReferenceDTO reference : references) {
            representations.add(applicationMapper.getApplicationRefereeRepresentation(reference, institution));
        }

        // TODO move this shit into the adapter
        WorkflowPropertyConfiguration configuration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithOrWithoutVersion(
                WORKFLOW_PROPERTY, application, APPLICATION_ASSIGN_REFEREE,
                application.getWorkflowPropertyConfigurationVersion());

        int referencesPending = configuration.getMinimum() - references.size();
        if (referencesPending > 0) {
            DomicileUseDTO domicileUseDTO = importedEntityService.getMostUsedDomicile(institution);

            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(application);
            String jobTitle = loader.load(SYSTEM_ROLE_APPLICATION_ADMINISTRATOR);
            String addressLineMock = loader.load(SYSTEM_ADDRESS_LINE_MOCK);
            String addressCodeMock = loader.load(SYSTEM_ADDRESS_CODE_MOCK);
            String phoneMock = loader.load(SYSTEM_PHONE_MOCK);

            if (domicileUseDTO != null) {
                ImportedEntityResponse domicileMock = importedEntityMapper.getImportedEntityRepresentation(domicileUseDTO.getDomicile(), institution);

                for (int i = 0; i < referencesPending; i++) {
                    representations.add(new ApplicationRefereeRepresentation()
                            .withUser(userMapper.getUserRepresentationSimple(institution.getUser()))
                            .withJobTitle(jobTitle)
                            .withAddress(
                                    new AddressApplicationRepresentation().withDomicile(domicileMock).withAddressLine1(addressLineMock)
                                            .withAddressCode(addressLineMock).withAddressCode(addressCodeMock)).withPhone(phoneMock));
                }
            }
        }

        return representations;
    }

    public List<User> getApplicationRefereesNotResponded(Application application) {
        return applicationDAO.getApplicationRefereesNotResponded(application);
    }

    public List<Integer> getApplicationsForExport() {
        return applicationDAO.getApplicationsForExport();
    }

    public DataTable getApplicationReport(ResourceListFilterDTO filter) throws Exception {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(systemService.getSystem());

        PrismScope scopeId = PrismScope.APPLICATION;
        List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(APPLICATION);

        User user = userService.getCurrentUser();
        resourceListFilterService.saveOrGetByUserAndScope(user, scopeId, filter);
        Set<Integer> assignedApplications = resourceService.getAssignedResources(user, scopeId, parentScopeIds, filter);

        List<PrismWorkflowPropertyDefinition> workflowPropertyDefinitions = applicationDAO.getApplicationWorkflowPropertyDefinitions(assignedApplications);
        List<PrismActionRedactionType> redactions = actionService.getRedactions(APPLICATION, assignedApplications, user);

        DataTable dataTable = new DataTable();

        List<ColumnDescription> headers = Lists.newLinkedList();
        List<PrismReportColumn> columns = Lists.newLinkedList();
        List<String> columnAccessors = Lists.newLinkedList();
        for (PrismReportColumn column : PrismReportColumn.values()) {
            if ((column.getDefinitions().isEmpty() || !Collections.disjoint(column.getDefinitions(), workflowPropertyDefinitions))
                    && (redactions.isEmpty() || Collections.disjoint(redactions, column.getRedactions()))) {
                headers.add(new ColumnDescription(column.getAccessor(), TEXT, loader.load(column.getTitle())));
                columns.add(column);
                columnAccessors.add(column.getColumnAccessor());
            }
        }

        headers.add(new ColumnDescription("link", TEXT, loader.load(SYSTEM_LINK)));
        dataTable.addColumns(headers);

        String dateFormat = loader.load(SYSTEM_DATE_FORMAT);
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
                    value = index == null ? "" : loader.load((PrismDisplayPropertyDefinition) PrismReflectionUtils.getProperty(index, "displayProperty"));
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

    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations(Application application) {
        List<WorkflowPropertyConfigurationRepresentation> configurations = (List<WorkflowPropertyConfigurationRepresentation>) (List<?>) //
        customizationService.getConfigurationRepresentationsWithOrWithoutVersion(WORKFLOW_PROPERTY, application, //
                application.getWorkflowPropertyConfigurationVersion());
        if (application.isSubmitted()) {
            for (WorkflowPropertyConfigurationRepresentation configuration : configurations) {
                PrismWorkflowPropertyDefinition property = (PrismWorkflowPropertyDefinition) configuration.getDefinitionId();
                if (property == APPLICATION_ASSIGN_REFEREE) {
                    configuration.setMaximum(property.getMaximumPermitted());
                } else if (property == APPLICATION_POSITION_DETAIL
                        && BooleanUtils.isTrue(application.getProgram().getRequirePositionDefinition())) {
                    configuration.setEnabled(true);
                    configuration.setRequired(true);
                }
            }
        }
        return configurations;
    }

    public <T extends Resource> List<Application> getUserAdministratorApplications(HashMultimap<PrismScope, T> userAdministratorResources) {
        return userAdministratorResources.isEmpty() ? Lists.<Application> newArrayList() : applicationDAO
                .getUserAdministratorApplications(userAdministratorResources);
    }

    public void prepopulateApplication(Application application) {
        Application previousApplication = applicationDAO.getPreviousSubmittedApplication(application);
        if (previousApplication != null) {
            applicationContext.getBean(ApplicationCopyHelper.class).copyApplication(application, previousApplication);
            BeanPropertyBindingResult errors = validateApplication(application);
            for (ObjectError error : errors.getAllErrors()) {
                Object property = PrismReflectionUtils.getProperty(application, error.getObjectName());
                if (ApplicationSection.class.isAssignableFrom(property.getClass())) {
                    ApplicationSection section = (ApplicationSection) property;
                    section.setLastUpdatedTimestamp(null);
                }
            }
        }
    }

    public ApplicationRatingSummaryDTO getApplicationRatingSummary(Application application) {
        return applicationDAO.getApplicationRatingSummary(application);
    }

    public ApplicationRatingSummaryDTO getApplicationRatingSummary(ResourceParent resource) {
        return applicationDAO.getApplicationRatingSummary(resource);
    }

    public ApplicationReferee getApplicationReferee(Application application, User user) {
        return applicationDAO.getApplicationReferee(application, user);
    }

    public BeanPropertyBindingResult validateApplication(Application application) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(application, "application");
        ValidationUtils.invokeValidator(applicationValidator, application, errors);
        return errors;
    }

    public List<Integer> getApplicationsByMatchingSuggestedSupervisor(String searchTerm) {
        return applicationDAO.getApplicationsByMatchingSuggestedSupervisor(searchTerm);
    }

    public ApplicationQualification getLatestApplicationQualification(Application application) {
        return applicationDAO.getLatestApplicationQualification(application);
    }

    public ApplicationEmploymentPosition getLatestApplicationEmploymentPosition(Application application) {
        return applicationDAO.getLatestApplicationEmploymentPosition(application);
    }

    public Long getProvidedReferenceCount(Application application) {
        return applicationDAO.getProvidedReferenceCount(application);
    }

    public Long getDeclinedReferenceCount(Application application) {
        return applicationDAO.getDeclinedReferenceCount(application);
    }

    public List<OtherApplicationSummaryRepresentation> getOtherLiveApplications(Application application) {
        return applicationDAO.getOtherLiveApplications(application);
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByYear(resource, constraints);
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByMonth(resource, constraints);
    }

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByWeek(resource, constraints);
    }

    public boolean isCanViewEqualOpportunitiesData(Application application, User user) {
        List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(application, user);
        if (actionEnhancements.size() > 0) {
            actionEnhancements.retainAll(APPLICATION_EQUAL_OPPORTUNITIES_VIEWER.getActionEnhancements());
            return actionEnhancements.size() > 0;
        }
        return false;
    }

    public boolean isApproved(Integer applicationId) {
        Application application = getById(applicationId);
        return resourceService.getResourceStateGroups(application).contains(PrismStateGroup.APPLICATION_APPROVED)
                && !application.getState().equals(APPLICATION_APPROVED);
    }

    private LocalDate getRecommendedStartDate(Application application, LocalDate earliest, LocalDate latest, LocalDate baseline) {
        ResourceParent parentResource = (ResourceParent) application.getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(parentResource.getClass())) {
            PrismOpportunityType opportunityType = PrismOpportunityType.valueOf(((ResourceOpportunity) parentResource).getOpportunityType().getName());
            DefaultStartDateDTO defaults = opportunityType.getDefaultStartDate(baseline);

            LocalDate immediate = defaults.getImmediate();
            LocalDate scheduled = defaults.getScheduled();

            LocalDate recommended = application.getDefaultStartType() == SCHEDULED ? scheduled : immediate;

            if (recommended.isBefore(earliest)) {
                recommended = earliest.plusWeeks(opportunityType.getDefaultStartDelay());
            }

            if (recommended.isAfter(latest)) {
                recommended = immediate.isAfter(latest) ? latest : immediate;
            }

            return recommended;
        }

        return baseline.withDayOfWeek(MONDAY).plusWeeks(4);
    }

}
