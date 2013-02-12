package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.dao.SourcesOfInterestDAO;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;

@Service
public class SourcesOfInterestService {

    private final SourcesOfInterestDAO sourcesOfInterestDAO;
    
    SourcesOfInterestService(){
        this(null);
    }
    
    @Autowired
    public SourcesOfInterestService(SourcesOfInterestDAO sourcesOfInterestDAO) {
        this.sourcesOfInterestDAO = sourcesOfInterestDAO;     
    }

    public List<SourcesOfInterest> getAllSourcesOfInterest() {
        return sourcesOfInterestDAO.getAllSourcesOfInterest();
    }
    
    public List<SourcesOfInterest> getAllEnabledSourcesOfInterest() {
        return sourcesOfInterestDAO.getAllEnabledSourcesOfInterest();
    }
    
    public SourcesOfInterest getSourcesOfInterestById(Integer id) {
        return sourcesOfInterestDAO.getSourcesOfInterestById(id);
    }
}
