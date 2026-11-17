package com.dripdrop.controller;

import com.dripdrop.dao.ContadorDAO;
import com.dripdrop.dao.ConfigDAO;
import com.dripdrop.dao.HistorialDAO;
import com.dripdrop.model.Configuracion;
import com.dripdrop.model.Ticket;
import com.dripdrop.util.PrintService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/orden")
public class OrderServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            BufferedReader reader = request.getReader();

            Ticket ticketReq = gson.fromJson(reader, Ticket.class);

            if (ticketReq == null ||
                    ticketReq.getProductos() == null ||
                    ticketReq.getProductos().isEmpty()) {

                enviarRespuesta(response, 400, false, null,
                        "El carrito está vacío.");

                return;
            }

            int numOrden = ContadorDAO.obtenerSiguienteOrden();

            String numeroFormateado = String.format("%03d", numOrden);

            Date ahora = new Date();

            String horaActual =
                    new SimpleDateFormat("HH:mm").format(ahora);

            String fechaActual =
                    new SimpleDateFormat("dd/MM/yyyy").format(ahora);

            String fechaISO =
                    new SimpleDateFormat("yyyy-MM-dd").format(ahora);

            String horaISO =
                    new SimpleDateFormat("HH:mm:ss").format(ahora);

            Configuracion config = ConfigDAO.leerConfiguracion();

            if (config == null) {
                throw new Exception("No se pudo cargar la configuración.");
            }

            boolean exitoImpresion = PrintService.imprimirTicket(
                    numeroFormateado,
                    ticketReq.getProductos(),
                    ticketReq.getTotal(),
                    fechaActual,
                    horaActual,
                    config
            );

            Ticket ticketFinal = new Ticket();

            ticketFinal.setNumero_orden(numeroFormateado);
            ticketFinal.setFecha(fechaISO);
            ticketFinal.setHora(horaISO);
            ticketFinal.setFecha_texto(fechaActual);
            ticketFinal.setHora_texto(horaActual);
            ticketFinal.setProductos(ticketReq.getProductos());
            ticketFinal.setTotal(ticketReq.getTotal());
            ticketFinal.setImpreso(exitoImpresion);

            HistorialDAO.guardarEnHistorial(ticketFinal);

            if (exitoImpresion) {

                enviarRespuesta(response, 200, true, numeroFormateado,
                        "Orden impresa correctamente.");

            } else {

                enviarRespuesta(response, 500, false, numeroFormateado,
                        "No se pudo comunicar con la impresora.");
            }

        } catch (Exception e) {

            getServletContext().log("Error al procesar la orden:", e);

            enviarRespuesta(response, 500, false, null,
                    "Ocurrió un error al procesar la orden.");
        }
    }

    private void enviarRespuesta(
            HttpServletResponse response,
            int codigo,
            boolean exito,
            String numeroOrden,
            String mensaje
    ) throws IOException {

        response.setStatus(codigo);

        Map<String, Object> resultado = new HashMap<>();

        resultado.put("exito", exito);
        resultado.put("numero_orden", numeroOrden);
        resultado.put("mensaje", mensaje);

        response.getWriter().write(gson.toJson(resultado));
    }
}
