package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Event;
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
			TimelinePhase phase = new TimelinePhase();
			phase.setDate(event.getDate());
			phase.setStatus(event.getNewStatus());
			phase.setAuthor(event.getUser());
			phase.setMessageCode(resolveMessageCodeForStatus(event.getNewStatus()));
			phases.add(phase);
			
		}
		sortAndSetExitDates(phases);
		List<Comment> visibleComments = applicationForm.getVisibleComments(userService.getCurrentUser());
		for (Comment comment : visibleComments) {
			for (TimelinePhase phase : phases) {
				if (comment.getDate().after(phase.getDate())
						&& (phase.getExitedPhaseDate() == null || comment.getDate().before(phase.getExitedPhaseDate()))) {
					phase.getComments().add(comment);
				}
			}
		}
		return phases;
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
