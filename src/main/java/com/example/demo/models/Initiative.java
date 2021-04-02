package com.example.demo.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "initiatives")
public class Initiative {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String text;
    @Enumerated(value = EnumType.STRING)
    private InitiativeStatus status;
    private Integer cost;
    private String author;
    private String performerAddress;
    //private ??? appendices;


    public Initiative() {
    }

    public Initiative(String name, String text, Integer cost, String author, String performerAddress) {
        this.name = name;
        this.text = text;
        this.cost = cost;
        this.author = author;
        this.performerAddress = performerAddress;
        this.status = InitiativeStatus.NEW;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InitiativeStatus getStatus() {
        return status;
    }

    public void setStatus(InitiativeStatus status) {
        this.status = status;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPerformerAddress() {
        return performerAddress;
    }

    public void setPerformerAddress(String performerAddress) {
        this.performerAddress = performerAddress;
    }
}
