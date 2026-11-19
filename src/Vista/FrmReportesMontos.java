/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Conexion.ConexionDB;
import Modelo.ReporteDAO;
import Modelo.ReporteDAO.ResumenMontos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.RoundingMode;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lenovo
 */
public class FrmReportesMontos extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmReportesMontos.class.getName());

    private final DefaultTableModel modeloTabla;
    private final JTable tablaReportes;
    private final JButton btnActualizar = new JButton("Actualizar reporte");
    private final JButton btnCerrar = new JButton("Cerrar");
    
    /**
     * Creates new form FrmReportesMontos
     */
    public FrmReportesMontos() {
        initComponents();
        
        setTitle("Reportes de Montos - Banco Académico UDB");
        setSize(680, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Encabezado azul marino
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(25, 55, 95));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel lblTitulo = new JLabel("REPORTES DE MONTOS", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);

        // Panel central con tabla de los 3 reportes
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 16));

        JPanel panelContenedor = new JPanel(new GridLayout(1, 1));
        panelContenedor.setBorder(BorderFactory.createTitledBorder("Resumen de montos por operación"));

        modeloTabla = new DefaultTableModel(
                new Object[]{"Reporte", "Tipo de movimiento", "Monto total ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaReportes = new JTable(modeloTabla);
        tablaReportes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaReportes.setRowHeight(32);
        tablaReportes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaReportes);
        panelContenedor.add(scrollPane);
        panelCentro.add(panelContenedor, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // Botones inferiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> cargarReportes());
        btnCerrar.addActionListener(e -> dispose());

        cargarReportes();
    }

    private void cargarReportes() {
        modeloTabla.setRowCount(0);

        try (Connection conn = ConexionDB.conectar()) {
            ReporteDAO dao = new ReporteDAO();
            ResumenMontos resumen = dao.obtenerResumenMontos(conn);

            modeloTabla.addRow(new Object[]{
                "Total depositado",
                "DEPOSITO",
                "$" + resumen.getTotalDepositado().setScale(2, RoundingMode.HALF_UP)
            });
            modeloTabla.addRow(new Object[]{
                "Total retirado",
                "RETIRO",
                "$" + resumen.getTotalRetirado().setScale(2, RoundingMode.HALF_UP)
            });
            modeloTabla.addRow(new Object[]{
                "Total transferido",
                "TRANSFERENCIA",
                "$" + resumen.getTotalTransferido().setScale(2, RoundingMode.HALF_UP)
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar los reportes de montos: " + ex.getMessage(),
                    "Error de consulta",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmReportesMontos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
