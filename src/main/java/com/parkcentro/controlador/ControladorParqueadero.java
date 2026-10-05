package com.parkcentro.controlador;

import com.parkcentro.modelo.Parqueadero;
import com.parkcentro.modelo.ParqueaderoException;
import com.parkcentro.modelo.Registro;
import com.parkcentro.modelo.TipoVehiculo;
import com.parkcentro.vista.IVistaParqueadero;

import java.time.LocalDateTime;

/**
 * Controlador del patrón MVC para el parqueadero ParkCentro.
 * <p>
 * Coordina el flujo de datos entre la vista ({@link IVistaParqueadero}) y el
 * modelo ({@link Parqueadero}). <b>No contiene lógica de tarifas ni de
 * validación de placas</b>; esa responsabilidad permanece en la capa de modelo.
 * <p>
 * El método {@link #iniciar()} ejecuta un bucle de menú que funciona
 * tanto con la implementación de consola como con la de Swing, ya que
 * {@link IVistaParqueadero#mostrarMenu()} es bloqueante en ambas.
 */
public class ControladorParqueadero {

    private final Parqueadero parqueadero;
    private final IVistaParqueadero vista;

    /**
     * @param parqueadero instancia del modelo (gestión de estado)
     * @param vista       implementación concreta de la vista
     */
    public ControladorParqueadero(Parqueadero parqueadero,
                                   IVistaParqueadero vista) {
        this.parqueadero = parqueadero;
        this.vista       = vista;
    }

    /**
     * Inicia el bucle principal del programa.
     * Muestra el menú y delega a la acción correspondiente hasta que
     * el usuario selecciona la opción de salir.
     */
    public void iniciar() {
        boolean activo = true;
        while (activo) {
            int opcion = vista.mostrarMenu();
            switch (opcion) {
                case 1  -> registrarEntrada();
                case 2  -> registrarSalida();
                case 3  -> consultarCupos();
                case 4  -> listarVehiculos();
                case 5  -> generarReporte();
                case 0  -> { activo = false; vista.cerrar(); }
                default -> vista.mostrarError("Opcion no valida. Intente de nuevo.");
            }
        }
    }

    /**
     * Solicita placa y tipo a la vista, delega al modelo el registro de entrada.
     */
    public void registrarEntrada() {
        try {
            String placa = vista.pedirPlaca();
            if (placa == null || placa.isBlank()) {
                vista.mostrarError("No se ingreso una placa.");
                return;
            }
            TipoVehiculo tipo = vista.pedirTipo();
            if (tipo == null) {
                vista.mostrarError("Tipo de vehiculo no valido.");
                return;
            }
            Registro registro = parqueadero.registrarEntrada(placa, tipo,
                                                              LocalDateTime.now());
            vista.mostrarMensaje("Entrada registrada exitosamente: "
                    + registro.getVehiculo()
                    + " a las " + registro.getHoraEntrada().toLocalTime());
        } catch (ParqueaderoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    /**
     * Solicita placa a la vista, delega al modelo el registro de salida
     * y muestra el valor a pagar.
     */
    public void registrarSalida() {
        try {
            String placa = vista.pedirPlaca();
            if (placa == null || placa.isBlank()) {
                vista.mostrarError("No se ingreso una placa.");
                return;
            }
            Registro registro = parqueadero.registrarSalida(placa,
                                                             LocalDateTime.now());
            vista.mostrarMensaje(String.format(
                    "Salida registrada: %s | Valor a pagar: $%,.0f",
                    registro.getVehiculo(), registro.getValorPagado()));
        } catch (ParqueaderoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    /**
     * Consulta y muestra los cupos disponibles y ocupados.
     */
    public void consultarCupos() {
        vista.mostrarCupos(
                parqueadero.getOcupados(TipoVehiculo.CARRO),
                parqueadero.getCuposDisponibles(TipoVehiculo.CARRO),
                parqueadero.getOcupados(TipoVehiculo.MOTO),
                parqueadero.getCuposDisponibles(TipoVehiculo.MOTO));
    }

    /**
     * Muestra la lista de vehículos actualmente dentro del parqueadero.
     */
    public void listarVehiculos() {
        vista.mostrarVehiculos(parqueadero.getVehiculosActivos());
    }

    /**
     * Genera y muestra el reporte de cierre detallado por tipo de vehículo (cumple RF6 y Diagrama UML).
     */
    public void generarReporte() {
        String reporte = parqueadero.generarReporte();
        vista.mostrarMensaje(reporte);
    }
}
