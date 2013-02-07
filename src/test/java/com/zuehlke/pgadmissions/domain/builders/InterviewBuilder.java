package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;

public class InterviewBuilder {

	private Integer id;
	private ApplicationForm application;
	private Date lastNotified;	
	private Date dueDate;	
	private String interviewTime;
	private String furtherDetails;
	private String locationURL;
	private List<Interviewer> interviewers = new ArrayList<Interviewer>();

	public InterviewBuilder lastNotified(Date lastNotified){
		this.lastNotified = lastNotified;
		return this;
	}
	
	public InterviewBuilder interviewers(Interviewer...interviewers) {
		for (Interviewer interviewer : interviewers) {
			this.interviewers.add(interviewer);
		}
		return this;
	}
	
	public InterviewBuilder dueDate(Date dueDate){
		this.dueDate = dueDate;
		return this;
	}
	
	public InterviewBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	
	public InterviewBuilder furtherDetails(String furtherDetails){
		this.furtherDetails = furtherDetails;
		return this;
	}
	
	
	public InterviewBuilder locationURL(String locationURL){
		this.locationURL = locationURL;
		return this;
	}
	
	public InterviewBuilder interviewTime(String interviewTime){
		this.interviewTime = interviewTime;
		return this;
	}
	
	public InterviewBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public Interview build() {
		Interview interview = new Interview();
		interview.setId(id);
		interview.setApplication(application);
		interview.setFurtherDetails(furtherDetails);
		interview.setLastNotified(lastNotified);
		interview.setLocationURL(locationURL);
		interview.setInterviewDueDate(dueDate);
		interview.getInterviewers().addAll(interviewers);
		interview.setInterviewTime(interviewTime);
		return interview;
	}
	
}
