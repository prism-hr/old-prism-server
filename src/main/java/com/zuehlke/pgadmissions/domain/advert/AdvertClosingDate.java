package com.zuehlke.pgadmissions.domain.advert;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "advert_closing_date", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "closing_date" }) })
public class AdvertClosingDate extends AdvertAttribute<LocalDate> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "closing_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate value;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public LocalDate getValue() {
        return value;
    }

    @Override
    public void setValue(LocalDate value) {
        this.value = value;
    }

}
