package com.diego.sitiomarcio.services;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

    public ReservaService() {
        client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
    }

    public boolean criarReserva(Reserva reserva) throws Exception {
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

    public List<Reserva> listarReservas() throws Exception {
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

    public boolean dataJaReservada(LocalDate data, String idIgnorado) throws Exception {
        List<Reserva> reservas = listarReservas();

        for (Reserva reserva : reservas) {
            if (idIgnorado != null && reserva.getId().equals(idIgnorado)) {
                continue;
            }

            LocalDate entrada = reserva.getDataEntrada();
            LocalDate saida = reserva.getDataSaida();

            if (
                    data.isEqual(entrada) ||
                            data.isEqual(saida) ||
                            (data.isAfter(entrada) && data.isBefore(saida))
            ) {
                return true;
            }
        }
        return false;
    }

    public void handlerCriarReserva(Scanner scanner) throws Exception {

        System.out.println("Nome do cliente: ");
        String nome = scanner.nextLine();

        System.out.println("Telefone: ");
        String telefone = scanner.nextLine();

        System.out.println("Observação: ");
        String observacao = scanner.nextLine();

        LocalDate entrada;
        while (true) {
            System.out.println("Data de entrada (DD/MM/AA): ");
            entrada = LocalDate.parse(scanner.nextLine(), formatter);


            if (dataJaReservada(entrada, null)) {
                System.out.println("❌ Esta data já está reservada. Digite outra.");
            } else {
                break;
            }
        }

        LocalDate saida;
        while (true) {
            System.out.println("Data de saída (DD/MM/AA): ");
            saida = LocalDate.parse(scanner.nextLine(), formatter);


            if (saida.isBefore(entrada)) {
                System.out.println("❌ Data de saída não pode ser antes da data de entrada.");
                continue;
            }

            if (dataJaReservada(entrada, null)) {
                System.out.println("❌ Esta data já está reservada. Digite outra.");
            } else {
                break;
            }

        }

        System.out.println("Valor da diária: ");
        double diaria = scanner.nextDouble();
        scanner.nextLine();

        Cliente cliente = new Cliente(nome, telefone, observacao);
        Usuario usuario = new Usuario("admin", "admin@admin.com", true); // provisório
        Reserva reserva = new Reserva(null, cliente, usuario, entrada, saida, diaria);

        boolean sucesso = criarReserva(reserva);

        if (sucesso) {
            System.out.println("✅ Reserva criada com sucesso!");
        } else {
            System.out.println("❌ Erro ao criar reserva.");
        }


    }

    public void handlerListarReservas(Scanner scanner) throws Exception {
        List<Reserva> reservas = listarReservas();
        if (reservas.isEmpty()) {
            System.out.println("Nenhuma reserva encontrada.");
        } else {
            System.out.println("======= RESERVAS =======");
            for (int i = 0; i < reservas.size(); i++) {
                System.out.println(formatarResumoReserva(reservas.get(i), i));
            }
        }
    }

    public void handlerEditarReservas(Scanner scanner) throws Exception{
        List<Reserva> reservas = listarReservas();
        if (reservas.isEmpty()) {
            System.out.println("❌ Nenhuma reserva disponível para editar.");
        }else {
            for (int i = 0; i < reservas.size(); i++) {
                System.out.println(i + " - " + reservas.get(i).getCliente().getNome());
            }

            System.out.print("Digite o numero da reserva que deseja editar: ");
            int indice = Integer.parseInt(scanner.nextLine());

            System.out.println("Digite o novo nome do cliente: ");
            String nome = scanner.nextLine();

            System.out.println("Digite o novo telefone: ");
            String telefone = scanner.nextLine();

            System.out.println("Digite nova observação: ");
            String observacao = scanner.nextLine();

            System.out.println("Digite nova data de entrada (DD/MM/AA): ");
            LocalDate entrada = LocalDate.parse(scanner.nextLine(), formatter);

            System.out.println("Digite nova data de saída (DD/MM/AA): ");
            LocalDate saida = LocalDate.parse(scanner.nextLine(), formatter);

            System.out.println("Digite novo valor da diária: ");
            double diaria = Double.parseDouble(scanner.nextLine());

            Cliente novoCliente = new Cliente(nome, telefone, observacao);
            Usuario novoUsuario = new Usuario("admin", "admin@admin.com", true);

            Reserva reservaAtualizada = new Reserva(null, novoCliente, novoUsuario, entrada, saida, diaria);

            boolean ok = editarReserva(indice, reservaAtualizada);
            System.out.println(ok ? "✅ Editado com sucesso!" : "❌ Falha ao editar.");
        }
    }

    public void handlerDeletarReservas(Scanner scanner) throws Exception{
        List<Reserva> reservas = listarReservas();
        if (reservas.isEmpty()){
            System.out.println("❌ Nenhuma reserva disponível para deletar.");
        }else {
            for (int i = 0; i < reservas.size(); i++) {
                System.out.println(i + " - " + reservas.get(i).getCliente().getNome());
            }

            System.out.println("Digite o numero da reserva que deseja deletar: ");
            int indice = Integer.parseInt(scanner.nextLine());

            boolean ok = deletarReserva(indice);
            System.out.println(ok ? "✅ Deletado com sucesso!" : "❌ Falha ao deletar.");
        }
    }

    public String formatarResumoReserva(Reserva reserva, int index) {
        return String.format("[%d] %s - %s -> %s | R$ %.2f | Total: R$ %.2f | \uD83D\uDCE9 %s",
                index,
                reserva.getCliente().getNome(),
                reserva.getDataEntrada().format(formatter),
                reserva.getDataSaida().format(formatter),
                reserva.getDiaria(),
                reserva.getValorTotal(),
                reserva.getCriadoPor().getNome());
    }
}