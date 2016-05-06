package uk.co.alumeni.prism.rest.representation;

public class TagRepresentation {

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagRepresentation withId(final Integer id) {
        this.id = id;
        return this;
    }

    public TagRepresentation withName(final String name) {
        this.name = name;
        return this;
    }

}
