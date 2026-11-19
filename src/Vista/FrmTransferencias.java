/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Conexion.ConexionDB;
import Modelo.TransferenciaDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Lenovo
 */
public class FrmTransferencias extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmTransferencias.class.getName());

    private final JTextField txtCuentaOrigen = new JTextField();
    private final JTextField txtCuentaDestino = new JTextField();
    private final JTextField txtMonto = new JTextField();
    private final JButton btnTransferir = new JButton("Realizar transferencia");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnCerrar = new JButton("Cerrar");
    
    /**
     * Creates new form FrmTransferencias
     */
    public FrmTransferencias() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        
        setTitle("Transferencias - Banco Académico UDB");
        setSize(560, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Banner superior azul marino (coherente con las demás ventanas)
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(12, 45, 87));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel lblTitulo = new JLabel("REALIZAR TRANSFERENCIA", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);

        // Panel central con datos de la transferencia
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 16));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 12, 12));
        panelCampos.setBorder(BorderFactory.createTitledBorder("Datos de la transferencia"));

        Font fuenteCampo = new Font("SansSerif", Font.PLAIN, 14);
        txtCuentaOrigen.setFont(fuenteCampo);
        txtCuentaDestino.setFont(fuenteCampo);
        txtMonto.setFont(fuenteCampo);

        JLabel lblOrigen = new JLabel("Número de cuenta origen:");
        JLabel lblDestino = new JLabel("Número de cuenta destino:");
        JLabel lblMonto = new JLabel("Monto a transferir ($):");
        lblOrigen.setFont(fuenteCampo);
        lblDestino.setFont(fuenteCampo);
        lblMonto.setFont(fuenteCampo);

        panelCampos.add(lblOrigen);
        panelCampos.add(txtCuentaOrigen);
        panelCampos.add(lblDestino);
        panelCampos.add(txtCuentaDestino);
        panelCampos.add(lblMonto);
        panelCampos.add(txtMonto);

        panelCentro.add(panelCampos, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelBotones.add(btnTransferir);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnTransferir.addActionListener(e -> procesarTransferencia());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnCerrar.addActionListener(e -> dispose());
    }
    
    private void procesarTransferencia() {
        String origenStr = txtCuentaOrigen.getText().trim();
        String destinoStr = txtCuentaDestino.getText().trim();
        String montoStr = txtMonto.getText().trim().replace(",", ".");

        if (origenStr.isEmpty() || destinoStr.isEmpty() || montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Complete todos los campos (cuenta origen, cuenta destino y monto).",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cuentaOrigen;
        int cuentaDestino;
        BigDecimal monto;

        try {
            cuentaOrigen = Integer.parseInt(origenStr);
            cuentaDestino = Integer.parseInt(destinoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Los números de cuenta deben ser valores numéricos enteros.",
                    "Formato inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            monto = new BigDecimal(montoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un monto numérico válido (ejemplo: 25.50).",
                    "Monto inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cambia ConexionDB.conectar() por el nombre exacto de tu método de conexión si difiere
        try (Connection conn = ConexionDB.conectar()) {
            TransferenciaDAO dao = new TransferenciaDAO();
            boolean exito = dao.realizarTransferencia(conn, cuentaOrigen, cuentaDestino, monto);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "Transferencia realizada con éxito por $" + monto.setScale(2, java.math.RoundingMode.HALF_UP) + ".",
                        "Operación exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "No se pudo completar la transferencia",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarCampos() {
        txtCuentaOrigen.setText("");
        txtCuentaDestino.setText("");
        txtMonto.setText("");
        txtCuentaOrigen.requestFocus();
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
        java.awt.EventQueue.invokeLater(() -> new FrmTransferencias().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
