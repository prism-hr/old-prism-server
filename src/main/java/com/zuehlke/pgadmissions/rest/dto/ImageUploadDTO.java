package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.document.PrismFileCategory.PrismImageCategory;

public class ImageUploadDTO {

    private Integer entityId;

    private PrismImageCategory imageCategory;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public PrismImageCategory getImageCategory() {
        return imageCategory;
    }

    public void setImageCategory(PrismImageCategory imageCategory) {
        this.imageCategory = imageCategory;
    }

}
