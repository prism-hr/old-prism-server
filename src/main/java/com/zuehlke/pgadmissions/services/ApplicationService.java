package com.zuehlke.pgadmissions.services;

import static com.google.visualization.datasource.datatable.value.ValueType.TEXT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_COVERING_LETTER_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_CV_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ADDRESS_CODE_MOCK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ADDRESS_LINE_MOCK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_LINK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PHONE_MOCK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_POSITION_DETAIL;
import static com.zuehlke.pgadmissions.utils.PrismConstants.ANGULAR_HASH;
import static org.joda.time.DateTimeConstants.MONDAY;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

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

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSection;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismReportColumn;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedStudyOption;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ApplicationRatingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReferenceDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReportListRowDTO;
import com.zuehlke.pgadmissions.dto.DefaultStartDateDTO;
import com.zuehlke.pgadmissions.dto.DomicileUseDTO;
import com.zuehlke.pgadmissions.exceptions.ApplicationExportException;
import com.zuehlke.pgadmissions.exceptions.PrismCannotApplyException;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceReportFilterDTO.ResourceReportFilterPropertyDTO;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation.DocumentSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation.EmploymentPositionSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation.QualificationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.ApplicationValidator;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

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

    public ApplicationStartDateRepresentation getStartDateRepresentation(Integer applicationId, PrismStudyOption studyOptionId) {
        LocalDate baseline = new LocalDate();
        Application application = getById(applicationId);

        ImportedStudyOption studyOption = importedEntityService.getByName(ImportedStudyOption.class, studyOptionId.name());
        ResourceStudyOption resourceStudyOption = resourceService.getStudyOption((ResourceOpportunity) application.getParentResource(), studyOption);

        if (resourceStudyOption == null && !application.getParentResource().sameAs(application.getInstitution())) {
            throw new PrismCannotApplyException();
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
                        resourceStudyOption.getResource().getOpportunityType().getPrismOpportunityType().getDefaultStartBuffer());
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

    public User getPrimarySupervisor(Comment offerRecommendationComment) {
        return applicationDAO.getPrimarySupervisor(offerRecommendationComment);
    }

    public List<ApplicationQualification> getApplicationExportQualifications(Application application) {
        return applicationDAO.getApplicationExportQualifications(application);
    }

    public List<ApplicationReferenceDTO> getApplicationExportReferees(Application application) throws Exception {
        List<ApplicationReferenceDTO> references = applicationDAO.getApplicationRefereesResponded(application);

        Institution institution = application.getInstitution();
        DomicileUseDTO domicileMock = importedEntityService.getMostUsedDomicile(institution);

        if (domicileMock == null) {
            throw new ApplicationExportException("No export domicile for mock referee for " + application.getCode());
        }

        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(application);
        String addressLineMock = loader.load(SYSTEM_ADDRESS_LINE_MOCK);

        WorkflowPropertyConfiguration configuration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithOrWithoutVersion(
                WORKFLOW_PROPERTY, application, application.getInstitution().getUser(), APPLICATION_ASSIGN_REFEREE,
                application.getWorkflowPropertyConfigurationVersion());

        int referencesPending = configuration.getMinimum() - references.size();
        for (int i = 0; i < referencesPending; i++) {
            references.add(new ApplicationReferenceDTO().withUser(institution.getUser()).withJobTitle(loader.load(SYSTEM_ROLE_APPLICATION_ADMINISTRATOR))
                    .withAddressLine1(addressLineMock).withAddressLine2(addressLineMock).withAddressTown(addressLineMock).withAddressRegion(addressLineMock)
                    .withAddressCode(loader.load(SYSTEM_ADDRESS_CODE_MOCK)).withAddressDomicile(domicileMock.getCode())
                    .withPhone(loader.load(SYSTEM_PHONE_MOCK)));
        }

        return references;
    }

    public List<User> getUnassignedApplicationReferees(Application application) {
        return applicationDAO.getUnassignedApplicationReferees(application);
    }

    public void filterResourceListData(ResourceListRowRepresentation representation, User currentUser) {
        if (currentUser.getId().equals(representation.getUser().getId())) {
            representation.setApplicationRatingAverage(null);
        }
    }

    public List<Integer> getApplicationsForExport() {
        return applicationDAO.getApplicationsForExport();
    }

    public ApplicationSummaryRepresentation getApplicationSummary(Integer applicationId) {
        Application application = getById(applicationId);

        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(application);
        String dateFormat = loader.load(SYSTEM_DATE_FORMAT);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();

        boolean programDetailNull = programDetail == null;
        boolean personalDetailNull = personalDetail == null;

        PrismStudyOption studyOption = programDetailNull ? null : programDetail.getStudyOptionDisplay();
        ApplicationSummaryRepresentation summary = new ApplicationSummaryRepresentation().withCreatedDate(application.getCreatedTimestampDisplay(dateFormat))
                .withSubmittedDate(application.getSubmittedTimestampDisplay(dateFormat)).withClosingDate(application.getClosingDateDisplay(dateFormat))
                .withPrimaryThemes(application.getPrimaryThemeDisplay()).withSecondaryThemes(application.getSecondaryThemeDisplay())
                .withPhone(personalDetail == null ? null : personalDetail.getPhone()).withSkype(personalDetailNull ? null : personalDetail.getSkype())
                .withStudyOption(studyOption == null ? null : loader.load(programDetail.getStudyOptionDisplay().getDisplayProperty()))
                .withReferralSource(programDetail == null ? null : programDetail.getReferralSourceDisplay());

        ApplicationQualification latestQualification = applicationDAO.getLatestApplicationQualification(application);
        if (latestQualification != null) {
            ImportedProgram importedProgram = latestQualification.getProgram();
            summary.setLatestQualification(new QualificationSummaryRepresentation().withTitle(importedProgram.getQualification())
                    .withSubject(importedProgram.getName()).withGrade(latestQualification.getGrade())
                    .withInstitution(importedProgram.getInstitution().getName()).withStartDate(latestQualification.getStartDateDisplay(dateFormat))
                    .withEndDate(latestQualification.getAwardDateDisplay(dateFormat)));
        }

        ApplicationEmploymentPosition latestEmploymentPosition = applicationDAO.getLatestApplicationEmploymentPosition(application);
        if (latestEmploymentPosition != null) {
            summary.setLatestEmploymentPosition(new EmploymentPositionSummaryRepresentation().withPosition(latestEmploymentPosition.getPosition())
                    .withEmployer(latestEmploymentPosition.getEmployerName()).withStartDate(latestEmploymentPosition.getStartDateDisplay(dateFormat))
                    .withEndDate(latestEmploymentPosition.getEndDateDisplay(dateFormat)));
        }

        ApplicationDocument applicationDocument = application.getDocument();
        if (applicationDocument != null) {
            Map<String, PrismDisplayPropertyDefinition> documentProperties = ImmutableMap.of( //
                    "personalStatement", APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL, //
                    "researchStatement", APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL, //
                    "cv", APPLICATION_DOCUMENT_CV_LABEL, //
                    "coveringLetter", APPLICATION_DOCUMENT_COVERING_LETTER_LABEL);

            for (Entry<String, PrismDisplayPropertyDefinition> documentProperty : documentProperties.entrySet()) {
                Document document = (Document) PrismReflectionUtils.getProperty(applicationDocument, documentProperty.getKey());
                if (document != null) {
                    summary.addDocument(new DocumentSummaryRepresentation().withId(document.getId()).withLabel(loader.load(documentProperty.getValue())));
                }
            }
        }

        Long providedReferenceCount = applicationDAO.getProvidedReferenceCount(application);
        summary.setReferenceProvidedCount(providedReferenceCount == null ? null : providedReferenceCount.intValue());

        Long declinedReferenceCount = applicationDAO.getDeclinedReferenceCount(application);
        summary.setReferenceDeclinedCount(declinedReferenceCount == null ? null : declinedReferenceCount.intValue());

        summary.setOtherLiveApplications(applicationDAO.getOtherLiveApplications(application));
        return summary;
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

    public List<ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByYear(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        return applicationDAO.getApplicationProcessingSummariesByYear(resource, constraints);
    }

    public LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByMonth(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        LinkedHashMultimap<String, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationDAO.getApplicationProcessingSummariesByMonth(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(processingSummary.getApplicationYear(), processingSummary);
        }
        return index;
    }

    public LinkedHashMultimap<ApplicationProcessingMonth, ApplicationProcessingSummaryDTO> getApplicationProcessingSummariesByWeek(ResourceParent resource,
            List<ResourceReportFilterPropertyDTO> constraints) {
        LinkedHashMultimap<ApplicationProcessingMonth, ApplicationProcessingSummaryDTO> index = LinkedHashMultimap.create();
        List<ApplicationProcessingSummaryDTO> processingSummaries = applicationDAO.getApplicationProcessingSummariesByWeek(resource, constraints);
        for (ApplicationProcessingSummaryDTO processingSummary : processingSummaries) {
            index.put(new ApplicationProcessingMonth(processingSummary.getApplicationYear(), processingSummary.getApplicationMonth()), processingSummary);
        }
        return index;
    }

    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations(Application application) throws Exception {
        List<WorkflowPropertyConfigurationRepresentation> configurations = (List<WorkflowPropertyConfigurationRepresentation>) (List<?>) //
        customizationService.getConfigurationRepresentationsWithOrWithoutVersion(WORKFLOW_PROPERTY, application, //
                application.getWorkflowPropertyConfigurationVersion());
        if (application.isSubmitted()) {
            for (WorkflowPropertyConfigurationRepresentation configuration : configurations) {
                PrismWorkflowPropertyDefinition property = (PrismWorkflowPropertyDefinition) configuration.getProperty();
                if (property == APPLICATION_ASSIGN_REFEREE) {
                    configuration.setMaximum(property.getMaximumPermitted());
                } else if (property == APPLICATION_POSITION_DETAIL
                        && BooleanUtils.isTrue(application.getProgram().getRequireProjectDefinition())) {
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

    private LocalDate getRecommendedStartDate(Application application, LocalDate earliest, LocalDate latest, LocalDate baseline) {
        if (!application.getParentResource().sameAs(application.getInstitution())) {
            PrismOpportunityType opportunityType = application.getProgram().getOpportunityType().getPrismOpportunityType();
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

    public static class ApplicationProcessingMonth {

        private String applicationYear;

        private Integer applicationMonth;

        public ApplicationProcessingMonth(String applicationYear, Integer applicationMonth) {
            this.applicationYear = applicationYear;
            this.applicationMonth = applicationMonth;
        }

        public String getApplicationYear() {
            return applicationYear;
        }

        public Integer getApplicationMonth() {
            return applicationMonth;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(applicationYear, applicationMonth);
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            final ApplicationProcessingMonth other = (ApplicationProcessingMonth) object;
            return Objects.equal(applicationYear, other.getApplicationYear()) && Objects.equal(applicationMonth, other.getApplicationMonth());
        }

    }

}
