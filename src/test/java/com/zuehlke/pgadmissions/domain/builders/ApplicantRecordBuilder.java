package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicantRecord;


public class ApplicantRecordBuilder {

	private Integer id;
	
	private String firstname;
	
	private String lastname;
	
	private String email;
	
	private String password;

	public ApplicantRecordBuilder firstname(String firstname){
		this.firstname = firstname;
		return this;
	}
	
	public ApplicantRecordBuilder lastname(String lastname){
		this.lastname = lastname;
		return this;
	}
	
	public ApplicantRecordBuilder email(String email){
		this.email = email;
		return this;
	}
	
	public ApplicantRecordBuilder password(String password){
		this.password = password;
		return this;
	}
	
	public ApplicantRecordBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ApplicantRecord toApplicantRecord() {
		ApplicantRecord record = new ApplicantRecord();
		record.setEmail(email);
		record.setFirstname(firstname);
		record.setId(id);
		record.setLastname(lastname);
		record.setPassword(password);
		return record;
	}
}
