package com.dripdrop.model;

public class Configuracion {

    private String nombre_negocio = "Drip & Drop";
    private String nombre_impresora = "//localhost/DripDrop";
    private String ruta_logo = "C:/xampp/tomcat/webapps/Drip_and_Drop/src/main/webapp/img/png/logo.png";
    private int ancho_maximo_logo_px = 300;
    private int umbral_logo = 190;

    public Configuracion() {
    }

    public String getNombre_negocio() {
        return nombre_negocio;
    }

    public void setNombre_negocio(String nombre_negocio) {
        this.nombre_negocio = nombre_negocio;
    }

    public String getNombre_impresora() {
        return nombre_impresora;
    }

    public void setNombre_impresora(String nombre_impresora) {
        this.nombre_impresora = nombre_impresora;
    }

    public String getRuta_logo() {
        return ruta_logo;
    }

    public void setRuta_logo(String ruta_logo) {
        this.ruta_logo = ruta_logo;
    }

    public int getAncho_maximo_logo_px() {
        return ancho_maximo_logo_px;
    }

    public void setAncho_maximo_logo_px(int ancho_maximo_logo_px) {
        this.ancho_maximo_logo_px = ancho_maximo_logo_px;
    }

    public int getUmbral_logo() {
        return umbral_logo;
    }

    public void setUmbral_logo(int umbral_logo) {
        this.umbral_logo = umbral_logo;
    }
}