package com.zuehlke.pgadmissions.domain;

import static javax.persistence.CascadeType.ALL;

import java.util.Arrays;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramExportFormat;

@Entity
@Table(name = "program_export", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "title" }) })
public class ProgramExport {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "program_export_format", nullable = false)
    private PrismProgramExportFormat format;

    @ManyToMany(cascade = ALL)
    @JoinTable(name = "program_export_program", joinColumns = { @JoinColumn(name = "program_export_id", nullable = false) })
    private Set<Program> programs = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrismProgramExportFormat getFormat() {
        return format;
    }

    public void setFormat(PrismProgramExportFormat format) {
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Program> getPrograms() {
        return programs;
    }

    public ProgramExport withId(Integer id) {
        this.id = id;
        return this;
    }

    public ProgramExport withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProgramExport withUser(User user) {
        this.user = user;
        return this;
    }

    public ProgramExport withFormat(PrismProgramExportFormat format) {
        this.format = format;
        return this;
    }

    public ProgramExport withPrograms(Program... programs) {
        this.programs.addAll(Arrays.asList(programs));
        return this;
    }

}
