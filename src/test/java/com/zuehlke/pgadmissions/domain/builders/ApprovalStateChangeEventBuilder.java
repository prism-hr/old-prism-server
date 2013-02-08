package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.ApprovalStateChangeEvent;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApprovalStateChangeEventBuilder {

	private Integer id;
	private Date eventDate;	
	private ApplicationFormStatus newStatus;
	private RegisteredUser user;
	private ApprovalRound approvalRound;
	
	public ApprovalStateChangeEventBuilder approvalRound(ApprovalRound approvalRound){
		this.approvalRound = approvalRound;
		return this;
	}
	
	public ApprovalStateChangeEventBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public ApprovalStateChangeEventBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ApprovalStateChangeEventBuilder date(Date eventDate){
		this.eventDate = eventDate;
		return this;
	}
	
	public ApprovalStateChangeEventBuilder newStatus(ApplicationFormStatus newStatus){
		this.newStatus = newStatus;
		return this;
	}
	
	public StateChangeEvent build(){
		ApprovalStateChangeEvent event = new ApprovalStateChangeEvent();
		event.setId(id);
		event.setDate(eventDate);
		event.setNewStatus(newStatus);
		event.setUser(user);
		event.setApprovalRound(approvalRound);
		return event;
	}
}
