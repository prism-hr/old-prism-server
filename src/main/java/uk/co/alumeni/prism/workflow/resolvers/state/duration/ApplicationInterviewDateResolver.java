package uk.co.alumeni.prism.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentInterviewAppointment;

@Component
public class ApplicationInterviewDateResolver implements StateDurationResolver<Application> {

	@Override
	public LocalDate resolve(Application resource, Comment comment) {
		CommentInterviewAppointment interviewAppointment = comment.getInterviewAppointment();
		return interviewAppointment == null ? null : interviewAppointment.getInterviewDateTime().toLocalDate();
	}

}
