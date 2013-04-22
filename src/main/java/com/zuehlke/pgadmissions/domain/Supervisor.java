package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;

@Entity(name = "SUPERVISOR")
public class Supervisor implements Serializable {

    private static final long serialVersionUID = -189828903532203309L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "registered_user_id")
    @Valid
    private RegisteredUser user;

    @Column(name = "last_notified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastNotified;

    @Column(name = "confirmed_supervision_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedSupervisionDate;

    @ManyToOne
    @JoinColumn(name = "approval_round_id", insertable = false, updatable = false)
    private ApprovalRound approvalRound;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "confirmed_supervision")
    private Boolean confirmedSupervision;
    
    @Column(name = "declined_supervision_reason")
    private String declinedSupervisionReason;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public Date getLastNotified() {
        return lastNotified;
    }

    public void setLastNotified(Date lastNotified) {
        this.lastNotified = lastNotified;
    }

    public ApprovalRound getApprovalRound() {
        return approvalRound;
    }

    public void setApprovalRound(ApprovalRound approvalRound) {
        this.approvalRound = approvalRound;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public Boolean getConfirmedSupervision() {
        return confirmedSupervision;
    }

    public void setConfirmedSupervision(Boolean confirmedSupervision) {
        this.confirmedSupervision = confirmedSupervision;
    }

    public String getDeclinedSupervisionReason() {
        return declinedSupervisionReason;
    }

    public void setDeclinedSupervisionReason(String declinedSupervisionReason) {
        this.declinedSupervisionReason = declinedSupervisionReason;
    }

    public Date getConfirmedSupervisionDate() {
        return confirmedSupervisionDate;
    }

    public void setConfirmedSupervisionDate(Date confirmedSupervisionDate) {
        this.confirmedSupervisionDate = confirmedSupervisionDate;
    }

    public boolean hasDeclinedSupervision(){
        return declinedSupervisionReason != null;
    }
    
    public boolean hasResponded(){
        return BooleanUtils.isTrue(getConfirmedSupervision()) || hasDeclinedSupervision();
    }
}
