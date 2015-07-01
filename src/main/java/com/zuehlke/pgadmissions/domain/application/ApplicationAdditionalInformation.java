package com.zuehlke.pgadmissions.domain.application;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "application_additional_information")
public class ApplicationAdditionalInformation extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "convictions_text")
    private String convictionsText;

    @OneToOne(mappedBy = "additionalInformation")
    private Application application;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationAdditionalInformation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationAdditionalInformation withConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
        return this;
    }

    public ApplicationAdditionalInformation withApplication(Application application) {
        this.application = application;
        return this;
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
