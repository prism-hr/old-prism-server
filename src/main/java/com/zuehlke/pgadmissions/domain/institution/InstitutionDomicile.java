package com.zuehlke.pgadmissions.domain.institution;

import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;
import org.apache.solr.analysis.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;

@AnalyzerDef(name = "institutionDomicileNameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class)})
@Entity
@Table(name = "INSTITUTION_DOMICILE")
@Indexed
public class InstitutionDomicile extends GeocodableLocation {

    @Id
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    @Field(analyzer = @Analyzer(definition = "institutionDomicileNameAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String name;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Embedded
    private GeographicLocation location;


    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public final GeographicLocation getLocation() {
        return location;
    }

    @Override
    public final void setLocation(GeographicLocation location) {
        this.location = location;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public InstitutionDomicile withId(String id) {
        this.id = id;
        return this;
    }

    public InstitutionDomicile withName(String name) {
        this.name = name;
        return this;
    }

    public InstitutionDomicile withCurrency(final String currency) {
        this.currency = currency;
        return this;
    }

    public InstitutionDomicile withLocation(GeographicLocation location) {
        this.location = location;
        return this;
    }

    public InstitutionDomicile withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String getLocationString() {
        return name;
    }

}
