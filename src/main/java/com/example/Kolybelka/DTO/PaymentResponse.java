package com.example.Kolybelka.DTO;

public class PaymentResponse {

    private String paymentUrl;

    public PaymentResponse(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    };
}
