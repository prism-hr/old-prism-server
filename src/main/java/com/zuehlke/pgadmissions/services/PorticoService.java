package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;

@Service
@Transactional
public class PorticoService {

    public List<Comment> getReferencesToSendToPortico(Application application) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Integer> getRefereesToSendToPorticoIds(Application application) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Document> getQualificationsToSendToPortico(Application application) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Integer> getQualificationsToSendToPorticoIds(Application application) {
        // TODO Auto-generated method stub
        return null;
    }

}