package com.alessandro.order_service.messaging.dto;

public class MessageCustomerRollback {

    private int customerId;
    private double creditIncrement;

    public MessageCustomerRollback(int customerId, double creditIncrement) {
        this.customerId = customerId;
        this.creditIncrement = creditIncrement;
    }

    public MessageCustomerRollback() {
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getCreditIncrement() {
        return creditIncrement;
    }

    public void setCreditIncrement(double creditIncrement) {
        this.creditIncrement = creditIncrement;
    }
}
