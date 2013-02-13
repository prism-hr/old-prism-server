package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalEvaluationComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ApprovalEvaluationCommentBuilder {

	private RegisteredUser user;
	private ApplicationForm application;
	private CommentType type;
	private Date date;	
	private String comment;	
	private Integer id;
	private ApplicationFormStatus nextStatus;
	private ApprovalRound approvalRound;
	

	public ApprovalEvaluationCommentBuilder approvalRound(ApprovalRound approvalRound){
		this.approvalRound = approvalRound;
		return this;
	}
	public ApprovalEvaluationCommentBuilder nextStatus(ApplicationFormStatus nextStatus){
		this.nextStatus = nextStatus;
		return this;
	}
	
	public ApprovalEvaluationCommentBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	
	public ApprovalEvaluationCommentBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	
		
	public ApprovalEvaluationCommentBuilder type(CommentType type){
		this.type = type;
		return this;
	}
	
	public ApprovalEvaluationCommentBuilder date(Date date){
		this.date = date;
		return this;
	}
	
	public ApprovalEvaluationCommentBuilder comment(String comment){
		this.comment = comment;
		return this;
	}
	
	public ApprovalEvaluationCommentBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ApprovalEvaluationComment build(){
		ApprovalEvaluationComment approvalEaluationComment = new ApprovalEvaluationComment();
		approvalEaluationComment.setApplication(application);
		approvalEaluationComment.setComment(comment);
		approvalEaluationComment.setDate(date);
		approvalEaluationComment.setId(id);
		approvalEaluationComment.setType(type);
		approvalEaluationComment.setUser(user);
		approvalEaluationComment.setNextStatus(nextStatus);
		approvalEaluationComment.setApprovalRound(approvalRound);
		return approvalEaluationComment;
	}
}
