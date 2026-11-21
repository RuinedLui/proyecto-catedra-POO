package Vista;

import Modelo.ReporteMovimientoDAO;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmMovimientos extends JFrame {

    private JTable tblMovimientos;
    private DefaultTableModel modelo;
    private JButton btnActualizar;

    public FrmMovimientos() {

        setTitle("Historial de Movimientos");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modelo = new DefaultTableModel();

        modelo.addColumn("ID");
        modelo.addColumn("Cuenta");
        modelo.addColumn("Destino");
        modelo.addColumn("Tipo");
        modelo.addColumn("Monto");
        modelo.addColumn("Fecha");
        modelo.addColumn("Descripción");

        tblMovimientos = new JTable(modelo);

        JScrollPane scroll = new JScrollPane(tblMovimientos);

        btnActualizar = new JButton("Actualizar");

        btnActualizar.addActionListener(e -> cargarMovimientos());

        add(scroll, "Center");
        add(btnActualizar, "South");

        cargarMovimientos();
    }

    private void cargarMovimientos() {

        modelo.setRowCount(0);

        ReporteMovimientoDAO dao = new ReporteMovimientoDAO();

        List<Object[]> lista = dao.obtenerHistorialCompleto();

        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
    }
}