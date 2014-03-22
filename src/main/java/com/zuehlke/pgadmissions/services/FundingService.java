package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.FundingDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;

@Service
@Transactional
public class FundingService {

    @Autowired
    private ApplicationFormService applicationsService;
    
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
        Funding funding = getById(fundingId);
        if (funding == null) {
            throw new ResourceNotFoundException();
        }
        return funding;
    }
    
    public void saveOrUpdate(ApplicationForm application, Integer fundingId, Funding funding) { 
        Funding persistentFunding;
        if (fundingId != null) {
            persistentFunding = fundingDAO.getById(fundingId);
            if (persistentFunding == null) {
                throw new ResourceNotFoundException();
            }
        } else {
            persistentFunding = new Funding();
            persistentFunding.setApplication(application);
            fundingDAO.save(persistentFunding);
        }
        applicationFormCopyHelper.copyFunding(persistentFunding, funding, true);
        applicationsService.saveOrUpdateApplicationFormSection(application);
    }
    
    public void delete(Integer fundingId) {
        Funding funding = getById(fundingId);
        fundingDAO.delete(funding);
        applicationsService.saveOrUpdateApplicationFormSection(funding.getApplication());
    }
    
}
