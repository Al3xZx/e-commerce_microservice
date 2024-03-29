package com.alessandro.order_service.d_entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class OrderLine {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer idProdotto;

    private String nomeProdotto;

    private Integer qta;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Order ordine;

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
