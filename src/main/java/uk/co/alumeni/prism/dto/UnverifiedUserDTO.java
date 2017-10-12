package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;

public class UnverifiedUserDTO extends ResourceConnectionDTO {

    private Integer userId;

    private String userFirstName;

    private String userLastName;

    private String userEmail;

    private String userLinkedinProfileUrl;

    private String userLinkedinImageUrl;

    private Integer userPortraitImageId;

    private PrismRole roleId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserLinkedinProfileUrl() {
        return userLinkedinProfileUrl;
    }

    public void setUserLinkedinProfileUrl(String userLinkedinProfileUrl) {
        this.userLinkedinProfileUrl = userLinkedinProfileUrl;
    }

    public String getUserLinkedinImageUrl() {
        return userLinkedinImageUrl;
    }

    public void setUserLinkedinImageUrl(String userLinkedinImageUrl) {
        this.userLinkedinImageUrl = userLinkedinImageUrl;
    }

    public Integer getUserPortraitImageId() {
        return userPortraitImageId;
    }

    public void setUserPortraitImageId(Integer userPortraitImageId) {
        this.userPortraitImageId = userPortraitImageId;
    }

    public PrismRole getRoleId() {
        return roleId;
    }

    public void setRoleId(PrismRole roleId) {
        this.roleId = roleId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), userId, roleId);
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            UnverifiedUserDTO other = (UnverifiedUserDTO) object;
            return Objects.equal(userId, other.getUserId()) && Objects.equal(roleId, other.getRoleId());
        }
        return false;
    }

}
