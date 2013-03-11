package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ReferenceDAO;
import com.zuehlke.pgadmissions.domain.ReferenceComment;

@Service
@Transactional
public class ReferenceService {

    private final ReferenceDAO referenceDAO;

    public ReferenceService() {
        this(null);
    }

    @Autowired
    public ReferenceService(ReferenceDAO referenceDAO) {
        this.referenceDAO = referenceDAO;
    }

    public ReferenceComment getReferenceById(Integer id) {
        return referenceDAO.getReferenceById(id);
    }

}
