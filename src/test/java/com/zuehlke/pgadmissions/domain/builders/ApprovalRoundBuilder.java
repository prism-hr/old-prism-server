package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Supervisor;

public class ApprovalRoundBuilder {
	
	private List<Supervisor> supervisors = new ArrayList<Supervisor>();	
	
	private ApplicationForm application;	
	
	private Integer id;
	
	private Date createdDate;
	
	public ApprovalRoundBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ApprovalRoundBuilder createdDate(Date createdDate){
		this.createdDate = createdDate;
		return this;
	}
	
	public ApprovalRoundBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	
	public ApprovalRoundBuilder supervisors(Supervisor... supervisors){
		this.supervisors.addAll(Arrays.asList(supervisors));
		return this;
	}
	
	public ApprovalRound build(){
		ApprovalRound approvalRound = new ApprovalRound();
		approvalRound.setApplication(application);
		approvalRound.setCreatedDate(createdDate);
		approvalRound.setSupervisors(supervisors);
		approvalRound.setId(id);
		return approvalRound;
	}
}
