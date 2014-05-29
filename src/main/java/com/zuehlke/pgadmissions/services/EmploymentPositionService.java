package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;

@Service
@Transactional
public class EmploymentPositionService {

    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
	private EntityDAO entityDAO;

    @Autowired
    private ApplicationCopyHelper applicationFormCopyHelper;

	public EmploymentPosition getById(Integer id) {
		return entityDAO.getById(EmploymentPosition.class, id);
	}

	public void saveOrUpdate(int applicationId, Integer employmentPositionId, EmploymentPosition employmentPosition) { 
	    Application application = applicationService.getById(applicationId);
	    EmploymentPosition persistentEmploymentPosition;
        if (employmentPositionId == null) {
            persistentEmploymentPosition = new EmploymentPosition();
            persistentEmploymentPosition.setApplication(application);
            application.getEmploymentPositions().add(persistentEmploymentPosition);
            applicationService.save(application);
        } else {
            persistentEmploymentPosition = entityDAO.getById(EmploymentPosition.class, employmentPositionId);
        }
        applicationFormCopyHelper.copyEmploymentPosition(persistentEmploymentPosition, employmentPosition, false);
    }

	public void delete(Integer employmentPositionId) {
	    EmploymentPosition employment = entityDAO.getById(EmploymentPosition.class, employmentPositionId);
        employment.getApplication().getEmploymentPositions().remove(employment);
	}
	
}
