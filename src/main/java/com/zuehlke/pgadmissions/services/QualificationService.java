package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;

@Service
@Transactional
public class QualificationService {
    
    @Autowired
    ApplicationFormService applicationsService;

    @Autowired
    ApplicationFormCopyHelper applicationFormCopyHelper;

    @Autowired
    private QualificationDAO qualificationDAO;

    public Qualification getById(Integer id) {
        return qualificationDAO.getById(id);
    }

    public Qualification getOrCreate(Integer qualificationId) {
        if (qualificationId == null) {
            return new Qualification();
        }
        Qualification qualification = getById(qualificationId);
        if (qualification == null) {
            throw new ResourceNotFoundException();
        }
        return qualification;
    }

    public void saveOrUpdate(ApplicationForm application, Integer qualificationId, Qualification qualification) {
        Qualification persistentQualification;
        if (qualificationId != null) {
            persistentQualification = qualificationDAO.getById(qualificationId);
            if (persistentQualification == null) {
                throw new ResourceNotFoundException();
            }
        } else {
            persistentQualification = new Qualification();
            persistentQualification.setApplication(application);
            qualificationDAO.save(persistentQualification);
        }
        applicationFormCopyHelper.copyQualification(persistentQualification, qualification, true);
        applicationsService.saveOrUpdateApplicationFormSection(application);
    }
    
    public void delete(Integer qualificationId) {
        Qualification qualification = getById(qualificationId);
        qualificationDAO.delete(qualification);
        applicationsService.saveOrUpdateApplicationFormSection(qualification.getApplication());
    }

    public void selectForSendingToPortico(final ApplicationForm applicationForm, final List<Integer> qualificationsSendToPortico) {
        for (Qualification qualification : applicationForm.getQualifications()) {
            qualification = qualificationDAO.getById(qualification.getId());
            qualification.setSendToUCL(false);
        }
        for (Integer qualificationId : qualificationsSendToPortico) {
            Qualification qualification = qualificationDAO.getById(qualificationId);
            qualification.setSendToUCL(true);
        }
    }

}
