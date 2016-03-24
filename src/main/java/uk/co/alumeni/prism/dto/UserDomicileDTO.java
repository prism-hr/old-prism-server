package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.PrismDomicile;

public class UserDomicileDTO {

    private Integer userId;
    
    private PrismDomicile domicileId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public PrismDomicile getDomicileId() {
        return domicileId;
    }

    public void setDomicileId(PrismDomicile domicileId) {
        this.domicileId = domicileId;
    }
    
}
