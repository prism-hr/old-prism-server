package uk.co.alumeni.prism.rest.dto.resource;

public class ResourceListFilterTagDTO {

    private Integer id;

    private String value;

    public ResourceListFilterTagDTO(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
