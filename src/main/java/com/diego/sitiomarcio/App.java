package com.diego.sitiomarcio;

import com.diego.sitiomarcio.models.Cliente;
import com.diego.sitiomarcio.models.Reserva;
import com.diego.sitiomarcio.models.Usuario;
import com.diego.sitiomarcio.services.ReservaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ReservaService service = new ReservaService();

        while (true) {
            System.out.println("\n====== MENU ======");
            System.out.println("1. Cadastrar reserva");
            System.out.println("2. Listar Reservas");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // limpa o buffer

            try {
                if (opcao == 1) {
                    System.out.println("Nome do cliente: ");
                    String nome = scanner.nextLine();

                    System.out.println("Telefone: ");
                    String telefone = scanner.nextLine();

                    System.out.println("Observação: ");
                    String observacao = scanner.nextLine();

                    System.out.println("Data de entrada (YYYY-MM-DD): ");
                    LocalDate entrada = LocalDate.parse(scanner.nextLine());

                    System.out.println("Data de saída (YYYY-MM-DD): ");
                    LocalDate saida = LocalDate.parse(scanner.nextLine());

                    System.out.println("Valor da diária: ");
                    double diaria = scanner.nextDouble();
                    scanner.nextLine();

                    Cliente cliente = new Cliente(nome, telefone, observacao);
                    Usuario usuario = new Usuario("admin", "admin@admin.com", true); // provisório
                    Reserva reserva = new Reserva(null, cliente, usuario, entrada, saida, diaria
                    );


                    boolean sucesso = service.criarReserva(reserva);

                    if (sucesso) {
                        System.out.println("✅ Reserva criada com sucesso!");
                    } else {
                        System.out.println("❌ Erro ao criar reserva.");
                    }

                } else if (opcao == 2) {
                    List<Reserva> reservas = service.listarReservas();
                    if (reservas.isEmpty()) {
                        System.out.println("Nenhuma reserva encontrada.");
                    } else {
                        for (Reserva r : reservas) {
                            System.out.println("\n-----------------------------");
                            System.out.println("Cliente: " + r.getCliente().getNome());
                            System.out.println("Telefone: " + r.getCliente().getTelefone());
                            System.out.println("Observação: " + r.getCliente().getObservacao());
                            System.out.println("Entrada: " + r.getDataEntrada());
                            System.out.println("Saída: " + r.getDataSaida());
                            System.out.println("Diária: R$ " + r.getDiaria());
                            System.out.println("Total: R$ " + r.getValorTotal());
                            System.out.println("Registrado por: " + r.getCriadoPor().getEmail());
                        }
                    }

                } else if (opcao == 0) {
                    System.out.println("Encerrando o programa.");
                    break; // encerra o while
                } else {
                    System.out.println("Opção inválida.");
                }

            } catch (Exception e) {
                System.out.println("❌ Erro: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
