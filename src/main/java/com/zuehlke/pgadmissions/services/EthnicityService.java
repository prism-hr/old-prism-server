package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.EthnicityDAO;
import com.zuehlke.pgadmissions.domain.Ethnicity;

@Service
@Transactional
public class EthnicityService {

    private EthnicityDAO ethnicityDAO;

    public EthnicityService() {
        this(null);
    }

    @Autowired
    public EthnicityService(EthnicityDAO ethnicityDAO) {
        this.ethnicityDAO = ethnicityDAO;
    }

    public List<Ethnicity> getAllEthnicities() {
        return ethnicityDAO.getAllEthnicities();
    }

    public List<Ethnicity> getAllEnabledEthnicities() {
        return ethnicityDAO.getAllEnabledEthnicities();
    }

    public Ethnicity getEthnicityById(Integer ethnicityId) {
        return ethnicityDAO.getEthnicityById(ethnicityId);
    }
}
