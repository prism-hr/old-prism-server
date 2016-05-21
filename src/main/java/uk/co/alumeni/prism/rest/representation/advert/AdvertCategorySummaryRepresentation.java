package uk.co.alumeni.prism.rest.representation.advert;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

public class AdvertCategorySummaryRepresentation<T> {

    private T id;

    private Long advertCount;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
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
    @SuppressWarnings("unchecked")
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        return equal(id, ((AdvertCategorySummaryRepresentation<T>) object).getId());
    }

}
