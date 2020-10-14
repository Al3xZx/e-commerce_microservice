package com.alessandro.order.support;

import com.alessandro.order.d_entity.LineaOrdine;
import com.alessandro.order.d_entity.Order;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CustomOrderLineListSerializer extends JsonSerializer<List<LineaOrdine>> {

//    public CustomListSerializer() {
//        this(null);
//    }
//
//    public CustomListSerializer(Class<List> t) {
//        super();
//    }


    public CustomOrderLineListSerializer() {
    }

    @Override
    public void serialize(List<LineaOrdine> items, JsonGenerator generator, SerializerProvider provider) throws IOException{

        List<LineaOrdine> ol = new LinkedList<>();
        for (LineaOrdine item : items) {
            LineaOrdine o = new LineaOrdine(item);
            Order order = new Order();
            order.setId(item.getOrdine().getId());
            o.setOrdine(order);
            ol.add(o);
        }
        generator.writeObject(ol);
    }
}
