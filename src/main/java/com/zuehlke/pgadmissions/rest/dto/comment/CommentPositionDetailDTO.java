package com.zuehlke.pgadmissions.rest.dto.comment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommentPositionDetailDTO {

    @NotNull
    @Size(max = 255)
    private String positionName;

    @NotNull
    @Size(max = 2000)
    private String positionDescription;

    public final String getPositionName() {
        return positionName;
    }

    public final void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public final String getPositionDescription() {
        return positionDescription;
    }

    public final void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

}
