package com.parkcentro.modelo;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Servicio de dominio puro que calcula el valor a pagar por la permanencia
 * de un vehículo en el parqueadero ParkCentro.
 *
 * <h3>Reglas de cobro:</h3>
 * <ol>
 *   <li>Los primeros {@value #MINUTOS_CORTESIA} minutos son gratuitos (cortesía).</li>
 *   <li>A partir del minuto {@value #MINUTOS_CORTESIA}+1, se cobra la tarifa por minuto
 *       del {@link TipoVehiculo}.</li>
 *   <li>Si el valor calculado supera el tope máximo diario del tipo, se cobra
 *       únicamente el tope.</li>
 * </ol>
 *
 * <p>Esta clase no tiene ninguna dependencia de I/O (System.out, Scanner, Swing).
 */
public class CalculadoraTarifa {

    /** Minutos de cortesía gratuitos. */
    private static final int MINUTOS_CORTESIA = 5;

    /**
     * Calcula el valor a pagar según el tipo de vehículo y el período de permanencia.
     *
     * @param tipo    el tipo de vehículo (define tarifa y tope)
     * @param entrada hora de ingreso al parqueadero
     * @param salida  hora de salida del parqueadero
     * @return el valor a cobrar (0 si permanece ≤ 5 min; tarifa×min si no supera tope; tope si lo supera)
     */
    public double calcular(TipoVehiculo tipo, LocalDateTime entrada, LocalDateTime salida) {
        long minutosTotales = Duration.between(entrada, salida).toMinutes();

        // Cortesía: los primeros 5 minutos son gratis
        if (minutosTotales <= MINUTOS_CORTESIA) {
            return 0;
        }

        long minutosCobrables = minutosTotales - MINUTOS_CORTESIA;
        double total = minutosCobrables * tipo.getTarifaPorMinuto();

        // Tope máximo diario
        return Math.min(total, tipo.getTopeMaximoDiario());
    }

    /**
     * Sobrecarga estática para calcular la tarifa a partir de un objeto Registro.
     * Coincide exactamente con el método especificado en el diagrama UML.
     *
     * @param registro   el registro del vehículo
     * @param horaSalida fecha y hora de salida
     * @return valor a pagar
     */
    public static double calcularTarifa(Registro registro, LocalDateTime horaSalida) {
        CalculadoraTarifa calc = new CalculadoraTarifa();
        return calc.calcular(registro.getVehiculo().getTipo(),
                             registro.getHoraEntrada(),
                             horaSalida);
    }
}
