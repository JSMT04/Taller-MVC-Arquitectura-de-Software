package com.parkcentro.vista;

import com.parkcentro.modelo.Registro;
import com.parkcentro.modelo.TipoVehiculo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementación de {@link IVistaParqueadero} con interfaz gráfica Java Swing.
 * <p>
 * Utiliza una {@link LinkedBlockingQueue} para sincronizar las selecciones de menú
 * con el bucle del controlador: los botones del panel principal encolan la opción
 * elegida y {@link #mostrarMenu()} desencola de forma bloqueante.
 * <p>
 * Los métodos {@link #pedirPlaca()} y {@link #pedirTipoVehiculo()} muestran
 * diálogos modales ({@link JOptionPane} / {@link JTextField} / {@link JComboBox}),
 * satisfaciendo la misma interfaz genérica que la versión de consola.
 */
public class VistaSwing implements IVistaParqueadero {

    // ── Paleta de colores ────────────────────────────────────────────────
    private static final Color BG_DARK       = new Color(30, 30, 46);
    private static final Color BG_PANEL      = new Color(40, 42, 58);
    private static final Color ACCENT        = new Color(137, 180, 250);
    private static final Color ACCENT_HOVER  = new Color(116, 160, 235);
    private static final Color TEXT_PRIMARY   = new Color(205, 214, 244);
    private static final Color TEXT_SECONDARY = new Color(166, 173, 200);
    private static final Color SUCCESS        = new Color(166, 227, 161);
    private static final Color ERROR_COLOR    = new Color(243, 139, 168);
    private static final Color TABLE_ROW_ALT  = new Color(49, 50, 68);

    private final JFrame frame;
    private final JTextArea logArea;
    private final LinkedBlockingQueue<Integer> menuQueue;

    public VistaSwing() {
        this.menuQueue = new LinkedBlockingQueue<>();

        // ── Ventana principal ────────────────────────────────────────────
        frame = new JFrame("ParkCentro - Sistema de Parqueadero");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 620);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.getContentPane().setBackground(BG_DARK);
        frame.setLayout(new BorderLayout(10, 10));

        // ── Encabezado ──────────────────────────────────────────────────
        JLabel header = new JLabel("PARQUEADERO PARKCENTRO", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(ACCENT);
        header.setOpaque(true);
        header.setBackground(BG_PANEL);
        header.setBorder(new EmptyBorder(18, 0, 18, 0));
        frame.add(header, BorderLayout.NORTH);

        // ── Panel de botones ────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new GridLayout(6, 1, 8, 8));
        btnPanel.setBackground(BG_PANEL);
        btnPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] labels = {
            "Registrar Entrada", "Registrar Salida",
            "Consultar Cupos",   "Listar Vehiculos",
            "Generar Reporte",   "Salir"
        };
        int[] opciones = {1, 2, 3, 4, 5, 0};

        for (int i = 0; i < labels.length; i++) {
            JButton btn = crearBoton(labels[i]);
            final int opcion = opciones[i];
            btn.addActionListener(e -> menuQueue.offer(opcion));
            btnPanel.add(btn);
        }
        frame.add(btnPanel, BorderLayout.WEST);

        // ── Área de log ─────────────────────────────────────────────────
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(BG_DARK);
        logArea.setForeground(TEXT_PRIMARY);
        logArea.setCaretColor(ACCENT);
        logArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(BG_PANEL, 2));
        scroll.getViewport().setBackground(BG_DARK);
        frame.add(scroll, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(BG_DARK);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });
        return btn;
    }

    // ── IVistaParqueadero ────────────────────────────────────────────────

    @Override
    public int mostrarMenu() {
        try {
            return menuQueue.take(); // bloquea hasta que se pulse un botón
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    @Override
    public String pedirPlaca() {
        JTextField campo = new JTextField(10);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        int result = JOptionPane.showConfirmDialog(frame, campo,
                "Ingrese la placa del vehiculo:",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && campo.getText() != null) {
            return campo.getText().trim();
        }
        return "";
    }

    @Override
    public TipoVehiculo pedirTipoVehiculo() {
        JComboBox<TipoVehiculo> combo = new JComboBox<>(TipoVehiculo.values());
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        int result = JOptionPane.showConfirmDialog(frame, combo,
                "Seleccione el tipo de vehiculo:",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return (TipoVehiculo) combo.getSelectedItem();
        }
        return null;
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        appendLog("[OK] " + mensaje);
        JOptionPane.showMessageDialog(frame, mensaje, "Informacion",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarError(String error) {
        appendLog("[ERROR] " + error);
        JOptionPane.showMessageDialog(frame, error, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarVehiculos(List<Registro> registros) {
        if (registros.isEmpty()) {
            mostrarMensaje("No hay vehiculos en el parqueadero actualmente.");
            return;
        }

        String[] columnas = {"Placa", "Tipo", "Hora Entrada"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        for (Registro r : registros) {
            modelo.addRow(new Object[]{
                    r.getVehiculo().getPlaca(),
                    r.getVehiculo().getTipo(),
                    r.getHoraEntrada().toString()
            });
        }
        JTable tabla = crearTabla(modelo);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setPreferredSize(new Dimension(500, 280));

        JOptionPane.showMessageDialog(frame, sp,
                "Vehiculos dentro (" + registros.size() + ")",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void mostrarCupos(int carrosOcupados, int carrosDisponibles,
                              int motosOcupadas, int motosDisponibles) {
        String msg = String.format(
                "Carros: %d ocupados / %d disponibles (Cap. %d)\n"
              + "Motos:  %d ocupadas / %d disponibles (Cap. %d)",
                carrosOcupados, carrosDisponibles, carrosOcupados + carrosDisponibles,
                motosOcupadas, motosDisponibles, motosOcupadas + motosDisponibles);
        appendLog(msg);
        JOptionPane.showMessageDialog(frame, msg, "Cupos Disponibles",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarReporte(int totalAtendidos, double ingresosTotales,
                                int carrosActivos, int motosActivas) {
        String msg = String.format(
                "=== REPORTE PARKCENTRO ===\n"
              + "Vehiculos atendidos (salidos): %d\n"
              + "Ingresos totales: $%,.0f\n"
              + "Carros dentro: %d\n"
              + "Motos dentro:  %d",
                totalAtendidos, ingresosTotales, carrosActivos, motosActivas);
        appendLog(msg);
        JOptionPane.showMessageDialog(frame, msg, "Reporte",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void cerrar() {
        appendLog("Cerrando ParkCentro...");
        frame.dispose();
    }

    // ── Utilidades ──────────────────────────────────────────────────────

    private void appendLog(String text) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(text + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private JTable crearTabla(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                                             int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(row % 2 == 0 ? BG_DARK : TABLE_ROW_ALT);
                c.setForeground(TEXT_PRIMARY);
                return c;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setGridColor(BG_PANEL);
        table.setBackground(BG_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(BG_DARK);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(BG_PANEL);
        th.setForeground(ACCENT);
        ((DefaultTableCellRenderer) th.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        return table;
    }
}
