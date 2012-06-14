package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalStateChangeEvent;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.InterviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.ReferenceEvent;
import com.zuehlke.pgadmissions.domain.ReviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.TimelineObject;
import com.zuehlke.pgadmissions.dto.TimelinePhase;
import com.zuehlke.pgadmissions.dto.TimelineReference;

@Service
public class TimelineService {

	private final UserService userService;

	TimelineService() {
		this(null);
	}

	@Autowired
	public TimelineService(UserService userService) {
		this.userService = userService;

	}

	private void addCommentsToCorrectPhase(ApplicationForm applicationForm, List<TimelinePhase> phases) {
		List<Comment> visibleComments = applicationForm.getVisibleComments(userService.getCurrentUser());
		for (Comment comment : visibleComments) {
			for (TimelinePhase phase : phases) {
				if (comment.getDate().after(phase.getEventDate())
						&& (phase.getExitedPhaseDate() == null || comment.getDate().before(phase.getExitedPhaseDate()))) {
					phase.getComments().add(comment);
				}
			}
		}
	}

	private TimelinePhase createTimelinePhaseForEvent(Event event, ApplicationForm applicationForm) {

		TimelinePhase phase = new TimelinePhase();
		phase.setEventDate(event.getDate());
		phase.setStatus(((StateChangeEvent) event).getNewStatus());
		phase.setAuthor(event.getUser());
		if (event instanceof ReviewStateChangeEvent) {
			phase.setReviewRound(((ReviewStateChangeEvent) event).getReviewRound());
		} else if (event instanceof InterviewStateChangeEvent) {
			phase.setInterview(((InterviewStateChangeEvent) event).getInterview());
		} else if (event instanceof ApprovalStateChangeEvent) {
			phase.setApprovalRound(((ApprovalStateChangeEvent) event).getApprovalRound());
		}
		if (phase.getStatus() == ApplicationFormStatus.REJECTED && event.getUser().isInRoleInProgram(Authority.APPROVER, applicationForm.getProgram())) {
			phase.setRejectedByApprover(true);
		}
		return phase;
	}

	private void setExitDates(List<TimelinePhase> phases) {
		Collections.sort(phases);
		for (int i = phases.size() - 1; i > 0; i--) {
			TimelinePhase thisPhase = phases.get(i);
			thisPhase.setExitedPhaseDate(phases.get(i - 1).getEventDate());
		}
	}

	public List<TimelineObject> getTimelineObjects(ApplicationForm applicationForm) {
		List<TimelineObject> timelineObjects = new ArrayList<TimelineObject>();
		List<TimelinePhase> phases = new ArrayList<TimelinePhase>();
		if (userService.getCurrentUser().equals(applicationForm.getApplicant())) {
			phases.add(createUnsubmittedPhase(applicationForm));
		}
		List<Event> events = applicationForm.getEvents();

		for (Event event : events) {
			if (event instanceof StateChangeEvent) {
				phases.add(createTimelinePhaseForEvent(event, applicationForm));
			} else if (event instanceof ReferenceEvent) {
				timelineObjects.add(createTimelineReferenceFromEvent(event));
			}
		}
		setExitDates(phases);
		addCommentsToCorrectPhase(applicationForm, phases);
		timelineObjects.addAll(phases);
		Collections.sort(timelineObjects);
		return timelineObjects;
	}

	private TimelinePhase createUnsubmittedPhase(ApplicationForm applicationForm) {
		TimelinePhase phase = new TimelinePhase();
		phase.setEventDate(applicationForm.getApplicationTimestamp());
		phase.setStatus(ApplicationFormStatus.UNSUBMITTED);
		phase.setAuthor(applicationForm.getApplicant());
		return phase;
		
	}

	private TimelineReference createTimelineReferenceFromEvent(Event event) {
		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setEventDate(event.getDate());
		timelineReference.setAuthor(event.getUser());
		timelineReference.setReferee(((ReferenceEvent) event).getReferee());
		return timelineReference;
	}

}
