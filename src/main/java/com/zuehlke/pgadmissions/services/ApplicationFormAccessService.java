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
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Transactional
@Service
public class ApplicationFormAccessService {

    @Autowired
    private ApplicationFormUpdateDAO applicationFormUpdateDAO;

    @Autowired
    private ApplicationFormLastAccessDAO applicationFormLastAccessDAO;

    public void updateAccessTimestamp(ApplicationForm form, RegisteredUser user, Date timestamp) {
        ApplicationFormLastAccess access = applicationFormLastAccessDAO.getLastAccess(form, user);
        if (access == null) {
            access = new ApplicationFormLastAccess();
            access.setApplicationForm(form);
            access.setUser(user);
        }
        access.setLastAccessTimestamp(timestamp);
        applicationFormLastAccessDAO.saveAccess(access);
    }

    public boolean userNeedsToSeeApplicationUpdates(ApplicationForm form, RegisteredUser user) {
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
