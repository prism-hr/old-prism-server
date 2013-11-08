package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
@Transactional
public class ApplicationFormAccessService extends ApplicationFormUserRoleService {

    @Deprecated
    public void updateAccessTimestamp(ApplicationForm form, RegisteredUser user, Date updateTimestamp) {
    	// This is the deprecated method. We will just remove it.
    }
    
    public void registerApplicationUpdate(ApplicationForm form, Date updateTimestamp, ApplicationUpdateScope applicationUpdateScope) {
        form.setLastUpdated(updateTimestamp);
        super.registerApplicationUpdate(form, updateTimestamp, applicationUpdateScope);
    }
    
    @Deprecated
    public boolean userNeedsToSeeApplicationUpdates(ApplicationForm form, RegisteredUser user) {
    	// This must be deprecated too?
        return true;
    }

}