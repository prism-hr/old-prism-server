package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.FundingDAO;
import com.zuehlke.pgadmissions.domain.Funding;

@Service
@Transactional
public class FundingService {

    private final FundingDAO fundingDAO;

    public FundingService() {
        this(null);
    }

    @Autowired
    public FundingService(FundingDAO fundingDAO) {
        this.fundingDAO = fundingDAO;
    }

    public Funding getFundingById(Integer id) {
        return fundingDAO.getFundingById(id);
    }

    public void delete(Funding funding) {
        fundingDAO.delete(funding);
    }

    public void save(Funding funding) {
        fundingDAO.save(funding);
    }
}
