package com.parkcentro.modelo;

/**
 * Excepción personalizada del dominio del parqueadero.
 * Se lanza cuando se viola una regla de negocio: placa inválida,
 * vehículo ya registrado, sin cupo o salida de placa no encontrada.
 */
public class ParqueaderoException extends RuntimeException {

    public ParqueaderoException(String mensaje) {
        super(mensaje);
    }
}
