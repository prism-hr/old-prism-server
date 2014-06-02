package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Funding;

@Service
@Transactional
public class FundingService {

    @Autowired
    private ApplicationService applicationFormService;
    
    @Autowired
    private EntityDAO entityDAO;

    @Autowired
    private ApplicationCopyHelper applicationFormCopyHelper;

    public Funding getById(Integer id) {
        return entityDAO.getById(Funding.class, id);
    }

    public void saveOrUpdate(int applicationId, Integer fundingId, Funding funding) { 
        Application application = applicationFormService.getById(applicationId);
        Funding persistentFunding;
        if (fundingId == null) {
            persistentFunding = new Funding();
            persistentFunding.setApplication(application);
            application.getFundings().add(persistentFunding);
        } else {
            persistentFunding = entityDAO.getById(Funding.class, fundingId);
        }
        applicationFormCopyHelper.copyFunding(persistentFunding, funding, true);
    }
    
    public void delete(Integer fundingId) {
        Funding funding = entityDAO.getById(Funding.class, fundingId);
        funding.getApplication().getFundings().remove(funding);
    }
    
}
