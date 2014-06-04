package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationFunding;

@Service
@Transactional
public class FundingService {

    @Autowired
    private ApplicationService applicationFormService;
    
    @Autowired
    private EntityDAO entityDAO;

    @Autowired
    private ApplicationCopyHelper applicationFormCopyHelper;

    public ApplicationFunding getById(Integer id) {
        return entityDAO.getById(ApplicationFunding.class, id);
    }

    public void saveOrUpdate(int applicationId, Integer fundingId, ApplicationFunding funding) { 
        Application application = applicationFormService.getById(applicationId);
        ApplicationFunding persistentFunding;
        if (fundingId == null) {
            persistentFunding = new ApplicationFunding();
            persistentFunding.setApplication(application);
            application.getApplicationFundings().add(persistentFunding);
        } else {
            persistentFunding = entityDAO.getById(ApplicationFunding.class, fundingId);
        }
        applicationFormCopyHelper.copyFunding(persistentFunding, funding, true);
    }
    
    public void delete(Integer fundingId) {
        ApplicationFunding funding = entityDAO.getById(ApplicationFunding.class, fundingId);
        funding.getApplication().getApplicationFundings().remove(funding);
    }
    
}
