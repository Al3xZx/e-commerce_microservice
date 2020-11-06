package com.alessandro.order_service.messaging.dto;


public class Customer {

    private Integer id;

    private String nome;

    private String cognome;

    private Double credit;

    public Customer() {
    }

    public Customer(String nome, String cognome, double credit) {
        this.nome = nome;
        this.cognome = cognome;
        this.credit = credit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", credit=" + credit +
                '}';
    }
}
