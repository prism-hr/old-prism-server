package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;


import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewCommentBuilder {
	
	private CheckedStatus willingToSupervice;
	private CheckedStatus suitableCandidate;
	private CheckedStatus decline;
	private CheckedStatus adminsNotified;
	private ApplicationForm applicationForm;
	private String comment;
	private Integer id;
	private Date createdTimeStamp;
	private RegisteredUser user;
	private CommentType commentType;
	
	public ReviewCommentBuilder willingToSupervice(CheckedStatus willingToSupervice) {
		this.willingToSupervice = willingToSupervice;
		return this;
	}
	
	public ReviewCommentBuilder suitableCandidate(CheckedStatus suitableCandidate) {
		this.suitableCandidate = suitableCandidate;
		return this;
	}
	
	public ReviewCommentBuilder decline(CheckedStatus decline) {
		this.decline = decline;
		return this;
	}
	
	
	public ReviewCommentBuilder adminsNotified(CheckedStatus adminsNotified) {
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
	
	public ReviewComment toReviewComment() {
		ReviewComment reviewComment = new ReviewComment();
		reviewComment.setApplication(applicationForm);
		reviewComment.setComment(comment);
		reviewComment.setType(commentType);
		reviewComment.setDate(createdTimeStamp);
		reviewComment.setDecline(decline);
		reviewComment.setId(id);
		reviewComment.setSuitableCandidate(suitableCandidate);
		reviewComment.setUser(user);
		reviewComment.setWillingToSupervice(willingToSupervice);
		reviewComment.setAdminsNotified(adminsNotified);
		return reviewComment;
		
	}
	
}
