package Vista;

import Modelo.ReporteMovimientoDAO;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class FrmReporteMovimientos extends JFrame {

    private JTable tablaMovimientos;
    private DefaultTableModel modelo;

    public FrmReporteMovimientos() {

        setTitle("Reporte de Historial de Movimientos");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        add(scroll);

        cargarMovimientos();
    }

    private void cargarMovimientos() {

        modelo.setRowCount(0);

        ReporteMovimientoDAO dao = new ReporteMovimientoDAO();

        List<Object[]> movimientos = dao.obtenerHistorialCompleto();

        for (Object[] movimiento : movimientos) {
            modelo.addRow(movimiento);
        }
    }
}