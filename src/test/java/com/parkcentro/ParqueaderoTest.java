package com.parkcentro;

import com.parkcentro.modelo.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas unitarias para el sistema de parqueadero ParkCentro.
 * <p>
 * Valida los 7 escenarios requeridos por el caso de estudio:
 * <ol>
 *   <li>Cortesía: carro con 4 min → $0</li>
 *   <li>Cobro normal: carro con 60 min → $7.150</li>
 *   <li>Tope diario: moto con 10 horas → $20.000</li>
 *   <li>Reingreso de placa existente → excepción</li>
 *   <li>Formato de placa inválido → excepción</li>
 *   <li>Ingreso sin cupo disponible → excepción</li>
 *   <li>Salida de placa no registrada → excepción</li>
 * </ol>
 * <p>
 * Se inyectan {@link LocalDateTime} fijos para garantizar resultados
 * deterministas e independientes del reloj del sistema.
 */
class ParqueaderoTest {

    private Parqueadero parqueadero;
    private CalculadoraTarifa calculadora;

    /** Hora base fija para todas las pruebas. */
    private static final LocalDateTime BASE =
            LocalDateTime.of(2026, 10, 5, 8, 0, 0);

    @BeforeEach
    void setUp() {
        parqueadero = new Parqueadero();
        calculadora = new CalculadoraTarifa();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  1. Cortesía: Carro con 4 minutos de permanencia → Cobro $0
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("1. Carro con 4 minutos de permanencia -> Cobro $0 (cortesia)")
    void testCarro4MinutosCobro0() {
        double valor = calculadora.calcular(
                TipoVehiculo.CARRO, BASE, BASE.plusMinutes(4));
        assertEquals(0, valor,
                "Los primeros 5 minutos deben ser gratis (cortesia)");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  2. Carro con 60 minutos → Cobro (60−5)*130 = $7.150
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("2. Carro con 60 minutos de permanencia -> Cobro $7.150")
    void testCarro60MinutosCobro7150() {
        double valor = calculadora.calcular(
                TipoVehiculo.CARRO, BASE, BASE.plusMinutes(60));
        assertEquals(7_150, valor,
                "Debe cobrar (60-5)*130 = $7.150");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  3. Moto con 10 horas → Cobro limitado al tope de $20.000
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("3. Moto con 10 horas de permanencia -> Cobro tope $20.000")
    void testMoto10HorasTopeDiario() {
        // 10 horas = 600 minutos → (600−5)*90 = 53.550 → tope 20.000
        double valor = calculadora.calcular(
                TipoVehiculo.MOTO, BASE, BASE.plusHours(10));
        assertEquals(20_000, valor,
                "El cobro debe estar limitado al tope diario de $20.000 para motos");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  4. Reingreso de placa existente → ParqueaderoException
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("4. Reingreso de placa ya registrada -> ParqueaderoException")
    void testReingresoDePlacaExistente() {
        parqueadero.registrarEntrada("ABC123", TipoVehiculo.CARRO, BASE);

        ParqueaderoException ex = assertThrows(ParqueaderoException.class,
                () -> parqueadero.registrarEntrada("ABC123", TipoVehiculo.CARRO,
                        BASE.plusMinutes(10)));

        assertTrue(ex.getMessage().contains("ya se encuentra"),
                "El mensaje debe indicar que el vehiculo ya esta dentro");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  5. Formato de placa inválido → ParqueaderoException
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("5. Formato de placa invalido -> ParqueaderoException")
    void testPlacaInvalidaException() {
        // "123ABC" no cumple ^[A-Z]{3}\d{3}$ para CARRO
        assertThrows(ParqueaderoException.class,
                () -> parqueadero.registrarEntrada("123ABC", TipoVehiculo.CARRO, BASE),
                "Debe lanzar excepcion por formato de placa invalido");

        // "ABCDEF" no cumple ^[A-Z]{3}\d{2}[A-Z]$ para MOTO
        assertThrows(ParqueaderoException.class,
                () -> parqueadero.registrarEntrada("ABCDEF", TipoVehiculo.MOTO, BASE),
                "Debe lanzar excepcion por formato de placa invalido para moto");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  6. Ingreso sin cupo disponible → ParqueaderoException
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("6. Ingreso sin cupo disponible -> ParqueaderoException")
    void testSinCupoDisponibleException() {
        // Llenar los 40 cupos de carros con placas válidas AAA000..AAA039
        for (int i = 0; i < 40; i++) {
            String placa = String.format("AAA%03d", i);
            parqueadero.registrarEntrada(placa, TipoVehiculo.CARRO, BASE);
        }

        // El cupo 41 debe fallar
        ParqueaderoException ex = assertThrows(ParqueaderoException.class,
                () -> parqueadero.registrarEntrada("BBB000", TipoVehiculo.CARRO, BASE));

        assertTrue(ex.getMessage().toLowerCase().contains("cupo"),
                "El mensaje debe mencionar que no hay cupo disponible");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  7. Salida de placa no registrada → ParqueaderoException
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("7. Salida de placa no registrada -> ParqueaderoException")
    void testSalidaPlacaNoRegistradaException() {
        ParqueaderoException ex = assertThrows(ParqueaderoException.class,
                () -> parqueadero.registrarSalida("ZZZ999", BASE.plusMinutes(30)));

        assertTrue(ex.getMessage().contains("no se encuentra"),
                "El mensaje debe indicar que la placa no esta registrada");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Prueba adicional: reingreso exitoso después de salida
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Adicional: Un vehiculo puede reingresar despues de su salida")
    void testReingresoExitosoDespuesDeSalida() {
        parqueadero.registrarEntrada("ABC123", TipoVehiculo.CARRO, BASE);
        Registro salida = parqueadero.registrarSalida("ABC123", BASE.plusMinutes(60));

        // Verificar que el registro quedó inactivo
        assertFalse(salida.isActivo(), "El registro debe quedar inactivo tras la salida");

        // Verificar que el cobro es correcto
        assertEquals(7_150, salida.getValorPagado());

        // El vehículo puede reingresar sin error
        assertDoesNotThrow(
                () -> parqueadero.registrarEntrada("ABC123", TipoVehiculo.CARRO,
                        BASE.plusHours(2)),
                "Debe permitir reingreso de placa que ya salio del parqueadero");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Prueba adicional: exactamente 5 minutos → cobro $0
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Adicional: Exactamente 5 minutos de permanencia -> Cobro $0")
    void testExactamente5MinutosCobro0() {
        double valor = calculadora.calcular(
                TipoVehiculo.CARRO, BASE, BASE.plusMinutes(5));
        assertEquals(0, valor,
                "Exactamente 5 minutos deben ser gratis (cortesia)");
    }
}
