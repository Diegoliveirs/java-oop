package com.diego.sitiomarcio;

import com.diego.sitiomarcio.services.ReservaService;
import com.diego.sitiomarcio.ui.Menu;

import java.util.Locale;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);
        ReservaService reservaService = new ReservaService();
        Menu menu = new Menu(scanner,reservaService);

        scanner.close();
    }
}
