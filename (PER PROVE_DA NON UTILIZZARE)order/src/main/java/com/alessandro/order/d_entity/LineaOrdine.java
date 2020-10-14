package com.alessandro.order.d_entity;

import com.alessandro.order.support.CustomOrderSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;

@Entity
public class LineaOrdine {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn
    //@JsonSerialize(using = CustomOrderSerializer.class)
    @JsonIgnore
    private Order ordine;//ordine al quale appartiene this

    private Integer idProdotto;

    private String nomeProdotto;

    private Integer qta;

    public LineaOrdine() {
    }

    public LineaOrdine(LineaOrdine ol) {
        this.id = ol.id;
        this.idProdotto = ol.idProdotto;
        this.nomeProdotto = ol.nomeProdotto;
        this.qta = ol.qta;
        this.ordine = ol.ordine;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Integer idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNomeProdotto() {
        return nomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }

    public Integer getQta() {
        return qta;
    }

    public void setQta(Integer qta) {
        this.qta = qta;
    }

    public Order getOrdine() {
        return ordine;
    }

    public void setOrdine(Order ordine) {
        this.ordine = ordine;
    }
}
