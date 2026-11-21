package Vista;

import Modelo.ReporteMovimientoDAO;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class FrmReporteClientes extends JFrame {

    private JTable tablaClientes;
    private DefaultTableModel modelo;

    public FrmReporteClientes() {

        setTitle("Reporte de Clientes Registrados");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modelo = new DefaultTableModel();

        modelo.addColumn("Código");
        modelo.addColumn("DUI");
        modelo.addColumn("Nombre");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Correo");
        modelo.addColumn("Estado");

        tablaClientes = new JTable(modelo);

        JScrollPane scroll = new JScrollPane(tablaClientes);

        add(scroll);

        cargarClientes();
    }

    private void cargarClientes() {

        modelo.setRowCount(0);

        ReporteMovimientoDAO dao = new ReporteMovimientoDAO();

        List<Object[]> clientes = dao.obtenerClientesRegistrados();

        for (Object[] cliente : clientes) {
            modelo.addRow(cliente);
        }
    }
}