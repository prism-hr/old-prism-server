package uk.co.alumeni.prism.dto;

public class AdvertUserDTO {

    private Integer advertId;

    private Integer userId;

    private String userFirstName;

    private String userLastName;

    private String userEmail;

    private String userLinkedinProfileUrl;

    private String userLinkedinImageUrl;

    private Integer userPortraitImageId;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

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

}
