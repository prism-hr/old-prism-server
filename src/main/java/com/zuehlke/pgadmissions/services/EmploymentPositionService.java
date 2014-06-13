package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;

@Service
@Transactional
public class EmploymentPositionService {

    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
	private EntityDAO entityDAO;

    @Autowired
    private ApplicationCopyHelper applicationFormCopyHelper;

	public ApplicationEmploymentPosition getById(Integer id) {
		return entityDAO.getById(ApplicationEmploymentPosition.class, id);
	}

	public void saveOrUpdate(int applicationId, Integer employmentPositionId, ApplicationEmploymentPosition employmentPosition) { 
	    Application application = applicationService.getById(applicationId);
	    ApplicationEmploymentPosition persistentEmploymentPosition;
        if (employmentPositionId == null) {
            persistentEmploymentPosition = new ApplicationEmploymentPosition();
            persistentEmploymentPosition.setApplication(application);
            application.getEmploymentPositions().add(persistentEmploymentPosition);
            applicationService.save(application);
        } else {
            persistentEmploymentPosition = entityDAO.getById(ApplicationEmploymentPosition.class, employmentPositionId);
        }
        applicationFormCopyHelper.copyEmploymentPosition(persistentEmploymentPosition, employmentPosition, false);
    }

	public void delete(Integer employmentPositionId) {
	    ApplicationEmploymentPosition employment = entityDAO.getById(ApplicationEmploymentPosition.class, employmentPositionId);
        employment.getApplication().getEmploymentPositions().remove(employment);
	}
	
}
