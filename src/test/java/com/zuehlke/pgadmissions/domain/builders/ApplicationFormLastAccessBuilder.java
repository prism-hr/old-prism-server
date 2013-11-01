package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormLastAccess;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ApplicationFormLastAccessBuilder {
    
    private Integer id;
    private RegisteredUser user;
    private ApplicationForm applicationForm;
    private Date lastAccessTimestamp;
    
    public ApplicationFormLastAccessBuilder id(Integer id) {
        this.id = id;
        return this;
    }
    
    public ApplicationFormLastAccessBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }
    
    public ApplicationFormLastAccessBuilder applicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
        return this;
    }
    
    public ApplicationFormLastAccessBuilder lastAccessTimestamp(Date lastAccessTimestamp) {
        this.lastAccessTimestamp = lastAccessTimestamp;
        return this;
    }
    
    public ApplicationFormLastAccess build() {
        ApplicationFormLastAccess result = new ApplicationFormLastAccess();
        result.setId(id);
        result.setUser(user);
        result.setApplicationForm(applicationForm);
        result.setLastAccessTimestamp(lastAccessTimestamp);
        return result;
    }
    

}
