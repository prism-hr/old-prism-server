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
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.FundingSource;

@Entity
@Table(name = "APPLICATION_FUNDING")
public class ApplicationFunding {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "funding_source_id")
    private FundingSource fundingSource;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "award_value", nullable = false)
    private String value;

    @Column(name = "award_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate awardDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public FundingSource getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(FundingSource fundingSource) {
        this.fundingSource = fundingSource;
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

    public ApplicationFunding withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationFunding withType(FundingSource fundingSource) {
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

    public String getAwardDateDisplay(String dateFormat) {
        return awardDate == null ? null : awardDate.toString(dateFormat);
    }

    public String getFundingSourceDisplay() {
        return fundingSource == null ? null : fundingSource.getName();
    }

}
