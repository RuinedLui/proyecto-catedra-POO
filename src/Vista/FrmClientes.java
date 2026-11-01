package Vista;

import Modelo.Cliente;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class FrmClientes extends JFrame {

    private static final Pattern DUI_REGEX = Pattern.compile("^\\d{8}-\\d$");
    private static final Pattern TEL_REGEX = Pattern.compile("^\\d{4}-?\\d{4}$");
    private static final Pattern CORREO_REGEX = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern NOMBRE_REGEX = Pattern.compile("^[\\p{L}][\\p{L} .'-]*$");

    private static final Border BORDE_ERROR = BorderFactory.createLineBorder(new Color(200, 0, 0), 2);

    private final Cliente.ClienteDAO dao = new Cliente.ClienteDAO();
    private List<Cliente> clientes = new ArrayList<>();
    private List<Cliente> mostrados = new ArrayList<>();

    private JTextField txtCodigo, txtDui, txtNombre, txtTelefono, txtCorreo, txtBuscar;
    private JTextArea txtDireccion;
    private JScrollPane spDireccion;
    private JComboBox<String> cmbEstado;
    private JButton btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnLimpiar;
    private JTable tabla;
    private DefaultTableModel modelo;

    private Border bordeCampoOriginal;
    private Border bordeScrollOriginal;

    public FrmClientes() {
        initComponents();
        recargarClientes();
        limpiarFormulario();
    }


    private void initComponents() {
        setTitle("Gestión de Clientes - Banco Académico UDB");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // no cierra todo el programa si se abre desde el menú
        setSize(900, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));

        txtCodigo = new JTextField(10);
        txtCodigo.setEditable(false);
        txtCodigo.setToolTipText("Se genera automáticamente");
        txtDui = new JTextField(15);
        txtNombre = new JTextField(30);
        txtTelefono = new JTextField(15);
        txtCorreo = new JTextField(30);
        txtDireccion = new JTextArea(3, 30);
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        spDireccion = new JScrollPane(txtDireccion);
        cmbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo", "Bloqueado"});

        bordeCampoOriginal = txtDui.getBorder();
        bordeScrollOriginal = spDireccion.getBorder();

        limitar(txtDui, 10, "[0-9-]");
        limitar(txtTelefono, 9, "[0-9-]");
        limitar(txtNombre, 100, ".");
        limitar(txtCorreo, 100, "\\S");

        agregarFila(form, 0, "Código:", txtCodigo);
        agregarFila(form, 1, "DUI (########-#):", txtDui);
        agregarFila(form, 2, "Nombre completo:", txtNombre);
        agregarFila(form, 3, "Teléfono:", txtTelefono);
        agregarFila(form, 4, "Correo electrónico:", txtCorreo);
        agregarFila(form, 5, "Dirección:", spDireccion);
        agregarFila(form, 6, "Estado:", cmbEstado);

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        txtBuscar = new JTextField(25);
        JPanel buscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        buscar.add(new JLabel("Buscar (código, DUI, nombre o correo):"));
        buscar.add(txtBuscar);

        modelo = new DefaultTableModel(
                new String[]{"Código", "DUI", "Nombre completo", "Teléfono", "Correo", "Dirección", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centro.add(buscar, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }
        });
    }

    private void agregarFila(JPanel panel, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.gridy = fila;
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.weightx = 0;
        panel.add(new JLabel(etiqueta), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, g);
    }

    private void limitar(JTextField campo, int max, String permitido) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isISOControl(c)) {
                    return;
                }
                if (campo.getText().length() >= max || !Character.toString(c).matches(permitido)) {
                    e.consume();
                }
            }
        });
    }

    private void guardar() {
        if (!validarCampos(0)) {
            return;
        }
        Cliente c = leerFormulario();
        if (dao.insertar(c)) {
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            recargarClientes();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el cliente.\nRevise la conexión con la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int codigo = Integer.parseInt(txtCodigo.getText());
        if (!validarCampos(codigo)) {
            return;
        }
        Cliente c = leerFormulario();
        c.setCodigoCliente(codigo);
        if (dao.actualizar(c)) {
            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            recargarClientes();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el cliente.\nRevise la conexión con la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + txtNombre.getText() + "?\n"
                + "También se eliminarán todas las cuentas bancarias asociadas a este cliente.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.eliminar(Integer.parseInt(txtCodigo.getText()))) {
            JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            recargarClientes();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el cliente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos(int codigoActual) {
        restaurarBordes();
        List<String> errores = new ArrayList<>();
        JComponent primero = null;

        String dui = txtDui.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (!DUI_REGEX.matcher(dui).matches()) {
            errores.add("DUI inválido. Use el formato ########-# (ej. 01234567-8).");
            primero = marcar(txtDui, primero);
        } else {
            for (Cliente c : clientes) {
                if (c.getDui().equals(dui) && c.getCodigoCliente() != codigoActual) {
                    errores.add("Ya existe un cliente con ese DUI.");
                    primero = marcar(txtDui, primero);
                    break;
                }
            }
        }

        if (nombre.isEmpty() || !NOMBRE_REGEX.matcher(nombre).matches()) {
            errores.add("El nombre es obligatorio y solo puede contener letras y espacios.");
            primero = marcar(txtNombre, primero);
        }

        if (!TEL_REGEX.matcher(telefono).matches()) {
            errores.add("Teléfono inválido. Use 8 dígitos (ej. 7123-4567).");
            primero = marcar(txtTelefono, primero);
        }

        if (!CORREO_REGEX.matcher(correo).matches()) {
            errores.add("Correo electrónico inválido (ej. nombre@correo.com).");
            primero = marcar(txtCorreo, primero);
        }

        if (direccion.isEmpty()) {
            errores.add("La dirección es obligatoria.");
            primero = marcar(spDireccion, primero);
        }

        if (cmbEstado.getSelectedItem() == null) {
            errores.add("Seleccione un estado.");
            primero = marcar(cmbEstado, primero);
        }

        if (!errores.isEmpty()) {
            StringBuilder msg = new StringBuilder("Corrija los siguientes campos:\n");
            for (String err : errores) {
                msg.append("• ").append(err).append("\n");
            }
            JOptionPane.showMessageDialog(this, msg.toString(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
            if (primero == spDireccion) {
                txtDireccion.requestFocus();
            } else if (primero != null) {
                primero.requestFocus();
            }
            return false;
        }
        return true;
    }

    private JComponent marcar(JComponent campo, JComponent primero) {
        campo.setBorder(BORDE_ERROR);
        return primero == null ? campo : primero;
    }

    private void restaurarBordes() {
        txtDui.setBorder(bordeCampoOriginal);
        txtNombre.setBorder(bordeCampoOriginal);
        txtTelefono.setBorder(bordeCampoOriginal);
        txtCorreo.setBorder(bordeCampoOriginal);
        spDireccion.setBorder(bordeScrollOriginal);
        cmbEstado.setBorder(null);
    }

    private Cliente leerFormulario() {
        Cliente c = new Cliente();
        c.setDui(txtDui.getText().trim());
        c.setNombreCompleto(txtNombre.getText().trim());
        c.setTelefono(txtTelefono.getText().trim());
        c.setCorreoElectronico(txtCorreo.getText().trim());
        c.setDireccion(txtDireccion.getText().trim());
        c.setEstado((String) cmbEstado.getSelectedItem());
        return c;
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        txtCodigo.setText("");
        txtDui.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        cmbEstado.setSelectedIndex(0);
        restaurarBordes();

        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        txtDui.requestFocus();
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= mostrados.size()) {
            return;
        }
        Cliente c = mostrados.get(fila);
        txtCodigo.setText(String.valueOf(c.getCodigoCliente()));
        txtDui.setText(c.getDui());
        txtNombre.setText(c.getNombreCompleto());
        txtTelefono.setText(c.getTelefono() == null ? "" : c.getTelefono());
        txtCorreo.setText(c.getCorreoElectronico() == null ? "" : c.getCorreoElectronico());
        txtDireccion.setText(c.getDireccion() == null ? "" : c.getDireccion());
        cmbEstado.setSelectedItem(c.getEstado());
        restaurarBordes();

        btnGuardar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void recargarClientes() {
        clientes = dao.obtenerClientes();
        filtrarTabla();
    }

    private void filtrarTabla() {
        String texto = txtBuscar == null ? "" : txtBuscar.getText().trim().toLowerCase();
        mostrados = new ArrayList<>();
        modelo.setRowCount(0);

        for (Cliente c : clientes) {
            boolean coincide = texto.isEmpty()
                    || String.valueOf(c.getCodigoCliente()).contains(texto)
                    || (c.getDui() != null && c.getDui().toLowerCase().contains(texto))
                    || (c.getNombreCompleto() != null && c.getNombreCompleto().toLowerCase().contains(texto))
                    || (c.getCorreoElectronico() != null && c.getCorreoElectronico().toLowerCase().contains(texto));
            if (coincide) {
                mostrados.add(c);
                modelo.addRow(new Object[]{
                    c.getCodigoCliente(), c.getDui(), c.getNombreCompleto(), c.getTelefono(),
                    c.getCorreoElectronico(), c.getDireccion(), c.getEstado()
                });
            }
        }
    }

    // Para probar el módulo de forma independiente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new FrmClientes().setVisible(true);
        });
    }
}
