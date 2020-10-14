package com.alessandro.order.support;

import com.alessandro.order.d_entity.Order;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


public class CustomOrderSerializer extends JsonSerializer<Order> {

    @Override
    public void serialize(Order order, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Integer id = order.getId();
        jsonGenerator.writeObject(id);
    }
}
