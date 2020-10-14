package com.alessandro.order.d_entity;

import com.alessandro.order.support.CustomOrderLineListSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;


@Entity
@Table(name = "order_table")
public class Order {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL)
//    @JsonIgnore
//    @JsonSerialize(using = CustomOrderLineListSerializer.class)
    private List<LineaOrdine> lineaOrdine = new LinkedList<>();

    @Column(name = "cliete")
    private Integer idCliente;

    @Column(name = "nome_cliente")
    private String nomeCliente;

    @Column(name = "cognome_cliente")
    private String cognomeCliente;

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

    public List<LineaOrdine> getLineaOrdine() {
        return lineaOrdine;
    }

    public void setLineaOrdine(List<LineaOrdine> lineaOrdine) {
        this.lineaOrdine = lineaOrdine;
    }
}
