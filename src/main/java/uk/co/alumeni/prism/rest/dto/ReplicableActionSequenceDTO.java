package uk.co.alumeni.prism.rest.dto;

import java.util.List;

public class ReplicableActionSequenceDTO {

    private List<Integer> resources;

    private List<Integer> templateComments;

    public List<Integer> getResources() {
        return resources;
    }

    public void setResources(List<Integer> resources) {
        this.resources = resources;
    }

    public List<Integer> getTemplateComments() {
        return templateComments;
    }

    public void setTemplateComments(List<Integer> templateComments) {
        this.templateComments = templateComments;
    }

}
