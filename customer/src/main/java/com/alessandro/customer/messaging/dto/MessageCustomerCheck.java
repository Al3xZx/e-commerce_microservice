package com.alessandro.customer.messaging.dto;

public class MessageCustomerCheck {

    private int customerId;
    private int orderId;
    private double creditDecremet;

    public MessageCustomerCheck() {
    }

    public MessageCustomerCheck(int customerId, int orderId, double creditDecremet) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.creditDecremet = creditDecremet;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getCreditDecremet() {
        return creditDecremet;
    }

    public void setCreditDecremet(double creditDecremet) {
        this.creditDecremet = creditDecremet;
    }
}
