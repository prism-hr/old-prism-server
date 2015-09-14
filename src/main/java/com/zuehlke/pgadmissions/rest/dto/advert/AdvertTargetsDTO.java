package com.zuehlke.pgadmissions.rest.dto.advert;

import java.util.List;

public class AdvertTargetsDTO {

    private List<AdvertTargetResourceDTO> resources;

    private List<AdvertTargetResourceDTO> selectedResources;

    public List<AdvertTargetResourceDTO> getResources() {
        return resources;
    }

    public void setResources(List<AdvertTargetResourceDTO> resources) {
        this.resources = resources;
    }

    public List<AdvertTargetResourceDTO> getSelectedResources() {
        return selectedResources;
    }

    public void setSelectedResources(List<AdvertTargetResourceDTO> selectedResources) {
        this.selectedResources = selectedResources;
    }

    public AdvertTargetsDTO withSelectedResources(List<AdvertTargetResourceDTO> selectedResources) {
        this.selectedResources = selectedResources;
        return this;
    }

}
