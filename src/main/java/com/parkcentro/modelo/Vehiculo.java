package com.parkcentro.modelo;

/**
 * Entidad que representa un vehículo con placa y tipo.
 * La validación del formato de placa se realiza en el constructor,
 * lanzando {@link ParqueaderoException} si el formato no coincide
 * con el patrón del {@link TipoVehiculo}.
 */
public class Vehiculo {

    private final String placa;
    private final TipoVehiculo tipo;

    /**
     * Crea un vehículo validando el formato de la placa.
     *
     * @param placa la placa del vehículo (se normaliza a mayúsculas y sin espacios)
     * @param tipo  el tipo de vehículo (CARRO o MOTO)
     * @throws ParqueaderoException si el formato de la placa no es válido para el tipo
     */
    public Vehiculo(String placa, TipoVehiculo tipo) {
        if (placa == null || tipo == null) {
            throw new ParqueaderoException("La placa y el tipo de vehículo son obligatorios");
        }
        String placaNormalizada = placa.toUpperCase().trim();
        if (!tipo.validarPlaca(placaNormalizada)) {
            throw new ParqueaderoException(
                    "Formato de placa inválido para " + tipo + ": " + placa
                    + ". Formato esperado: " + tipo.getPatronPlaca().pattern());
        }
        this.placa = placaNormalizada;
        this.tipo  = tipo;
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public String getPlaca()       { return placa; }
    public TipoVehiculo getTipo()  { return tipo;  }

    @Override
    public String toString() {
        return tipo + " [" + placa + "]";
    }
}
