package uk.co.alumeni.prism.dto;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Objects;

public abstract class ResourceConnectionAbstractDTO implements Comparable<ResourceConnectionAbstractDTO> {

    public abstract Integer getInstitutionId();

    public abstract void setInstitutionId(Integer institutionId);

    public abstract String getInstitutionName();

    public abstract void setInstitutionName(String institutionName);

    public abstract Integer getInstitutionLogoImageId();

    public abstract void setInstitutionLogoImageId(Integer institutionLogoImageId);

    public abstract Integer getDepartmentId();

    public abstract void setDepartmentId(Integer departmentId);

    public abstract String getDepartmentName();

    public abstract void setDepartmentName(String departmentName);

    @Override
    public int hashCode() {
        return Objects.hashCode(getInstitutionId(), getDepartmentId());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceConnectionDTO other = (ResourceConnectionDTO) object;
        return Objects.equal(getInstitutionId(), other.getInstitutionId()) && Objects.equal(getDepartmentId(), other.getDepartmentId());
    }

    @Override
    public int compareTo(ResourceConnectionAbstractDTO other) {
        int compare = ObjectUtils.compare(getInstitutionName(), other.getInstitutionName());
        compare = compare == 0 ? ObjectUtils.compare(getDepartmentName(), other.getDepartmentName()) : compare;

        if (this.getClass().equals(UnverifiedUserDTO.class) && other.getClass().equals(UnverifiedUserDTO.class)) {
            UnverifiedUserDTO thisUpcast = (UnverifiedUserDTO) this;
            UnverifiedUserDTO otherUpcast = (UnverifiedUserDTO) other;

            compare = ObjectUtils.compare(thisUpcast.getUserFirstName(), otherUpcast.getUserFirstName());
            return compare == 0 ? ObjectUtils.compare(thisUpcast.getUserLastName(), otherUpcast.getUserLastName()) : compare;
        }

        return compare;
    }

}
