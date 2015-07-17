package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.Competence;

import javax.persistence.*;

@Entity
@Table(name = "advert_competence", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "competence_id" }) })
public class AdvertCompetence extends AdvertTarget<Competence> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence value;

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

    @Override
    public Competence getValue() {
        return value;
    }

    @Override
    public void setValue(Competence competence) {
        this.value = competence;
    }

    @Override
    public String getTitle() {
        return value.getTitle();
    }

}
