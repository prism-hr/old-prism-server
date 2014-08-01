package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;

@Service
@Transactional
public class ApplicationQualificationService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationCopyHelper applicationCopyHelper;

    @Autowired
    private EntityService entityService;

    public ApplicationQualification getById(Integer id) {
        return entityService.getById(ApplicationQualification.class, id);
    }

    public void saveOrUpdate(int applicationId, Integer qualificationId, ApplicationQualification qualification) {
        Application application = applicationService.getById(applicationId);
        ApplicationQualification persistentQualification;
        if (qualificationId == null) {
            persistentQualification = new ApplicationQualification();
            persistentQualification.setApplication(application);
            application.getQualifications().add(persistentQualification);
        } else {
            persistentQualification = entityService.getById(ApplicationQualification.class, qualificationId);
        }
        applicationCopyHelper.copyQualification(persistentQualification, qualification, true);
    }

    public void delete(Integer qualificationId) {
        ApplicationQualification qualification = entityService.getById(ApplicationQualification.class, qualificationId);
        qualification.getApplication().getQualifications().remove(qualification);
    }

    public void selectForSendingToPortico(int applicationId, final List<Integer> qualificationsSendToPortico) {
        Application application = applicationService.getById(applicationId);
        for (ApplicationQualification qualification : application.getQualifications()) {
            boolean toBeSelected = qualificationsSendToPortico.contains(qualification.getId());
            qualification.setIncludeInExport(toBeSelected);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<ApplicationQualification> getExportApplicationQualifications() {
        return (List<ApplicationQualification>) entityService.getByPropertyNotNull(ApplicationQualification.class, "document");
    }

}
