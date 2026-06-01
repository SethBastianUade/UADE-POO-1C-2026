package mvc.view;

import mvc.dto.ProveedorDTO;
import mvc.enums.CondicionImpositiva;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProveedorGUI extends JInternalFrame{
    private JTextField txtCuit, txtRazonSocial, txtTelefono, txtCorreo, txtLimiteDeuda;
    private JComboBox<CondicionImpositiva> cbCondicion;
    private JButton btnGuardar, btnModificar, btnCambiarEstado, btnLimpiar, btnAsociarRubros;
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;

    public ProveedorGUI() {
        super("Gestión de Proveedores", true, true, true, true);
        setSize(750, 450);
        setLayout(new BorderLayout());

        inicializarFormulario();
        inicializarTabla();

        new mvc.controller.ProveedorController(this);
    }

    private void inicializarFormulario() {
        JPanel panelForm = new JPanel(new GridLayout(3, 4, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Proveedor"));

        panelForm.add(new JLabel("CUIT:")); 
        txtCuit = new JTextField(); 
        panelForm.add(txtCuit);

        panelForm.add(new JLabel("Razón Social:")); 
        txtRazonSocial = new JTextField(); 
        panelForm.add(txtRazonSocial);

        panelForm.add(new JLabel("Teléfono:")); 
        txtTelefono = new JTextField(); 
        panelForm.add(txtTelefono);

        panelForm.add(new JLabel("Correo:")); 
        txtCorreo = new JTextField(); 
        panelForm.add(txtCorreo);

        panelForm.add(new JLabel("Condición IVA:")); 
        cbCondicion = new JComboBox<>(CondicionImpositiva.values()); 
        panelForm.add(cbCondicion);

        panelForm.add(new JLabel("Límite Crédito ($):")); 
        txtLimiteDeuda = new JTextField("0.0"); 
        panelForm.add(txtLimiteDeuda);

        // Panel de botones (FlowLayout para no deformarlos)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnGuardar = new JButton("Guardar Nuevo");
        btnModificar = new JButton("Guardar Cambios");
        btnCambiarEstado = new JButton("Activar / Desactivar");
        btnLimpiar = new JButton("Limpiar");
        btnAsociarRubros = new JButton("Asociar Rubros");
        

        btnModificar.setEnabled(false);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnCambiarEstado);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnAsociarRubros);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelForm, BorderLayout.CENTER);
        panelNorte.add(panelBotones, BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(new String[]{"CUIT", "Razón Social", "Condición", "Límite Deuda", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tablaProveedores), BorderLayout.CENTER);
    }

    // --- Getters para el Controlador ---
    public String getCuit() { return txtCuit.getText().trim(); }
    public String getRazonSocial() { return txtRazonSocial.getText().trim(); }
    public String getTelefono() { return txtTelefono.getText().trim(); }
    public String getCorreo() { return txtCorreo.getText().trim(); }
    public String getLimiteDeuda() { return txtLimiteDeuda.getText().trim(); }
    public CondicionImpositiva getCondicionSeleccionada() { return (CondicionImpositiva) cbCondicion.getSelectedItem(); }

    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnModificar() { return btnModificar; }
    public JButton getBtnCambiarEstado() { return btnCambiarEstado; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnAsociarRubros() { return btnAsociarRubros; }
    public JTable getTablaProveedores() { return tablaProveedores; }

    public String getCuitSeleccionadoEnTabla() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila != -1) {
            return modeloTabla.getValueAt(fila, 0).toString();
        }
        return null;
    }

    public void setDatosFormulario(String cuit, String razon, String tel, String correo, CondicionImpositiva cond, double limite) {
        txtCuit.setText(cuit);
        txtCuit.setEnabled(false); // CUIT bloqueado al editar
        txtRazonSocial.setText(razon);
        txtTelefono.setText(tel);
        txtCorreo.setText(correo);
        cbCondicion.setSelectedItem(cond);
        txtLimiteDeuda.setText(String.valueOf(limite));

        btnGuardar.setEnabled(false);
        btnModificar.setEnabled(true);
    }

    public void limpiarFormulario() {
        txtCuit.setText("");
        txtCuit.setEnabled(true);
        txtRazonSocial.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        cbCondicion.setSelectedIndex(0);
        txtLimiteDeuda.setText("0.0");

        btnGuardar.setEnabled(true);
        btnModificar.setEnabled(false);
        tablaProveedores.clearSelection();
    }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    public void actualizarTabla(List<ProveedorDTO> dtos) {
        modeloTabla.setRowCount(0);
        for (ProveedorDTO dto : dtos) {
            modeloTabla.addRow(new Object[]{
                dto.getCuit(), dto.getRazonSocial(), dto.getCondicionImpositiva(),
                "$" + dto.getLimiteDeuda(), dto.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }
    
}
