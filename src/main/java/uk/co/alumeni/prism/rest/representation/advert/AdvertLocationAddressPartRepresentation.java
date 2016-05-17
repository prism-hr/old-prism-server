package uk.co.alumeni.prism.rest.representation.advert;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Objects;

public class AdvertLocationAddressPartRepresentation implements Comparable<AdvertLocationAddressPartRepresentation> {

    private Integer id;

    private String name;

    private Long advertCount;

    private List<AdvertLocationAddressPartRepresentation> subParts;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAdvertCount() {
        return advertCount;
    }

    public void setAdvertCount(Long advertCount) {
        this.advertCount = advertCount;
    }

    public List<AdvertLocationAddressPartRepresentation> getSubParts() {
        return subParts;
    }

    public void setSubParts(List<AdvertLocationAddressPartRepresentation> subParts) {
        this.subParts = subParts;
    }

    public AdvertLocationAddressPartRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertLocationAddressPartRepresentation withName(String name) {
        this.name = name;
        return this;
    }

    public AdvertLocationAddressPartRepresentation withAdvertCount(Long advertCount) {
        this.advertCount = advertCount;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        return Objects.equal(id, ((AdvertLocationAddressPartRepresentation) object).getId());
    }

    @Override
    public int compareTo(AdvertLocationAddressPartRepresentation other) {
        return ObjectUtils.compare(name, other.getName());
    }

}
