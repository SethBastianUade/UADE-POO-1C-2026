package mvc.view;

import mvc.dto.DocumentoComercialDTO;
import mvc.dto.ProveedorDTO;
import mvc.dto.TotalDiarioDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDocumentosGUI extends JInternalFrame {
    private JTextField txtDesde, txtHasta;
    private JComboBox<String> cbProveedor;
    private JButton btnBuscar;
    private JLabel lblResumen;

    private JTable tablaDocumentos, tablaTotalesDia;
    private DefaultTableModel modeloTabla;
    private DefaultTableModel modeloTablaTotales;

    private List<ProveedorDTO> proveedoresCombo = new ArrayList<>();

    public ConsultaDocumentosGUI() {
        super("Consulta: Trazabilidad Documental", true, true, true, true);
        setSize(900, 560);
        setLayout(new BorderLayout());

        inicializarFiltros();
        inicializarTablas();

        new mvc.controller.ConsultaDocumentosController(this);
    }

    private void inicializarFiltros() {
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros (fechas vacías = sin límite)"));

        panelFiltros.add(new JLabel("Desde (AAAA-MM-DD):"));
        txtDesde = new JTextField(8);
        panelFiltros.add(txtDesde);

        panelFiltros.add(new JLabel("Hasta (AAAA-MM-DD):"));
        txtHasta = new JTextField(8);
        panelFiltros.add(txtHasta);

        panelFiltros.add(new JLabel("Proveedor:"));
        cbProveedor = new JComboBox<>();
        cbProveedor.setPreferredSize(new Dimension(250, 25));
        panelFiltros.add(cbProveedor);

        btnBuscar = new JButton("Buscar");
        panelFiltros.add(btnBuscar);

        add(panelFiltros, BorderLayout.NORTH);
    }

    private void inicializarTablas() {
        modeloTabla = new DefaultTableModel(new String[]{"Número", "Tipo", "Proveedor", "Fecha", "Importe", "Registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaDocumentos = new JTable(modeloTabla);
        tablaDocumentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollDetalle = new JScrollPane(tablaDocumentos);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Detalle de documentos recibidos"));

        modeloTablaTotales = new DefaultTableModel(new String[]{"Fecha", "Cantidad", "Importe total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaTotalesDia = new JTable(modeloTablaTotales);
        JScrollPane scrollTotales = new JScrollPane(tablaTotalesDia);
        scrollTotales.setBorder(BorderFactory.createTitledBorder("Totales por día"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollDetalle, scrollTotales);
        split.setResizeWeight(0.65);
        add(split, BorderLayout.CENTER);

        lblResumen = new JLabel("Documentos: 0 — Importe total: $0.00");
        JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelResumen.add(lblResumen);
        add(panelResumen, BorderLayout.SOUTH);
    }

    // --- Métodos públicos que usa el controller ---
    public void cargarComboProveedores(List<ProveedorDTO> proveedores) {
        proveedoresCombo = proveedores;
        cbProveedor.removeAllItems();
        cbProveedor.addItem("Todos");
        for (ProveedorDTO p : proveedores) {
            cbProveedor.addItem(p.getCuit() + " - " + p.getRazonSocial());
        }
    }

    // null = sin filtro de proveedor
    public String getCuitProveedorFiltro() {
        int idx = cbProveedor.getSelectedIndex();
        return (idx > 0) ? proveedoresCombo.get(idx - 1).getCuit() : null;
    }

    public String getDesde() { return txtDesde.getText().trim(); }
    public String getHasta() { return txtHasta.getText().trim(); }

    public void actualizarTabla(List<DocumentoComercialDTO> lista) {
        modeloTabla.setRowCount(0);
        for (DocumentoComercialDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                    dto.getNumeroDocumento(), dto.getTipoDocumento(), dto.getRazonSocialProveedor(),
                    dto.getFechaEmision(), String.format("$%.2f", dto.getImporteTotal()), dto.getEstadoRegistro()
            });
        }
    }

    public void actualizarTablaTotalesDia(List<TotalDiarioDTO> lista) {
        modeloTablaTotales.setRowCount(0);
        for (TotalDiarioDTO dto : lista) {
            modeloTablaTotales.addRow(new Object[]{
                    dto.getFecha(), dto.getCantidad(), String.format("$%.2f", dto.getImporteTotal())
            });
        }
    }

    public void setResumen(String resumen) { lblResumen.setText(resumen); }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    public JButton getBtnBuscar() { return btnBuscar; }
}
