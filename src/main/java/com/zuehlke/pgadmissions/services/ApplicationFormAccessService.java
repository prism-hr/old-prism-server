package com.zuehlke.pgadmissions.services;

import static java.util.Arrays.asList;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormLastAccessDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormUpdateDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Transactional
@Service
public class ApplicationFormAccessService extends ApplicationFormUserRoleService {

    @Autowired
    private ApplicationFormUpdateDAO applicationFormUpdateDAO;

    @Autowired
    private ApplicationFormLastAccessDAO applicationFormLastAccessDAO;

    public void updateAccessTimestamp(ApplicationForm form, RegisteredUser user, Date timestamp) {
    	// The date parameter is not doing anything here. We can remove it when we finish the integration
        deregisterApplicationUpdate(form, user);
    }

    public boolean userNeedsToSeeApplicationUpdates(ApplicationForm form, RegisteredUser user) {
    	// This is doing nothing at all now. We handle it through the new functions
        List<ApplicationFormUpdate> missedUpdates = applicationFormUpdateDAO.getUpdatesForUser(form, user);

        if (missedUpdates == null || missedUpdates.isEmpty()) {
            return false;
        }
        
        Set<Authority> internalGroup = new HashSet<Authority>();
        internalGroup.addAll(asList(Authority.ADMITTER, Authority.ADMINISTRATOR));
        
        for (ApplicationFormUpdate update : missedUpdates) {
            if (update.getUpdateVisibility()==ApplicationUpdateScope.ALL_USERS) {
                return true;
            }
            else {
                for (Authority authority : user.getAuthoritiesForProgram(form.getProgram())) {
                    if (internalGroup.contains(authority)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
