package uk.co.alumeni.prism.rest.representation.resource;

import java.util.List;

public class ResourceRepresentationMetadataUserRelated {

    private String label;

    private List<String> users;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public ResourceRepresentationMetadataUserRelated withLabel(String label) {
        this.label = label;
        return this;
    }

    public ResourceRepresentationMetadataUserRelated withUsers(List<String> users) {
        this.users = users;
        return this;
    }

}
