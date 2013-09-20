package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;

public class ReviewRoundBuilder {
	
	private List<Reviewer> reviewers = new ArrayList<Reviewer>();	
	
	private ApplicationForm application;	
	
	private Integer id;
	
	private Date createdDate;
	
	public ReviewRoundBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ReviewRoundBuilder createdDate(Date createdDate){
		this.createdDate = createdDate;
		return this;
	}
	
	public ReviewRoundBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	
	public ReviewRoundBuilder reviewers(Reviewer... reviewers){
		this.reviewers.addAll(Arrays.asList(reviewers));
		return this;
	}
	
	public ReviewRound build(){
		ReviewRound reviewRound = new ReviewRound();
		reviewRound.setId(id);
		reviewRound.setReviewers(reviewers);
		reviewRound.setApplication(application);
		reviewRound.setCreatedDate(createdDate);		
		return reviewRound;
	}
}
