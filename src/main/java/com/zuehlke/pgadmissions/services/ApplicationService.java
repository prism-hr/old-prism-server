package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismProgramStartType.SCHEDULED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationPositionDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
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
import com.zuehlke.pgadmissions.dto.DefaultStartDateDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO.CommentApplicationOfferDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO.CommentApplicationPositionDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.DocumentSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.EmploymentPositionSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.QualificationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.ApplicationValidator;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Service
@Transactional
public class ApplicationService {

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
    private ApplicationCopyHelper applicationCopyHelper;

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
    private ApplicationValidator applicationValidator;

    @Autowired
    private ApplicationContext applicationContext;

    public Application getById(Integer id) {
        return entityService.getById(Application.class, id);
    }

    public Application create(User user, ApplicationDTO applicationDTO) throws Exception {
        Resource parentResource = entityService.getById(applicationDTO.getResourceScope().getResourceClass(), applicationDTO.getResourceId());
        Application application = new Application().withUser(user).withParentResource(parentResource).withDoRetain(false).withCreatedTimestamp(new DateTime());

        Application previousApplication = getPreviousApplication(application);
        if (previousApplication != null) {
            applicationCopyHelper.copyApplicationData(application, previousApplication);
        }

        return application;
    }

    public void save(Application application) {
        entityService.save(application);
    }

    public Application getByCode(String code) {
        return entityService.getByProperty(Application.class, "code", code);
    }

    public ApplicationStartDateRepresentation getStartDateRepresentation(Integer applicationId, PrismStudyOption studyOptionId) {
        LocalDate baseline = new LocalDate();
        Application application = getById(applicationId);
        StudyOption studyOption = importedEntityService.getImportedEntityByCode(StudyOption.class, application.getInstitution(), studyOptionId.name());
        ProgramStudyOption programStudyOption = programService.getEnabledProgramStudyOption(application.getProgram(), studyOption);
        return new ApplicationStartDateRepresentation().withEarliestDate(getEarliestStartDate(programStudyOption.getId(), baseline))
                .withRecommendedDate(getRecommendedStartDate(application, programStudyOption, baseline))
                .withLatestDate(getLatestStartDate(programStudyOption.getId()));
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

        if (refereesRespondedSize == 0 || refereesNotRespondedSize == 0) {
            return refereesResponded;
        }

        WorkflowPropertyConfiguration configuration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithOrWithoutVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, application, application.getInstitution().getUser(),
                PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE, application.getWorkflowPropertyConfigurationVersion());

        for (int i = 0; i < (configuration.getMinimum() - refereesRespondedSize); i++) {
            refereesResponded.add(refereesNotResponded.get(i));
        }

        return refereesResponded;
    }

    public void validateApplication(Application application) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(application, "application");
        ValidationUtils.invokeValidator(applicationValidator, application, errors);
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

        if (comment.isTransitionComment()) {
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
            InstantiationException, IllegalAccessException {
        Application application = entityService.getById(Application.class, applicationId);
        PrismAction actionId = commentDTO.getAction();

        if (actionId == PrismAction.APPLICATION_COMPLETE) {
            validateApplication(application);
        }

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        User delegateUser = userService.getById(commentDTO.getDelegateUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());

        Comment comment = new Comment().withResource(application).withContent(commentDTO.getContent()).withUser(user).withDelegateUser(delegateUser)
                .withAction(action).withTransitionState(transitionState).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse())).withApplicationEligible(commentDTO.getApplicationEligible())
                .withApplicationInterested(commentDTO.getApplicationInterested()).withInterviewDateTime(commentDTO.getInterviewDateTime())
                .withInterviewTimeZone(commentDTO.getInterviewTimeZone()).withInterviewDuration(commentDTO.getInterviewDuration())
                .withInterviewerInstructions(commentDTO.getInterviewerInstructions()).withIntervieweeInstructions(commentDTO.getIntervieweeInstructions())
                .withInterviewLocation(commentDTO.getInterviewLocation()).withApplicationRating(commentDTO.getApplicationRating());

        CommentApplicationPositionDetailDTO positionDetailDTO = commentDTO.getPositionDetail();
        if (positionDetailDTO != null) {
            comment.setPositionDetail(new CommentApplicationPositionDetail().withPositionTitle(positionDetailDTO.getPositionTitle()));
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

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        boolean personaDetailNull = personalDetail == null;

        ApplicationSummaryRepresentation summary = new ApplicationSummaryRepresentation().withCreatedDate(application.getCreatedTimestampDisplay(dateFormat))
                .withSubmittedDate(application.getSubmittedTimestampDisplay(dateFormat)).withClosingDate(application.getClosingDateDisplay(dateFormat))
                .withPrimaryThemes(application.getPrimaryThemeDisplay()).withSecondaryThemes(application.getSecondaryThemeDisplay())
                .withPhone(personalDetail == null ? null : personalDetail.getPhone()).withSkype(personaDetailNull ? null : personalDetail.getSkype());

        ApplicationQualification latestQualification = applicationDAO.getLatestApplicationQualification(application);
        if (latestQualification != null) {
            summary.setLatestQualification(new QualificationSummaryRepresentation().withTitle(latestQualification.getTitle())
                    .withSubject(latestQualification.getSubject()).withGrade(latestQualification.getGrade())
                    .withInstitution(latestQualification.getInstitution().getName()).withStartDate(latestQualification.getStartDateDisplay(dateFormat))
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

    public DataTable getApplicationReport(ResourceListFilterDTO filterDTO) throws TypeMismatchException {
        DataTable dataTable = new DataTable();

        ArrayList<ColumnDescription> cd = Lists.newArrayList();
        cd.add(new ColumnDescription("applicationId", ValueType.TEXT, "Application ID"));
        cd.add(new ColumnDescription("firstNames", ValueType.TEXT, "First Name(s)"));
        cd.add(new ColumnDescription("lastName", ValueType.TEXT, "Last Name"));
        cd.add(new ColumnDescription("email", ValueType.TEXT, "E-mail"));
        cd.add(new ColumnDescription("nationality1", ValueType.TEXT, "Nationality 1"));
        cd.add(new ColumnDescription("nationality2", ValueType.TEXT, "Nationality 2"));
        cd.add(new ColumnDescription("dateOfBirth", ValueType.DATE, "Date Of Birth"));
        cd.add(new ColumnDescription("gender", ValueType.TEXT, "Gender"));
        cd.add(new ColumnDescription("programmeId", ValueType.TEXT, "Programme ID"));
        cd.add(new ColumnDescription("programmeName", ValueType.TEXT, "Programme Name"));
        cd.add(new ColumnDescription("projectTitle", ValueType.TEXT, "Project Title"));
        cd.add(new ColumnDescription("studyOption", ValueType.TEXT, "Study Option"));
        cd.add(new ColumnDescription("sourcesOfInterest", ValueType.TEXT, "How did you find us"));
        cd.add(new ColumnDescription("sourcesOfInterestText", ValueType.TEXT, "Additional Information"));
        cd.add(new ColumnDescription("provisionalSupervisors", ValueType.TEXT, "Provisional Supervisors"));
        cd.add(new ColumnDescription("academicYear", ValueType.TEXT, "Academic Year"));
        cd.add(new ColumnDescription("submittedDate", ValueType.DATE, "Submitted"));
        cd.add(new ColumnDescription("lastEditedDate", ValueType.DATE, "Last Edited"));
        cd.add(new ColumnDescription("totalFunding", ValueType.TEXT, "Total Funding"));

        // overall rating
        cd.add(new ColumnDescription("averageOverallRating", ValueType.TEXT, "Average Overall Rating"));
        cd.add(new ColumnDescription("overallPositiveEndorsements", ValueType.TEXT, "Overall Positive Endorsements"));

        cd.add(new ColumnDescription("status", ValueType.TEXT, "Status"));
        cd.add(new ColumnDescription("validationTime", ValueType.NUMBER, "Validation Time (hours)"));
        cd.add(new ColumnDescription("feeStatus", ValueType.TEXT, "Fee status"));
        cd.add(new ColumnDescription("academicallyQualified", ValueType.TEXT, "Academically Qualified?"));
        cd.add(new ColumnDescription("adequateEnglish", ValueType.TEXT, "Adequate English?"));

        // reference report
        cd.add(new ColumnDescription("receivedReferences", ValueType.NUMBER, "Received References"));
        cd.add(new ColumnDescription("declinedReferences", ValueType.NUMBER, "Declined References"));
        cd.add(new ColumnDescription("positiveReferenceEndorsements", ValueType.TEXT, "Positive Reference Endorsements"));
        cd.add(new ColumnDescription("negativeReferenceEndorsements", ValueType.TEXT, "Negative Reference Endorsements"));
        cd.add(new ColumnDescription("averageReferenceRating", ValueType.TEXT, "Average Reference Rating"));

        // review report
        cd.add(new ColumnDescription("reviewStages", ValueType.NUMBER, "Review Stages"));
        cd.add(new ColumnDescription("reviewTime", ValueType.NUMBER, "Review Time (hours)"));
        cd.add(new ColumnDescription("positiveReviewEndorsements", ValueType.TEXT, "Positive Review Endorsements"));
        cd.add(new ColumnDescription("negativeReviewEndorsements", ValueType.TEXT, "Negative Review Endorsements"));
        cd.add(new ColumnDescription("averageReviewRating", ValueType.TEXT, "Average Review Rating"));

        // interview report
        cd.add(new ColumnDescription("interviewStages", ValueType.NUMBER, "Interview Stages"));
        cd.add(new ColumnDescription("interviewTime", ValueType.NUMBER, "Interview Time (hours)"));
        cd.add(new ColumnDescription("interviewReports", ValueType.NUMBER, "Interview Reports"));
        cd.add(new ColumnDescription("positiveInterviewEndorsements", ValueType.TEXT, "Positive Interview Endorsements"));
        cd.add(new ColumnDescription("negativeInterviewEndorsements", ValueType.TEXT, "Negative Interview Endorsements"));
        cd.add(new ColumnDescription("averageInterviewRating", ValueType.TEXT, "Average Interview Rating"));

        // approval report
        cd.add(new ColumnDescription("approvalTime", ValueType.NUMBER, "Approval Time (hours)"));
        cd.add(new ColumnDescription("approvalStages", ValueType.NUMBER, "Approval Stages"));
        cd.add(new ColumnDescription("primarySupervisor", ValueType.TEXT, "Primary Supervisor"));
        cd.add(new ColumnDescription("secondarySupervisor", ValueType.TEXT, "Secondary Supervisor"));
        cd.add(new ColumnDescription("outcome", ValueType.TEXT, "Outcome"));
        cd.add(new ColumnDescription("outcomedate", ValueType.DATE, "Outcome Date"));
        cd.add(new ColumnDescription("outcomeType", ValueType.TEXT, "Outcome Type"));
        cd.add(new ColumnDescription("outcomeNote", ValueType.TEXT, "Outcome Note"));

        // link to application
        cd.add(new ColumnDescription("applicationLink", ValueType.TEXT, "Link To Application"));

        dataTable.addColumns(cd);

        TableRow row = new TableRow();
        // row.addCell("app1");
        dataTable.addRow(row);
        return dataTable;
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
        ApplicationReferee referee = applicationDAO.getRefereeByUser(application, comment.getAuthor());
        referee.setComment(comment);
    }

    private void synchroniseOfferRecommendation(Application application, Comment comment) {
        application.setConfirmedStartDate(comment.getPositionProvisionalStartDate());
        application.setConfirmedPrimarySupervisor(Iterables.getFirst(roleService.getRoleUsers(application, PrismRole.APPLICATION_PRIMARY_SUPERVISOR), null));
        application
                .setConfirmedSecondarySupervisor(Iterables.getFirst(roleService.getRoleUsers(application, PrismRole.APPLICATION_SECONDARY_SUPERVISOR), null));
        application.setConfirmedOfferType(comment.getAppointmentConditions() == null ? PrismOfferType.UNCONDITIONAL : PrismOfferType.CONDITIONAL);
    }

    private Application getPreviousApplication(Application application) {
        Application previousApplication = applicationDAO.getPreviousSubmittedApplication(application);
        if (previousApplication == null) {
            previousApplication = applicationDAO.getPreviousUnsubmittedApplication(application);
        }
        return previousApplication;
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

}
