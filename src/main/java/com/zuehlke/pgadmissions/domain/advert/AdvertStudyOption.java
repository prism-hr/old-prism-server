package com.zuehlke.pgadmissions.domain.advert;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

import uk.co.alumeni.prism.api.model.resource.ResourceInstanceGroupDefinition;

@Entity
@Table(name = "advert_study_option", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "imported_study_option_id" }) })
public class AdvertStudyOption implements UniqueEntity, ResourceInstanceGroupDefinition<ImportedEntitySimple, AdvertStudyOptionInstance> {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id")
    private Advert advert;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imported_study_option_id", nullable = false)
    private ImportedEntitySimple studyOption;

    @Column(name = "application_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationStartDate;

    @Column(name = "application_close_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationCloseDate;

    @OneToMany(mappedBy = "studyOption")
    private Set<AdvertStudyOptionInstance> instances = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Advert getAdvert() {
        return advert;
    }
    
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public ImportedEntitySimple getStudyOption() {
        return studyOption;
    }

    @Override
    public void setStudyOption(ImportedEntitySimple studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDate getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    @Override
    public Set<AdvertStudyOptionInstance> getInstances() {
        return instances;
    }

    @Override
    public void setInstances(Set<AdvertStudyOptionInstance> instances) {
        this.instances = instances;
    }

    public AdvertStudyOption withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertStudyOption withStudyOption(ImportedEntitySimple studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public AdvertStudyOption withApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
        return this;
    }

    public AdvertStudyOption withApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
        return this;
    }
    
    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addExclusion("advert", advert).addProperty("studyOption", studyOption);
    }

}
