package com.zuehlke.pgadmissions.rest.dto.comment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommentApplicationPositionDetailDTO {

    @NotNull
    @Size(max = 255)
    private String positionTitle;

    @NotNull
    @Size(max = 2000)
    private String positionDescription;

    public final String getPositionTitle() {
        return positionTitle;
    }

    public final void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public final String getPositionDescription() {
        return positionDescription;
    }

    public final void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

}