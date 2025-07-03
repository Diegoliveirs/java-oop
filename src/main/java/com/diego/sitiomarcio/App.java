package com.diego.sitiomarcio;

import com.diego.sitiomarcio.models.Cliente;
import com.diego.sitiomarcio.models.Reserva;
import com.diego.sitiomarcio.models.Usuario;
import com.diego.sitiomarcio.services.ReservaService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);
        ReservaService service = new ReservaService();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        while (true) {
            System.out.println("\n====== MENU ======");
            System.out.println("1. Cadastrar reserva");
            System.out.println("2. Listar Reservas");
            System.out.println("3. Editar Reservas");
            System.out.println("4. Deletar Reservas");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            try {
                if (opcao == 1) {
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
                        boolean ocupada = service.dataJaReservada(entrada, null);

                        if (ocupada) {
                            System.out.println("❌ Esta data já está reservada. Digite outra.");
                        }else {
                            break;
                        }
                        
                    }

                    LocalDate saida;
                    while (true) {
                        System.out.println("Data de entrada (DD/MM/AA): ");
                        saida = LocalDate.parse(scanner.nextLine(), formatter);
                        boolean ocupada = service.dataJaReservada(saida, null);

                        if (ocupada) {
                            System.out.println("❌ Esta data já está reservada. Digite outra.");
                        }else {
                            break;
                        }

                    }

                    System.out.println("Valor da diária: ");
                    double diaria = scanner.nextDouble();
                    scanner.nextLine();

                    Cliente cliente = new Cliente(nome, telefone, observacao);
                    Usuario usuario = new Usuario("admin", "admin@admin.com", true); // provisório
                    Reserva reserva = new Reserva(null, cliente, usuario, entrada, saida, diaria);

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
                            System.out.println("Entrada: " + r.getDataEntrada().format(formatter));
                            System.out.println("Saída: " + r.getDataSaida().format(formatter));
                            System.out.println("Diária: R$ " + r.getDiaria());
                            System.out.println("Total: R$ " + r.getValorTotal());
                            System.out.println("Registrado por: " + r.getCriadoPor().getEmail());
                        }
                    }

                } else if (opcao == 3) {
                    List<Reserva> reservas = service.listarReservas();
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

                        boolean ok = service.editarReserva(indice, reservaAtualizada);
                        System.out.println(ok ? "✅ Editado com sucesso!" : "❌ Falha ao editar.");
                    }
                } else if (opcao == 4) {
                    List<Reserva> reservas = service.listarReservas();
                    if (reservas.isEmpty()){
                        System.out.println("❌ Nenhuma reserva disponível para deletar.");
                    }else {
                        for (int i = 0; i < reservas.size(); i++) {
                            System.out.println(i + " - " + reservas.get(i).getCliente().getNome());
                        }

                        System.out.println("Digite o numero da reserva que deseja deletar: ");
                        int indice = Integer.parseInt(scanner.nextLine());

                        boolean ok = service.deletarReserva(indice);
                        System.out.println(ok ? "✅ Deletado com sucesso!" : "❌ Falha ao deletar.");
                    }
                } else if (opcao == 0) {
                    System.out.println("Encerrando o programa.");
                    break;
                } else {
                    System.out.println("Opção inválida.");
                }

            } catch (Exception e) {
                System.out.println("❌ Erro: Não foi possível processar a operação");
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}
