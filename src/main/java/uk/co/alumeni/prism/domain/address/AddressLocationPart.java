package uk.co.alumeni.prism.domain.address;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import jersey.repackaged.com.google.common.collect.Sets;
import uk.co.alumeni.prism.domain.UniqueEntity;

import com.google.common.base.Objects;

@Entity
@Table(name = "address_location_part")
public class AddressLocationPart implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private AddressLocationPart parent;

    @Lob
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_index", nullable = false)
    private String nameIndex;

    @OneToMany(mappedBy = "locationPart")
    private Set<AddressLocation> locations = Sets.newHashSet();

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

    public Set<AddressLocation> getLocations() {
        return locations;
    }

    public AddressLocationPart withParent(AddressLocationPart parent) {
        this.parent = parent;
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
        return Objects.hashCode(parent, nameIndex);
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
        if (Objects.equal(nameIndex, other.getNameIndex())) {
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
        return new EntitySignature().addProperty("nameIndex", nameIndex);
    }

}
