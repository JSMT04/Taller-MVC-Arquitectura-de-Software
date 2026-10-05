package com.parkcentro.vista;

import com.parkcentro.modelo.Registro;
import com.parkcentro.modelo.TipoVehiculo;

import java.util.List;
import java.util.Scanner;

/**
 * Implementación de {@link IVistaParqueadero} basada en consola (Scanner / System.out).
 * <p>
 * Captura datos del usuario mediante lectura de teclado y muestra información
 * en la salida estándar con formato legible y decorado.
 */
public class VistaConsola implements IVistaParqueadero {

    private final Scanner scanner;

    public VistaConsola() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public int mostrarMenu() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     PARQUEADERO PARKCENTRO - Menu       ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Registrar entrada de vehiculo       ║");
        System.out.println("║  2. Registrar salida de vehiculo        ║");
        System.out.println("║  3. Consultar cupos disponibles         ║");
        System.out.println("║  4. Listar vehiculos dentro             ║");
        System.out.println("║  5. Generar reporte                     ║");
        System.out.println("║  0. Salir                               ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Seleccione una opcion: ");

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public String pedirPlaca() {
        System.out.print("Ingrese la placa del vehiculo: ");
        return scanner.nextLine().trim();
    }

    @Override
    public TipoVehiculo pedirTipoVehiculo() {
        System.out.println("Tipo de vehiculo:");
        System.out.println("  1. Carro");
        System.out.println("  2. Moto");
        System.out.print("Seleccione: ");
        String opcion = scanner.nextLine().trim();
        return switch (opcion) {
            case "1" -> TipoVehiculo.CARRO;
            case "2" -> TipoVehiculo.MOTO;
            default  -> null;
        };
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        System.out.println("\n[OK] " + mensaje);
    }

    @Override
    public void mostrarError(String error) {
        System.out.println("\n[ERROR] " + error);
    }

    @Override
    public void mostrarVehiculos(List<Registro> registros) {
        if (registros.isEmpty()) {
            System.out.println("\nNo hay vehiculos en el parqueadero actualmente.");
            return;
        }
        System.out.println("\nVehiculos actualmente en el parqueadero:");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-12s %-6s %-22s%n", "PLACA", "TIPO", "HORA ENTRADA");
        System.out.println("----------------------------------------------------------------------");
        for (Registro r : registros) {
            System.out.printf("%-12s %-6s %-22s%n",
                    r.getVehiculo().getPlaca(),
                    r.getVehiculo().getTipo(),
                    r.getHoraEntrada().toString());
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Total: " + registros.size() + " vehiculo(s)");
    }

    @Override
    public void mostrarCupos(int carrosOcupados, int carrosDisponibles,
                              int motosOcupadas, int motosDisponibles) {
        System.out.println("\nEstado de Cupos:");
        System.out.println("----------------------------------------");
        System.out.printf("  Carros: %d ocupados / %d disponibles (Total: %d)%n",
                carrosOcupados, carrosDisponibles, carrosOcupados + carrosDisponibles);
        System.out.printf("  Motos:  %d ocupadas / %d disponibles (Total: %d)%n",
                motosOcupadas, motosDisponibles, motosOcupadas + motosDisponibles);
        System.out.println("----------------------------------------");
    }

    @Override
    public void mostrarReporte(int totalAtendidos, double ingresosTotales,
                                int carrosActivos, int motosActivas) {
        System.out.println("\n========================================");
        System.out.println("   REPORTE DEL PARQUEADERO PARKCENTRO");
        System.out.println("========================================");
        System.out.printf("  Vehiculos atendidos (salidos): %d%n", totalAtendidos);
        System.out.printf("  Ingresos totales:         $%,.0f%n", ingresosTotales);
        System.out.printf("  Carros actualmente dentro:   %d%n", carrosActivos);
        System.out.printf("  Motos actualmente dentro:    %d%n", motosActivas);
        System.out.println("========================================");
    }

    @Override
    public void cerrar() {
        System.out.println("\nGracias por usar ParkCentro. Hasta pronto.");
        scanner.close();
    }
}
