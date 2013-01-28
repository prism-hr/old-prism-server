package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;

@Service
public class QualificationService {

    private final ApplicationFormDAO applicationFormDAO;
    
	private final QualificationDAO qualificationDAO;

	public QualificationService() {
		this(null, null);
	}

	@Autowired
	public QualificationService(final QualificationDAO qualificationDAO, final ApplicationFormDAO applicationFormDAO) {
		this.qualificationDAO = qualificationDAO;
		this.applicationFormDAO = applicationFormDAO;
	}

	public Qualification getQualificationById(Integer id) {
		return qualificationDAO.getQualificationById(id);
	}

	@Transactional
	public void delete(Qualification qualification) {
		qualificationDAO.delete(qualification);

	}

	@Transactional
	public void save(Qualification qualification) {
		qualificationDAO.save(qualification);
	}

	@Transactional
    public void selectForSendingToPortico(final String applicationNumber, final List<Integer> qualificationSendToUcl) {
	    ApplicationForm applicationForm = applicationFormDAO.getApplicationByApplicationNumber(applicationNumber);
	    for (Qualification qualification : applicationForm.getQualifications()) {
	        qualification.setSendToUCL(false);
	    }
	    
	    for (Integer qualificationId : qualificationSendToUcl) {
	        Qualification qualification = qualificationDAO.getQualificationById(qualificationId);
	        qualification.setSendToUCL(true);
	    }
    }
}
