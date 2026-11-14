
package Vista;

import Modelo.DepositoDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;

public class FrmDepositos extends JFrame {

    // DAO encargado de realizar los depósitos
    private final DepositoDAO depositoDAO = new DepositoDAO();

    // Componentes de la ventana
    private JTextField txtNumeroCuenta;
    private JTextField txtMonto;

    public FrmDepositos() {
        configurarVentana();
        crearFormulario();
    }

    private void configurarVentana() {

        setTitle("Depósitos - Banco Académico UDB");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void crearFormulario() {

        // Panel principal
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );
        principal.setBackground(Color.WHITE);

        // Encabezado
        JPanel encabezado = new JPanel();
        encabezado.setBackground(new Color(25, 55, 95));

        JLabel titulo = new JLabel("REALIZAR DEPÓSITO");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 23));

        encabezado.add(titulo);

        // Formulario
        JPanel formulario = new JPanel(new GridLayout(2, 2, 12, 20));
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(
                BorderFactory.createTitledBorder("Datos del depósito")
        );

        JLabel lblCuenta = new JLabel("Número de cuenta:");
        lblCuenta.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        txtNumeroCuenta = new JTextField();

        JLabel lblMonto = new JLabel("Monto a depositar ($):");
        lblMonto.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        txtMonto = new JTextField();

        formulario.add(lblCuenta);
        formulario.add(txtNumeroCuenta);
        formulario.add(lblMonto);
        formulario.add(txtMonto);

        // Botones
        JPanel botones = new JPanel();

        JButton btnDepositar = new JButton("Realizar depósito");
        JButton btnLimpiar = new JButton("Limpiar");

        btnDepositar.addActionListener(e -> realizarDeposito());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        botones.add(btnDepositar);
        botones.add(btnLimpiar);

        // Agregar componentes
        JPanel contenido = new JPanel(new BorderLayout(15, 15));
        contenido.setBackground(Color.WHITE);
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(contenido, BorderLayout.CENTER);

        add(principal);
    }

    // Método para procesar un depósito
    private void realizarDeposito() {

        try {

            String cuentaTexto = txtNumeroCuenta.getText().trim();
            String montoTexto = txtMonto.getText().trim();

            if (cuentaTexto.isEmpty() || montoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Complete todos los campos.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int numeroCuenta = Integer.parseInt(cuentaTexto);

            BigDecimal monto = new BigDecimal(montoTexto);

            // Ejecutar depósito mediante el DAO
            depositoDAO.realizarDeposito(numeroCuenta, monto);

            JOptionPane.showMessageDialog(
                    this,
                    "¡Depósito realizado correctamente!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarCampos();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un número de cuenta y un monto válidos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al realizar el depósito",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Limpiar campos
    private void limpiarCampos() {

        txtNumeroCuenta.setText("");
        txtMonto.setText("");
        txtNumeroCuenta.requestFocus();
    }
}
