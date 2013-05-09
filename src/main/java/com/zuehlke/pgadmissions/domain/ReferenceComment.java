package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "REFERENCE_COMMENT")
public class ReferenceComment extends Comment {

    private static final long serialVersionUID = 5269362387094590530L;

    @Column(name = "suitable_for_UCL")
    private Boolean suitableForUCL;

    @Column(name = "suitable_for_Programme")
    private Boolean suitableForProgramme;

    @OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "referee_id")
    private Referee referee;

    @Column(name = "updated_time_stamp", insertable = false)
    @Generated(GenerationTime.ALWAYS)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private CommentType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provided_by")
    private RegisteredUser providedBy;
    
    @Transient
    private String alert;

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public boolean isSuitableForUCLSet() {
        return suitableForUCL != null;
    }

    public boolean isSuitableForProgrammeSet() {
        return suitableForProgramme != null;
    }

    public Referee getReferee() {
        return referee;
    }

    public void setReferee(Referee referee) {
        this.referee = referee;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Boolean getSuitableForUCL() {
        return suitableForUCL;
    }

    public void setSuitableForUCL(Boolean suitableForUCL) {
        this.suitableForUCL = suitableForUCL;
    }

    public Boolean getSuitableForProgramme() {
        return suitableForProgramme;
    }

    public void setSuitableForProgramme(Boolean suitableForProgramme) {
        this.suitableForProgramme = suitableForProgramme;
    }

    public RegisteredUser getProvidedBy() {
        return providedBy;
    }

    public void setProvidedBy(RegisteredUser providedBy) {
        this.providedBy = providedBy;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
    
}
