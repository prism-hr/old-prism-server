package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class ValidationCommentBuilder {

	private RegisteredUser user;
	private ApplicationForm application;
	private ValidationQuestionOptions qualifiedForPhd;
	private ValidationQuestionOptions englishCompentencyOk;
	private HomeOrOverseas homeOrOverseas;
	private CommentType type;
	private Date date;	
	private String comment;	
	private Integer id;
	
	
	public ValidationCommentBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	
	public ValidationCommentBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	
	public ValidationCommentBuilder qualifiedForPhd(ValidationQuestionOptions qualifiedForPhd){
		this.qualifiedForPhd = qualifiedForPhd;
		return this;
	}
	
	
	public ValidationCommentBuilder englishCompentencyOk(ValidationQuestionOptions englishCompentencyOk){
		this.englishCompentencyOk = englishCompentencyOk;
		return this;
	}
	
	public ValidationCommentBuilder homeOrOverseas(HomeOrOverseas homeOrOverseas){
		this.homeOrOverseas = homeOrOverseas;
		return this;
	}
	
	public ValidationCommentBuilder type(CommentType type){
		this.type = type;
		return this;
	}
	
	public ValidationCommentBuilder date(Date date){
		this.date = date;
		return this;
	}
	
	public ValidationCommentBuilder comment(String comment){
		this.comment = comment;
		return this;
	}
	
	public ValidationCommentBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ValidationComment toValidationComment(){
		ValidationComment validationComment = new ValidationComment();
		validationComment.setApplication(application);
		validationComment.setComment(comment);
		validationComment.setDate(date);
		validationComment.setEnglishCompentencyOk(englishCompentencyOk);
		validationComment.setHomeOrOverseas(homeOrOverseas);
		validationComment.setId(id);
		validationComment.setQualifiedForPhd(qualifiedForPhd);
		validationComment.setType(type);
		validationComment.setUser(user);
		return validationComment;
	}
}
