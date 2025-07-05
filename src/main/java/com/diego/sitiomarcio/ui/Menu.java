package com.diego.sitiomarcio.ui;

import com.diego.sitiomarcio.services.ReservaService;

import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final ReservaService reservaService;

    public Menu(Scanner scanner, ReservaService reservaService) {
        this.scanner = scanner;
        this.reservaService = reservaService;
    }

    public void exibirMenu() throws Exception {
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

            switch (opcao) {
                case 1 -> reservaService.handlerCriarReserva(scanner);
                case 2 -> reservaService.handlerListarReservas(scanner);
                case 3-> reservaService.handlerEditarReservas(scanner);
                case 4 -> reservaService.handlerDeletarReservas(scanner);
                case 0 -> {
                    System.out.println("Encerrando o programa.");
                    return;
                }
                default -> System.out.println("Opção Inválida.");

            }
        }
    }
}
