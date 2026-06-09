package mvc.view;

import mvc.dto.ItemDTO;
import mvc.dto.LineaOrdenCompraDTO;
import mvc.dto.OrdenDeCompraDTO;
import mvc.dto.ProveedorDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeCompraGUI extends JInternalFrame {
    // Formulario de nueva OC
    private JComboBox<String> cbProveedor;
    private JTextField txtFechaEntrega;
    private JButton btnCrearOC;

    // Formulario de líneas
    private JComboBox<String> cbItem;
    private JTextField txtCantidad, txtPrecio;
    private JButton btnAgregarLinea;

    // Acciones sobre la OC seleccionada
    private JButton btnConfirmar, btnAprobar, btnCancelar;

    // Tablas
    private JTable tablaOC, tablaLineas;
    private DefaultTableModel modeloTabla;
    private DefaultTableModel modeloTablaLineas;

    // Listas paralelas a los combos para devolver el cuit/código elegido sin exponer el modelo
    private List<ProveedorDTO> proveedoresCombo = new ArrayList<>();
    private List<ItemDTO> itemsCombo = new ArrayList<>();

    public OrdenDeCompraGUI() {
        super("Gestión de Órdenes de Compra", true, true, true, true);
        setSize(950, 620);
        setLayout(new BorderLayout());

        inicializarFormulario();
        inicializarTablas();
        inicializarBotonesAccion();

        new mvc.controller.OrdenDeCompraController(this);
    }

    private void inicializarFormulario() {
        JPanel panelNuevaOC = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNuevaOC.setBorder(BorderFactory.createTitledBorder("Nueva Orden de Compra"));

        panelNuevaOC.add(new JLabel("Proveedor:"));
        cbProveedor = new JComboBox<>();
        cbProveedor.setPreferredSize(new Dimension(280, 25));
        panelNuevaOC.add(cbProveedor);

        panelNuevaOC.add(new JLabel("Entrega esperada (AAAA-MM-DD):"));
        txtFechaEntrega = new JTextField(10);
        panelNuevaOC.add(txtFechaEntrega);

        btnCrearOC = new JButton("Crear OC (Borrador)");
        panelNuevaOC.add(btnCrearOC);

        add(panelNuevaOC, BorderLayout.NORTH);
    }

    private void inicializarTablas() {
        // Tabla principal de OC
        modeloTabla = new DefaultTableModel(new String[]{"N° OC", "Proveedor", "Fecha", "Total", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaOC = new JTable(modeloTabla);
        tablaOC.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollOC = new JScrollPane(tablaOC);
        scrollOC.setBorder(BorderFactory.createTitledBorder("Órdenes de Compra"));

        // Formulario para agregar líneas
        JPanel panelLinea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLinea.setBorder(BorderFactory.createTitledBorder("Agregar línea (solo OC en BORRADOR)"));

        panelLinea.add(new JLabel("Ítem:"));
        cbItem = new JComboBox<>();
        cbItem.setPreferredSize(new Dimension(250, 25));
        panelLinea.add(cbItem);

        panelLinea.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(6);
        panelLinea.add(txtCantidad);

        panelLinea.add(new JLabel("Precio acordado ($):"));
        txtPrecio = new JTextField(8);
        panelLinea.add(txtPrecio);

        btnAgregarLinea = new JButton("Agregar línea");
        panelLinea.add(btnAgregarLinea);

        // Sub-tabla de líneas de la OC seleccionada
        modeloTablaLineas = new DefaultTableModel(new String[]{"Código", "Descripción", "Cantidad", "Precio Unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaLineas = new JTable(modeloTablaLineas);
        tablaLineas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLineas = new JScrollPane(tablaLineas);
        scrollLineas.setBorder(BorderFactory.createTitledBorder("Líneas de la OC seleccionada"));

        JPanel panelDetalle = new JPanel(new BorderLayout());
        panelDetalle.add(panelLinea, BorderLayout.NORTH);
        panelDetalle.add(scrollLineas, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollOC, panelDetalle);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);
    }

    private void inicializarBotonesAccion() {
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnConfirmar = new JButton("Confirmar OC");
        btnAprobar = new JButton("Aprobar (Supervisor)");
        btnCancelar = new JButton("Cancelar OC");

        btnAprobar.setEnabled(false); // solo se habilita con una OC en PENDIENTE_APROBACION

        panelAcciones.add(btnConfirmar);
        panelAcciones.add(btnAprobar);
        panelAcciones.add(btnCancelar);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    // --- Métodos públicos que usa el controller ---
    public void cargarComboProveedores(List<ProveedorDTO> proveedores) {
        proveedoresCombo = proveedores;
        cbProveedor.removeAllItems();
        for (ProveedorDTO p : proveedores) {
            cbProveedor.addItem(p.getCuit() + " - " + p.getRazonSocial());
        }
    }

    public void cargarComboItems(List<ItemDTO> items) {
        itemsCombo = items;
        cbItem.removeAllItems();
        for (ItemDTO i : items) {
            cbItem.addItem(i.getCodigo() + " - " + i.getDescripcion());
        }
    }

    public String getCuitProveedorSeleccionado() {
        int idx = cbProveedor.getSelectedIndex();
        return (idx != -1) ? proveedoresCombo.get(idx).getCuit() : null;
    }

    public String getCodigoItemSeleccionado() {
        int idx = cbItem.getSelectedIndex();
        return (idx != -1) ? itemsCombo.get(idx).getCodigo() : null;
    }

    public String getFechaEntrega() { return txtFechaEntrega.getText().trim(); }
    public String getCantidad() { return txtCantidad.getText().trim(); }
    public String getPrecio() { return txtPrecio.getText().trim(); }

    public void setPrecioSugerido(double precio) { txtPrecio.setText(String.valueOf(precio)); }

    public String getNumeroOCSeleccionada() {
        int fila = tablaOC.getSelectedRow();
        if (fila != -1) {
            return modeloTabla.getValueAt(fila, 0).toString();
        }
        return null;
    }

    public void seleccionarOC(String numeroOC) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (modeloTabla.getValueAt(i, 0).toString().equals(numeroOC)) {
                tablaOC.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    public void actualizarTabla(List<OrdenDeCompraDTO> lista) {
        modeloTabla.setRowCount(0);
        for (OrdenDeCompraDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                dto.getNumeroOC(), dto.getRazonSocialProveedor(), dto.getFechaEmision(),
                String.format("$%.2f", dto.getTotalBruto()), dto.getEstado()
            });
        }
    }

    public void actualizarTablaLineas(List<LineaOrdenCompraDTO> lista) {
        modeloTablaLineas.setRowCount(0);
        for (LineaOrdenCompraDTO dto : lista) {
            modeloTablaLineas.addRow(new Object[]{
                dto.getCodigoItem(), dto.getDescripcionItem(), dto.getCantidad(),
                String.format("$%.2f", dto.getPrecioUnitario()), String.format("$%.2f", dto.getSubtotal())
            });
        }
    }

    public void limpiarFormularioLinea() {
        txtCantidad.setText("");
        txtPrecio.setText("");
    }

    public void habilitarAprobar(boolean habilitado) { btnAprobar.setEnabled(habilitado); }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    // Getters de componentes para que el controller suscriba eventos
    public JButton getBtnCrearOC() { return btnCrearOC; }
    public JButton getBtnAgregarLinea() { return btnAgregarLinea; }
    public JButton getBtnConfirmar() { return btnConfirmar; }
    public JButton getBtnAprobar() { return btnAprobar; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JTable getTablaOC() { return tablaOC; }
    public JComboBox<String> getCbItem() { return cbItem; }
}
