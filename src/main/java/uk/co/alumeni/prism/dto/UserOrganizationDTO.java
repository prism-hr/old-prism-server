package uk.co.alumeni.prism.dto;

import static com.google.common.base.Objects.equal;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;

import com.google.common.base.Objects;

public class UserOrganizationDTO {

    private Integer userId;

    private Integer departmentId;

    private String departmentName;

    private Integer institutionId;

    private String institutionName;

    private PrismDomicile domicileId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public PrismDomicile getDomicileId() {
        return domicileId;
    }

    public void setDomicileId(PrismDomicile domicileId) {
        this.domicileId = domicileId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, departmentId, institutionId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        UserOrganizationDTO other = (UserOrganizationDTO) object;
        return equal(userId, other.getUserId()) && equal(departmentId, other.getDepartmentId()) && equal(institutionId, other.getInstitutionId());
    }

}
