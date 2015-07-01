package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewAppointment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

@Component
public class ApplicationInterviewDateResolver implements StateDurationResolver {

	@Override
	public LocalDate resolve(Resource resource, Comment comment) {
		CommentInterviewAppointment interviewAppointment = comment.getInterviewAppointment();
		return interviewAppointment == null ? null : interviewAppointment.getInterviewDateTime().toLocalDate();
	}

}
