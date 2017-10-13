package uk.co.alumeni.prism.domain;

import java.math.BigDecimal;

public abstract class WeightedRelation {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract BigDecimal getRelationStrength();

    public abstract void setRelationStrength(BigDecimal relationStrength);

}
