package com.diego.sitiomarcio.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reserva {
    private String id;
    private Cliente cliente;
    private Usuario usuario;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private double diaria;

    public Reserva(String id, Cliente cliente, Usuario usuario, LocalDate dataEntrada, LocalDate dataSaida, double diaria) {
        this.id = id;
        this.cliente = cliente;
        this.usuario = usuario;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.diaria = diaria;
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Usuario getCriadoPor() {
        return usuario;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public double getDiaria() {
        return diaria;
    }

    public double getValorTotal() {
        long dias = ChronoUnit.DAYS.between(dataEntrada,dataSaida) + 1;
        return dias * diaria;
    }
}
