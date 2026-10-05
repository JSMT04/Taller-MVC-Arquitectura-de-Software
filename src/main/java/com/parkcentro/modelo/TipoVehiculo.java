package com.parkcentro.modelo;

import java.util.regex.Pattern;

/**
 * Enumeración que define los tipos de vehículo soportados por el parqueadero ParkCentro.
 * Cada tipo encapsula su capacidad máxima, tarifa por minuto, tope diario y
 * patrón regex de validación de placa.
 */
public enum TipoVehiculo {

    CARRO(40, 130, 45_000, Pattern.compile("^[A-Z]{3}\\d{3}$")),
    MOTO(20,  90, 20_000, Pattern.compile("^[A-Z]{3}\\d{2}[A-Z]$"));

    private final int capacidadMaxima;
    private final int tarifaPorMinuto;
    private final int topeMaximoDiario;
    private final Pattern patronPlaca;

    TipoVehiculo(int capacidadMaxima, int tarifaPorMinuto,
                 int topeMaximoDiario, Pattern patronPlaca) {
        this.capacidadMaxima  = capacidadMaxima;
        this.tarifaPorMinuto  = tarifaPorMinuto;
        this.topeMaximoDiario = topeMaximoDiario;
        this.patronPlaca      = patronPlaca;
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public int getCapacidadMaxima()  { return capacidadMaxima;  }
    public int getTarifaPorMinuto()  { return tarifaPorMinuto;  }
    public int getTopeMaximoDiario() { return topeMaximoDiario; }
    public int getTopeDiario()       { return topeMaximoDiario; }
    public Pattern getPatronPlaca()  { return patronPlaca;      }

    /**
     * Valida si una placa cumple con el formato esperado para este tipo de vehículo.
     * @param placa la placa en mayúsculas a validar
     * @return {@code true} si la placa coincide con el patrón regex
     */
    public boolean validarPlaca(String placa) {
        return patronPlaca.matcher(placa).matches();
    }
}
