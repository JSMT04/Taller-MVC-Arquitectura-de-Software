package com.parkcentro.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registro de permanencia de un vehículo en el parqueadero.
 * <p>
 * Un registro nace <b>activo</b> al momento de la entrada. Al registrar la salida
 * se invoca {@link #cerrar(LocalDateTime, double)}, que marca el registro como
 * <b>inactivo</b>, fija la hora de salida y el valor pagado. Esto permite que
 * el cupo se libere y la misma placa pueda reingresar en el futuro.
 */
public class Registro {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Vehiculo vehiculo;
    private final LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private double valorPagado;
    private boolean activo;

    /**
     * Crea un registro activo con la hora de entrada proporcionada.
     *
     * @param vehiculo    el vehículo que ingresa
     * @param horaEntrada la marca de tiempo de ingreso (inyectable para pruebas)
     */
    public Registro(Vehiculo vehiculo, LocalDateTime horaEntrada) {
        this.vehiculo    = vehiculo;
        this.horaEntrada = horaEntrada;
        this.activo      = true;
        this.valorPagado = 0;
    }

    /**
     * Cierra el registro al momento de la salida.
     * Marca el registro como <b>inactivo</b>, asigna la hora de salida y
     * el valor calculado a pagar.
     *
     * @param horaSalida  la marca de tiempo de salida
     * @param valorPagado el valor calculado por la permanencia
     */
    public void cerrar(LocalDateTime horaSalida, double valorPagado) {
        this.horaSalida  = horaSalida;
        this.valorPagado = valorPagado;
        this.activo      = false;
    }

    /**
     * Alias de cerrar() coincidente con el nombre de método del Diagrama UML.
     */
    public void registrarSalida(LocalDateTime horaSalida, double valorPagado) {
        cerrar(horaSalida, valorPagado);
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public Vehiculo getVehiculo()         { return vehiculo;    }
    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public LocalDateTime getHoraSalida()  { return horaSalida;  }
    public double getValorPagado()        { return valorPagado; }
    public boolean isActivo()             { return activo;      }

    @Override
    public String toString() {
        String salida = (horaSalida != null) ? FMT.format(horaSalida) : "---";
        return String.format("%-10s | %-5s | Entrada: %s | Salida: %s | Valor: $%,.0f",
                vehiculo.getPlaca(), vehiculo.getTipo(),
                FMT.format(horaEntrada), salida, valorPagado);
    }
}
