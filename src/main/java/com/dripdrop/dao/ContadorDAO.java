package com.dripdrop.dao;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class ContadorDAO {

    private static final String RUTA_CONTADOR =
            "C:/xampp/tomcat/webapps/dripdrop/data/contador.txt";

    public static synchronized int obtenerSiguienteOrden() throws IOException {

        File file = new File(RUTA_CONTADOR);

        File carpeta = file.getParentFile();

        if (carpeta != null && !carpeta.exists()) {
            if (!carpeta.mkdirs() && !carpeta.exists()) {
                throw new IOException("No se pudo crear la carpeta del contador.");
            }
        }

        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("No se pudo crear el archivo del contador.");
        }

        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                FileChannel channel = raf.getChannel();
                FileLock lock = channel.lock()
        ) {

            raf.seek(0);

            String linea = raf.readLine();

            int numeroActual = 0;

            if (linea != null && !linea.trim().isEmpty()) {
                try {
                    numeroActual = Integer.parseInt(linea.trim());
                } catch (NumberFormatException e) {
                    throw new IOException("El contador contiene un valor invalido.", e);
                }
            }

            if (numeroActual < 0 || numeroActual == Integer.MAX_VALUE) {
                throw new IOException("El numero del contador esta fuera del rango permitido.");
            }

            int siguienteNumero = numeroActual + 1;

            raf.setLength(0);
            raf.seek(0);
            raf.writeBytes(String.valueOf(siguienteNumero));

            return siguienteNumero;
        }
    }
}