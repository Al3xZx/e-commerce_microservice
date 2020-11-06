package com.alessandro.product.messaging.dto;

public class OrderLine {

    private Integer id;

    private Integer idProdotto;

    private String nomeProdotto;

    private Double prezzoProdotto;

    private Integer qta;

    private Double totale; //prezzoPordotto * qta

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

    public Double getTotale() {
        return totale;
    }

    public void setTotale(Double totale) {
        this.totale = totale;
    }

    public Double getPrezzoProdotto() {
        return prezzoProdotto;
    }

    public void setPrezzoProdotto(Double prezzoProdotto) {
        this.prezzoProdotto = prezzoProdotto;
    }
}
