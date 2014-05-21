package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.EmploymentPositionDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;

@Service
@Transactional
public class EmploymentPositionService {

    @Autowired
    private ApplicationService applicationFormService;
    
    @Autowired
	private EmploymentPositionDAO employmentPositionDAO;

    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

	public EmploymentPosition getById(Integer id) {
		return employmentPositionDAO.getById(id);
	}

	public EmploymentPosition getOrCreate(Integer employmentPositionId) {
        if (employmentPositionId == null) {
            return new EmploymentPosition();
        }
        return getSecuredInstance(employmentPositionId);
    }
	
	public void saveOrUpdate(Application application, Integer employmentPositionId, EmploymentPosition employmentPosition) { 
	    EmploymentPosition persistentEmploymentPosition;
        if (employmentPositionId == null) {
            persistentEmploymentPosition = new EmploymentPosition();
            persistentEmploymentPosition.setApplication(application);
            application.getEmploymentPositions().add(persistentEmploymentPosition);
            applicationFormService.saveUpdate(application);
        } else {
            persistentEmploymentPosition = getSecuredInstance(employmentPositionId);
        }
        applicationFormCopyHelper.copyEmploymentPosition(persistentEmploymentPosition, employmentPosition, false);
        applicationFormService.saveOrUpdateApplicationSection(application);
    }

	public void delete(Integer employmentPositionId) {
	    EmploymentPosition employmentPosition = getById(employmentPositionId);
		employmentPositionDAO.delete(employmentPosition);
	    applicationFormService.saveOrUpdateApplicationSection(employmentPosition.getApplication());
	}
	
	private EmploymentPosition getSecuredInstance(Integer employmentPositionId) {
	    EmploymentPosition employmentPosition = getById(employmentPositionId);
        if (employmentPosition == null) {
            throw new ResourceNotFoundException();
        }
        return employmentPosition;
	}
	
}
