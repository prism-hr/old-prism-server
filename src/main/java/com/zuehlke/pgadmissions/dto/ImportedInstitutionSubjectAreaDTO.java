package com.zuehlke.pgadmissions.dto;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

public class ImportedInstitutionSubjectAreaDTO {

    private List<BigDecimal> head = Lists.newArrayList();

    private Integer id;

    private BigDecimal relationStrength;

    private Long relationCount;

    private Long relationCountCumulative;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getRelationStrength() {
        return relationStrength;
    }

    public void setRelationStrength(BigDecimal relationStrength) {
        this.relationStrength = relationStrength;
    }

    public Long getRelationCount() {
        return relationCount;
    }

    public void setRelationCount(Long relationCount) {
        this.relationCount = relationCount;
    }

    public void addRelationCount(Long relationCount) {
        this.relationCountCumulative = this.relationCountCumulative == null ? relationCount : this.relationCountCumulative + relationCount;
    }

    public BigDecimal getHead() {
        return head.stream().reduce(ZERO, BigDecimal::add);
    }

    public void addHead(Integer limit, BigDecimal value, Long occurences) {
        int counter = 0;
        for (int i = 0; i < (limit - head.size()); i++) {
            if (counter == occurences.intValue()) {
                break;
            }
            head.add(value);
            counter++;
        }
    }

    public Long getTailLength() {
        return relationCountCumulative - head.size();
    }

}
