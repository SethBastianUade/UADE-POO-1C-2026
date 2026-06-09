package mvc.view;

import mvc.dto.OrdenDeCompraDTO;
import mvc.dto.OrdenDePagoDTO;
import mvc.dto.ProveedorDTO;
import mvc.dto.RubroDTO;
import mvc.enums.EstadoOrdenCompra;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeguimientoComprasGUI extends JInternalFrame {
    // Pestaña OC
    private JComboBox<String> cbEstadoOC, cbRubro, cbProveedorOC;
    private JButton btnBuscarOC;
    private JTable tablaOC;
    private DefaultTableModel modeloTabla;

    // Pestaña Pagos
    private JTextField txtDesde, txtHasta;
    private JComboBox<String> cbMedioPago, cbProveedorOP;
    private JButton btnBuscarPagos;
    private JTable tablaPagos;
    private DefaultTableModel modeloTablaPagos;

    private List<ProveedorDTO> proveedoresCombo = new ArrayList<>();
    private List<RubroDTO> rubrosCombo = new ArrayList<>();

    public SeguimientoComprasGUI() {
        super("Consulta: Seguimiento de Compras y Pagos", true, true, true, true);
        setSize(950, 540);
        setLayout(new BorderLayout());

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Órdenes de Compra", inicializarPanelOC());
        pestanias.addTab("Pagos realizados", inicializarPanelPagos());
        add(pestanias, BorderLayout.CENTER);

        new mvc.controller.SeguimientoComprasController(this);
    }

    private JPanel inicializarPanelOC() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        filtros.add(new JLabel("Estado:"));
        cbEstadoOC = new JComboBox<>();
        cbEstadoOC.addItem("Todos");
        for (EstadoOrdenCompra estado : EstadoOrdenCompra.values()) {
            cbEstadoOC.addItem(estado.toString());
        }
        filtros.add(cbEstadoOC);
        filtros.add(new JLabel("Rubro:"));
        cbRubro = new JComboBox<>();
        filtros.add(cbRubro);
        filtros.add(new JLabel("Proveedor:"));
        cbProveedorOC = new JComboBox<>();
        cbProveedorOC.setPreferredSize(new Dimension(220, 25));
        filtros.add(cbProveedorOC);
        btnBuscarOC = new JButton("Buscar");
        filtros.add(btnBuscarOC);
        panel.add(filtros, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"N° OC", "Proveedor", "Fecha", "Total", "Estado", "Operador"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaOC = new JTable(modeloTabla);
        panel.add(new JScrollPane(tablaOC), BorderLayout.CENTER);
        return panel;
    }

    private JPanel inicializarPanelPagos() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros (fechas vacías = sin límite)"));
        filtros.add(new JLabel("Desde (AAAA-MM-DD):"));
        txtDesde = new JTextField(8);
        filtros.add(txtDesde);
        filtros.add(new JLabel("Hasta (AAAA-MM-DD):"));
        txtHasta = new JTextField(8);
        filtros.add(txtHasta);
        filtros.add(new JLabel("Medio de pago:"));
        cbMedioPago = new JComboBox<>(new String[]{"Todos", "Efectivo", "Transferencia Bancaria", "Cheque Propio", "Cheque de Terceros"});
        filtros.add(cbMedioPago);
        filtros.add(new JLabel("Proveedor:"));
        cbProveedorOP = new JComboBox<>();
        cbProveedorOP.setPreferredSize(new Dimension(220, 25));
        filtros.add(cbProveedorOP);
        btnBuscarPagos = new JButton("Buscar");
        filtros.add(btnBuscarPagos);
        panel.add(filtros, BorderLayout.NORTH);

        modeloTablaPagos = new DefaultTableModel(new String[]{"N° OP", "Proveedor", "Fecha", "Bruto", "Retenido", "Neto", "Operador"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPagos = new JTable(modeloTablaPagos);
        panel.add(new JScrollPane(tablaPagos), BorderLayout.CENTER);
        return panel;
    }

    // --- Métodos públicos que usa el controller ---
    public void cargarComboProveedores(List<ProveedorDTO> proveedores) {
        proveedoresCombo = proveedores;
        cbProveedorOC.removeAllItems();
        cbProveedorOP.removeAllItems();
        cbProveedorOC.addItem("Todos");
        cbProveedorOP.addItem("Todos");
        for (ProveedorDTO p : proveedores) {
            cbProveedorOC.addItem(p.getCuit() + " - " + p.getRazonSocial());
            cbProveedorOP.addItem(p.getCuit() + " - " + p.getRazonSocial());
        }
    }

    public void cargarComboRubros(List<RubroDTO> rubros) {
        rubrosCombo = rubros;
        cbRubro.removeAllItems();
        cbRubro.addItem("Todos");
        for (RubroDTO r : rubros) {
            cbRubro.addItem(r.getCodigo() + " - " + r.getDescripcion());
        }
    }

    // null = sin filtro
    public String getEstadoOCFiltro() {
        int idx = cbEstadoOC.getSelectedIndex();
        return (idx > 0) ? cbEstadoOC.getSelectedItem().toString() : null;
    }

    public String getCodigoRubroFiltro() {
        int idx = cbRubro.getSelectedIndex();
        return (idx > 0) ? rubrosCombo.get(idx - 1).getCodigo() : null;
    }

    public String getCuitProveedorOCFiltro() {
        int idx = cbProveedorOC.getSelectedIndex();
        return (idx > 0) ? proveedoresCombo.get(idx - 1).getCuit() : null;
    }

    public String getCuitProveedorOPFiltro() {
        int idx = cbProveedorOP.getSelectedIndex();
        return (idx > 0) ? proveedoresCombo.get(idx - 1).getCuit() : null;
    }

    public String getTipoMedioFiltro() {
        switch (cbMedioPago.getSelectedIndex()) {
            case 1: return "EFECTIVO";
            case 2: return "TRANSFERENCIA_BANCARIA";
            case 3: return "CHEQUE_PROPIO";
            case 4: return "CHEQUE_DE_TERCEROS";
            default: return null; // Todos
        }
    }

    public String getDesde() { return txtDesde.getText().trim(); }
    public String getHasta() { return txtHasta.getText().trim(); }

    public void actualizarTablaOC(List<OrdenDeCompraDTO> lista) {
        modeloTabla.setRowCount(0);
        for (OrdenDeCompraDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                    dto.getNumeroOC(), dto.getRazonSocialProveedor(), dto.getFechaEmision(),
                    String.format("$%.2f", dto.getTotalBruto()), dto.getEstado(), dto.getOperador()
            });
        }
    }

    public void actualizarTablaPagos(List<OrdenDePagoDTO> lista) {
        modeloTablaPagos.setRowCount(0);
        for (OrdenDePagoDTO dto : lista) {
            modeloTablaPagos.addRow(new Object[]{
                    dto.getNumeroOP(), dto.getRazonSocialProveedor(), dto.getFechaEmision(),
                    String.format("$%.2f", dto.getTotalBruto()), String.format("$%.2f", dto.getTotalRetenido()),
                    String.format("$%.2f", dto.getTotalNeto()), dto.getOperador()
            });
        }
    }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    public JButton getBtnBuscarOC() { return btnBuscarOC; }
    public JButton getBtnBuscarPagos() { return btnBuscarPagos; }
}
