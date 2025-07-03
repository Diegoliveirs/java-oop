package com.diego.sitiomarcio.services;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.diego.sitiomarcio.models.Cliente;
import com.diego.sitiomarcio.models.Reserva;
import com.diego.sitiomarcio.models.Usuario;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReservaService {

    private static final String URL_BASE = "https://uzbgduepdnnzhrjnegwc.supabase.co/rest/v1/reservas";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV6YmdkdWVwZG5uemhyam5lZ3djIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEzNzIzMjAsImV4cCI6MjA2Njk0ODMyMH0.p2CtD5zGe61P20uDGbUGM1pcSBYYJefHhN6P3N5k360";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public ReservaService() {
        client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
    }

    public boolean criarReserva(Reserva reserva) throws Exception{
        String json = String.format(Locale.US, """
              {
              "cliente_nome": "%s",
              "cliente_tel": "%s",
              "observacao": "%s",
              "data_entrada": "%s",
              "data_saida": "%s",
              "diaria": %f,
              "criado_por": "%s"
            }
            """,
                reserva.getCliente().getNome(),
                reserva.getCliente().getTelefone(),
                reserva.getCliente().getObservacao(),
                reserva.getDataEntrada(),
                reserva.getDataSaida(),
                reserva.getDiaria(),
                reserva.getCriadoPor().getEmail()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .header("Content-Type", "application/json")
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Supabase Response Status Code: " + response.statusCode());
        System.out.println("Supabase Response Body: " + response.body());
        return response.statusCode() == 201;
    }

    public boolean deletarReserva(int indice) throws Exception {
        List<Reserva> reservas = listarReservas();

        if (indice < 0 || indice >= reservas.size()) {
            System.out.println("❌ Índice inválido.");
            return false;
        }

        String id = reservas.get(indice).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "?id=eq." + id))
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status code delete: " + response.statusCode());
        System.out.println("Resposta delete " + response.body());

        return response.statusCode() == 204;

    }

    public boolean editarReserva(int indice, Reserva novaReserva) throws Exception {
        List<Reserva> reservas = listarReservas();

        if (indice < 0 || indice >= reservas.size()) {
            System.out.println("❌ Índice inválido.");
            return false;
        }

        String id = reservas.get(indice).getId();

        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        String json = String.format("""
        {
          "cliente_nome": "%s",
          "cliente_tel": "%s",
          "observacao": "%s",
          "data_entrada": "%s",
          "data_saida": "%s",
          "diaria": %f,
          "criado_por": "%s"
        }
        """,
                novaReserva.getCliente().getNome(),
                novaReserva.getCliente().getTelefone(),
                novaReserva.getCliente().getObservacao(),
                novaReserva.getDataEntrada().format(isoFormatter),
                novaReserva.getDataSaida().format(isoFormatter),
                novaReserva.getDiaria(),
                novaReserva.getCriadoPor().getEmail()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "?id=eq." + id))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status code edit: " + response.statusCode());
        System.out.println("Resposta Edit: " + response.body());

        return response.statusCode() == 204;
    }

    public List<Reserva> listarReservas() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "?select=*"))
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Reserva> reservas = new ArrayList<>();

        if (response.statusCode() == 200) {
            JsonNode array = mapper.readTree(response.body());
            for (JsonNode node : array) {
                Cliente cliente = new Cliente(
                        node.get("cliente_nome").asText(),
                        node.get("cliente_tel").asText(),
                        node.get("observacao").asText()
                );

                Usuario usuario = new Usuario(
                        node.get("criado_por").asText(),
                        node.get("criado_por").asText(),
                        false
                );

                Reserva reserva = new Reserva(
                        node.get("id").asText(),
                        cliente,
                        usuario,
                        LocalDate.parse(node.get("data_entrada").asText()),
                        LocalDate.parse((node.get("data_saida").asText())),
                        node.get("diaria").asDouble()
                );

                reservas.add(reserva);
            }
        }
        return reservas;
    }
}