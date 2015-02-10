package com.zuehlke.pgadmissions.services;

import static com.google.visualization.datasource.datatable.value.ValueType.TEXT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_LINK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSection;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationPositionDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismReportColumn;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReportListRowDTO;
import com.zuehlke.pgadmissions.dto.DefaultStartDateDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentApplicationOfferDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentApplicationPositionDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.DocumentSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.EmploymentPositionSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.QualificationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.WorkflowPropertyConfigurationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.ApplicationValidator;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.Constants;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class ApplicationService {

    @Value("${application.url}")
    private String applicationUrl;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ApplicationExportService applicationExportService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceListFilterService resourceListFilterService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ApplicationValidator applicationValidator;

    @Autowired
    private ApplicationContext applicationContext;

    public Application getById(Integer id) {
        return entityService.getById(Application.class, id);
    }

    public Application create(User user, ApplicationDTO applicationDTO) throws Exception {
        Resource parentResource = entityService.getById(applicationDTO.getResourceScope().getResourceClass(), applicationDTO.getResourceId());
        return new Application().withUser(user).withParentResource(parentResource).withDoRetain(false).withCreatedTimestamp(new DateTime());
    }

    public void save(Application application) throws BeansException, IOException, IntegrationException {
        prepopulateApplication(application);
        entityService.save(application);
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

        StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, application.getInstitution(), studyOptionId.name());
        ProgramStudyOption programStudyOption = programService.getEnabledProgramStudyOption(application.getProgram(), studyOption);

        if (programStudyOption != null) {
            return new ApplicationStartDateRepresentation().withEarliestDate(getEarliestStartDate(programStudyOption.getId(), baseline))
                    .withRecommendedDate(getRecommendedStartDate(application, programStudyOption, baseline))
                    .withLatestDate(getLatestStartDate(programStudyOption.getId()));
        }

        return null;
    }

    public LocalDate getEarliestStartDate(Integer studyOptionId, LocalDate baseline) {
        if (studyOptionId == null) {
            return null;
        }

        ProgramStudyOption studyOption = entityService.getById(ProgramStudyOption.class, studyOptionId);
        LocalDate studyOptionStart = studyOption.getApplicationStartDate();
        LocalDate earliestStartDate = studyOptionStart.isBefore(baseline) ? baseline : studyOptionStart;
        earliestStartDate = earliestStartDate.withDayOfWeek(DateTimeConstants.MONDAY);
        return earliestStartDate.isBefore(studyOptionStart) ? earliestStartDate.plusWeeks(1) : earliestStartDate;
    }

    public LocalDate getLatestStartDate(Integer studyOptionId) {
        if (studyOptionId == null) {
            return null;
        }

        ProgramStudyOption studyOption = entityService.getById(ProgramStudyOption.class, studyOptionId);
        LocalDate closeDate = studyOption.getApplicationCloseDate().plusMonths(
                studyOption.getProgram().getProgramType().getPrismProgramType().getDefaultStartBuffer());
        LocalDate latestStartDate = closeDate.withDayOfWeek(DateTimeConstants.MONDAY);
        return latestStartDate.isAfter(closeDate) ? latestStartDate.minusWeeks(1) : latestStartDate;
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

    public List<ApplicationReferee> getApplicationExportReferees(Application application) {
        List<ApplicationReferee> refereesResponded = applicationDAO.getApplicationRefereesResponded(application);
        int refereesRespondedSize = refereesResponded.size();

        List<ApplicationReferee> refereesNotResponded = applicationDAO.getApplicationRefereesNotResponded(application);
        int refereesNotRespondedSize = refereesNotResponded.size();

        if (refereesNotRespondedSize == 0) {
            return refereesResponded;
        } else if (refereesRespondedSize == 0) {
            return refereesNotResponded;
        }

        WorkflowPropertyConfiguration configuration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithOrWithoutVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, application, application.getInstitution().getUser(),
                PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE, application.getWorkflowPropertyConfigurationVersion());

        for (int i = 0; i < (configuration.getMinimum() - refereesRespondedSize); i++) {
            refereesResponded.add(refereesNotResponded.get(i));
        }

        return refereesResponded;
    }

    public void validateApplicationAndThrowException(Application application) {
        BeanPropertyBindingResult errors = validateApplication(application);
        if (errors.hasErrors()) {
            throw new PrismValidationException("Application not completed", errors);
        }
    }

    public void preProcessApplication(Application application, Comment comment) {
        if (comment.isApplicationSubmittedComment()) {
            application.setSubmittedTimestamp(new DateTime());
            AdvertClosingDate advertClosingDate = application.getAdvert().getClosingDate();
            application.setClosingDate(advertClosingDate == null ? null : advertClosingDate.getClosingDate());
        }
    }

    public void postProcessApplication(Application application, Comment comment) throws DeduplicationException {
        if (comment.isApplicationCreatedComment()) {
            applicationSummaryService.incrementApplicationCreatedCount(application);
        }

        if (comment.isProjectCreateApplicationComment()) {
            synchroniseProjectSupervisors(application);
        }

        if (comment.isApplicationProvideReferenceComment()) {
            synchroniseReferees(application, comment);
        }

        if (comment.isApplicationConfirmOfferRecommendationComment()) {
            synchroniseOfferRecommendation(application, comment);
        }

        if (comment.isApplicationRatingComment()) {
            applicationSummaryService.summariseApplication(application, comment);
        }

        if (comment.isStateGroupTransitionComment()) {
            applicationSummaryService.summariseApplicationProcessing(application);
        }

        if (comment.isApplicationSubmittedComment()) {
            applicationSummaryService.incrementApplicationSubmittedCount(application);
        }

        if (comment.isApplicationApprovedComment()) {
            applicationSummaryService.incrementApplicationApprovedCount(application);
        }

        if (comment.isApplicationRejectedComment()) {
            applicationSummaryService.incrementApplicationRejectedCount(application);
        }

        if (comment.isApplicationWithdrawnComment()) {
            applicationSummaryService.incrementApplicationWithdrawnCount(application);
        }

        if (comment.isApplicationCompletionComment()) {
            application.setCompletionDate(comment.getCreatedTimestamp().toLocalDate());
        }

        if (comment.isApplicationPurgeComment()) {
            purgeApplication(application, comment);
        }
    }

    public ActionOutcomeDTO executeAction(@PathVariable Integer applicationId, @Valid @RequestBody CommentDTO commentDTO) throws DeduplicationException,
            InstantiationException, IllegalAccessException, BeansException, WorkflowEngineException, IOException, IntegrationException {
        Application application = entityService.getById(Application.class, applicationId);
        PrismAction actionId = commentDTO.getAction();

        if (actionId == PrismAction.APPLICATION_COMPLETE) {
            validateApplicationAndThrowException(application);
        }

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        User delegateUser = userService.getById(commentDTO.getDelegateUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());

        Comment comment = new Comment().withResource(application).withContent(commentDTO.getContent()).withUser(user).withDelegateUser(delegateUser)
                .withAction(action).withTransitionState(transitionState).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse())).withApplicationEligible(commentDTO.getApplicationEligible())
                .withApplicationInterested(commentDTO.getApplicationInterested()).withApplicationRating(commentDTO.getApplicationRating());

        CommentApplicationPositionDetailDTO positionDetailDTO = commentDTO.getPositionDetail();
        if (positionDetailDTO != null) {
            comment.setPositionDetail(new CommentApplicationPositionDetail().withPositionTitle(positionDetailDTO.getPositionTitle()).withPositionDescription(
                    positionDetailDTO.getPositionDescription()));
        }

        CommentApplicationOfferDetailDTO offerDetailDTO = commentDTO.getOfferDetail();
        if (offerDetailDTO != null) {
            comment.setOfferDetail(new CommentApplicationOfferDetail().withPositionProvisionStartDate(offerDetailDTO.getPositionProvisionalStartDate())
                    .withAppointmentConditions(offerDetailDTO.getAppointmentConditions()));
        }

        commentService.appendCommentProperties(comment, commentDTO);

        if (actionId == PrismAction.APPLICATION_COMPLETE) {
            Role refereeRole = entityService.getById(Role.class, PrismRole.APPLICATION_REFEREE);
            for (ApplicationReferee referee : application.getReferees()) {
                comment.getAssignedUsers().add(new CommentAssignedUser().withUser(referee.getUser()).withRole(refereeRole));
            }
            Role supervisorRole = entityService.getById(Role.class, PrismRole.APPLICATION_SUGGESTED_SUPERVISOR);
            for (ApplicationSupervisor supervisor : application.getSupervisors()) {
                comment.getAssignedUsers().add(new CommentAssignedUser().withUser(supervisor.getUser()).withRole(supervisorRole));
            }
        }

        if (commentDTO.getAppointmentTimeslots() != null) {
            commentService.appendAppointmentTimeslots(comment, commentDTO);
        }

        if (commentDTO.getAppointmentPreferences() != null) {
            commentService.appendAppointmentPreferences(comment, commentDTO);
        }

        if (commentDTO.getRejectionReason() != null) {
            commentService.appendRejectionReason(comment, commentDTO);
        }

        return actionService.executeUserAction(application, action, comment);
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

        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(application, userService.getCurrentUser());
        String dateFormat = loader.load(PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT);

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
                .withReferralSource(programDetail == null ? null : programDetail.getReferralSourceDisplay()).withReferrer(application.getReferrer());

        ApplicationQualification latestQualification = applicationDAO.getLatestApplicationQualification(application);
        if (latestQualification != null) {
            summary.setLatestQualification(new QualificationSummaryRepresentation().withTitle(latestQualification.getTitle())
                    .withSubject(latestQualification.getSubject()).withGrade(latestQualification.getGrade())
                    .withInstitution(latestQualification.getInstitutionDisplay()).withStartDate(latestQualification.getStartDateDisplay(dateFormat))
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
            Map<String, PrismDisplayPropertyDefinition> documentProperties = ImmutableMap.of("personalStatement",
                    PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL, "researchStatement",
                    PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL, "cv",
                    PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_CV_LABEL, "coveringLetter",
                    PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_COVERING_LETTER_LABEL);

            for (Entry<String, PrismDisplayPropertyDefinition> documentProperty : documentProperties.entrySet()) {
                Document document = (Document) ReflectionUtils.getProperty(applicationDocument, documentProperty.getKey());
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
        summary.setProcessings(applicationSummaryService.getProcessings(application));

        return summary;
    }

    public DataTable getApplicationReport(ResourceListFilterDTO filter) throws TypeMismatchException, IntrospectionException {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(systemService.getSystem(), userService.getCurrentUser());

        PrismScope scopeId = PrismScope.APPLICATION;
        List<PrismScope> parentScopeIds = scopeService.getParentScopesDescending(PrismScope.APPLICATION);

        User user = userService.getCurrentUser();
        resourceListFilterService.saveOrGetByUserAndScope(user, scopeId, filter);
        Set<Integer> assignedApplications = resourceService.getAssignedResources(user, scopeId, parentScopeIds, filter);

        List<PrismWorkflowPropertyDefinition> workflowPropertyDefinitions = applicationDAO.getApplicationWorkflowPropertyDefinitions(assignedApplications);
        List<PrismActionRedactionType> redactions = actionService.getRedactions(PrismScope.APPLICATION, assignedApplications, user);

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

        List<ApplicationReportListRowDTO> reportRows = getApplicationReport(assignedApplications, Joiner.on(", ").join(columnAccessors));
        for (ApplicationReportListRowDTO reportRow : reportRows) {
            TableRow row = new TableRow();
            for (PrismReportColumn column : columns) {
                String value = null;
                String getMethod = "get" + WordUtils.capitalize(column.getAccessor()) + "Display";
                switch (column.getAccessorType()) {
                case DATE:
                    value = (String) ReflectionUtils.invokeMethod(reportRow, getMethod, dateFormat);
                    break;
                case DISPLAY_PROPERTY:
                    Enum<?> index = (Enum<?>) ReflectionUtils.invokeMethod(reportRow, getMethod);
                    value = index == null ? "" : loader.load((PrismDisplayPropertyDefinition) ReflectionUtils.getProperty(index, "displayProperty"));
                    break;
                case STRING:
                    value = (String) ReflectionUtils.invokeMethod(reportRow, getMethod);
                    break;
                }
                row.addCell(value);
            }
            row.addCell(applicationUrl + "/" + Constants.ANGULAR_HASH + "/application/" + reportRow.getIdDisplay() + "/view");
            dataTable.addRow(row);
        }

        return dataTable;
    }

    public List<WorkflowPropertyConfigurationRepresentation> getWorkflowPropertyConfigurations(Application application) {
        List<WorkflowPropertyConfigurationRepresentation> configurations = (List<WorkflowPropertyConfigurationRepresentation>) (List<?>) customizationService
                .getConfigurationRepresentationsWithOrWithoutVersion(PrismConfiguration.WORKFLOW_PROPERTY, application,
                        application.getWorkflowPropertyConfigurationVersion());
        if (application.isSubmitted()) {
            for (WorkflowPropertyConfigurationRepresentation configuration : configurations) {
                PrismWorkflowPropertyDefinition definitionId = (PrismWorkflowPropertyDefinition) configuration.getDefinitionId();
                if (definitionId == PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE) {
                    configuration.setMaximum(definitionId.getMaximumPermitted());
                } else if (definitionId == PrismWorkflowPropertyDefinition.APPLICATION_POSITION_DETAIL
                        && BooleanUtils.isTrue(application.getProgram().getRequireProjectDefinition())) {
                    configuration.setEnabled(true);
                    configuration.setRequired(true);
                }
            }
        }
        return configurations;
    }

    private List<ApplicationReportListRowDTO> getApplicationReport(Set<Integer> assignedApplications, String columns) {
        return assignedApplications.isEmpty() ? new ArrayList<ApplicationReportListRowDTO>() : applicationDAO.getApplicationReport(assignedApplications,
                columns);
    }

    private void purgeApplication(Application application, Comment comment) {
        if (!application.getRetain()) {
            application.setApplicationRatingCount(null);
            application.setApplicationRatingAverage(null);
            application.setProgramDetail(null);
            application.getSupervisors().clear();
            application.setPersonalDetail(null);
            application.setAddress(null);
            application.getQualifications().clear();
            application.getEmploymentPositions().clear();
            application.getFundings().clear();
            application.getReferees().clear();
            application.setDocument(null);
            application.setAdditionalInformation(null);
        }
        commentService.delete(application, comment);
    }

    private void synchroniseProjectSupervisors(Application application) {
        Role supervisorRole = roleService.getById(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR);
        List<User> supervisorUsers = roleService.getRoleUsers(application, supervisorRole);

        for (User supervisorUser : supervisorUsers) {
            application.getSupervisors().add(new ApplicationSupervisor().withUser(supervisorUser).withAcceptedSupervision(true));
        }
    }

    private void synchroniseReferees(Application application, Comment comment) {
        ApplicationReferee referee = applicationDAO.getRefereeByUser(application, comment.getActionOwner());
        referee.setComment(comment);
    }

    private void synchroniseOfferRecommendation(Application application, Comment comment) {
        CommentApplicationOfferDetail offerDetail = comment.getOfferDetail();
        if (offerDetail != null) {
            application.setConfirmedStartDate(offerDetail.getPositionProvisionalStartDate());
            application.setConfirmedOfferType(offerDetail.getAppointmentConditions() == null ? PrismOfferType.UNCONDITIONAL : PrismOfferType.CONDITIONAL);
        }
    }

    private LocalDate getRecommendedStartDate(Application application, ProgramStudyOption studyOption, LocalDate baseline) {
        if (studyOption == null) {
            return null;
        }

        LocalDate earliest = getEarliestStartDate(studyOption.getId(), baseline);
        LocalDate latest = getLatestStartDate(studyOption.getId());

        PrismProgramType programType = application.getProgram().getProgramType().getPrismProgramType();
        DefaultStartDateDTO defaults = programType.getDefaultStartDate(baseline);

        LocalDate immediate = defaults.getImmediate();
        LocalDate scheduled = defaults.getScheduled();

        LocalDate recommended = application.getDefaultStartType() == SCHEDULED ? scheduled : immediate;

        if (recommended.isBefore(earliest)) {
            recommended = earliest.plusWeeks(programType.getDefaultStartDelay());
        }

        if (recommended.isAfter(latest)) {
            recommended = immediate.isAfter(latest) ? latest : immediate;
        }

        return recommended;
    }

    private BeanPropertyBindingResult validateApplication(Application application) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(application, "application");
        ValidationUtils.invokeValidator(applicationValidator, application, errors);
        return errors;
    }

    private void prepopulateApplication(Application application) throws BeansException, IOException, IntegrationException {
        Application previousApplication = applicationDAO.getPreviousSubmittedApplication(application);
        if (previousApplication != null) {
            applicationContext.getBean(ApplicationCopyHelper.class).copyApplication(application, previousApplication);
            BeanPropertyBindingResult errors = validateApplication(application);
            for (ObjectError error : errors.getAllErrors()) {
                Object property = ReflectionUtils.getProperty(application, error.getObjectName());
                if (ApplicationSection.class.isAssignableFrom(property.getClass())) {
                    ReflectionUtils.setProperty(property, "lastUpdatedTimestamp", null);
                }
            }
        }
    }

}
