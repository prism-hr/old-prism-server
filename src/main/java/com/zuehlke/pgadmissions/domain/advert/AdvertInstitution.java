package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.*;

@Entity
@Table(name = "ADVERT_INSTITUTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "institution_id" }),
        @UniqueConstraint(columnNames = { "institution_id", "advert_id" }) })
public class AdvertInstitution extends AdvertFilterCategory {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", updatable = false, insertable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

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

    public final Institution getInstitution() {
        return institution;
    }

    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public Object getValue() {
        return getInstitution();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("institution", institution);
    }

}
