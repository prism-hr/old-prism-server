package uk.co.alumeni.prism.rest.representation.advert;

public class AdvertCompetenceRepresentation {

    private Integer competenceId;

    private String name;

    private String description;

    private Integer importance;

    public Integer getCompetenceId() {
        return competenceId;
    }

    public void setCompetenceId(Integer competenceId) {
        this.competenceId = competenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public AdvertCompetenceRepresentation withCompetenceId(final Integer competenceId) {
        this.competenceId = competenceId;
        return this;
    }

    public AdvertCompetenceRepresentation withName(final String name) {
        this.name = name;
        return this;
    }

    public AdvertCompetenceRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }

    public AdvertCompetenceRepresentation withImportance(Integer importance) {
        this.importance = importance;
        return this;
    }

}
