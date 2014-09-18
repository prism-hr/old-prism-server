package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

@Entity
@Table(name = "ADVERT_TARGET", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "institution_id", "program_type" }) })
public class AdvertTarget implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false, insertable = false, updatable = false)
    private Advert advert;
    
    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;
    
    @Column(name = "program_type")
    @Enumerated(EnumType.STRING)
    private PrismProgramType programType;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Advert getAdvert() {
        return advert;
    }

    public final void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public final Institution getInstitution() {
        return institution;
    }

    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public final PrismProgramType getProgramType() {
        return programType;
    }

    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("advert", advert);
        properties.put("institution", institution);
        properties.put("programType", programType);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
