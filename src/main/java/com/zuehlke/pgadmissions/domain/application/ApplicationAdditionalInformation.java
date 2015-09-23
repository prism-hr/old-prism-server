package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.profile.ProfileAdditionalInformation;

@Entity
@Table(name = "application_additional_information")
public class ApplicationAdditionalInformation extends ApplicationSection implements ProfileAdditionalInformation<Application> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "additionalInformation")
    private Application association;

    @Lob
    @Column(name = "requirements")
    private String requirements;

    @Lob
    @Column(name = "convictions")
    private String convictions;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Application getAssociation() {
        return association;
    }

    public void setAssociation(Application application) {
        this.association = application;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getConvictions() {
        return convictions;
    }

    public void setConvictions(String convictions) {
        this.convictions = convictions;
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
