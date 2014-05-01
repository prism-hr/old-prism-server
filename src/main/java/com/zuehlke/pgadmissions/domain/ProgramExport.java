package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.ProgramExportFormat;

@Entity
@Table(name = "PROGRAM_EXPORT")
public class ProgramExport implements Serializable {

    private static final long serialVersionUID = 2739581666640036046L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "program_export_format_id")
    private ProgramExportFormat format;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "RESEARCH_OPPORTUNITIES_FEED_PROGRAM_LINK", joinColumns = @JoinColumn(name = "feed_id"), inverseJoinColumns = @JoinColumn(name = "program_id"))
    private List<Program> programs = new ArrayList<Program>();

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

    public ProgramExportFormat getFormat() {
        return format;
    }

    public void setFormat(ProgramExportFormat format) {
        this.format = format;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public ProgramExport withFormat(ProgramExportFormat format) {
        this.format = format;
        return this;
    }

    public ProgramExport withPrograms(Program... programs) {
        this.programs.addAll(Arrays.asList(programs));
        return this;
    }
}
