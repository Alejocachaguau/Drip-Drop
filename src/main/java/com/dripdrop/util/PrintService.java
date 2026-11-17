package com.dripdrop.util;

import com.dripdrop.model.Configuracion;
import com.dripdrop.model.Producto;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;

public class PrintService {

    private static final byte[] ESC_INIT = {0x1B, 0x40};
    private static final byte[] ALIGN_LEFT = {0x1B, 0x61, 0x00};
    private static final byte[] ALIGN_CENTER = {0x1B, 0x61, 0x01};
    private static final byte[] ALIGN_RIGHT = {0x1B, 0x61, 0x02};
    private static final byte[] SIZE_NORMAL = {0x1D, 0x21, 0x00};
    private static final byte[] SIZE_DOUBLE = {0x1D, 0x21, 0x11};
    private static final byte[] SIZE_TRIPLE = {0x1D, 0x21, 0x22};
    private static final byte[] BOLD_ON = {0x1B, 0x45, 0x01};
    private static final byte[] BOLD_OFF = {0x1B, 0x45, 0x00};

    private static final int ANCHO_TICKET = 32;

    private static String repetir(char caracter, int cantidad) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < cantidad; i++) {
            sb.append(caracter);
        }

        return sb.toString();
    }

    public static boolean imprimirTicket(String numeroOrden, List<Producto> productos, double total,
                                         String fecha, String hora, Configuracion config) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            baos.write(ESC_INIT);
            baos.write(ALIGN_CENTER);
            baos.write(SIZE_DOUBLE);
            baos.write("NUEVA ORDEN\n".getBytes("UTF-8"));

            baos.write(SIZE_TRIPLE);
            baos.write(("#" + numeroOrden + "\n").getBytes("UTF-8"));

            baos.write(SIZE_NORMAL);
            baos.write(ALIGN_LEFT);
            baos.write((repetir('-', ANCHO_TICKET) + "\n").getBytes("UTF-8"));

            baos.write(("Fecha: " + fecha + "\n").getBytes("UTF-8"));
            baos.write(("Hora:  " + hora + "\n").getBytes("UTF-8"));
            baos.write((repetir('-', ANCHO_TICKET) + "\n").getBytes("UTF-8"));

            DecimalFormat df = new DecimalFormat("#.00");

            for (Producto p : productos) {

                String izq = p.getCantidad() + "x " + p.getNombre();
                String der = "$" + df.format(p.getPrecio() * p.getCantidad());

                baos.write((lineaConPrecioDerecha(izq, der) + "\n").getBytes("UTF-8"));
            }

            baos.write((repetir('-', ANCHO_TICKET) + "\n").getBytes("UTF-8"));

            baos.write(BOLD_ON);
            baos.write(SIZE_DOUBLE);
            baos.write(ALIGN_RIGHT);

            baos.write("TOTAL:\n".getBytes("UTF-8"));
            baos.write(("$" + df.format(total) + "\n").getBytes("UTF-8"));

            baos.write(SIZE_NORMAL);
            baos.write(BOLD_OFF);
            baos.write(ALIGN_LEFT);

            baos.write((repetir('-', ANCHO_TICKET) + "\n").getBytes("UTF-8"));

            baos.write(ALIGN_CENTER);

            try {

                byte[] logoEscPos = rasterizarLogo(
                        config.getRuta_logo(),
                        config.getAncho_maximo_logo_px(),
                        config.getUmbral_logo()
                );

                if (logoEscPos != null) {
                    baos.write(logoEscPos);
                    baos.write("\n".getBytes());
                }

            } catch (Exception e) {
            }

            baos.write(SIZE_DOUBLE);
            baos.write((config.getNombre_negocio() + "\n").getBytes("UTF-8"));

            baos.write(SIZE_NORMAL);
            baos.write(ALIGN_LEFT);
            baos.write("\n\n\n\n\n".getBytes());

            try (FileOutputStream fos = new FileOutputStream(config.getNombre_impresora())) {

                fos.write(baos.toByteArray());
                fos.flush();

            }

            return true;

        } catch (Exception e) {

            System.err.println("Error de impresion: " + e.getMessage());
            return false;

        }
    }

    private static String lineaConPrecioDerecha(String izquierda, String derecha) {

        int espacio = ANCHO_TICKET - izquierda.length() - derecha.length();

        if (espacio < 1) {

            izquierda = izquierda.substring(0, ANCHO_TICKET - derecha.length() - 1);
            espacio = 1;

        }

        return izquierda + repetir(' ', espacio) + derecha;
    }

    private static byte[] rasterizarLogo(String rutaImagen, int anchoMaximo, int umbralNegro)
            throws Exception {

        File imgFile = new File(rutaImagen);

        if (!imgFile.exists()) {
            return null;
        }

        BufferedImage original = ImageIO.read(imgFile);

        if (original == null) {
            return null;
        }

        int anchoOriginal = original.getWidth();
        int altoOriginal = original.getHeight();

        int anchoFinal = Math.min(anchoMaximo, anchoOriginal);

        anchoFinal -= (anchoFinal % 8);

        int altoFinal = (int) (altoOriginal * ((double) anchoFinal / anchoOriginal));

        BufferedImage redimensionada = new BufferedImage(
                anchoFinal,
                altoFinal,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = redimensionada.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, anchoFinal, altoFinal);

        g.drawImage(original, 0, 0, anchoFinal, altoFinal, null);

        g.dispose();

        int bytesPorFila = anchoFinal / 8;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(new byte[]{0x1D, 0x76, 0x30, 0x00});

        baos.write(new byte[]{
                (byte) (bytesPorFila & 0xFF),
                (byte) ((bytesPorFila >> 8) & 0xFF)
        });

        baos.write(new byte[]{
                (byte) (altoFinal & 0xFF),
                (byte) ((altoFinal >> 8) & 0xFF)
        });

        for (int y = 0; y < altoFinal; y++) {

            for (int xByte = 0; xByte < bytesPorFila; xByte++) {

                int bitByte = 0;

                for (int bit = 0; bit < 8; bit++) {

                    int x = (xByte * 8) + bit;

                    Color c = new Color(redimensionada.getRGB(x, y));

                    int gris = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

                    if (gris < umbralNegro) {
                        bitByte |= (0x80 >> bit);
                    }

                }

                baos.write(bitByte);

            }

        }

        return baos.toByteArray();
    }
}