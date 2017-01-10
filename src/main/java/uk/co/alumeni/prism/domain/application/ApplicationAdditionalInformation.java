package uk.co.alumeni.prism.domain.application;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import uk.co.alumeni.prism.domain.profile.ProfileAdditionalInformation;

import javax.persistence.*;

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
    public void setAssociation(Application application) {
        this.association = application;
    }

    @Override
    public String getRequirements() {
        return requirements;
    }

    @Override
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    @Override
    public String getConvictions() {
        return convictions;
    }

    @Override
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
