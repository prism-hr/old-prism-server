package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.FundingDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Funding;

@Service
@Transactional
public class FundingService {

    @Autowired
    private FundingDAO fundingDAO;

    @Autowired
    ApplicationFormDAO applicationFormDAO;

    @Autowired
    private DocumentService documentService;

    public Funding getFundingById(Integer id) {
        return fundingDAO.getFundingById(id);
    }

    public void delete(Funding funding) {
        fundingDAO.delete(funding);
    }

    public void save(int applicationId, Integer fundingId, Funding newFunding) {
        ApplicationForm application = applicationFormDAO.get(applicationId);

        Funding funding;
        if (fundingId != null) {
            funding = fundingDAO.getFundingById(fundingId);
            Preconditions.checkState(funding.getApplication().getId().equals(application.getId()));
        } else {
            funding = new Funding();
            application.getFundings().add(funding);
            funding.setApplication(application);
        }

        documentService.documentReferentialityChanged(funding.getDocument(), newFunding.getDocument());

        funding.setType(newFunding.getType());
        funding.setDocument(newFunding.getDocument());
        funding.setDescription(newFunding.getDescription());
        funding.setValue(newFunding.getValue());
        funding.setAwardDate(newFunding.getAwardDate());
        fundingDAO.saveOrUpdate(funding);
    }
}
