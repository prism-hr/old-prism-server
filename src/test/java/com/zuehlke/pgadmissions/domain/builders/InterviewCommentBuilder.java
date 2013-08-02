package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class InterviewCommentBuilder {
	
	private Boolean willingToSupervice;
	private Boolean suitableCandidateForUCL;
	private Boolean suitableCandidateForProgramme;
	private Integer applicantRating;
	private boolean decline;
	private boolean adminsNotified;
	private ApplicationForm applicationForm;
	private String comment;
	private Integer id;
	private Date createdTimeStamp;
	private RegisteredUser user;
	private CommentType commentType;
	private Interviewer interviewer;
	
	public InterviewCommentBuilder createdTimeStamp(Date createdTimeStamp) {
	    this.createdTimeStamp = createdTimeStamp;
	    return this;
	}
	
	public InterviewCommentBuilder interviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
		return this;
	}
	
	public InterviewCommentBuilder willingToSupervise(Boolean willingToSupervice) {
		this.willingToSupervice = willingToSupervice;
		return this;
	}
	
	public InterviewCommentBuilder suitableCandidateForUcl(Boolean suitableCandidate) {
		this.suitableCandidateForUCL = suitableCandidate;
		return this;
	}
	
	public InterviewCommentBuilder suitableCandidateForProgramme(Boolean suitableCandidateForProgramme) {
		this.suitableCandidateForProgramme = suitableCandidateForProgramme;
		return this;
	}

	public InterviewCommentBuilder suitableCandidateForProgramme(Integer applicantRating) {
	    this.applicantRating = applicantRating;
	    return this;
	}
	
	public InterviewCommentBuilder decline(boolean decline) {
		this.decline = decline;
		return this;
	}
	
	
	public InterviewCommentBuilder adminsNotified(boolean adminsNotified) {
		this.adminsNotified = adminsNotified;
		return this;
	}
	
	public InterviewCommentBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public InterviewCommentBuilder application (ApplicationForm application){
		this.applicationForm = application;
		return this;
	}
	public InterviewCommentBuilder comment (String comment){
		this.comment = comment;
		return this;
	}
	public InterviewCommentBuilder commentType (CommentType commentType){
		this.commentType = commentType;
		return this;
	}
	public InterviewCommentBuilder user (RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public InterviewComment build() {
		InterviewComment interviewComment = new InterviewComment();
		interviewComment.setApplication(applicationForm);
		interviewComment.setComment(comment);
		interviewComment.setType(commentType);
		interviewComment.setDate(createdTimeStamp);
		interviewComment.setDecline(decline);
		interviewComment.setId(id);
		interviewComment.setSuitableCandidateForUcl(suitableCandidateForUCL);
		interviewComment.setUser(user);
		interviewComment.setWillingToSupervise(willingToSupervice);
		interviewComment.setAdminsNotified(adminsNotified);
		interviewComment.setInterviewer(interviewer);
		interviewComment.setSuitableCandidateForProgramme(suitableCandidateForProgramme);
		interviewComment.setApplicantRating(applicantRating);
		return interviewComment;
	}
}
