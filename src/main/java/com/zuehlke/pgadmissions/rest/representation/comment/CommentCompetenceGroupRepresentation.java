package com.zuehlke.pgadmissions.rest.representation.comment;

import java.util.List;

public class CommentCompetenceGroupRepresentation {

    private Integer importance;

    private List<CommentCompetenceRepresentation> competences;

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public List<CommentCompetenceRepresentation> getCompetences() {
        return competences;
    }

    public void setCompetences(List<CommentCompetenceRepresentation> competences) {
        this.competences = competences;
    }

    public CommentCompetenceGroupRepresentation withCompetences(final List<CommentCompetenceRepresentation> competences) {
        this.competences = competences;
        return this;
    }

    public CommentCompetenceGroupRepresentation withImportance(final Integer importance) {
        this.importance = importance;
        return this;
    }


}
