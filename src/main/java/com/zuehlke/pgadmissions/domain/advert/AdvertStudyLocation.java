package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.UniqueEntity;

@Entity
@Table(name = "advert_study_location", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "study_location" }) })
public class AdvertStudyLocation implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id")
    private Advert advert;

    @Column(name = "study_location", nullable = false)
    private String studyLocation;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public final String getStudyLocation() {
        return studyLocation;
    }

    public final void setStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
    }

    public AdvertStudyLocation withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertStudyLocation withStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("advert", "advert").addProperty("studyLocation", studyLocation);
    }

}
