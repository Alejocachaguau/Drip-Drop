package com.dripdrop.model;

import java.util.List;

public class Ticket {

    private String numero_orden;
    private String fecha;
    private String hora;
    private String fecha_texto;
    private String hora_texto;
    private List<Producto> productos;
    private double total;
    private boolean impreso;

    public Ticket() {
    }

    public String getNumero_orden() {
        return numero_orden;
    }

    public void setNumero_orden(String numero_orden) {
        this.numero_orden = numero_orden;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha_texto() {
        return fecha_texto;
    }

    public void setFecha_texto(String fecha_texto) {
        this.fecha_texto = fecha_texto;
    }

    public String getHora_texto() {
        return hora_texto;
    }

    public void setHora_texto(String hora_texto) {
        this.hora_texto = hora_texto;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isImpreso() {
        return impreso;
    }

    public void setImpreso(boolean impreso) {
        this.impreso = impreso;
    }
}
