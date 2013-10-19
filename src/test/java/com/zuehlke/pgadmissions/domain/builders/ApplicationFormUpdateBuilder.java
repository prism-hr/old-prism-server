package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;

public class ApplicationFormUpdateBuilder {

    private Integer id;

    private ApplicationUpdateScope updateVisibility;

    private ApplicationForm applicationForm;

    private Date updateTimestamp;
    
    public ApplicationFormUpdateBuilder id(Integer id) {
        this.id = id;
        return this;
    }
    
    public ApplicationFormUpdateBuilder updateVisibility(ApplicationUpdateScope updateVisibility) {
        this.updateVisibility = updateVisibility;
        return this;
    }
    public ApplicationFormUpdateBuilder applicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
        return this;
    }
    public ApplicationFormUpdateBuilder updateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
        return this;
    }
    
    public ApplicationFormUpdate build() {
        ApplicationFormUpdate update = new ApplicationFormUpdate();
        update.setId(this.id);
        update.setUpdateVisibility(this.updateVisibility);
        update.setApplicationForm(this.applicationForm);
        update.setUpdateTimestamp(this.updateTimestamp);
        return update;
    }

}
