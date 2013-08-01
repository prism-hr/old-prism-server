package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;

@Entity(name = "APPLICATION_FORM_UPDATE")
public class ApplicationFormUpdate implements Serializable {

    private static final long serialVersionUID = -5875001675695445368L;

    @Id
    @GeneratedValue
    private Integer id;
    
    @Column(name = "update_visibility")
    private ApplicationUpdateScope updateVisibility;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id")
    private ApplicationForm applicationForm;
    
    @Column(name = "update_timestamp")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updateTimestamp;

    public Integer getId() {
        return id;
    }
    
    public ApplicationFormUpdate() {
        
    }
    
    public ApplicationFormUpdate(ApplicationForm form, ApplicationUpdateScope scope, Date updateTimestamp) {
        this.applicationForm = form;
        this.updateVisibility = scope;
        this.updateTimestamp = updateTimestamp;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ApplicationUpdateScope getUpdateVisibility() {
        return updateVisibility;
    }

    public void setUpdateVisibility(ApplicationUpdateScope updateVisibility) {
        this.updateVisibility = updateVisibility;
    }

    public ApplicationForm getApplicationForm() {
        return applicationForm;
    }

    public void setApplicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
    
    
    
}
