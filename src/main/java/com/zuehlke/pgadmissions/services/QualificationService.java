package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Qualification;

@Service
@Transactional
public class QualificationService {

    @Autowired
    private ApplicationService applicationFormService;

    @Autowired
    private ApplicationCopyHelper applicationFormCopyHelper;

    @Autowired
    private EntityDAO entityDAO;

    public Qualification getById(Integer id) {
        return entityDAO.getById(Qualification.class, id);
    }

    public void saveOrUpdate(int applicationId, Integer qualificationId, Qualification qualification) {
        Application application = applicationFormService.getById(applicationId);
        Qualification persistentQualification;
        if (qualificationId == null) {
            persistentQualification = new Qualification();
            persistentQualification.setApplication(application);
            application.getQualifications().add(persistentQualification);
        } else {
            persistentQualification = entityDAO.getById(Qualification.class, qualificationId);
        }
        applicationFormCopyHelper.copyQualification(persistentQualification, qualification, true);
    }

    public void delete(Integer qualificationId) {
        Qualification qualification = entityDAO.getById(Qualification.class, qualificationId);
        qualification.getApplication().getQualifications().remove(qualification);
    }

    public void selectForSendingToPortico(int applicationId, final List<Integer> qualificationsSendToPortico) {
        Application application = applicationFormService.getById(applicationId);
        for (Qualification qualification : application.getQualifications()) {
            boolean toBeSelected = qualificationsSendToPortico.contains(qualification.getId());
            qualification.setIncludeInExport(toBeSelected);
        }
    }

}
