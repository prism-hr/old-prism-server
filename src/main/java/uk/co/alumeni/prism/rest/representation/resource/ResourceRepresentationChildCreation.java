package uk.co.alumeni.prism.rest.representation.resource;

public class ResourceRepresentationChildCreation extends ResourceRepresentationIdentity {

    private Boolean createDirectly;

    public Boolean isCreateDirectly() {
        return createDirectly;
    }

    public void setCreateDirectly(Boolean createDirectly) {
        this.createDirectly = createDirectly;
    }

}
