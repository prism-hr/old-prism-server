package uk.co.alumeni.prism.dto;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

public class ResourceAdvertDTO {

    private Integer resourceId;

    private Integer advertId;

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resourceId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceAdvertDTO other = (ResourceAdvertDTO) object;
        return equal(resourceId, other.getResourceId());
    }

}
