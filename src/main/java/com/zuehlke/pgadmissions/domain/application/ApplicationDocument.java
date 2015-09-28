package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.profile.ProfileDocument;

@Entity
@Table(name = "application_document")
public class ApplicationDocument extends ApplicationSection implements ProfileDocument<Application> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "document")
    private Application association;

    @Column(name = "personal_summary")
    private String personalSummary;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cv_id", unique = true)
    private Document cv;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "covering_letter_id", unique = true)
    private Document coveringLetter;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Application getAssociation() {
        return association;
    }

    @Override
    public void setAssociation(Application association) {
        this.association = association;
    }

    @Override
    public String getPersonalSummary() {
        return personalSummary;
    }

    @Override
    public void setPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
    }

    @Override
    public Document getCv() {
        return cv;
    }

    @Override
    public void setCv(Document cv) {
        this.cv = cv;
    }

    public final Document getCoveringLetter() {
        return coveringLetter;
    }

    public final void setCoveringLetter(Document coveringLetter) {
        this.coveringLetter = coveringLetter;
    }

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

}
