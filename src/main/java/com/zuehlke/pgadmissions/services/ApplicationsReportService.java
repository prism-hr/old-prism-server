package com.zuehlke.pgadmissions.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.utils.MathUtils;

@Service("applicationsReportService")
@Transactional
public class ApplicationsReportService {

    private final ApplicationsService applicationsService;

    public ApplicationsReportService() {
        this(null);
    }

    @Autowired
    public ApplicationsReportService(ApplicationsService applicationsService) {
        this.applicationsService = applicationsService;
    }

    public DataTable getApplicationsReport(RegisteredUser user, ApplicationsFiltering filtering) {
        filtering.setBlockCount(1);

        DataTable data = new DataTable();

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
        cd.add(new ColumnDescription("averageOverallRating", ValueType.TEXT, "Average Overall Rating"));
        cd.add(new ColumnDescription("status", ValueType.TEXT, "Status"));
        cd.add(new ColumnDescription("validationTime", ValueType.NUMBER, "Validation Time (hours)"));
        cd.add(new ColumnDescription("feeStatus", ValueType.TEXT, "Fee status"));
        cd.add(new ColumnDescription("academicallyQualified", ValueType.TEXT, "Academically Qualified?"));
        cd.add(new ColumnDescription("adequateEnglish", ValueType.TEXT, "Adequate English?"));
        cd.add(new ColumnDescription("receivedReferences", ValueType.NUMBER, "Received References"));
        cd.add(new ColumnDescription("declinedReferences", ValueType.NUMBER, "Declined References"));
        
        cd.add(new ColumnDescription("reviewStages", ValueType.NUMBER, "Review Stages"));
        cd.add(new ColumnDescription("reviewTime", ValueType.NUMBER, "Review Time (hours)"));
        cd.add(new ColumnDescription("positiveReviewEndorsements", ValueType.NUMBER, "Positive Review Endorsements"));
        cd.add(new ColumnDescription("negativeReviewEndorsements", ValueType.NUMBER, "Negative Review Endorsements"));
        cd.add(new ColumnDescription("averageReviewRating", ValueType.TEXT, "Average Review Rating"));

        cd.add(new ColumnDescription("interviewStages", ValueType.NUMBER, "Interview Stages"));
        cd.add(new ColumnDescription("interviewTime", ValueType.NUMBER, "Interview Time (hours)"));
        cd.add(new ColumnDescription("interviewReports", ValueType.NUMBER, "Interview Reports"));
        cd.add(new ColumnDescription("positiveInterviewEndorsements", ValueType.NUMBER, "Positive Interview Endorsements"));
        cd.add(new ColumnDescription("negativeInterviewEndorsements", ValueType.NUMBER, "Negative Interview Endorsements"));
        cd.add(new ColumnDescription("averageInterviewRating", ValueType.TEXT, "Average Interview Rating"));

        cd.add(new ColumnDescription("approvalTime", ValueType.NUMBER, "Approval Time (hours)"));
        cd.add(new ColumnDescription("approvalStages", ValueType.NUMBER, "Approval Stages"));
        cd.add(new ColumnDescription("primarySupervisor", ValueType.TEXT, "Primary Supervisor"));
        cd.add(new ColumnDescription("secondarySupervisor", ValueType.TEXT, "Secondary Supervisor"));
        cd.add(new ColumnDescription("outcome", ValueType.TEXT, "Outcome"));
        cd.add(new ColumnDescription("outcomedate", ValueType.DATE, "Outcome Date"));
        cd.add(new ColumnDescription("outcomeType", ValueType.TEXT, "Outcome Type"));
        cd.add(new ColumnDescription("outcomeNote", ValueType.TEXT, "Outcome Note"));
        data.addColumns(cd);
        List<ApplicationForm> applications = new ArrayList<ApplicationForm>();
        do {
            applications = applicationsService.getAllVisibleAndMatchedApplications(user, filtering);
            filtering.setBlockCount(filtering.getBlockCount() + 1);

            // Fill the data table.
            for (ApplicationForm app : applications) {

                if (app.getStatus() == ApplicationFormStatus.UNSUBMITTED) {
                    continue;
                }

                if (app.isPersonalDetailsNull()) {
                    // Quick fix for PRISM-425
                    // Some users managed to submit their applications without
                    // providing their personal details. Namely
                    // RRDCOMSING01-2013-000144
                    // and TMRCOMSVEI01-2013-000158.
                    continue;
                }

                RegisteredUser applicant = app.getApplicant();
                PersonalDetails personalDetails = app.getPersonalDetails();
                String firstNames = Joiner.on(" ").skipNulls().join(applicant.getFirstName(), applicant.getFirstName2(), applicant.getFirstName3());
                Program program = app.getProgram();
                ProgrammeDetails programmeDetails = app.getProgrammeDetails();
                ValidationComment validationComment = getLatestvalidationComment(app);
                int[] receivedAndDeclinedReferences = getNumberOfReceivedAndDeclinedReferences(app);
                int[] reviewEndorsements = getNumberOfPositiveAndNegativeReviewEndorsements(app);
                int[] interviewEndorsements = getNumberOfPositiveAndNegativeInterviewEndorsements(app);

                Date approveDate = getApproveDate(app);
                boolean canSeeRating = user.getId() != applicant.getId();

                TableRow row = new TableRow();

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
                row.addCell(programmeDetails.getStudyOption() != null ? programmeDetails.getStudyOption() : StringUtils.EMPTY);
                row.addCell(programmeDetails.getSourcesOfInterest() != null ? StringUtils.trimToEmpty(programmeDetails.getSourcesOfInterest().getName())
                                : StringUtils.EMPTY);
                row.addCell(StringUtils.trimToEmpty(programmeDetails.getSourcesOfInterestText()));
                row.addCell(getSuggestedSupervisors(programmeDetails));
                row.addCell(getAcademicYear(app));
                row.addCell(app.getSubmittedDate() != null ? getDateValue(app.getSubmittedDate()) : DateValue.getNullValue());
                row.addCell(app.getLastUpdated() != null ? getDateValue(app.getLastUpdated()) : DateValue.getNullValue());
                row.addCell(canSeeRating ? printRating(app.getAverageRatingFormatted()) : "N/R");
                row.addCell(app.getStatus().displayValue());
                row.addCell(new NumberValue(getTimeSpentIn(app, ApplicationFormStatus.VALIDATION)));
                row.addCell(validationComment != null ? validationComment.getHomeOrOverseas().getDisplayValue() : StringUtils.EMPTY);
                row.addCell(validationComment != null ? validationComment.getQualifiedForPhd().getDisplayValue() : StringUtils.EMPTY);
                row.addCell(validationComment != null ? validationComment.getEnglishCompentencyOk().getDisplayValue() : StringUtils.EMPTY);
                row.addCell(new NumberValue(receivedAndDeclinedReferences[0]));
                row.addCell(new NumberValue(receivedAndDeclinedReferences[1]));
                row.addCell(new NumberValue(app.getReviewRounds().size()));
                row.addCell(new NumberValue(getTimeSpentIn(app, ApplicationFormStatus.REVIEW)));
                row.addCell(new NumberValue(reviewEndorsements[0]));
                row.addCell(new NumberValue(reviewEndorsements[1]));
                row.addCell(canSeeRating ? printRating(getAverageRatingForAllReviewRounds(app)) : "N/R");

                row.addCell(new NumberValue(app.getInterviews().size()));
                row.addCell(new NumberValue(getTimeSpentIn(app, ApplicationFormStatus.INTERVIEW)));
                row.addCell(new NumberValue(getNumberOfInterviewReports(app)));
                row.addCell(new NumberValue(interviewEndorsements[0]));
                row.addCell(new NumberValue(interviewEndorsements[1]));
                row.addCell(canSeeRating ? printRating(getAverageRatingForAllInterviewRounds(app)) : "N/R");

                row.addCell(new NumberValue(getTimeSpentIn(app, ApplicationFormStatus.APPROVAL)));
                row.addCell(new NumberValue(app.getApprovalRounds().size()));
                row.addCell(getPrintablePrimarySupervisor(app));
                row.addCell(getPrintableSecondarySupervisor(app));
                row.addCell(app.getStatus() == ApplicationFormStatus.APPROVED ? "Approved" : "Not approved");
                row.addCell(approveDate != null ? getDateValue(approveDate) : DateValue.getNullValue());
                row.addCell(approveDate != null ? getConditionalType(app) : StringUtils.EMPTY);
                row.addCell(approveDate != null ? getOfferConditions(app) : StringUtils.EMPTY);

                try {
                    data.addRow(row);
                } catch (TypeMismatchException e) {
                    throw new RuntimeException(e);
                }
            }
        } while (!applications.isEmpty());
        return data;
    }

    private long getTimeSpentIn(ApplicationForm app, ApplicationFormStatus applicationStatus) {
        List<Event> events = app.getEvents();
        List<StateChangeEvent> stateEvents = Lists.newArrayList(Iterables.filter(events, StateChangeEvent.class));
        Collections.sort(stateEvents);

        Date stageBegin = null;
        long millisSum = 0;

        for (StateChangeEvent event : stateEvents) {
            if (stageBegin != null) {
                Date stageEnd = event.getDate();
                long millisDiff = stageEnd.getTime() - stageBegin.getTime(); // hours
                millisSum += millisDiff;
                stageBegin = null;
            }

            if (event.getNewStatus() == applicationStatus) {
                stageBegin = event.getDate();
            }
        }

        if (stageBegin != null) {
            Date stageEnd = new Date();
            long millisDiff = stageEnd.getTime() - stageBegin.getTime();
            millisSum += millisDiff;
            stageBegin = null;
        }

        return millisSum / 3600000; // convert to hours
    }

    private ValidationComment getLatestvalidationComment(ApplicationForm app) {
        List<Comment> comments = app.getApplicationComments();
        List<ValidationComment> validationComments = Lists.newArrayList(Iterables.filter(comments, ValidationComment.class));
        validationComments = Ordering.natural().reverse().sortedCopy(validationComments);
        return Iterables.getFirst(validationComments, null);

    }

    private String getSuggestedSupervisors(ProgrammeDetails programmeDetails) {
        List<SuggestedSupervisor> supervisors = programmeDetails.getSuggestedSupervisors();
        String supervisorsString = Joiner.on(", ").join(Iterables.transform(supervisors, new Function<SuggestedSupervisor, String>() {
            public String apply(SuggestedSupervisor supervisor) {
                return supervisor.getFirstname() + " " + supervisor.getLastname();
            }
        }));
        return supervisorsString;
    }

    private DateValue getDateValue(Date date) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        return new DateValue(calendar);
    }

    private int[] getNumberOfReceivedAndDeclinedReferences(ApplicationForm app) {
        int[] reicevedAndDeclinedCount = new int[2];
        for (Referee referee : app.getReferees()) {
            if (referee.hasResponded() && referee.getReference() != null) {
                reicevedAndDeclinedCount[0]++;
            } else if (referee.isDeclined()) {
                reicevedAndDeclinedCount[1]++;
            }
        }
        return reicevedAndDeclinedCount;
    }

    private int[] getNumberOfPositiveAndNegativeReviewEndorsements(ApplicationForm app) {
        int[] endorsements = new int[2];
        for (Referee referee : app.getReferees()) {
            if (referee.hasResponded() && referee.getReference() != null) {
                ReferenceComment reference = referee.getReference();
                if (BooleanUtils.isTrue(reference.getSuitableForProgramme())) {
                    endorsements[0]++;
                } else if (BooleanUtils.isFalse(reference.getSuitableForProgramme())) {
                    endorsements[1]++;
                }

                if (BooleanUtils.isTrue(reference.getSuitableForUCL())) {
                    endorsements[0]++;
                } else if (BooleanUtils.isFalse(reference.getSuitableForUCL())) {
                    endorsements[1]++;
                }
            }
        }
        return endorsements;
    }

    private int getNumberOfInterviewReports(ApplicationForm app) {
        Interview interview = app.getLatestInterview();
        if (interview == null) {
            return 0;
        }
        int count = 0;
        for (Interviewer interviewer : interview.getInterviewers()) {
            if (interviewer.getInterviewComment() != null) {
                count++;
            }
        }
        return count;
    }

    private int[] getNumberOfPositiveAndNegativeInterviewEndorsements(ApplicationForm app) {
        int[] endorsements = new int[2];
        Interview interview = app.getLatestInterview();
        if (interview == null) {
            return endorsements;
        }
        for (Interviewer interviewer : interview.getInterviewers()) {
            if (interviewer.getInterviewComment() != null) {
                InterviewComment comment = interviewer.getInterviewComment();
                if (BooleanUtils.isTrue(comment.getSuitableCandidateForProgramme())) {
                    endorsements[0]++;
                } else if (BooleanUtils.isFalse(comment.getSuitableCandidateForProgramme())) {
                    endorsements[1]++;
                }

                if (BooleanUtils.isTrue(comment.getSuitableCandidateForUcl())) {
                    endorsements[0]++;
                } else if (BooleanUtils.isFalse(comment.getSuitableCandidateForUcl())) {
                    endorsements[1]++;
                }
            }
        }
        return endorsements;
    }

    private String getPrintablePrimarySupervisor(ApplicationForm app) {
        ApprovalRound approvalRound = app.getLatestApprovalRound();
        if (approvalRound != null) {
            Supervisor primarySupervisor = approvalRound.getPrimarySupervisor();
            if (primarySupervisor != null) {
                return primarySupervisor.getUser().getDisplayName();
            }
        }
        return StringUtils.EMPTY;
    }

    private String getPrintableSecondarySupervisor(ApplicationForm app) {
        ApprovalRound approvalRound = app.getLatestApprovalRound();
        if (approvalRound != null) {
            Supervisor secondarySupervisor = approvalRound.getSecondarySupervisor();
            if (secondarySupervisor != null) {
                return secondarySupervisor.getUser().getDisplayName();
            }
        }
        return StringUtils.EMPTY;
    }

    private Date getApproveDate(ApplicationForm app) {
        List<Event> events = app.getEvents();
        for (Event event : events) {
            if (event instanceof StateChangeEvent) {
                StateChangeEvent stateChangeEvent = (StateChangeEvent) event;
                if (stateChangeEvent.getNewStatus() == ApplicationFormStatus.APPROVED) {
                    return stateChangeEvent.getDate();
                }
            }
        }
        return null;
    }

    private String getConditionalType(ApplicationForm app) {
        ApprovalRound approvalRound = app.getLatestApprovalRound();
        if (approvalRound != null) {
            if (BooleanUtils.isTrue(approvalRound.getRecommendedConditionsAvailable())) {
                return "Conditional";
            } else if (BooleanUtils.isTrue(approvalRound.getRecommendedConditionsAvailable())) {
                return "Unconditional";
            }
        }
        return StringUtils.EMPTY;
    }

    private String getOfferConditions(ApplicationForm app) {
        ApprovalRound approvalRound = app.getLatestApprovalRound();
        if (approvalRound != null) {
            if (approvalRound.getRecommendedConditions() != null) {
                return approvalRound.getRecommendedConditions();
            }
        }
        return StringUtils.EMPTY;
    }

    private String getProjectTitle(ApplicationForm app) {
        ApprovalRound approvalRound = app.getLatestApprovalRound();
        if (approvalRound != null) {
            if (approvalRound.getProjectTitle() != null) {
                return approvalRound.getProjectTitle();
            }
        }
        return StringUtils.EMPTY;
    }

    private String getAcademicYear(ApplicationForm app) {
        Date startDate = app.getProgrammeDetails().getStartDate();
        if (startDate != null) {
            for (ProgramInstance instance : app.getProgram().getInstances()) {
                if (instance.isDateWithinBounds(startDate)) {
                    return instance.getAcademic_year();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    private String getAverageRatingForAllInterviewRounds(ApplicationForm app) {
        List<Interview> interviews = app.getInterviews();
        if (interviews.isEmpty()) {
            return null;
        }
        BigDecimal ratingTotal = new BigDecimal(0);
        for (Interview interview : interviews) {
            BigDecimal averageRating = interview.getAverageRating();
            if (averageRating != null) {
                ratingTotal = ratingTotal.add(interview.getAverageRating());
            }
        }
        return MathUtils.formatRating(new BigDecimal(ratingTotal.doubleValue() / interviews.size()));
    }

    private String getAverageRatingForAllReviewRounds(ApplicationForm app) {
        List<ReviewRound> reviewRounds = app.getReviewRounds();
        if (reviewRounds.isEmpty()) {
            return null;
        }
        BigDecimal ratingTotal = new BigDecimal(0);
        for (ReviewRound reviewRound : reviewRounds) {
            BigDecimal averageRating = reviewRound.getAverageRating();
            if (averageRating != null) {
                ratingTotal = ratingTotal.add(averageRating);
            }
        }
        return MathUtils.formatRating(new BigDecimal(ratingTotal.doubleValue() / reviewRounds.size()));
    }

    private String printRating(String rating) {
        return rating == null ? "0" : rating;
    }
}
