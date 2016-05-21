package uk.co.alumeni.prism.dto;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;

import com.google.common.base.Objects;

public class AdvertThemeSummaryDTO implements Comparable<AdvertThemeSummaryDTO> {

    private Integer id;

    private String name;

    private Long advertCount;

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
        return equal(id, ((AdvertThemeSummaryDTO) object).getId());
    }

    @Override
    public int compareTo(AdvertThemeSummaryDTO other) {
        return compare(name, other.getName());
    }

}
