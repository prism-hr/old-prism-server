package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;

@Component
public class ApplicationDescriptorProvider {
    private ActionsProvider actionsProvider;
    private ApplicationFormAccessService accessService;
    
    public ApplicationDescriptorProvider(){
        
    }
    
    @Autowired
    public ApplicationDescriptorProvider(ActionsProvider actionsProvider,ApplicationFormAccessService accessService){
        this.actionsProvider = actionsProvider;
        this.accessService = accessService;
    }
    
    public ApplicationDescriptor getApplicationDescriptorForUser(ApplicationForm applicationForm, RegisteredUser user){
        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
        boolean userNeedsToSeeApplicationUpdates = accessService.userNeedsToSeeApplicationUpdates(applicationForm, user);
        applicationDescriptor.setNeedsToSeeUpdate(userNeedsToSeeApplicationUpdates);
        ActionsDefinitions actions = actionsProvider.calculateActions(user, applicationForm);
        applicationDescriptor.setActionsDefinition(actions);
        return applicationDescriptor;
    }
}
