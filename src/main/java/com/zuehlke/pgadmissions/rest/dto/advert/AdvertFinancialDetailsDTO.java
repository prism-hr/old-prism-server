package com.zuehlke.pgadmissions.rest.dto.advert;

public class AdvertFinancialDetailsDTO {

    private AdvertFinancialDetailDTO fee;

    private AdvertFinancialDetailDTO pay;

    public AdvertFinancialDetailDTO getFee() {
        return fee;
    }

    public void setFee(AdvertFinancialDetailDTO fee) {
        this.fee = fee;
    }

    public AdvertFinancialDetailDTO getPay() {
        return pay;
    }

    public void setPay(AdvertFinancialDetailDTO pay) {
        this.pay = pay;
    }

    public AdvertFinancialDetailsDTO withFee(AdvertFinancialDetailDTO fee) {
        this.fee = fee;
        return this;
    }

    public AdvertFinancialDetailsDTO withPay(AdvertFinancialDetailDTO pay) {
        this.pay = pay;
        return this;
    }
    
}
