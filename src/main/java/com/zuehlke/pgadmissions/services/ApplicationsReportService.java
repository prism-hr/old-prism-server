package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import javassist.bytecode.stackmap.TypeData.ClassName;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateTimeValue;
import com.google.visualization.datasource.datatable.value.DateValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationFilterGroup;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
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
                User applicant = app.getUser();
                PersonalDetails personalDetails = app.getPersonalDetails();
                String firstNames = Joiner.on(" ").skipNulls().join(applicant.getFirstName(), applicant.getFirstName2(), applicant.getFirstName3());
                Program program = app.getProgram();
                ProgramDetails programmeDetails = app.getProgramDetails();
                ValidationComment validationComment = (ValidationComment) applicationsService.getLatestStateChangeComment(app, SystemAction.APPLICATION_COMPLETE_VALIDATION_STAGE);
                int[] receivedAndDeclinedReferences = getNumberOfReceivedAndDeclinedReferences(app);
                int[] referenceEndorsements = getNumberOfPositiveAndNegativeReferenceEndorsements(app);
                int[] reviewEndorsements = getNumberOfPositiveAndNegativeReviewEndorsements(app);
                int[] interviewEndorsements = getNumberOfPositiveAndNegativeInterviewEndorsements(app);

                int overallPositiveEndorsements = referenceEndorsements[0] + reviewEndorsements[0] + interviewEndorsements[0];

                LocalDate approveDate = getApproveDate(app);
                boolean canSeeRating = user.getId() != applicant.getId();

                TableRow row = new TableRow();

                if (reportType == ReportFormat.SHORT) {
                    row.addCell(app.getApplicationNumber());
                    row.addCell(firstNames);
                    row.addCell(applicant.getLastName());
                    row.addCell(program.getTitle());
                    row.addCell(getProjectTitle(app));
                    row.addCell(getSuggestedSupervisors(programmeDetails));
                    row.addCell(getAcademicYear(app));
                    row.addCell(app.getSubmittedTimestamp() != null ? getDateTimeValue(app.getSubmittedTimestamp()) : DateValue.getNullValue());
                    row.addCell(getFundingTotal(app));

                    // overall rating
//                    row.addCell(canSeeRating ? printRating(app.getAverageRatingFormatted()) : N_R);
                    row.addCell(app.getState().getId().name());

                    // reference report
                    row.addCell(receivedAndDeclinedReferences[0]);
                    row.addCell(canSeeRating ? printRating(getAverageReferenceRating(app)) : N_R);

                    // review report
//                    row.addCell(new NumberValue(app.getReviewRounds().size()));
                    row.addCell(canSeeRating ? String.valueOf(reviewEndorsements[0]) : N_R);
                    row.addCell(canSeeRating ? String.valueOf(reviewEndorsements[1]) : N_R);
                    row.addCell(canSeeRating ? printRating(getAverageRatingForAllReviewRounds(app)) : N_R);

                    // link to application
                    row.addCell(getApplicationLink(app));
                } else {
                    row.addCell(app.getApplicationNumber());
                    row.addCell(firstNames);
                    row.addCell(applicant.getLastName());
                    row.addCell(applicant.getEmail());
                    row.addCell(personalDetails.getFirstNationality() != null ? personalDetails.getFirstNationality().getName() : StringUtils.EMPTY);
                    row.addCell(personalDetails.getSecondNationality() != null ? personalDetails.getSecondNationality().getName() : StringUtils.EMPTY);
                    row.addCell(getDateValue(personalDetails.getDateOfBirth()));
                    row.addCell(personalDetails.getGender().getDisplayValue());
                    row.addCell(program.getCode());
                    row.addCell(program.getTitle());
                    row.addCell(getProjectTitle(app));
                    row.addCell(programmeDetails.getStudyOption() != null ? programmeDetails.getStudyOption().getDisplayName() : StringUtils.EMPTY);
                    row.addCell(programmeDetails.getSourceOfInterest() != null ? StringUtils.trimToEmpty(programmeDetails.getSourceOfInterest().getName()) : StringUtils.EMPTY);
                    row.addCell(StringUtils.trimToEmpty(programmeDetails.getSourceOfInterestText()));
                    row.addCell(getSuggestedSupervisors(programmeDetails));
                    row.addCell(getAcademicYear(app));
                    row.addCell(app.getSubmittedTimestamp() != null ? getDateTimeValue(app.getSubmittedTimestamp()) : DateValue.getNullValue());
//                    row.addCell(app.getUpdateTimestamp() != null ? getDateValue(app.getUpdateTimestamp()) : DateValue.getNullValue());
                    row.addCell(getFundingTotal(app));

                    // overall rating
//                    row.addCell(canSeeRating ? printRating(app.getAverageRatingFormatted()) : N_R);
                    row.addCell(canSeeRating ? String.valueOf(overallPositiveEndorsements) : N_R);

                    row.addCell(app.getState().getId().name());
                    row.addCell(new NumberValue(getTimeSpentIn(app, PrismState.APPLICATION_VALIDATION)));
                    row.addCell(validationComment != null ? validationComment.getHomeOrOverseas().getDisplayValue() : StringUtils.EMPTY);
                    row.addCell(validationComment != null ? validationComment.getQualifiedForPhd().getDisplayValue() : StringUtils.EMPTY);
                    row.addCell(validationComment != null ? validationComment.getEnglishCompetencyOk().getDisplayValue() : StringUtils.EMPTY);

                    // reference report
                    row.addCell(receivedAndDeclinedReferences[0]);
                    row.addCell(receivedAndDeclinedReferences[1]);
                    row.addCell(canSeeRating ? String.valueOf(referenceEndorsements[0]) : N_R);
                    row.addCell(canSeeRating ? String.valueOf(referenceEndorsements[1]) : N_R);
                    row.addCell(canSeeRating ? printRating(getAverageReferenceRating(app)) : N_R);

                    // review report
//                    row.addCell(new NumberValue(app.getReviewRounds().size()));
                    row.addCell(new NumberValue(getTimeSpentIn(app, PrismState.APPLICATION_REVIEW)));
                    row.addCell(canSeeRating ? String.valueOf(reviewEndorsements[0]) : N_R);
                    row.addCell(canSeeRating ? String.valueOf(reviewEndorsements[1]) : N_R);
                    row.addCell(canSeeRating ? printRating(getAverageRatingForAllReviewRounds(app)) : N_R);

                    // interview report
                    row.addCell(new NumberValue(666));
                    row.addCell(new NumberValue(getTimeSpentIn(app, PrismState.APPLICATION_INTERVIEW)));
                    row.addCell(new NumberValue(getNumberOfInterviewReports(app)));
                    row.addCell(canSeeRating ? String.valueOf(interviewEndorsements[0]) : N_R);
                    row.addCell(canSeeRating ? String.valueOf(interviewEndorsements[1]) : N_R);
                    row.addCell(canSeeRating ? printRating(getAverageRatingForAllInterviewRounds(app)) : N_R);

                    // approval report
                    row.addCell(new NumberValue(getTimeSpentIn(app, PrismState.APPLICATION_APPROVAL)));
//                    row.addCell(new NumberValue(app.getApprovalRounds().size()));
                    row.addCell(getPrintablePrimarySupervisor(app));
                    row.addCell(getPrintableSecondarySupervisor(app));
                    row.addCell(app.getState().getId() == PrismState.APPLICATION_APPROVED ? "Approved" : "Not approved");
                    row.addCell(approveDate != null ? getDateValue(approveDate) : DateValue.getNullValue());
                    row.addCell(approveDate != null ? getConditionalType(app) : StringUtils.EMPTY);
                    row.addCell(approveDate != null ? getOfferConditions(app) : StringUtils.EMPTY);

                    // link to application
                    row.addCell(getApplicationLink(app));

                }

                try {
                    data.addRow(row);
                } catch (TypeMismatchException e) {
                    throw new RuntimeException(e);
                }

            } catch (NullPointerException e) {
                logger.info("User tried to download spreadsheet report for corrupted application: " + app.getApplicationNumber() + ".", e);
                continue;
            }
        }

        return data;
    }



    private String getApplicationLink(Application app) {
        String applicationLink = host + "/pgadmissions/application?view=view&applicationId=" + app.getApplicationNumber();
        return applicationLink;
    }

    private long getTimeSpentIn(Application app, PrismState applicationStatus) {
        return 666; // FIXME
    }

    private String getSuggestedSupervisors(ProgramDetails programmeDetails) {
        List<SuggestedSupervisor> supervisors = programmeDetails.getSuggestedSupervisors();
        String supervisorsString = Joiner.on(", ").join(Iterables.transform(supervisors, new Function<SuggestedSupervisor, String>() {
            public String apply(SuggestedSupervisor supervisor) {
                return supervisor.getUser().getFirstName() + " " + supervisor.getUser().getLastName();
            }
        }));
        return supervisorsString;
    }

    private DateValue getDateValue(LocalDate date) {
        if (date == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(date.toDateTimeAtStartOfDay().getMillis());
        return new DateValue(calendar);
    }
    
    private DateTimeValue getDateTimeValue(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(dateTime.getMillis());
        return new DateTimeValue(calendar);
    }

    private int[] getNumberOfReceivedAndDeclinedReferences(Application app) {
        int[] reicevedAndDeclinedCount = new int[2];
        for (Referee referee : app.getReferees()) {
            if (referee.getComment() != null) {
                reicevedAndDeclinedCount[0]++;
            } else if (referee.isDeclined()) {
                reicevedAndDeclinedCount[1]++;
            }
        }
        return reicevedAndDeclinedCount;
    }

    private int[] getNumberOfPositiveAndNegativeReferenceEndorsements(Application app) {
        int[] endorsements = new int[2];
        for (Referee referee : app.getReferees()) {
            if (referee.getComment() != null) {
                Comment reference = referee.getComment();
                if (BooleanUtils.isTrue(reference.getSuitableForProgramme())) {
                    endorsements[0]++;
                } else if (BooleanUtils.isFalse(reference.getSuitableForProgramme())) {
                    endorsements[1]++;
                }

                if (BooleanUtils.isTrue(reference.getSuitableForInstitution())) {
                    endorsements[0]++;
                } else if (BooleanUtils.isFalse(reference.getSuitableForInstitution())) {
                    endorsements[1]++;
                }
            }
        }
        return endorsements;
    }

    private int[] getNumberOfPositiveAndNegativeReviewEndorsements(Application app) {
        int[] endorsements = new int[2];
        // FIXME
//        ReviewRound review = app.getLatestReviewRound();
//        if (review == null) {
//            return endorsements;
//        }
//        for (Reviewer reviewer : review.getReviewers()) {
//            if (reviewer.getReview() != null) {
//                ReviewComment comment = reviewer.getReview();
//                Boolean[] answers = new Boolean[4];
//                answers[0] = comment.getSuitableForProgramme();
//                answers[1] = comment.getSuitableForInstitution();
//                answers[2] = comment.getWillingToInterview();
//                answers[3] = comment.getWillingToSupervise();
//
//                for (Boolean answer : answers) {
//                    if (BooleanUtils.isTrue(answer)) {
//                        endorsements[0]++;
//                    } else if (BooleanUtils.isFalse(answer)) {
//                        endorsements[1]++;
//                    }
//                }
//            }
//        }
        return endorsements;
    }

    private int getNumberOfInterviewReports(Application app) {
//        Interview interview = app.getLatestInterview();
//        if (interview == null) {
//            return 0;
//        }
//        int count = 0;
//        for (Interviewer interviewer : interview.getInterviewers()) {
//            if (interviewer.getInterviewComment() != null) {
//                count++;
//            }
//        }
//        return count;
        return 666;
    }

    private int[] getNumberOfPositiveAndNegativeInterviewEndorsements(Application app) {
//        int[] endorsements = new int[2];
//        Interview interview = app.getLatestInterview();
//        if (interview == null) {
//            return endorsements;
//        }
//        for (Interviewer interviewer : interview.getInterviewers()) {
//            if (interviewer.getInterviewComment() != null) {
//                InterviewComment comment = interviewer.getInterviewComment();
//                if (BooleanUtils.isTrue(comment.getSuitableForProgramme())) {
//                    endorsements[0]++;
//                } else if (BooleanUtils.isFalse(comment.getSuitableForProgramme())) {
//                    endorsements[1]++;
//                }
//
//                if (BooleanUtils.isTrue(comment.getSuitableForInstitution())) {
//                    endorsements[0]++;
//                } else if (BooleanUtils.isFalse(comment.getSuitableForInstitution())) {
//                    endorsements[1]++;
//                }
//            }
//        }
//        return endorsements;
        return new int[]{666, 666};
    }

    private String getPrintablePrimarySupervisor(Application app) {
//        ApprovalRound approvalRound = app.getLatestApprovalRound();
//        if (approvalRound != null) {
//            Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
//            if (primarySupervisor != null) {
//                return primarySupervisor.getUser().getDisplayName();
//            }
//        }
        return StringUtils.EMPTY;
    }

    private String getPrintableSecondarySupervisor(Application app) {
//        ApprovalRound approvalRound = app.getLatestApprovalRound();
//        if (approvalRound != null) {
//            Supervisor secondarySupervisor = approvalRound.getSecondarySupervisor();
//            if (secondarySupervisor != null) {
//                return secondarySupervisor.getUser().getDisplayName();
//            }
//        }
        return StringUtils.EMPTY;
    }

    private LocalDate getApproveDate(Application app) {
//        List<Event> events = app.getEvents();
//        for (Event event : events) {
//            if (event instanceof StateChangeEvent) {
//                StateChangeEvent stateChangeEvent = (StateChangeEvent) event;
//                if (stateChangeEvent.getNewStatus() == ApplicationFormStatus.APPROVED) {
//                    return stateChangeEvent.getDate();
//                }
//            }
//        }
        return null;
    }

    private String getConditionalType(Application app) {
//        ApprovalRound approvalRound = app.getLatestApprovalRound();
//        if (approvalRound != null) {
//            if (BooleanUtils.isTrue(approvalRound.getRecommendedConditionsAvailable())) {
//                return "Conditional";
//            } else if (BooleanUtils.isTrue(approvalRound.getRecommendedConditionsAvailable())) {
//                return "Unconditional";
//            }
//        }
        return StringUtils.EMPTY;
    }

    private String getOfferConditions(Application app) {
//        ApprovalRound approvalRound = app.getLatestApprovalRound();
//        if (approvalRound != null) {
//            if (approvalRound.getRecommendedConditions() != null) {
//                return approvalRound.getRecommendedConditions();
//            }
//        }
        return StringUtils.EMPTY;
    }

    private String getProjectTitle(Application app) {
//        ApprovalRound approvalRound = app.getLatestApprovalRound();
//        if (approvalRound != null) {
//            if (approvalRound.getProjectTitle() != null) {
//                return approvalRound.getProjectTitle();
//            }
//        }
        return StringUtils.EMPTY;
    }

    private String getAcademicYear(Application app) {
        LocalDate startDate = app.getProgramDetails().getStartDate();
        if (startDate != null) {
            for (ProgramInstance instance : app.getProgram().getInstances()) {
                if (instance.isDateWithinBounds(startDate)) {
                    return instance.getAcademicYear();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    private String getAverageRatingForAllInterviewRounds(Application app) {
//        List<Interview> interviews = app.getInterviews();
//        if (interviews.isEmpty()) {
//            return null;
//        }
//        BigDecimal ratingTotal = new BigDecimal(0);
//        for (Interview interview : interviews) {
//            BigDecimal averageRating = interview.getAverageRating();
//            if (averageRating != null) {
//                ratingTotal = ratingTotal.add(interview.getAverageRating());
//            }
//        }
//        return MathUtils.formatRating(new BigDecimal(ratingTotal.doubleValue() / interviews.size()));
        return "666";
    }

    private String getFundingTotal(Application app) {
        List<Funding> funding = app.getFundings();
        Integer totalFunding = 0;
        for (Funding temp : funding) {
            totalFunding = totalFunding + temp.getValueAsInteger();
        }
        return totalFunding.toString();
    }

    private String getAverageReferenceRating(Application app) {
//        BigDecimal referenceRating = applicantRatingService.getAverageReferenceRating(app);
//        return MathUtils.formatRating(referenceRating);
        return "666";
    }

    private String getAverageRatingForAllReviewRounds(Application app) {
//        List<ReviewRound> reviewRounds = app.getReviewRounds();
//        if (reviewRounds.isEmpty()) {
//            return null;
//        }
//        BigDecimal ratingTotal = new BigDecimal(0);
//        for (ReviewRound reviewRound : reviewRounds) {
//            BigDecimal averageRating = reviewRound.getAverageRating();
//            if (averageRating != null) {
//                ratingTotal = ratingTotal.add(averageRating);
//            }
//        }
//        return MathUtils.formatRating(new BigDecimal(ratingTotal.doubleValue() / reviewRounds.size()));
        return "666";
    }

    private String printRating(String rating) {
        return rating == null ? N_R : rating;
    }
}