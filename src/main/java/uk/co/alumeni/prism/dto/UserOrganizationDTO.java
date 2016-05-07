package uk.co.alumeni.prism.dto;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;

import org.joda.time.DateTime;

import com.google.common.base.Objects;

public class UserOrganizationDTO implements Comparable<UserOrganizationDTO> {

    private Integer userId;

    private Integer departmentId;

    private String departmentName;

    private Integer institutionId;

    private String institutionName;

    private Integer institutionLogoImageId;

    private DateTime acceptedTimestamp;

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

    public Integer getInstitutionLogoImageId() {
        return institutionLogoImageId;
    }

    public void setInstitutionLogoImageId(Integer institutionLogoImageId) {
        this.institutionLogoImageId = institutionLogoImageId;
    }

    public DateTime getAcceptedTimestamp() {
        return acceptedTimestamp;
    }

    public void setAcceptedTimestamp(DateTime acceptedTimestamp) {
        this.acceptedTimestamp = acceptedTimestamp;
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

    @Override
    public int compareTo(UserOrganizationDTO other) {
        int compare = compare(userId, other.getUserId());
        compare = compare == 0 ? compare(other.getAcceptedTimestamp(), acceptedTimestamp) : compare;
        compare = compare == 0 ? compare(other.getDepartmentId(), departmentId) : compare;
        return compare == 0 ? compare(other.getInstitutionId(), institutionId) : compare;
    }

}
