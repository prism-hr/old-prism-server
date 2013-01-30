package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.dto.QualificationsAdminEditDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Service
public class QualificationService {

    private final ApplicationFormDAO applicationFormDAO;
    
	private final QualificationDAO qualificationDAO;
	
	private final EncryptionHelper encryptionHelper;

	public QualificationService() {
		this(null, null, null);
	}

	@Autowired
	public QualificationService(final QualificationDAO qualificationDAO, final ApplicationFormDAO applicationFormDAO, final EncryptionHelper encryptionHelper) {
		this.qualificationDAO = qualificationDAO;
		this.applicationFormDAO = applicationFormDAO;
		this.encryptionHelper = encryptionHelper;
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
    public void selectForSendingToPortico(final ApplicationForm applicationForm, final String sendToPorticoData) {
	    Gson gson = new Gson();
        QualificationsAdminEditDTO qualificationsData = gson.fromJson(sendToPorticoData, QualificationsAdminEditDTO.class);

        ArrayList<Integer> decryptedIds = new ArrayList<Integer>(2);
        for (String encryptedId : qualificationsData.getQualifications()) {
            decryptedIds.add(encryptionHelper.decryptToInteger(encryptedId));
        }
	    
	    for (Qualification qualification : applicationForm.getQualifications()) {
	        qualification.setSendToUCL(false);
	    }
	    
	    for (Integer qualificationId : decryptedIds) {
	        Qualification qualification = qualificationDAO.getQualificationById(qualificationId);
	        qualification.setSendToUCL(true);
	    }
    }
}
