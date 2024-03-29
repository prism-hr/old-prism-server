package uk.co.alumeni.prism.domain.advert;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;

import javax.persistence.*;

import static com.google.common.base.Objects.equal;

@Entity
@Table(name = "advert_function", uniqueConstraints = {@UniqueConstraint(columnNames = {"advert_id", "function"})})
public class AdvertFunction extends AdvertAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "function", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAdvertFunction function;

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

    public PrismAdvertFunction getFunction() {
        return function;
    }

    public void setFunction(PrismAdvertFunction function) {
        this.function = function;
    }

    public AdvertFunction withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertFunction withFunction(PrismAdvertFunction function) {
        this.function = function;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(advert, function);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        AdvertFunction other = (AdvertFunction) object;
        return equal(advert, other.getAdvert()) && equal(function, other.getFunction());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("function", function);
    }

}
