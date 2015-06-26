package com.zuehlke.pgadmissions.domain.advert;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.department.Department;

@Entity
@Table(name = "ADVERT_DEPARTMENT", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "department_id" }) })
public class AdvertDepartment extends AdvertTarget<Department> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "importance", nullable = false)
    private BigDecimal importance;

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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public BigDecimal getImportance() {
        return importance;
    }

    @Override
    public void setImportance(BigDecimal importance) {
        this.importance = importance;
    }

    @Override
    public Department getValue() {
        return department;
    }

    @Override
    public void setValue(Department value) {
        setDepartment(value);
    }
    
    @Override
    public String getTitle() {
        return department.getTitle();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("department", department);
    }

}
