package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import javassist.bytecode.stackmap.TypeData.ClassName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationFilterGroup;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ReportFormat;

@Service("applicationsReportService")
@Transactional
public class ApplicationsReportService {

    private static final String N_R = "N/R";

    private static Logger logger = LoggerFactory.getLogger(ClassName.class.getName());

    @Autowired
    private ApplicationService applicationsService;

    @Value("${application.host}") 
    private String host;

    public DataTable getApplicationsReport(User user, ApplicationFilterGroup filtering, ReportFormat reportType) {
        // TODO implement report functionality (supposedly using query and write new tests)
        DataTable data = new DataTable();

        ArrayList<ColumnDescription> cd = Lists.newArrayList();

        if (reportType == ReportFormat.SHORT) {
            cd.add(new ColumnDescription("applicationId", ValueType.TEXT, "Application ID"));
            cd.add(new ColumnDescription("firstNames", ValueType.TEXT, "First Name(s)"));
            cd.add(new ColumnDescription("lastName", ValueType.TEXT, "Last Name"));
            cd.add(new ColumnDescription("programmeName", ValueType.TEXT, "Programme Name"));
            cd.add(new ColumnDescription("projectTitle", ValueType.TEXT, "Project Title"));
            cd.add(new ColumnDescription("provisionalSupervisors", ValueType.TEXT, "Provisional Supervisors"));
            cd.add(new ColumnDescription("academicYear", ValueType.TEXT, "Academic Year"));
            cd.add(new ColumnDescription("submittedDate", ValueType.DATE, "Submitted"));
            cd.add(new ColumnDescription("totalFunding", ValueType.TEXT, "Total Funding"));

            // overall rating
            cd.add(new ColumnDescription("averageOverallRating", ValueType.TEXT, "Average Overall Rating"));

            cd.add(new ColumnDescription("status", ValueType.TEXT, "Status"));

            // reference report
            cd.add(new ColumnDescription("receivedReferences", ValueType.NUMBER, "Received References"));
            cd.add(new ColumnDescription("averageReferenceRating", ValueType.TEXT, "Average Reference Rating"));

            // review report
            cd.add(new ColumnDescription("reviewStages", ValueType.NUMBER, "Review Stages"));
            cd.add(new ColumnDescription("positiveReviewEndorsements", ValueType.TEXT, "Positive Review Endorsements"));
            cd.add(new ColumnDescription("negativeReviewEndorsements", ValueType.TEXT, "Negative Review Endorsements"));
            cd.add(new ColumnDescription("averageReviewRating", ValueType.TEXT, "Average Review Rating"));

            // link to application
            cd.add(new ColumnDescription("applicationLink", ValueType.TEXT, "Link To Application"));
        } else {
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
        }

        data.addColumns(cd);

        List<Application> applications = applicationsService.getApplicationsForReport(user, filtering, reportType);

        for (Application app : applications) {

            if (app.getSubmittedTimestamp() == null || app.getPersonalDetails() == null) {
                continue;
            }

            try {
//                User applicant = app.getUser();
//                PersonalDetails personalDetails = app.getPersonalDetails();
//                String firstNames = Joiner.on(" ").skipNulls().join(applicant.getFirstName(), applicant.getFirstName2(), applicant.getFirstName3());
//                Program program = app.getProgram();
//                ProgramDetails programmeDetails = app.getProgramDetails();
//                ValidationComment validationComment = (ValidationComment) applicationsService.getLatestStateChangeComment(app, SystemAction.APPLICATION_COMPLETE_VALIDATION_STAGE);
//                int[] receivedAndDeclinedReferences = getNumberOfReceivedAndDeclinedReferences(app);
//                int[] referenceEndorsements = getNumberOfPositiveAndNegativeReferenceEndorsements(app);
//                int[] reviewEndorsements = getNumberOfPositiveAndNegativeReviewEndorsements(app);
//                int[] interviewEndorsements = getNumberOfPositiveAndNegativeInterviewEndorsements(app);
//
//                int overallPositiveEndorsements = referenceEndorsements[0] + reviewEndorsements[0] + interviewEndorsements[0];
//
//                LocalDate approveDate = getApproveDate(app);
//                boolean canSeeRating = user.getId() != applicant.getId();

                TableRow row = new TableRow();

                if (reportType == ReportFormat.SHORT) {
//                    row.addCell(app.getApplicationNumber());
//                    row.addCell(firstNames);
//                    row.addCell(applicant.getLastName());
//                    row.addCell(program.getTitle());
//                    row.addCell(getProjectTitle(app));
//                    row.addCell(getSuggestedSupervisors(programmeDetails));
//                    row.addCell(getAcademicYear(app));
//                    row.addCell(app.getSubmittedTimestamp() != null ? getDateTimeValue(app.getSubmittedTimestamp()) : DateValue.getNullValue());
//                    row.addCell(getFundingTotal(app));
//
//                    // overall rating
//                    row.addCell(canSeeRating ? printRating(app.getAverageRatingFormatted()) : N_R);
//                    row.addCell(app.getState().getId().name());
//
//                    // reference report
//                    row.addCell(receivedAndDeclinedReferences[0]);
//                    row.addCell(canSeeRating ? printRating(getAverageReferenceRating(app)) : N_R);
//
//                    // review report
//                    row.addCell(new NumberValue(app.getReviewRounds().size()));
//                    row.addCell(canSeeRating ? String.valueOf(reviewEndorsements[0]) : N_R);
//                    row.addCell(canSeeRating ? String.valueOf(reviewEndorsements[1]) : N_R);
//                    row.addCell(canSeeRating ? printRating(getAverageRatingForAllReviewRounds(app)) : N_R);
//
//                    // link to application
//                    row.addCell(getApplicationLink(app));
                } else {
//                    row.addCell(app.getApplicationNumber());
//                    row.addCell(firstNames);
//                    row.addCell(applicant.getLastName());
//                    row.addCell(applicant.getEmail());
//                    row.addCell(personalDetails.getFirstNationality() != null ? personalDetails.getFirstNationality().getName() : StringUtils.EMPTY);
//                    row.addCell(personalDetails.getSecondNationality() != null ? personalDetails.getSecondNationality().getName() : StringUtils.EMPTY);
//                    row.addCell(getDateValue(personalDetails.getDateOfBirth()));
//                    row.addCell(personalDetails.getGender().getDisplayValue());
//                    row.addCell(program.getCode());
//                    row.addCell(program.getTitle());
//                    row.addCell(getProjectTitle(app));
//                    row.addCell(programmeDetails.getStudyOption() != null ? programmeDetails.getStudyOption().getDisplayName() : StringUtils.EMPTY);
//                    row.addCell(programmeDetails.getSourceOfInterest() != null ? StringUtils.trimToEmpty(programmeDetails.getSourceOfInterest().getName()) : StringUtils.EMPTY);
//                    row.addCell(StringUtils.trimToEmpty(programmeDetails.getSourceOfInterestText()));
//                    row.addCell(getSuggestedSupervisors(programmeDetails));
//                    row.addCell(getAcademicYear(app));
//                    row.addCell(app.getSubmittedTimestamp() != null ? getDateTimeValue(app.getSubmittedTimestamp()) : DateValue.getNullValue());
//                    row.addCell(app.getUpdateTimestamp() != null ? getDateValue(app.getUpdateTimestamp()) : DateValue.getNullValue());
//                    row.addCell(getFundingTotal(app));
//
//                    // overall rating
//                    row.addCell(canSeeRating ? printRating(app.getAverageRatingFormatted()) : N_R);
//                    row.addCell(canSeeRating ? String.valueOf(overallPositiveEndorsements) : N_R);
//
//                    row.addCell(app.getState().getId().name());
//                    row.addCell(new NumberValue(getTimeSpentIn(app, PrismState.APPLICATION_VALIDATION)));
//                    row.addCell(validationComment != null ? validationComment.getResidenceStatus().getDisplayValue() : StringUtils.EMPTY);
//                    row.addCell(validationComment != null ? validationComment.getQualified().getDisplayValue() : StringUtils.EMPTY);
//                    row.addCell(validationComment != null ? validationComment.getCompetentInWorkLanguage().getDisplayValue() : StringUtils.EMPTY);
//
//                    // reference report
//                    row.addCell(receivedAndDeclinedReferences[0]);
//                    row.addCell(receivedAndDeclinedReferences[1]);
//                    row.addCell(canSeeRating ? String.valueOf(referenceEndorsements[0]) : N_R);
//                    row.addCell(canSeeRating ? String.valueOf(referenceEndorsements[1]) : N_R);
//                    row.addCell(canSeeRating ? printRating(getAverageReferenceRating(app)) : N_R);
//
//                    // review report
//                    row.addCell(new NumberValue(app.getReviewRounds().size()));
//                    row.addCell(new NumberValue(getTimeSpentIn(app, PrismState.APPLICATION_REVIEW)));
//                    row.addCell(canSeeRating ? String.valueOf(reviewEndorsements[0]) : N_R);
//                    row.addCell(canSeeRating ? String.valueOf(reviewEndorsements[1]) : N_R);
//                    row.addCell(canSeeRating ? printRating(getAverageRatingForAllReviewRounds(app)) : N_R);
//
//                    // interview report
//                    row.addCell(new NumberValue(666));
//                    row.addCell(new NumberValue(getTimeSpentIn(app, PrismState.APPLICATION_INTERVIEW)));
//                    row.addCell(new NumberValue(getNumberOfInterviewReports(app)));
//                    row.addCell(canSeeRating ? String.valueOf(interviewEndorsements[0]) : N_R);
//                    row.addCell(canSeeRating ? String.valueOf(interviewEndorsements[1]) : N_R);
//                    row.addCell(canSeeRating ? printRating(getAverageRatingForAllInterviewRounds(app)) : N_R);
//
//                    // approval report
//                    row.addCell(new NumberValue(getTimeSpentIn(app, PrismState.APPLICATION_APPROVAL)));
//                    row.addCell(new NumberValue(app.getApprovalRounds().size()));
//                    row.addCell(getPrintablePrimarySupervisor(app));
//                    row.addCell(getPrintableSecondarySupervisor(app));
//                    row.addCell(app.getState().getId() == PrismState.APPLICATION_APPROVED ? "Approved" : "Not approved");
//                    row.addCell(approveDate != null ? getDateValue(approveDate) : DateValue.getNullValue());
//                    row.addCell(approveDate != null ? getConditionalType(app) : StringUtils.EMPTY);
//                    row.addCell(approveDate != null ? getOfferConditions(app) : StringUtils.EMPTY);
//
//                    // link to application
//                    row.addCell(getApplicationLink(app));

                }

                try {
                    data.addRow(row);
                } catch (TypeMismatchException e) {
                    throw new RuntimeException(e);
                }

            } catch (NullPointerException e) {
                logger.info("User tried to download spreadsheet report for corrupted application: " + app.getCode() + ".", e);
                continue;
            }
        }

        return data;
    }



}