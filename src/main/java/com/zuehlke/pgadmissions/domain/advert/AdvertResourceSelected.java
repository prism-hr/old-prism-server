package com.zuehlke.pgadmissions.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.department.Department;

@Entity
@Table(name = "advert_resource_selected", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "institution_id", "department_id" }) })
public class AdvertResourceSelected extends AdvertTargetResource {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "endorsed", nullable = false)
    private Boolean endorsed = false;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Boolean getEndorsed() {
        return endorsed;
    }

    public void setEndorsed(Boolean endorsed) {
        this.endorsed = endorsed;
    }

    public AdvertResourceSelected withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertResourceSelected withValue(ResourceParent<?> value) {
        setValue(value);
        return this;
    }

    public AdvertResourceSelected withEndorsed(Boolean endorsed) {
        this.endorsed = endorsed;
        return this;
    }

}
