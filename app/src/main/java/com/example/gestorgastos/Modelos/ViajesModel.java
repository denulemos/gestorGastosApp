package com.example.gestorgastos.Modelos;

public class ViajesModel {

    private int id , montoTotal;
    private String chofer, origen, destino, estado;

    public ViajesModel(int id, String chofer, String origen, String destino, String estado, int montoTotal) {
        this.id = id;
        this.chofer = chofer;
        this.origen = origen;
        this.destino = destino;
        this.estado = estado;
        this.montoTotal = montoTotal;
    }

    public ViajesModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(int montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
}

