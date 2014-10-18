package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

@Entity
@Table(name = "ADVERT_TARGET_PROGRAM_TYPE", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "program_type" }),
        @UniqueConstraint(columnNames = { "program_type", "advert_id" }) })
public class AdvertTargetProgramType extends AdvertFilterCategory {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", updatable = false, insertable = false)
    private Advert advert;

    @Column(name = "program_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;

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

    public final PrismProgramType getProgramType() {
        return programType;
    }

    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    @Override
    public Object getValue() {
        return getProgramType();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("programType", programType);
    }

}
