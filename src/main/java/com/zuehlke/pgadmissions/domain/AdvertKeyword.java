package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "ADVERT_KEYWORD", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "keyword" }) })
public class AdvertKeyword implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false, insertable = false, updatable = false)
    private Advert advert;
    
    @Column(name = "keyword", nullable = false)
    private String keyword;

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

    public final String getKeyword() {
        return keyword;
    }

    public final void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("advert", advert);
        properties.put("keyword", keyword);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
