package com.alessandro.order_service.d_entity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "order_table")
public class Order {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer idCliente;

    private String nomeCliente;

    private String cognomeCliente;

    private Double totale; // foreach orderline in lineaOrdine
                           //         totale += orderline.getTotale();

    private String state;//SOSPESO, CONFERMATO, ANNULLATO

    private String messageState;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL)
    private List<OrderLine> lineaOrdine = new LinkedList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCognomeCliente() {
        return cognomeCliente;
    }

    public void setCognomeCliente(String cognomeCliente) {
        this.cognomeCliente = cognomeCliente;
    }

    public List<OrderLine> getLineaOrdine() {
        return lineaOrdine;
    }

    public void setLineaOrdine(List<OrderLine> lineaOrdine) {
        this.lineaOrdine = lineaOrdine;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessageState() {
        return messageState;
    }

    public void setMessageState(String messageState) {
        this.messageState = messageState;
    }

    public Double getTotale() {
        return totale;
    }

    public void setTotale(Double totale) {
        this.totale = totale;
    }
}
