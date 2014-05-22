package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.FundingDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;

@Service
@Transactional
public class FundingService {

    @Autowired
    private ApplicationService applicationFormService;
    
    @Autowired
    private FundingDAO fundingDAO;

    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    public Funding getById(Integer id) {
        return fundingDAO.getById(id);
    }
    
    public Funding getOrCreate(Integer fundingId) {
        if (fundingId == null) {
            return new Funding();
        }
        return getSecuredInstance(fundingId);
    }
    
    public void saveOrUpdate(Application application, Integer fundingId, Funding funding) { 
        Funding persistentFunding;
        if (fundingId == null) {
            persistentFunding = new Funding();
            persistentFunding.setApplication(application);
            application.getFundings().add(persistentFunding);
            applicationFormService.saveUpdate(application);
        } else {
            persistentFunding = getSecuredInstance(fundingId);
        }
        applicationFormCopyHelper.copyFunding(persistentFunding, funding, true);
        applicationFormService.saveOrUpdateApplicationSection(application);
    }
    
    public void delete(Integer fundingId) {
        Funding funding = getById(fundingId);
        fundingDAO.delete(funding);
        applicationFormService.saveOrUpdateApplicationSection(funding.getApplication());
    }
    
    private Funding getSecuredInstance(Integer fundingId) {
        Funding funding = getById(fundingId);
        if (funding == null) {
            throw new ResourceNotFoundException();
        }
        return funding;
    }
    
}
