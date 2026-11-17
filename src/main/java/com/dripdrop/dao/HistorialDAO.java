package com.dripdrop.dao;

import com.dripdrop.model.Ticket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HistorialDAO {

    private static final String RUTA_HISTORIAL =
            "C:/xampp/tomcat/webapps/dripdrop/data/historial.json";

    private static final Gson gson = new Gson();

    private static final Type TIPO_LISTA =
            new TypeToken<ArrayList<Ticket>>() {}.getType();

    public static synchronized void guardarEnHistorial(Ticket ticket)
            throws IOException {

        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo.");
        }

        List<Ticket> historial = leerHistorial();

        historial.add(ticket);

        File file = new File(RUTA_HISTORIAL);

        File carpeta = file.getParentFile();

        if (carpeta != null && !carpeta.exists()) {
            if (!carpeta.mkdirs() && !carpeta.exists()) {
                throw new IOException("No se pudo crear la carpeta del historial.");
            }
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file),
                StandardCharsets.UTF_8)) {

            gson.toJson(historial, writer);
        }
    }

    public static synchronized List<Ticket> leerHistorial()
            throws IOException {

        File file = new File(RUTA_HISTORIAL);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(file),
                StandardCharsets.UTF_8)) {

            List<Ticket> historial = gson.fromJson(reader, TIPO_LISTA);

            return historial != null
                    ? historial
                    : new ArrayList<>();

        } catch (Exception e) {
            throw new IOException(
                    "No se pudo leer el historial de ordenes.",
                    e
            );
        }
    }
}
