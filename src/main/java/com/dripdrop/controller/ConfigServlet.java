package com.dripdrop.controller;

import com.dripdrop.dao.ConfigDAO;
import com.dripdrop.model.Configuracion;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/configuracion")
public class ConfigServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Configuracion configuracion = ConfigDAO.leerConfiguracion();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(configuracion));

        } catch (Exception e) {

            getServletContext().log(
                    "Error al consultar la configuracion", e
            );

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "No se pudo consultar la configuracion."
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Configuracion configuracion = gson.fromJson(
                    request.getReader(),
                    Configuracion.class
            );

            if (configuracion == null) {
                enviarRespuesta(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "La configuracion recibida no es valida."
                );
                return;
            }

            if (configuracion.getNombre_negocio() == null
                    || configuracion.getNombre_negocio().trim().isEmpty()) {

                enviarRespuesta(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "El nombre del negocio es obligatorio."
                );
                return;
            }

            if (configuracion.getAncho_maximo_logo_px() < 0
                    || configuracion.getUmbral_logo() < 0
                    || configuracion.getUmbral_logo() > 255) {

                enviarRespuesta(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "Los valores de configuracion del logo no son validos."
                );
                return;
            }

            ConfigDAO.guardarConfiguracion(configuracion);

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "Configuracion guardada correctamente."
            );

        } catch (Exception e) {

            getServletContext().log(
                    "Error al guardar la configuracion", e
            );

            enviarRespuesta(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "No se pudo guardar la configuracion."
            );
        }
    }

    private void enviarRespuesta(HttpServletResponse response,
                                 int codigo,
                                 boolean exito,
                                 String mensaje) throws IOException {

        response.setStatus(codigo);

        Map<String, Object> resultado = new HashMap<>();

        resultado.put("exito", exito);
        resultado.put("mensaje", mensaje);

        response.getWriter().write(gson.toJson(resultado));
    }
}