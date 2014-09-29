package com.zuehlke.pgadmissions.rest.dto;

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
}
