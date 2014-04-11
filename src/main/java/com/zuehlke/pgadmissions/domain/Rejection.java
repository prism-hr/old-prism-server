package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "REJECTION")
public class Rejection implements Serializable {

    private static final long serialVersionUID = 6510744657140247807L;

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "rejection")
    private ApplicationForm applicationForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reject_reason_id")
    private RejectReason rejectionReason;

    private boolean includeProspectusLink;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public ApplicationForm getApplicationForm() {
        return applicationForm;
    }

    public void setApplicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
    }

    public RejectReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(RejectReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public boolean isIncludeProspectusLink() {
        return includeProspectusLink;
    }

    public void setIncludeProspectusLink(boolean includeProspectusLink) {
        this.includeProspectusLink = includeProspectusLink;
    }
}
