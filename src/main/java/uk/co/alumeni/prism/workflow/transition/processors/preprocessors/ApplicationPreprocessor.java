package uk.co.alumeni.prism.workflow.transition.processors.preprocessors;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.InstitutionService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;

import javax.inject.Inject;
import java.util.List;

import static org.joda.time.DateTimeConstants.MONDAY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;

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
        application.setClosingDate(application.getAdvert().getClosingDate());
    }

    private void appendInterviewScheduledConfirmedComments(Application application, Comment comment) {
        if (comment.getInterviewAppointment().getInterviewDateTime() != null) {
            PrismAction prismAction = APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
            Action action = actionService.getById(prismAction);
            DateTime baseline = comment.getCreatedTimestamp().minusSeconds(1);

            User invoker = comment.getUser();
            List<User> users = userService.getUsersWithActions(application, prismAction, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY);
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

}
