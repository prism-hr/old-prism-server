package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;

@Service
@Transactional
public class ActionService {

    @Autowired
    private ActionDAO actionDAO;
    
    public Action getById(ApplicationFormAction actionId) {
        return actionDAO.getById(actionId);
    }
    
    public List<ApplicationFormAction> getActionIdByActionType(ActionType actionType) {
        return actionDAO.getActionIdByActionType(actionType);
    }
    
}
