package com.zuehlke.pgadmissions.domain.advert;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "advert_closing_date", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "closing_date" }) })
public class AdvertClosingDate implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "closing_date", nullable = false)
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

    @Override
    public int hashCode() {
        return Objects.hashCode(advert, closingDate);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final AdvertClosingDate other = (AdvertClosingDate) object;
        return Objects.equal(advert, other.getAdvert()) && Objects.equal(closingDate, other.getClosingDate());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("advert", advert).addProperty("closingDate", closingDate);
    }

}
