package com.zuehlke.pgadmissions.rest.representation;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class SubjectAreaRepresentation {

    private Integer id;

    private String name;

    private Set<SubjectAreaRepresentation> children = new TreeSet<>((c1, c2) -> c1.getId().compareTo(c2.getId()));

    public SubjectAreaRepresentation(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<SubjectAreaRepresentation> getChildren() {
        return children;
    }

    public SubjectAreaRepresentation withName(String name) {
        this.name = name;
        return this;
    }

    public SubjectAreaRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public SubjectAreaRepresentation addChild(SubjectAreaRepresentation child) {
        children.add(child);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectAreaRepresentation that = (SubjectAreaRepresentation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
