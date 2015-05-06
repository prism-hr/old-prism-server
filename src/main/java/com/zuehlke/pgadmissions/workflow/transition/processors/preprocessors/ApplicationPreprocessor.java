package com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ApplicationPreprocessor implements ResourceProcessor {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private UserService userService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Application application = (Application) resource;
        if (comment.isApplicationCreatedComment()) {
            setReportingPeriod(application);
        }

        if (comment.isApplicationSubmittedComment()) {
            setSubmissionData(application);
        }

        if (comment.isApplicationInterviewScheduledConfirmedComment()) {
            appendInterviewScheduledConfirmedComments(application, comment);
        }
    }

    private void setReportingPeriod(Application application) {
        Institution institution = application.getInstitution();
        DateTime createdTimestamp = application.getCreatedTimestamp();
        Integer applicationYear = createdTimestamp.getYear();
        Integer applicationMonth = createdTimestamp.getMonthOfYear();
        application.setApplicationYear(institutionService.getBusinessYear(institution, applicationYear, applicationMonth));
        application.setApplicationMonth(applicationMonth);
        application.setApplicationMonthSequence(institutionService.getMonthOfBusinessYear(institution, applicationMonth));
    }

    private void setSubmissionData(Application application) {
        application.setSubmittedTimestamp(new DateTime());
        AdvertClosingDate advertClosingDate = application.getAdvert().getClosingDate();
        application.setClosingDate(advertClosingDate == null ? null : advertClosingDate.getClosingDate());
    }

    private void appendInterviewScheduledConfirmedComments(Application application, Comment comment) throws Exception {
        Resource resource = comment.getResource();
        PrismAction prismAction = APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
        Action action = actionService.getById(prismAction);
        DateTime baseline = comment.getCreatedTimestamp().minusSeconds(1);

        User invoker = comment.getUser();
        List<User> users = userService.getUsersWithAction(resource, prismAction, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY);
        LocalDateTime interviewDateTime = comment.getInterviewAppointment().getInterviewDateTime();
        for (User user : users) {
            List<LocalDateTime> preferences = commentService.getAppointmentPreferences(application, user);
            if (!preferences.contains(interviewDateTime)) {
                Comment newPreferenceComment = commentService.createInterviewPreferenceComment(resource, action, invoker, user, interviewDateTime, baseline);
                actionService.executeActionSilent(resource, action, newPreferenceComment);
            }
        }
    }

}