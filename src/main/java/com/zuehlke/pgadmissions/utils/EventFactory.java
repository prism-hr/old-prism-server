package com.zuehlke.pgadmissions.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.ApprovalStateChangeEvent;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.ReviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class EventFactory {

	private final UserService userService;

	@Autowired
	public EventFactory(UserService userService) {
		this.userService = userService;
	}

	public Event createEvent(ApplicationFormStatus status) {
		StateChangeEvent event = new StateChangeEvent();
		event.setNewStatus(status);
		event.setUser(userService.getCurrentUser());
		event.setDate(new Date());
		return event;
	}

	public Event createEvent(ReviewRound reviewRound) {
		ReviewStateChangeEvent event = new ReviewStateChangeEvent();
		event.setNewStatus(ApplicationFormStatus.REVIEW);
		event.setUser(userService.getCurrentUser());
		event.setDate(new Date());
		event.setReviewRound(reviewRound);
		return event;
	}

	public Event createEvent(Interview interview) {
		InterviewStateChangeEvent event = new InterviewStateChangeEvent();
		event.setNewStatus(ApplicationFormStatus.INTERVIEW);
		event.setUser(userService.getCurrentUser());
		event.setDate(new Date());
		event.setInterview(interview);
		return event;
	}

	public Event createEvent(ApprovalRound approvalRound) {
		ApprovalStateChangeEvent event = new ApprovalStateChangeEvent();
		event.setNewStatus(ApplicationFormStatus.APPROVAL);
		event.setUser(userService.getCurrentUser());
		event.setDate(new Date());
		event.setApprovalRound(approvalRound);
		return event;
	}

}
