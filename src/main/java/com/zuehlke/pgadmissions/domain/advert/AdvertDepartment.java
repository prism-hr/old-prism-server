package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.resource.Department;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "advert_department", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "department_id" }) })
public class AdvertDepartment extends AdvertTarget<Department> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department value;

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

    @Override
    public Department getValue() {
        return value;
    }

    @Override
    public void setValue(Department department) {
        this.value = department;
    }

    @Override
    public String getTitle() {
        return value.getTitle();
    }

}
