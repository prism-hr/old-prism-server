package uk.co.alumeni.prism.dto;

public class ResourceChildCreationDTO extends ResourceIdentityDTO {

    private Boolean createDirectly;

    public Boolean getCreateDirectly() {
        return createDirectly;
    }

    public void setCreateDirectly(Boolean createDirectly) {
        this.createDirectly = createDirectly;
    }

}
