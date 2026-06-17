package mvc.view;

import mvc.dto.OrdenDeCompraDTO;
import mvc.dto.RecepcionLineaDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecepcionMercaderiaGUI extends JInternalFrame {
    private JComboBox<String> cbOC;
    private JTextField txtCantidad;
    private JButton btnRegistrar;
    private JTable tablaLineas;
    private DefaultTableModel modeloTabla;

    // Lista paralela al combo para devolver el N° de OC elegido sin exponer el modelo
    private List<OrdenDeCompraDTO> ordenesCombo = new ArrayList<>();

    public RecepcionMercaderiaGUI() {
        super("Recepción de Mercadería", true, true, true, true);
        setSize(800, 450);
        setLayout(new BorderLayout());

        inicializarFormulario();
        inicializarTabla();

        new mvc.controller.RecepcionController(this);
    }

    private void inicializarFormulario() {
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Orden de Compra (emitida o parcialmente recibida)"));
        panelSeleccion.add(new JLabel("OC:"));
        cbOC = new JComboBox<>();
        cbOC.setPreferredSize(new Dimension(380, 25));
        panelSeleccion.add(cbOC);
        add(panelSeleccion, BorderLayout.NORTH);
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
                new String[]{"Línea", "Código", "Descripción", "Pedida", "Recibida", "Pendiente"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaLineas = new JTable(modeloTabla);
        tablaLineas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaLineas);
        scroll.setBorder(BorderFactory.createTitledBorder("Líneas de la OC"));
        add(scroll, BorderLayout.CENTER);

        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelAccion.add(new JLabel("Cantidad a recibir:"));
        txtCantidad = new JTextField(8);
        panelAccion.add(txtCantidad);
        btnRegistrar = new JButton("Registrar recepción");
        panelAccion.add(btnRegistrar);
        add(panelAccion, BorderLayout.SOUTH);
    }

    // --- Métodos públicos que usa el controller ---
    public void cargarComboOrdenes(List<OrdenDeCompraDTO> ordenes) {
        ordenesCombo = ordenes;
        cbOC.removeAllItems();
        for (OrdenDeCompraDTO oc : ordenes) {
            cbOC.addItem(oc.getNumeroOC() + " - " + oc.getRazonSocialProveedor() + " (" + oc.getEstado() + ")");
        }
    }

    public String getNumeroOCSeleccionada() {
        int idx = cbOC.getSelectedIndex();
        return (idx != -1) ? ordenesCombo.get(idx).getNumeroOC() : null;
    }

    public void actualizarTablaLineas(List<RecepcionLineaDTO> lineas) {
        modeloTabla.setRowCount(0);
        for (RecepcionLineaDTO dto : lineas) {
            modeloTabla.addRow(new Object[]{
                dto.getIdLinea(), dto.getCodigo(), dto.getDescripcion(),
                dto.getCantidadPedida(), dto.getCantidadRecibida(), dto.getCantidadPendiente()
            });
        }
    }

    public int getIdLineaSeleccionada() {
        int fila = tablaLineas.getSelectedRow();
        if (fila == -1) {
            return -1;
        }
        return Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
    }

    public String getCantidad() { return txtCantidad.getText().trim(); }

    public void limpiarCantidad() { txtCantidad.setText(""); }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    public JComboBox<String> getCbOC() { return cbOC; }
    public JButton getBtnRegistrar() { return btnRegistrar; }
}
