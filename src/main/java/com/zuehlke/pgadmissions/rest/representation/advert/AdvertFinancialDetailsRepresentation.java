package com.zuehlke.pgadmissions.rest.representation.advert;

public class AdvertFinancialDetailsRepresentation {

    private AdvertFinancialDetailRepresentation fee;

    private AdvertFinancialDetailRepresentation pay;

    public AdvertFinancialDetailRepresentation getFee() {
        return fee;
    }

    public void setFee(AdvertFinancialDetailRepresentation fee) {
        this.fee = fee;
    }

    public AdvertFinancialDetailRepresentation getPay() {
        return pay;
    }

    public void setPay(AdvertFinancialDetailRepresentation pay) {
        this.pay = pay;
    }

    public AdvertFinancialDetailsRepresentation withFee(AdvertFinancialDetailRepresentation fee) {
        this.fee = fee;
        return this;
    }

    public AdvertFinancialDetailsRepresentation withPay(AdvertFinancialDetailRepresentation pay) {
        this.pay = pay;
        return this;
    }

}
