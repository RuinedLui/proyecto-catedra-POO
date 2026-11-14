
package Vista;

import Modelo.Cuenta;
import Modelo.ReporteCuentasActivasDAO;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FrmReporteCuentasActivas extends JFrame {

    private final ReporteCuentasActivasDAO reporteDAO =
            new ReporteCuentasActivasDAO();

    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;

    public FrmReporteCuentasActivas() {
        configurarVentana();
        crearInterfaz();
        cargarReporte();
    }

    // Configuración de la ventana
    private void configurarVentana() {

        setTitle("Reporte de Cuentas Activas - Banco Académico UDB");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Construcción de la interfaz
    private void crearInterfaz() {

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );
        principal.setBackground(Color.WHITE);

        // Encabezado
        JPanel encabezado = new JPanel();

        encabezado.setBackground(new Color(25, 55, 95));

        JLabel titulo = new JLabel("REPORTE DE CUENTAS ACTIVAS");

        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        encabezado.add(titulo);

        // Modelo de la tabla
        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "Número de cuenta",
                    "Código cliente",
                    "Tipo de cuenta",
                    "Saldo ($)",
                    "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaCuentas = new JTable(modeloTabla);
        tablaCuentas.setRowHeight(28);
        tablaCuentas.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        tablaCuentas.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        JScrollPane scrollTabla = new JScrollPane(tablaCuentas);

        scrollTabla.setBorder(
                BorderFactory.createTitledBorder(
                        "Listado de cuentas activas"
                )
        );

        // Botones
        JPanel panelBotones = new JPanel();

        JButton btnActualizar =
                new JButton("Actualizar reporte");

        JButton btnCerrar =
                new JButton("Cerrar");

        btnActualizar.addActionListener(e -> cargarReporte());

        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);

        // Organizar la ventana
        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(scrollTabla, BorderLayout.CENTER);
        principal.add(panelBotones, BorderLayout.SOUTH);

        add(principal);
    }

    // Consultar las cuentas activas en MySQL
    private void cargarReporte() {

        modeloTabla.setRowCount(0);

        try {

            List<Cuenta> cuentas =
                    reporteDAO.obtenerCuentasActivas();

            for (Cuenta cuenta : cuentas) {

                modeloTabla.addRow(new Object[]{
                    cuenta.getNumeroCuenta(),
                    cuenta.getCodigoCliente(),
                    cuenta.getTipoCuenta(),
                    cuenta.getSaldo(),
                    cuenta.getEstado()
                });
            }

            if (cuentas.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No existen cuentas activas registradas."
                );

            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cargar el reporte: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
