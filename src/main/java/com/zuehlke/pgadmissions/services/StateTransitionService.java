package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

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
    
    public String resolveView(final ApplicationForm form, final String action) {
        return viewResolver.resolveView(form, action);
    }
    
    public List<ApplicationFormStatus> getAvailableNextStati(final ApplicationFormStatus status) {
        ArrayList<ApplicationFormStatus> nextStatuses = new ArrayList<ApplicationFormStatus>();
        switch (status) {
        case APPROVAL:
            nextStatuses.add(ApplicationFormStatus.REVIEW);
            nextStatuses.add(ApplicationFormStatus.INTERVIEW);
            nextStatuses.add(ApplicationFormStatus.APPROVAL);
            nextStatuses.add(ApplicationFormStatus.APPROVED);
            nextStatuses.add(ApplicationFormStatus.REJECTED);
            break;
        case APPROVED:
            break;
        case INTERVIEW:
            nextStatuses.add(ApplicationFormStatus.REVIEW);
            nextStatuses.add(ApplicationFormStatus.INTERVIEW);
            nextStatuses.add(ApplicationFormStatus.APPROVAL);
            nextStatuses.add(ApplicationFormStatus.REJECTED);
            break;
        case REJECTED:
            break;
        case REVIEW:
            nextStatuses.add(ApplicationFormStatus.REVIEW);
            nextStatuses.add(ApplicationFormStatus.INTERVIEW);
            nextStatuses.add(ApplicationFormStatus.APPROVAL);
            nextStatuses.add(ApplicationFormStatus.REJECTED);
            break;
        case UNSUBMITTED:
            break;
        case VALIDATION:
            nextStatuses.add(ApplicationFormStatus.REVIEW);
            nextStatuses.add(ApplicationFormStatus.INTERVIEW);
            nextStatuses.add(ApplicationFormStatus.APPROVAL);
            nextStatuses.add(ApplicationFormStatus.REJECTED);
            break;
        case WITHDRAWN:
            break;
        default:
            break;
        }
        return nextStatuses;
    }
}