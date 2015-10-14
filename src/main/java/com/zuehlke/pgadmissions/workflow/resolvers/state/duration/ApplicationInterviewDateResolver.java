package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewAppointment;

@Component
public class ApplicationInterviewDateResolver implements StateDurationResolver<Application> {

	@Override
	public LocalDate resolve(Application resource, Comment comment) {
		CommentInterviewAppointment interviewAppointment = comment.getInterviewAppointment();
		return interviewAppointment == null ? null : interviewAppointment.getInterviewDateTime().toLocalDate();
	}

}
