package uk.co.alumeni.prism.rest.dto;

import java.util.List;

import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;

public class ReplicableActionSequenceDTO {

    private List<ResourceDTO> resources;

    private List<Integer> templateComments;

    public List<ResourceDTO> getResources() {
        return resources;
    }

    public void setResources(List<ResourceDTO> resources) {
        this.resources = resources;
    }

    public List<Integer> getTemplateComments() {
        return templateComments;
    }

    public void setTemplateComments(List<Integer> templateComments) {
        this.templateComments = templateComments;
    }

}
