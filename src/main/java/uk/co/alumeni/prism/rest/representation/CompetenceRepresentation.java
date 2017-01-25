package uk.co.alumeni.prism.rest.representation;

public class CompetenceRepresentation extends TagRepresentation {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompetenceRepresentation withId(final Integer id) {
        setId(id);
        return this;
    }

    public CompetenceRepresentation withName(final String name) {
        setName(name);
        return this;
    }

    public CompetenceRepresentation withDescription(final String description) {
        this.description = description;
        return this;
    }

}
