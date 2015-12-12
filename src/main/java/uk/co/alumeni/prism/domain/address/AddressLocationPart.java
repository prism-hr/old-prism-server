package uk.co.alumeni.prism.domain.address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.definitions.PrismAddressLocationPartType;

@Entity
@Table(name = "address_location_part", uniqueConstraints = { @UniqueConstraint(columnNames = { "parent_id", "address_location_part_type", "name_index" }) })
public class AddressLocationPart implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private AddressLocationPart parent;

    @Column(name = "address_location_part_type", nullable = false)
    private PrismAddressLocationPartType type;

    @Lob
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_index", nullable = false)
    private String nameIndex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AddressLocationPart getParent() {
        return parent;
    }

    public void setParent(AddressLocationPart parent) {
        this.parent = parent;
    }

    public PrismAddressLocationPartType getType() {
        return type;
    }

    public void setType(PrismAddressLocationPartType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(String nameIndex) {
        this.nameIndex = nameIndex;
    }

    public AddressLocationPart withParent(AddressLocationPart parent) {
        this.parent = parent;
        return this;
    }

    public AddressLocationPart withType(PrismAddressLocationPartType type) {
        this.type = type;
        return this;
    }

    public AddressLocationPart withName(String name) {
        this.name = name;
        return this;
    }

    public AddressLocationPart withNameIndex(String nameIndex) {
        this.nameIndex = nameIndex;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parent, type, nameIndex);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        AddressLocationPart other = (AddressLocationPart) object;
        if (Objects.equal(type, other.getType()) && Objects.equal(nameIndex, other.getNameIndex())) {
            AddressLocationPart otherParent = other.getParent();
            if (parent == null && otherParent == null) {
                return true;
            } else if (!(parent == null || otherParent == null)) {
                return Objects.equal(parent.getId(), otherParent.getId());
            }
        }
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("parent", parent).addProperty("type", type).addProperty("nameIndex", nameIndex);
    }

}
