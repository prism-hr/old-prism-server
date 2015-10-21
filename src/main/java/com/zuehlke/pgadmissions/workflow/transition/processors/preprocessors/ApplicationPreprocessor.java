package com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;
import static org.joda.time.DateTimeConstants.MONDAY;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ApplicationPreprocessor implements ResourceProcessor<Application> {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private UserService userService;

    @Override
    public void process(Application resource, Comment comment) {
        if (comment.isApplicationCreatedComment()) {
            setReportingPeriod(resource);
        }

        if (comment.isApplicationCompleteComment()) {
            setSubmissionData(resource);
        }

        if (comment.isApplicationInterviewScheduledConfirmedComment()) {
            appendInterviewScheduledConfirmedComments(resource, comment);
        }
    }

    private void setReportingPeriod(Application application) {
        Institution institution = application.getInstitution();
        DateTime createdTimestamp = application.getCreatedTimestamp();
        Integer applicationYear = createdTimestamp.getYear();
        Integer applicationMonth = createdTimestamp.getMonthOfYear();
        Integer applicationWeek = createdTimestamp.getWeekOfWeekyear();
        application.setApplicationYear(institutionService.getBusinessYear(institution, applicationYear, applicationMonth));
        application.setApplicationMonth(applicationMonth);
        application.setApplicationMonthSequence(createdTimestamp.toLocalDate().withDayOfMonth(1));
        application.setApplicationWeek(applicationWeek);
        application.setApplicationWeekSequence(createdTimestamp.toLocalDate().withDayOfWeek(MONDAY));
    }

    private void setSubmissionData(Application application) {
        application.setSubmittedTimestamp(new DateTime());
        AdvertClosingDate advertClosingDate = application.getAdvert().getClosingDate();
        application.setClosingDate(advertClosingDate == null ? null : advertClosingDate.getClosingDate());
    }

    private void appendInterviewScheduledConfirmedComments(Application application, Comment comment) {
        PrismAction prismAction = APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
        Action action = actionService.getById(prismAction);
        DateTime baseline = comment.getCreatedTimestamp().minusSeconds(1);

        User invoker = comment.getUser();
        List<User> users = userService.getUsersWithAction(application, prismAction, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY);
        LocalDateTime interviewDateTime = comment.getInterviewAppointment().getInterviewDateTime();
        for (User user : users) {
            List<LocalDateTime> preferences = commentService.getAppointmentPreferences(application, user);
            if (!preferences.contains(interviewDateTime)) {
                Comment newPreferenceComment = commentService.createInterviewPreferenceComment(application, action, invoker, user, interviewDateTime, baseline);
                actionService.executeActionSilent(application, action, newPreferenceComment);
            }
        }
    }

}
