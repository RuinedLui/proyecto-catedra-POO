package Vista;

import Modelo.RetiroDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Pantalla de retiros y reporte de cuentas bloqueadas.
 * Crear en NetBeans como "Java Class" (no como JFrame Form).
 */
public class FrmRetiros extends javax.swing.JFrame {

    private final RetiroDAO retiroDAO = new RetiroDAO();

    // Pestaña de retiro
    private javax.swing.JTextField txtNumeroCuenta;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JLabel lblResultado;

    // Pestaña de reporte
    private javax.swing.JTable tablaBloqueadas;
    private javax.swing.table.DefaultTableModel modeloBloqueadas;

    public FrmRetiros() {
        crearFormulario();
    }

    private void crearFormulario() {

        setTitle("Retiros - Banco Académico UDB");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setLayout(new java.awt.BorderLayout(10, 10));

        // Encabezado
        javax.swing.JPanel encabezado = new javax.swing.JPanel();
        encabezado.setBackground(new java.awt.Color(25, 55, 95));

        javax.swing.JLabel titulo = new javax.swing.JLabel("RETIROS BANCARIOS");
        titulo.setForeground(java.awt.Color.WHITE);
        titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        encabezado.add(titulo);

        // Pestañas
        javax.swing.JTabbedPane pestanas = new javax.swing.JTabbedPane();
        pestanas.addTab("Realizar retiro", crearPanelRetiro());
        pestanas.addTab("Cuentas bloqueadas", crearPanelBloqueadas());

        add(encabezado, java.awt.BorderLayout.NORTH);
        add(pestanas, java.awt.BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------
    // PESTAÑA 1: RETIRO
    // ---------------------------------------------------------------
    private javax.swing.JPanel crearPanelRetiro() {

        javax.swing.JPanel formulario = new javax.swing.JPanel(
                new java.awt.GridLayout(2, 2, 10, 10)
        );
        formulario.setBorder(
                javax.swing.BorderFactory.createTitledBorder("Datos del retiro")
        );

        txtNumeroCuenta = new javax.swing.JTextField();
        txtMonto = new javax.swing.JTextField();

        formulario.add(new javax.swing.JLabel("Número de cuenta:"));
        formulario.add(txtNumeroCuenta);
        formulario.add(new javax.swing.JLabel("Monto a retirar ($):"));
        formulario.add(txtMonto);

        javax.swing.JButton btnRetirar = new javax.swing.JButton("Realizar retiro");
        javax.swing.JButton btnLimpiar = new javax.swing.JButton("Limpiar");

        btnRetirar.addActionListener(e -> realizarRetiro());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        javax.swing.JPanel botones = new javax.swing.JPanel();
        botones.add(btnRetirar);
        botones.add(btnLimpiar);

        lblResultado = new javax.swing.JLabel(" ", javax.swing.SwingConstants.CENTER);
        lblResultado.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));

        javax.swing.JPanel panel = new javax.swing.JPanel(
                new java.awt.BorderLayout(10, 10)
        );
        panel.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        panel.add(formulario, java.awt.BorderLayout.NORTH);
        panel.add(lblResultado, java.awt.BorderLayout.CENTER);
        panel.add(botones, java.awt.BorderLayout.SOUTH);

        return panel;
    }

    private void realizarRetiro() {

        try {
            String cuentaTexto = txtNumeroCuenta.getText().trim();
            String montoTexto = txtMonto.getText().trim();

            if (cuentaTexto.isEmpty() || montoTexto.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(
                        this, "Complete el número de cuenta y el monto."
                );
                return;
            }

            int numeroCuenta = Integer.parseInt(cuentaTexto);
            BigDecimal monto = new BigDecimal(montoTexto);

            BigDecimal nuevoSaldo =
                    retiroDAO.realizarRetiro(numeroCuenta, monto);

            lblResultado.setForeground(new java.awt.Color(0, 128, 0));
            lblResultado.setText("Retiro exitoso. Nuevo saldo: $"
                    + nuevoSaldo.toPlainString());

            javax.swing.JOptionPane.showMessageDialog(
                    this, "¡Retiro realizado correctamente!"
            );

            txtMonto.setText("");
            txtMonto.requestFocus();

        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Verifique que el número de cuenta y el monto sean numéricos.",
                    "Datos inválidos",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalArgumentException e) {
            // Validaciones de monto / cuenta
            javax.swing.JOptionPane.showMessageDialog(
                    this, e.getMessage(), "Datos inválidos",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {
            // Cuenta inexistente, inactiva/bloqueada, saldo insuficiente, error de BD
            lblResultado.setForeground(java.awt.Color.RED);
            lblResultado.setText("No se pudo realizar el retiro.");

            javax.swing.JOptionPane.showMessageDialog(
                    this, e.getMessage(), "No se pudo retirar",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarCampos() {
        txtNumeroCuenta.setText("");
        txtMonto.setText("");
        lblResultado.setText(" ");
        txtNumeroCuenta.requestFocus();
    }

    // ---------------------------------------------------------------
    // PESTAÑA 2: REPORTE DE CUENTAS BLOQUEADAS
    // ---------------------------------------------------------------
    private javax.swing.JPanel crearPanelBloqueadas() {

        modeloBloqueadas = new javax.swing.table.DefaultTableModel(
                new Object[]{
                    "Número de cuenta",
                    "Código cliente",
                    "Tipo de cuenta",
                    "Saldo",
                    "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaBloqueadas = new javax.swing.JTable(modeloBloqueadas);
        tablaBloqueadas.setRowHeight(25);

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(tablaBloqueadas);
        scroll.setBorder(
                javax.swing.BorderFactory.createTitledBorder("Cuentas bloqueadas")
        );

        javax.swing.JButton btnGenerar =
                new javax.swing.JButton("Generar reporte");
        btnGenerar.addActionListener(e -> cargarCuentasBloqueadas());

        javax.swing.JPanel botones = new javax.swing.JPanel();
        botones.add(btnGenerar);

        javax.swing.JPanel panel = new javax.swing.JPanel(
                new java.awt.BorderLayout(10, 10)
        );
        panel.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
        panel.add(scroll, java.awt.BorderLayout.CENTER);
        panel.add(botones, java.awt.BorderLayout.SOUTH);

        return panel;
    }

    private void cargarCuentasBloqueadas() {

        try {
            modeloBloqueadas.setRowCount(0);

            List<Object[]> filas = retiroDAO.obtenerCuentasBloqueadas();

            for (Object[] fila : filas) {
                modeloBloqueadas.addRow(fila);
            }

            if (filas.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(
                        this, "No hay cuentas bloqueadas."
                );
            }

        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this, "No se pudo generar el reporte: " + e.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new FrmRetiros().setVisible(true));
    }
}
