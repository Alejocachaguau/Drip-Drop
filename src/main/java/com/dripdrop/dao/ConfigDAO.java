package com.dripdrop.dao;

import com.dripdrop.model.Configuracion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConfigDAO {

    private static final String RUTA_CONFIG =
            "C:/xampp/tomcat/webapps/dripdrop/data/configuracion.json";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Leer configuracion
    public static synchronized Configuracion leerConfiguracion() {

        File archivo = new File(RUTA_CONFIG);

        if (!archivo.exists()) {
            System.err.println(
                    "No se encontro el archivo de configuracion: "
                            + archivo.getAbsolutePath()
            );

            return new Configuracion();
        }

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(archivo),
                StandardCharsets.UTF_8)) {

            Configuracion configuracion =
                    gson.fromJson(reader, Configuracion.class);

            if (configuracion == null) {
                System.err.println(
                        "El archivo de configuracion esta vacio."
                );

                return new Configuracion();
            }

            return configuracion;

        } catch (Exception e) {

            System.err.println(
                    "Error al leer la configuracion: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return new Configuracion();
        }
    }

    // Guardar configuracion
    public static synchronized void guardarConfiguracion(
            Configuracion configuracion) throws IOException {

        if (configuracion == null) {
            throw new IllegalArgumentException(
                    "La configuracion no puede ser nula."
            );
        }

        File archivo = new File(RUTA_CONFIG);
        File directorio = archivo.getParentFile();

        // Crear directorio si no existe
        if (directorio != null && !directorio.exists()) {

            if (!directorio.mkdirs() && !directorio.exists()) {
                throw new IOException(
                        "No se pudo crear el directorio: "
                                + directorio.getAbsolutePath()
                );
            }
        }

        // Guardar archivo JSON en UTF-8
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(archivo, false),
                StandardCharsets.UTF_8)) {

            gson.toJson(configuracion, writer);
            writer.flush();
        }

        System.out.println(
                "Configuracion guardada correctamente en: "
                        + archivo.getAbsolutePath()
        );
    }
}