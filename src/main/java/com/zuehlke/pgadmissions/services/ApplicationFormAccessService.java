package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormLastAccessDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Transactional
@Service
public class ApplicationFormAccessService {

    @Autowired
    private ApplicationFormLastAccessDAO applicationFormLastAccessDAO;
    
    private Date getLastAccessTimestamp(ApplicationForm form, RegisteredUser user) {
        ApplicationFormLastAccess access = applicationFormLastAccessDAO.getLastAccess(form, user);
        if (access!=null) {
            return access.getLastAccessTimestamp();
        }
        return null;
    }
    
    public void updateAccessTimestamp(ApplicationForm form, RegisteredUser user, Date timestamp) {
        ApplicationFormLastAccess access = applicationFormLastAccessDAO.getLastAccess(form, user);
        if (access==null) {
            access = new ApplicationFormLastAccess();
            access.setApplicationForm(form);
            access.setUser(user);
        }
        access.setLastAccessTimestamp(timestamp);
        applicationFormLastAccessDAO.saveAccess(access);
    }
    
    public boolean userNeedsToSeeApplicationUpdates(ApplicationForm form, RegisteredUser user) {
        Date lastAccessByUser = getLastAccessTimestamp(form, user);
        Date lastModified = form.getLastUpdated();
        if (lastAccessByUser == null || lastModified == null) {
            return true;
        }
        return lastAccessByUser.before(lastModified);
    }
}
