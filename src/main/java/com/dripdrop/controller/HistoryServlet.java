package com.dripdrop.controller;

import com.dripdrop.dao.HistorialDAO;
import com.dripdrop.model.Ticket;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/historial")
public class HistoryServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            String desde = request.getParameter("desde");
            String hasta = request.getParameter("hasta");

            if (desde != null && !desde.matches("\\d{4}-\\d{2}-\\d{2}")) {
                enviarRespuesta(response, 400, false,
                        "El formato de fecha inicial no es valido.", null);
                return;
            }

            if (hasta != null && !hasta.matches("\\d{4}-\\d{2}-\\d{2}")) {
                enviarRespuesta(response, 400, false,
                        "El formato de fecha final no es valido.", null);
                return;
            }

            if (desde != null && hasta != null && desde.compareTo(hasta) > 0) {
                enviarRespuesta(response, 400, false,
                        "La fecha inicial no puede ser posterior a la final.", null);
                return;
            }

            List<Ticket> historial = HistorialDAO.leerHistorial();

            if (desde != null || hasta != null) {

                historial = historial.stream()
                        .filter(t -> t.getFecha() != null)
                        .filter(t -> {
                            boolean cumpleDesde =
                                    desde == null || t.getFecha().compareTo(desde) >= 0;

                            boolean cumpleHasta =
                                    hasta == null || t.getFecha().compareTo(hasta) <= 0;

                            return cumpleDesde && cumpleHasta;
                        })
                        .collect(Collectors.toList());
            }

            enviarRespuesta(response, 200, true,
                    "Historial consultado correctamente.", historial);

        } catch (Exception e) {

            getServletContext().log("Error al consultar el historial:", e);

            enviarRespuesta(response, 500, false,
                    "No se pudo consultar el historial.", null);
        }
    }

    private void enviarRespuesta(
            HttpServletResponse response,
            int codigo,
            boolean exito,
            String mensaje,
            List<Ticket> ordenes
    ) throws IOException {

        response.setStatus(codigo);

        Map<String, Object> resultado = new HashMap<>();

        resultado.put("exito", exito);
        resultado.put("mensaje", mensaje);
        resultado.put("ordenes", ordenes);

        response.getWriter().write(gson.toJson(resultado));
    }
}
