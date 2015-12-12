package uk.co.alumeni.prism.dto;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Objects;

import uk.co.alumeni.prism.domain.definitions.PrismAddressLocationPartType;

public class AdvertLocationAddressPartSummaryDTO implements Comparable<AdvertLocationAddressPartSummaryDTO> {

    private Integer id;

    private Integer parentId;

    private PrismAddressLocationPartType type;

    private String name;

    private Long advertCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public PrismAddressLocationPartType getType() {
        return type;
    }

    public void setType(PrismAddressLocationPartType type) {
        this.type = type;
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
        return Objects.equal(id, ((AdvertLocationAddressPartSummaryDTO) object).getId());
    }

    @Override
    public int compareTo(AdvertLocationAddressPartSummaryDTO other) {
        return ObjectUtils.compare(name, other.getName());
    }

}
