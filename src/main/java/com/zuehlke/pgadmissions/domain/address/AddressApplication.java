package com.zuehlke.pgadmissions.domain.address;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

@Entity
@Table(name = "address")
public class AddressApplication extends Address {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id")
    private ImportedEntitySimple domicile;

    public ImportedEntitySimple getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedEntitySimple domicile) {
        this.domicile = domicile;
    }
    
    @Override
    public String getLocationString() {
        return super.getLocationString() + ", " + domicile.getName();
    }

}
