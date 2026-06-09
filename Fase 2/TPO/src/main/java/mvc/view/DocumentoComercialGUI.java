package mvc.view;

import mvc.dto.DocumentoComercialDTO;
import mvc.dto.ItemDTO;
import mvc.dto.LineaDocumentoDTO;
import mvc.dto.ProveedorDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentoComercialGUI extends JInternalFrame {
    // Cabecera común
    private JComboBox<String> cbTipo;
    private JComboBox<String> cbProveedor;
    private JTextField txtNumero, txtFechaEmision, txtImporteTotal;

    // Campos específicos (CardLayout)
    private JPanel panelCard;
    private CardLayout cardLayout;
    private JTextField txtCae, txtFechaVencimientoCae, txtBaseIVA, txtMontoIVA; // Factura
    private JTextField txtMotivo;                                               // ND / NC
    private JComboBox<String> cbFacturaOrigen;                                  // ND / NC

    // Líneas
    private JComboBox<String> cbItem;
    private JTextField txtCantidad, txtPrecioUnitario, txtAlicuota;
    private JButton btnAgregarLinea;

    private JButton btnRegistrar, btnAprobar;

    private JTable tablaDocumentos, tablaLineas;
    private DefaultTableModel modeloTabla;
    private DefaultTableModel modeloTablaLineas;

    // Listas paralelas a los combos para devolver el cuit/código sin exponer el modelo
    private List<ProveedorDTO> proveedoresCombo = new ArrayList<>();
    private List<ItemDTO> itemsCombo = new ArrayList<>();

    public DocumentoComercialGUI() {
        super("Gestión de Documentos Comerciales", true, true, true, true);
        setSize(1000, 680);
        setLayout(new BorderLayout());

        inicializarFormulario();
        inicializarTablas();
        inicializarBotonesAccion();

        new mvc.controller.DocumentoComercialController(this);
    }

    private void inicializarFormulario() {
        // Cabecera común a los tres tipos
        JPanel panelCabecera = new JPanel(new GridLayout(3, 4, 10, 5));
        panelCabecera.setBorder(BorderFactory.createTitledBorder("Nuevo Documento"));

        panelCabecera.add(new JLabel("Tipo:"));
        cbTipo = new JComboBox<>(new String[]{"Factura", "Nota de Débito", "Nota de Crédito"});
        cbTipo.addActionListener(e -> cambiarCard());
        panelCabecera.add(cbTipo);

        panelCabecera.add(new JLabel("Proveedor:"));
        cbProveedor = new JComboBox<>();
        panelCabecera.add(cbProveedor);

        panelCabecera.add(new JLabel("N° Documento:"));
        txtNumero = new JTextField();
        panelCabecera.add(txtNumero);

        panelCabecera.add(new JLabel("Fecha emisión (AAAA-MM-DD):"));
        txtFechaEmision = new JTextField();
        panelCabecera.add(txtFechaEmision);

        panelCabecera.add(new JLabel("Importe total ($):"));
        txtImporteTotal = new JTextField();
        panelCabecera.add(txtImporteTotal);
        panelCabecera.add(new JLabel(""));
        panelCabecera.add(new JLabel(""));

        // Campos específicos por tipo
        cardLayout = new CardLayout();
        panelCard = new JPanel(cardLayout);

        JPanel cardFactura = new JPanel(new GridLayout(2, 4, 10, 5));
        cardFactura.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));
        cardFactura.add(new JLabel("CAE:"));
        txtCae = new JTextField();
        cardFactura.add(txtCae);
        cardFactura.add(new JLabel("Vto. CAE (AAAA-MM-DD):"));
        txtFechaVencimientoCae = new JTextField();
        cardFactura.add(txtFechaVencimientoCae);
        cardFactura.add(new JLabel("Base imponible IVA ($):"));
        txtBaseIVA = new JTextField();
        cardFactura.add(txtBaseIVA);
        cardFactura.add(new JLabel("Monto IVA ($):"));
        txtMontoIVA = new JTextField();
        cardFactura.add(txtMontoIVA);

        JPanel cardNota = new JPanel(new GridLayout(1, 4, 10, 5));
        cardNota.setBorder(BorderFactory.createTitledBorder("Datos de la Nota"));
        cardNota.add(new JLabel("Motivo:"));
        txtMotivo = new JTextField();
        cardNota.add(txtMotivo);
        cardNota.add(new JLabel("Factura origen:"));
        cbFacturaOrigen = new JComboBox<>();
        cardNota.add(cbFacturaOrigen);

        panelCard.add(cardFactura, "FACTURA");
        panelCard.add(cardNota, "NOTA");

        // Carga de líneas (antes de registrar)
        JPanel panelLinea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLinea.setBorder(BorderFactory.createTitledBorder("Líneas del documento (se agregan al registrar)"));
        panelLinea.add(new JLabel("Ítem:"));
        cbItem = new JComboBox<>();
        cbItem.setPreferredSize(new Dimension(220, 25));
        panelLinea.add(cbItem);
        panelLinea.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(5);
        panelLinea.add(txtCantidad);
        panelLinea.add(new JLabel("Precio unit. ($):"));
        txtPrecioUnitario = new JTextField(7);
        panelLinea.add(txtPrecioUnitario);
        panelLinea.add(new JLabel("Alícuota IVA (%):"));
        txtAlicuota = new JTextField(5);
        panelLinea.add(txtAlicuota);
        btnAgregarLinea = new JButton("Agregar línea");
        panelLinea.add(btnAgregarLinea);

        JPanel panelRegistrar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegistrar = new JButton("Registrar documento");
        panelRegistrar.add(btnRegistrar);

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.add(panelCabecera);
        panelNorte.add(panelCard);
        panelNorte.add(panelLinea);
        panelNorte.add(panelRegistrar);

        add(panelNorte, BorderLayout.NORTH);
    }

    private void inicializarTablas() {
        modeloTabla = new DefaultTableModel(new String[]{
                "Número", "Tipo", "Proveedor", "Fecha", "Importe", "Saldo", "Cancelación", "Registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaDocumentos = new JTable(modeloTabla);
        tablaDocumentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollDocs = new JScrollPane(tablaDocumentos);
        scrollDocs.setBorder(BorderFactory.createTitledBorder("Documentos Comerciales"));

        modeloTablaLineas = new DefaultTableModel(new String[]{
                "Código", "Descripción", "Cantidad", "Precio Unit.", "Alícuota IVA", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaLineas = new JTable(modeloTablaLineas);
        tablaLineas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLineas = new JScrollPane(tablaLineas);
        scrollLineas.setBorder(BorderFactory.createTitledBorder("Líneas del documento seleccionado / a registrar"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollDocs, scrollLineas);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);
    }

    private void inicializarBotonesAccion() {
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAprobar = new JButton("Aprobar (Supervisor)");
        btnAprobar.setEnabled(false); // solo se habilita con un documento OBSERVADO
        panelAcciones.add(btnAprobar);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    private void cambiarCard() {
        cardLayout.show(panelCard, cbTipo.getSelectedIndex() == 0 ? "FACTURA" : "NOTA");
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

    public void cargarComboFacturas(List<String> numerosFactura) {
        cbFacturaOrigen.removeAllItems();
        for (String numero : numerosFactura) {
            cbFacturaOrigen.addItem(numero);
        }
    }

    public String getTipoSeleccionado() {
        switch (cbTipo.getSelectedIndex()) {
            case 1: return "NOTA_DE_DEBITO";
            case 2: return "NOTA_DE_CREDITO";
            default: return "FACTURA";
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

    public String getNumeroFacturaOrigen() {
        Object seleccion = cbFacturaOrigen.getSelectedItem();
        return (seleccion != null) ? seleccion.toString() : null;
    }

    public String getNumeroDocumento() { return txtNumero.getText().trim(); }
    public String getFechaEmision() { return txtFechaEmision.getText().trim(); }
    public String getImporteTotal() { return txtImporteTotal.getText().trim(); }
    public String getCae() { return txtCae.getText().trim(); }
    public String getFechaVencimientoCae() { return txtFechaVencimientoCae.getText().trim(); }
    public String getBaseIVA() { return txtBaseIVA.getText().trim(); }
    public String getMontoIVA() { return txtMontoIVA.getText().trim(); }
    public String getMotivo() { return txtMotivo.getText().trim(); }
    public String getCantidad() { return txtCantidad.getText().trim(); }
    public String getPrecioUnitario() { return txtPrecioUnitario.getText().trim(); }
    public String getAlicuota() { return txtAlicuota.getText().trim(); }

    public String getNumeroDocumentoSeleccionado() {
        int fila = tablaDocumentos.getSelectedRow();
        if (fila != -1) {
            return modeloTabla.getValueAt(fila, 0).toString();
        }
        return null;
    }

    public void seleccionarDocumento(String numeroDocumento) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (modeloTabla.getValueAt(i, 0).toString().equals(numeroDocumento)) {
                tablaDocumentos.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    public void actualizarTabla(List<DocumentoComercialDTO> lista) {
        modeloTabla.setRowCount(0);
        for (DocumentoComercialDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                    dto.getNumeroDocumento(), dto.getTipoDocumento(), dto.getRazonSocialProveedor(),
                    dto.getFechaEmision(), String.format("$%.2f", dto.getImporteTotal()),
                    String.format("$%.2f", dto.getSaldoPendiente()),
                    dto.getEstadoCancelacion(), dto.getEstadoRegistro()
            });
        }
    }

    public void actualizarTablaLineas(List<LineaDocumentoDTO> lista) {
        modeloTablaLineas.setRowCount(0);
        for (LineaDocumentoDTO dto : lista) {
            modeloTablaLineas.addRow(new Object[]{
                    dto.getCodigoItem(), dto.getDescripcionItem(), dto.getCantidad(),
                    String.format("$%.2f", dto.getPrecioUnitario()),
                    dto.getAlicuotaIVA() + "%", String.format("$%.2f", dto.getSubtotal())
            });
        }
    }

    public void limpiarFormulario() {
        txtNumero.setText("");
        txtFechaEmision.setText("");
        txtImporteTotal.setText("");
        txtCae.setText("");
        txtFechaVencimientoCae.setText("");
        txtBaseIVA.setText("");
        txtMontoIVA.setText("");
        txtMotivo.setText("");
        limpiarFormularioLinea();
    }

    public void limpiarFormularioLinea() {
        txtCantidad.setText("");
        txtPrecioUnitario.setText("");
        txtAlicuota.setText("");
    }

    public void habilitarAprobar(boolean habilitado) { btnAprobar.setEnabled(habilitado); }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    // Getters de componentes para que el controller suscriba eventos
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JButton getBtnAgregarLinea() { return btnAgregarLinea; }
    public JButton getBtnAprobar() { return btnAprobar; }
    public JTable getTablaDocumentos() { return tablaDocumentos; }
    public JComboBox<String> getCbProveedor() { return cbProveedor; }
}
