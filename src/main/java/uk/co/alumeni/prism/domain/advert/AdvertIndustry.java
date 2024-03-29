package uk.co.alumeni.prism.domain.advert;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;

import javax.persistence.*;

import static com.google.common.base.Objects.equal;

@Entity
@Table(name = "advert_industry", uniqueConstraints = {@UniqueConstraint(columnNames = {"advert_id", "industry"})})
public class AdvertIndustry extends AdvertAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "industry", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAdvertIndustry industry;

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

    public PrismAdvertIndustry getIndustry() {
        return industry;
    }

    public void setIndustry(PrismAdvertIndustry industry) {
        this.industry = industry;
    }

    public AdvertIndustry withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertIndustry withIndustry(PrismAdvertIndustry industry) {
        this.industry = industry;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(advert, industry);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        AdvertIndustry other = (AdvertIndustry) object;
        return equal(advert, other.getAdvert()) && equal(industry, other.getIndustry());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("industry", industry);
    }

}
