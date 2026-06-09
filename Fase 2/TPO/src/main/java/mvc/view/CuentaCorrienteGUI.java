package mvc.view;

import mvc.dto.DocumentoComercialDTO;
import mvc.dto.MovimientoCuentaCorrienteDTO;
import mvc.dto.ProveedorDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CuentaCorrienteGUI extends JInternalFrame {
    private JComboBox<String> cbProveedor;
    private JLabel lblDeudaActual;

    private JTable tablaMovimientos;
    private DefaultTableModel modeloTabla;

    private JTextField txtDiasAntiguedad;
    private JButton btnFiltrarPendientes;
    private JTable tablaPendientes;
    private DefaultTableModel modeloTablaPendientes;

    private List<ProveedorDTO> proveedoresCombo = new ArrayList<>();

    public CuentaCorrienteGUI() {
        super("Consulta: Cuenta Corriente de Proveedor", true, true, true, true);
        setSize(950, 600);
        setLayout(new BorderLayout());

        inicializarCabecera();
        inicializarPestanias();

        new mvc.controller.CuentaCorrienteController(this);
    }

    private void inicializarCabecera() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Proveedor"));
        panel.add(new JLabel("Proveedor:"));
        cbProveedor = new JComboBox<>();
        cbProveedor.setPreferredSize(new Dimension(300, 25));
        panel.add(cbProveedor);
        lblDeudaActual = new JLabel("Deuda actual: $0.00");
        lblDeudaActual.setFont(lblDeudaActual.getFont().deriveFont(Font.BOLD));
        panel.add(Box.createHorizontalStrut(30));
        panel.add(lblDeudaActual);
        add(panel, BorderLayout.NORTH);
    }

    private void inicializarPestanias() {
        // Pestaña 1: cuenta corriente cronológica
        modeloTabla = new DefaultTableModel(new String[]{"Fecha", "Tipo", "N° Doc", "Debe", "Haber", "Saldo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaMovimientos = new JTable(modeloTabla);
        tablaMovimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollMovimientos = new JScrollPane(tablaMovimientos);

        // Pestaña 2: documentos pendientes filtrables por antigüedad
        JPanel panelPendientes = new JPanel(new BorderLayout());
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Antigüedad mínima (días):"));
        txtDiasAntiguedad = new JTextField("0", 5);
        panelFiltro.add(txtDiasAntiguedad);
        btnFiltrarPendientes = new JButton("Filtrar");
        panelFiltro.add(btnFiltrarPendientes);
        panelPendientes.add(panelFiltro, BorderLayout.NORTH);

        modeloTablaPendientes = new DefaultTableModel(new String[]{"Número", "Tipo", "Fecha", "Importe", "Saldo", "Cancelación"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPendientes = new JTable(modeloTablaPendientes);
        panelPendientes.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Cuenta corriente", scrollMovimientos);
        pestanias.addTab("Documentos pendientes de pago", panelPendientes);
        add(pestanias, BorderLayout.CENTER);
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

    public String getDiasAntiguedad() { return txtDiasAntiguedad.getText().trim(); }

    public void actualizarTablaMovimientos(List<MovimientoCuentaCorrienteDTO> lista) {
        modeloTabla.setRowCount(0);
        for (MovimientoCuentaCorrienteDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                    dto.getFecha(), dto.getTipo(), dto.getNumeroDocumento(),
                    dto.getDebe() != 0 ? String.format("$%.2f", dto.getDebe()) : "",
                    dto.getHaber() != 0 ? String.format("$%.2f", dto.getHaber()) : "",
                    String.format("$%.2f", dto.getSaldo())
            });
        }
    }

    public void actualizarTablaPendientes(List<DocumentoComercialDTO> lista) {
        modeloTablaPendientes.setRowCount(0);
        for (DocumentoComercialDTO dto : lista) {
            modeloTablaPendientes.addRow(new Object[]{
                    dto.getNumeroDocumento(), dto.getTipoDocumento(), dto.getFechaEmision(),
                    String.format("$%.2f", dto.getImporteTotal()),
                    String.format("$%.2f", dto.getSaldoPendiente()), dto.getEstadoCancelacion()
            });
        }
    }

    public void setDeudaActual(double deuda) {
        lblDeudaActual.setText(String.format("Deuda actual: $%.2f", deuda));
    }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    public JComboBox<String> getCbProveedor() { return cbProveedor; }
    public JButton getBtnFiltrarPendientes() { return btnFiltrarPendientes; }
}
