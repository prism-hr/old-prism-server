package com.zuehlke.pgadmissions.rest.dto.profile;

public class ProfileListFilterDTO {

    private String keyword;

    private String sequenceIdentifier;

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

}
