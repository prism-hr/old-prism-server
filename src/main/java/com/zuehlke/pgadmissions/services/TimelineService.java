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
import com.zuehlke.pgadmissions.domain.ReviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.TimelinePhase;

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

	public List<TimelinePhase> getPhases(ApplicationForm applicationForm) {
		List<TimelinePhase> phases = new ArrayList<TimelinePhase>();
		List<Event> events = applicationForm.getEvents();
		 
		for (Event event : events) {
			if(event instanceof StateChangeEvent){
				phases.add(createTimelineObjectForEvent(event));
			}
			
		}
		sortAndSetExitDates(phases);
		addCommentsToCorrectPhase(applicationForm, phases);
		return phases;
	}

	private void addCommentsToCorrectPhase(ApplicationForm applicationForm, List<TimelinePhase> phases) {
		List<Comment> visibleComments = applicationForm.getVisibleComments(userService.getCurrentUser());
		for (Comment comment : visibleComments) {
			for (TimelinePhase phase : phases) {
				if (comment.getDate().after(phase.getDate())
						&& (phase.getExitedPhaseDate() == null || comment.getDate().before(phase.getExitedPhaseDate()))) {
					phase.getComments().add(comment);
				}
			}
		}
	}

	private TimelinePhase createTimelineObjectForEvent(Event event) {
		
		TimelinePhase phase = new TimelinePhase();
		phase.setDate(event.getDate());			
		phase.setStatus( ((StateChangeEvent)event).getNewStatus());
		phase.setAuthor(event.getUser());
		phase.setMessageCode(resolveMessageCodeForStatus(((StateChangeEvent)event).getNewStatus()));
		if(event instanceof ReviewStateChangeEvent){
			phase.setReviewRound(((ReviewStateChangeEvent)event).getReviewRound());
		}else if(event instanceof InterviewStateChangeEvent){
			phase.setInterview(((InterviewStateChangeEvent)event).getInterview());
		}
		else if(event instanceof ApprovalStateChangeEvent){
			phase.setApprovalRound(((ApprovalStateChangeEvent)event).getApprovalRound());
		}
		return phase;
	}

	private void sortAndSetExitDates(List<TimelinePhase> phases) {
		Collections.sort(phases);
		for (int i = phases.size() - 1; i > 0; i--) {
			TimelinePhase thisPhase = phases.get(i);
			thisPhase.setExitedPhaseDate(phases.get(i - 1).getDate());
		}
	}
	
	private String resolveMessageCodeForStatus(ApplicationFormStatus status){
		return "timeline.phase." + status.displayValue().toLowerCase().replace(" ", "_");
	}

}
