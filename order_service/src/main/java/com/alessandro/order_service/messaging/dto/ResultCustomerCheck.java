package com.alessandro.order_service.messaging.dto;

public class ResultCustomerCheck {

    private Customer customer;
    private int orderId;
    private String messageResult;

    public ResultCustomerCheck(Customer customer, int orderId, String messageResult) {
        this.customer = customer;
        this.orderId = orderId;
        this.messageResult = messageResult;
    }

    public ResultCustomerCheck() {
    }

    public String getMessageResult() {
        return messageResult;
    }

    public void setMessageResult(String messageResult) {
        this.messageResult = messageResult;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
