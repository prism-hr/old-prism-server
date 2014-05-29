package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

@Entity
@Table(name = "ADVERT_CLOSING_DATE")
public class AdvertClosingDate implements Serializable {

    private static final long serialVersionUID = -1883742652445622591L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advert_id", nullable = false, updatable = false, insertable = false)
    private Advert advert;

    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate;

    @Column(name = "study_places")
    private Integer studyPlaces;

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public Integer getStudyPlaces() {
        return studyPlaces;
    }

    public void setStudyPlaces(Integer studyPlaces) {
        this.studyPlaces = studyPlaces;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AdvertClosingDate withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public AdvertClosingDate withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }
    
    public AdvertClosingDate withClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
        return this;
    }
    
    public AdvertClosingDate withStudyPlaces(Integer studyPlaces) {
        this.studyPlaces = studyPlaces;
        return this;
    }
}
