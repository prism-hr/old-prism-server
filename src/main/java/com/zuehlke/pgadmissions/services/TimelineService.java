package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalStateChangeEvent;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ConfirmEligibilityEvent;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.InterviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceEvent;
import com.zuehlke.pgadmissions.domain.ReviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.dto.ApplicationCreatedPhase;
import com.zuehlke.pgadmissions.dto.TimelineConfirmEligibility;
import com.zuehlke.pgadmissions.dto.TimelineObject;
import com.zuehlke.pgadmissions.dto.TimelinePhase;
import com.zuehlke.pgadmissions.dto.TimelineReference;

@Service
@Transactional
public class TimelineService {

	private final UserService userService;

	public TimelineService() {
		this(null);
	}

	@Autowired
	public TimelineService(UserService userService) {
		this.userService = userService;

	}

	private void addCommentsToCorrectPhaseExcludingEligibility(ApplicationForm applicationForm, List<TimelinePhase> phases, Set<Comment> loadedComments) {
		List<Comment> visibleComments = applicationForm.getVisibleComments(userService.getCurrentUser());
		for (Comment comment : visibleComments) {
			for (TimelinePhase phase : phases) {
				if (CommentType.REFERENCE != comment.getType() && comment.getDate().compareTo(phase.getEventDate()) >= 0 
						&& (phase.getExitedPhaseDate() == null || comment.getDate().before(phase.getExitedPhaseDate()))
						&& !loadedComments.contains(comment)) {
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
		if(((StateChangeEvent) event).getNewStatus().equals(ApplicationFormStatus.APPROVAL)){
		    Event firstApprovalEvent = getFirstApprovalEvent(applicationForm);
			if(firstApprovalEvent != null && !event.getId().equals(firstApprovalEvent.getId())) {
				phase.setMessageCode("timeline.phase.approval");
			}
		}
		return phase;
	}

	private Event getFirstApprovalEvent( ApplicationForm applicationForm){
		Event firstevent = null;
		List<Event> events = applicationForm.getEvents();
		for (Event event : events) {
			if(event instanceof StateChangeEvent && ((StateChangeEvent) event).getNewStatus().equals(ApplicationFormStatus.APPROVAL)){
				if(firstevent == null  || firstevent.getDate().after(event.getDate())){
					firstevent = event;
				}		
	
			}
		}
		return firstevent;
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
		
		phases.add(createUnsubmittedPhase(applicationForm));

		List<Event> events = applicationForm.getEvents();
		Set<Comment> confirmEligibilityComments = new HashSet<Comment>();
		
		for (Event event : events) {
			if (event instanceof StateChangeEvent) {
				phases.add(createTimelinePhaseForEvent(event, applicationForm));
			} else if (event instanceof ConfirmEligibilityEvent) {
			    timelineObjects.add(createTimelineConfirmEligibilityEvent(event));
			    confirmEligibilityComments.add(((ConfirmEligibilityEvent) event).getComment());
			}else if (event instanceof ReferenceEvent) {
				timelineObjects.add(createTimelineReferenceFromEvent(event));
			}
		}
		setExitDates(phases);
		addCommentsToCorrectPhaseExcludingEligibility(applicationForm, phases, confirmEligibilityComments);
		timelineObjects.addAll(phases);
		Collections.sort(timelineObjects);
		return timelineObjects;
	}

	private TimelinePhase createUnsubmittedPhase(ApplicationForm applicationForm) {
		ApplicationCreatedPhase phase = new ApplicationCreatedPhase();
		phase.setEventDate(applicationForm.getApplicationTimestamp());
		phase.setStatus(ApplicationFormStatus.UNSUBMITTED);
		phase.setAuthor(applicationForm.getApplicant());

		if (applicationForm.getProject() != null) {
			phase.setProjectTitle(applicationForm.getProject().getTitle());
			phase.setProjectDescription(applicationForm.getProject().getDescription());
		}
		
		return phase;
		
	}

	private TimelineReference createTimelineReferenceFromEvent(Event event) {
		TimelineReference timelineReference = new TimelineReference();
		timelineReference.setEventDate(event.getDate());
		timelineReference.setAuthor(event.getUser());
		
		Referee referee = ((ReferenceEvent) event).getReferee();
		timelineReference.setReferee(referee);
		return timelineReference;
	}
	
	private TimelineConfirmEligibility createTimelineConfirmEligibilityEvent(Event event) {
	    TimelineConfirmEligibility timelineConfirmEligibility = new TimelineConfirmEligibility();
	    timelineConfirmEligibility.setEventDate(event.getDate());
	    timelineConfirmEligibility.setAuthor(event.getUser());
	    timelineConfirmEligibility.setComment(((ConfirmEligibilityEvent)event).getComment());
	    return timelineConfirmEligibility;
	}
}
