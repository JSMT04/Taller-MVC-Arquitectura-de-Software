# 🚗 Sistema de Control de Parqueadero "ParkCentro"
### Universidad EAN — Arquitectura de Software
**Patrón de Diseño Arquitectónico: Modelo-Vista-Controlador (MVC)**

---

## 📋 Descripción del Proyecto

El sistema **ParkCentro** es una solución de software desarrollada en **Java (JDK 17+)** orientada a automatizar y optimizar la administración de entradas, salidas, cálculo de tarifas, disponibilidad de cupos y generación de informes de cierre para el parqueadero de la Universidad EAN.

La solución ha sido construida bajo el principio de **desacoplamiento total**, garantizando que la lógica de negocio (Modelo) sea 100% independiente de los mecanismos de interacción con el usuario (Vistas de Consola o Swing) y de la infraestructura de almacenamiento.

---

## ⚙️ Reglas de Negocio y Requerimientos

| Parámetro | Carros | Motos |
| :--- | :---: | :---: |
| **Capacidad Máxima** | 40 cupos | 20 cupos |
| **Tarifa por Minuto** | $130 / min | $90 / min |
| **Tiempo de Cortesía** | Primeros 5 min gratis ($0) | Primeros 5 min gratis ($0) |
| **Tope Máximo Diario (Tarifa Plena)** | $45.000 | $20.000 |
| **Formato de Placa (Regex)** | `^[A-Z]{3}\d{3}$` (ej: `ABC123`) | `^[A-Z]{3}\d{2}[A-Z]$` (ej: `ABC12D`) |

- **Excepciones de Dominio**: Todas las inconsistencias (reingreso de placa activa, cupos agotados, placa mal formada o salida de vehículo no registrado) lanzan de forma controlada la excepción propia `ParqueaderoException`.
- **Cierre y Reingreso**: Al registrar la salida de un vehículo, su registro pasa de activo a inactivo (`activo = false`) y se mueve al historial de atención, liberando inmediatamente el cupo para permitir futuros reingresos de la misma placa.

---

### Separación de Responsabilidades:
1. **Modelo (`com.parkcentro.modelo`)**:
   - `TipoVehiculo`: Enum con constantes de negocio (capacidad, tarifa, tope y regex).
   - `Vehiculo`: Entidad que valida la placa según su tipo en el momento de instanciarse.
   - `Registro`: Modela la permanencia temporal, hora de entrada, salida, valor pagado y estado activo/inactivo.
   - `CalculadoraTarifa`: Servicio puro de dominio que computa el valor aplicando cortesía y topes.
   - `Parqueadero`: Gestor central del parqueadero; soporta inyección de `LocalDateTime` para pruebas deterministas y sincronización de hilos (`thread-safe`).
   - `ParqueaderoException`: Excepción de dominio para violaciones de reglas de negocio.
   - **CERO dependencias** de `System.out`, `Scanner` o Swing.

2. **Vista (`com.parkcentro.vista`)**:
   - `IVistaParqueadero`: Interfaz que define el contrato de captura y despliegue (genérica para consola, GUI o web).
   - `VistaConsola`: Implementación interactiva en terminal con menú amigable.
   - `VistaSwing`: Implementación gráfica moderna (Dark Theme) con `JFrame`, `JTable`, diálogos modales y colas sincronizadas (`BlockingQueue`).

3. **Controlador (`com.parkcentro.controlador`)**:
   - `ControladorParqueadero`: Recibe peticiones de la vista, orquesta las llamadas al modelo y retorna los resultados formateados a la vista. No calcula tarifas ni captura datos directamente.

4. **Punto de Entrada (`com.parkcentro`)**:
   - `Main`: Inicializa el modelo, la vista seleccionada y el controlador.

---

## 📂 Estructura del Repositorio

```text
├── .gitignore
├── README.md
├── Diagrama UML.png
├── lib/
│   └── junit-platform-console-standalone-1.10.2.jar
├── src/
│   ├── main/java/com/parkcentro/
│   │   ├── Main.java                       # Punto de entrada (Consola / Swing / Dual)
│   │   ├── controlador/
│   │   │   └── ControladorParqueadero.java
│   │   ├── modelo/
│   │   │   ├── CalculadoraTarifa.java
│   │   │   ├── Parqueadero.java
│   │   │   ├── ParqueaderoException.java
│   │   │   ├── Registro.java
│   │   │   ├── TipoVehiculo.java
│   │   │   └── Vehiculo.java
│   │   └── vista/
│   │       ├── IVistaParqueadero.java
│   │       ├── VistaConsola.java
│   │       └── VistaSwing.java
│   └── test/java/com/parkcentro/
│       └── ParqueaderoTest.java            # Suite de pruebas unitarias JUnit 5
```

---

## 🚀 Compilación y Ejecución

### Prerrequisitos
- **Java Development Kit (JDK 17 o superior)** instalado y configurado en el PATH del sistema (`java -version`, `javac -version`).

### 1. Compilar el Proyecto
Abre una terminal (PowerShell o CMD) en la raíz del proyecto y ejecuta:

```powershell
javac -d build -encoding UTF-8 (Get-ChildItem -Recurse -Filter *.java -Path src\main\java).FullName
```

---

### 2. Formas de Ejecución

#### Opción A: Modo Consola (Terminal)
Inicia la aplicación en la interfaz tradicional de consola:
```powershell
java -cp build com.parkcentro.Main
```
*(o explícitamente: `java -cp build com.parkcentro.Main --consola`)*

#### Opción B: Modo Gráfico (Java Swing)
Abre la interfaz gráfica de usuario con ventanas, botones y tablas:
```powershell
java -cp build com.parkcentro.Main --swing
```

---

### 🌟 Opción C: Modo DUAL (Consola y Swing Simultáneamente)
¿Quieres ver en vivo el poder de la arquitectura MVC y la sincronización del modelo?

El sistema incluye el modo `--dual`, el cual **lanza la ventana de Swing y la terminal de Consola al mismo tiempo**, compartiendo **exactamente la misma instancia del Modelo en memoria**:

```powershell
java -cp build com.parkcentro.Main --dual
```

#### ¿Cómo interactuar en modo simultáneo?
1. Registra la entrada de un vehículo desde la ventana de **Swing** (botón *"Registrar Entrada"* con placa `ABC123`, tipo `CARRO`).
2. Ve a la **Consola**, digita la opción `4` (*"Listar vehiculos dentro"*) o `3` (*"Consultar cupos disponibles"*). ¡Verás que el vehículo ingresado desde Swing aparece inmediatamente en la consola!
3. Ahora registra la salida del vehículo desde la **Consola** (opción `2`).
4. Haz clic en *"Listar Vehiculos"* o *"Generar Reporte"* en la ventana de **Swing**: verás que el vehículo ya salió y el recaudo se ha sumado al reporte en tiempo real.

> **Fundamento técnico:** Gracias a que la clase `Parqueadero` implementa sincronización de acceso (`thread-safe`) y el patrón MVC separa el estado de la presentación, múltiples vistas independientes pueden operar concurrentemente sobre el mismo modelo de dominio.

---

## 🧪 Pruebas Unitarias (JUnit 5)

La suite de pruebas en `src/test/java/com/parkcentro/ParqueaderoTest.java` evalúa de forma automatizada los 7 escenarios requeridos y 2 casos de frontera adicionales:

1. **Carro con 4 minutos de permanencia**: Cobro exacto de **$0** (cortesía aplicada).
2. **Carro con 60 minutos de permanencia**: Cobro exacto de **$7.150** `((60 - 5) * 130)`.
3. **Moto con 10 horas de permanencia**: Cobro limitado al tope diario de **$20.000**.
4. **Intento de reingreso de vehículo activo**: Lanza `ParqueaderoException` ("El vehículo ya se encuentra en el parqueadero").
5. **Formato de placa inválido**: Lanza `ParqueaderoException` para carro o moto con placas mal formadas.
6. **Ingreso superando capacidad máxima**: Lanza `ParqueaderoException` al intentar ingresar el vehículo 41 de carros.
7. **Salida de placa no registrada**: Lanza `ParqueaderoException` ("El vehículo no se encuentra en el parqueadero").
8. **Prueba Adicional - Reingreso post-salida**: Comprueba que una vez el vehículo sale y su registro se marca como inactivo, el cupo se libera y el vehículo puede volver a entrar más tarde.
9. **Prueba Adicional - Frontera de 5 minutos**: Valida que exactamente a los 5 minutos el cobro sea $0.

### Compilar y Ejecutar las Pruebas:
```powershell
# 1. Compilar pruebas
javac -cp "lib\junit-platform-console-standalone-1.10.2.jar;build" -d build\test -encoding UTF-8 (Get-ChildItem -Recurse -Filter *.java -Path src\test\java).FullName

# 2. Ejecutar suite con JUnit 5 Console Launcher
java -jar lib\junit-platform-console-standalone-1.10.2.jar execute --class-path "build;build\test" --scan-classpath
```

**Resultado:**
```text
Test run finished after 88 ms
[         4 containers found      ]
[         4 containers successful ]
[         9 tests found           ]
[         9 tests successful      ]
[         0 tests failed          ]
```

---


## 👨‍💻 Autor
- **Estudiante:** Juan Martelo (JSMT04)
- **Materia:** Arquitectura de Software
- **Institución:** Universidad EAN
