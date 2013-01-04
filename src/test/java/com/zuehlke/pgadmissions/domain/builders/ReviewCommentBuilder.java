package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewCommentBuilder {
	
	private Boolean willingToInterview;
	private Boolean suitableCandidateForUcl;
	private Boolean suitableCandidateForProgramme;
	private boolean decline;
	private boolean adminsNotified;
	private ApplicationForm applicationForm;
	private String comment;
	private Integer id;
	private Date createdTimeStamp;
	private RegisteredUser user;
	private CommentType commentType;
	private Reviewer reviewer;
	
	public ReviewCommentBuilder reviewer(Reviewer reviewer) {
		this.reviewer = reviewer;
		return this;
	}
	
	public ReviewCommentBuilder willingToInterview(Boolean willingToInterview) {
		this.willingToInterview = willingToInterview;
		return this;
	}
	
	public ReviewCommentBuilder suitableCandidateForUCL(Boolean suitableCandidate) {
		this.suitableCandidateForUcl = suitableCandidate;
		return this;
	}
	
	public ReviewCommentBuilder suitableCandidateForProgramme(Boolean suitableCandidateForProgramme) {
		this.suitableCandidateForProgramme = suitableCandidateForProgramme;
		return this;
	}
	
	public ReviewCommentBuilder decline(boolean decline) {
		this.decline = decline;
		return this;
	}
	
	
	public ReviewCommentBuilder adminsNotified(boolean adminsNotified) {
		this.adminsNotified = adminsNotified;
		return this;
	}
	
	public ReviewCommentBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public ReviewCommentBuilder application (ApplicationForm application){
		this.applicationForm = application;
		return this;
	}
	public ReviewCommentBuilder comment (String comment){
		this.comment = comment;
		return this;
	}
	public ReviewCommentBuilder commentType (CommentType commentType){
		this.commentType = commentType;
		return this;
	}
	public ReviewCommentBuilder user (RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public ReviewComment build() {
		ReviewComment reviewComment = new ReviewComment();
		reviewComment.setApplication(applicationForm);
		reviewComment.setComment(comment);
		reviewComment.setType(commentType);
		reviewComment.setDate(createdTimeStamp);
		reviewComment.setDecline(decline);
		reviewComment.setId(id);
		reviewComment.setSuitableCandidateForUcl(suitableCandidateForUcl);
		reviewComment.setUser(user);
		reviewComment.setWillingToInterview(willingToInterview);
		reviewComment.setAdminsNotified(adminsNotified);
		reviewComment.setReviewer(reviewer);
		reviewComment.setSuitableCandidateForProgramme(suitableCandidateForProgramme);
		return reviewComment;
		
	}
	
}
