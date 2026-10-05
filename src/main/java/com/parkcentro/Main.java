package com.parkcentro;

import com.parkcentro.controlador.ControladorParqueadero;
import com.parkcentro.modelo.Parqueadero;
import com.parkcentro.vista.IVistaParqueadero;
import com.parkcentro.vista.VistaConsola;
import com.parkcentro.vista.VistaSwing;

/**
 * Punto de entrada de la aplicación ParkCentro.
 * <p>
 * Orquesta la inicialización del Modelo, la Vista y el Controlador.
 * <ul>
 *   <li>Sin argumentos o con {@code --consola}: inicia en modo consola.</li>
 *   <li>Con {@code --swing}: inicia con la interfaz gráfica Swing.</li>
 *   <li>Con {@code --dual} o {@code --simultaneo}: ejecuta ambas vistas en simultáneo compartiendo el mismo modelo.</li>
 * </ul>
 */
public class Main {

    public static void main(String[] args) {
        Parqueadero modelo = new Parqueadero();

        // ── Modo DUAL: Consola y Swing funcionando simultáneamente ──────
        if (args.length > 0 && ("--dual".equalsIgnoreCase(args[0]) 
                || "--ambos".equalsIgnoreCase(args[0]) 
                || "--simultaneo".equalsIgnoreCase(args[0]))) {
            System.out.println("=================================================================");
            System.out.println("   MODO DUAL ACTIVADO: Consola y Swing compartiendo el mismo Modelo");
            System.out.println("   Cualquier cambio hecho en Swing se refleja en la Consola y viceversa.");
            System.out.println("=================================================================");

            // Hilo secundario para la vista gráfica Swing
            IVistaParqueadero vistaSwing = new VistaSwing();
            ControladorParqueadero ctrlSwing = new ControladorParqueadero(modelo, vistaSwing);
            Thread hiloSwing = new Thread(ctrlSwing::iniciar, "Hilo-Swing");
            hiloSwing.setDaemon(true);
            hiloSwing.start();

            // Hilo principal para la vista de Consola
            IVistaParqueadero vistaConsola = new VistaConsola();
            ControladorParqueadero ctrlConsola = new ControladorParqueadero(modelo, vistaConsola);
            ctrlConsola.iniciar();
            return;
        }

        // ── Selección individual de la vista ────────────────────────────
        IVistaParqueadero vista;
        if (args.length > 0 && "--swing".equalsIgnoreCase(args[0])) {
            vista = new VistaSwing();
        } else {
            vista = new VistaConsola();
        }

        ControladorParqueadero controlador = new ControladorParqueadero(modelo, vista);
        controlador.iniciar();
    }
}
