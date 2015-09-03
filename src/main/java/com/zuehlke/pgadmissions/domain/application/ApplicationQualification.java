package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;

@Entity
@Table(name = "application_qualification")
public class ApplicationQualification extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "imported_program_id")
    private ImportedProgram program;

    @Column(name = "start_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @Column(name = "award_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate awardDate;

    @Column(name = "qualification_language", nullable = false)
    private String language;

    @Column(name = "grade", nullable = false)
    private String grade;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", unique = true)
    private Document document;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public ImportedProgram getProgram() {
        return program;
    }

    public void setProgram(ImportedProgram program) {
        this.program = program;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public ApplicationQualification withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationQualification withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationQualification withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }

    public ApplicationQualification withLanguage(String language) {
        this.language = language;
        return this;
    }

    public ApplicationQualification withGrade(String grade) {
        this.grade = grade;
        return this;
    }

    public ApplicationQualification withDocument(Document document) {
        this.document = document;
        return this;
    }

    public ApplicationQualification withProgram(ImportedProgram program) {
        this.program = program;
        return this;
    }

    public ApplicationQualification withApplication(Application application) {
        this.application = application;
        return this;
    }

    public ApplicationQualification withCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public String getStartDateDisplay(String dateFormat) {
        return startDate == null ? null : startDate.toString(dateFormat);
    }

    public String getAwardDateDisplay(String dateFormat) {
        return awardDate == null ? null : awardDate.toString(dateFormat);
    }

}
