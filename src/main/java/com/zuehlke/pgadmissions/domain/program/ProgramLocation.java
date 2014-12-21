package com.zuehlke.pgadmissions.domain.program;

import com.google.common.base.Objects;

import javax.persistence.*;

@Entity
@Table(name = "PROGRAM_LOCATION", uniqueConstraints = @UniqueConstraint(columnNames = { "program_id", "location" }))
public class ProgramLocation {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "program_id", insertable = false, updatable = false)
    private Program program;

    @Column(name = "location", nullable = false)
    private String location;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Program getProgram() {
        return program;
    }

    public final void setProgram(Program program) {
        this.program = program;
    }

    public final String getLocation() {
        return location;
    }

    public final void setLocation(String location) {
        this.location = location;
    }

    public ProgramLocation withLocation(String location) {
        this.location = location;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(program, location);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ProgramLocation other = (ProgramLocation) object;
        return Objects.equal(program, other.getProgram()) && Objects.equal(location, other.getLocation());
    }

}
