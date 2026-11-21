package Vista;

import Modelo.ReporteMovimientoDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmReporteMovimientosCuenta extends JFrame {

    private JTextField txtCuenta;
    private JButton btnBuscar;

    private JTable tablaMovimientos;
    private DefaultTableModel modelo;

    public FrmReporteMovimientosCuenta() {

        setTitle("Reporte de Movimientos por Cuenta");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelSuperior = new JPanel(new FlowLayout());

        panelSuperior.add(new JLabel("Número de Cuenta:"));

        txtCuenta = new JTextField(10);
        panelSuperior.add(txtCuenta);

        btnBuscar = new JButton("Buscar");
        panelSuperior.add(btnBuscar);

        modelo = new DefaultTableModel();

        modelo.addColumn("ID");
        modelo.addColumn("Cuenta");
        modelo.addColumn("Cuenta Destino");
        modelo.addColumn("Tipo");
        modelo.addColumn("Monto");
        modelo.addColumn("Fecha");
        modelo.addColumn("Descripción");

        tablaMovimientos = new JTable(modelo);

        JScrollPane scroll = new JScrollPane(tablaMovimientos);

        setLayout(new BorderLayout());

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscarMovimientos());
    }

    private void buscarMovimientos() {

        modelo.setRowCount(0);

        try {

            int numeroCuenta = Integer.parseInt(
                txtCuenta.getText().trim()
            );

            ReporteMovimientoDAO dao =
                    new ReporteMovimientoDAO();

            List<Object[]> movimientos =
                    dao.obtenerMovimientosPorCuenta(
                            numeroCuenta);

            for (Object[] fila : movimientos) {
                modelo.addRow(fila);
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un número de cuenta válido."
            );
        }
    }
}