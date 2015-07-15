package com.zuehlke.pgadmissions.domain.application;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "APPLICATION_FUNDING")
public class ApplicationFunding extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "imported_funding_source_id")
    private ImportedEntitySimple fundingSource;

    @Column(name = "sponsor")
    private String sponsor;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "award_value", nullable = false)
    private String value;

    @Column(name = "award_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate awardDate;

    @Column(name = "terms")
    private String terms;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", unique = true)
    private Document document;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ImportedEntitySimple getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(ImportedEntitySimple fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public ApplicationFunding withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationFunding withType(ImportedEntitySimple fundingSource) {
        this.fundingSource = fundingSource;
        return this;
    }

    public ApplicationFunding withDocument(Document document) {
        this.document = document;
        return this;
    }

    public ApplicationFunding withDescription(String description) {
        this.description = description;
        return this;
    }

    public ApplicationFunding withValue(String value) {
        this.value = value;
        return this;
    }

    public ApplicationFunding withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }

    public ApplicationFunding withApplication(Application application) {
        this.application = application;
        return this;
    }

}
