package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class StateTransitionService {

    private final StateTransitionViewResolver viewResolver;

    public StateTransitionService() {
        this(null);
    }
    
    @Autowired
    public StateTransitionService(final StateTransitionViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }
    
    public String resolveView(final ApplicationForm form) {
        return viewResolver.resolveView(form);
    }
    
    public ApplicationFormStatus[] getAvailableNextStati(final ApplicationFormStatus status) {
        ArrayList<ApplicationFormStatus> nextStati = new ArrayList<ApplicationFormStatus>();
        switch (status) {
        case APPROVAL:
            nextStati.add(ApplicationFormStatus.REVIEW);
            nextStati.add(ApplicationFormStatus.INTERVIEW);
            nextStati.add(ApplicationFormStatus.APPROVED);
            nextStati.add(ApplicationFormStatus.REJECTED);
            nextStati.add(ApplicationFormStatus.REQUEST_RESTART_APPROVAL);
            break;
        case APPROVED:
            break;
        case INTERVIEW:
            nextStati.add(ApplicationFormStatus.REVIEW);
            nextStati.add(ApplicationFormStatus.INTERVIEW);
            nextStati.add(ApplicationFormStatus.APPROVAL);
            nextStati.add(ApplicationFormStatus.REJECTED);
            break;
        case REJECTED:
            break;
        case REQUEST_RESTART_APPROVAL:
            break;
        case REVIEW:
            nextStati.add(ApplicationFormStatus.REVIEW);
            nextStati.add(ApplicationFormStatus.INTERVIEW);
            nextStati.add(ApplicationFormStatus.APPROVAL);
            nextStati.add(ApplicationFormStatus.REJECTED);
            break;
        case UNSUBMITTED:
            break;
        case VALIDATION:
            nextStati.add(ApplicationFormStatus.REVIEW);
            nextStati.add(ApplicationFormStatus.INTERVIEW);
            nextStati.add(ApplicationFormStatus.APPROVAL);
            nextStati.add(ApplicationFormStatus.REJECTED);
            break;
        case WITHDRAWN:
            break;
        default:
            break;
        }
        return nextStati.toArray(new ApplicationFormStatus[] {});
    }
}
