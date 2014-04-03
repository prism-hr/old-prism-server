package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ReferenceComment;

@Service
@Transactional
public class PorticoService {

    public List<ReferenceComment> getReferencesToSendToPortico() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Integer> getReferencesToSendToPorticoIds() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Document> getQualificationsToSendToPortico() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Integer> getQualificationsToSendToPorticoIds() {
        // TODO Auto-generated method stub
        return null;
    }

}