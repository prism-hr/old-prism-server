package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramClosingDateDAO;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;

@Service
@Transactional
public class ProgramClosingDateService {
    
    @Autowired
    private ProgramClosingDateDAO programClosingDateDAO;
    
    public void updateClosingDate(ProgramClosingDate closingDate) {
        programClosingDateDAO.updateClosingDate(closingDate);
    }
    
    public void deleteClosingDateById(Integer programClosingDateId) {
        ProgramClosingDate programClosingDate = programClosingDateDAO.getById(programClosingDateId);
        programClosingDateDAO.deleteClosingDate(programClosingDate);
    }
    
}
