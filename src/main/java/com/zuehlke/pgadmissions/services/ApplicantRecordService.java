package com.zuehlke.pgadmissions.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicantRecordDAO;
import com.zuehlke.pgadmissions.domain.ApplicantRecord;

@Service("applicantRecordService")
public class ApplicantRecordService {

	private final ApplicantRecordDAO applicationRecordDAO;

	ApplicantRecordService(){
		this(null);
	}

	
	@Autowired
	public ApplicantRecordService(ApplicantRecordDAO applicationRecordDAO) {
		this.applicationRecordDAO = applicationRecordDAO;
	}
	
	@Transactional
	public void save(ApplicantRecord record){
		applicationRecordDAO.save(record);
	}



}
