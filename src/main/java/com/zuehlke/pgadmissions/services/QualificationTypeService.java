package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
import com.zuehlke.pgadmissions.domain.QualificationType;

@Service
public class QualificationTypeService {

    private final QualificationTypeDAO qualificationTypeDAO;
    
    public QualificationTypeService() {
        this(null);
    }
    
    @Autowired
    public QualificationTypeService(QualificationTypeDAO qualificationTypeDAO) {
        this.qualificationTypeDAO = qualificationTypeDAO;
    }

    @Transactional
    public QualificationType getQualificationTypeById(Integer id) {
        return qualificationTypeDAO.getQualificationTypeById(id);
    }
}
