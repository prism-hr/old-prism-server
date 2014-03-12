package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class StageDurationService {

    private final StageDurationDAO durationDAO;
    
    public StageDurationService() {
        this(null);
    }
    
    @Transactional
    public void save(StageDuration stageDuration) {
        durationDAO.save(stageDuration);
    }
    
    @Autowired
    public StageDurationService(StageDurationDAO durationDAO) {
        this.durationDAO = durationDAO;
    }

    @Transactional
    public StageDuration getByStatus(ApplicationFormStatus stage) {
        return durationDAO.getByStatus(stage);
    }

}
