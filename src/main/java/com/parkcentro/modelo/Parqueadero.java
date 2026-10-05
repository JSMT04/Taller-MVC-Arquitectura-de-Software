package com.parkcentro.modelo;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Gestor principal del estado del parqueadero ParkCentro.
 * <p>
 * Administra los vehículos activos (dentro del parqueadero) y el historial
 * de vehículos que ya han salido. Coordina la validación de cupos,
 * la prevención de reingreso de placas activas y el cálculo de tarifa
 * delegando a {@link CalculadoraTarifa}.
 * <p>
 * Al registrar la salida de un vehículo, el {@link Registro} se marca como
 * inactivo ({@link Registro#cerrar(LocalDateTime, double)}), se retira del
 * mapa de activos y se mueve al historial, liberando el cupo para que esa
 * misma placa pueda volver a ingresar en el futuro.
 * <p>
 * Los métodos de entrada y salida aceptan un {@link LocalDateTime} inyectable
 * para facilitar las pruebas unitarias con tiempos deterministas.
 */
public class Parqueadero {

    /** Mapa de vehículos actualmente dentro: placa normalizada → registro activo. */
    private final Map<String, Registro> vehiculosActivos;

    /** Lista de registros ya cerrados (vehículos que salieron). */
    private final List<Registro> historial;

    /** Calculadora de tarifa inyectada internamente. */
    private final CalculadoraTarifa calculadora;

    public Parqueadero() {
        this.vehiculosActivos = new LinkedHashMap<>();
        this.historial        = new ArrayList<>();
        this.calculadora      = new CalculadoraTarifa();
    }

    // ── Operaciones principales ──────────────────────────────────────────

    /**
     * Registra la entrada de un vehículo al parqueadero.
     *
     * @param placa       placa del vehículo
     * @param tipo        tipo de vehículo (CARRO/MOTO)
     * @param horaEntrada hora de ingreso (inyectable para pruebas)
     * @return el {@link Registro} creado
     * @throws ParqueaderoException si la placa es inválida, ya está dentro o no hay cupo
     */
    public synchronized Registro registrarEntrada(String placa, TipoVehiculo tipo,
                                                  LocalDateTime horaEntrada) {
        // La validación de formato de placa ocurre en el constructor de Vehiculo
        Vehiculo vehiculo = new Vehiculo(placa, tipo);
        String placaNorm  = vehiculo.getPlaca();

        // Verificar que no esté ya dentro del parqueadero
        if (vehiculosActivos.containsKey(placaNorm)) {
            throw new ParqueaderoException(
                    "El vehículo ya se encuentra en el parqueadero");
        }

        // Verificar disponibilidad de cupo para el tipo
        long ocupadosTipo = vehiculosActivos.values().stream()
                .filter(r -> r.getVehiculo().getTipo() == tipo)
                .count();
        if (ocupadosTipo >= tipo.getCapacidadMaxima()) {
            throw new ParqueaderoException(
                    "No hay cupo disponible para " + tipo);
        }

        Registro registro = new Registro(vehiculo, horaEntrada);
        vehiculosActivos.put(placaNorm, registro);
        return registro;
    }

    /**
     * Registra la salida de un vehículo del parqueadero.
     * <p>
     * El registro se marca como inactivo, se retira de los activos y se
     * mueve al historial, liberando el cupo automáticamente.
     *
     * @param placa      placa del vehículo que sale
     * @param horaSalida hora de salida (inyectable para pruebas)
     * @return el {@link Registro} cerrado con el valor calculado
     * @throws ParqueaderoException si la placa no se encuentra entre los activos
     */
    public synchronized Registro registrarSalida(String placa, LocalDateTime horaSalida) {
        String placaNorm = placa.toUpperCase().trim();

        Registro registro = vehiculosActivos.get(placaNorm);
        if (registro == null) {
            throw new ParqueaderoException(
                    "El vehículo con placa " + placa
                    + " no se encuentra en el parqueadero");
        }

        // Calcular tarifa
        TipoVehiculo tipo = registro.getVehiculo().getTipo();
        double valor = calculadora.calcular(tipo, registro.getHoraEntrada(), horaSalida);

        // Cerrar registro (activo → false), retirar de activos, mover a historial
        registro.cerrar(horaSalida, valor);
        vehiculosActivos.remove(placaNorm);
        historial.add(registro);

        return registro;
    }

    // ── Consultas ────────────────────────────────────────────────────────

    /**
     * Cupos disponibles para un tipo de vehículo.
     */
    public synchronized int getCuposDisponibles(TipoVehiculo tipo) {
        return tipo.getCapacidadMaxima() - getOcupados(tipo);
    }

    /**
     * Cantidad de vehículos actualmente dentro para un tipo.
     */
    public synchronized int getOcupados(TipoVehiculo tipo) {
        return (int) vehiculosActivos.values().stream()
                .filter(r -> r.getVehiculo().getTipo() == tipo)
                .count();
    }

    /**
     * Lista inmutable de registros de vehículos actualmente dentro.
     */
    public synchronized List<Registro> getVehiculosActivos() {
        return Collections.unmodifiableList(new ArrayList<>(vehiculosActivos.values()));
    }

    /**
     * Lista inmutable del historial de registros cerrados (ya salieron).
     */
    public synchronized List<Registro> getHistorial() {
        return Collections.unmodifiableList(new ArrayList<>(historial));
    }

    /**
     * Suma de los valores pagados por todos los vehículos que han salido.
     */
    public synchronized double getIngresosTotales() {
        return historial.stream().mapToDouble(Registro::getValorPagado).sum();
    }

    /**
     * Total de vehículos que han sido atendidos (ya salieron).
     */
    public synchronized int getTotalVehiculosAtendidos() {
        return historial.size();
    }
}
