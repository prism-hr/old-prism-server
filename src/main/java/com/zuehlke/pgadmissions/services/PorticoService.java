package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ReferenceComment;

@Service
@Transactional
public class PorticoService {

    public List<ReferenceComment> getReferencesToSendToPortico(ApplicationForm application) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Integer> getRefereesToSendToPorticoIds(ApplicationForm application) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Document> getQualificationsToSendToPortico(ApplicationForm application) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Integer> getQualificationsToSendToPorticoIds(ApplicationForm application) {
        // TODO Auto-generated method stub
        return null;
    }

}