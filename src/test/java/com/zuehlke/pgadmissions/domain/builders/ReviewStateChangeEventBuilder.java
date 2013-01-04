package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.ReviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ReviewStateChangeEventBuilder {

	private Integer id;
	private Date eventDate;	
	private ApplicationFormStatus newStatus;
	private RegisteredUser user;
	private ReviewRound reviewRound;
	
	public ReviewStateChangeEventBuilder reviewRound(ReviewRound reviewRound){
		this.reviewRound = reviewRound;
		return this;
	}
	
	public ReviewStateChangeEventBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public ReviewStateChangeEventBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ReviewStateChangeEventBuilder date(Date eventDate){
		this.eventDate = eventDate;
		return this;
	}
	
	public ReviewStateChangeEventBuilder newStatus(ApplicationFormStatus newStatus){
		this.newStatus = newStatus;
		return this;
	}
	
	public StateChangeEvent build(){
		ReviewStateChangeEvent event = new ReviewStateChangeEvent();
		event.setId(id);
		event.setDate(eventDate);
		event.setNewStatus(newStatus);
		event.setUser(user);
		event.setReviewRound(reviewRound);
		return event;
	}
}
