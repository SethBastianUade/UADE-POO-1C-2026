package mvc.view;

import mvc.dto.DocumentoComercialDTO;
import mvc.dto.MedioDePagoDTO;
import mvc.dto.OrdenDePagoDTO;
import mvc.dto.ProveedorDTO;
import mvc.dto.RetencionDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenDePagoGUI extends JInternalFrame {
    private static final int COL_INCLUIR = 0;
    private static final int COL_NUMERO = 1;
    private static final int COL_MONTO_APLICAR = 6;

    // Selector de proveedor
    private JComboBox<String> cbProveedor;

    // Pestaña "Nueva OP": documentos pendientes
    private JTable tablaDocumentos;
    private DefaultTableModel modeloTabla;

    // Retenciones (solo lectura)
    private JTable tablaRetenciones;
    private DefaultTableModel modeloTablaRetenciones;

    // Medios de pago
    private JComboBox<String> cbTipoMedio;
    private JPanel panelCardMedio;
    private CardLayout cardLayoutMedio;
    private JTextField txtImporteMedio;
    private JTextField txtNroReferencia, txtCuentaOrigen;
    private JTextField txtNroCheque, txtBanco, txtFechaEmisionCheque, txtFechaVencimientoCheque, txtFirmante;
    private JButton btnAgregarMedio, btnQuitarMedio;
    private JTable tablaMedios;
    private DefaultTableModel modeloTablaMedios;

    // Totales y emisión
    private JLabel lblTotalBruto, lblTotalRetenido, lblTotalNeto, lblTotalMedios;
    private JButton btnEmitir;

    // Pestaña "OP emitidas"
    private JTable tablaOP;
    private DefaultTableModel modeloTablaOP;

    // Lista paralela al combo de proveedores
    private List<ProveedorDTO> proveedoresCombo = new ArrayList<>();

    public OrdenDePagoGUI() {
        super("Gestión de Órdenes de Pago", true, true, true, true);
        setSize(1050, 720);
        setLayout(new BorderLayout());

        inicializarSelectorProveedor();

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Nueva Orden de Pago", inicializarPanelNuevaOP());
        pestanias.addTab("OP emitidas", inicializarPanelHistorial());
        add(pestanias, BorderLayout.CENTER);

        new mvc.controller.OrdenDePagoController(this);
    }

    private void inicializarSelectorProveedor() {
        JPanel panelProveedor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelProveedor.setBorder(BorderFactory.createTitledBorder("Proveedor a pagar"));
        panelProveedor.add(new JLabel("Proveedor:"));
        cbProveedor = new JComboBox<>();
        cbProveedor.setPreferredSize(new Dimension(320, 25));
        panelProveedor.add(cbProveedor);
        add(panelProveedor, BorderLayout.NORTH);
    }

    private JPanel inicializarPanelNuevaOP() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 1. Documentos pendientes (checkbox + monto a aplicar editable)
        modeloTabla = new DefaultTableModel(new String[]{
                "Incluir", "Número", "Tipo", "Fecha", "Importe", "Saldo", "Monto a aplicar ($)"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == COL_INCLUIR) ? Boolean.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COL_INCLUIR || column == COL_MONTO_APLICAR;
            }
        };
        tablaDocumentos = new JTable(modeloTabla);
        tablaDocumentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollDocs = new JScrollPane(tablaDocumentos);
        scrollDocs.setPreferredSize(new Dimension(1000, 140));
        scrollDocs.setBorder(BorderFactory.createTitledBorder("1. Documentos pendientes: marque cuáles pagar y el monto a aplicar"));
        panel.add(scrollDocs);

        // 2. Retenciones calculadas (solo lectura)
        modeloTablaRetenciones = new DefaultTableModel(new String[]{
                "Tipo impuesto", "Base imponible", "%", "Monto retenido", "¿Con exclusión?"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRetenciones = new JTable(modeloTablaRetenciones);
        JScrollPane scrollRetenciones = new JScrollPane(tablaRetenciones);
        scrollRetenciones.setPreferredSize(new Dimension(1000, 100));
        scrollRetenciones.setBorder(BorderFactory.createTitledBorder("2. Retenciones (calculadas automáticamente sobre el total bruto)"));
        panel.add(scrollRetenciones);

        // 3. Medios de pago
        panel.add(inicializarPanelMedios());

        // 4. Totales + emitir
        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        panelTotales.setBorder(BorderFactory.createTitledBorder("4. Totales"));
        lblTotalBruto = new JLabel("Bruto: $0.00");
        lblTotalRetenido = new JLabel("Retenido: $0.00");
        lblTotalNeto = new JLabel("NETO A PAGAR: $0.00");
        lblTotalNeto.setFont(lblTotalNeto.getFont().deriveFont(Font.BOLD));
        lblTotalMedios = new JLabel("Medios cargados: $0.00");
        btnEmitir = new JButton("Emitir Orden de Pago");
        panelTotales.add(lblTotalBruto);
        panelTotales.add(lblTotalRetenido);
        panelTotales.add(lblTotalNeto);
        panelTotales.add(lblTotalMedios);
        panelTotales.add(btnEmitir);
        panel.add(panelTotales);

        return panel;
    }

    private JPanel inicializarPanelMedios() {
        JPanel panelMedios = new JPanel(new BorderLayout());
        panelMedios.setBorder(BorderFactory.createTitledBorder("3. Medios de pago (deben cubrir el neto a pagar)"));

        JPanel panelCarga = new JPanel();
        panelCarga.setLayout(new BoxLayout(panelCarga, BoxLayout.Y_AXIS));

        JPanel filaTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaTipo.add(new JLabel("Tipo:"));
        cbTipoMedio = new JComboBox<>(new String[]{"Efectivo", "Transferencia Bancaria", "Cheque Propio", "Cheque de Terceros"});
        cbTipoMedio.addActionListener(e -> cambiarCardMedio());
        filaTipo.add(cbTipoMedio);
        filaTipo.add(new JLabel("Importe ($):"));
        txtImporteMedio = new JTextField(8);
        filaTipo.add(txtImporteMedio);
        panelCarga.add(filaTipo);

        // Campos dinámicos por tipo de medio
        cardLayoutMedio = new CardLayout();
        panelCardMedio = new JPanel(cardLayoutMedio);

        panelCardMedio.add(new JPanel(), "EFECTIVO"); // sin campos adicionales

        JPanel cardTransferencia = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardTransferencia.add(new JLabel("N° Referencia:"));
        txtNroReferencia = new JTextField(10);
        cardTransferencia.add(txtNroReferencia);
        cardTransferencia.add(new JLabel("Cuenta origen:"));
        txtCuentaOrigen = new JTextField(12);
        cardTransferencia.add(txtCuentaOrigen);
        panelCardMedio.add(cardTransferencia, "TRANSFERENCIA");

        JPanel cardCheque = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardCheque.add(new JLabel("N° Cheque:"));
        txtNroCheque = new JTextField(7);
        cardCheque.add(txtNroCheque);
        cardCheque.add(new JLabel("Banco:"));
        txtBanco = new JTextField(8);
        cardCheque.add(txtBanco);
        cardCheque.add(new JLabel("Emisión (AAAA-MM-DD):"));
        txtFechaEmisionCheque = new JTextField(8);
        cardCheque.add(txtFechaEmisionCheque);
        cardCheque.add(new JLabel("Vencimiento (AAAA-MM-DD):"));
        txtFechaVencimientoCheque = new JTextField(8);
        cardCheque.add(txtFechaVencimientoCheque);
        cardCheque.add(new JLabel("Firmante:"));
        txtFirmante = new JTextField(8);
        cardCheque.add(txtFirmante);
        panelCardMedio.add(cardCheque, "CHEQUE");

        panelCarga.add(panelCardMedio);

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAgregarMedio = new JButton("Agregar medio de pago");
        btnQuitarMedio = new JButton("Quitar medio seleccionado");
        filaBotones.add(btnAgregarMedio);
        filaBotones.add(btnQuitarMedio);
        panelCarga.add(filaBotones);

        panelMedios.add(panelCarga, BorderLayout.NORTH);

        modeloTablaMedios = new DefaultTableModel(new String[]{"Tipo", "Detalle", "Importe"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaMedios = new JTable(modeloTablaMedios);
        tablaMedios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollMedios = new JScrollPane(tablaMedios);
        scrollMedios.setPreferredSize(new Dimension(1000, 90));
        panelMedios.add(scrollMedios, BorderLayout.CENTER);

        return panelMedios;
    }

    private JPanel inicializarPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        modeloTablaOP = new DefaultTableModel(new String[]{
                "N° OP", "Fecha", "Total Bruto", "Total Retenido", "Total Neto", "Operador"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaOP = new JTable(modeloTablaOP);
        tablaOP.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaOP);
        scroll.setBorder(BorderFactory.createTitledBorder("Órdenes de Pago emitidas al proveedor seleccionado"));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void cambiarCardMedio() {
        switch (cbTipoMedio.getSelectedIndex()) {
            case 1: cardLayoutMedio.show(panelCardMedio, "TRANSFERENCIA"); break;
            case 2:
            case 3: cardLayoutMedio.show(panelCardMedio, "CHEQUE"); break;
            default: cardLayoutMedio.show(panelCardMedio, "EFECTIVO"); break;
        }
    }

    // --- Métodos públicos que usa el controller ---
    public void cargarComboProveedores(List<ProveedorDTO> proveedores) {
        proveedoresCombo = proveedores;
        cbProveedor.removeAllItems();
        for (ProveedorDTO p : proveedores) {
            cbProveedor.addItem(p.getCuit() + " - " + p.getRazonSocial());
        }
    }

    public String getCuitProveedorSeleccionado() {
        int idx = cbProveedor.getSelectedIndex();
        return (idx != -1) ? proveedoresCombo.get(idx).getCuit() : null;
    }

    public void cargarDocumentosPendientes(List<DocumentoComercialDTO> docs) {
        modeloTabla.setRowCount(0);
        for (DocumentoComercialDTO dto : docs) {
            modeloTabla.addRow(new Object[]{
                    Boolean.FALSE, dto.getNumeroDocumento(), dto.getTipoDocumento(), dto.getFechaEmision(),
                    String.format("$%.2f", dto.getImporteTotal()),
                    String.format("$%.2f", dto.getSaldoPendiente()), ""
            });
        }
    }

    public int getCantidadFilasDocumentos() { return modeloTabla.getRowCount(); }

    public boolean isDocumentoIncluidoEnFila(int fila) {
        return Boolean.TRUE.equals(modeloTabla.getValueAt(fila, COL_INCLUIR));
    }

    public String getNumeroDocumentoEnFila(int fila) {
        return modeloTabla.getValueAt(fila, COL_NUMERO).toString();
    }

    public String getMontoAplicarEnFila(int fila) {
        Object valor = modeloTabla.getValueAt(fila, COL_MONTO_APLICAR);
        return (valor != null) ? valor.toString().trim() : "";
    }

    public void setMontoAplicarEnFila(int fila, double monto) {
        modeloTabla.setValueAt(String.valueOf(monto), fila, COL_MONTO_APLICAR);
    }

    public void actualizarTablaRetenciones(List<RetencionDTO> retenciones) {
        modeloTablaRetenciones.setRowCount(0);
        for (RetencionDTO dto : retenciones) {
            modeloTablaRetenciones.addRow(new Object[]{
                    dto.getTipoImpuesto(), String.format("$%.2f", dto.getBaseImponible()),
                    dto.getPorcentaje() + "%", String.format("$%.2f", dto.getMontoRetenido()),
                    dto.getConExclusion()
            });
        }
    }

    public String getTipoMedioSeleccionado() {
        switch (cbTipoMedio.getSelectedIndex()) {
            case 1: return "TRANSFERENCIA_BANCARIA";
            case 2: return "CHEQUE_PROPIO";
            case 3: return "CHEQUE_DE_TERCEROS";
            default: return "EFECTIVO";
        }
    }

    public String getImporteMedio() { return txtImporteMedio.getText().trim(); }
    public String getNroReferencia() { return txtNroReferencia.getText().trim(); }
    public String getCuentaOrigen() { return txtCuentaOrigen.getText().trim(); }
    public String getNroCheque() { return txtNroCheque.getText().trim(); }
    public String getBanco() { return txtBanco.getText().trim(); }
    public String getFechaEmisionCheque() { return txtFechaEmisionCheque.getText().trim(); }
    public String getFechaVencimientoCheque() { return txtFechaVencimientoCheque.getText().trim(); }
    public String getFirmante() { return txtFirmante.getText().trim(); }

    public void actualizarTablaMedios(List<MedioDePagoDTO> medios) {
        modeloTablaMedios.setRowCount(0);
        for (MedioDePagoDTO dto : medios) {
            modeloTablaMedios.addRow(new Object[]{
                    dto.getTipo(), dto.getDetalle(), String.format("$%.2f", dto.getImporte())
            });
        }
    }

    public int getFilaMedioSeleccionada() { return tablaMedios.getSelectedRow(); }

    public void limpiarFormularioMedio() {
        txtImporteMedio.setText("");
        txtNroReferencia.setText("");
        txtCuentaOrigen.setText("");
        txtNroCheque.setText("");
        txtBanco.setText("");
        txtFechaEmisionCheque.setText("");
        txtFechaVencimientoCheque.setText("");
        txtFirmante.setText("");
    }

    public void setTotales(double bruto, double retenido, double neto, double medios) {
        lblTotalBruto.setText(String.format("Bruto: $%.2f", bruto));
        lblTotalRetenido.setText(String.format("Retenido: $%.2f", retenido));
        lblTotalNeto.setText(String.format("NETO A PAGAR: $%.2f", neto));
        lblTotalMedios.setText(String.format("Medios cargados: $%.2f", medios));
    }

    public void actualizarTablaOP(List<OrdenDePagoDTO> ordenes) {
        modeloTablaOP.setRowCount(0);
        for (OrdenDePagoDTO dto : ordenes) {
            modeloTablaOP.addRow(new Object[]{
                    dto.getNumeroOP(), dto.getFechaEmision(),
                    String.format("$%.2f", dto.getTotalBruto()),
                    String.format("$%.2f", dto.getTotalRetenido()),
                    String.format("$%.2f", dto.getTotalNeto()),
                    dto.getOperador()
            });
        }
    }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    // Getters de componentes para que el controller suscriba eventos
    public JComboBox<String> getCbProveedor() { return cbProveedor; }
    public JTable getTablaDocumentos() { return tablaDocumentos; }
    public JButton getBtnAgregarMedio() { return btnAgregarMedio; }
    public JButton getBtnQuitarMedio() { return btnQuitarMedio; }
    public JButton getBtnEmitir() { return btnEmitir; }
}
