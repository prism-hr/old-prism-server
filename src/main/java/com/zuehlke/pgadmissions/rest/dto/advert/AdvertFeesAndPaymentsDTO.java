package com.zuehlke.pgadmissions.rest.dto.advert;

public class AdvertFeesAndPaymentsDTO {

    private FinancialDetailsDTO fee;

    private FinancialDetailsDTO pay;

    public FinancialDetailsDTO getFee() {
        return fee;
    }

    public void setFee(FinancialDetailsDTO fee) {
        this.fee = fee;
    }

    public FinancialDetailsDTO getPay() {
        return pay;
    }

    public void setPay(FinancialDetailsDTO pay) {
        this.pay = pay;
    }

    public AdvertFeesAndPaymentsDTO withFee(FinancialDetailsDTO fee) {
        this.fee = fee;
        return this;
    }

    public AdvertFeesAndPaymentsDTO withPay(FinancialDetailsDTO pay) {
        this.pay = pay;
        return this;
    }
    
}
