package uk.co.alumeni.prism.rest.dto.profile;

public class ProfileListFilterDTO {

    private Integer userId;

    private String keyword;

    private String sequenceIdentifier;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public ProfileListFilterDTO withUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

}
