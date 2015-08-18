package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.department.Department;

import javax.persistence.*;

@Entity
@Table(name = "advert_resource", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "institution_id", "department_id" }) })
public class AdvertResource extends AdvertTargetResource {

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

    public AdvertResource withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertResource withValue(ResourceParent<?> value) {
        setValue(value);
        return this;
    }

}
