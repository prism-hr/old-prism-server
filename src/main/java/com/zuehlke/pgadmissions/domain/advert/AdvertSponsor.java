package com.zuehlke.pgadmissions.domain.advert;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@Table(name = "ADVERT_SPONSOR")
public class AdvertSponsor {

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;
    
    @ManyToOne
    @JoinColumn(name = "sponsor_id", nullable = false)
    private Institution sponsor;
    
    @Column(name = "sponsorship_committed", nullable = false)
    private BigDecimal sponsorshipCommitted;
    
    @Column(name = "sponsorship_provided", nullable = false)
    private BigDecimal sponsorshipProvided;
    
    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Institution getSponsor() {
        return sponsor;
    }

    public void setSponsor(Institution sponsor) {
        this.sponsor = sponsor;
    }

    public BigDecimal getSponsorshipCommitted() {
        return sponsorshipCommitted;
    }

    public void setSponsorshipCommitted(BigDecimal sponsorshipCommitted) {
        this.sponsorshipCommitted = sponsorshipCommitted;
    }

    public BigDecimal getSponsorshipProvided() {
        return sponsorshipProvided;
    }

    public void setSponsorshipProvided(BigDecimal sponsorshipProvided) {
        this.sponsorshipProvided = sponsorshipProvided;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    
    public AdvertSponsor withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }
    
    public AdvertSponsor withSponsor(Institution sponsor) {
        this.sponsor = sponsor;
        return this;
    }
    
    public AdvertSponsor withSponsorshipCommitted(BigDecimal sponsorshipCommitted) {
        this.sponsorshipCommitted = sponsorshipCommitted;
        return this;
    }
    
    public AdvertSponsor withSponsorshipProvided(BigDecimal sponsorshipProvided) {
        this.sponsorshipProvided = sponsorshipProvided;
        return this;
    }
    
    public AdvertSponsor withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }
    
}
