package com.parkcentro.vista;

import com.parkcentro.modelo.Registro;
import com.parkcentro.modelo.TipoVehiculo;

import java.util.List;

/**
 * Interfaz que abstrae la capa de presentación del parqueadero ParkCentro.
 * <p>
 * Define métodos genéricos de captura de datos y visualización de resultados,
 * sin atarse a una tecnología concreta (consola, Swing, web, etc.).
 * Cada método devuelve tipos de dominio simples ({@code String},
 * {@link TipoVehiculo}, {@code int}) de modo que cualquier implementación
 * —ya sea un {@code Scanner}, un {@code JTextField}/{@code JComboBox} dentro
 * de un diálogo modal, o un formulario web— pueda satisfacerlo.
 */
public interface IVistaParqueadero {

    /**
     * Presenta el menú principal y retorna la opción elegida por el usuario.
     *
     * @return número de opción seleccionada (1-5 para acciones, 0 para salir,
     *         -1 si la entrada no es válida)
     */
    int mostrarMenu();

    /**
     * Solicita al usuario la placa de un vehículo.
     *
     * @return la placa ingresada como {@code String} (sin normalizar)
     */
    String pedirPlaca();

    /**
     * Solicita al usuario el tipo de vehículo.
     *
     * @return {@link TipoVehiculo#CARRO} o {@link TipoVehiculo#MOTO},
     *         o {@code null} si la selección fue inválida
     */
    TipoVehiculo pedirTipoVehiculo();

    /**
     * Muestra un mensaje informativo al usuario.
     *
     * @param mensaje texto del mensaje
     */
    void mostrarMensaje(String mensaje);

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param error texto descriptivo del error
     */
    void mostrarError(String error);

    /**
     * Presenta un listado de registros de vehículos.
     *
     * @param registros lista de registros a mostrar
     */
    void mostrarVehiculos(List<Registro> registros);

    /**
     * Muestra el estado de cupos del parqueadero.
     *
     * @param carrosOcupados     cantidad de carros actualmente dentro
     * @param carrosDisponibles  cupos libres para carros
     * @param motosOcupadas      cantidad de motos actualmente dentro
     * @param motosDisponibles   cupos libres para motos
     */
    void mostrarCupos(int carrosOcupados, int carrosDisponibles,
                      int motosOcupadas, int motosDisponibles);

    /**
     * Presenta un reporte resumen del parqueadero.
     *
     * @param totalAtendidos  vehículos que ya han salido (historial)
     * @param ingresosTotales suma de valores cobrados
     * @param carrosActivos   carros actualmente dentro
     * @param motosActivas    motos actualmente dentro
     */
    void mostrarReporte(int totalAtendidos, double ingresosTotales,
                        int carrosActivos, int motosActivas);

    /**
     * Libera recursos asociados a la vista (Scanner, ventana, etc.).
     */
    void cerrar();
}
