package com.alessandro.product.messaging.dto;

import java.util.List;

public class ProductsOL {

    private List<OrderLine> orderLineList;
    private int orderId;
    private String message;

    public ProductsOL(List<OrderLine> orderLineList, int orderId) {
        this.orderLineList = orderLineList;
        this.orderId = orderId;
    }

    public ProductsOL(List<OrderLine> orderLineList, int orderId, String message) {
        this.orderLineList = orderLineList;
        this.orderId = orderId;
        this.message = message;
    }

    public ProductsOL() {
    }

    public List<OrderLine> getOrderLineList() {
        return orderLineList;
    }

    public void setOrderLineList(List<OrderLine> orderLineList) {
        this.orderLineList = orderLineList;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
